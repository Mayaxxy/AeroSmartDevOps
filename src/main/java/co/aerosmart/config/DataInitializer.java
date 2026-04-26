package co.aerosmart.config;

import co.aerosmart.model.Passenger;
import co.aerosmart.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inicializa datos por defecto en la base de datos.
 * Crea un usuario administrador si no existe.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@gmail.com";
        
        if (!passengerRepository.existsByEmail(adminEmail)) {
            Passenger admin = new Passenger();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin1234!"));
            admin.setFirstName("Admin");
            admin.setLastName("AeroSmart");
            admin.setDocumentId("ADMIN001");
            admin.setPhone("+57 300 000 0000");
            admin.setAcceptedDataPolicy(true);
            admin.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));
            admin.setDocumentType("CC");
            admin.setRole("ADMIN");
            
            passengerRepository.save(admin);
            log.info("Usuario administrador creado: {}", adminEmail);
            log.info("Contraseña por defecto: Admin1234!");
        } else {
            log.info("Usuario administrador ya existe: {}", adminEmail);
        }
    }
}
