package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.italkyou.beans.BeanRedirect;
import com.italkyou.utils.Const;

/**
 * Created by rsantillanc on 19/08/2015.
 */
public class RedirectDAO extends BaseDAO {

    //Statics - field
    private static final String REDIRECT_ID = "redirectId";
    private static final String ZIPCODE = "zipcode";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String USER_ID = "userId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String FLAG_PERMANENT = "flagPermanent";
    private static final String FLAG_ACTIVE = "flagActive";

    //Statics - index
    private static final int INDEX_REDIRECT_ID = 0;
    private static final int INDEX_ZIPCODE = 1;
    private static final int INDEX_PHONE_NUMBER = 2;
    private static final int INDEX_USER_ID = 3;
    private static final int INDEX_FROM_DATE = 4;
    private static final int INDEX_TO_DATE = 5;
    private static final int INDEX_FLAG_PERMANENT = 6;
    private static final int INDEX_FLAG_ACTIVE = 7;
    //Name Redirect table
    private static final String TABLE_NAME = "REDIRECT";
    private static final String TAG = RedirectDAO.class.getSimpleName() + Const.ESPACIO_BLANCO;

    //Querys
    final String select_by_userId = "SELECT * FROM " + TABLE_NAME
            + " WHERE " + USER_ID + " = ?";

    final String select_phonenumber_by_userid = "SELECT " + PHONE_NUMBER +
            " FROM " + TABLE_NAME +
            " WHERE " + USER_ID + " = ? ;";


    //Objects
    private BeanRedirect objRedirect;


    public RedirectDAO(Context contexto) {
        super(contexto);
    }


    /**
     * Este método permite insertar un registro cuando el evento de redireccionar anexo, sea activado.
     *
     * @param bean Objecto que contiene los datos a insertar.
     * @return un número entero que indica un exito en la insercion
     */
    public int insertRedirect(BeanRedirect bean) {
        objRedirect = bean;
        long row_affected;

        ContentValues cv = new ContentValues();
        cv.put(ZIPCODE, bean.getZipcode());
        cv.put(PHONE_NUMBER, bean.getPhoneNumber());
        cv.put(USER_ID, bean.getUserId());
        cv.put(FROM_DATE, bean.getFromDate());
        cv.put(TO_DATE, bean.getToDate());
        cv.put(FLAG_PERMANENT, bean.getFlagPermanent());
        cv.put(FLAG_ACTIVE, bean.getFlagActive());
        row_affected = db.insert(TABLE_NAME, null, cv);

        return (int) row_affected;
    }

    /**
     * @param userId usuario filtro.
     * @return true si existe.
     */
    public boolean checkIfExist(String userId) {
        String[] args = {userId};
        Cursor cs = getDb().rawQuery(select_phonenumber_by_userid, args);
        return (cs.moveToFirst()) ? true : false;
    }


    /**
     * Este método actualiza un registro específico en la tabla de redireccionamiento.
     *
     * @param bean Objecto que contiene para la actualización
     * @return un entero positivo mayor a 0 si la actualizació fue correcta.
     */
    public int updateRedirect(BeanRedirect bean) {
        objRedirect = bean;
        int row_affected;
        String where = USER_ID + " = ?";
        String[] args = {String.valueOf(objRedirect.getUserId())};

        ContentValues cv = new ContentValues();
        cv.put(ZIPCODE, bean.getZipcode());
        cv.put(PHONE_NUMBER, bean.getPhoneNumber());
        cv.put(USER_ID, bean.getUserId());
        cv.put(FROM_DATE, bean.getFromDate());
        cv.put(TO_DATE, bean.getToDate());
        cv.put(FLAG_PERMANENT, bean.getFlagPermanent());
        cv.put(FLAG_ACTIVE, bean.getFlagActive());

        row_affected = db.update(TABLE_NAME, cv, where, args);
        return row_affected;
    }

    /**
     * Método para consultar un redireccionamento en especifico.
     * @param userId es el campo para realizar el filtro.
     * @return devuelve un objeto BeanRedirect, con los datos.
     */
    public BeanRedirect getRedirect(String userId) {
        String[] args = {userId};
        Cursor cs = db.rawQuery(select_by_userId, args);

        if (cs.moveToFirst()) {
            objRedirect = new BeanRedirect();
            objRedirect.setZipcode(cs.getInt(INDEX_ZIPCODE));
            objRedirect.setPhoneNumber(cs.getLong(INDEX_PHONE_NUMBER));
            objRedirect.setUserId(cs.getInt(INDEX_USER_ID));
            objRedirect.setFromDate(cs.getString(INDEX_FROM_DATE));
            objRedirect.setToDate(cs.getString(INDEX_TO_DATE));
            objRedirect.setFlagPermanent(cs.getInt(INDEX_FLAG_PERMANENT));
            objRedirect.setFlagActive(cs.getInt(INDEX_FLAG_ACTIVE));
            Log.e(Const.DEBUG_DATABASE, TAG + "getRedirect: " + objRedirect.toString());
        } else
            objRedirect = null;


        if (cs != null)
            cs.close();

        return objRedirect;
    }
}
