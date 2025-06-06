USE lajaula;

CREATE TABLE IF NOT EXISTS `usuario` (
    `idUsuario` INT NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(100) NOT NULL,
    `apellido` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `rol` VARCHAR(50) NOT NULL, -- Por ejemplo, 'CLIENTE', 'ADMIN'
    PRIMARY KEY (`idUsuario`)
);

CREATE TABLE IF NOT EXISTS `cancha` (
    `idCancha` INT NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(100) NOT NULL,
    `tipoCancha` VARCHAR(50) NOT NULL, -- Cambiado de 'tipo' a 'tipoCancha' para coincidir con Java
    `precioPorHora` DECIMAL(10, 2) NOT NULL, -- Nuevo: Precio por hora de la cancha
    PRIMARY KEY (`idCancha`)
);

CREATE TABLE IF NOT EXISTS `reserva` (
    `idReserva` INT NOT NULL AUTO_INCREMENT,
    `fecha` DATE NOT NULL,
    `horaInicio` TIME NOT NULL,
    `horaFin` TIME NOT NULL,
    `estado` VARCHAR(50) NOT NULL, -- Nuevo: Por ejemplo, 'PENDIENTE_PAGO', 'PAGADA', 'CANCELADA', 'CONFIRMADA'
    `idUsuario` INT NOT NULL,
    `idCancha` INT NOT NULL,
    PRIMARY KEY (`idReserva`),
    INDEX `fk_reserva_usuario_idx` (`idUsuario` ASC) VISIBLE,
    INDEX `fk_reserva_cancha_idx` (`idCancha` ASC) VISIBLE,
    CONSTRAINT `fk_reserva_usuario`
        FOREIGN KEY (`idUsuario`)
        REFERENCES `usuario` (`idUsuario`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `fk_reserva_cancha`
        FOREIGN KEY (`idCancha`)
        REFERENCES `cancha` (`idCancha`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS `pago` (
    `idPago` INT NOT NULL AUTO_INCREMENT,
    `idReserva` INT NOT NULL, -- Clave foránea a la reserva
    `monto` DECIMAL(10, 2) NOT NULL,
    `fechaPago` DATE NOT NULL,
    `metodoPago` VARCHAR(50) NOT NULL, -- Por ejemplo, 'TARJETA', 'EFECTIVO', 'TRANSFERENCIA'
    `estado` VARCHAR(50) NOT NULL, -- Por ejemplo, 'COMPLETADO', 'PENDIENTE', 'REEMBOLSADO'
    PRIMARY KEY (`idPago`),
    INDEX `fk_pago_reserva_idx` (`idReserva` ASC) VISIBLE,
    CONSTRAINT `fk_pago_reserva`
        FOREIGN KEY (`idReserva`)
        REFERENCES `reserva` (`idReserva`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);