package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva de un pasajero en un vuelo.
 * Un pasajero solo puede ver información de vuelos asociados a su reserva.
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

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

    @Column(unique = true, nullable = false)
    private String reservationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    private String seatNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private CheckIn checkIn;

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
     * Verifica si la reserva permite acceso a información operativa del vuelo.
     * Reservas canceladas no pueden acceder.
     */
    public boolean allowsFlightAccess() {
        return status == ReservationStatus.ACTIVE;
    }

    /**
     * Verifica si la reserva está dentro del período de check-in (24-48h antes del vuelo).
     */
    public boolean isCheckInWindowOpen() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInStart = flight.getDepartureTime().minusHours(48);
        LocalDateTime checkInEnd = flight.getDepartureTime().minusHours(2);
        return now.isAfter(checkInStart) && now.isBefore(checkInEnd);
    }
}
