package co.aerosmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de BoardingPass.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPassDTO {
    private Long id;
    private Long checkInId;
    private String boardingToken;
    private String qrCodeBase64;
    private LocalDateTime generatedAt;
    private LocalDateTime validUntil;
    private boolean used;
    private FlightDTO flight;
    private String seatNumber;
    private String passengerName;
}
