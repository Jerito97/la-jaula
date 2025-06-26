import clases.Cancha;
import clases.Reserva;
import clases.Usuario;
import clases.Pago;

import clases.GestorUsuarios;
import clases.GestorCanchas;
import clases.GestorReservas;
import clases.GestorPagos;
import clases.ConexionBD;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    // Declaración de los gestores principales
    private static GestorUsuarios gestorUsuarios;
    private static GestorCanchas gestorCanchas;
    private static GestorReservas gestorReservas;
    private static GestorPagos gestorPagos;

    public static void main(String[] args) {
        // Intentar establecer conexión a la base de datos al iniciar el sistema
        try (Connection conn = ConexionBD.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión a la base de datos 'lajaula' exitosa al iniciar.");
            } else {
                // Si la conexión falla, se imprime un mensaje y se termina el programa
                System.err.println("No se pudo establecer conexión a la base de datos al iniciar.");
                System.err.println("Por favor, verifica tu servidor MySQL y las credenciales en ConexionBD.java.");
                System.exit(1);
            }
        } catch (SQLException e) {
            // Captura de errores SQL al intentar conectar
            System.err.println("Error SQL al probar la conexión: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Inicialización de los gestores con sus respectivas dependencias
        gestorUsuarios = new GestorUsuarios(scanner);
        gestorCanchas = new GestorCanchas(scanner);
        gestorReservas = new GestorReservas(gestorUsuarios, gestorCanchas, scanner);
        gestorPagos = new GestorPagos(gestorReservas, scanner);

        // Llamada al menú principal del sistema
        runSistemaLaJaula(args);
    }

    // Método principal del sistema con menú de navegación
    public static void runSistemaLaJaula(String[] args) {
        int opcionPrincipal;

        do {
            // Menú principal
            System.out.println("--- Sistema La Jaula ---");
            System.out.println("1. Gestión de Usuarios");
            System.out.println("2. Gestión de Canchas");
            System.out.println("3. Gestión de Reservas");
            System.out.println("4. Gestión de Pagos");
            System.out.println("5. Salir");
            System.out.print("Elija una opción: ");

            try {
                // Leer opción del usuario
                opcionPrincipal = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
            } catch (InputMismatchException e) {
                // Manejo de entrada no numérica
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpiar buffer
                opcionPrincipal = 0; // Valor inválido para que repita el menú
                continue;
            }

            // Lógica de selección de opción
            switch (opcionPrincipal) {
                case 1:
                    gestorUsuarios.gestionar(); // Accede al menú de usuarios
                    break;
                case 2:
                    gestorCanchas.gestionar(); // Accede al menú de canchas
                    break;
                case 3:
                    gestorReservas.gestionar(); // Accede al menú de reservas
                    break;
                case 4:
                    gestorPagos.gestionar(); // Accede al menú de pagos
                    break;
                case 5:
                    System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
            System.out.println();
        } while (opcionPrincipal != 5); // Repite hasta que el usuario elija salir

        // Cierre del scanner al terminar el sistema
        scanner.close();
    }
}
