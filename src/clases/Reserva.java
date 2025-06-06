package clases;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int idReserva;
    private Usuario usuario;
    private Cancha cancha;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;

    public Reserva(int idReserva, Usuario usuario, Cancha cancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estado) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    // Retorna el ID de la reserva
    public int getIdReserva() { return idReserva; }

    // Modifica el ID de la reserva
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    // Retorna el usuario que hizo la reserva
    public Usuario getUsuario() { return usuario; }

    // Modifica el usuario que hizo la reserva
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // Retorna la cancha reservada
    public Cancha getCancha() { return cancha; }

    // Modifica la cancha reservada
    public void setCancha(Cancha cancha) { this.cancha = cancha; }

    // Retorna la fecha de la reserva
    public LocalDate getFecha() { return fecha; }

    // Modifica la fecha de la reserva
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    // Retorna la hora de inicio de la reserva
    public LocalTime getHoraInicio() { return horaInicio; }

    // Modifica la hora de inicio de la reserva
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    // Retorna la hora de fin de la reserva
    public LocalTime getHoraFin() { return horaFin; }

    // Modifica la hora de fin de la reserva
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    // Retorna el estado actual de la reserva
    public String getEstado() { return estado; }

    // Modifica el estado de la reserva
    public void setEstado(String estado) { this.estado = estado; }

    // Devuelve una cadena con la info básica de la reserva formateada
    @Override
    public String toString() {
        return "ID: " + idReserva +
                ", Usuario: " + (usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "N/A") +
                ", Cancha: " + (cancha != null ? cancha.getNombre() : "N/A") +
                ", Fecha: " + fecha +
                ", Hora Inicio: " + horaInicio +
                ", Hora Fin: " + horaFin +
                ", Estado: " + estado;
    }
}
