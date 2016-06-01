package grp02.udea.edu.co.wakeupnow.view;

/**
 * Created by Esteban on 12/04/2016.
 */
public class ItemAlarma {

    public static final String ACTIVADA = "ACTIVADA";
    public static final String DESACTIVADA = "DESACTIVADA";
    public static final String AM = "a.m";
    public static final String PM = "p.m";

    private String horaAlarma;
    private String minutoAlarma;
    private boolean esPM;
    private boolean estaActiva;
    private int idItem;

    public ItemAlarma() {
        esPM = false;
        estaActiva = true;
    }

    public String getHoraAlarma() {
        return horaAlarma;
    }

    public void setHoraAlarma(int horaAlarma) {
        if(horaAlarma<9){
            if (horaAlarma==0){
                this.horaAlarma = "12";
            }else {
                this.horaAlarma = "0" + horaAlarma;
            }
        } else {
            this.horaAlarma = String.valueOf(horaAlarma);
        }
    }

    public String getMinutoAlarma() {
        return minutoAlarma;
    }

    public void setMinutoAlarma(int minutoAlarma) {
        if(minutoAlarma<9){
            this.minutoAlarma = "0"+minutoAlarma;
        } else {
            this.minutoAlarma = String.valueOf(minutoAlarma);
        }
    }

    public boolean isEsPM() {
        return esPM;
    }

    public void setEsPM(boolean esPM) {
        this.esPM = esPM;
    }

    public boolean isEstaActiva() {
        return estaActiva;
    }

    public void setEstaActiva(boolean estaActiva) {
        this.estaActiva = estaActiva;
    }

    public String getAlarma(){
        return horaAlarma+":"+minutoAlarma;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }
}
