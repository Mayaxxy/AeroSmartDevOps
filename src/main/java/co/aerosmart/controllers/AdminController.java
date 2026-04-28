package co.aerosmart.controllers;

import co.aerosmart.dto.*;
import co.aerosmart.model.AuditAction;
import co.aerosmart.model.Role;
import co.aerosmart.services.AdminService;
import co.aerosmart.services.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el módulo ADMIN de FlyTrack.
 * Todos los endpoints requieren rol ADMIN y JWT válido.
 *
 * Endpoints de gestión de usuarios:
 *   POST   /api/admin/users                        — Crear usuario
 *   GET    /api/admin/users                        — Listar usuarios (filtros: role, active)
 *   GET    /api/admin/users/{id}                   — Obtener usuario por ID
 *   PUT    /api/admin/users/{id}                   — Editar usuario
 *   PATCH  /api/admin/users/{id}/role              — Cambiar rol
 *   PATCH  /api/admin/users/{id}/deactivate        — Desactivar usuario
 *   PATCH  /api/admin/users/{id}/reactivate        — Reactivar usuario
 *   DELETE /api/admin/users/{id}                   — Eliminar usuario
 *   PATCH  /api/admin/users/{id}/reset-password    — Restablecer contraseña
 *
 * Endpoints de auditoría:
 *   GET    /api/admin/audit-logs                   — Listar auditoría (filtros: targetId, action)
 *
 * Endpoints de vuelos (existentes):
 *   GET    /api/admin/flights                      — Listar vuelos
 *   POST   /api/admin/flights                      — Crear vuelo
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final FlightService flightService;

    // ─────────────────────────────────────────────────────────────────────────
    // Gestión de usuarios
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo usuario con cualquier rol del sistema.
     * POST /api/admin/users
     */
    @PostMapping("/users")
    public ResponseEntity<AdminUserDTO> createUser(
            Authentication auth,
            @Valid @RequestBody AdminCreateUserRequest request) {
        AdminUserDTO created = adminService.createUser(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Lista todos los usuarios con filtros opcionales.
     * GET /api/admin/users?role=ADMIN&active=true
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(adminService.getAllUsers(role, active));
    }

    /**
     * Obtiene un usuario por su ID.
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Edita los datos permitidos de un usuario (no el email).
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<AdminUserDTO> updateUser(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(auth.getName(), id, request));
    }

    /**
     * Cambia el rol de un usuario.
     * PATCH /api/admin/users/{id}/role
     */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<AdminUserDTO> changeRole(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody AdminChangeRoleRequest request) {
        return ResponseEntity.ok(adminService.changeRole(auth.getName(), id, request));
    }

    /**
     * Desactiva una cuenta de usuario sin eliminarla.
     * PATCH /api/admin/users/{id}/deactivate
     */
    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            Authentication auth,
            @PathVariable Long id) {
        adminService.deactivateUser(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reactiva una cuenta de usuario previamente desactivada.
     * PATCH /api/admin/users/{id}/reactivate
     */
    @PatchMapping("/users/{id}/reactivate")
    public ResponseEntity<AdminUserDTO> reactivateUser(
            Authentication auth,
            @PathVariable Long id) {
        return ResponseEntity.ok(adminService.reactivateUser(auth.getName(), id));
    }

    /**
     * Elimina permanentemente un usuario del sistema.
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            Authentication auth,
            @PathVariable Long id) {
        adminService.deleteUser(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restablece la contraseña de un usuario.
     * PATCH /api/admin/users/{id}/reset-password
     */
    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody AdminResetPasswordRequest request) {
        adminService.resetPassword(auth.getName(), id, request);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auditoría
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Lista los registros de auditoría con filtros opcionales.
     * GET /api/admin/audit-logs?targetId=5&action=ROLE_CHANGE
     */
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogs(
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) AuditAction action) {
        return ResponseEntity.ok(adminService.getAuditLogs(targetId, action));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Vuelos (endpoints existentes preservados)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/flights")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDTO> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightDTO flight = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flight);
    }
}
