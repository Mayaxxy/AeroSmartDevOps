package co.aerosmart.controllers;

import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.model.ReservationStatus;
import co.aerosmart.services.PassengerService;
import lombok.RequiredArgsConstructor;
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
}
