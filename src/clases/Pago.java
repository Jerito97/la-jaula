package clases;

import java.time.LocalDate;

public class Pago {
    private int idPago;
    private Reserva reserva;
    private double monto;
    private LocalDate fechaPago;
    private String metodoPago;
    private String estado;

    public Pago(int idPago, Reserva reserva, double monto, LocalDate fechaPago, String metodoPago, String estado) {
        this.idPago = idPago;
        this.reserva = reserva;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    // Retorna el ID del pago
    public int getIdPago() { return idPago; }

    // Modifica el ID del pago
    public void setIdPago(int idPago) { this.idPago = idPago; }

    // Retorna la reserva asociada al pago
    public Reserva getReserva() { return reserva; }

    // Modifica la reserva asociada al pago
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    // Retorna el monto pagado
    public double getMonto() { return monto; }

    // Modifica el monto pagado
    public void setMonto(double monto) { this.monto = monto; }

    // Retorna la fecha del pago
    public LocalDate getFechaPago() { return fechaPago; }

    // Modifica la fecha del pago
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    // Retorna el método de pago usado
    public String getMetodoPago() { return metodoPago; }

    // Modifica el método de pago usado
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    // Retorna el estado actual del pago
    public String getEstado() { return estado; }

    // Modifica el estado del pago
    public void setEstado(String estado) { this.estado = estado; }

    // Devuelve una cadena con la info básica del pago formateada
    @Override
    public String toString() {
        return "ID Pago: " + idPago +
                ", Reserva ID: " + (reserva != null ? reserva.getIdReserva() : "N/A") +
                ", Monto: $" + String.format("%.2f", monto) +
                ", Fecha Pago: " + fechaPago +
                ", Método: " + metodoPago +
                ", Estado: " + estado;
    }
}
