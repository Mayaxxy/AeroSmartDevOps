package co.aerosmart.dto;

import co.aerosmart.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el ADMIN cree un nuevo usuario con cualquier rol.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateUserRequest {

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(CC|PASSPORT|CE)$", message = "Tipo de documento inválido. Use: CC, PASSPORT o CE")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    private String documentId;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
    private String firstName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono solo puede contener números (7-15 dígitos)")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;

    @NotNull(message = "El rol es obligatorio. Valores permitidos: ADMIN, RECEPCIONISTA")
    private Role role;
}
