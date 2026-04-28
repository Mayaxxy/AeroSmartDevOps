package co.aerosmart.config;

import co.aerosmart.model.Passenger;
import co.aerosmart.model.Role;
import co.aerosmart.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Quitar NOT NULL de birth_date para compatibilidad con usuarios sin fecha de nacimiento
        try {
            jdbcTemplate.execute("ALTER TABLE passengers ALTER COLUMN birth_date DROP NOT NULL");
            log.info("Migración: birth_date ahora es nullable");
        } catch (Exception e) {
            log.debug("birth_date ya es nullable: {}", e.getMessage());
        }

        // Migrar rol 'USER' a 'PASSENGER' (datos legacy)
        int migrated = jdbcTemplate.update("UPDATE passengers SET role = 'PASSENGER' WHERE role = 'USER'");
        if (migrated > 0) {
            log.info("Migración: {} usuario(s) con rol USER migrados a PASSENGER", migrated);
        }

        // Rellenar active=true para registros existentes con null (migración segura)
        int updated = jdbcTemplate.update("UPDATE passengers SET active = true WHERE active IS NULL");
        if (updated > 0) {
            log.info("Migración: {} usuario(s) actualizado(s) con active=true", updated);
        }
        initializeAdminUser();
        initializeReceptionistUser();
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
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            passengerRepository.save(admin);
            log.info("Usuario administrador creado: {}", adminEmail);
            log.info("Contraseña por defecto: Admin1234!");
        } else {
            log.info("Usuario administrador ya existe: {}", adminEmail);
        }
    }

    private void initializeReceptionistUser() {
        String receptionistEmail = "receptionist@gmail.com";

        if (!passengerRepository.existsByEmail(receptionistEmail)) {
            Passenger receptionist = new Passenger();
            receptionist.setEmail(receptionistEmail);
            receptionist.setPassword(passwordEncoder.encode("Receptionist1234!"));
            receptionist.setFirstName("Receptionist");
            receptionist.setLastName("AeroSmart");
            receptionist.setDocumentId("RECEP001");
            receptionist.setPhone("+57 300 000 0001");
            receptionist.setAcceptedDataPolicy(true);
            receptionist.setBirthDate(LocalDateTime.of(1992, 1, 1, 0, 0));
            receptionist.setDocumentType("CC");
            receptionist.setRole(Role.RECEPCIONISTA);
            receptionist.setActive(true);

            passengerRepository.save(receptionist);
            log.info("Usuario recepcionista creado: {}", receptionistEmail);
            log.info("Contraseña por defecto: Receptionist1234!");
        } else {
            log.info("Usuario recepcionista ya existe: {}", receptionistEmail);
        }
    }
}
