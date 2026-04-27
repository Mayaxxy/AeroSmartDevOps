package co.aerosmart.services;

import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.dto.PassengerSearchResultDTO;
import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.mappers.PassengerMapper;
import co.aerosmart.mappers.ReservationMapper;
import co.aerosmart.model.Flight;
import co.aerosmart.model.Passenger;
import co.aerosmart.model.Reservation;
import co.aerosmart.model.Role;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para ReceptionistService verificando búsqueda de pasajeros.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReceptionistService Tests")
class ReceptionistServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private PassengerService passengerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReceptionistService receptionistService;

    private Passenger receptionist;
    private Passenger passenger;
    private Passenger adminUser;
    private List<Reservation> reservations;
    private PassengerDTO passengerDTO;
    private List<ReservationDTO> reservationDTOs;

    @BeforeEach
    void setUp() {
        // Setup receptionist user
        receptionist = new Passenger();
        receptionist.setId(1L);
        receptionist.setEmail("receptionist@example.com");
        receptionist.setRole(Role.RECEPCIONISTA);

        // Setup passenger
        passenger = new Passenger();
        passenger.setId(2L);
        passenger.setDocumentId("123456789");
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setRole(Role.PASSENGER);

        // Setup admin user
        adminUser = new Passenger();
        adminUser.setId(3L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        // Setup reservations
        reservations = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationCode("RES001");
        reservation.setPassenger(passenger);
        reservation.setFlight(new Flight());
        reservations.add(reservation);

        // Setup DTOs
        passengerDTO = new PassengerDTO();
        passengerDTO.setId(2L);
        passengerDTO.setDocumentId("123456789");

        reservationDTOs = new ArrayList<>();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(1L);
        reservationDTO.setReservationCode("RES001");
        reservationDTOs.add(reservationDTO);
    }

    @Test
    @DisplayName("SearchPassenger debe buscar por passengerId exitosamente")
    void searchPassenger_shouldSearchByPassengerIdSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findById(2L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.findByPassengerId(2L)).thenReturn(reservations);
        when(passengerMapper.toDTO(passenger)).thenReturn(passengerDTO);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTOs.get(0));

        // Act
        PassengerSearchResultDTO result = receptionistService.searchPassenger(null, 2L, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(passengerDTO, result.getPassenger());
        assertEquals(1, result.getReservations().size());
        verify(passengerRepository).findById(2L);
        verify(reservationRepository).findByPassengerId(2L);
    }

    @Test
    @DisplayName("SearchPassenger debe buscar por documentId exitosamente")
    void searchPassenger_shouldSearchByDocumentIdSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findByDocumentId("123456789")).thenReturn(Optional.of(passenger));
        when(reservationRepository.findByPassengerId(2L)).thenReturn(reservations);
        when(passengerMapper.toDTO(passenger)).thenReturn(passengerDTO);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTOs.get(0));

        // Act
        PassengerSearchResultDTO result = receptionistService.searchPassenger("123456789", null, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(passengerDTO, result.getPassenger());
        assertEquals(1, result.getReservations().size());
        verify(passengerRepository).findByDocumentId("123456789");
        verify(reservationRepository).findByPassengerId(2L);
    }

    @Test
    @DisplayName("SearchPassenger debe incluir información de reservas y vuelos")
    void searchPassenger_shouldIncludeReservationsAndFlights() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findById(2L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.findByPassengerId(2L)).thenReturn(reservations);
        when(passengerMapper.toDTO(passenger)).thenReturn(passengerDTO);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTOs.get(0));

        // Act
        PassengerSearchResultDTO result = receptionistService.searchPassenger(null, 2L, authentication);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getReservations());
        assertEquals(1, result.getReservations().size());
        verify(reservationMapper).toDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("SearchPassenger debe rechazar búsqueda sin parámetros")
    void searchPassenger_shouldRejectSearchWithoutParameters() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> receptionistService.searchPassenger(null, null, authentication)
        );
        assertEquals("Debe proporcionar documentId o passengerId para la búsqueda", exception.getMessage());
        verify(passengerRepository, never()).findById(anyLong());
        verify(passengerRepository, never()).findByDocumentId(anyString());
    }

    @Test
    @DisplayName("SearchPassenger debe rechazar búsqueda de usuario sin rol RECEPCIONISTA")
    void searchPassenger_shouldRejectSearchFromNonReceptionistUser() {
        // Arrange
        when(authentication.getName()).thenReturn("john.doe@example.com");
        when(passengerService.findByEmail("john.doe@example.com")).thenReturn(passenger);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> receptionistService.searchPassenger("123456789", null, authentication)
        );
        assertEquals("Solo usuarios con rol RECEPCIONISTA pueden realizar búsquedas de pasajeros", exception.getMessage());
        verify(passengerRepository, never()).findByDocumentId(anyString());
    }

    @Test
    @DisplayName("SearchPassenger debe rechazar búsqueda de usuario ADMIN")
    void searchPassenger_shouldRejectSearchFromAdminUser() {
        // Arrange
        when(authentication.getName()).thenReturn("admin@example.com");
        when(passengerService.findByEmail("admin@example.com")).thenReturn(adminUser);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> receptionistService.searchPassenger("123456789", null, authentication)
        );
        assertEquals("Solo usuarios con rol RECEPCIONISTA pueden realizar búsquedas de pasajeros", exception.getMessage());
        verify(passengerRepository, never()).findByDocumentId(anyString());
    }

    @Test
    @DisplayName("SearchPassenger debe rechazar búsqueda sin autenticación")
    void searchPassenger_shouldRejectSearchWithoutAuthentication() {
        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> receptionistService.searchPassenger("123456789", null, null)
        );
        assertEquals("Usuario no autenticado", exception.getMessage());
        verify(passengerRepository, never()).findByDocumentId(anyString());
    }

    @Test
    @DisplayName("SearchPassenger debe lanzar excepción cuando pasajero no existe por ID")
    void searchPassenger_shouldThrowExceptionWhenPassengerNotFoundById() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> receptionistService.searchPassenger(null, 999L, authentication)
        );
        assertEquals("Pasajero no encontrado con ID: 999", exception.getMessage());
        verify(reservationRepository, never()).findByPassengerId(anyLong());
    }

    @Test
    @DisplayName("SearchPassenger debe lanzar excepción cuando pasajero no existe por documento")
    void searchPassenger_shouldThrowExceptionWhenPassengerNotFoundByDocument() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findByDocumentId("999999999")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> receptionistService.searchPassenger("999999999", null, authentication)
        );
        assertEquals("Pasajero no encontrado con documento: 999999999", exception.getMessage());
        verify(reservationRepository, never()).findByPassengerId(anyLong());
    }

    @Test
    @DisplayName("SearchPassenger debe retornar lista vacía de reservas si pasajero no tiene reservas")
    void searchPassenger_shouldReturnEmptyReservationsListWhenPassengerHasNoReservations() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findById(2L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.findByPassengerId(2L)).thenReturn(new ArrayList<>());
        when(passengerMapper.toDTO(passenger)).thenReturn(passengerDTO);

        // Act
        PassengerSearchResultDTO result = receptionistService.searchPassenger(null, 2L, authentication);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getReservations());
        assertEquals(0, result.getReservations().size());
    }

    @Test
    @DisplayName("SearchPassenger debe priorizar passengerId sobre documentId cuando ambos están presentes")
    void searchPassenger_shouldPrioritizePassengerIdOverDocumentId() {
        // Arrange
        when(authentication.getName()).thenReturn("receptionist@example.com");
        when(passengerService.findByEmail("receptionist@example.com")).thenReturn(receptionist);
        when(passengerRepository.findById(2L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.findByPassengerId(2L)).thenReturn(reservations);
        when(passengerMapper.toDTO(passenger)).thenReturn(passengerDTO);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationDTOs.get(0));

        // Act
        PassengerSearchResultDTO result = receptionistService.searchPassenger("123456789", 2L, authentication);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).findById(2L);
        verify(passengerRepository, never()).findByDocumentId(anyString());
    }
}
