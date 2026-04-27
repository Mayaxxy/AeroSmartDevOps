package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de reporte de equipaje realizada por recepcionista.
 * Incluye el email del pasajero, ID del vuelo y descripción del problema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistBaggageReportRequest {
    
    @NotBlank(message = "El email del pasajero es obligatorio")
    private String passengerEmail;
    
    @NotNull(message = "El ID del vuelo es obligatorio")
    private Long flightId;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
}
