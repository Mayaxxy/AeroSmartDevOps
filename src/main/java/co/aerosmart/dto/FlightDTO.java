package co.aerosmart.dto;

import co.aerosmart.model.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Flight.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private Long id;
    private String flightCode;
    private String originAirport;
    private String destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime estimatedDepartureTime;
    private LocalDateTime arrivalTime;
    private FlightStatus status;
    private String gate;
    private LocalDateTime boardingStartTime;
    private LocalDateTime boardingCloseTime;
    private AirplaneDTO airplane; // Información del avión asignado
}
