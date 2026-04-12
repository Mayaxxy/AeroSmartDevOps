package co.aerosmart.controllers;

import co.aerosmart.dto.NotificationDTO;
import co.aerosmart.model.Passenger;
import co.aerosmart.services.NotificationService;
import co.aerosmart.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de notificaciones.
 * Requiere autenticación.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final PassengerService passengerService;

    /**
     * Obtiene todas las notificaciones del pasajero autenticado.
     * GET /api/notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(Authentication authentication) {
        String passengerEmail = authentication.getName();
        Passenger passenger = passengerService.findByEmail(passengerEmail);
        List<NotificationDTO> notifications = notificationService.getPassengerNotifications(passenger.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones no leídas del pasajero autenticado.
     * GET /api/notifications/unread
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(Authentication authentication) {
        String passengerEmail = authentication.getName();
        Passenger passenger = passengerService.findByEmail(passengerEmail);
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(passenger.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marca una notificación como leída.
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}
