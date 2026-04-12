package co.aerosmart.model;

/**
 * Tipos de notificaciones que se pueden enviar a pasajeros.
 */
public enum NotificationType {
    FLIGHT_TIME_CHANGE,    // Cambio de hora del vuelo
    GATE_CHANGE,           // Cambio de puerta de embarque
    FLIGHT_DELAY,          // Vuelo retrasado
    FLIGHT_CANCELLATION,   // Vuelo cancelado
    BOARDING_STARTED,      // Abordaje iniciado
    BOARDING_CLOSING       // Abordaje por cerrar
}
