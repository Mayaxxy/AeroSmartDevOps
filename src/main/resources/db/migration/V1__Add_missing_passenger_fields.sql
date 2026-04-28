-- Agregar campos faltantes a la tabla passengers si no existen
ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS middle_name VARCHAR(50),
ADD COLUMN IF NOT EXISTS second_last_name VARCHAR(50);

-- Asegurar que birth_date existe (nullable para compatibilidad con módulo admin)
-- ALTER TABLE passengers ALTER COLUMN birth_date SET NOT NULL;

-- Asegurar que document_type existe
ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS document_type VARCHAR(10);

-- Actualizar document_type si es NULL (para datos existentes)
UPDATE passengers SET document_type = 'CC' WHERE document_type IS NULL;

-- Hacer document_type NOT NULL
ALTER TABLE passengers
ALTER COLUMN document_type SET NOT NULL;
