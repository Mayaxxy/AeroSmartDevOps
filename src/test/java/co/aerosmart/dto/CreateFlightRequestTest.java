package co.aerosmart.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateFlightRequest Validation Tests")
class CreateFlightRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateFlightRequest createValidRequest() {
        CreateFlightRequest request = new CreateFlightRequest();
        request.setFlightCode("AV123");
        request.setOriginAirport("BOG");
        request.setDestinationAirport("MDE");
        request.setDepartureTime(LocalDateTime.now().plusHours(2));
        request.setArrivalTime(LocalDateTime.now().plusHours(3));
        request.setGate("A12");
        return request;
    }

    @Test
    @DisplayName("Valid request should pass all validations")
    void testValidRequest() {
        CreateFlightRequest request = createValidRequest();
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    // Flight Code Pattern Validation Tests

    @Test
    @DisplayName("Flight code with 2 letters and 3 numbers should be valid")
    void testFlightCodeValid3Digits() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("AV123");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with 2 letters and 4 numbers should be valid")
    void testFlightCodeValid4Digits() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("LA1234");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with lowercase letters should fail")
    void testFlightCodeLowercase() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("av123");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("formato AA123 o AA1234")));
    }

    @Test
    @DisplayName("Flight code with only 1 letter should fail")
    void testFlightCodeOneLetter() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("A123");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with 3 letters should fail")
    void testFlightCodeThreeLetters() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("AVI123");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with only 2 numbers should fail")
    void testFlightCodeTwoDigits() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("AV12");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with 5 numbers should fail")
    void testFlightCodeFiveDigits() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("AV12345");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Flight code with special characters should fail")
    void testFlightCodeSpecialCharacters() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("AV-123");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Empty flight code should fail")
    void testFlightCodeEmpty() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode("");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Null flight code should fail")
    void testFlightCodeNull() {
        CreateFlightRequest request = createValidRequest();
        request.setFlightCode(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    // Date Consistency Validation Tests

    @Test
    @DisplayName("Arrival time after departure time should be valid")
    void testValidDateRange() {
        CreateFlightRequest request = createValidRequest();
        LocalDateTime departure = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime arrival = LocalDateTime.of(2024, 6, 15, 12, 0);
        request.setDepartureTime(departure);
        request.setArrivalTime(arrival);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Arrival time before departure time should fail")
    void testArrivalBeforeDeparture() {
        CreateFlightRequest request = createValidRequest();
        LocalDateTime departure = LocalDateTime.of(2024, 6, 15, 12, 0);
        LocalDateTime arrival = LocalDateTime.of(2024, 6, 15, 10, 0);
        request.setDepartureTime(departure);
        request.setArrivalTime(arrival);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("posterior a la fecha de salida")));
    }

    @Test
    @DisplayName("Arrival time equal to departure time should fail")
    void testArrivalEqualsDeparture() {
        CreateFlightRequest request = createValidRequest();
        LocalDateTime sameTime = LocalDateTime.of(2024, 6, 15, 10, 0);
        request.setDepartureTime(sameTime);
        request.setArrivalTime(sameTime);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("posterior a la fecha de salida")));
    }

    @Test
    @DisplayName("Null departure time should fail")
    void testNullDepartureTime() {
        CreateFlightRequest request = createValidRequest();
        request.setDepartureTime(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("fecha de salida es obligatoria")));
    }

    @Test
    @DisplayName("Null arrival time should fail")
    void testNullArrivalTime() {
        CreateFlightRequest request = createValidRequest();
        request.setArrivalTime(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("fecha de llegada es obligatoria")));
    }

    // Airport Validation Tests

    @Test
    @DisplayName("Empty origin airport should fail")
    void testEmptyOriginAirport() {
        CreateFlightRequest request = createValidRequest();
        request.setOriginAirport("");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("aeropuerto de origen es obligatorio")));
    }

    @Test
    @DisplayName("Null origin airport should fail")
    void testNullOriginAirport() {
        CreateFlightRequest request = createValidRequest();
        request.setOriginAirport(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Empty destination airport should fail")
    void testEmptyDestinationAirport() {
        CreateFlightRequest request = createValidRequest();
        request.setDestinationAirport("");
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("aeropuerto de destino es obligatorio")));
    }

    @Test
    @DisplayName("Null destination airport should fail")
    void testNullDestinationAirport() {
        CreateFlightRequest request = createValidRequest();
        request.setDestinationAirport(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    // Edge Cases

    @Test
    @DisplayName("Valid request with minimal time difference should pass")
    void testMinimalTimeDifference() {
        CreateFlightRequest request = createValidRequest();
        LocalDateTime departure = LocalDateTime.of(2024, 6, 15, 10, 0, 0);
        LocalDateTime arrival = LocalDateTime.of(2024, 6, 15, 10, 0, 1);
        request.setDepartureTime(departure);
        request.setArrivalTime(arrival);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid request without gate should pass")
    void testValidRequestWithoutGate() {
        CreateFlightRequest request = createValidRequest();
        request.setGate(null);
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Multiple validation errors should be reported")
    void testMultipleValidationErrors() {
        CreateFlightRequest request = new CreateFlightRequest();
        request.setFlightCode("invalid");
        request.setOriginAirport("");
        request.setDestinationAirport("");
        request.setDepartureTime(null);
        request.setArrivalTime(null);
        
        Set<ConstraintViolation<CreateFlightRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 4, "Should have multiple validation errors");
    }
}
