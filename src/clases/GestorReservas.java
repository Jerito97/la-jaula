package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GestorReservas {
    private GestorUsuarios gestorUsuarios;
    private GestorCanchas gestorCanchas;
    private Scanner scanner;

    public GestorReservas(GestorUsuarios gestorUsuarios, GestorCanchas gestorCanchas, Scanner scanner) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorCanchas = gestorCanchas;
        this.scanner = scanner;
    }

    // Menú principal de gestión de reservas
    public void gestionar() {
        int opcionReservas;
        do {
            System.out.println("\n--- Gestión de Reservas ---");
            System.out.println("1. Listar Reservas");
            System.out.println("2. Agregar Nueva Reserva");
            System.out.println("3. Modificar Reserva Existente");
            System.out.println("4. Cancelar Reserva");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Elija una opción: ");

            try {
                opcionReservas = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
                opcionReservas = 0;
                continue;
            }

            // Ejecuta la acción correspondiente
            switch (opcionReservas) {
                case 1:
                    listarReservas();
                    break;
                case 2:
                    agregarReserva();
                    break;
                case 3:
                    modificarReserva();
                    break;
                case 4:
                    cancelarReserva();
                    break;
                case 5:
                    System.out.println("Volviendo al Menú Principal de La Jaula...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionReservas != 5);
    }

    // Lista todas las reservas disponibles
    public void listarReservas() {
        System.out.println("\n--- Listado de Reservas ---");
        List<Reserva> reservas = obtenerTodasLasReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas en el sistema.");
            return;
        }
        for (Reserva reserva : reservas) {
            System.out.println(reserva);
        }
    }

    // Crea una nueva reserva validando la disponibilidad
    public void agregarReserva() {
        System.out.println("\n--- Agregar Nueva Reserva ---");

        // Seleccionar usuario y cancha
        Usuario usuarioSeleccionado = gestorUsuarios.seleccionarUsuario();
        if (usuarioSeleccionado == null) return;

        Cancha canchaSeleccionada = gestorCanchas.seleccionarCancha();
        if (canchaSeleccionada == null) return;

        // Seleccionar día y hora
        LocalDate fechaSeleccionada = seleccionarDia();
        if (fechaSeleccionada == null) return;

        LocalTime horaInicio = seleccionarHoraInicio(canchaSeleccionada, fechaSeleccionada);
        if (horaInicio == null) return;

        LocalTime horaFin = seleccionarHoraFin(canchaSeleccionada, fechaSeleccionada, horaInicio);
        if (horaFin == null) return;

        // Validar que no haya superposición con otra reserva
        if (existeSolapamiento(canchaSeleccionada, fechaSeleccionada, horaInicio, horaFin)) {
            System.out.println("¡Error! La cancha ya está reservada en ese horario. Por favor, elija otro.");
            return;
        }

        // Insertar reserva en la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO reserva (fecha, horaInicio, horaFin, estado, idUsuario, idCancha) VALUES (?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(fechaSeleccionada));
            stmt.setTime(2, Time.valueOf(horaInicio));
            stmt.setTime(3, Time.valueOf(horaFin));
            stmt.setString(4, "PENDIENTE_PAGO");
            stmt.setInt(5, usuarioSeleccionado.getId());
            stmt.setInt(6, canchaSeleccionada.getIdCancha());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                // Obtener el ID generado para mostrarlo
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int nuevoId = rs.getInt(1);
                        System.out.println("Reserva agregada con éxito. ID: " + nuevoId);
                        Reserva nuevaReserva = new Reserva(nuevoId, usuarioSeleccionado, canchaSeleccionada, fechaSeleccionada, horaInicio, horaFin, "PENDIENTE_PAGO");
                        System.out.println(nuevaReserva);
                    }
                }
            } else {
                System.out.println("No se pudo agregar la reserva.");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Permite modificar el estado de una reserva existente
    public void modificarReserva() {
        System.out.println("\n--- Modificar Reserva ---");

        List<Reserva> reservas = obtenerTodasLasReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para modificar.");
            return;
        }

        listarReservas();
        System.out.print("Ingrese el ID de la reserva a modificar: ");
        int idModificar;
        try {
            idModificar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Reserva reservaModificar = buscarReservaPorId(idModificar);
        if (reservaModificar == null) {
            System.out.println("Reserva con ID " + idModificar + " no encontrada.");
            return;
        }

        System.out.println("Reserva actual: " + reservaModificar);
        System.out.println("Deje en blanco para mantener el valor actual.");

        // Permite modificar el estado
        System.out.print("Nuevo estado (PENDIENTE_PAGO, CONFIRMADA, PAGADA, CANCELADA, COMPLETADA) (" + reservaModificar.getEstado() + "): ");
        String nuevoEstado = scanner.nextLine().toUpperCase();
        if (!nuevoEstado.isEmpty()) {
            if (List.of("PENDIENTE_PAGO", "CONFIRMADA", "PAGADA", "CANCELADA", "COMPLETADA").contains(nuevoEstado)) {
                reservaModificar.setEstado(nuevoEstado);
            } else {
                System.out.println("Estado no válido. Se mantendrá el estado actual.");
            }
        }

        // Actualizar el estado en la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE reserva SET estado = ? WHERE idReserva = ?"
             )) {
            stmt.setString(1, reservaModificar.getEstado());
            stmt.setInt(2, reservaModificar.getIdReserva());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Reserva modificada con éxito: " + reservaModificar);
            } else {
                System.out.println("No se pudo modificar la reserva.");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cancela una reserva existente
    public void cancelarReserva() {
        System.out.println("\n--- Cancelar Reserva ---");
        List<Reserva> reservas = obtenerTodasLasReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para cancelar.");
            return;
        }

        listarReservas();
        System.out.print("Ingrese el ID de la reserva a cancelar: ");
        int idEliminar;
        try {
            idEliminar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Reserva reservaEliminar = buscarReservaPorId(idEliminar);
        if (reservaEliminar == null) {
            System.out.println("Reserva con ID " + idEliminar + " no encontrada.");
            return;
        }

        // Actualizar estado en objeto y base de datos
        reservaEliminar.setEstado("CANCELADA");
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE reserva SET estado = ? WHERE idReserva = ?")) {
            stmt.setString(1, "CANCELADA");
            stmt.setInt(2, reservaEliminar.getIdReserva());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Reserva con ID " + idEliminar + " ha sido CANCELADA.");
                System.out.println(reservaEliminar);
            } else {
                System.out.println("No se pudo cancelar la reserva.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cancelar reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Recupera todas las reservas de la base de datos y construye los objetos correspondientes
    public List<Reserva> obtenerTodasLasReservas() {
        List<Reserva> reservas = new ArrayList<>();

        // Consulta SQL que une reserva, usuario y cancha
        String sql = "SELECT r.idReserva, r.fecha, r.horaInicio, r.horaFin, r.estado, " +
                "u.idUsuario, u.nombre AS usuarioNombre, u.apellido, u.email, u.password, u.rol, " +
                "c.idCancha, c.nombre AS canchaNombre, c.tipoCancha, c.precioPorHora " +
                "FROM reserva r " +
                "JOIN usuario u ON r.idUsuario = u.idUsuario " +
                "JOIN cancha c ON r.idCancha = c.idCancha";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorre los resultados y crea objetos Reserva con sus relaciones
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

                reservas.add(new Reserva(
                        rs.getInt("idReserva"),
                        usuario,
                        cancha,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("horaInicio").toLocalTime(),
                        rs.getTime("horaFin").toLocalTime(),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas: " + e.getMessage());
            e.printStackTrace();
        }

        return reservas;
    }


    // Método para verificar solapamiento
    public boolean existeSolapamiento(Cancha cancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE idCancha = ? AND fecha = ? " +
                "AND ( (horaInicio < ? AND horaFin > ?) OR (horaInicio >= ? AND horaInicio < ?) ) " +
                "AND estado IN ('PENDIENTE_PAGO', 'CONFIRMADA', 'PAGADA')"; // Solo consideramos estados activos

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cancha.getIdCancha());
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(horaFin));
            stmt.setTime(4, Time.valueOf(horaInicio));
            stmt.setTime(5, Time.valueOf(horaInicio));
            stmt.setTime(6, Time.valueOf(horaFin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar solapamiento: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Verifica si una hora específica está ocupada en una cancha y fecha dadas.
    private boolean isHoraOcupada(Cancha cancha, LocalDate fecha, LocalTime horaAProbar) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE idCancha = ? AND fecha = ? " +
                "AND (? >= horaInicio AND ? < horaFin) " +
                "AND estado IN ('PENDIENTE_PAGO', 'CONFIRMADA', 'PAGADA')"; // Solo estados activos

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cancha.getIdCancha());
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(horaAProbar));
            stmt.setTime(4, Time.valueOf(horaAProbar));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Hora ocupada
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar hora ocupada: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Hora libre
    }


    // Busca una reserva por su ID y la devuelve como objeto Reserva con sus objetos relacionados Usuario y Cancha.
    public Reserva buscarReservaPorId(int idReserva) {
        String sql = "SELECT r.idReserva, r.fecha, r.horaInicio, r.horaFin, r.estado, " +
                "u.idUsuario, u.nombre AS usuarioNombre, u.apellido, u.email, u.password, u.rol, " +
                "c.idCancha, c.nombre AS canchaNombre, c.tipoCancha, c.precioPorHora " +
                "FROM reserva r " +
                "JOIN usuario u ON r.idUsuario = u.idUsuario " +
                "JOIN cancha c ON r.idCancha = c.idCancha " +
                "WHERE r.idReserva = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
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
                    return new Reserva(
                            rs.getInt("idReserva"),
                            usuario,
                            cancha,
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("horaInicio").toLocalTime(),
                            rs.getTime("horaFin").toLocalTime(),
                            rs.getString("estado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar reserva por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Obtiene una lista de todas las reservas que están pendientes de pago.
    public List<Reserva> obtenerReservasPendientesDePago() {
        List<Reserva> reservasPendientes = new ArrayList<>();
        String sql = "SELECT r.idReserva, r.fecha, r.horaInicio, r.horaFin, r.estado, " +
                "u.idUsuario, u.nombre AS usuarioNombre, u.apellido, u.email, u.password, u.rol, " +
                "c.idCancha, c.nombre AS canchaNombre, c.tipoCancha, c.precioPorHora " +
                "FROM reserva r " +
                "JOIN usuario u ON r.idUsuario = u.idUsuario " +
                "JOIN cancha c ON r.idCancha = c.idCancha " +
                "WHERE r.estado = 'PENDIENTE_PAGO'";

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
                reservasPendientes.add(new Reserva(
                        rs.getInt("idReserva"),
                        usuario,
                        cancha,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("horaInicio").toLocalTime(),
                        rs.getTime("horaFin").toLocalTime(),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas pendientes de pago: " + e.getMessage());
            e.printStackTrace();
        }
        return reservasPendientes;
    }

    // Actualiza el estado de una reserva en la base de datos.
    public void actualizarEstadoReserva(Reserva reserva, String nuevoEstado) {
        String sql = "UPDATE reserva SET estado = ? WHERE idReserva = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, reserva.getIdReserva());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Permite al usuario seleccionar un día de entre los próximos 7 días.
    public LocalDate seleccionarDia() {
        System.out.println("Seleccione el día:");
        List<LocalDate> diasDisponibles = new ArrayList<>();
        // CAMBIO AQUI: Obtener la fecha actual del sistema
        LocalDate fechaBase = LocalDate.now(); // Obtiene la fecha actual (hoy)

        for (int i = 0; i < 7; i++) {
            diasDisponibles.add(fechaBase.plusDays(i)); // Añade hoy y los 6 días siguientes
        }

        for (int i = 0; i < diasDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + diasDisponibles.get(i));
        }
        System.out.print("Elija una opción: ");
        int opcionDia;
        try {
            opcionDia = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el día.");
            scanner.nextLine();
            return null;
        }

        if (opcionDia > 0 && opcionDia <= diasDisponibles.size()) {
            return diasDisponibles.get(opcionDia - 1);
        } else {
            System.out.println("Opción de día no válida.");
            return null;
        }
    }


    // Permite seleccionar una hora de inicio disponible para una cancha en una fecha dada.
    public LocalTime seleccionarHoraInicio(Cancha cancha, LocalDate fecha) {
        System.out.println("Seleccione hora de inicio disponible:");
        List<LocalTime> todasLasHoras = new ArrayList<>();
        // Las horas de reserva van de 12:00 a 21:00
        for (int i = 12; i <= 21; i++) {
            todasLasHoras.add(LocalTime.of(i, 0));
        }

        // Obtener la hora actual del sistema
        LocalTime horaActual = LocalTime.now();
        // Obtener la fecha actual del sistema
        LocalDate fechaActual = LocalDate.now();

        List<LocalTime> horasDisponibles = todasLasHoras.stream()
                .filter(hora -> {
                    // Si la fecha seleccionada es hoy, filtramos las horas que ya pasaron
                    if (fecha.isEqual(fechaActual)) {
                        // Solo mostramos horas que son IGUAL o POSTERIORES a la siguiente hora completa
                        // Ejemplo: Si son 18:27, la siguiente hora completa es 19:00.
                        // Entonces, hora.getHour() debe ser >= horaActual.getHour() + 1
                        return hora.getHour() >= horaActual.getHour() + 1 && !isHoraOcupada(cancha, fecha, hora);
                    } else {
                        // Para cualquier otra fecha (futura), no aplicamos este filtro de hora pasada
                        return !isHoraOcupada(cancha, fecha, hora);
                    }
                })
                .collect(Collectors.toList());

        if (horasDisponibles.isEmpty()) {
            System.out.println("No hay horas de inicio disponibles para esta cancha y fecha en los horarios válidos.");
            return null;
        }

        for (int i = 0; i < horasDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + horasDisponibles.get(i));
        }
        System.out.print("Elija una opción: ");
        int opcionHora;
        try {
            opcionHora = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para la hora de inicio.");
            scanner.nextLine();
            return null;
        }

        if (opcionHora > 0 && opcionHora <= horasDisponibles.size()) {
            return horasDisponibles.get(opcionHora - 1);
        } else {
            System.out.println("Opción de hora de inicio no válida.");
            return null;
        }
    }

    // Permite seleccionar una hora de fin válida para una reserva.
    public LocalTime seleccionarHoraFin(Cancha cancha, LocalDate fecha, LocalTime horaInicio) {
        System.out.println("Seleccione hora de fin disponible:");
        List<LocalTime> todasLasHorasFin = new ArrayList<>();
        for (int i = horaInicio.getHour() + 1; i <= 21; i++) {
            todasLasHorasFin.add(LocalTime.of(i, 0));
        }

        List<LocalTime> horasDisponiblesFin = todasLasHorasFin.stream()
                .filter(horaFin -> !existeSolapamiento(cancha, fecha, horaInicio, horaFin))
                .collect(Collectors.toList());

        if (horasDisponiblesFin.isEmpty()) {
            System.out.println("No hay horas de fin disponibles después de " + horaInicio + " para esta cancha y fecha.");
            return null;
        }

        for (int i = 0; i < horasDisponiblesFin.size(); i++) {
            System.out.println((i + 1) + ". " + horasDisponiblesFin.get(i));
        }
        System.out.print("Elija una opción: ");
        int opcionHora;
        try {
            opcionHora = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para la hora de fin.");
            scanner.nextLine();
            return null;
        }

        if (opcionHora > 0 && opcionHora <= horasDisponiblesFin.size()) {
            return horasDisponiblesFin.get(opcionHora - 1);
        } else {
            System.out.println("Opción de hora de fin no válida.");
            return null;
        }
    }
}