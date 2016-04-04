package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.italkyou.beans.BeanTelefono;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RenzoD on 09/06/2015.
 */
public class TelephoneDAO {

    private static final String TAG = TelephoneDAO.class.getSimpleName();
    private static final String TELEFONO = "TBL_TELEFONO";

    private static final String SELECT_IF_EXIST = "SELECT * FROM "
            + TELEFONO + " WHERE (LOOKUP_KEY = ? AND NUMERO = ?);";

    private static final String SELECT_WHERE_LOOKUP_KEY_ANNEX = "SELECT * FROM "
            + TELEFONO
            + " WHERE (LOOKUP_KEY = ? AND ANEXO IS NOT NULL AND ANEXO <> '')";

    private static final String SELECT_WHERE_LOOKUPKEY = "SELECT * FROM "
            + TELEFONO
            + " WHERE LOOKUP_KEY = ? GROUP BY NUMERO";

    //Index
    private static final int INDEX_ID = 0;
    private static final int INDEX_ID_CONTACTO = 1;
    private static final int INDEX_NOMBRE_ITY = 2;
    private static final int INDEX_LOOKUP_KEY = 3;
    private static final int INDEX_NUMERO = 4;
    private static final int INDEX_ANEXO = 5;
    private static final int INDEX_CONECTADO = 6;

    //Columns
    private static final String ID = "ID";
    private static final String ID_CONTACTO = "ID_CONTACTO";
    private static final String NOMBRE_ITY = "NOMBRE_ITY";
    private static final String LOOKUP_KEY = "LOOKUP_KEY";
    private static final String NUMERO = "NUMERO";
    private static final String ANEXO = "ANEXO";
    private static final String CONECTADO = "CONECTADO";
    private static Context _context;

    protected BaseDatosSQLiteHelper dbHelper = null;
    protected static SQLiteDatabase db;

    public TelephoneDAO(Context contexto, boolean isWritable) {
        openDatabase(contexto);
        initDatabase(isWritable);
    }

    private void openDatabase(Context context) {
        try {
            if (dbHelper == null) {
                dbHelper = new BaseDatosSQLiteHelper(context, TablasBD.BD_NOMBRE_ITY, null, TablasBD.BD_VERSION_ITY);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error en database ", ex);
        }

    }

    public int updateTelephone(String nroCelular, String lookUpKey, String conectado) {
        int row_affected;
        String whereClause = LOOKUP_KEY + " = ? and " + NUMERO + " like ?";

        ContentValues cv = new ContentValues();
        cv.put(NUMERO, nroCelular);
        cv.put(CONECTADO, conectado);
        row_affected = db.update(TELEFONO, cv, whereClause, new String[]{lookUpKey, nroCelular});
        return row_affected;
    }

    public int updateTelephoneAfterSync(String lookUpKey, String annex, String number, String ityName) {
//        initDatabase(_context);

        int row_affected;
        String whereClause = LOOKUP_KEY + " = ? and " + NUMERO + " like ?";

        ContentValues cv = new ContentValues();
        cv.put(ANEXO, annex);
        cv.put(NUMERO, number);
        cv.put(NOMBRE_ITY, ityName);
        row_affected = db.update(TELEFONO, cv, whereClause, new String[]{lookUpKey, number});
        return row_affected;
    }


    public int insertTelephone(String nroCelular, int idUsuario, String lookUpKey, String nroAnexo, String conectado) {

        int row_affected = 0;
        if (!isTelephoneExist(lookUpKey, nroCelular)) {

            ContentValues cv = new ContentValues();
            cv.put(ID_CONTACTO, idUsuario);
            cv.put(LOOKUP_KEY, lookUpKey);
            cv.put(NUMERO, nroCelular);
            cv.put(ANEXO, nroAnexo);
            cv.put(CONECTADO, conectado);

            row_affected = ((int) db.insert(TELEFONO, null, cv));

        }


        return row_affected;
    }

    public int delete(String lookUp_key) {
        String where = LOOKUP_KEY + " = ?";
        return db.delete(TELEFONO, where, new String[]{lookUp_key});
    }

    private void initDatabase(boolean isWritable) {

        try {
            if (db != null)
                if (db.isOpen())
                    return;

            if (isWritable) {
                db = dbHelper.getWritableDatabase();
            } else
                db = dbHelper.getReadableDatabase();

        } catch (NullPointerException ex) {

        }
    }

    public void closeDatabase() {

        try {

            if (db != null)
                if (db.isOpen())
                    db.close();

            if (dbHelper != null)
                dbHelper.close();


            db = null;
            dbHelper = null;

        } catch (NullPointerException np) {

        } catch (Exception ex) {

        }

    }

    /**
     * @return Una lista BeanTelefono con los los anexos
     */
    public List<Object> getTelephonesByKey(String lookUpKey) {
        List<Object> listObjects = new ArrayList<>();
        Cursor cur;
        cur = db.rawQuery(SELECT_WHERE_LOOKUPKEY, new String[]{lookUpKey});
        return loopTelephones(cur, listObjects);
    }


    private List<Object> loopTelephones(Cursor cur, List<Object> listObjects) {

        if (cur != null && cur.moveToFirst())
            do {

                BeanTelefono telefono = new BeanTelefono();
                telefono.setPosContacto(cur.getInt(INDEX_ID));
                telefono.setIdContacto(cur.getInt(INDEX_ID_CONTACTO));
                telefono.setNombreIty(cur.getString(INDEX_NOMBRE_ITY));
                telefono.setLookUpKey(cur.getString(INDEX_LOOKUP_KEY));
                telefono.setNumero(cur.getString(INDEX_NUMERO));
                telefono.setAnexo(cur.getString(INDEX_ANEXO));
                telefono.setConectado(cur.getString(INDEX_CONECTADO));
                listObjects.add(telefono);

            } while (cur.moveToNext());


        if (!cur.isClosed()) {
            cur.close();
        }

        return listObjects;
    }


    public boolean isTelephoneExist(String lookUpKey, String telephone) {
        boolean on = false;
        Cursor cur;
        try {
            cur = db.rawQuery(SELECT_IF_EXIST, new String[]{lookUpKey, telephone});
            if (cur.getCount() > 0) {
                on = true;
            } else
                on = false;

            if (cur != null)
                if (!cur.isClosed())
                    cur.close();

        } catch (Exception ex) {

        }


//        if (db.isOpen())
//            db.close();

        return on;
    }


}
