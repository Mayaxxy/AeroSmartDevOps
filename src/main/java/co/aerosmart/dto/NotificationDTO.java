package co.aerosmart.dto;

import co.aerosmart.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Notification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;
    private FlightDTO flight;
}
