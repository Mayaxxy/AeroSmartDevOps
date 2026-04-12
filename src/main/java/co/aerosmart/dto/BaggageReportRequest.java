package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un reporte de equipaje.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaggageReportRequest {
    
    @NotNull(message = "El ID del vuelo es obligatorio")
    private Long flightId;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
}
