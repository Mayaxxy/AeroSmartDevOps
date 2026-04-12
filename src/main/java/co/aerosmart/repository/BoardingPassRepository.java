package co.aerosmart.repository;

import co.aerosmart.model.BoardingPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos de BoardingPass.
 */
@Repository
public interface BoardingPassRepository extends JpaRepository<BoardingPass, Long> {
    
    /**
     * Busca un pase de abordaje por su token único.
     */
    Optional<BoardingPass> findByBoardingToken(String boardingToken);
    
    /**
     * Busca un pase de abordaje por ID de check-in.
     */
    @Query("SELECT bp FROM BoardingPass bp WHERE bp.checkIn.id = :checkInId")
    Optional<BoardingPass> findByCheckInId(@Param("checkInId") Long checkInId);
    
    /**
     * Verifica si existe un pase de abordaje para un check-in.
     */
    @Query("SELECT CASE WHEN COUNT(bp) > 0 THEN true ELSE false END FROM BoardingPass bp " +
           "WHERE bp.checkIn.id = :checkInId")
    boolean existsByCheckInId(@Param("checkInId") Long checkInId);
}
