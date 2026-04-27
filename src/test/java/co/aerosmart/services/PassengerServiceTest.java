package co.aerosmart.services;

import co.aerosmart.dto.AuthResponse;
import co.aerosmart.dto.CreateReceptionistRequest;
import co.aerosmart.dto.LoginRequest;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.dto.RegisterRequest;
import co.aerosmart.dto.UpdateProfileRequest;
import co.aerosmart.mappers.PassengerMapper;
import co.aerosmart.model.Passenger;
import co.aerosmart.model.Role;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para PassengerService verificando funcionalidad de roles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PassengerService Tests")
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerService passengerService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private CreateReceptionistRequest createReceptionistRequest;
    private Passenger passenger;
    private Passenger receptionist;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setDocumentId("123456789");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPhone("+57 300 000 0000");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        createReceptionistRequest = new CreateReceptionistRequest();
        createReceptionistRequest.setDocumentType("CC");
        createReceptionistRequest.setDocumentId("987654321");
        createReceptionistRequest.setFirstName("Jane");
        createReceptionistRequest.setLastName("Smith");
        createReceptionistRequest.setEmail("jane.smith@example.com");
        createReceptionistRequest.setPhone("+57 300 111 1111");
        createReceptionistRequest.setPassword("SecurePass123!");

        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setDocumentId("123456789");
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPhone("+57 300 000 0000");
        passenger.setPassword("encodedPassword");
        passenger.setRole(Role.PASSENGER);

        receptionist = new Passenger();
        receptionist.setId(2L);
        receptionist.setDocumentId("987654321");
        receptionist.setFirstName("Jane");
        receptionist.setLastName("Smith");
        receptionist.setEmail("jane.smith@example.com");
        receptionist.setPhone("+57 300 111 1111");
        receptionist.setPassword("encodedPassword");
        receptionist.setRole(Role.RECEPCIONISTA);
    }

    @Test
    @DisplayName("Register debe asignar rol PASSENGER por defecto")
    void register_shouldAssignPassengerRoleByDefault() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(null);

        // Act
        AuthResponse response = passengerService.register(registerRequest);

        // Assert
        assertNotNull(response);
        verify(passengerRepository).save(argThat(p -> 
            p.getRole() == Role.PASSENGER
        ));
        verify(jwtUtil).generateToken(eq("john.doe@example.com"), eq("PASSENGER"));
    }

    @Test
    @DisplayName("Register debe incluir rol en JWT token")
    void register_shouldIncludeRoleInJwtToken() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(null);

        // Act
        passengerService.register(registerRequest);

        // Assert
        verify(jwtUtil).generateToken(eq("john.doe@example.com"), eq("PASSENGER"));
    }

    @Test
    @DisplayName("Login debe incluir rol en JWT token")
    void login_shouldIncludeRoleInJwtToken() {
        // Arrange
        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(null);

        // Act
        AuthResponse response = passengerService.login(loginRequest);

        // Assert
        assertNotNull(response);
        verify(jwtUtil).generateToken(eq("john.doe@example.com"), eq("PASSENGER"));
    }

    @Test
    @DisplayName("Login debe funcionar con rol RECEPCIONISTA")
    void login_shouldWorkWithReceptionistRole() {
        // Arrange
        passenger.setRole(Role.RECEPCIONISTA);
        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(null);

        // Act
        AuthResponse response = passengerService.login(loginRequest);

        // Assert
        assertNotNull(response);
        verify(jwtUtil).generateToken(eq("john.doe@example.com"), eq("RECEPCIONISTA"));
    }

    @Test
    @DisplayName("Login debe funcionar con rol ADMIN")
    void login_shouldWorkWithAdminRole() {
        // Arrange
        passenger.setRole(Role.ADMIN);
        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(null);

        // Act
        AuthResponse response = passengerService.login(loginRequest);

        // Assert
        assertNotNull(response);
        verify(jwtUtil).generateToken(eq("john.doe@example.com"), eq("ADMIN"));
    }

    @Test
    @DisplayName("Register debe rechazar documento duplicado")
    void register_shouldRejectDuplicateDocument() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.register(registerRequest)
        );
        assertEquals("Ya existe un pasajero con ese documento", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register debe rechazar email duplicado")
    void register_shouldRejectDuplicateEmail() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.register(registerRequest)
        );
        assertEquals("Ya existe un pasajero con ese email", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login debe rechazar credenciales inválidas - usuario no existe")
    void login_shouldRejectInvalidCredentials_userNotFound() {
        // Arrange
        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> passengerService.login(loginRequest)
        );
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Login debe rechazar credenciales inválidas - contraseña incorrecta")
    void login_shouldRejectInvalidCredentials_wrongPassword() {
        // Arrange
        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(
            org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> passengerService.login(loginRequest)
        );
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("CreateReceptionist debe crear usuario con rol RECEPCIONISTA")
    void createReceptionist_shouldCreateUserWithReceptionistRole() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(receptionist);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        PassengerDTO result = passengerService.createReceptionist(createReceptionistRequest);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).save(argThat(p -> 
            p.getRole() == Role.RECEPCIONISTA &&
            p.getEmail().equals("jane.smith@example.com") &&
            p.getFirstName().equals("Jane") &&
            p.getLastName().equals("Smith")
        ));
    }

    @Test
    @DisplayName("CreateReceptionist debe rechazar documento duplicado")
    void createReceptionist_shouldRejectDuplicateDocument() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.createReceptionist(createReceptionistRequest)
        );
        assertEquals("Ya existe un usuario con ese documento", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("CreateReceptionist debe rechazar email duplicado")
    void createReceptionist_shouldRejectDuplicateEmail() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.createReceptionist(createReceptionistRequest)
        );
        assertEquals("Ya existe un usuario con ese email", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("CreateReceptionist debe encriptar la contraseña")
    void createReceptionist_shouldEncryptPassword() {
        // Arrange
        when(passengerRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(passengerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(receptionist);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        passengerService.createReceptionist(createReceptionistRequest);

        // Assert
        verify(passwordEncoder).encode(eq("SecurePass123!"));
        verify(passengerRepository).save(argThat(p -> 
            p.getPassword().equals("encodedPassword")
        ));
    }

    // ========== Tests para updateProfile ==========

    @Test
    @DisplayName("UpdateProfile debe actualizar nombre y teléfono exitosamente")
    void updateProfile_shouldUpdateNameAndPhoneSuccessfully() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Juan");
        request.setLastName("Pérez");
        request.setPhone("+57 300 999 9999");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        PassengerDTO result = passengerService.updateProfile("john.doe@example.com", request);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).save(argThat(p -> 
            p.getFirstName().equals("Juan") &&
            p.getLastName().equals("Pérez") &&
            p.getPhone().equals("+57 300 999 9999")
        ));
    }

    @Test
    @DisplayName("UpdateProfile debe actualizar todos los campos permitidos")
    void updateProfile_shouldUpdateAllAllowedFields() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Juan");
        request.setMiddleName("Carlos");
        request.setLastName("Pérez");
        request.setSecondLastName("García");
        request.setDocumentType("CC");
        request.setBirthDate(LocalDate.of(1990, 5, 15));
        request.setPhone("+57 300 999 9999");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        PassengerDTO result = passengerService.updateProfile("john.doe@example.com", request);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).save(argThat(p -> 
            p.getFirstName().equals("Juan") &&
            p.getMiddleName().equals("Carlos") &&
            p.getLastName().equals("Pérez") &&
            p.getSecondLastName().equals("García") &&
            p.getDocumentType().equals("CC") &&
            p.getBirthDate() != null &&
            p.getPhone().equals("+57 300 999 9999")
        ));
    }

    @Test
    @DisplayName("UpdateProfile debe cambiar contraseña con currentPassword válido")
    void updateProfile_shouldChangePasswordWithValidCurrentPassword() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("NewSecurePass123!");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewSecurePass123!")).thenReturn("newEncodedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        PassengerDTO result = passengerService.updateProfile("john.doe@example.com", request);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(passwordEncoder).encode("NewSecurePass123!");
        verify(passengerRepository).save(argThat(p -> 
            p.getPassword().equals("newEncodedPassword")
        ));
    }

    @Test
    @DisplayName("UpdateProfile debe rechazar cambio de contraseña con currentPassword inválido")
    void updateProfile_shouldRejectPasswordChangeWithInvalidCurrentPassword() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("NewSecurePass123!");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.updateProfile("john.doe@example.com", request)
        );
        assertEquals("La contraseña actual es incorrecta", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("UpdateProfile debe rechazar si solo se proporciona currentPassword sin newPassword")
    void updateProfile_shouldRejectIfOnlyCurrentPasswordProvided() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setCurrentPassword("password123");
        // newPassword es null

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.updateProfile("john.doe@example.com", request)
        );
        assertEquals("Para cambiar la contraseña, debe proporcionar tanto la contraseña actual como la nueva", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("UpdateProfile debe rechazar si solo se proporciona newPassword sin currentPassword")
    void updateProfile_shouldRejectIfOnlyNewPasswordProvided() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        // currentPassword es null
        request.setNewPassword("NewSecurePass123!");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> passengerService.updateProfile("john.doe@example.com", request)
        );
        assertEquals("Para cambiar la contraseña, debe proporcionar tanto la contraseña actual como la nueva", exception.getMessage());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @DisplayName("UpdateProfile no debe modificar email (campo no existe en UpdateProfileRequest)")
    void updateProfile_shouldNotModifyEmail() {
        // Arrange
        String originalEmail = "john.doe@example.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Juan");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        passengerService.updateProfile(originalEmail, request);

        // Assert
        verify(passengerRepository).save(argThat(p -> 
            p.getEmail().equals(originalEmail) // Email no debe cambiar
        ));
    }

    @Test
    @DisplayName("UpdateProfile no debe modificar documentId (campo no existe en UpdateProfileRequest)")
    void updateProfile_shouldNotModifyDocumentId() {
        // Arrange
        String originalDocumentId = "123456789";
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Juan");

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        passengerService.updateProfile("john.doe@example.com", request);

        // Assert
        verify(passengerRepository).save(argThat(p -> 
            p.getDocumentId().equals(originalDocumentId) // DocumentId no debe cambiar
        ));
    }

    @Test
    @DisplayName("UpdateProfile debe permitir actualización parcial de campos")
    void updateProfile_shouldAllowPartialFieldUpdate() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPhone("+57 300 888 8888"); // Solo actualizar teléfono

        when(passengerRepository.findByEmail(anyString())).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toDTO(any(Passenger.class))).thenReturn(new PassengerDTO());

        // Act
        PassengerDTO result = passengerService.updateProfile("john.doe@example.com", request);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).save(argThat(p -> 
            p.getPhone().equals("+57 300 888 8888") &&
            p.getFirstName().equals("John") && // Nombre original no cambia
            p.getLastName().equals("Doe") // Apellido original no cambia
        ));
    }
}
