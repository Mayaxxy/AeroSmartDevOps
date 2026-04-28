package co.aerosmart.dto;

import co.aerosmart.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para datos de usuario en el módulo ADMIN.
 * Nunca incluye el campo password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDTO {
    private Long id;
    private String documentId;
    private String documentType;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
