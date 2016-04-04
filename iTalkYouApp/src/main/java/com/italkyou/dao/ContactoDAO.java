package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanContactUser;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.controladores.LogicTelephone;
import com.italkyou.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class ContactoDAO {

    private static final String CONTACTO = "TBL_CONTACTO";
    private static final String CHAT_USER = "CHAT_USER";
    private static final String TAG = ContactoDAO.class.getSimpleName();
    private String TELEFONO = "TBL_TELEFONO";
    private String FAVORITO = "TBL_FAVORITO";
    TelephoneDAO daoTelephone;

    //Rows
    private static final String ID = "ID";
    private static final String ID_CONTACTO = "ID_CONTACTO";
    private static final String NOMBRE = "NOMBRE";
    private static final String NUMERO = "NUMERO";
    private static final String LOOKUP_KEY = "LOOKUP_KEY";
    private static final String FLAG_ITY = "FLAG_ITY";
    private static final String FLAG_FOTO = "FLAG_FOTO";
    private static final String FLAG_SINITY = "FLAG_SINITY";

    //index
    private static final int INDEX_ID_CONTACTO = 0;
    private static final int INDEX_NOMBRE = 1;
    private static final int INDEX_NUMERO = 2;
    private static final int INDEX_LOOKUP_KEY = 3;
    private static final int INDEX_FLAG_ITY = 4;
    private static final int INDEX_FLAG_FOTO = 5;
    private static final int INDEX_FLAG_SINITY = 6;

    private static final String SELECT_IF_EXIST = "SELECT * FROM "
            + CONTACTO + " WHERE LOOKUP_KEY = ?";

    private static Context _context;

    protected BaseDatosSQLiteHelper dbHelper = null;
    protected static SQLiteDatabase db;


    public ContactoDAO(Context contexto, boolean isWritable) {
        _context = contexto;
        openDatabase(contexto);
        initDatabase(isWritable);
    }

    /**
     * Metodo para insertar un contacto en la base de datos interna de la aplicacion.
     *
     * @param contacto, es la entidad del contacto
     *                  El campo FLAG_ITY identifica que contacto es de la red ITALKYOU
     *                  1 Es Miembro y tiene la aplicacion instalada
     *                  2 No es miembro.
     */
    public int insertarContacto(BeanContact contacto) {
        ContentValues valores = new ContentValues();
        int rows = 0;
        if (!isContactExist(String.valueOf(contacto.getLookUpKey()))) {

            if (contacto.getTelefono().length() > 0) {
                valores.put("ID_CONTACTO", contacto.getIdContacto());
                valores.put("NOMBRE", contacto.getNombre());
                valores.put("NUMERO", contacto.getTelefono());
                valores.put("FLAG_FOTO", contacto.getFoto());
                valores.put("FLAG_ITY", contacto.getUsuarioITY());
                valores.put("LOOKUP_KEY", contacto.getLookUpKey());
                //Indica que ya se hizo la verificacion de contacto con ITY
                // 0 no se ha verificado con ITY, 1 si se ha verificado con ITY
                valores.put("FLAG_SINITY", contacto.getFlagValidacion());

                long value = db.insert(CONTACTO, null, valores);
                rows = (int) value;
            }
        }

        return (rows);

        //LogApp.log("[CONTACTO DAO ]= se inserto contacto");
    }

    public int modificarContacto(BeanContact contacto) {
        ContentValues cv = new ContentValues();
        cv.put(ID_CONTACTO, contacto.getIdContacto());
        cv.put(NOMBRE, contacto.getNombre());
        cv.put(NUMERO, contacto.getTelefono());
        cv.put(FLAG_FOTO, contacto.getFoto());
        cv.put(FLAG_ITY, Const.NO_USER_ITY);
        cv.put(FLAG_SINITY, Const.NEED_SYNCHRONIZE);
        return db.update(CONTACTO, cv, "LOOKUP_KEY = ?", new String[]{contacto.getLookUpKey()});
    }

    public void actualizarListaContactosITalkYou(BeanContactUser bContactos, List<OutputContact> lstContactosITalkYou) {


        List<BeanTelefono> lstTelefonos = bContactos.getListaTelefonos();
        List<BeanContact> lstContactos = bContactos.getListaContactos();

        //Actualizo el flag (Optimizar esto para el futuro, bucles innecesarios)
        for (BeanContact bContacto : lstContactos)
            actualizarFlagContactoSincronizado(bContacto.getLookUpKey());

        for (BeanTelefono telefono : lstTelefonos) {
//            boolean guardarAnexo = false;

            for (OutputContact contactITY : lstContactosITalkYou) {

                if (telefono.getNumero().contains(contactITY.getCelular())
                        || telefono.getNumero().equals(contactITY.getCelular())) {
                    daoTelephone = new TelephoneDAO(_context, true);
                    daoTelephone.updateTelephoneAfterSync(telefono.getLookUpKey(), contactITY.getAnexo(), telefono.getNumero(), contactITY.getNombre());

                    actualizarFlagITYContacto(telefono.getLookUpKey());
                }

            }

//            if (!guardarAnexo)
//                daoTelephone.insertTelephone(telefono.getNumero(), telefono.getIdContacto(), telefono.getLookUpKey(), telefono.getAnexo(), "0");
        }
    }


    public void actualizarFlagContactoSincronizado(String lookUpKey) {
        ContentValues cv = new ContentValues();
        cv.put(FLAG_SINITY, Const.SYNCHRONIZE_DONE);
        int update = db.update(CONTACTO, cv, LOOKUP_KEY + "=?", new String[]{lookUpKey});
        Log.e(TAG, "updated!: " + update);
    }

    public void actualizarFlagITYContacto(String lookUpKey) {
        ContentValues cv = new ContentValues();
        cv.put(FLAG_ITY, Const.USER_ITY);
        db.update(CONTACTO, cv, LOOKUP_KEY + "=?", new String[]{lookUpKey});
    }


    /**
     * Metodo para obtener los contactos por sincronizar con la red IYT.
     *
     * @return una lista de objetos de contactos.
     */
    public List<Object> obtenerListadoContactoPorSincronizar() {
//        initDatabase(_context);

        List<Object> lista = new ArrayList();
        String[] filtro = new String[]{"0"};
        String consultaSql = "SELECT * FROM " + CONTACTO + " WHERE FLAG_SINITY=?";

        Cursor cursor = db.rawQuery(consultaSql, filtro);

        if (cursor != null && cursor.moveToFirst())

            do {

                BeanContact contacto = new BeanContact();
                contacto.setIdContacto(cursor.getInt(cursor.getColumnIndex(ID_CONTACTO)));
                contacto.setNombre(cursor.getString(cursor.getColumnIndex(NOMBRE)));
                contacto.setTelefono(cursor.getString(cursor.getColumnIndex(NUMERO)));
                contacto.setLookUpKey(cursor.getString(cursor.getColumnIndex(LOOKUP_KEY)));
                contacto.setFoto(cursor.getInt(cursor.getColumnIndex(FLAG_FOTO)));
                contacto.setUsuarioITY(cursor.getString(cursor.getColumnIndex(FLAG_ITY)));
                contacto.setFlagValidacion(cursor.getInt(cursor.getColumnIndex(FLAG_SINITY)));
                lista.add(contacto);

            } while (cursor.moveToNext());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

//        if (db.isOpen())
//            db.close();

        return lista;
    }

    public List<Object> obtenerListadoContacto() {

//        initDatabase(_context);
        List<Object> lista = new ArrayList();
        Cursor cursor = db.query(CONTACTO, new String[]{"ID_CONTACTO", "NOMBRE", "NUMERO", "LOOKUP_KEY", "FLAG_FOTO", "FLAG_ITY"}, null, null, null, null, "NOMBRE ASC");

        if (cursor.moveToFirst())
            do {
                BeanContact contacto = new BeanContact();
                contacto.setIdContacto(cursor.getInt(0));
                contacto.setNombre(cursor.getString(1));
                contacto.setTelefono(cursor.getString(2));
                contacto.setLookUpKey(cursor.getString(3));
                contacto.setFoto(cursor.getInt(4));
                contacto.setUsuarioITY(cursor.getString(5));
                contacto.setPhones(LogicTelephone.getTelephonesByKey(_context, contacto.getLookUpKey()));
                lista.add(contacto);
            } while (cursor.moveToNext());

        cursor.close();
//        if (db.isOpen())
//            db.close();
        return lista;
    }

    public List<Object> obtenerListaContactoItalkYou() {
//        initDatabase(_context);

        List<Object> lista = new ArrayList();
        Cursor cursor = db.query(CONTACTO, new String[]{"ID_CONTACTO", "NOMBRE", "NUMERO", "LOOKUP_KEY", "FLAG_FOTO", "FLAG_ITY"}, "FLAG_ITY = ?", new String[]{Const.USER_ITY}, null, null, "NOMBRE ASC");

        if (cursor.moveToFirst())
            do {
                BeanContact contacto = new BeanContact();
                contacto.setIdContacto(cursor.getInt(0));
                contacto.setNombre(cursor.getString(1));
                contacto.setTelefono(cursor.getString(2));
                contacto.setLookUpKey(cursor.getString(3));
                contacto.setFoto(cursor.getInt(4));
                contacto.setUsuarioITY(cursor.getString(5));
                contacto.setPhones(LogicTelephone.getTelephonesByKey(_context, contacto.getLookUpKey()));
                lista.add(contacto);
//                    } while (cursorAnexo.moveToNext());

//                if (cursorAnexo != null && !cursorAnexo.isClosed()) {
//                    cursorAnexo.close();
//                }
            } while (cursor.moveToNext());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
//        if (db.isOpen())
//            db.close();
        return lista;
    }

    public BeanContact obtenerContacto(String id) {
        BeanContact contacto = new BeanContact();
        Cursor cursor = db.query(CONTACTO, new String[]{"ID_CONTACTO", "NOMBRE", "NUMERO", "FLAG_FOTO", "FLAG_ITY"}, "ID_CONTACTO=?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst())
            do {
                contacto.setIdContacto(cursor.getInt(0));
                contacto.setNombre(cursor.getString(1));
                contacto.setTelefono(cursor.getString(2));
                contacto.setFoto(cursor.getInt(3));
                contacto.setUsuarioITY(cursor.getString(4));
            } while (cursor.moveToNext());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

//        if (db.isOpen())
//            db.close();
        return contacto;
    }

    public int existeContactos() {
        Cursor cursor = db.query(CONTACTO, null, null, null, null, null, null);
        int cont = cursor.getCount();
        cursor.close();
//        if (db.isOpen())
//            db.close();
        return cont;
    }

    /**
     * Metodo para verificar si hay contactos que no tienen la sincronizacion con ITY
     *
     * @return
     */
    public int existeContactosPorSincronizar() {
        int cont = 0;
        String[] filtros = new String[]{"0"};
        Cursor cursor = db.rawQuery("SELECT * FROM " + CONTACTO + " WHERE FLAG_SINITY = ? ", filtros);

        if (cursor != null) {
            cont = cursor.getCount();
            cursor.close();
        }

//        if (db.isOpen())
//            db.close();

        return cont;
    }


    /**
     * ***********************************************************************************************************************************
     */
    //Contactos alamacenados en ITY
    public List<Object> obtenerListadoTelefono(String key) {
        List<Object> lista = new ArrayList();
        Cursor cursor = db.query(TELEFONO, new String[]{"ID_CONTACTO", "LOOKUP_KEY", "NUMERO", "ANEXO", "CONECTADO"}, "LOOKUP_KEY=?", new String[]{key}, null, null, null);
        if (cursor.moveToFirst())
            do {
                BeanTelefono telefono = new BeanTelefono();
                telefono.setIdContacto(cursor.getInt(0));
                telefono.setIdContacto(cursor.getInt(1));
                telefono.setNumero(cursor.getString(2));
                telefono.setAnexo(cursor.getString(3));
                telefono.setConectado(cursor.getString(4));
                lista.add(telefono);
            } while (cursor.moveToNext());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return lista;
    }


    //rsantillanc
    public int deleteContact(int contactId) {
        return db.delete(CONTACTO, "ID_CONTACTO =?", new String[]{String.valueOf(contactId)});
    }

    public int countContacts() {
        Cursor c = db.rawQuery("SELECT * FROM " + CONTACTO, null);
        return c.getCount();
    }

    public boolean isContactExist(String lookUpKey) {
        Cursor cur = db.rawQuery(SELECT_IF_EXIST, new String[]{lookUpKey});
        int row = cur.getCount();
        cur.close();
        return row > 0;
    }


    public BeanContact getLastContact() {
        String[] projection = {ID_CONTACTO, NUMERO, LOOKUP_KEY};
        Cursor cu = db.query(CONTACTO, projection, null, null, null, null, ID_CONTACTO + " DESC", "1");
        BeanContact contact = new BeanContact();

        if (cu.moveToFirst()) {
            contact.setIdContacto(cu.getInt(0));
            contact.setTelefono(cu.getString(1));
            contact.setLookUpKey(cu.getString(2));
        } else
            contact = null;

        cu.close();
        return contact;
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


    public void drop(String tbl_telefono) {
        try {
            this.db.delete(CONTACTO, null, null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tbl_telefono + "'");
        } catch (Exception ex) {
            Log.e(TAG, "No se puedo Eliminar " + ex);
        }
    }

    public void synchContact() {

    }
}
