package co.aerosmart.services;

import co.aerosmart.dto.*;
import co.aerosmart.model.AuditAction;
import co.aerosmart.model.AuditLog;
import co.aerosmart.model.Passenger;
import co.aerosmart.model.Role;
import co.aerosmart.repository.AuditLogRepository;
import co.aerosmart.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para el módulo ADMIN de FlyTrack.
 * Gestiona usuarios (CRUD), roles, activación/desactivación y auditoría.
 * Garantiza que siempre exista al menos un ADMIN activo en el sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private static final String MUST_HAVE_ONE_ADMIN = "Debe existir al menos un ADMIN activo en el sistema";

    private final PassengerRepository passengerRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    // ─────────────────────────────────────────────────────────────────────────
    // Creación de usuarios
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo usuario con el rol especificado.
     * Valida unicidad de email y documentId.
     */
    @Transactional
    public AdminUserDTO createUser(String adminEmail, AdminCreateUserRequest request) {
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema");
        }
        if (passengerRepository.existsByDocumentId(request.getDocumentId())) {
            throw new IllegalArgumentException("El documento ya está registrado en el sistema");
        }
        if (request.getRole() == Role.PASSENGER) {
            throw new IllegalArgumentException("El admin no puede crear pasajeros. Los pasajeros se registran por su cuenta.");
        }

        Passenger user = new Passenger();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setDocumentId(request.getDocumentId().trim());
        user.setDocumentType(request.getDocumentType().trim());
        user.setPhone(request.getPhone().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);
        user.setAcceptedDataPolicy(true);

        user = passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.CREATE,
                "Usuario creado con rol " + user.getRole().name());

        log.info("Admin {} creó usuario {} con rol {}", adminEmail, user.getEmail(), user.getRole());
        return toAdminUserDTO(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consulta de usuarios
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Lista todos los usuarios con filtros opcionales por rol y estado.
     */
    @Transactional(readOnly = true)
    public List<AdminUserDTO> getAllUsers(Role role, Boolean active) {
        List<Passenger> users;

        if (role != null && active != null) {
            users = passengerRepository.findByRoleAndActive(role, active);
        } else if (role != null) {
            users = passengerRepository.findByRole(role);
        } else if (active != null) {
            users = passengerRepository.findByActive(active);
        } else {
            users = passengerRepository.findAll();
        }

        return users.stream().map(this::toAdminUserDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID.
     */
    @Transactional(readOnly = true)
    public AdminUserDTO getUserById(Long id) {
        Passenger user = findUserById(id);
        return toAdminUserDTO(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Edición de usuarios
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Actualiza los campos permitidos de un usuario.
     * El email no puede ser modificado.
     */
    @Transactional
    public AdminUserDTO updateUser(String adminEmail, Long id, AdminUpdateUserRequest request) {
        Passenger user = findUserById(id);

        StringBuilder changes = new StringBuilder("Campos actualizados: ");

        if (request.getDocumentId() != null) {
            if (passengerRepository.existsByDocumentIdAndIdNot(request.getDocumentId(), id)) {
                throw new IllegalArgumentException("El documento ya está registrado en el sistema");
            }
            changes.append("documentId, ");
            user.setDocumentId(request.getDocumentId().trim());
        }
        if (request.getDocumentType() != null) {
            changes.append("documentType, ");
            user.setDocumentType(request.getDocumentType().trim());
        }
        if (request.getFirstName() != null) {
            changes.append("firstName, ");
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null) {
            changes.append("lastName, ");
            user.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null) {
            changes.append("phone, ");
            user.setPhone(request.getPhone().trim());
        }

        user = passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.UPDATE,
                changes.toString().replaceAll(", $", ""));

        log.info("Admin {} actualizó usuario {}", adminEmail, user.getEmail());
        return toAdminUserDTO(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cambio de rol
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cambia el rol de un usuario.
     * Garantiza que siempre quede al menos un ADMIN activo.
     */
    @Transactional
    public AdminUserDTO changeRole(String adminEmail, Long id, AdminChangeRoleRequest request) {
        Passenger user = findUserById(id);
        Role previousRole = user.getRole();
        Role newRole = request.getRole();

        if (previousRole == Role.ADMIN && newRole != Role.ADMIN && Boolean.TRUE.equals(user.getActive())) {
            assertAtLeastOneAdminRemains(id);
        }

        user.setRole(newRole);
        user = passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.ROLE_CHANGE,
                "Rol cambiado de " + previousRole.name() + " a " + newRole.name());

        log.info("Admin {} cambió rol de {} de {} a {}", adminEmail, user.getEmail(), previousRole, newRole);
        return toAdminUserDTO(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Desactivación / Reactivación
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Desactiva una cuenta de usuario sin eliminarla.
     * Garantiza que siempre quede al menos un ADMIN activo.
     */
    @Transactional
    public void deactivateUser(String adminEmail, Long id) {
        Passenger user = findUserById(id);

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new IllegalStateException("El usuario ya está desactivado");
        }

        if (user.getRole() == Role.ADMIN) {
            assertAtLeastOneAdminRemains(id);
        }

        user.setActive(false);
        passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.DEACTIVATE,
                "Cuenta desactivada");

        log.info("Admin {} desactivó usuario {}", adminEmail, user.getEmail());
    }

    /**
     * Reactiva una cuenta de usuario previamente desactivada.
     */
    @Transactional
    public AdminUserDTO reactivateUser(String adminEmail, Long id) {
        Passenger user = findUserById(id);

        if (Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalStateException("El usuario ya está activo");
        }

        user.setActive(true);
        user = passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.REACTIVATE,
                "Cuenta reactivada");

        log.info("Admin {} reactivó usuario {}", adminEmail, user.getEmail());
        return toAdminUserDTO(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Eliminación
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Elimina permanentemente un usuario del sistema.
     * Garantiza que siempre quede al menos un ADMIN.
     */
    @Transactional
    public void deleteUser(String adminEmail, Long id) {
        Passenger user = findUserById(id);

        if (user.getRole() == Role.ADMIN) {
            // Para eliminación contamos todos los ADMIN (activos e inactivos)
            long totalAdmins = passengerRepository.findByRole(Role.ADMIN).stream()
                    .filter(p -> !p.getId().equals(id))
                    .count();
            if (totalAdmins == 0) {
                throw new IllegalStateException("Debe existir al menos un ADMIN en el sistema");
            }
        }

        Long adminId = getAdminId(adminEmail);
        // Registrar auditoría ANTES de eliminar para preservar el email
        recordAudit(adminId, null, user.getEmail(), AuditAction.DELETE,
                "Usuario eliminado permanentemente (id=" + id + ", rol=" + user.getRole().name() + ")");

        passengerRepository.delete(user);
        log.info("Admin {} eliminó usuario {} (id={})", adminEmail, user.getEmail(), id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Restablecimiento de contraseña
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Restablece la contraseña de un usuario.
     * Nunca registra el valor de la contraseña en auditoría.
     */
    @Transactional
    public void resetPassword(String adminEmail, Long id, AdminResetPasswordRequest request) {
        Passenger user = findUserById(id);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        passengerRepository.save(user);

        Long adminId = getAdminId(adminEmail);
        recordAudit(adminId, user.getId(), user.getEmail(), AuditAction.UPDATE,
                "Campos actualizados: password");

        log.info("Admin {} restableció contraseña de usuario {}", adminEmail, user.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auditoría
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retorna los registros de auditoría con filtros opcionales.
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditLogs(Long targetId, AuditAction action) {
        List<AuditLog> logs;

        if (targetId != null && action != null) {
            logs = auditLogRepository.findByTargetIdAndActionOrderByCreatedAtDesc(targetId, action);
        } else if (targetId != null) {
            logs = auditLogRepository.findByTargetIdOrderByCreatedAtDesc(targetId);
        } else if (action != null) {
            logs = auditLogRepository.findByActionOrderByCreatedAtDesc(action);
        } else {
            logs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        }

        return logs.stream().map(this::toAuditLogDTO).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private Passenger findUserById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id));
    }

    private Long getAdminId(String adminEmail) {
        return passengerRepository.findByEmail(adminEmail)
                .map(Passenger::getId)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + adminEmail));
    }

    /**
     * Verifica que al excluir el usuario dado, aún quede al menos un ADMIN activo.
     */
    private void assertAtLeastOneAdminRemains(Long excludeId) {
        long activeAdmins = passengerRepository.findByRoleAndActive(Role.ADMIN, true).stream()
                .filter(p -> !p.getId().equals(excludeId))
                .count();
        // También contar admins con active=null (registros viejos) como activos
        long nullActiveAdmins = passengerRepository.findByRole(Role.ADMIN).stream()
                .filter(p -> p.getActive() == null && !p.getId().equals(excludeId))
                .count();
        if (activeAdmins + nullActiveAdmins == 0) {
            throw new IllegalStateException(MUST_HAVE_ONE_ADMIN);
        }
    }

    private void recordAudit(Long adminId, Long targetId, String targetEmail,
                              AuditAction action, String description) {
        AuditLog log = AuditLog.builder()
                .adminId(adminId)
                .targetId(targetId)
                .targetEmail(targetEmail)
                .action(action)
                .description(description)
                .build();
        auditLogRepository.save(log);
    }

    private AdminUserDTO toAdminUserDTO(Passenger p) {
        return AdminUserDTO.builder()
                .id(p.getId())
                .documentId(p.getDocumentId())
                .documentType(p.getDocumentType())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .role(p.getRole())
                .active(p.getActive() != null ? p.getActive() : true)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private AuditLogDTO toAuditLogDTO(AuditLog a) {
        return AuditLogDTO.builder()
                .id(a.getId())
                .adminId(a.getAdminId())
                .targetId(a.getTargetId())
                .targetEmail(a.getTargetEmail())
                .action(a.getAction())
                .description(a.getDescription())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
