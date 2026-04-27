-- Add receptionist_id column to baggage_reports table for audit purposes
ALTER TABLE baggage_reports ADD COLUMN receptionist_id BIGINT;

-- Add comment to document the purpose
COMMENT ON COLUMN baggage_reports.receptionist_id IS 'ID del recepcionista que creó el reporte de equipaje (para auditoría)';
