package co.aerosmart.services;

import co.aerosmart.dto.NotificationDTO;
import co.aerosmart.mappers.NotificationMapper;
import co.aerosmart.model.*;
import co.aerosmart.repository.NotificationRepository;
import co.aerosmart.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de notificaciones.
 * Envía notificaciones en tiempo real a pasajeros sobre cambios en vuelos.
 * Previene notificaciones duplicadas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationMapper notificationMapper;

    /**
     * Notifica a todos los pasajeros de un vuelo sobre un cambio de hora.
     */
    @Transactional
    public void notifyFlightTimeChange(Flight flight, LocalDateTime oldTime, LocalDateTime newTime) {
        List<Reservation> reservations = reservationRepository.findByFlightId(flight.getId());
        
        for (Reservation reservation : reservations) {
            if (reservation.allowsFlightAccess()) {
                String message = String.format(
                    "El vuelo %s ha cambiado su hora de salida de %s a %s",
                    flight.getFlightCode(),
                    oldTime.toString(),
                    newTime.toString()
                );
                
                createNotification(reservation.getPassenger(), flight, NotificationType.FLIGHT_TIME_CHANGE, message);
            }
        }
        
        log.info("Notificaciones de cambio de hora enviadas para vuelo {}", flight.getFlightCode());
    }

    /**
     * Notifica a todos los pasajeros de un vuelo sobre un cambio de puerta.
     */
    @Transactional
    public void notifyGateChange(Flight flight, String oldGate, String newGate) {
        List<Reservation> reservations = reservationRepository.findByFlightId(flight.getId());
        
        for (Reservation reservation : reservations) {
            if (reservation.allowsFlightAccess()) {
                String message = String.format(
                    "El vuelo %s ha cambiado de puerta: de %s a %s",
                    flight.getFlightCode(),
                    oldGate != null ? oldGate : "sin asignar",
                    newGate
                );
                
                createNotification(reservation.getPassenger(), flight, NotificationType.GATE_CHANGE, message);
            }
        }
        
        log.info("Notificaciones de cambio de puerta enviadas para vuelo {}", flight.getFlightCode());
    }

    /**
     * Notifica a todos los pasajeros de un vuelo sobre un retraso.
     */
    @Transactional
    public void notifyFlightDelay(Flight flight) {
        List<Reservation> reservations = reservationRepository.findByFlightId(flight.getId());
        
        for (Reservation reservation : reservations) {
            if (reservation.allowsFlightAccess()) {
                String message = String.format(
                    "El vuelo %s está retrasado. Nueva hora estimada: %s",
                    flight.getFlightCode(),
                    flight.getEstimatedDepartureTime() != null 
                        ? flight.getEstimatedDepartureTime().toString() 
                        : "por confirmar"
                );
                
                createNotification(reservation.getPassenger(), flight, NotificationType.FLIGHT_DELAY, message);
            }
        }
        
        log.info("Notificaciones de retraso enviadas para vuelo {}", flight.getFlightCode());
    }

    /**
     * Notifica a todos los pasajeros de un vuelo sobre una cancelación.
     */
    @Transactional
    public void notifyFlightCancellation(Flight flight) {
        List<Reservation> reservations = reservationRepository.findByFlightId(flight.getId());
        
        for (Reservation reservation : reservations) {
            if (reservation.allowsFlightAccess()) {
                String message = String.format(
                    "El vuelo %s ha sido cancelado. Por favor contacte con atención al cliente.",
                    flight.getFlightCode()
                );
                
                createNotification(reservation.getPassenger(), flight, NotificationType.FLIGHT_CANCELLATION, message);
            }
        }
        
        log.info("Notificaciones de cancelación enviadas para vuelo {}", flight.getFlightCode());
    }

    /**
     * Notifica a todos los pasajeros que el abordaje ha iniciado.
     */
    @Transactional
    public void notifyBoardingStarted(Flight flight) {
        List<Reservation> reservations = reservationRepository.findByFlightId(flight.getId());
        
        for (Reservation reservation : reservations) {
            if (reservation.allowsFlightAccess()) {
                String message = String.format(
                    "El abordaje del vuelo %s ha iniciado. Puerta: %s",
                    flight.getFlightCode(),
                    flight.getGate() != null ? flight.getGate() : "por confirmar"
                );
                
                createNotification(reservation.getPassenger(), flight, NotificationType.BOARDING_STARTED, message);
            }
        }
        
        log.info("Notificaciones de inicio de abordaje enviadas para vuelo {}", flight.getFlightCode());
    }

    /**
     * Crea una notificación si no existe una duplicada.
     */
    private void createNotification(Passenger passenger, Flight flight, NotificationType type, String message) {
        // Prevenir notificaciones duplicadas
        boolean exists = notificationRepository.existsByPassengerIdAndFlightIdAndType(
            passenger.getId(), 
            flight.getId(), 
            type
        );
        
        if (!exists) {
            Notification notification = new Notification();
            notification.setPassenger(passenger);
            notification.setFlight(flight);
            notification.setType(type);
            notification.setMessage(message);
            
            notificationRepository.save(notification);
        }
    }

    /**
     * Obtiene todas las notificaciones de un pasajero.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getPassengerNotifications(Long passengerId) {
        return notificationRepository.findByPassengerId(passengerId)
            .stream()
            .map(notificationMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones no leídas de un pasajero.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long passengerId) {
        return notificationRepository.findUnreadByPassengerId(passengerId)
            .stream()
            .map(notificationMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        
        notification.markAsRead();
        notificationRepository.save(notification);
    }
}
