package co.aerosmart.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el ADMIN edite los datos de un usuario.
 * Solo se permiten actualizar: firstName, lastName, phone, documentId, documentType.
 * El email no puede ser modificado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserRequest {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
    private String firstName;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono solo puede contener números (7-15 dígitos)")
    private String phone;

    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    private String documentId;

    @Pattern(regexp = "^(CC|PASSPORT|CE)$", message = "Tipo de documento inválido. Use: CC, PASSPORT o CE")
    private String documentType;
}
