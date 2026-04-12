package co.aerosmart.config;

import co.aerosmart.model.Passenger;
import co.aerosmart.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!passengerRepository.existsByEmail("admin@aerosmart.co")) {
            Passenger admin = new Passenger();
            admin.setDocumentType("CC");
            admin.setDocumentId("0000000001");
            admin.setFirstName("Admin");
            admin.setLastName("AeroPuerto");
            admin.setEmail("admin@aerosmart.co");
            admin.setPhone("+573001234567");
            admin.setBirthDate(LocalDate.of(1990, 1, 1));
            admin.setPassword(passwordEncoder.encode("Admin@2024!"));
            admin.setAcceptedDataPolicy(true);
            admin.setRole("ADMIN");
            passengerRepository.save(admin);
            log.info("✅ Admin creado: admin@aerosmart.co / Admin@2024!");
        }
    }
}
