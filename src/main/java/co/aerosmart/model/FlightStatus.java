package co.aerosmart.model;

/**
 * Estados posibles de un vuelo en el sistema FlyTrack.
 * Un vuelo no puede estar en dos estados incompatibles simultáneamente.
 */
public enum FlightStatus {
    SCHEDULED,    // Programado
    DELAYED,      // Retrasado
    BOARDING,     // Abordando
    DEPARTED,     // Despegado
    ARRIVED,      // Llegado
    CANCELLED     // Cancelado
}
