package co.aerosmart.controllers;

import co.aerosmart.dto.CreateReceptionistRequest;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.model.Role;
import co.aerosmart.services.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para AdminController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Unit Tests")
class AdminControllerTest {

    @Mock
    private PassengerService passengerService;

    @InjectMocks
    private AdminController adminController;

    private CreateReceptionistRequest validRequest;
    private PassengerDTO receptionistDTO;

    @BeforeEach
    void setUp() {
        validRequest = new CreateReceptionistRequest();
        validRequest.setDocumentType("CC");
        validRequest.setDocumentId("987654321");
        validRequest.setFirstName("Jane");
        validRequest.setLastName("Smith");
        validRequest.setEmail("jane.smith@example.com");
        validRequest.setPhone("+573001111111");
        validRequest.setPassword("SecurePass123!");

        receptionistDTO = new PassengerDTO();
        receptionistDTO.setId(1L);
        receptionistDTO.setDocumentId("987654321");
        receptionistDTO.setFirstName("Jane");
        receptionistDTO.setLastName("Smith");
        receptionistDTO.setEmail("jane.smith@example.com");
        receptionistDTO.setPhone("+573001111111");
        receptionistDTO.setRole(Role.RECEPCIONISTA);
    }

    @Test
    @DisplayName("createReceptionist debe crear recepcionista y retornar 201 CREATED")
    void createReceptionist_shouldCreateReceptionistAndReturn201() {
        // Arrange
        when(passengerService.createReceptionist(any(CreateReceptionistRequest.class)))
            .thenReturn(receptionistDTO);

        // Act
        ResponseEntity<PassengerDTO> response = adminController.createReceptionist(validRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(response.getBody().getRole()).isEqualTo(Role.RECEPCIONISTA);
        verify(passengerService).createReceptionist(validRequest);
    }

    @Test
    @DisplayName("createReceptionist debe llamar al servicio con el request correcto")
    void createReceptionist_shouldCallServiceWithCorrectRequest() {
        // Arrange
        when(passengerService.createReceptionist(any(CreateReceptionistRequest.class)))
            .thenReturn(receptionistDTO);

        // Act
        adminController.createReceptionist(validRequest);

        // Assert
        verify(passengerService).createReceptionist(validRequest);
    }

    @Test
    @DisplayName("createReceptionist debe retornar el DTO del recepcionista creado")
    void createReceptionist_shouldReturnCreatedReceptionistDTO() {
        // Arrange
        when(passengerService.createReceptionist(any(CreateReceptionistRequest.class)))
            .thenReturn(receptionistDTO);

        // Act
        ResponseEntity<PassengerDTO> response = adminController.createReceptionist(validRequest);

        // Assert
        assertThat(response.getBody()).isEqualTo(receptionistDTO);
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getFirstName()).isEqualTo("Jane");
        assertThat(response.getBody().getLastName()).isEqualTo("Smith");
    }
}
