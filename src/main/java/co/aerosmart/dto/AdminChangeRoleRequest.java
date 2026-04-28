package co.aerosmart.dto;

import co.aerosmart.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el ADMIN cambie el rol de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminChangeRoleRequest {

    @NotNull(message = "El rol es obligatorio")
    private Role role;
}
