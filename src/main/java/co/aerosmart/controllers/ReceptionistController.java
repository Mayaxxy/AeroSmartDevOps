package co.aerosmart.controllers;

import co.aerosmart.dto.*;
import co.aerosmart.services.BaggageReportService;
import co.aerosmart.services.BoardingPassService;
import co.aerosmart.services.CheckInService;
import co.aerosmart.services.ReceptionistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones del rol RECEPCIONISTA.
 * Todos los endpoints están restringidos a usuarios con rol RECEPCIONISTA.
 * 
 * Funcionalidades:
 * - Búsqueda de pasajeros por documento o ID
 * - Realización de check-in para pasajeros
 * - Generación de pases de abordaje con QR
 * - Registro de reportes de equipaje
 */
@RestController
@RequestMapping("/api/receptionist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receptionist", description = "Endpoints para operaciones de recepcionistas")
@SecurityRequirement(name = "bearerAuth")
public class ReceptionistController {

    private final ReceptionistService receptionistService;
    private final CheckInService checkInService;
    private final BoardingPassService boardingPassService;
    private final BaggageReportService baggageReportService;

    /**
     * Busca un pasajero por documento o ID.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta búsqueda.
     * 
     * @param documentId Documento de identidad del pasajero (opcional)
     * @param passengerId ID del pasajero (opcional)
     * @param authentication Información de autenticación del usuario
     * @return PassengerSearchResultDTO con información del pasajero, reservas y vuelos
     */
    @GetMapping("/passengers/search")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Buscar pasajero",
        description = "Busca un pasajero por documento o ID. Retorna información completa del pasajero, sus reservas y vuelos asociados."
    )
    public ResponseEntity<PassengerSearchResultDTO> searchPassenger(
            @RequestParam(required = false) String documentId,
            @RequestParam(required = false) Long passengerId,
            Authentication authentication) {
        
        log.info("Recepcionista {} buscando pasajero: documentId={}, passengerId={}", 
            authentication.getName(), documentId, passengerId);
        
        PassengerSearchResultDTO result = receptionistService.searchPassenger(
            documentId, 
            passengerId, 
            authentication
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Realiza check-in para un pasajero.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta operación.
     * 
     * Valida:
     * - Que la reserva exista y esté activa
     * - Que el vuelo no esté cancelado
     * - Que no exista un check-in activo previo
     * - Que esté dentro de la ventana de check-in (24-48h antes)
     * 
     * @param request Datos del check-in (email del pasajero y código de reserva)
     * @param authentication Información de autenticación del usuario
     * @return BoardingPassDTO con el pase de abordaje generado
     */
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Realizar check-in",
        description = "Realiza check-in para un pasajero y genera su pase de abordaje con código QR."
    )
    public ResponseEntity<BoardingPassDTO> performCheckIn(
            @Valid @RequestBody ReceptionistCheckInRequest request,
            Authentication authentication) {
        
        log.info("Recepcionista {} realizando check-in para pasajero {} con reserva {}", 
            authentication.getName(), request.getPassengerEmail(), request.getReservationCode());
        
        // Crear CheckInRequest para el servicio existente
        CheckInRequest checkInRequest = new CheckInRequest(request.getReservationCode());
        
        BoardingPassDTO boardingPass = checkInService.performCheckIn(
            checkInRequest, 
            request.getPassengerEmail()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(boardingPass);
    }

    /**
     * Genera un pase de abordaje con código QR para un check-in existente.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta operación.
     * 
     * Valida:
     * - Que el check-in exista y esté activo
     * - Que el vuelo permita generación de QR
     * - Que esté dentro de la ventana de tiempo válida
     * 
     * @param checkInId ID del check-in
     * @param authentication Información de autenticación del usuario
     * @return BoardingPassDTO con el pase de abordaje y código QR
     */
    @PostMapping("/boarding-pass/{checkInId}")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Generar pase de abordaje",
        description = "Genera un pase de abordaje con código QR para un check-in existente."
    )
    public ResponseEntity<BoardingPassDTO> generateBoardingPass(
            @PathVariable Long checkInId,
            Authentication authentication) {
        
        log.info("Recepcionista {} generando pase de abordaje para check-in {}", 
            authentication.getName(), checkInId);
        
        // Obtener el pase de abordaje existente o regenerar el QR si es necesario
        BoardingPassDTO boardingPass = boardingPassService.getBoardingPassByCheckInId(checkInId);
        
        return ResponseEntity.ok(boardingPass);
    }

    /**
     * Registra un reporte de equipaje para un pasajero.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta operación.
     * 
     * Valida:
     * - Que el pasajero tenga una reserva activa en el vuelo
     * - Que no exceda el límite de reportes activos simultáneos
     * 
     * @param request Datos del reporte (email del pasajero, ID del vuelo y descripción)
     * @param authentication Información de autenticación del usuario
     * @return BaggageReportDTO con el reporte creado
     */
    @PostMapping("/baggage-report")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Registrar reporte de equipaje",
        description = "Registra un reporte de equipaje para un pasajero. El pasajero debe tener una reserva activa en el vuelo."
    )
    public ResponseEntity<BaggageReportDTO> createBaggageReport(
            @Valid @RequestBody ReceptionistBaggageReportRequest request,
            Authentication authentication) {
        
        log.info("Recepcionista {} registrando reporte de equipaje para pasajero {} en vuelo {}", 
            authentication.getName(), request.getPassengerEmail(), request.getFlightId());
        
        // Crear BaggageReportRequest para el servicio existente
        BaggageReportRequest baggageRequest = new BaggageReportRequest(
            request.getFlightId(),
            request.getDescription()
        );
        
        BaggageReportDTO report = baggageReportService.createReport(
            baggageRequest,
            request.getPassengerEmail()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Obtiene todos los reportes de equipaje.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta operación.
     * 
     * @param authentication Información de autenticación del usuario
     * @return Lista de todos los reportes de equipaje
     */
    @GetMapping("/baggage-reports")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Listar todos los reportes de equipaje",
        description = "Obtiene todos los reportes de equipaje del sistema."
    )
    public ResponseEntity<java.util.List<BaggageReportDTO>> getAllBaggageReports(
            Authentication authentication) {
        
        log.info("Recepcionista {} consultando todos los reportes de equipaje", 
            authentication.getName());
        
        java.util.List<BaggageReportDTO> reports = baggageReportService.getAllReports();
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Actualiza el estado de un reporte de equipaje.
     * Solo usuarios con rol RECEPCIONISTA pueden realizar esta operación.
     * 
     * Valida el flujo de estados: PENDING → IN_PROGRESS → RESOLVED
     * 
     * @param reportId ID del reporte
     * @param status Nuevo estado del reporte
     * @param authentication Información de autenticación del usuario
     * @return BaggageReportDTO con el reporte actualizado
     */
    @PutMapping("/baggage-reports/{reportId}/status")
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(
        summary = "Actualizar estado de reporte",
        description = "Actualiza el estado de un reporte de equipaje siguiendo el flujo: PENDING → IN_PROGRESS → RESOLVED"
    )
    public ResponseEntity<BaggageReportDTO> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam String status,
            Authentication authentication) {
        
        log.info("Recepcionista {} actualizando reporte {} a estado {}", 
            authentication.getName(), reportId, status);
        
        BaggageReportDTO report = baggageReportService.updateReportStatus(reportId, status);
        
        return ResponseEntity.ok(report);
    }
}
