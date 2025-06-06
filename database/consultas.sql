USE lajaula;

-- 1. Ver todas las reservas en una fecha específica
-- Esta consulta te permite encontrar todas las reservas programadas para un día en particular.
SELECT
    r.idReserva,
    r.fecha,
    r.horaInicio,
    r.horaFin,
    r.estado,
    u.nombre AS nombreUsuario,
    u.apellido AS apellidoUsuario,
    c.nombre AS nombreCancha,
    c.tipoCancha
FROM
    reserva r
JOIN
    usuario u ON r.idUsuario = u.idUsuario
JOIN
    cancha c ON r.idCancha = c.idCancha
WHERE
    r.fecha = '2025-05-20'; -- Puedes cambiar '2025-05-20' por la fecha deseada

-- 2. Ver canchas disponibles en un horario específico
-- Esta consulta identifica qué canchas no tienen reservas que se superpongan con un horario dado en una fecha específica.
SELECT
    c.idCancha,
    c.nombre,
    c.tipoCancha,
    c.precioPorHora
FROM
    cancha c
WHERE
    c.idCancha NOT IN (
        SELECT
            r.idCancha
        FROM
            reserva r
        WHERE
            r.fecha = '2025-05-20' -- Fecha a consultar
            AND r.estado IN ('CONFIRMADA', 'PENDIENTE_PAGO', 'PAGADA') -- Considera solo reservas activas
            AND (
                ('18:00:00' < r.horaFin AND '19:00:00' > r.horaInicio) -- Rango de hora: de 18:00:00 a 19:00:00
            )
    );

-- 3. Ver historial de reservas de una cancha específica
-- Muestra todas las reservas realizadas para una cancha en particular, incluyendo quién la reservó.
SELECT
    r.fecha,
    r.horaInicio,
    r.horaFin,
    r.estado,
    u.nombre AS nombreUsuario,
    u.apellido AS apellidoUsuario,
    p.monto AS montoPago,
    p.estado AS estadoPago
FROM
    reserva r
JOIN
    usuario u ON r.idUsuario = u.idUsuario
JOIN
    cancha c ON r.idCancha = c.idCancha
LEFT JOIN -- Usamos LEFT JOIN para incluir reservas incluso si no tienen un pago asociado todavía
    pago p ON r.idReserva = p.idReserva
WHERE
    c.idCancha = 1 -- Cambia '1' por el ID de la cancha que deseas consultar
ORDER BY
    r.fecha ASC, r.horaInicio ASC;

-- 4. Ranking de usuarios que más reservan
-- Genera una lista de usuarios ordenada por la cantidad total de reservas que han realizado, de mayor a menor.
SELECT
    u.nombre AS nombreUsuario,
    u.apellido AS apellidoUsuario,
    u.email,
    COUNT(r.idReserva) AS totalReservas
FROM
    usuario u
JOIN
    reserva r ON u.idUsuario = r.idUsuario
GROUP BY
    u.idUsuario, u.nombre, u.apellido, u.email
ORDER BY
    totalReservas DESC;
