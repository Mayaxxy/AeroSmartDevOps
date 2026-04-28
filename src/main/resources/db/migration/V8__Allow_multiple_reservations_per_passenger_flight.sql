-- Eliminar la constraint única que impide re-reservar un vuelo cancelado.
-- La lógica de "solo una reserva ACTIVE por pasajero/vuelo" se maneja en la capa de servicio.
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_passenger_id_flight_id_key;
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS uk_reservations_passenger_flight;
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS ukdhmi0iajw7kcnakv2xkyln5u5;
