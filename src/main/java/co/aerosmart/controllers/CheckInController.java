package co.aerosmart.controllers;

import co.aerosmart.dto.BoardingPassDTO;
import co.aerosmart.dto.CheckInRequest;
import co.aerosmart.services.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de check-in.
 * Requiere autenticación.
 */
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * Realiza el check-in de un pasajero.
     * POST /api/checkin
     * Retorna el pase de abordaje con QR.
     */
    @PostMapping
    public ResponseEntity<BoardingPassDTO> performCheckIn(
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication) {
        String passengerEmail = authentication.getName();
        BoardingPassDTO boardingPass = checkInService.performCheckIn(request, passengerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardingPass);
    }

    /**
     * Cancela un check-in.
     * DELETE /api/checkin/{checkInId}
     */
    @DeleteMapping("/{checkInId}")
    public ResponseEntity<Void> cancelCheckIn(@PathVariable Long checkInId) {
        checkInService.cancelCheckIn(checkInId);
        return ResponseEntity.noContent().build();
    }
}
