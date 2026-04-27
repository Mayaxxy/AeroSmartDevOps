-- Crear tabla de aviones
CREATE TABLE IF NOT EXISTS airplanes (
    id BIGSERIAL PRIMARY KEY,
    registration VARCHAR(20) UNIQUE NOT NULL,
    model VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    year_manufactured INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Agregar columna airplane_id a la tabla flights
ALTER TABLE flights ADD COLUMN IF NOT EXISTS airplane_id BIGINT;

-- Agregar foreign key constraint
ALTER TABLE flights 
ADD CONSTRAINT fk_flights_airplane 
FOREIGN KEY (airplane_id) 
REFERENCES airplanes(id) 
ON DELETE SET NULL;

-- Crear índice para mejorar búsquedas
CREATE INDEX IF NOT EXISTS idx_airplanes_status ON airplanes(status);
CREATE INDEX IF NOT EXISTS idx_airplanes_registration ON airplanes(registration);
CREATE INDEX IF NOT EXISTS idx_flights_airplane_id ON flights(airplane_id);

-- Insertar algunos aviones de ejemplo
INSERT INTO airplanes (registration, model, manufacturer, capacity, status, year_manufactured) VALUES
('HK-5050', 'Boeing 737-800', 'Boeing', 189, 'AVAILABLE', 2018),
('HK-5051', 'Airbus A320', 'Airbus', 180, 'AVAILABLE', 2019),
('HK-5052', 'Boeing 787-8', 'Boeing', 242, 'AVAILABLE', 2020),
('HK-5053', 'Airbus A321', 'Airbus', 220, 'AVAILABLE', 2021),
('HK-5054', 'Boeing 737-700', 'Boeing', 149, 'AVAILABLE', 2017),
('N12345', 'Boeing 777-300ER', 'Boeing', 396, 'AVAILABLE', 2019),
('N67890', 'Airbus A330-300', 'Airbus', 277, 'AVAILABLE', 2018)
ON CONFLICT (registration) DO NOTHING;
