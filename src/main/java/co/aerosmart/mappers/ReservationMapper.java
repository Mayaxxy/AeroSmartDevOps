package co.aerosmart.mappers;

import co.aerosmart.dto.ReservationDTO;
import co.aerosmart.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Reservation y ReservationDTO.
 */
@Component
@RequiredArgsConstructor
public class ReservationMapper {
    
    private final FlightMapper flightMapper;
    
    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationCode(reservation.getReservationCode());
        dto.setStatus(reservation.getStatus());
        dto.setSeatNumber(reservation.getSeatNumber());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setFlight(flightMapper.toDTO(reservation.getFlight()));
        dto.setHasCheckIn(reservation.getCheckIn() != null);
        if (reservation.getCheckIn() != null) {
            dto.setCheckInId(reservation.getCheckIn().getId());
        }
        
        return dto;
    }
}
