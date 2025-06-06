package clases;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GestorPagos {
    private GestorReservas gestorReservas;
    private Scanner scanner;

    public GestorPagos(GestorReservas gestorReservas, Scanner scanner) {
        this.gestorReservas = gestorReservas;
        this.scanner = scanner;
    }

    // Menú de gestión de pago
    public void gestionar() {
        int opcionPagos;
        do {
            System.out.println("\n--- Gestión de Pagos ---");
            System.out.println("1. Listar Pagos");
            System.out.println("2. Registrar Nuevo Pago");
            System.out.println("3. Eliminar Pago");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Elija una opción: ");

            try {
                opcionPagos = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
                opcionPagos = 0;
                continue;
            }

            switch (opcionPagos) {
                case 1:
                    listarPagos();
                    break;
                case 2:
                    registrarPago();
                    break;
                case 3:
                    eliminarPago();
                    break;
                case 4:
                    System.out.println("Volviendo al Menú Principal de La Jaula...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionPagos != 4);
    }

    // Lista todos los pagos almacenados en la base de datos e imprime su información
    public void listarPagos() {
        System.out.println("\n--- Listado de Pagos ---");
        List<Pago> pagos = obtenerTodosLosPagos();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados en el sistema.");
            return;
        }
        for (Pago pago : pagos) {
            System.out.println(pago);
        }
    }

    // Registra un nuevo pago para una reserva pendiente
    public void registrarPago() {
        System.out.println("\n--- Registrar Nuevo Pago ---");

        // Obtiene las reservas pendientes de pago desde gestorReservas
        List<Reserva> reservasPendientes = gestorReservas.obtenerReservasPendientesDePago();

        if (reservasPendientes.isEmpty()) {
            System.out.println("No hay reservas pendientes de pago para registrar pagos.");
            return;
        }

        // Permite al usuario seleccionar la reserva para la que hará el pago
        Reserva reservaParaPagar = seleccionarReservaPendienteDePago(reservasPendientes);
        if (reservaParaPagar == null) {
            System.out.println("No se seleccionó una reserva válida.");
            return;
        }

        // Solicita al usuario ingresar el monto del pago
        System.out.print("Ingrese el monto del pago: ");
        double montoPago;
        try {
            montoPago = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida para el monto. Por favor, ingrese un número.");
            scanner.nextLine();
            return;
        }

        // Solicita al usuario el método de pago
        System.out.print("Ingrese el método de pago (EFECTIVO/TARJETA/TRANSFERENCIA): ");
        String metodoPago = scanner.nextLine().toUpperCase();

        // Calcula el costo estimado de la reserva en base a la duración y precio por hora
        long duracionHoras = java.time.temporal.ChronoUnit.HOURS.between(reservaParaPagar.getHoraInicio(), reservaParaPagar.getHoraFin());
        double costoEstimado = reservaParaPagar.getCancha().getPrecioPorHora() * duracionHoras;

        // Valida si el monto ingresado coincide, es menor o mayor que el costo estimado
        if (montoPago < costoEstimado) {
            System.out.println("Advertencia: El monto ingresado ($" + String.format("%.2f", montoPago) + ") es menor que el costo estimado de la reserva ($" + String.format("%.2f", costoEstimado) + ").");
            System.out.print("¿Desea continuar de todos modos? (s/n): ");
            String confirmacion = scanner.nextLine();
            if (!confirmacion.equalsIgnoreCase("s")) {
                System.out.println("Registro de pago cancelado.");
                return;
            }
        } else if (montoPago > costoEstimado) {
            System.out.println("Advertencia: El monto ingresado ($" + String.format("%.2f", montoPago) + ") es mayor que el costo estimado de la reserva ($" + String.format("%.2f", costoEstimado) + ").");
        } else {
            System.out.println("El monto ingresado ($" + String.format("%.2f", montoPago) + ") coincide con el costo estimado de la reserva ($" + String.format("%.2f", costoEstimado) + ").");
        }

        // Inserta el pago en la base de datos y actualiza el estado de la reserva a PAGADA
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO pago (idReserva, monto, fechaPago, metodoPago, estado) VALUES (?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            stmt.setInt(1, reservaParaPagar.getIdReserva());
            stmt.setDouble(2, montoPago);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setString(4, metodoPago);
            stmt.setString(5, "COMPLETADO");

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                // Obtiene el ID del pago recién insertado
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int nuevoId = rs.getInt(1);
                        System.out.println("Pago registrado con éxito. ID Pago: " + nuevoId);
                    }
                }
                // Actualiza el estado de la reserva a "PAGADA"
                gestorReservas.actualizarEstadoReserva(reservaParaPagar, "PAGADA");
                System.out.println("Estado de la reserva " + reservaParaPagar.getIdReserva() + " actualizado a: PAGADA");
            } else {
                System.out.println("No se pudo registrar el pago.");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Elimina un pago seleccionado por ID y actualiza la reserva asociada a estado PENDIENTE_PAGO
    public void eliminarPago() {
        System.out.println("\n--- Eliminar Pago ---");
        List<Pago> pagos = obtenerTodosLosPagos();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos para eliminar.");
            return;
        }

        listarPagos();
        System.out.print("Ingrese el ID del pago a eliminar: ");
        int idEliminar;
        try {
            idEliminar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        // Busca el pago en la base de datos por su ID
        Pago pagoEliminar = buscarPagoPorId(idEliminar);
        if (pagoEliminar == null) {
            System.out.println("Pago con ID " + idEliminar + " no encontrado.");
            return;
        }

        // Actualiza el estado de la reserva asociada a PENDIENTE_PAGO antes de eliminar el pago
        Reserva reservaAsociada = pagoEliminar.getReserva();
        if (reservaAsociada != null) {
            gestorReservas.actualizarEstadoReserva(reservaAsociada, "PENDIENTE_PAGO");
            System.out.println("Estado de la reserva " + reservaAsociada.getIdReserva() + " ha vuelto a: PENDIENTE_PAGO.");
        }

        // Elimina el pago de la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM pago WHERE idPago = ?")) {
            stmt.setInt(1, idEliminar);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Pago con ID " + idEliminar + " eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el pago.");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Obtiene todos los pagos almacenados en la base de datos con toda su información relacionada
    public List<Pago> obtenerTodosLosPagos() {
        List<Pago> pagos = new ArrayList<>();
        // Consulta SQL que une pago con reserva, usuario y cancha para obtener datos completos
        String sql = "SELECT p.idPago, p.monto, p.fechaPago, p.metodoPago, p.estado AS pagoEstado, " +
                "r.idReserva, r.fecha, r.horaInicio, r.horaFin, r.estado AS reservaEstado, " +
                "u.idUsuario, u.nombre AS usuarioNombre, u.apellido, u.email, u.password, u.rol, " +
                "c.idCancha, c.nombre AS canchaNombre, c.tipoCancha, c.precioPorHora " +
                "FROM pago p " +
                "JOIN reserva r ON p.idReserva = r.idReserva " +
                "JOIN usuario u ON r.idUsuario = u.idUsuario " +
                "JOIN cancha c ON r.idCancha = c.idCancha";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("usuarioNombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
                Cancha cancha = new Cancha(
                        rs.getInt("idCancha"),
                        rs.getString("canchaNombre"),
                        rs.getString("tipoCancha"),
                        rs.getDouble("precioPorHora")
                );
                Reserva reserva = new Reserva(
                        rs.getInt("idReserva"),
                        usuario,
                        cancha,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("horaInicio").toLocalTime(),
                        rs.getTime("horaFin").toLocalTime(),
                        rs.getString("reservaEstado")
                );
                pagos.add(new Pago(
                        rs.getInt("idPago"),
                        reserva,
                        rs.getDouble("monto"),
                        rs.getDate("fechaPago").toLocalDate(),
                        rs.getString("metodoPago"),
                        rs.getString("pagoEstado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pagos: " + e.getMessage());
            e.printStackTrace();
        }
        return pagos;
    }

    // Busca y retorna un pago específico por su ID desde la base de datos
    private Pago buscarPagoPorId(int id) {
        String sql = "SELECT p.idPago, p.monto, p.fechaPago, p.metodoPago, p.estado AS pagoEstado, " +
                "r.idReserva, r.fecha, r.horaInicio, r.horaFin, r.estado AS reservaEstado, " +
                "u.idUsuario, u.nombre AS usuarioNombre, u.apellido, u.email, u.password, u.rol, " +
                "c.idCancha, c.nombre AS canchaNombre, c.tipoCancha, c.precioPorHora " +
                "FROM pago p " +
                "JOIN reserva r ON p.idReserva = r.idReserva " +
                "JOIN usuario u ON r.idUsuario = u.idUsuario " +
                "JOIN cancha c ON r.idCancha = c.idCancha " +
                "WHERE p.idPago = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("usuarioNombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                    Cancha cancha = new Cancha(
                            rs.getInt("idCancha"),
                            rs.getString("canchaNombre"),
                            rs.getString("tipoCancha"),
                            rs.getDouble("precioPorHora")
                    );
                    Reserva reserva = new Reserva(
                            rs.getInt("idReserva"),
                            usuario,
                            cancha,
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("horaInicio").toLocalTime(),
                            rs.getTime("horaFin").toLocalTime(),
                            rs.getString("reservaEstado")
                    );
                    return new Pago(
                            rs.getInt("idPago"),
                            reserva,
                            rs.getDouble("monto"),
                            rs.getDate("fechaPago").toLocalDate(),
                            rs.getString("metodoPago"),
                            rs.getString("pagoEstado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar pago por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Muestra una lista numerada de reservas pendientes y permite seleccionar una para el pago
    public Reserva seleccionarReservaPendienteDePago(List<Reserva> reservasPendientes) {
        System.out.println("\nSeleccione la reserva a la que se le registrará el pago:");
        for (int i = 0; i < reservasPendientes.size(); i++) {
            Reserva r = reservasPendientes.get(i);
            long duracionHoras = java.time.temporal.ChronoUnit.HOURS.between(r.getHoraInicio(), r.getHoraFin());
            double costoEstimado = r.getCancha().getPrecioPorHora() * duracionHoras;
            System.out.println((i + 1) + ". ID Reserva: " + r.getIdReserva() +
                    ", Usuario: " + r.getUsuario().getNombre() +
                    ", Cancha: " + r.getCancha().getNombre() +
                    ", Fecha: " + r.getFecha() +
                    ", Hora: " + r.getHoraInicio() + " - " + r.getHoraFin() +
                    ", Costo Estimado: $" + String.format("%.2f", costoEstimado));
        }
        System.out.print("Ingrese el número de la reserva: ");
        int seleccion;
        try {
            seleccion = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida.");
            scanner.nextLine();
            return null;
        }
        if (seleccion < 1 || seleccion > reservasPendientes.size()) {
            System.out.println("Selección fuera de rango.");
            return null;
        }
        return reservasPendientes.get(seleccion - 1);
    }
}
