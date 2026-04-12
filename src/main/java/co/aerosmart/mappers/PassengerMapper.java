package co.aerosmart.mappers;

import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public PassengerDTO toDTO(Passenger passenger) {
        if (passenger == null) return null;

        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setDocumentId(passenger.getDocumentId());
        dto.setDocumentType(passenger.getDocumentType());
        dto.setFirstName(passenger.getFirstName());
        dto.setMiddleName(passenger.getMiddleName());
        dto.setLastName(passenger.getLastName());
        dto.setSecondLastName(passenger.getSecondLastName());
        dto.setBirthDate(passenger.getBirthDate());
        dto.setEmail(passenger.getEmail());
        dto.setPhone(passenger.getPhone());
        dto.setRole(passenger.getRole());
        return dto;
    }
}
