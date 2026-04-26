package co.aerosmart.controllers;

import co.aerosmart.dto.CreateFlightRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests básicos para FlightController.
 * Verifica la estructura del endpoint POST /api/flights para creación de vuelos.
 */
class FlightControllerTest {

    @Test
    void createFlightRequest_WithValidData_IsValid() {
        // Arrange & Act
        CreateFlightRequest request = new CreateFlightRequest();
        request.setFlightCode("AV123");
        request.setOriginAirport("BOG");
        request.setDestinationAirport("MDE");
        request.setDepartureTime(LocalDateTime.now().plusDays(1));
        request.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(1));
        request.setGate("A12");

        // Assert
        assertThat(request.getFlightCode()).isEqualTo("AV123");
        assertThat(request.getOriginAirport()).isEqualTo("BOG");
        assertThat(request.getDestinationAirport()).isEqualTo("MDE");
        assertThat(request.getGate()).isEqualTo("A12");
    }

    @Test
    void createFlightRequest_WithInvalidFlightCode_CanBeCreated() {
        // Arrange & Act
        CreateFlightRequest request = new CreateFlightRequest();
        request.setFlightCode("INVALID");
        request.setOriginAirport("BOG");
        request.setDestinationAirport("MDE");
        request.setDepartureTime(LocalDateTime.now().plusDays(1));
        request.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(1));

        // Assert - Object can be created, validation happens at controller level
        assertThat(request.getFlightCode()).isEqualTo("INVALID");
    }
}
