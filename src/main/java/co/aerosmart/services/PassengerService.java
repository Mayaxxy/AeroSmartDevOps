package co.aerosmart.services;

import co.aerosmart.dto.AuthResponse;
import co.aerosmart.dto.CreateReceptionistRequest;
import co.aerosmart.dto.LoginRequest;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.dto.RegisterRequest;
import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.dto.UpdateProfileRequest;
import co.aerosmart.mappers.PassengerMapper;
import co.aerosmart.mappers.ReservationMapper;
import co.aerosmart.model.Flight;
import co.aerosmart.model.Passenger;
import co.aerosmart.model.Reservation;
import co.aerosmart.model.ReservationStatus;
import co.aerosmart.model.Role;
import co.aerosmart.repository.FlightRepository;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import co.aerosmart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de pasajeros y autenticación.
 * Implementa UserDetailsService para integración con Spring Security.
 */
@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class PassengerService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PassengerMapper passengerMapper;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final FlightRepository flightRepository;

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
        passenger.setDocumentType(request.getDocumentType());
        passenger.setDocumentId(request.getDocumentId());
        passenger.setFirstName(request.getFirstName());
        passenger.setMiddleName(request.getMiddleName());
        passenger.setLastName(request.getLastName());
        passenger.setSecondLastName(request.getSecondLastName());
        passenger.setBirthDate(request.getBirthDate() != null
                ? request.getBirthDate().atStartOfDay() : null);
        passenger.setEmail(request.getEmail());
        passenger.setPhone(request.getPhone());
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));
        passenger.setAcceptedDataPolicy(request.isAcceptedDataPolicy());
        passenger.setRole(Role.PASSENGER); // Asignar rol por defecto

        passenger = passengerRepository.save(passenger);

        // Generar token JWT con rol
        String token = jwtUtil.generateToken(passenger.getEmail(), passenger.getRole().name());

        return new AuthResponse(token, passengerMapper.toDTO(passenger));
    }

    /**
     * Autentica un pasajero y genera token JWT.
     * Rechaza usuarios con cuenta desactivada.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Buscar pasajero
        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        // Validar contraseña manualmente
        if (!passwordEncoder.matches(request.getPassword(), passenger.getPassword())) {
            throw new UsernameNotFoundException("Credenciales inválidas");
        }

        // Rechazar usuarios desactivados
        if (Boolean.FALSE.equals(passenger.getActive())) {
            throw new IllegalStateException("Cuenta desactivada. Contacte al administrador");
        }

        // Generar token JWT con rol
        String token = jwtUtil.generateToken(passenger.getEmail(), passenger.getRole().name());

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
     * Actualiza el perfil de un pasajero.
     * Permite actualizar: firstName, middleName, lastName, secondLastName, birthDate, phone, documentType.
     * Prohibe modificar: email, documentId.
     * Soporta cambio opcional de contraseña validando currentPassword.
     */
    @Transactional
    public PassengerDTO updateProfile(String email, UpdateProfileRequest request) {
        // Buscar pasajero por email
        Passenger passenger = findByEmail(email);

        // Actualizar campos permitidos (solo si no son null)
        if (request.getFirstName() != null) {
            passenger.setFirstName(request.getFirstName());
        }
        if (request.getMiddleName() != null) {
            passenger.setMiddleName(request.getMiddleName());
        }
        if (request.getLastName() != null) {
            passenger.setLastName(request.getLastName());
        }
        if (request.getSecondLastName() != null) {
            passenger.setSecondLastName(request.getSecondLastName());
        }
        if (request.getDocumentType() != null) {
            passenger.setDocumentType(request.getDocumentType());
        }
        if (request.getBirthDate() != null) {
            // Convertir LocalDate a LocalDateTime (inicio del día)
            passenger.setBirthDate(request.getBirthDate().atStartOfDay());
        }
        if (request.getPhone() != null) {
            passenger.setPhone(request.getPhone());
        }

        // Cambio de contraseña (opcional)
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            // Validar contraseña actual
            if (!passwordEncoder.matches(request.getCurrentPassword(), passenger.getPassword())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }
            // Actualizar contraseña
            passenger.setPassword(passwordEncoder.encode(request.getNewPassword()));
        } else if (request.getCurrentPassword() != null || request.getNewPassword() != null) {
            // Si solo se proporciona uno de los dos campos, lanzar error
            throw new IllegalArgumentException("Para cambiar la contraseña, debe proporcionar tanto la contraseña actual como la nueva");
        }

        // Guardar cambios
        passenger = passengerRepository.save(passenger);

        return passengerMapper.toDTO(passenger);
    }

    /**
     * Crea un nuevo usuario recepcionista.
     * Solo puede ser ejecutado por usuarios con rol ADMIN.
     */
    @Transactional
    public PassengerDTO createReceptionist(CreateReceptionistRequest request) {
        // Validar que no exista el documento
        if (passengerRepository.existsByDocumentId(request.getDocumentId())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese documento");
        }

        // Validar que no exista el email
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        // Crear nuevo recepcionista
        Passenger receptionist = new Passenger();
        receptionist.setDocumentId(request.getDocumentId());
        receptionist.setFirstName(request.getFirstName());
        receptionist.setLastName(request.getLastName());
        receptionist.setEmail(request.getEmail());
        receptionist.setPhone(request.getPhone());
        receptionist.setPassword(passwordEncoder.encode(request.getPassword()));
        receptionist.setRole(Role.RECEPCIONISTA); // Asignar rol RECEPCIONISTA

        receptionist = passengerRepository.save(receptionist);

        return passengerMapper.toDTO(receptionist);
    }

    /**
     * Obtiene las reservas del pasajero autenticado.
     * Permite filtrar por estado (ej: ACTIVE) y ordena por fecha de vuelo descendente.
     * 
     * @param email Email del pasajero autenticado
     * @param status Filtro opcional por estado de reserva
     * @return Lista de reservas ordenadas por fecha de vuelo descendente
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getPassengerReservations(String email, ReservationStatus status) {
        // Buscar pasajero por email
        Passenger passenger = findByEmail(email);
        
        // Obtener reservas según filtro de estado
        List<Reservation> reservations;
        if (status != null) {
            reservations = reservationRepository.findByPassengerIdAndStatus(passenger.getId(), status);
        } else {
            reservations = reservationRepository.findByPassengerId(passenger.getId());
        }
        
        // Ordenar por fecha de vuelo descendente y mapear a DTO
        return reservations.stream()
            .sorted(Comparator.comparing(
                (Reservation r) -> r.getFlight().getDepartureTime(),
                Comparator.reverseOrder()
            ))
            .map(reservationMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Crea una reserva para el pasajero en un vuelo.
     */
    @Transactional
    public ReservationDTO createReservation(String email, Long flightId) {
        Passenger passenger = findByEmail(email);
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));

        if (flight.getStatus() == co.aerosmart.model.FlightStatus.CANCELLED) {
            throw new IllegalArgumentException("No se puede reservar un vuelo cancelado");
        }
        if (flight.getDepartureTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede reservar un vuelo que ya salió");
        }
        if (reservationRepository.existsActiveReservation(passenger.getId(), flightId)) {
            throw new IllegalArgumentException("Ya tienes una reserva activa para este vuelo");
        }

        // Validar capacidad del vuelo
        if (flight.getAirplane() != null) {
            long activeReservations = reservationRepository.findByFlightId(flightId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE)
                .count();
            
            if (activeReservations >= flight.getAirplane().getCapacity()) {
                throw new IllegalArgumentException("El vuelo está lleno. No hay asientos disponibles");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        reservation.setFlight(flight);
        reservation.setReservationCode(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation = reservationRepository.save(reservation);

        log.info("Reserva creada: {} para pasajero {} en vuelo {}", 
            reservation.getReservationCode(), email, flight.getFlightCode());
        return reservationMapper.toDTO(reservation);
    }

    /**
     * Cancela una reserva del pasajero.
     */
    @Transactional
    public void cancelReservation(String email, Long reservationId) {
        Passenger passenger = findByEmail(email);
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reservation.getPassenger().getId().equals(passenger.getId())) {
            throw new IllegalArgumentException("Esta reserva no pertenece al pasajero autenticado");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Reserva {} cancelada por pasajero {}", reservationId, email);
    }

    /**
     * Implementación de UserDetailsService para Spring Security.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Pasajero no encontrado: " + email));

        String role = passenger.getRole() != null ? passenger.getRole().name() : "PASSENGER";
        return User.withUsername(passenger.getEmail())
            .password(passenger.getPassword())
            .authorities("ROLE_" + role)
            .build();
    }
}
