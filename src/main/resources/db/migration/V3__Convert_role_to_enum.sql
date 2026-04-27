-- Migración para convertir el campo role de VARCHAR a ENUM
-- Agrega soporte para el nuevo rol RECEPCIONISTA

-- Paso 1: Agregar columna role si no existe (para compatibilidad)
ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS role VARCHAR(20);

-- Paso 2: Actualizar valores NULL o vacíos a PASSENGER por defecto
UPDATE passengers 
SET role = 'PASSENGER' 
WHERE role IS NULL OR role = '';

-- Paso 3: Validar que todos los valores existentes sean válidos
-- Convertir cualquier valor no estándar a PASSENGER
UPDATE passengers 
SET role = 'PASSENGER' 
WHERE role NOT IN ('ADMIN', 'PASSENGER', 'RECEPCIONISTA');

-- Paso 4: Hacer la columna NOT NULL
ALTER TABLE passengers
ALTER COLUMN role SET NOT NULL;

-- Paso 5: Agregar constraint para validar valores del enum
ALTER TABLE passengers
DROP CONSTRAINT IF EXISTS passengers_role_check;

ALTER TABLE passengers
ADD CONSTRAINT passengers_role_check 
CHECK (role IN ('ADMIN', 'PASSENGER', 'RECEPCIONISTA'));

-- Paso 6: Crear índice para mejorar búsquedas por rol
CREATE INDEX IF NOT EXISTS idx_passengers_role ON passengers(role);
