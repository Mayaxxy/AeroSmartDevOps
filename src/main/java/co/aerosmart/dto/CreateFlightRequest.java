package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateFlightRequest {

    @NotBlank(message = "El código de vuelo es obligatorio")
    private String flightCode;

    @NotBlank(message = "El aeropuerto de origen es obligatorio")
    private String originAirport;

    @NotBlank(message = "El aeropuerto de destino es obligatorio")
    private String destinationAirport;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime departureTime;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDateTime arrivalTime;

    private String gate;
}
