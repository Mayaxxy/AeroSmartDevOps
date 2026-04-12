package co.aerosmart.mappers;

import co.aerosmart.dto.NotificationDTO;
import co.aerosmart.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Notification y NotificationDTO.
 */
@Component
@RequiredArgsConstructor
public class NotificationMapper {
    
    private final FlightMapper flightMapper;
    
    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setSentAt(notification.getSentAt());
        dto.setRead(notification.isRead());
        dto.setFlight(flightMapper.toDTO(notification.getFlight()));
        
        return dto;
    }
}
