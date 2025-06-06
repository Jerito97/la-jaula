package clases;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol;

    public Usuario(int id, String nombre, String apellido, String email, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Retorna el ID del usuario
    public int getId() { return id; }

    // Modifica el ID del usuario
    public void setId(int id) { this.id = id; }

    // Retorna el nombre del usuario
    public String getNombre() { return nombre; }

    // Modifica el nombre del usuario
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Retorna el apellido del usuario
    public String getApellido() { return apellido; }

    // Modifica el apellido del usuario
    public void setApellido(String apellido) { this.apellido = apellido; }

    // Retorna el email del usuario
    public String getEmail() { return email; }

    // Modifica el email del usuario
    public void setEmail(String email) { this.email = email; }

    // Retorna la contraseña del usuario
    public String getPassword() { return password; }

    // Modifica la contraseña del usuario
    public void setPassword(String password) { this.password = password; }

    // Retorna el rol del usuario
    public String getRol() { return rol; }

    // Modifica el rol del usuario
    public void setRol(String rol) { this.rol = rol; }

    // Devuelve una cadena con la info básica del usuario (sin mostrar contraseña)
    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + " " + apellido + ", Email: " + email + ", Rol: " + rol;
    }
}
