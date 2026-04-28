package co.aerosmart.repository;

import co.aerosmart.model.AuditAction;
import co.aerosmart.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para registros de auditoría del módulo ADMIN.
 * Los registros son de solo lectura una vez creados.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** Todos los registros ordenados por fecha descendente */
    List<AuditLog> findAllByOrderByCreatedAtDesc();

    /** Registros filtrados por usuario objetivo */
    List<AuditLog> findByTargetIdOrderByCreatedAtDesc(Long targetId);

    /** Registros filtrados por tipo de acción */
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action);

    /** Registros filtrados por usuario objetivo y tipo de acción */
    List<AuditLog> findByTargetIdAndActionOrderByCreatedAtDesc(Long targetId, AuditAction action);
}
