package grp02.udea.edu.co.wakeupnow.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Locale;
import grp02.udea.edu.co.wakeupnow.modelo.Alarma;

/**
 * Created by Pc1 on 22/03/2016.
 */
public class AlarmaDAO extends BDAppRunDAO{

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public AlarmaDAO(Context context) {
        super(context);
    }

    public long guardarAlarma(Alarma alarma){
        ContentValues values=new ContentValues();
        //int idDia=alarma.getDia().getIdDia();
        int vibracion=0;
        int activada=0;
        if(alarma.isVibracion()){
            vibracion=1
;        }
        if (alarma.isActivada()){
            activada=1;
        }
        values.put(DataBaseHelper.ID_ALARMA_COLUMNA,alarma.getIdAlarma());
        values.put(DataBaseHelper.NOMBRE_ALARMA_COLUMNA,"StringCualquiera");
        values.put(DataBaseHelper.FECHA_ALARMA_COLUMNA,formatter.format(alarma.getFecha()));
        values.put(DataBaseHelper.ID_DIA_ALARMA_COLUMNA,0);
        values.put(DataBaseHelper.VIBRACION_ALARMA_COLUMNA,false);
        values.put(DataBaseHelper.ACTIVADA_ALARMA_COLUMNA,activada);
        return getDb().insert(DataBaseHelper.TABLA_ALARMA,null,values);

    }


    public int eliminarAlarma(Alarma alarma){
        String nomAlarma=alarma.getNombre();
        return getDb().delete(DataBaseHelper.TABLA_ALARMA, DataBaseHelper.NOMBRE_ALARMA_COLUMNA + "=?",
                new String[]{nomAlarma});

    }

    public Cursor getTodasLasAlarmas() {
        String[] columnas = new String[]{DataBaseHelper.ID_ALARMA_COLUMNA,DataBaseHelper.NOMBRE_ALARMA_COLUMNA ,
                DataBaseHelper.FECHA_ALARMA_COLUMNA,DataBaseHelper.ACTIVADA_ALARMA_COLUMNA};
        return getDb().query(DataBaseHelper.TABLA_ALARMA, columnas, null, null, null, null, null);

    }

    public int getIdUltimoRegistro(){
        Cursor c=getTodasLasAlarmas();
        if(c.moveToLast()){
            return c.getInt(0);
        }else{
            return -1;
        }
    }
}
