package co.aerosmart.controllers;

import co.aerosmart.dto.FlightDTO;
import co.aerosmart.model.FlightStatus;
import co.aerosmart.services.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de vuelos.
 * Endpoints públicos para consulta de vuelos.
 * Endpoints privados para actualización de vuelos (requieren autenticación).
 */
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FlightController {

    private final FlightService flightService;

    /**
     * Obtiene todos los vuelos próximos (no cancelados).
     * GET /api/flights/public/upcoming
     */
    @GetMapping("/public/upcoming")
    public ResponseEntity<List<FlightDTO>> getUpcomingFlights() {
        List<FlightDTO> flights = flightService.getUpcomingFlights();
        return ResponseEntity.ok(flights);
    }

    /**
     * Busca vuelos por aeropuerto de origen.
     * GET /api/flights/public/origin/{originAirport}
     */
    @GetMapping("/public/origin/{originAirport}")
    public ResponseEntity<List<FlightDTO>> getFlightsByOrigin(@PathVariable String originAirport) {
        List<FlightDTO> flights = flightService.getFlightsByOrigin(originAirport);
        return ResponseEntity.ok(flights);
    }

    /**
     * Busca vuelos por aeropuerto de destino.
     * GET /api/flights/public/destination/{destinationAirport}
     */
    @GetMapping("/public/destination/{destinationAirport}")
    public ResponseEntity<List<FlightDTO>> getFlightsByDestination(@PathVariable String destinationAirport) {
        List<FlightDTO> flights = flightService.getFlightsByDestination(destinationAirport);
        return ResponseEntity.ok(flights);
    }

    /**
     * Actualiza el estado de un vuelo.
     * PUT /api/flights/{flightCode}/status
     * Requiere autenticación (para personal del aeropuerto).
     */
    @PutMapping("/{flightCode}/status")
    public ResponseEntity<FlightDTO> updateFlightStatus(
            @PathVariable String flightCode,
            @RequestParam FlightStatus status) {
        FlightDTO flight = flightService.updateFlightStatus(flightCode, status);
        return ResponseEntity.ok(flight);
    }

    /**
     * Actualiza la puerta de embarque de un vuelo.
     * PUT /api/flights/{flightCode}/gate
     * Requiere autenticación (para personal del aeropuerto).
     */
    @PutMapping("/{flightCode}/gate")
    public ResponseEntity<FlightDTO> updateGate(
            @PathVariable String flightCode,
            @RequestParam String gate) {
        FlightDTO flight = flightService.updateGate(flightCode, gate);
        return ResponseEntity.ok(flight);
    }

    /**
     * Actualiza la hora de salida de un vuelo.
     * PUT /api/flights/{flightCode}/departure-time
     * Requiere autenticación (para personal del aeropuerto).
     */
    @PutMapping("/{flightCode}/departure-time")
    public ResponseEntity<FlightDTO> updateDepartureTime(
            @PathVariable String flightCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
        FlightDTO flight = flightService.updateDepartureTime(flightCode, departureTime);
        return ResponseEntity.ok(flight);
    }
}
