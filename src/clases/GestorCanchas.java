package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GestorCanchas {
    private Scanner scanner;

    public GestorCanchas(Scanner scanner) {
        this.scanner = scanner;
    }

    // Menú para la gestión de canchas
    public void gestionar() {
        int opcionCanchas;
        do {
            System.out.println("\n--- Gestión de Canchas ---");
            System.out.println("1. Listar Canchas");
            System.out.println("2. Agregar Nueva Cancha");
            System.out.println("3. Modificar Cancha Existente");
            System.out.println("4. Eliminar Cancha");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Elija una opción: ");

            try {
                opcionCanchas = scanner.nextInt();
                scanner.nextLine(); // Limpieza de buffer
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpieza de entrada inválida
                opcionCanchas = 0;
                continue;
            }

            // Llamada a métodos según la opción seleccionada
            switch (opcionCanchas) {
                case 1: listarCanchas(); break;
                case 2: agregarCancha(); break;
                case 3: modificarCancha(); break;
                case 4: eliminarCancha(); break;
                case 5: System.out.println("Volviendo al Menú Principal de La Jaula..."); break;
                default: System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionCanchas != 5);
    }

    // Lista todas las canchas desde la base de datos
    public void listarCanchas() {
        System.out.println("\n--- Listado de Canchas ---");
        List<Cancha> canchas = obtenerTodasLasCanchas(); // Obtener canchas de la BD
        if (canchas.isEmpty()) {
            System.out.println("No hay canchas registradas en el sistema.");
            return;
        }
        for (Cancha cancha : canchas) {
            System.out.println(cancha); // toString() de Cancha
        }
    }

    // Permite ingresar los datos de una nueva cancha y guardarla en la BD
    public void agregarCancha() {
        System.out.println("\n--- Agregar Nueva Cancha ---");
        System.out.print("Ingrese nombre de la cancha: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese tipo de cancha (ej. Fútbol 5, Pádel): ");
        String tipoCancha = scanner.nextLine();

        System.out.print("Ingrese precio por hora: ");
        double precioPorHora;
        try {
            precioPorHora = scanner.nextDouble();
            scanner.nextLine(); // Limpieza del buffer
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida para el precio. Debe ser un número.");
            scanner.nextLine();
            return;
        }

        // Inserta la cancha en la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO cancha (nombre, tipoCancha, precioPorHora) VALUES (?, ?, ?)")) {
            stmt.setString(1, nombre);
            stmt.setString(2, tipoCancha);
            stmt.setDouble(3, precioPorHora);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cancha '" + nombre + "' de " + tipoCancha + " agregada con éxito.");
            } else {
                System.out.println("No se pudo agregar la cancha.");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar cancha: " + e.getMessage());
        }
    }

    // Modifica los datos de una cancha ya existente, identificada por su ID
    public void modificarCancha() {
        System.out.println("\n--- Modificar Cancha ---");
        List<Cancha> canchas = obtenerTodasLasCanchas();
        if (canchas.isEmpty()) {
            System.out.println("No hay canchas para modificar.");
            return;
        }

        listarCanchas(); // Mostrar todas las canchas con ID
        System.out.print("Ingrese el ID de la cancha a modificar: ");
        int idModificar;
        try {
            idModificar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Cancha canchaModificar = buscarCanchaPorId(idModificar); // Buscar cancha en BD
        if (canchaModificar == null) {
            System.out.println("Cancha con ID " + idModificar + " no encontrada.");
            return;
        }

        // Solicitar nuevos datos (se pueden dejar en blanco para mantener el valor actual)
        System.out.println("Cancha actual: " + canchaModificar);
        System.out.println("Deje en blanco para mantener el valor actual.");

        System.out.print("Nuevo nombre (" + canchaModificar.getNombre() + "): ");
        String nuevoNombre = scanner.nextLine();
        if (!nuevoNombre.isEmpty()) canchaModificar.setNombre(nuevoNombre);

        System.out.print("Nuevo tipo de cancha (" + canchaModificar.getTipoCancha() + "): ");
        String nuevoTipoCancha = scanner.nextLine();
        if (!nuevoTipoCancha.isEmpty()) canchaModificar.setTipoCancha(nuevoTipoCancha);

        System.out.print("Nuevo precio por hora (" + String.format("%.2f", canchaModificar.getPrecioPorHora()) + "): ");
        String precioStr = scanner.nextLine();
        if (!precioStr.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(precioStr);
                canchaModificar.setPrecioPorHora(nuevoPrecio);
            } catch (NumberFormatException e) {
                System.out.println("Precio no válido. Se mantendrá el precio actual.");
            }
        }

        // Actualizar la cancha en la BD
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE cancha SET nombre = ?, tipoCancha = ?, precioPorHora = ? WHERE idCancha = ?")) {
            stmt.setString(1, canchaModificar.getNombre());
            stmt.setString(2, canchaModificar.getTipoCancha());
            stmt.setDouble(3, canchaModificar.getPrecioPorHora());
            stmt.setInt(4, canchaModificar.getIdCancha());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cancha modificada con éxito: " + canchaModificar);
            } else {
                System.out.println("No se pudo modificar la cancha.");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar cancha: " + e.getMessage());
        }
    }

    // Elimina una cancha por ID, con verificación de integridad referencial
    public void eliminarCancha() {
        System.out.println("\n--- Eliminar Cancha ---");
        List<Cancha> canchas = obtenerTodasLasCanchas();
        if (canchas.isEmpty()) {
            System.out.println("No hay canchas para eliminar.");
            return;
        }

        listarCanchas();
        System.out.print("Ingrese el ID de la cancha a eliminar: ");
        int idEliminar;
        try {
            idEliminar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Cancha canchaEliminar = buscarCanchaPorId(idEliminar);
        if (canchaEliminar == null) {
            System.out.println("Cancha con ID " + idEliminar + " no encontrada.");
            return;
        }

        // Eliminar cancha
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cancha WHERE idCancha = ?")) {
            stmt.setInt(1, idEliminar);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cancha '" + canchaEliminar.getNombre() + "' (ID: " + idEliminar + ") eliminada con éxito.");
            } else {
                System.out.println("No se pudo eliminar la cancha.");
            }
        } catch (SQLException e) {
            // Control de error específico por restricción de clave foránea
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Error: No se puede eliminar la cancha " + idEliminar + " porque tiene reservas asociadas.");
                System.out.println("Primero elimine las reservas de esta cancha.");
            } else {
                System.err.println("Error al eliminar cancha: " + e.getMessage());
            }
        }
    }

    // Devuelve una lista con todas las canchas desde la BD
    public List<Cancha> obtenerTodasLasCanchas() {
        List<Cancha> canchas = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cancha");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                canchas.add(new Cancha(
                        rs.getInt("idCancha"),
                        rs.getString("nombre"),
                        rs.getString("tipoCancha"),
                        rs.getDouble("precioPorHora")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener canchas: " + e.getMessage());
        }
        return canchas;
    }

    // Muestra un menú de canchas disponibles y permite seleccionar una
    public Cancha seleccionarCancha() {
        List<Cancha> canchas = obtenerTodasLasCanchas();
        if (canchas.isEmpty()) {
            System.out.println("No hay canchas para seleccionar.");
            return null;
        }
        System.out.println("Seleccione la cancha:");
        for (int i = 0; i < canchas.size(); i++) {
            System.out.println((i + 1) + ". " + canchas.get(i).getNombre() + " (" + canchas.get(i).getTipoCancha() + ", $" + String.format("%.2f", canchas.get(i).getPrecioPorHora()) + "/hora)");
        }

        System.out.print("Elija una opción: ");
        int opcionCancha;
        try {
            opcionCancha = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para la cancha.");
            scanner.nextLine();
            return null;
        }

        if (opcionCancha > 0 && opcionCancha <= canchas.size()) {
            return canchas.get(opcionCancha - 1);
        } else {
            System.out.println("Opción de cancha no válida.");
            return null;
        }
    }

    // Busca y retorna una cancha específica por su ID desde la BD
    public Cancha buscarCanchaPorId(int id) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cancha WHERE idCancha = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cancha(
                            rs.getInt("idCancha"),
                            rs.getString("nombre"),
                            rs.getString("tipoCancha"),
                            rs.getDouble("precioPorHora")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cancha por ID: " + e.getMessage());
        }
        return null;
    }
}
