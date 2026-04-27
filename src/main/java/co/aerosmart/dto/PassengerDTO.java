package co.aerosmart.dto;

import co.aerosmart.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Passenger.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {
    private Long id;
    private String documentId;
    private String documentType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private java.time.LocalDateTime birthDate;
    private String email;
    private String phone;
    private Role role;
}
