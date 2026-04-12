package co.aerosmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Passenger.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {
    private Long id;
    
    @NotBlank(message = "El documento es obligatorio")
    private String documentId;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;
}
