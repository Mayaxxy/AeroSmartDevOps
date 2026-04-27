package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa el pase de abordaje con código QR.
 * El QR se genera solo cuando el check-in está habilitado (24h-2h antes del vuelo).
 * El QR es dinámico y tiene validez limitada hasta el cierre de abordaje.
 * Contiene un token seguro (no datos personales) y es de uso único.
 */
@Entity
@Table(name = "boarding_passes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id", nullable = false, unique = true)
    private CheckIn checkIn;

    @Column(name = "receptionist_id")
    private Long receptionistId;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String boardingToken;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "last_regenerated_at")
    private LocalDateTime lastRegeneratedAt;

    private boolean used = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        generatedAt = LocalDateTime.now();
        lastRegeneratedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el QR es válido para uso.
     * Debe estar dentro del tiempo válido, no usado, y el vuelo no cancelado.
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return !used 
            && now.isBefore(validUntil) 
            && checkIn.getReservation().getFlight().allowsQRGeneration();
    }

    /**
     * Marca el QR como usado.
     */
    public void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el QR necesita regeneración (cada 60 segundos).
     */
    public boolean needsRegeneration() {
        return LocalDateTime.now().isAfter(lastRegeneratedAt.plusSeconds(60));
    }
}
