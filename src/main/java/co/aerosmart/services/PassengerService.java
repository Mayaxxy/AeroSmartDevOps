package co.aerosmart.services;

import co.aerosmart.dto.AuthResponse;
import co.aerosmart.dto.LoginRequest;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.dto.RegisterRequest;
import co.aerosmart.mappers.PassengerMapper;
import co.aerosmart.model.Passenger;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Servicio para gestión de pasajeros y autenticación.
 * Implementa UserDetailsService para integración con Spring Security.
 */
@Service
@RequiredArgsConstructor
public class PassengerService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PassengerMapper passengerMapper;

    /**
     * Registra un nuevo pasajero en el sistema.
     * Valida que no exista documento o email duplicado.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que no exista el documento
        if (passengerRepository.existsByDocumentId(request.getDocumentId())) {
            throw new IllegalArgumentException("Ya existe un pasajero con ese documento");
        }

        // Validar que no exista el email
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un pasajero con ese email");
        }

        // Crear nuevo pasajero
        Passenger passenger = new Passenger();
        passenger.setDocumentId(request.getDocumentId());
        passenger.setFirstName(request.getFirstName());
        passenger.setLastName(request.getLastName());
        passenger.setEmail(request.getEmail());
        passenger.setPhone(request.getPhone());
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));

        passenger = passengerRepository.save(passenger);

        // Generar token JWT
        String token = jwtUtil.generateToken(passenger.getEmail());

        return new AuthResponse(token, passengerMapper.toDTO(passenger));
    }

    /**
     * Autentica un pasajero y genera token JWT.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Buscar pasajero
        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado"));

        // Generar token JWT
        String token = jwtUtil.generateToken(passenger.getEmail());

        return new AuthResponse(token, passengerMapper.toDTO(passenger));
    }

    /**
     * Busca un pasajero por email.
     */
    @Transactional(readOnly = true)
    public Passenger findByEmail(String email) {
        return passengerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado"));
    }

    /**
     * Busca un pasajero por ID.
     */
    @Transactional(readOnly = true)
    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pasajero no encontrado"));
    }

    /**
     * Obtiene el perfil de un pasajero.
     */
    @Transactional(readOnly = true)
    public PassengerDTO getProfile(String email) {
        Passenger passenger = findByEmail(email);
        return passengerMapper.toDTO(passenger);
    }

    /**
     * Implementación de UserDetailsService para Spring Security.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado: " + email));

        return new User(passenger.getEmail(), passenger.getPassword(), new ArrayList<>());
    }
}
