package clases;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cancha {
    private int idCancha;
    private String nombre;
    private String tipoCancha;
    private double precioPorHora;

    public Cancha(int idCancha, String nombre, String tipoCancha, double precioPorHora) {
        this.idCancha = idCancha;
        this.nombre = nombre;
        this.tipoCancha = tipoCancha;
        this.precioPorHora = precioPorHora;
    }

    // Retorna el ID de la cancha
    public int getIdCancha() { return idCancha; }

    // Modifica el ID de la cancha
    public void setIdCancha(int idCancha) { this.idCancha = idCancha; }

    // Retorna el nombre de la cancha
    public String getNombre() { return nombre; }

    // Modifica el nombre de la cancha
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Retorna el tipo de cancha
    public String getTipoCancha() { return tipoCancha; }

    // Modifica el tipo de cancha
    public void setTipoCancha(String tipoCancha) { this.tipoCancha = tipoCancha; }

    // Retorna el precio por hora de la cancha
    public double getPrecioPorHora() { return precioPorHora; }

    // Modifica el precio por hora de la cancha
    public void setPrecioPorHora(double precioPorHora) { this.precioPorHora = precioPorHora; }

    // Devuelve una cadena con la información básica de la cancha formateada
    @Override
    public String toString() {
        return "ID: " + idCancha + ", Nombre: " + nombre + ", Tipo: " + tipoCancha + ", Precio/Hora: $" + String.format("%.2f", precioPorHora);
    }
}
