package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un vuelo en el sistema FlyTrack.
 * Cada vuelo tiene código único, origen, destino, estado y puerta de embarque.
 */
@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de vuelo es obligatorio")
    @Column(unique = true, nullable = false)
    private String flightCode;

    @NotBlank(message = "El aeropuerto de origen es obligatorio")
    private String originAirport;

    @NotBlank(message = "El aeropuerto de destino es obligatorio")
    private String destinationAirport;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "estimated_departure_time")
    private LocalDateTime estimatedDepartureTime;

    @NotNull(message = "La fecha de llegada es obligatoria")
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status = FlightStatus.SCHEDULED;

    private String gate;

    @Column(name = "boarding_start_time")
    private LocalDateTime boardingStartTime;

    @Column(name = "boarding_close_time")
    private LocalDateTime boardingCloseTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Boarding inicia 45 min antes del vuelo
        boardingStartTime = departureTime.minusMinutes(45);
        // Boarding cierra 15 min antes del vuelo
        boardingCloseTime = departureTime.minusMinutes(15);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el vuelo permite check-in.
     * No se permite check-in si el vuelo está cancelado.
     */
    public boolean allowsCheckIn() {
        return status != FlightStatus.CANCELLED;
    }

    /**
     * Verifica si el vuelo permite generación de QR.
     * No se permite QR si el vuelo está cancelado.
     */
    public boolean allowsQRGeneration() {
        return status != FlightStatus.CANCELLED;
    }

    /**
     * Verifica si el boarding está abierto.
     */
    public boolean isBoardingOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(boardingStartTime) && now.isBefore(boardingCloseTime);
    }
}
