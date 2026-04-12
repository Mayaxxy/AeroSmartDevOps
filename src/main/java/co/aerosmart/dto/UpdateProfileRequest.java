package co.aerosmart.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "Solo letras")
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "Solo letras")
    private String lastName;

    @Size(max = 50)
    private String secondLastName;

    private String documentType;

    private LocalDate birthDate;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Teléfono inválido")
    private String phone;

    // Para cambio de contraseña (opcional)
    private String currentPassword;

    @Size(min = 8, max = 64)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener mayúscula, minúscula, número y carácter especial"
    )
    private String newPassword;
}
