package co.aerosmart.repository;

import co.aerosmart.model.Airplane;
import co.aerosmart.model.AirplaneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos con aviones.
 */
@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    
    Optional<Airplane> findByRegistration(String registration);
    
    boolean existsByRegistration(String registration);
    
    List<Airplane> findByStatus(AirplaneStatus status);
    
    List<Airplane> findByStatusOrderByRegistrationAsc(AirplaneStatus status);
}
