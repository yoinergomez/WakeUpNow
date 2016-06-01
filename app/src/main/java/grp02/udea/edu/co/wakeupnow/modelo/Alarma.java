package grp02.udea.edu.co.wakeupnow.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pc1 on 22/03/2016.
 */
public class Alarma {

    private int idAlarma;
    private String nombre;
    private Date fecha;
    private Dia dia;
    private boolean vibracion;
    private boolean activada;

    public Alarma(){}

    public int getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(int idAlarma) {
        this.idAlarma = idAlarma;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Dia getDia() {
        return dia;
    }

    public void setIdDia(Dia dia) {
        this.dia = dia;
    }

    public boolean isVibracion() {
        return vibracion;
    }

    public void setVibracion(boolean vibracion) {
        this.vibracion = vibracion;
    }

    public boolean isActivada() {
        return activada;
    }

    public void setActivada(boolean activada) {
        this.activada = activada;
    }
    /*
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    */
    public Date getFecha() {
        return fecha;
    }



}
