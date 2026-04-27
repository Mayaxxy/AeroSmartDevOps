package co.aerosmart.controllers;

import co.aerosmart.dto.CreateFlightRequest;
import co.aerosmart.dto.FlightDTO;
import co.aerosmart.model.FlightStatus;
import co.aerosmart.services.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Vuelos", description = "API para gestión de vuelos")
public class FlightController {

    private final FlightService flightService;

    /**
     * Crea un nuevo vuelo en el sistema.
     * POST /api/flights
     * 
     * Solo usuarios con rol ADMIN pueden crear vuelos.
     * Valida que el código de vuelo sea único y que los datos sean consistentes.
     * 
     * @param request Datos del vuelo a crear (código, origen, destino, fechas, puerta)
     * @return FlightDTO con el vuelo creado y status HTTP 201 Created
     * @throws IllegalArgumentException si el código de vuelo ya existe o los datos son inválidos
     */
    @Operation(
        summary = "Crear nuevo vuelo",
        description = "Crea un nuevo vuelo en el sistema. Solo usuarios con rol ADMIN pueden crear vuelos. " +
                      "Valida que el código de vuelo sea único, que los aeropuertos de origen y destino sean diferentes, " +
                      "y que la fecha de llegada sea posterior a la fecha de salida."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Vuelo creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos o código de vuelo duplicado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token JWT requerido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No autorizado - Requiere rol ADMIN",
            content = @Content(mediaType = "application/json")
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightDTO> createFlight(
            @Parameter(description = "Datos del vuelo a crear", required = true)
            @Valid @RequestBody CreateFlightRequest request) {
        FlightDTO flight = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flight);
    }

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
     * Obtiene todos los vuelos (incluyendo cancelados y pasados).
     * GET /api/flights
     * Requiere autenticación (solo para admin).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
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
