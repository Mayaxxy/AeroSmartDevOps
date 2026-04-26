package co.aerosmart.validation;

import co.aerosmart.dto.CreateFlightRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FlightDatesValidator implements ConstraintValidator<ValidFlightDates, CreateFlightRequest> {

    @Override
    public void initialize(ValidFlightDates constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(CreateFlightRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null objects
        }

        if (request.getDepartureTime() == null || request.getArrivalTime() == null) {
            return true; // Let @NotNull handle null fields
        }

        return request.getArrivalTime().isAfter(request.getDepartureTime());
    }
}
