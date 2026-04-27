package co.aerosmart.dto;

import co.aerosmart.model.AirplaneStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir información de aviones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirplaneDTO {
    private Long id;
    private String registration;
    private String model;
    private String manufacturer;
    private Integer capacity;
    private AirplaneStatus status;
    private Integer yearManufactured;
}
