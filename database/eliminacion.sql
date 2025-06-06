USE lajaula;

-- Eliminar una reserva específica
SET FOREIGN_KEY_CHECKS = 0; 
-- 1. Eliminar los pagos asociados a la reserva (si existen)
DELETE FROM
    pago
WHERE
    idReserva = 3;
-- 2. Eliminar la reserva específica
DELETE FROM
    reserva
WHERE
    idReserva = 3;

SET FOREIGN_KEY_CHECKS = 1;
