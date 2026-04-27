package co.aerosmart.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para verificar que el campo role en Passenger funciona correctamente con el enum Role.
 */
@DisplayName("Passenger Role Field Tests")
class PassengerRoleTest {

    @Test
    @DisplayName("Debe asignar y obtener rol ADMIN correctamente")
    void shouldSetAndGetAdminRole() {
        Passenger passenger = new Passenger();
        passenger.setRole(Role.ADMIN);
        
        assertEquals(Role.ADMIN, passenger.getRole());
        assertEquals("ADMIN", passenger.getRole().name());
    }

    @Test
    @DisplayName("Debe asignar y obtener rol PASSENGER correctamente")
    void shouldSetAndGetPassengerRole() {
        Passenger passenger = new Passenger();
        passenger.setRole(Role.PASSENGER);
        
        assertEquals(Role.PASSENGER, passenger.getRole());
        assertEquals("PASSENGER", passenger.getRole().name());
    }

    @Test
    @DisplayName("Debe asignar y obtener rol RECEPCIONISTA correctamente")
    void shouldSetAndGetReceptionistRole() {
        Passenger passenger = new Passenger();
        passenger.setRole(Role.RECEPCIONISTA);
        
        assertEquals(Role.RECEPCIONISTA, passenger.getRole());
        assertEquals("RECEPCIONISTA", passenger.getRole().name());
    }

    @Test
    @DisplayName("Debe permitir rol null")
    void shouldAllowNullRole() {
        Passenger passenger = new Passenger();
        passenger.setRole(null);
        
        assertNull(passenger.getRole());
    }

    @Test
    @DisplayName("Debe crear pasajero con todos los campos incluyendo rol")
    void shouldCreatePassengerWithAllFields() {
        Passenger passenger = new Passenger();
        passenger.setEmail("test@example.com");
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setDocumentId("123456");
        passenger.setPhone("+57 300 000 0000");
        passenger.setPassword("password123");
        passenger.setRole(Role.PASSENGER);
        
        assertNotNull(passenger);
        assertEquals(Role.PASSENGER, passenger.getRole());
        assertEquals("test@example.com", passenger.getEmail());
    }
}
