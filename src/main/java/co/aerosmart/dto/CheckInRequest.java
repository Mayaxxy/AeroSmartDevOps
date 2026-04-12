package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de check-in.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    
    @NotBlank(message = "El código de reserva es obligatorio")
    private String reservationCode;
}
