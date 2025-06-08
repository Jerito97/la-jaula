USE lajaula;

-- Inserción de usuarios de ejemplo
INSERT INTO usuario (nombre, apellido, email, password, rol) VALUES
('Juan', 'Pérez', 'juanperez@gmail.com', '4qxvBVof#u4', 'CLIENTE'),
('María', 'López', 'marialopez@gmail.com', 'vwt9LNgy$e7', 'CLIENTE'),
('Carlos', 'Díaz', 'carlosdiaz@gmail.com', 'E42teBYp^76', 'CLIENTE'),
('Ana', 'Torres', 'anatorres@gmail.com', '9RN4z3TX&C9', 'ADMIN');

-- Inserción de canchas de ejemplo
INSERT INTO cancha (nombre, tipoCancha, precioPorHora) VALUES
('Cancha A', 'fútbol 5', 100000.00),
('Cancha B', 'fútbol 5', 100000.00),
('Cancha C', 'padel', 40000.00),
('Cancha D', 'tenis', 50000.00);

-- Inserción de reservas de ejemplo
INSERT INTO reserva (fecha, horaInicio, horaFin, estado, idUsuario, idCancha) VALUES
('2025-06-10', '18:00:00', '19:00:00', 'PAGADA', 1, 1),
('2025-06-10', '19:00:00', '20:00:00', 'PENDIENTE_PAGO', 2, 2),
('2025-06-11', '20:00:00', '21:00:00', 'PAGADA', 3, 3),
('2025-06-12', '21:00:00', '22:00:00', 'PAGADA', 1, 2),
('2025-06-15', '20:00:00', '21:00:00', 'PENDIENTE_PAGO', 2, 4);

-- Inserción de pagos de ejemplo
INSERT INTO pago (idReserva, monto, fechaPago, metodoPago, estado) VALUES
(1, 25.00, '2025-06-09', 'TARJETA', 'COMPLETADO'),
(3, 18.00, '2025-06-10', 'EFECTIVO', 'COMPLETADO'),
(4, 35.00, '2025-06-11', 'TRANSFERENCIA', 'COMPLETADO');
