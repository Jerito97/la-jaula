INSERT INTO usuario (nombre, email, password, rol) VALUES
('Juan Pérez', 'juanperez@gmail.com', '4qxvBVof#u4', 'cliente'),
('María López', 'marialopez@gmail.com', 'vwt9LNgy$e7', 'cliente'),
('Carlos Díaz', 'carlosdiaz@gmail.com', 'E42teBYp^76', 'cliente'),
('Ana Torres', 'anatorres@gmail.com', '9RN4z3TX&C9', 'admin');

INSERT INTO cancha (nombre, tipo, ubicacion) VALUES
('Cancha 1', 'fútbol 5', 'Sede Central'),
('Cancha 2', 'fútbol 5', 'Sede Central'),
('Cancha 1', 'padel', 'Sede Central'),
('Cancha 2', 'padel', 'Sede Central'),
('Cancha 3', 'fútbol 5', 'Sede Central');

-- Asegurate de que los idUsuario y idCancha existan antes de insertar estas reservas
INSERT INTO reserva (fecha, horaInicio, horaFin, idUsuario, idCancha) VALUES
('2025-05-20', '18:00:00', '19:00:00', 1, 1),
('2025-05-20', '19:00:00', '20:00:00', 2, 2),
('2025-05-21', '20:00:00', '21:00:00', 3, 3),
('2025-05-22', '21:00:00', '22:00:00', 1, 2),
('2025-05-23', '17:00:00', '18:00:00', 4, 1),
('2025-05-24', '18:00:00', '19:30:00', 3, 4),
('2025-05-25', '20:00:00', '21:00:00', 2, 4);
