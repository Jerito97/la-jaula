CREATE TABLE usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    rol VARCHAR(50)
);

CREATE TABLE cancha (
    idCancha INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    tipo VARCHAR(50),
    ubicacion VARCHAR(100)
);

CREATE TABLE reserva (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE,
    horaInicio TIME,
    horaFin TIME,
    idUsuario INT,
    idCancha INT,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),
    FOREIGN KEY (idCancha) REFERENCES cancha(idCancha)
);
