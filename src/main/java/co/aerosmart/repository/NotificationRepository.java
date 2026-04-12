package co.aerosmart.repository;

import co.aerosmart.model.Notification;
import co.aerosmart.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar operaciones de base de datos de Notification.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Busca todas las notificaciones de un pasajero.
     */
    @Query("SELECT n FROM Notification n WHERE n.passenger.id = :passengerId ORDER BY n.sentAt DESC")
    List<Notification> findByPassengerId(@Param("passengerId") Long passengerId);
    
    /**
     * Busca notificaciones no leídas de un pasajero.
     */
    @Query("SELECT n FROM Notification n WHERE n.passenger.id = :passengerId AND n.read = false ORDER BY n.sentAt DESC")
    List<Notification> findUnreadByPassengerId(@Param("passengerId") Long passengerId);
    
    /**
     * Busca todas las notificaciones de un vuelo.
     */
    @Query("SELECT n FROM Notification n WHERE n.flight.id = :flightId ORDER BY n.sentAt DESC")
    List<Notification> findByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Busca notificaciones por tipo.
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Verifica si ya existe una notificación del mismo tipo para un pasajero y vuelo.
     * Esto previene notificaciones duplicadas.
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n " +
           "WHERE n.passenger.id = :passengerId " +
           "AND n.flight.id = :flightId " +
           "AND n.type = :type")
    boolean existsByPassengerIdAndFlightIdAndType(
        @Param("passengerId") Long passengerId,
        @Param("flightId") Long flightId,
        @Param("type") NotificationType type
    );
}
