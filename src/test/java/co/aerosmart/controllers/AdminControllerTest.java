package co.aerosmart.controllers;

import co.aerosmart.dto.*;
import co.aerosmart.model.Role;
import co.aerosmart.services.AdminService;
import co.aerosmart.services.FlightService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Unit Tests")
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private FlightService flightService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    private AdminUserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new AdminUserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("jane.smith@example.com");
    }

    @Test
    @DisplayName("getAllUsers debe retornar lista de usuarios")
    void getAllUsers_shouldReturnUserList() {
        when(adminService.getAllUsers(any(), any())).thenReturn(List.of(userDTO));

        ResponseEntity<List<AdminUserDTO>> response = adminController.getAllUsers(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("getUserById debe retornar usuario por ID")
    void getUserById_shouldReturnUser() {
        when(adminService.getUserById(1L)).thenReturn(userDTO);

        ResponseEntity<AdminUserDTO> response = adminController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deleteUser debe retornar 204 NO CONTENT")
    void deleteUser_shouldReturn204() {
        when(authentication.getName()).thenReturn("admin@test.com");
        doNothing().when(adminService).deleteUser(any(), any());

        ResponseEntity<Void> response = adminController.deleteUser(authentication, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(adminService).deleteUser("admin@test.com", 1L);
    }
}