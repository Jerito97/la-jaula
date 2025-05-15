-- 1. Ver todas las reservas en una fecha específica
SELECT * FROM reserva
WHERE fecha = '2025-05-20';

-- 2. Ver canchas libres en un horario específico
SELECT *
FROM cancha c
WHERE c.idCancha NOT IN (
    SELECT r.idCancha
    FROM reserva r
    WHERE r.fecha = '2025-05-20'
      AND '18:00:00' < r.horaFin
      AND '18:00:00' >= r.horaInicio
);

-- 3. Ver historial de reservas de una cancha
SELECT r.fecha, r.horaInicio, r.horaFin, u.nombre AS reservadoPor
FROM reserva r
JOIN usuario u ON r.idUsuario = u.idUsuario
WHERE r.idCancha = 1
ORDER BY r.fecha, r.horaInicio;

-- 4. Ranking de usuarios que más reservan
SELECT u.nombre, COUNT(*) AS totalReservas
FROM reserva r
JOIN usuario u ON r.idUsuario = u.idUsuario
GROUP BY u.idUsuario
ORDER BY totalReservas DESC;
