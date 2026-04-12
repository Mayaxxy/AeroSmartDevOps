package co.aerosmart.repository;

import co.aerosmart.model.BaggageReport;
import co.aerosmart.model.BaggageReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar operaciones de base de datos de BaggageReport.
 */
@Repository
public interface BaggageReportRepository extends JpaRepository<BaggageReport, Long> {
    
    /**
     * Busca todos los reportes de un pasajero.
     */
    @Query("SELECT br FROM BaggageReport br WHERE br.passenger.id = :passengerId")
    List<BaggageReport> findByPassengerId(@Param("passengerId") Long passengerId);
    
    /**
     * Busca todos los reportes de un vuelo.
     */
    @Query("SELECT br FROM BaggageReport br WHERE br.flight.id = :flightId")
    List<BaggageReport> findByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Busca reportes por estado.
     */
    List<BaggageReport> findByStatus(BaggageReportStatus status);
    
    /**
     * Cuenta reportes activos (no resueltos) de un pasajero.
     */
    @Query("SELECT COUNT(br) FROM BaggageReport br " +
           "WHERE br.passenger.id = :passengerId " +
           "AND br.status IN ('PENDING', 'IN_PROGRESS')")
    long countActiveReportsByPassengerId(@Param("passengerId") Long passengerId);
}
