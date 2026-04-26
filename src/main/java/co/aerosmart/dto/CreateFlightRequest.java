package co.aerosmart.dto;

import co.aerosmart.validation.ValidFlightDates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ValidFlightDates
public class CreateFlightRequest {

    @NotBlank(message = "El código de vuelo es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3,4}$", message = "El código de vuelo debe tener formato AA123 o AA1234 (2 letras mayúsculas + 3-4 números)")
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
