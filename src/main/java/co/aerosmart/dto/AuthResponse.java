package co.aerosmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación con JWT token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private PassengerDTO passenger;
    
    public AuthResponse(String token, PassengerDTO passenger) {
        this.token = token;
        this.passenger = passenger;
    }
}
