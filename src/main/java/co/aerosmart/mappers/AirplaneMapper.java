package co.aerosmart.mappers;

import co.aerosmart.dto.AirplaneDTO;
import co.aerosmart.model.Airplane;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Airplane y AirplaneDTO.
 */
@Component
public class AirplaneMapper {

    public AirplaneDTO toDTO(Airplane airplane) {
        if (airplane == null) {
            return null;
        }
        
        AirplaneDTO dto = new AirplaneDTO();
        dto.setId(airplane.getId());
        dto.setRegistration(airplane.getRegistration());
        dto.setModel(airplane.getModel());
        dto.setManufacturer(airplane.getManufacturer());
        dto.setCapacity(airplane.getCapacity());
        dto.setStatus(airplane.getStatus());
        dto.setYearManufactured(airplane.getYearManufactured());
        
        return dto;
    }

    public Airplane toEntity(AirplaneDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Airplane airplane = new Airplane();
        airplane.setId(dto.getId());
        airplane.setRegistration(dto.getRegistration());
        airplane.setModel(dto.getModel());
        airplane.setManufacturer(dto.getManufacturer());
        airplane.setCapacity(dto.getCapacity());
        airplane.setStatus(dto.getStatus());
        airplane.setYearManufactured(dto.getYearManufactured());
        
        return airplane;
    }
}
