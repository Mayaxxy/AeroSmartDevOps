-- Add receptionist_id column to check_ins table for audit purposes
-- This allows tracking which receptionist performed each check-in operation

ALTER TABLE check_ins
ADD COLUMN receptionist_id BIGINT;

-- Add comment to document the purpose of this column
COMMENT ON COLUMN check_ins.receptionist_id IS 'ID of the receptionist who performed the check-in (null if done by passenger)';
