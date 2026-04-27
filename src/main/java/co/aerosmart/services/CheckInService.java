package co.aerosmart.services;

import co.aerosmart.dto.BoardingPassDTO;
import co.aerosmart.dto.CheckInRequest;
import co.aerosmart.model.*;
import co.aerosmart.repository.CheckInRepository;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de check-in.
 * El check-in es obligatorio para generar el QR de abordaje.
 * Un pasajero no puede tener dos check-in activos para el mismo vuelo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final BoardingPassService boardingPassService;

    /**
     * Realiza el check-in de un pasajero.
     * Valida que:
     * - La reserva exista y esté activa
     * - El vuelo no esté cancelado
     * - No exista un check-in activo previo
     * - Esté dentro de la ventana de check-in (24-48h antes)
     * 
     * Si el usuario autenticado es RECEPCIONISTA, registra su ID para auditoría.
     */
    @Transactional
    public BoardingPassDTO performCheckIn(CheckInRequest request, String passengerEmail) {
        // Buscar reserva
        Reservation reservation = reservationRepository.findByReservationCode(request.getReservationCode())
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Validar que la reserva pertenezca al pasajero
        if (!reservation.getPassenger().getEmail().equals(passengerEmail)) {
            throw new IllegalArgumentException("Esta reserva no pertenece al pasajero autenticado");
        }

        // Validar que la reserva esté activa
        if (!reservation.allowsFlightAccess()) {
            throw new IllegalStateException("La reserva está cancelada");
        }

        Flight flight = reservation.getFlight();

        // Validar que el vuelo permita check-in
        if (!flight.allowsCheckIn()) {
            throw new IllegalStateException("El vuelo está cancelado, no se permite check-in");
        }

        // Validar que esté dentro de la ventana de check-in (24h-2h antes del vuelo)
        if (!reservation.isCheckInWindowOpen()) {
            throw new IllegalStateException("El check-in solo está disponible entre 48 y 2 horas antes del vuelo");
        }

        // Validar que no exista un check-in activo previo para el mismo vuelo
        boolean hasActiveCheckIn = checkInRepository.existsActiveCheckInForPassengerAndFlight(
            reservation.getPassenger().getId(),
            flight.getId()
        );

        if (hasActiveCheckIn) {
            throw new IllegalStateException("Ya existe un check-in activo para este vuelo");
        }

        // Crear check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        checkIn.setStatus(CheckInStatus.ACTIVE);
        
        // Extraer receptionistId si el usuario autenticado es RECEPCIONISTA
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isReceptionist = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_RECEPCIONISTA"));
            
            if (isReceptionist) {
                String receptionistEmail = authentication.getName();
                Passenger receptionist = passengerRepository.findByEmail(receptionistEmail)
                    .orElseThrow(() -> new IllegalStateException("Recepcionista no encontrado"));
                checkIn.setReceptionistId(receptionist.getId());
                log.info("Check-in realizado por recepcionista ID: {}", receptionist.getId());
            }
        }
        
        checkIn = checkInRepository.save(checkIn);

        log.info("Check-in realizado para reserva {} en vuelo {}", 
            reservation.getReservationCode(), 
            flight.getFlightCode());

        // Generar pase de abordaje con QR
        return boardingPassService.generateBoardingPass(checkIn);
    }

    /**
     * Cancela un check-in si aún es modificable.
     */
    @Transactional
    public void cancelCheckIn(Long checkInId) {
        CheckIn checkIn = checkInRepository.findById(checkInId)
            .orElseThrow(() -> new IllegalArgumentException("Check-in no encontrado"));

        if (!checkIn.canBeModified()) {
            throw new IllegalStateException("El check-in no puede modificarse después del cierre de abordaje");
        }

        checkIn.setStatus(CheckInStatus.CANCELLED);
        checkInRepository.save(checkIn);

        log.info("Check-in {} cancelado", checkInId);
    }

    /**
     * Busca un check-in por ID de reserva.
     */
    @Transactional(readOnly = true)
    public CheckIn findByReservationId(Long reservationId) {
        return checkInRepository.findByReservationId(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Check-in no encontrado para esta reserva"));
    }
}
