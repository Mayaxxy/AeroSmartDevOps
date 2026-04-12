package co.aerosmart.model;

/**
 * Estados posibles de un reporte de equipaje.
 * El flujo debe ser: PENDING → IN_PROGRESS → RESOLVED
 */
public enum BaggageReportStatus {
    PENDING,      // Pendiente
    IN_PROGRESS,  // En proceso
    RESOLVED      // Resuelto
}
