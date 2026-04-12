package co.aerosmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de nuevos pasajeros.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
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
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
