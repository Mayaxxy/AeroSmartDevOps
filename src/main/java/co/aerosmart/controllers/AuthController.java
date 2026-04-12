package co.aerosmart.controllers;

import co.aerosmart.dto.AuthResponse;
import co.aerosmart.dto.LoginRequest;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.dto.RegisterRequest;
import co.aerosmart.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y registro de pasajeros.
 * Endpoints públicos para login y registro.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final PassengerService passengerService;

    /**
     * Registra un nuevo pasajero.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = passengerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Autentica un pasajero y retorna JWT token.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = passengerService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el perfil del pasajero autenticado.
     * GET /api/auth/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<PassengerDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        PassengerDTO profile = passengerService.getProfile(email);
        return ResponseEntity.ok(profile);
    }
}
