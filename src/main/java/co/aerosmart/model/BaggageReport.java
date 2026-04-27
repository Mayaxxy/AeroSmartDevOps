package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un reporte de equipaje.
 * Un pasajero solo puede reportar inconvenientes si tiene un vuelo registrado.
 * El estado sigue el flujo: pendiente → en proceso → resuelto.
 */
@Entity
@Table(name = "baggage_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaggageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "receptionist_id")
    private Long receptionistId;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaggageReportStatus status = BaggageReportStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Cambia el estado del reporte siguiendo el flujo permitido.
     * No se puede saltar estados.
     */
    public void changeStatus(BaggageReportStatus newStatus) {
        if (this.status == BaggageReportStatus.PENDING && newStatus == BaggageReportStatus.IN_PROGRESS) {
            this.status = newStatus;
            this.reviewedAt = LocalDateTime.now();
        } else if (this.status == BaggageReportStatus.IN_PROGRESS && newStatus == BaggageReportStatus.RESOLVED) {
            this.status = newStatus;
            this.resolvedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Transición de estado no permitida: " + this.status + " -> " + newStatus);
        }
    }
}
