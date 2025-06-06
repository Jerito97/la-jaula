package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // URL de conexión a la base de datos MySQL (nombre de la base: lajaula)
    private static final String URL = "jdbc:mysql://localhost:3306/lajaula";

    // Usuario y contraseña para acceder a la base de datos
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "0000";

    /**
     * Establece y devuelve una conexión a la base de datos MySQL.
     * @return un objeto Connection si la conexión es exitosa; null si ocurre un error.
     */
    public static Connection getConnection() {
        Connection conexion = null;
        try {
            // Intenta establecer la conexión usando los datos definidos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            // System.out.println("Conexión a la base de datos exitosa.");
        } catch (SQLException e) {
            // Si hay un error, lo muestra por consola
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        // Retorna la conexión (puede ser null si falló)
        return conexion;
    }

    /**
     * Cierra una conexión activa a la base de datos.
     * @param conexion el objeto Connection a cerrar.
     */
    public static void closeConnection(Connection conexion) {
        if (conexion != null) {
            try {
                // Intenta cerrar la conexión si no es null
                conexion.close();
            } catch (SQLException e) {
                // Muestra cualquier error que ocurra al cerrar
                System.err.println("Error al cerrar la conexión a la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
