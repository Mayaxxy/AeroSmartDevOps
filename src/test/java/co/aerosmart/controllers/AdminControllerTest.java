package co.aerosmart.controllers;

import co.aerosmart.dto.AdminCreateUserRequest;
import co.aerosmart.dto.AdminUserDTO;
import co.aerosmart.model.Role;
import co.aerosmart.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para AdminController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Unit Tests")
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AdminCreateUserRequest validRequest;
    private AdminUserDTO userDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        validRequest = new AdminCreateUserRequest();
        validRequest.setDocumentType("CC");
        validRequest.setDocumentId("987654321");
        validRequest.setFirstName("Jane");
        validRequest.setLastName("Smith");
        validRequest.setEmail("jane.smith@example.com");
        validRequest.setPhone("+573001111111");
        validRequest.setPassword("SecurePass123!");
        validRequest.setRole(Role.RECEPCIONISTA);

        userDTO = new AdminUserDTO();
        userDTO.setId(1L);
        userDTO.setDocumentId("987654321");
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPhone("+573001111111");
        userDTO.setRole(Role.RECEPCIONISTA);
        userDTO.setActive(true);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
    }

    @Test
    @DisplayName("createUser debe crear usuario y retornar 201 CREATED")
    void createUser_shouldCreateUserAndReturn201() {
        // Arrange
        when(adminService.createUser(anyString(), any(AdminCreateUserRequest.class)))
            .thenReturn(userDTO);

        // Act
        ResponseEntity<AdminUserDTO> response = adminController.createUser(authentication, validRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(response.getBody().getRole()).isEqualTo(Role.RECEPCIONISTA);
        verify(adminService).createUser("admin@example.com", validRequest);
    }

    @Test
    @DisplayName("createUser debe llamar al servicio con el request correcto")
    void createUser_shouldCallServiceWithCorrectRequest() {
        // Arrange
        when(adminService.createUser(anyString(), any(AdminCreateUserRequest.class)))
            .thenReturn(userDTO);

        // Act
        adminController.createUser(authentication, validRequest);

        // Assert
        verify(adminService).createUser("admin@example.com", validRequest);
    }

    @Test
    @DisplayName("createUser debe retornar el DTO del usuario creado")
    void createUser_shouldReturnCreatedUserDTO() {
        // Arrange
        when(adminService.createUser(anyString(), any(AdminCreateUserRequest.class)))
            .thenReturn(userDTO);

        // Act
        ResponseEntity<AdminUserDTO> response = adminController.createUser(authentication, validRequest);

        // Assert
        assertThat(response.getBody()).isEqualTo(userDTO);
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getFirstName()).isEqualTo("Jane");
        assertThat(response.getBody().getLastName()).isEqualTo("Smith");
        assertThat(response.getBody().getActive()).isTrue();
    }
}
