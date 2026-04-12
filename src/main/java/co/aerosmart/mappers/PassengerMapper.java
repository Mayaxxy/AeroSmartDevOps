package co.aerosmart.mappers;

import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.model.Passenger;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Passenger y PassengerDTO.
 */
@Component
public class PassengerMapper {
    
    public PassengerDTO toDTO(Passenger passenger) {
        if (passenger == null) {
            return null;
        }
        
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setDocumentId(passenger.getDocumentId());
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setPhone(passenger.getPhone());
        
        return dto;
    }
}
