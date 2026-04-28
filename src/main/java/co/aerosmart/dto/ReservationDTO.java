package co.aerosmart.dto;

import co.aerosmart.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private String reservationCode;
    private ReservationStatus status;
    private String seatNumber;
    private LocalDateTime createdAt;
    private FlightDTO flight;
    private Boolean hasCheckIn;
    private Long checkInId;
}
