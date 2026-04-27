package co.aerosmart.controllers;

import co.aerosmart.dto.AirplaneDTO;
import co.aerosmart.mappers.AirplaneMapper;
import co.aerosmart.model.Airplane;
import co.aerosmart.services.AirplaneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar aviones.
 */
@RestController
@RequestMapping("/api/airplanes")
@RequiredArgsConstructor
@Tag(name = "Airplanes", description = "API para gestión de aviones")
public class AirplaneController {

    private final AirplaneService airplaneService;
    private final AirplaneMapper airplaneMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los aviones", description = "Retorna la lista completa de aviones (solo admin)")
    public ResponseEntity<List<AirplaneDTO>> getAllAirplanes() {
        List<AirplaneDTO> airplanes = airplaneService.getAllAirplanes()
                .stream()
                .map(airplaneMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(airplanes);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener aviones disponibles", description = "Retorna solo los aviones con estado AVAILABLE")
    public ResponseEntity<List<AirplaneDTO>> getAvailableAirplanes() {
        List<AirplaneDTO> airplanes = airplaneService.getAvailableAirplanes()
                .stream()
                .map(airplaneMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(airplanes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener avión por ID")
    public ResponseEntity<AirplaneDTO> getAirplaneById(@PathVariable Long id) {
        Airplane airplane = airplaneService.getAirplaneById(id);
        return ResponseEntity.ok(airplaneMapper.toDTO(airplane));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo avión", description = "Crea un nuevo avión en el sistema (solo admin)")
    public ResponseEntity<AirplaneDTO> createAirplane(@Valid @RequestBody AirplaneDTO airplaneDTO) {
        Airplane airplane = airplaneMapper.toEntity(airplaneDTO);
        Airplane created = airplaneService.createAirplane(airplane);
        return ResponseEntity.status(HttpStatus.CREATED).body(airplaneMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un avión")
    public ResponseEntity<AirplaneDTO> updateAirplane(
            @PathVariable Long id,
            @Valid @RequestBody AirplaneDTO airplaneDTO) {
        Airplane airplane = airplaneMapper.toEntity(airplaneDTO);
        Airplane updated = airplaneService.updateAirplane(id, airplane);
        return ResponseEntity.ok(airplaneMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un avión")
    public ResponseEntity<Void> deleteAirplane(@PathVariable Long id) {
        airplaneService.deleteAirplane(id);
        return ResponseEntity.noContent().build();
    }
}
