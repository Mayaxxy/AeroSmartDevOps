package co.aerosmart.services;

import co.aerosmart.dto.PassengerSearchResultDTO;
import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.mappers.PassengerMapper;
import co.aerosmart.mappers.ReservationMapper;
import co.aerosmart.model.Passenger;
import co.aerosmart.model.Reservation;
import co.aerosmart.model.Role;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para operaciones de recepcionistas.
 * Permite búsqueda de pasajeros con información completa de reservas y vuelos.
 */
@Service
@RequiredArgsConstructor
public class ReceptionistService {

    private final PassengerRepository passengerRepository;
    private final ReservationRepository reservationRepository;
    private final PassengerMapper passengerMapper;
    private final ReservationMapper reservationMapper;
    private final PassengerService passengerService;

    /**
     * Busca un pasajero por documentId o passengerId.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta búsqueda.
     * 
     * @param documentId Documento de identidad del pasajero (opcional)
     * @param passengerId ID del pasajero (opcional)
     * @param authentication Información de autenticación del usuario
     * @return PassengerSearchResultDTO con información del pasajero, reservas y vuelos
     * @throws AccessDeniedException si el usuario no tiene rol RECEPCIONISTA
     * @throws IllegalArgumentException si no se proporciona documentId ni passengerId
     * @throws IllegalArgumentException si no se encuentra el pasajero
     */
    @Transactional(readOnly = true)
    public PassengerSearchResultDTO searchPassenger(
            String documentId, 
            Long passengerId, 
            Authentication authentication) {
        
        // Validar que el usuario tenga rol RECEPCIONISTA
        validateReceptionistRole(authentication);
        
        // Validar que se proporcione al menos un parámetro de búsqueda
        if (documentId == null && passengerId == null) {
            throw new IllegalArgumentException("Debe proporcionar documentId o passengerId para la búsqueda");
        }
        
        // Buscar pasajero
        Passenger passenger;
        if (passengerId != null) {
            passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Pasajero no encontrado con ID: " + passengerId));
        } else {
            passenger = passengerRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Pasajero no encontrado con documento: " + documentId));
        }
        
        // Obtener reservas del pasajero
        List<Reservation> reservations = reservationRepository.findByPassengerId(passenger.getId());
        
        // Convertir a DTOs
        List<ReservationDTO> reservationDTOs = reservations.stream()
            .map(reservationMapper::toDTO)
            .collect(Collectors.toList());
        
        // Construir resultado
        PassengerSearchResultDTO result = new PassengerSearchResultDTO();
        result.setPassenger(passengerMapper.toDTO(passenger));
        result.setReservations(reservationDTOs);
        
        return result;
    }

    /**
     * Valida que el usuario autenticado tenga rol RECEPCIONISTA.
     * 
     * @param authentication Información de autenticación del usuario
     * @throws AccessDeniedException si el usuario no tiene rol RECEPCIONISTA
     */
    private void validateReceptionistRole(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        
        Passenger user = passengerService.findByEmail(authentication.getName());
        
        if (user.getRole() != Role.RECEPCIONISTA) {
            throw new AccessDeniedException("Solo usuarios con rol RECEPCIONISTA pueden realizar búsquedas de pasajeros");
        }
    }
}
