package co.aerosmart.dto;

import co.aerosmart.model.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para registros de auditoría.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private Long adminId;
    private Long targetId;
    private String targetEmail;
    private AuditAction action;
    private String description;
    private LocalDateTime createdAt;
}
