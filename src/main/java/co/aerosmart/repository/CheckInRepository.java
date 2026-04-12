package co.aerosmart.repository;

import co.aerosmart.model.CheckIn;
import co.aerosmart.model.CheckInStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de CheckIn.
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    
    /**
     * Busca un check-in por ID de reserva.
     */
    @Query("SELECT c FROM CheckIn c WHERE c.reservation.id = :reservationId")
    Optional<CheckIn> findByReservationId(@Param("reservationId") Long reservationId);
    
    /**
     * Verifica si existe un check-in activo para una reserva.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CheckIn c " +
           "WHERE c.reservation.id = :reservationId AND c.status = :status")
    boolean existsByReservationIdAndStatus(
        @Param("reservationId") Long reservationId, 
        @Param("status") CheckInStatus status
    );
    
    /**
     * Verifica si existe un check-in activo para un pasajero en un vuelo específico.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CheckIn c " +
           "WHERE c.reservation.passenger.id = :passengerId " +
           "AND c.reservation.flight.id = :flightId " +
           "AND c.status = 'ACTIVE'")
    boolean existsActiveCheckInForPassengerAndFlight(
        @Param("passengerId") Long passengerId, 
        @Param("flightId") Long flightId
    );
}
