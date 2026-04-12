package co.aerosmart.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    // Tipo de documento: CC, PASSPORT, CE
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(CC|PASSPORT|CE)$", message = "Tipo de documento inválido")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    private String documentId;
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
    private String firstName;

    // Segundo nombre opcional
    @Size(max = 50, message = "El segundo nombre no puede superar 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]*$", message = "El segundo nombre solo puede contener letras")
    private String middleName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
    private String lastName;

    // Segundo apellido opcional
    @Size(max = 50, message = "El segundo apellido no puede superar 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]*$", message = "El segundo apellido solo puede contener letras")
    private String secondLastName;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "El email no tiene un formato válido"
    )
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 15, message = "El teléfono debe tener entre 7 y 15 dígitos")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono solo puede contener números")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "La contraseña debe tener mayúscula, minúscula, número y carácter especial"
    )
    private String password;

    @AssertTrue(message = "Debes aceptar la política de tratamiento de datos")
    private boolean acceptedDataPolicy;
}
