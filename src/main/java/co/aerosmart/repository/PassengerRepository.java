package co.aerosmart.repository;

import co.aerosmart.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de Passenger.
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    /**
     * Busca un pasajero por su documento de identidad.
     */
    Optional<Passenger> findByDocumentId(String documentId);
    
    /**
     * Busca un pasajero por su email.
     */
    Optional<Passenger> findByEmail(String email);
    
    /**
     * Verifica si existe un pasajero con el documento dado.
     */
    boolean existsByDocumentId(String documentId);
    
    /**
     * Verifica si existe un pasajero con el email dado.
     */
    boolean existsByEmail(String email);
}
