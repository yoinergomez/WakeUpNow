package grp02.udea.edu.co.wakeupnow.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Pc1 on 22/03/2016.
 */
public class BDAppRunDAO {
    private DataBaseHelper helper;
    private SQLiteDatabase db;

    public BDAppRunDAO(Context context) {

        helper = new DataBaseHelper(context);
        abrir();
    }

    public void abrir(){
        db=helper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public DataBaseHelper getHelper() {
        return helper;
    }

    public void setHelper(DataBaseHelper helper) {
        this.helper = helper;
    }
}
