package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
