package co.aerosmart.dto;

import co.aerosmart.model.BaggageReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de BaggageReport.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaggageReportDTO {
    private Long id;
    private String description;
    private BaggageReportStatus status;
    private FlightDTO flight;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime resolvedAt;
}
