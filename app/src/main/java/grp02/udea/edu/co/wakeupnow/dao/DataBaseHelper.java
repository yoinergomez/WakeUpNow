package grp02.udea.edu.co.wakeupnow.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pc1 on 22/03/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static  final String BD_NOMBRE = "BD_WAkeUpNow.sql";
    private static  final int BD_VERSION_ESQUEMA = 1;

    public static final String TABLA_DIA = "Dia";
    public static final String TABLA_ALARMA = "Alarma";

    //campos de la la tabla Dia
    public static final String ID_DIA_COLUMNA = "_id";
    public static final String NOMBRE_DIA_COLUMNA = "nombre";

    //campos de la la tabla Alarma
    public static final String ID_ALARMA_COLUMNA = "_id";
    public static final String NOMBRE_ALARMA_COLUMNA = "nombre";
    public static final String FECHA_ALARMA_COLUMNA = "fecha";
    public static final String ID_DIA_ALARMA_COLUMNA = "id_Dia";
    public static final String VIBRACION_ALARMA_COLUMNA = "vibracion";
    public static final String ACTIVADA_ALARMA_COLUMNA= "activada";



    public static final String CREAR_TABLA_DIA ="CREATE TABLE "
            + TABLA_DIA + "(" + ID_DIA_COLUMNA + " INTEGER PRIMARY KEY, "
            + NOMBRE_DIA_COLUMNA + " TEXT NOT NULL );";

    public static final String CREAR_TABLA_ALARMA="CREATE TABLE "
            + TABLA_ALARMA + "(" + ID_ALARMA_COLUMNA + " INTEGER PRIMARY KEY, "
            + NOMBRE_ALARMA_COLUMNA + " TEXT, " + FECHA_ALARMA_COLUMNA + " DATE NOT NULL, "
            + ID_DIA_ALARMA_COLUMNA + " INTEGER, " + VIBRACION_ALARMA_COLUMNA + " INTEGER, "
            + ACTIVADA_ALARMA_COLUMNA + " INTEGER );";





    public DataBaseHelper(Context context) {
        super(context, BD_NOMBRE, null, BD_VERSION_ESQUEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREAR_TABLA_DIA);
        db.execSQL(CREAR_TABLA_ALARMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
