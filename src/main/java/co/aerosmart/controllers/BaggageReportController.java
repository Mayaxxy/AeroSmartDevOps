package co.aerosmart.controllers;

import co.aerosmart.dto.BaggageReportDTO;
import co.aerosmart.dto.BaggageReportRequest;
import co.aerosmart.model.BaggageReportStatus;
import co.aerosmart.services.BaggageReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de reportes de equipaje.
 * Requiere autenticación.
 */
@RestController
@RequestMapping("/api/baggage-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BaggageReportController {

    private final BaggageReportService baggageReportService;

    /**
     * Crea un reporte de equipaje.
     * POST /api/baggage-reports
     */
    @PostMapping
    public ResponseEntity<BaggageReportDTO> createReport(
            @Valid @RequestBody BaggageReportRequest request,
            Authentication authentication) {
        String passengerEmail = authentication.getName();
        BaggageReportDTO report = baggageReportService.createReport(request, passengerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Obtiene todos los reportes del pasajero autenticado.
     * GET /api/baggage-reports/my-reports
     */
    @GetMapping("/my-reports")
    public ResponseEntity<List<BaggageReportDTO>> getMyReports(Authentication authentication) {
        String passengerEmail = authentication.getName();
        List<BaggageReportDTO> reports = baggageReportService.getPassengerReports(passengerEmail);
        return ResponseEntity.ok(reports);
    }

    /**
     * Obtiene un reporte específico por ID.
     * GET /api/baggage-reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<BaggageReportDTO> getReportById(
            @PathVariable Long reportId,
            Authentication authentication) {
        String passengerEmail = authentication.getName();
        BaggageReportDTO report = baggageReportService.getReportById(reportId, passengerEmail);
        return ResponseEntity.ok(report);
    }

    /**
     * Actualiza el estado de un reporte.
     * PUT /api/baggage-reports/{reportId}/status
     * Para uso del personal del aeropuerto.
     */
    @PutMapping("/{reportId}/status")
    public ResponseEntity<BaggageReportDTO> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam BaggageReportStatus status) {
        BaggageReportDTO report = baggageReportService.updateReportStatus(reportId, status);
        return ResponseEntity.ok(report);
    }

    /**
     * Obtiene reportes por estado.
     * GET /api/baggage-reports/by-status/{status}
     * Para uso del personal del aeropuerto.
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<BaggageReportDTO>> getReportsByStatus(@PathVariable BaggageReportStatus status) {
        List<BaggageReportDTO> reports = baggageReportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }
}
