## Sistema de Reservas - La Jaula
Este proyecto implementa un sistema de gestión de reservas completo y eficiente para el complejo deportivo "La Jaula". Desarrollado en Java, permite una administración ágil de usuarios, la configuración detallada de canchas (fútbol y pádel), y un control preciso sobre las reservas, incluyendo la gestión de sus diferentes estados de pago.

## Contenido del Proyecto
### /database

Este directorio agrupa todos los scripts SQL necesarios para la configuración y la gestión de la base de datos:
- creacion_tablas.sql: Script esencial para crear la estructura de todas las tablas de la base de datos.
- inserciones.sql: Contiene datos iniciales de ejemplo para poblar las tablas, ideal para facilitar pruebas y un inicio rápido del sistema.
- consultas.sql: Un conjunto de consultas SQL útiles para realizar operaciones comunes y diagnósticos sobre los datos del sistema.
- eliminacion.sql: Script para eliminar las tablas de la base de datos, muy práctico para reiniciar el esquema o en entornos de desarrollo.

### /src/clases

Aquí residen los archivos Java que definen la lógica y la estructura fundamental del sistema:
- ConexionBD.java: La clase fundamental que establece y gestiona la conexión con la base de datos. Es el puente vital para que la aplicación pueda interactuar y persistir los datos.
- Usuario.java: Modela la entidad del usuario. Define su estructura de datos (ID, nombre, email, rol, etc.) y los comportamientos básicos de cada usuario en el sistema.
- Cancha.java: Representa la entidad de una cancha. Incluye atributos como el ID, nombre, tipo de cancha (fútbol 5, pádel) y el precio por hora, elementos clave para la gestión de las instalaciones.
- Reserva.java: Define la entidad de una reserva. Encapsula toda la información de una reserva específica, como la fecha, horarios, estado, y las relaciones directas con el usuario y la cancha involucrados.
- GestorUsuarios.java: Actúa como el controlador principal para la lógica de negocio de los usuarios. Maneja todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) relacionadas con la administración de usuarios, apoyándose en ConexionBD.
- GestorCanchas.java: Es el controlador dedicado a la gestión de las canchas. Permite realizar todas las operaciones CRUD sobre las canchas, facilitando la adición, listado, modificación y eliminación de las instalaciones deportivas.
- GestorReservas.java: La clase más compleja y central del sistema, ya que coordina toda la lógica de negocio relacionada con las reservas. Se encarga de la creación, modificación, cancelación y consulta de reservas. Implementa lógica crucial como la validación de disponibilidad (para evitar solapamientos) y el manejo de los diferentes estados de la reserva (PENDIENTE_PAGO, CONFIRMADA, PAGADA, CANCELADA, y el nuevo estado PAGO_PARCIAL para los pagos a cuenta).
- Main.java: La clase principal y punto de entrada de la aplicación. Desde aquí se inicializan todos los componentes clave del sistema (conexión a DB, gestores) y se presenta el menú principal al usuario para que pueda interactuar con el sistema.

## Requisitos del Sistema
Para la correcta ejecución y funcionamiento del sistema, son necesarios los siguientes componentes:
- MySQL Workbench o cualquier cliente compatible con MySQL.
- Servidor MySQL (local o remoto).
- Java Development Kit (JDK) 8 o superior.
- Driver JDBC para MySQL.
