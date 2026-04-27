package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de check-in realizada por recepcionista.
 * Incluye el email del pasajero y el código de reserva.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistCheckInRequest {
    
    @NotBlank(message = "El email del pasajero es obligatorio")
    private String passengerEmail;
    
    @NotBlank(message = "El código de reserva es obligatorio")
    private String reservationCode;
}
