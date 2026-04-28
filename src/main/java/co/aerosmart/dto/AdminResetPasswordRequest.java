package co.aerosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el ADMIN restablezca la contraseña de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResetPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String newPassword;
}
