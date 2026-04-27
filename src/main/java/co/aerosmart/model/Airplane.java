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
 * Entidad que representa un avión en el sistema.
 * Cada avión tiene una matrícula única, modelo y capacidad.
 */
@Entity
@Table(name = "airplanes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airplane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La matrícula es obligatoria")
    @Column(unique = true, nullable = false)
    private String registration; // Matrícula (ej: N12345, HK-5050)

    @NotBlank(message = "El modelo es obligatorio")
    private String model; // Modelo (ej: Boeing 737-800, Airbus A320)

    @NotBlank(message = "El fabricante es obligatorio")
    private String manufacturer; // Fabricante (ej: Boeing, Airbus)

    @NotNull(message = "La capacidad es obligatoria")
    private Integer capacity; // Capacidad total de pasajeros

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AirplaneStatus status = AirplaneStatus.AVAILABLE;

    @Column(name = "year_manufactured")
    private Integer yearManufactured;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "airplane")
    private List<Flight> flights = new ArrayList<>();

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
     * Verifica si el avión está disponible para asignar a un vuelo.
     */
    public boolean isAvailable() {
        return status == AirplaneStatus.AVAILABLE;
    }
}
