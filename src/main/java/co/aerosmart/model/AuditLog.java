package co.aerosmart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registro inmutable de auditoría para todas las acciones del módulo ADMIN.
 * Almacena quién realizó la acción, sobre qué usuario, qué tipo de acción y cuándo.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del administrador que realizó la acción */
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    /** ID del usuario afectado (puede ser null si fue eliminado) */
    @Column(name = "target_id")
    private Long targetId;

    /** Email del usuario afectado (preservado incluso tras eliminación) */
    @Column(name = "target_email", length = 100)
    private String targetEmail;

    /** Tipo de acción realizada */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    /** Descripción detallada del cambio */
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
