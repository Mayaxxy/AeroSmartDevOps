package co.aerosmart.model;

/**
 * Enum que representa los roles de usuario en el sistema FlyTrack.
 * Define los tres roles principales: ADMIN, PASSENGER y RECEPCIONISTA.
 */
public enum Role {
    /**
     * Rol de administrador con permisos completos del sistema
     */
    ADMIN,
    
    /**
     * Rol de pasajero con acceso a funcionalidades de usuario final
     */
    PASSENGER,
    
    /**
     * Rol de recepcionista con permisos para realizar check-in y gestionar reportes
     */
    RECEPCIONISTA
}
