package co.aerosmart.repository;

import co.aerosmart.model.Passenger;
import co.aerosmart.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de Passenger.
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    /** Busca un pasajero por su documento de identidad. */
    Optional<Passenger> findByDocumentId(String documentId);

    /** Busca un pasajero por su email. */
    Optional<Passenger> findByEmail(String email);

    /** Verifica si existe un pasajero con el documento dado. */
    boolean existsByDocumentId(String documentId);

    /** Verifica si existe un pasajero con el email dado. */
    boolean existsByEmail(String email);

    /** Lista todos los pasajeros con un rol específico. */
    List<Passenger> findByRole(Role role);

    /** Lista todos los pasajeros con un estado activo/inactivo. */
    List<Passenger> findByActive(Boolean active);

    /** Lista todos los pasajeros con un rol y estado específicos. */
    List<Passenger> findByRoleAndActive(Role role, Boolean active);

    /** Cuenta los usuarios activos con un rol específico. */
    long countByRoleAndActive(Role role, Boolean active);

    /** Verifica si existe otro pasajero con el mismo documentId excluyendo un ID. */
    boolean existsByDocumentIdAndIdNot(String documentId, Long id);
}
