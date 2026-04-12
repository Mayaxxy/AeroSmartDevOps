package co.aerosmart.repository;

import co.aerosmart.model.Reservation;
import co.aerosmart.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de Reservation.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Busca una reserva por su código único.
     */
    Optional<Reservation> findByReservationCode(String reservationCode);
    
    /**
     * Busca todas las reservas de un pasajero.
     */
    @Query("SELECT r FROM Reservation r WHERE r.passenger.id = :passengerId")
    List<Reservation> findByPassengerId(@Param("passengerId") Long passengerId);
    
    /**
     * Busca todas las reservas activas de un pasajero.
     */
    @Query("SELECT r FROM Reservation r WHERE r.passenger.id = :passengerId AND r.status = :status")
    List<Reservation> findByPassengerIdAndStatus(
        @Param("passengerId") Long passengerId, 
        @Param("status") ReservationStatus status
    );
    
    /**
     * Busca todas las reservas de un vuelo.
     */
    @Query("SELECT r FROM Reservation r WHERE r.flight.id = :flightId")
    List<Reservation> findByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Verifica si existe una reserva activa para un pasajero en un vuelo específico.
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE r.passenger.id = :passengerId AND r.flight.id = :flightId AND r.status = 'ACTIVE'")
    boolean existsActiveReservation(
        @Param("passengerId") Long passengerId, 
        @Param("flightId") Long flightId
    );
}
