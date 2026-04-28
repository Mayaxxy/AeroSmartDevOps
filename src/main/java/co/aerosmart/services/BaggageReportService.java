package co.aerosmart.services;

import co.aerosmart.dto.BaggageReportDTO;
import co.aerosmart.dto.BaggageReportRequest;
import co.aerosmart.mappers.BaggageReportMapper;
import co.aerosmart.model.BaggageReport;
import co.aerosmart.model.BaggageReportStatus;
import co.aerosmart.model.Flight;
import co.aerosmart.model.Passenger;
import co.aerosmart.repository.BaggageReportRepository;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de reportes de equipaje.
 * Un pasajero solo puede reportar inconvenientes si tiene un vuelo registrado.
 * El estado sigue el flujo: PENDING → IN_PROGRESS → RESOLVED.
 * Hay un límite de reportes activos simultáneos por pasajero.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BaggageReportService {

    private final BaggageReportRepository baggageReportRepository;
    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final PassengerService passengerService;
    private final FlightService flightService;
    private final BaggageReportMapper baggageReportMapper;

    private static final int MAX_ACTIVE_REPORTS = 3;

    /**
     * Crea un reporte de equipaje.
     * Valida que:
     * - El pasajero tenga una reserva válida (no cancelada) en el vuelo
     * - No exceda el límite de 3 reportes activos
     * 
     * Si el usuario autenticado es RECEPCIONISTA, registra su ID para auditoría.
     */
    @Transactional
    public BaggageReportDTO createReport(BaggageReportRequest request, String passengerEmail) {
        Passenger passenger = passengerService.findByEmail(passengerEmail);
        Flight flight = flightService.findById(request.getFlightId());

        // Validar que el pasajero tenga una reserva válida (no cancelada) en el vuelo
        boolean hasValidReservation = reservationRepository.findByPassengerId(passenger.getId())
            .stream()
            .anyMatch(r -> r.getFlight().getId().equals(flight.getId()) 
                && r.getStatus() != co.aerosmart.model.ReservationStatus.CANCELLED);

        if (!hasValidReservation) {
            throw new IllegalArgumentException("No tienes una reserva válida en este vuelo");
        }

        // Validar límite de reportes activos (PENDING + IN_PROGRESS)
        long activeReportsCount = baggageReportRepository.countActiveReportsByPassengerId(passenger.getId());
        if (activeReportsCount >= MAX_ACTIVE_REPORTS) {
            throw new IllegalStateException(
                "Has alcanzado el límite de " + MAX_ACTIVE_REPORTS + " reportes activos simultáneos"
            );
        }

        // Crear reporte
        BaggageReport report = new BaggageReport();
        report.setPassenger(passenger);
        report.setFlight(flight);
        report.setDescription(request.getDescription());
        report.setStatus(BaggageReportStatus.PENDING);
        
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
                report.setReceptionistId(receptionist.getId());
                log.info("Reporte de equipaje creado por recepcionista ID: {}", receptionist.getId());
            }
        }
        
        report = baggageReportRepository.save(report);

        log.info("Reporte de equipaje creado por pasajero {} para vuelo {}", 
            passenger.getEmail(), 
            flight.getFlightCode());

        return baggageReportMapper.toDTO(report);
    }

    /**
     * Obtiene todos los reportes de un pasajero.
     */
    @Transactional(readOnly = true)
    public List<BaggageReportDTO> getPassengerReports(String passengerEmail) {
        Passenger passenger = passengerService.findByEmail(passengerEmail);
        
        return baggageReportRepository.findByPassengerId(passenger.getId())
            .stream()
            .map(baggageReportMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un reporte por ID.
     */
    @Transactional(readOnly = true)
    public BaggageReportDTO getReportById(Long reportId, String passengerEmail) {
        BaggageReport report = baggageReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        // Validar que el reporte pertenezca al pasajero
        if (!report.getPassenger().getEmail().equals(passengerEmail)) {
            throw new IllegalArgumentException("Este reporte no pertenece al pasajero autenticado");
        }

        return baggageReportMapper.toDTO(report);
    }

    /**
     * Cambia el estado de un reporte siguiendo el flujo permitido.
     * Solo personal autorizado puede cambiar estados (implementar autorización).
     */
    @Transactional
    public BaggageReportDTO updateReportStatus(Long reportId, BaggageReportStatus newStatus) {
        BaggageReport report = baggageReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        // Cambiar estado (valida transiciones permitidas)
        report.changeStatus(newStatus);
        report = baggageReportRepository.save(report);

        log.info("Reporte {} cambió a estado {}", reportId, newStatus);

        return baggageReportMapper.toDTO(report);
    }

    /**
     * Obtiene todos los reportes por estado.
     * Para uso de personal del aeropuerto.
     */
    @Transactional(readOnly = true)
    public List<BaggageReportDTO> getReportsByStatus(BaggageReportStatus status) {
        return baggageReportRepository.findByStatus(status)
            .stream()
            .map(baggageReportMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los reportes del sistema.
     * Para uso de recepcionistas.
     */
    @Transactional(readOnly = true)
    public List<BaggageReportDTO> getAllReports() {
        return baggageReportRepository.findAll()
            .stream()
            .map(baggageReportMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un reporte usando String.
     * Valida el flujo de estados y convierte el String a enum.
     */
    @Transactional
    public BaggageReportDTO updateReportStatus(Long reportId, String statusStr) {
        BaggageReportStatus newStatus;
        try {
            newStatus = BaggageReportStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + statusStr);
        }
        return updateReportStatus(reportId, newStatus);
    }
}
