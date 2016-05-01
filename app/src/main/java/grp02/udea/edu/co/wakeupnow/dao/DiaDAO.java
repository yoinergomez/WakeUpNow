package grp02.udea.edu.co.wakeupnow.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import grp02.udea.edu.co.wakeupnow.modelo.Dia;

/**
 * Created by Pc1 on 22/03/2016.
 */
public class DiaDAO extends BDAppRunDAO {

    public DiaDAO(Context context) {
        super(context);
    }

    public long guardarDia(Dia dia){
        Log.d("@@", DataBaseHelper.CREAR_TABLA_DIA);
        ContentValues values=new ContentValues();
        values.put(DataBaseHelper.NOMBRE_DIA_COLUMNA,dia.getNombre());
        return getDb().insert(DataBaseHelper.TABLA_DIA,null,values);


    }


    public Cursor getTodasLosDias() {
        String[] columnas = new String[]{DataBaseHelper.ID_DIA_COLUMNA,DataBaseHelper.NOMBRE_DIA_COLUMNA};
        return getDb().query(DataBaseHelper.TABLA_DIA, columnas, null, null, null, null, null);

    }
}
