package co.aerosmart.controllers;

import co.aerosmart.dto.BoardingPassDTO;
import co.aerosmart.services.BoardingPassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de pases de abordaje.
 * Requiere autenticación.
 */
@RestController
@RequestMapping("/api/boarding-pass")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoardingPassController {

    private final BoardingPassService boardingPassService;

    /**
     * Obtiene el pase de abordaje de un pasajero.
     * GET /api/boarding-pass/{checkInId}
     * El QR se regenera automáticamente si es necesario (cada 60 seg).
     */
    @GetMapping("/{checkInId}")
    public ResponseEntity<BoardingPassDTO> getBoardingPass(
            @PathVariable Long checkInId,
            Authentication authentication) {
        String passengerEmail = authentication.getName();
        BoardingPassDTO boardingPass = boardingPassService.getBoardingPass(checkInId, passengerEmail);
        return ResponseEntity.ok(boardingPass);
    }

    /**
     * Valida y usa un pase de abordaje al escanear el QR.
     * POST /api/boarding-pass/validate
     * Para uso del personal en la puerta de embarque.
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateBoardingPass(
            @RequestParam String boardingToken,
            @RequestParam String flightCode) {
        boardingPassService.validateAndUseBoardingPass(boardingToken, flightCode);
        return ResponseEntity.ok("Pase de abordaje válido. Acceso permitido.");
    }
}
