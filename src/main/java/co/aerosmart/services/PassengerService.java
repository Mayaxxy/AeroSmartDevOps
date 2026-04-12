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
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PassengerService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PassengerMapper passengerMapper;
    // ApplicationContext no causa ciclo — se resuelve antes que los beans de seguridad
    private final ApplicationContext applicationContext;

    // Obtener AuthenticationManager de forma lazy para romper el ciclo circular
    private AuthenticationManager getAuthenticationManager() {
        return applicationContext.getBean(AuthenticationManager.class);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        int age = Period.between(request.getBirthDate(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("Debes tener al menos 18 años para registrarte");
        }

        validateDocumentFormat(request.getDocumentType(), request.getDocumentId());

        if (!request.isAcceptedDataPolicy()) {
            throw new IllegalArgumentException("Debes aceptar la política de tratamiento de datos");
        }

        if (passengerRepository.existsByDocumentId(request.getDocumentId())) {
            throw new IllegalArgumentException("Ya existe un pasajero con ese documento");
        }
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un pasajero con ese email");
        }

        Passenger passenger = new Passenger();
        passenger.setDocumentType(request.getDocumentType());
        passenger.setDocumentId(request.getDocumentId());
        passenger.setFirstName(request.getFirstName().trim());
        passenger.setMiddleName(request.getMiddleName() != null ? request.getMiddleName().trim() : null);
        passenger.setLastName(request.getLastName().trim());
        passenger.setSecondLastName(request.getSecondLastName() != null ? request.getSecondLastName().trim() : null);
        passenger.setBirthDate(request.getBirthDate());
        passenger.setEmail(request.getEmail().toLowerCase().trim());
        passenger.setPhone(request.getPhone().trim());
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));
        passenger.setAcceptedDataPolicy(true);

        passenger = passengerRepository.save(passenger);
        String token = jwtUtil.generateToken(passenger.getEmail());
        return new AuthResponse(token, passengerMapper.toDTO(passenger));
    }

    private void validateDocumentFormat(String type, String documentId) {
        switch (type) {
            case "CC" -> {
                if (!documentId.matches("^[0-9]{6,10}$"))
                    throw new IllegalArgumentException("La cédula de ciudadanía debe tener entre 6 y 10 dígitos numéricos");
            }
            case "PASSPORT" -> {
                if (!documentId.matches("^[A-Z]{1,2}[0-9]{6,7}$"))
                    throw new IllegalArgumentException("El pasaporte debe tener formato válido (ej: AB123456)");
            }
            case "CE" -> {
                if (!documentId.matches("^[0-9]{4,6}$"))
                    throw new IllegalArgumentException("La cédula de extranjería debe tener entre 4 y 6 dígitos numéricos");
            }
            default -> throw new IllegalArgumentException("Tipo de documento no válido");
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado"));

        String token = jwtUtil.generateToken(passenger.getEmail());
        return new AuthResponse(token, passengerMapper.toDTO(passenger));
    }

    @Transactional(readOnly = true)
    public Passenger findByEmail(String email) {
        return passengerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado"));
    }

    @Transactional(readOnly = true)
    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pasajero no encontrado"));
    }

    @Transactional(readOnly = true)
    public PassengerDTO getProfile(String email) {
        return passengerMapper.toDTO(findByEmail(email));
    }

    @Transactional
    public PassengerDTO updateProfile(String email, co.aerosmart.dto.UpdateProfileRequest request) {
        Passenger passenger = findByEmail(email);

        if (request.getFirstName() != null)     passenger.setFirstName(request.getFirstName().trim());
        if (request.getMiddleName() != null)    passenger.setMiddleName(request.getMiddleName().trim());
        if (request.getLastName() != null)      passenger.setLastName(request.getLastName().trim());
        if (request.getSecondLastName() != null) passenger.setSecondLastName(request.getSecondLastName().trim());
        if (request.getDocumentType() != null)  passenger.setDocumentType(request.getDocumentType());
        if (request.getBirthDate() != null)     passenger.setBirthDate(request.getBirthDate());
        if (request.getPhone() != null)         passenger.setPhone(request.getPhone().trim());

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null ||
                !passwordEncoder.matches(request.getCurrentPassword(), passenger.getPassword())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }
            passenger.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return passengerMapper.toDTO(passengerRepository.save(passenger));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado: " + email));
        return new User(passenger.getEmail(), passenger.getPassword(), new ArrayList<>());
    }
}
