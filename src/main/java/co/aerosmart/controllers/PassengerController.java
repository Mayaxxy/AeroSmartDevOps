package co.aerosmart.controllers;

import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.model.ReservationStatus;
import co.aerosmart.services.PassengerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de pasajeros.
 * Endpoints protegidos que requieren autenticación JWT.
 */
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PassengerController {

    private final PassengerService passengerService;

    /**
     * Obtiene las reservas del pasajero autenticado.
     * GET /api/passengers/my-reservations
     * 
     * @param authentication Usuario autenticado
     * @param status Filtro opcional por estado (ej: ACTIVE)
     * @return Lista de reservas del pasajero
     */
    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationDTO>> getMyReservations(
            Authentication authentication,
            @RequestParam(required = false) ReservationStatus status) {
        String email = authentication.getName();
        List<ReservationDTO> reservations = passengerService.getPassengerReservations(email, status);
        return ResponseEntity.ok(reservations);
    }

    /** Crea una reserva para el pasajero autenticado en un vuelo. POST /api/passengers/reservations */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        ReservationDTO reservation = passengerService.createReservation(email, request.getFlightId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /** Cancela una reserva del pasajero autenticado. DELETE /api/passengers/reservations/{id} */
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        passengerService.cancelReservation(email, id);
        return ResponseEntity.noContent().build();
    }

    @Data
    static class CreateReservationRequest {
        @NotNull(message = "El ID del vuelo es obligatorio")
        private Long flightId;
    }
}
