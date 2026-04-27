package co.aerosmart.model;

/**
 * Estados posibles de un avión en el sistema.
 */
public enum AirplaneStatus {
    AVAILABLE,      // Disponible para asignar a vuelos
    IN_FLIGHT,      // En vuelo actualmente
    MAINTENANCE,    // En mantenimiento
    OUT_OF_SERVICE  // Fuera de servicio
}
