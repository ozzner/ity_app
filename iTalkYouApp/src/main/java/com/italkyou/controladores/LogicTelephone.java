package com.italkyou.controladores;

import android.content.Context;

import com.italkyou.beans.BeanTelefono;
import com.italkyou.dao.TelephoneDAO;

import java.util.List;

/**
 * Created by RenzoD on 09/06/2015.
 */
public class LogicTelephone {


    /**
     * @param c   Is the context of the application,
     * @param key Is the key to search contact stored in the device,
     * @return An object BeanTelefono
     */
    public static List<Object> getTelephonesByKey(Context c, String key) {

        TelephoneDAO dao = new TelephoneDAO(c, false);
        List<Object> bean = dao.getTelephonesByKey(key);
        return bean;
    }

    public static List<BeanTelefono> getAllTelephones(Context c, int contactID) {
        List<BeanTelefono> bean = null;
        TelephoneDAO dao = new TelephoneDAO(c, false);
        return bean;
    }

    public static int updateTelephone(Context c, String lookUpKey, String phone, String status) {
        TelephoneDAO dao = new TelephoneDAO(c, true);
        int row = dao.updateTelephone(phone, lookUpKey, status);
        return row;
    }

    public static int insertTelephone(Context c, int contactID, String lookUpKey, String annex, String phone) {
        final String DEFAULT = "0";
        TelephoneDAO dao = new TelephoneDAO(c, true);
        int rows = dao.insertTelephone(phone, contactID, lookUpKey, annex, DEFAULT);
        return rows;
    }

    public static int delete(Context c, String lookUp_key) {
        TelephoneDAO dao = new TelephoneDAO(c,true);
        return dao.delete(lookUp_key);
    }
}
