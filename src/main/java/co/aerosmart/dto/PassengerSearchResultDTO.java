package co.aerosmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resultado de búsqueda de pasajeros por recepcionistas.
 * Incluye información completa del pasajero, sus reservas y vuelos asociados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerSearchResultDTO {
    private PassengerDTO passenger;
    private List<ReservationDTO> reservations;
}
