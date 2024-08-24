package persistencia;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;
    private Double monto;

    public Usuario(String nombre, Double monto) {
        this.nombre = nombre;
        this.monto = monto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", monto=" + monto +
                '}';
    }
}