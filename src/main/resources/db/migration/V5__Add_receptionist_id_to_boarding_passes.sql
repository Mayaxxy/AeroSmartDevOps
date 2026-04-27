-- Agregar columna receptionist_id a la tabla boarding_passes para auditoría
-- Requirement 7.2: Registrar qué recepcionista generó cada pase de abordaje

ALTER TABLE boarding_passes
ADD COLUMN receptionist_id BIGINT;

-- Agregar comentario a la columna para documentación
COMMENT ON COLUMN boarding_passes.receptionist_id IS 'ID del recepcionista que generó el pase de abordaje (auditoría)';
