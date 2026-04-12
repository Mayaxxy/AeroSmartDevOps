package co.aerosmart.mappers;

import co.aerosmart.dto.BaggageReportDTO;
import co.aerosmart.model.BaggageReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre BaggageReport y BaggageReportDTO.
 */
@Component
@RequiredArgsConstructor
public class BaggageReportMapper {
    
    private final FlightMapper flightMapper;
    
    public BaggageReportDTO toDTO(BaggageReport report) {
        if (report == null) {
            return null;
        }
        
        BaggageReportDTO dto = new BaggageReportDTO();
        dto.setId(report.getId());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setFlight(flightMapper.toDTO(report.getFlight()));
        dto.setCreatedAt(report.getCreatedAt());
        dto.setReviewedAt(report.getReviewedAt());
        dto.setResolvedAt(report.getResolvedAt());
        
        return dto;
    }
}
