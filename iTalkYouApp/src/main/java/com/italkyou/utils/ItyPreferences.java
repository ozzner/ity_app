package com.italkyou.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by RenzoD on 17/06/2015.
 */
public class ItyPreferences {
    private static final int MODE_PRIVATE = Context.MODE_PRIVATE;
    private static final String ITY_PREFERENCES = "ItyPreferences";

    private static final int INT_DEFAULT_VALUE = -19;//My birthday negative :D
    public static final String KEY_SIZE = "ItyContactSize";
    public static final String KEY_LAST_CONTACT = "lastLookupKey";
    private static final String DEFAULT_STRING = "";
    private static final String KEY_ANNEX = "ItyPreferences.annex";

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;
    private Context _context;


    /**
     * @param _context Contexto de la aplicaci�n.
     * @see <p>Metodo que inicia las preferencias.</p>
     */
    public ItyPreferences(Context _context) {
        this._context = _context;
        mPreferences = _context.getSharedPreferences(ITY_PREFERENCES, MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    /**
     * @param size Valor que se almacenar para saber cuantos se grabaron anteriormente.
     * @return valor entero, si es 1 = ok, sino error.
     * @see <p>Metodo para limpar almacenar las preferencias</p>
     */
    public int saveContactsSize(int size) {

        try {
            mEditor.putInt(KEY_SIZE, size);
            mEditor.commit();
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }


    public void saveAnnex(String annex) {
        mEditor.putString(KEY_ANNEX, annex).apply();
    }

    public String getAnnex() {
        return mPreferences.getString(KEY_ANNEX, "");
    }

    /**
     * @return Valor entero almacenado en preferencias. (representa el tama�o de la lista).
     * @see <p>Metodo para obtener el tama�o de la lista de los contactos</p>
     */
    public int getSaveStored() {
        return mPreferences.getInt(KEY_SIZE, INT_DEFAULT_VALUE);
    }


    /**
     * Este metodo graba el ultimo lookupKey para la syncronización posterior de los contactos.
     *
     * @param lookupKey
     */
    public void saveLastLookupKey(String lookupKey) {
        mEditor.putString(KEY_LAST_CONTACT, lookupKey);
        mEditor.commit();
    }


    /**
     * Este método devuelve el último key guardado.
     *
     * @return
     */
    public String getLastLookupKey() {
        return mPreferences.getString(KEY_LAST_CONTACT, DEFAULT_STRING);
    }

    /**
     * @see <p>Metodo para limpar toda la preferencia almacenada</p>
     */

    public void clear() {
        mEditor.clear().apply();
        mEditor.commit();
    }

    /**
     * @param key es la etiqueta para remover un valor especifico
     */
    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void putString(String sipSessionId) {
        mEditor.putString(Const.SIP_SESSION_ID,sipSessionId);
        mEditor.apply();
        mEditor.commit();
    }

    public String getString(String key){
        return mPreferences.getString(key,"");
    }
}
