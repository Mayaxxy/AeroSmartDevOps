package co.aerosmart.mappers;

import co.aerosmart.dto.FlightDTO;
import co.aerosmart.model.Flight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Flight y FlightDTO.
 */
@Component
@RequiredArgsConstructor
public class FlightMapper {
    
    private final AirplaneMapper airplaneMapper;
    
    public FlightDTO toDTO(Flight flight) {
        if (flight == null) {
            return null;
        }
        
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightCode(flight.getFlightCode());
        dto.setOriginAirport(flight.getOriginAirport());
        dto.setDestinationAirport(flight.getDestinationAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setEstimatedDepartureTime(flight.getEstimatedDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setStatus(flight.getStatus());
        dto.setGate(flight.getGate());
        dto.setBoardingStartTime(flight.getBoardingStartTime());
        dto.setBoardingCloseTime(flight.getBoardingCloseTime());
        dto.setAirplane(airplaneMapper.toDTO(flight.getAirplane()));
        
        return dto;
    }
}
