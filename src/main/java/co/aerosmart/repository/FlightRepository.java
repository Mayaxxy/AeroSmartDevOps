package co.aerosmart.repository;

import co.aerosmart.model.Flight;
import co.aerosmart.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de Flight.
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    /**
     * Busca un vuelo por su código único.
     */
    Optional<Flight> findByFlightCode(String flightCode);
    
    /**
     * Busca vuelos por estado.
     */
    List<Flight> findByStatus(FlightStatus status);
    
    /**
     * Busca vuelos por aeropuerto de origen.
     */
    List<Flight> findByOriginAirport(String originAirport);
    
    /**
     * Busca vuelos por aeropuerto de destino.
     */
    List<Flight> findByDestinationAirport(String destinationAirport);
    
    /**
     * Busca vuelos con salida en un rango de fechas.
     */
    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :start AND :end")
    List<Flight> findFlightsByDepartureBetween(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    /**
     * Busca vuelos activos (no cancelados) con salida próxima.
     */
    @Query("SELECT f FROM Flight f WHERE f.status != 'CANCELLED' AND f.departureTime > :now ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlights(@Param("now") LocalDateTime now);
}
