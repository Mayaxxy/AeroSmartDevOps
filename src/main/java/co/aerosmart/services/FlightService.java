package co.aerosmart.services;

import co.aerosmart.dto.FlightDTO;
import co.aerosmart.mappers.FlightMapper;
import co.aerosmart.model.Flight;
import co.aerosmart.model.FlightStatus;
import co.aerosmart.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de vuelos.
 * Maneja operaciones CRUD y lógica de negocio de vuelos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final NotificationService notificationService;
    private final AirplaneService airplaneService;

    /**
     * Crea un nuevo vuelo (solo admin).
     */
    @Transactional
    public FlightDTO createFlight(co.aerosmart.dto.CreateFlightRequest request) {
        if (flightRepository.findByFlightCode(request.getFlightCode()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vuelo con el código: " + request.getFlightCode());
        }

        Flight flight = new Flight();
        flight.setFlightCode(request.getFlightCode().toUpperCase().trim());
        flight.setOriginAirport(request.getOriginAirport().toUpperCase().trim());
        flight.setDestinationAirport(request.getDestinationAirport().toUpperCase().trim());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setGate(request.getGate());
        flight.setStatus(co.aerosmart.model.FlightStatus.SCHEDULED);

        // Asignar avión si se proporcionó
        if (request.getAirplaneId() != null) {
            co.aerosmart.model.Airplane airplane = airplaneService.getAirplaneById(request.getAirplaneId());
            if (!airplane.isAvailable()) {
                throw new IllegalArgumentException("El avión seleccionado no está disponible");
            }
            flight.setAirplane(airplane);
        }

        flight = flightRepository.save(flight);
        log.info("Vuelo creado: {} con avión: {}", 
            flight.getFlightCode(), 
            flight.getAirplane() != null ? flight.getAirplane().getRegistration() : "sin asignar");
        return flightMapper.toDTO(flight);
    }

    /**
     * Obtiene todos los vuelos (para admin).
     */
    @Transactional(readOnly = true)
    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll()
            .stream()
            .map(flightMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca un vuelo por su código.
     */
    @Transactional(readOnly = true)
    public Flight findByFlightCode(String flightCode) {
        return flightRepository.findByFlightCode(flightCode)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado: " + flightCode));
    }

    /**
     * Busca un vuelo por ID.
     */
    @Transactional(readOnly = true)
    public Flight findById(Long id) {
        return flightRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
    }

    /**
     * Obtiene todos los vuelos próximos (no cancelados).
     */
    @Transactional(readOnly = true)
    public List<FlightDTO> getUpcomingFlights() {
        return flightRepository.findUpcomingFlights(LocalDateTime.now())
            .stream()
            .map(flightMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca vuelos por aeropuerto de origen.
     */
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByOrigin(String originAirport) {
        return flightRepository.findByOriginAirport(originAirport)
            .stream()
            .map(flightMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca vuelos por aeropuerto de destino.
     */
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByDestination(String destinationAirport) {
        return flightRepository.findByDestinationAirport(destinationAirport)
            .stream()
            .map(flightMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un vuelo y envía notificaciones.
     */
    @Transactional
    public FlightDTO updateFlightStatus(String flightCode, FlightStatus newStatus) {
        Flight flight = findByFlightCode(flightCode);
        FlightStatus oldStatus = flight.getStatus();

        // Validar transición de estado
        validateStatusTransition(oldStatus, newStatus);

        flight.setStatus(newStatus);
        flight = flightRepository.save(flight);

        log.info("Vuelo {} cambió de estado: {} -> {}", flightCode, oldStatus, newStatus);

        // Enviar notificaciones según el cambio de estado
        if (newStatus == FlightStatus.DELAYED) {
            notificationService.notifyFlightDelay(flight);
        } else if (newStatus == FlightStatus.CANCELLED) {
            notificationService.notifyFlightCancellation(flight);
        } else if (newStatus == FlightStatus.BOARDING) {
            notificationService.notifyBoardingStarted(flight);
        }

        return flightMapper.toDTO(flight);
    }

    /**
     * Actualiza la puerta de embarque y notifica a pasajeros.
     */
    @Transactional
    public FlightDTO updateGate(String flightCode, String newGate) {
        Flight flight = findByFlightCode(flightCode);
        String oldGate = flight.getGate();

        flight.setGate(newGate);
        flight = flightRepository.save(flight);

        log.info("Vuelo {} cambió puerta de embarque: {} -> {}", flightCode, oldGate, newGate);

        // Notificar cambio de puerta
        notificationService.notifyGateChange(flight, oldGate, newGate);

        return flightMapper.toDTO(flight);
    }

    /**
     * Actualiza la hora de salida y notifica a pasajeros.
     */
    @Transactional
    public FlightDTO updateDepartureTime(String flightCode, LocalDateTime newDepartureTime) {
        Flight flight = findByFlightCode(flightCode);
        LocalDateTime oldDepartureTime = flight.getDepartureTime();

        flight.setDepartureTime(newDepartureTime);
        flight.setEstimatedDepartureTime(newDepartureTime);
        
        // Actualizar tiempos de boarding
        flight.setBoardingStartTime(newDepartureTime.minusMinutes(45));
        flight.setBoardingCloseTime(newDepartureTime.minusMinutes(15));
        
        flight = flightRepository.save(flight);

        log.info("Vuelo {} cambió hora de salida: {} -> {}", flightCode, oldDepartureTime, newDepartureTime);

        // Notificar cambio de hora
        notificationService.notifyFlightTimeChange(flight, oldDepartureTime, newDepartureTime);

        return flightMapper.toDTO(flight);
    }

    /**
     * Valida que la transición de estado sea permitida.
     */
    private void validateStatusTransition(FlightStatus oldStatus, FlightStatus newStatus) {
        // No se puede cambiar de CANCELLED a otro estado
        if (oldStatus == FlightStatus.CANCELLED) {
            throw new IllegalStateException("No se puede cambiar el estado de un vuelo cancelado");
        }

        // No se puede estar BOARDING y CANCELLED simultáneamente
        if (oldStatus == FlightStatus.BOARDING && newStatus == FlightStatus.CANCELLED) {
            throw new IllegalStateException("No se puede cancelar un vuelo que está abordando");
        }
    }
}
