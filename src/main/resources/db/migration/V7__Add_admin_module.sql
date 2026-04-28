-- Agregar columna active a passengers para soporte de desactivación
ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;

-- Activar todos los usuarios existentes
UPDATE passengers SET active = TRUE WHERE active IS NULL;

-- Crear tabla de auditoría
CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    admin_id    BIGINT NOT NULL,
    target_id   BIGINT,
    target_email VARCHAR(100),
    action      VARCHAR(20) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_audit_logs_target_id ON audit_logs(target_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at DESC);
