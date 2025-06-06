package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GestorUsuarios {
    private Scanner scanner;
    public GestorUsuarios(Scanner scanner) {
        this.scanner = scanner;
    }

    // Menú principal para gestionar usuarios
    public void gestionar() {
        int opcionUsuarios;
        do {
            System.out.println("\n--- Gestión de Usuarios ---");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Agregar Nuevo Usuario");
            System.out.println("3. Modificar Usuario Existente");
            System.out.println("4. Eliminar Usuario");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Elija una opción: ");

            try {
                opcionUsuarios = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
                opcionUsuarios = 0;
                continue;
            }

            switch (opcionUsuarios) {
                case 1:
                    listarUsuarios(); // Mostrar todos los usuarios
                    break;
                case 2:
                    agregarUsuario(); // Crear un nuevo usuario
                    break;
                case 3:
                    modificarUsuario(); // Modificar usuario existente
                    break;
                case 4:
                    eliminarUsuario(); // Eliminar usuario
                    break;
                case 5:
                    System.out.println("Volviendo al Menú Principal de La Jaula...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionUsuarios != 5);
    }

    // Mostrar todos los usuarios registrados en la base de datos
    public void listarUsuarios() {
        System.out.println("\n--- Listado de Usuarios ---");
        List<Usuario> usuarios = obtenerTodosLosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados en el sistema.");
            return;
        }
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    // Permite ingresar los datos para crear un nuevo usuario
    public void agregarUsuario() {
        System.out.println("\n--- Agregar Nuevo Usuario ---");
        System.out.print("Ingrese nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();
        System.out.print("Ingrese rol (CLIENTE/ADMIN): ");
        String rol = scanner.nextLine().toUpperCase();

        // Inserta el nuevo usuario en la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO usuario (nombre, apellido, email, password, rol) VALUES (?, ?, ?, ?, ?)"
             )) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, rol);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario '" + nombre + " " + apellido + "' agregado con éxito.");
            } else {
                System.out.println("No se pudo agregar el usuario.");
            }
        } catch (SQLException e) {
            // Error 23xxx suelen ser restricciones (como email duplicado)
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Error: El email '" + email + "' ya está registrado. Por favor, use otro.");
            } else {
                System.err.println("Error al agregar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Permite modificar un usuario existente
    public void modificarUsuario() {
        System.out.println("\n--- Modificar Usuario ---");
        List<Usuario> usuarios = obtenerTodosLosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para modificar.");
            return;
        }

        listarUsuarios();
        System.out.print("Ingrese el ID del usuario a modificar: ");
        int idModificar;
        try {
            idModificar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Usuario usuarioModificar = buscarUsuarioPorId(idModificar);
        if (usuarioModificar == null) {
            System.out.println("Usuario con ID " + idModificar + " no encontrado.");
            return;
        }

        System.out.println("Usuario actual: " + usuarioModificar);
        System.out.println("Deje en blanco para mantener el valor actual.");

        // Pedir nuevos valores, o dejar los actuales
        System.out.print("Nuevo nombre (" + usuarioModificar.getNombre() + "): ");
        String nuevoNombre = scanner.nextLine();
        if (!nuevoNombre.isEmpty()) usuarioModificar.setNombre(nuevoNombre);

        System.out.print("Nuevo apellido (" + usuarioModificar.getApellido() + "): ");
        String nuevoApellido = scanner.nextLine();
        if (!nuevoApellido.isEmpty()) usuarioModificar.setApellido(nuevoApellido);

        System.out.print("Nuevo email (" + usuarioModificar.getEmail() + "): ");
        String nuevoEmail = scanner.nextLine();
        if (!nuevoEmail.isEmpty()) usuarioModificar.setEmail(nuevoEmail);

        System.out.print("Nueva contraseña: ");
        String nuevaPassword = scanner.nextLine();
        if (!nuevaPassword.isEmpty()) usuarioModificar.setPassword(nuevaPassword);

        System.out.print("Nuevo rol (CLIENTE/ADMIN) (" + usuarioModificar.getRol() + "): ");
        String nuevoRol = scanner.nextLine().toUpperCase();
        if (!nuevoRol.isEmpty() && (nuevoRol.equals("CLIENTE") || nuevoRol.equals("ADMIN"))) {
            usuarioModificar.setRol(nuevoRol);
        } else if (!nuevoRol.isEmpty()) {
            System.out.println("Rol no válido. Se mantendrá el rol actual.");
        }

        // Guardar los cambios en la base de datos
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE usuario SET nombre = ?, apellido = ?, email = ?, password = ?, rol = ? WHERE idUsuario = ?"
             )) {
            stmt.setString(1, usuarioModificar.getNombre());
            stmt.setString(2, usuarioModificar.getApellido());
            stmt.setString(3, usuarioModificar.getEmail());
            stmt.setString(4, usuarioModificar.getPassword());
            stmt.setString(5, usuarioModificar.getRol());
            stmt.setInt(6, usuarioModificar.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario modificado con éxito: " + usuarioModificar);
            } else {
                System.out.println("No se pudo modificar el usuario.");
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Error: El email '" + usuarioModificar.getEmail() + "' ya está registrado por otro usuario.");
            } else {
                System.err.println("Error al modificar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Permite eliminar un usuario de la base de datos
    public void eliminarUsuario() {
        System.out.println("\n--- Eliminar Usuario ---");
        List<Usuario> usuarios = obtenerTodosLosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para eliminar.");
            return;
        }

        listarUsuarios();
        System.out.print("Ingrese el ID del usuario a eliminar: ");
        int idEliminar;
        try {
            idEliminar = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el ID.");
            scanner.nextLine();
            return;
        }

        Usuario usuarioEliminar = buscarUsuarioPorId(idEliminar);
        if (usuarioEliminar == null) {
            System.out.println("Usuario con ID " + idEliminar + " no encontrado.");
            return;
        }

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM usuario WHERE idUsuario = ?")) {
            stmt.setInt(1, idEliminar);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Usuario '" + usuarioEliminar.getNombre() + " " + usuarioEliminar.getApellido() + "' (ID: " + idEliminar + ") eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el usuario.");
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Error: No se puede eliminar el usuario " + idEliminar + " porque tiene reservas o pagos asociados.");
            } else {
                System.err.println("Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Recupera todos los usuarios desde la base de datos
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuario");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    // Permite al usuario seleccionar un usuario de una lista
    public Usuario seleccionarUsuario() {
        List<Usuario> usuarios = obtenerTodosLosUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para seleccionar.");
            return null;
        }
        System.out.println("Seleccione el usuario:");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ". " + usuarios.get(i).getNombre() + " " + usuarios.get(i).getApellido() + " (ID: " + usuarios.get(i).getId() + ")");
        }
        System.out.print("Elija una opción: ");
        int opcionUsuario;
        try {
            opcionUsuario = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número para el usuario.");
            scanner.nextLine();
            return null;
        }

        if (opcionUsuario > 0 && opcionUsuario <= usuarios.size()) {
            return usuarios.get(opcionUsuario - 1);
        } else {
            System.out.println("Opción de usuario no válida.");
            return null;
        }
    }

    // Busca un usuario por su ID en la base de datos
    public Usuario buscarUsuarioPorId(int id) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuario WHERE idUsuario = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
