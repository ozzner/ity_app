package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.italkyou.beans.BeanPais;
import com.italkyou.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class PaisDAO extends BaseDAO {

    private String PAIS = "TBL_PAIS";

    public PaisDAO(Context contexto) {
        super(contexto);
    }

    public void insertarPais(BeanPais pais) {
        ContentValues valores = new ContentValues();
        valores.put("ID_PREFIJO", pais.getID_Prefijo());
        valores.put("DESCRIPCION_ES", pais.getDescripcionES());
        valores.put("DESCRIPCION_EN", pais.getDescripcionEN());
        valores.put("ID_PAIS", pais.getID_Pais());
        //valores.put("ID_GTM", pais.getID_Gtm());
        valores.put("ID_GTM", "8");
        valores.put("MCC", pais.getMCC());
        getDb().insert(PAIS, null, valores);
        //Log.i("ITalkYou","[PAIS DAO ]= se inserto el pais "+pais.getMCC());
    }

    public void modificarPais(BeanPais pais) {
        ContentValues cv = new ContentValues();
        cv.put("DESCRIPCION_ES", pais.getDescripcionES());
        cv.put("DESCRIPCION_EN", pais.getDescripcionEN());
        cv.put("ID_PAIS", pais.getID_Pais());
        cv.put("ID_GTM", pais.getID_Gtm());
        cv.put("MCC", pais.getMCC());
        getDb().update(PAIS, cv, "ID=?", new String[]{pais.getID()});
        Log.i("Intico", "[PAIS DAO ]= se actualiza el pais");
    }

    public List<Object> obtenerListadoPais(String idioma_int) {
        List<Object> lista = new ArrayList<>();
        Cursor cursor;

        if (idioma_int.equals(Const.IDIOMA_ES))
            cursor = getDb().query(PAIS, new String[]{"ID", "ID_PREFIJO", "DESCRIPCION_ES", "DESCRIPCION_EN", "ID_PAIS", "ID_GTM", "MCC"}, null, null, null, null, "DESCRIPCION_ES ASC");
        else
            cursor = getDb().query(PAIS, new String[]{"ID", "ID_PREFIJO", "DESCRIPCION_ES", "DESCRIPCION_EN", "ID_PAIS", "ID_GTM", "MCC"}, null, null, null, null,  "DESCRIPCION_EN ASC");

        if (cursor.moveToFirst())
            do {
                BeanPais pais = new BeanPais();
                pais.setID(cursor.getString(0));
                pais.setID_Prefijo(cursor.getString(1));
                pais.setDescripcionES(cursor.getString(2));
                pais.setDescripcionEN(cursor.getString(3));
                pais.setID_Pais(cursor.getString(4));
                pais.setID_Gtm(cursor.getString(5));
                pais.setMCC(cursor.getString(6));
                lista.add(pais);
            } while (cursor.moveToNext());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return lista;
    }

    public BeanPais obtenerPais(String codigo) {
        BeanPais pais = new BeanPais();
        Cursor cursor = getDb().query(PAIS, new String[]{"ID", "ID_PREFIJO", "DESCRIPCION_ES", "DESCRIPCION_EN", "ID_PAIS", "ID_GTM", "MCC"}, "MCC=?", new String[]{codigo.toUpperCase()}, null, null, null);
        if (cursor.moveToFirst()) {
            pais.setID(cursor.getString(0));
            pais.setID_Prefijo(cursor.getString(1));
            pais.setDescripcionES(cursor.getString(2));
            pais.setDescripcionEN(cursor.getString(3));
            pais.setID_Pais(cursor.getString(4));
            pais.setID_Gtm(cursor.getString(5));
            pais.setMCC(cursor.getString(6));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return pais;
    }

    public BeanPais getCountryByZip(String zip) {
        BeanPais pais = new BeanPais();
        Cursor cursor = getDb().query(PAIS, null, "ID_PREFIJO=?", new String[]{zip}, null, null, null);
        if (cursor.moveToFirst()) {
            pais.setID(cursor.getString(0));
            pais.setID_Prefijo(cursor.getString(1));
            pais.setDescripcionES(cursor.getString(2));
            pais.setDescripcionEN(cursor.getString(3));
            pais.setID_Pais(cursor.getString(4));
            pais.setID_Gtm(cursor.getString(5));
            pais.setMCC(cursor.getString(6));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return pais;
    }

    public int numeroDatos() {
        int cont = 0;
        Cursor cursor = getDb().query(PAIS, new String[]{"ID"}, null, null, null, null, null);
        Log.i("Intico", "[PAIS DAO ]= nro datos " + cursor.getCount());
        cont = cursor.getCount();
        return cont;
    }

    public void borrarDatos() {
        borrarTodo(PAIS);
        Log.i("Intico", "[PAIS DAO ]= se borro todos los datos");
    }
}
