package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa el check-in de un pasajero.
 * El check-in es obligatorio para generar el QR de abordaje.
 * Un pasajero no puede tener dos check-in activos para el mismo vuelo.
 */
@Entity
@Table(name = "check_ins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckInStatus status = CheckInStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "checkIn", cascade = CascadeType.ALL)
    private BoardingPass boardingPass;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        checkInTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el check-in puede ser modificado.
     * No se puede modificar después del cierre de abordaje.
     */
    public boolean canBeModified() {
        LocalDateTime boardingCloseTime = reservation.getFlight().getBoardingCloseTime();
        return LocalDateTime.now().isBefore(boardingCloseTime);
    }

    /**
     * Verifica si el check-in permite generación de QR.
     */
    public boolean allowsQRGeneration() {
        return status == CheckInStatus.ACTIVE;
    }
}
