package co.aerosmart.services;

import co.aerosmart.dto.BaggageReportDTO;
import co.aerosmart.dto.BaggageReportRequest;
import co.aerosmart.mappers.BaggageReportMapper;
import co.aerosmart.model.*;
import co.aerosmart.repository.BaggageReportRepository;
import co.aerosmart.repository.PassengerRepository;
import co.aerosmart.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para BaggageReportService.
 * Verifica la funcionalidad de auditoría con receptionistId.
 */
@ExtendWith(MockitoExtension.class)
class BaggageReportServiceTest {

    @Mock
    private BaggageReportRepository baggageReportRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerService passengerService;

    @Mock
    private FlightService flightService;

    @Mock
    private BaggageReportMapper baggageReportMapper;

    @InjectMocks
    private BaggageReportService baggageReportService;

    private Passenger passenger;
    private Passenger receptionist;
    private Flight flight;
    private BaggageReportRequest request;
    private BaggageReport savedReport;
    private BaggageReportDTO reportDTO;

    @BeforeEach
    void setUp() {
        // Setup passenger
        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setEmail("passenger@test.com");
        passenger.setRole(Role.PASSENGER);

        // Setup receptionist
        receptionist = new Passenger();
        receptionist.setId(2L);
        receptionist.setEmail("receptionist@test.com");
        receptionist.setRole(Role.RECEPCIONISTA);

        // Setup flight
        flight = new Flight();
        flight.setId(100L);
        flight.setFlightCode("AA123");

        // Setup request
        request = new BaggageReportRequest();
        request.setFlightId(100L);
        request.setDescription("Equipaje dañado");

        // Setup saved report
        savedReport = new BaggageReport();
        savedReport.setId(1L);
        savedReport.setPassenger(passenger);
        savedReport.setFlight(flight);
        savedReport.setDescription("Equipaje dañado");
        savedReport.setStatus(BaggageReportStatus.PENDING);

        // Setup DTO
        reportDTO = new BaggageReportDTO();
        reportDTO.setId(1L);
        reportDTO.setDescription("Equipaje dañado");
    }

    @Test
    void createReport_AsPassenger_ShouldNotSetReceptionistId() {
        // Arrange
        when(passengerService.findByEmail("passenger@test.com")).thenReturn(passenger);
        when(flightService.findById(100L)).thenReturn(flight);
        
        // Mock reservation with ACTIVE status
        Reservation activeReservation = new Reservation();
        activeReservation.setPassenger(passenger);
        activeReservation.setFlight(flight);
        activeReservation.setStatus(co.aerosmart.model.ReservationStatus.ACTIVE);
        when(reservationRepository.findByPassengerId(1L)).thenReturn(Collections.singletonList(activeReservation));
        
        when(baggageReportRepository.countActiveReportsByPassengerId(1L)).thenReturn(0L);
        when(baggageReportRepository.save(any(BaggageReport.class))).thenReturn(savedReport);
        when(baggageReportMapper.toDTO(savedReport)).thenReturn(reportDTO);

        // Setup authentication as PASSENGER
        Authentication auth = new UsernamePasswordAuthenticationToken(
            "passenger@test.com",
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSENGER"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Act
        BaggageReportDTO result = baggageReportService.createReport(request, "passenger@test.com");

        // Assert
        assertNotNull(result);
        ArgumentCaptor<BaggageReport> reportCaptor = ArgumentCaptor.forClass(BaggageReport.class);
        verify(baggageReportRepository).save(reportCaptor.capture());
        BaggageReport capturedReport = reportCaptor.getValue();
        assertNull(capturedReport.getReceptionistId(), "ReceptionistId should be null for passenger");
        
        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReport_AsReceptionist_ShouldSetReceptionistId() {
        // Arrange
        when(passengerService.findByEmail("passenger@test.com")).thenReturn(passenger);
        when(flightService.findById(100L)).thenReturn(flight);
        
        // Mock reservation with ACTIVE status
        Reservation activeReservation = new Reservation();
        activeReservation.setPassenger(passenger);
        activeReservation.setFlight(flight);
        activeReservation.setStatus(co.aerosmart.model.ReservationStatus.ACTIVE);
        when(reservationRepository.findByPassengerId(1L)).thenReturn(Collections.singletonList(activeReservation));
        
        when(baggageReportRepository.countActiveReportsByPassengerId(1L)).thenReturn(0L);
        when(passengerRepository.findByEmail("receptionist@test.com")).thenReturn(Optional.of(receptionist));
        
        savedReport.setReceptionistId(2L);
        when(baggageReportRepository.save(any(BaggageReport.class))).thenReturn(savedReport);
        when(baggageReportMapper.toDTO(savedReport)).thenReturn(reportDTO);

        // Setup authentication as RECEPCIONISTA
        Authentication auth = new UsernamePasswordAuthenticationToken(
            "receptionist@test.com",
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECEPCIONISTA"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Act
        BaggageReportDTO result = baggageReportService.createReport(request, "passenger@test.com");

        // Assert
        assertNotNull(result);
        ArgumentCaptor<BaggageReport> reportCaptor = ArgumentCaptor.forClass(BaggageReport.class);
        verify(baggageReportRepository).save(reportCaptor.capture());
        BaggageReport capturedReport = reportCaptor.getValue();
        assertEquals(2L, capturedReport.getReceptionistId(), "ReceptionistId should be set for receptionist");
        verify(passengerRepository).findByEmail("receptionist@test.com");
        
        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReport_NoActiveReservation_ShouldThrowException() {
        // Arrange
        when(passengerService.findByEmail("passenger@test.com")).thenReturn(passenger);
        when(flightService.findById(100L)).thenReturn(flight);
        
        // Mock reservation with CANCELLED status
        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setPassenger(passenger);
        cancelledReservation.setFlight(flight);
        cancelledReservation.setStatus(co.aerosmart.model.ReservationStatus.CANCELLED);
        when(reservationRepository.findByPassengerId(1L)).thenReturn(Collections.singletonList(cancelledReservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> baggageReportService.createReport(request, "passenger@test.com")
        );
        assertEquals("No tienes una reserva válida en este vuelo", exception.getMessage());
        verify(baggageReportRepository, never()).save(any());
    }

    @Test
    void createReport_ExceedsMaxActiveReports_ShouldThrowException() {
        // Arrange
        when(passengerService.findByEmail("passenger@test.com")).thenReturn(passenger);
        when(flightService.findById(100L)).thenReturn(flight);
        
        // Mock reservation with ACTIVE status
        Reservation activeReservation = new Reservation();
        activeReservation.setPassenger(passenger);
        activeReservation.setFlight(flight);
        activeReservation.setStatus(co.aerosmart.model.ReservationStatus.ACTIVE);
        when(reservationRepository.findByPassengerId(1L)).thenReturn(Collections.singletonList(activeReservation));
        
        when(baggageReportRepository.countActiveReportsByPassengerId(1L)).thenReturn(3L);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> baggageReportService.createReport(request, "passenger@test.com")
        );
        assertTrue(exception.getMessage().contains("límite de 3 reportes activos"));
        verify(baggageReportRepository, never()).save(any());
    }
}
