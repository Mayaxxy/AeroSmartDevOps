package co.aerosmart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String documentType; // CC, PASSPORT, CE

    @NotBlank
    @Column(unique = true, nullable = false, length = 20)
    private String documentId;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String middleName;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(length = 50)
    private String secondLastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 15)
    private String phone;

    @NotBlank
    private String password;

    @Column(name = "accepted_data_policy", nullable = false)
    private boolean acceptedDataPolicy;

    @Column(nullable = false, length = 20)
    private String role = "PASSENGER"; // PASSENGER, ADMIN

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private List<BaggageReport> baggageReports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
