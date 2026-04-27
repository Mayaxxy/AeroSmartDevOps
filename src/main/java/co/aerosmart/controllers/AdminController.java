package co.aerosmart.controllers;

import co.aerosmart.dto.CreateFlightRequest;
import co.aerosmart.dto.CreateReceptionistRequest;
import co.aerosmart.dto.FlightDTO;
import co.aerosmart.dto.PassengerDTO;
import co.aerosmart.services.FlightService;
import co.aerosmart.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FlightService flightService;
    private final PassengerService passengerService;

    @GetMapping("/flights")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDTO> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightDTO flight = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flight);
    }

    @PostMapping("/receptionists")
    public ResponseEntity<PassengerDTO> createReceptionist(@Valid @RequestBody CreateReceptionistRequest request) {
        PassengerDTO receptionist = passengerService.createReceptionist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(receptionist);
    }
}
