package co.aerosmart.mappers;

import co.aerosmart.dto.BoardingPassDTO;
import co.aerosmart.model.BoardingPass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre BoardingPass y BoardingPassDTO.
 */
@Component
@RequiredArgsConstructor
public class BoardingPassMapper {
    
    private final FlightMapper flightMapper;
    
    public BoardingPassDTO toDTO(BoardingPass boardingPass, String qrCodeBase64) {
        if (boardingPass == null) {
            return null;
        }
        
        BoardingPassDTO dto = new BoardingPassDTO();
        dto.setId(boardingPass.getId());
        dto.setCheckInId(boardingPass.getCheckIn().getId());
        dto.setBoardingToken(boardingPass.getBoardingToken());
        dto.setQrCodeBase64(qrCodeBase64);
        dto.setGeneratedAt(boardingPass.getGeneratedAt());
        dto.setValidUntil(boardingPass.getValidUntil());
        dto.setUsed(boardingPass.isUsed());
        dto.setFlight(flightMapper.toDTO(boardingPass.getCheckIn().getReservation().getFlight()));
        dto.setSeatNumber(boardingPass.getCheckIn().getReservation().getSeatNumber());
        
        // Nombre del pasajero para mostrar en el pase
        var passenger = boardingPass.getCheckIn().getReservation().getPassenger();
        dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
        
        return dto;
    }
}
