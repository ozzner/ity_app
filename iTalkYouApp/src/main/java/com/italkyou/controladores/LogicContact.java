package com.italkyou.controladores;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanContactUser;
import com.italkyou.beans.BeanFramesTelephones;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.dao.ContactoDAO;
import com.italkyou.dao.TablasBD;
import com.italkyou.dao.TelephoneDAO;
import com.italkyou.utils.Const;
import com.italkyou.utils.ItyPreferences;

import java.util.ArrayList;
import java.util.List;

public class
LogicContact {

    private static final String TAG = LogicContact.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static ItyPreferences mPreferences;

    //For contacts
    private static final Uri QUERY_URI = ContactsContract.Contacts.CONTENT_URI;
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String PHOTO_ID = ContactsContract.Contacts.PHOTO_ID;
    private static final String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    //For phone numbers
    private static final Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String STARRED_CONTACT = ContactsContract.Contacts.STARRED;

    //For emails
    private static final Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private static final String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    private static final String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;

    //others
    private static final int MAX_FRAMES = 55;


    public LogicContact() {
    }

    /**
     * Metodo para listar los contactos almacenados en el dispositivo.
     *
     * @param context contexto de la aplicacion.
     * @return Entidad que contiene la lista de contactos y telefonos del dispositivo.
     */
    public BeanContactUser listarContactoDispositivo(Context context) {
        BeanContactUser contactoUsuario = new BeanContactUser();
        ContentResolver contentResolver = context.getContentResolver();
        int pos = 0;
        List<BeanContact> listaContactos = new ArrayList();
        List<BeanTelefono> listaTelefonos = new ArrayList();

        String[] projection = new String[]{
                CONTACT_ID //1
                , DISPLAY_NAME
                , LOOKUP_KEY
                , HAS_PHONE_NUMBER
                , PHOTO_ID//5
        };

        String selection = HAS_PHONE_NUMBER + ">?";
        String[] selectionArgs = new String[]{"0"};

        Cursor contactsCursor = context.getContentResolver().query(QUERY_URI, projection, selection, selectionArgs, CONTACT_ID + " ASC");
        mPreferences = new ItyPreferences(context);
        mPreferences.saveContactsSize(contactsCursor.getCount());

        if (contactsCursor.moveToFirst()) {

            do {
                BeanContact contacto = new BeanContact();
                int contactID = contactsCursor.getInt(0);
                String name = contactsCursor.getString(1);
                String lookupKey = contactsCursor.getString(2);
                int hasNumbers = contactsCursor.getInt(3);
                int photo = contactsCursor.getInt(4);

                if (name.equalsIgnoreCase("Arturo intico 2")) {
                    Log.e("Debug ", "pointtt");
                }

                contacto.setIdContacto(contactID);
                contacto.setNombre(name);
                contacto.setLookUpKey(lookupKey);
                contacto.setFoto(photo);

                //if can get number
                if (hasNumbers > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            PHONE_CONTENT_URI
                            , null
                            , PHONE_CONTACT_ID + " = ?"
                            , new String[]{String.valueOf(contactID)}
                            , PHONE_NUMBER + " ASC");

                    String chain = "", temp = "santillán";
                    if (phoneCursor.moveToFirst()) {

                        do {
                            BeanTelefono telefono = new BeanTelefono();

                            String numero = phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER));
                            //                            numero = numero.replace(Const.CADENA_PREFIJO, Const.cad_vacia);
                            numero = numero.replace(Const.ESPACIO_BLANCO, Const.cad_vacia);
                            numero = numero.replace("*", Const.cad_vacia);
                            numero = numero.replace("-", Const.cad_vacia);
                            numero = numero.replace("(", Const.cad_vacia);
                            numero = numero.replace(")", Const.cad_vacia);

                            if (!numero.contains(temp)) {
                                temp = numero;
                                chain = chain + numero + Const.SEPARADOR_COMA;

                                telefono.setIdContacto(contactID);
                                telefono.setNumero(numero);
                                telefono.setPosContacto(pos);
                                telefono.setLookUpKey(contacto.getLookUpKey());
                                listaTelefonos.add(telefono);
                            }

                        } while (phoneCursor.moveToNext());

                        if (phoneCursor != null && !phoneCursor.isClosed()) {
                            phoneCursor.close();
                        }

                        contacto.setTelefono(chain);
                        listaContactos.add(contacto);
                        pos++;
                    }
                }

            } while (contactsCursor.moveToNext());
            // get the last LookupKey and save
            int lastSize = listaContactos.size() - 1;
            mPreferences.saveLastLookupKey(listaContactos.get(lastSize).getLookUpKey());
        }

        if (contactsCursor != null && !contactsCursor.isClosed()) {
            contactsCursor.close();
        }

        contactoUsuario.setListaContactos(listaContactos);
        contactoUsuario.setListaTelefonos(listaTelefonos);

        return contactoUsuario;
    }


    private String obtenerNumerosTelefonicos(List<BeanTelefono> listaNumeros, int posIn, int posFin) {

        String lista = "";
        if (posFin > listaNumeros.size())
            posFin = listaNumeros.size();

        for (int i = posIn; i < posFin; i++)
            lista += listaNumeros.get(i).getNumero() + Const.SEPARADOR_COMA;

        return lista;
    }


    private int obtenerPosicion(List<BeanTelefono> listaTelefonos, String telefono) {

        for (int i = 0; i < listaTelefonos.size(); i++) {
            String numero = listaTelefonos.get(i).getNumero();
            if (numero.equals(telefono)) return i;
        }
        return 0;
    }


    /**
     * Metodo para almacenar los contactos del telefono en la base interna, estos contactos aun no estan validados con la red ITalkYou
     *
     * @param contextoApp, es el contexto de la aplicacion.
     */
    public BeanContactUser almacenarContactosDispositivo(Context contextoApp) {

        Log.e(TAG, "SE INICIA EL GUARDADO DE CONTACTOS SIN VALIDAR CON ITALKYOU");
        //Se carga la lista de contactos.
        BeanContactUser lstContactosUsuario = listarContactoDispositivo(contextoApp);

        BeanRespuestaOperacion datoContacto = new BeanRespuestaOperacion();
        datoContacto.setNombreObjecto(Const.almacenar_contactos);
        datoContacto.setObjeto(lstContactosUsuario.getListaContactos());

        //se guardan los contactos.
        AsyncGuardarDAOTask almacenarContacto = new AsyncGuardarDAOTask(contextoApp);
        almacenarContacto.execute(datoContacto);

        BeanRespuestaOperacion datoTelefono = new BeanRespuestaOperacion();
        datoTelefono.setNombreObjecto(Const.almacenar_numeros);
        datoTelefono.setObjeto(lstContactosUsuario.getListaTelefonos());

        //se guardan los telefonos.
        AsyncGuardarDAOTask almacenarTelefono = new AsyncGuardarDAOTask(contextoApp);
        almacenarTelefono.execute(datoTelefono);

        Log.e(TAG, "SE FINALIZA EL GUARDADO DE CONTACTOS SIN VALIDAR CON ITALKYOU");
        return lstContactosUsuario;

    }


    public void actualizarContactosITalkYou(BeanContactUser bContactos, Context contexoApp, List<OutputContact> contactosITalkYou, boolean borrarTelefonos) {


        ContactoDAO daoContacto = new ContactoDAO(contexoApp, true);
        if (borrarTelefonos)
            daoContacto.drop("TBL_TELEFONO");

        daoContacto.actualizarListaContactosITalkYou(bContactos, contactosITalkYou);
    }

    /**
     * Metodo para verificar la existencia de contactos.
     *
     * @return un flag que indicaria si se debe sincronizar todos los contactos.
     */
    public static boolean checkIfContactsExist(Context contexto) {

        ContactoDAO daoContacto = new ContactoDAO(contexto, false);
        int cantContactos = daoContacto.existeContactos();
//        daoContacto.cerrar();
        Log.e(TAG, "La cantidad de contactos es :" + cantContactos);

        if (cantContactos > 0)
            return true;
        else
            return false;

    }

    /**
     * Metodo para validar si hay contactos por validar con la red ITY.
     *
     * @param contexto, es el contexto de la aplicacion.
     * @return true, si hay contactos por sincronizar, false si no hay contactos por sincronizar.
     */
    public static boolean verificarExistenciaContactosPorSincronizar(Context contexto) {

        ContactoDAO daoContacto = new ContactoDAO(contexto, false);
        int cantContactos = daoContacto.existeContactosPorSincronizar();
        Log.e(TAG, "Tenemos " + cantContactos + " por sincronizar!!!!!!");
//        daoContacto.cerrar();

        if (cantContactos > 0)
            return true;
        else
            return false;
    }


    public List<BeanFramesTelephones> getElementsToSyncInITY(Context contextoApp) {
        ContactoDAO daoContacto = new ContactoDAO(contextoApp, false);
        List<BeanFramesTelephones> framesList = new ArrayList<>();
        ArrayList<BeanContact> lstContactos = (ArrayList) daoContacto.obtenerListadoContactoPorSincronizar();
//        daoContacto.cerrar();

        String cadenaTelefonos = "";
        int count = 0;
        for (BeanContact bContacto : lstContactos) {
            String empty = "";

            String[] Phones = bContacto.getTelefono().split(",");
            count = count + Phones.length;
            cadenaTelefonos = cadenaTelefonos + bContacto.getTelefono().replace(Const.CADENA_PREFIJO, empty);

            if (count >= MAX_FRAMES) {
                BeanFramesTelephones frames = new BeanFramesTelephones();
                frames.setCount(count);
                frames.setFrameTelephone(cadenaTelefonos);
                framesList.add(frames);

                //reset
                count = 0;
                cadenaTelefonos = "";
            }

            Log.e(Const.DEBUG_CONTACTS, TAG + "TELEPHONE: " + bContacto.getTelefono());
        }

        BeanFramesTelephones frames = new BeanFramesTelephones();
        frames.setCount(count);
        frames.setFrameTelephone(cadenaTelefonos);
        framesList.add(frames);

        return framesList;

    }

    public static int guardarListadoContactos(Context contexto, List<BeanContact> listado, boolean flagDelete) {
        int rows = 0;
        ContactoDAO lstDao = new ContactoDAO(contexto, true);

        if (flagDelete)
            lstDao.drop(TablasBD.TBL_CONTACTO);

        for (int i = 0; i < listado.size(); i++) {
            rows = lstDao.insertarContacto(listado.get(i));
        }

//        lstDao.cerrar();

        return rows;
    }

    public static List<Object> obtenerListadoContactos(Context contexto) {

        ContactoDAO lstDao = new ContactoDAO(contexto, false);
        List<Object> lista = lstDao.obtenerListadoContacto();

        return lista;
    }


    public static List<Object> obtenerListadoContactosAnexo(Context contexto) {
        List<Object> lista;
        ContactoDAO lstDao = new ContactoDAO(contexto, false);
        lista = lstDao.obtenerListaContactoItalkYou();
        return lista;
    }

//    public static boolean seAlmacenoContactos(Context contexto) {
//        boolean flag = false;
//        ContactoDAO contacto = new ContactoDAO(contexto);
//        int cont = contacto.existeContactos();
////        contacto.cerrar();
//        if (cont > 0) flag = true;
//        return flag;
//    }

    public static int guardarListadoTelefono(Context contexto, List<BeanTelefono> listado) {
        int rows = 0;

        for (int i = 0; i < listado.size(); i++) {

            rows = LogicTelephone.insertTelephone(
                    contexto
                    , listado.get(i).getIdContacto()
                    , listado.get(i).getLookUpKey()
                    , listado.get(i).getAnexo()
                    , listado.get(i).getNumero());

        }

        return rows;
    }


    public static void borrarDatos(Context contexto) {
        ContactoDAO contactoDao = new ContactoDAO(contexto, true);
        contactoDao.drop(TablasBD.TBL_CONTACTO);
    }

    //rsantillanc
    public static int updateContact(BeanContact contacto, Context c) {
        ContactoDAO dao = new ContactoDAO(c, true);
        int row = dao.modificarContacto(contacto);
        return row;
    }

    public static int deleteContactById(Context c, int idContacto) {
        ContactoDAO dao = new ContactoDAO(c, true);
        return dao.deleteContact(idContacto);
    }

    public static int haveNewsContacts(Context c) {
        if (mPreferences == null)
            mPreferences = new ItyPreferences(c);

        int sizeStored = mPreferences.getSaveStored();
        int sizeDevice = getContactsSize(c);

        return sizeDevice - sizeStored;

    }

    public static int getContactsSize(Context c) {
        String selection = HAS_PHONE_NUMBER + ">?";
        String[] selectionArgs = new String[]{"0"};
        Cursor cu = c.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, selection, selectionArgs, null);
        return cu.getCount();
    }

    public static BeanContactUser getNewsContacts(Context c) {

        BeanContactUser contactoUsuario = new BeanContactUser();
        ContentResolver contentResolver = c.getContentResolver();
        int pos = 0;
        List<BeanContact> listaContactos = new ArrayList();
        List<BeanTelefono> listaTelefonos = new ArrayList();

        int count = 0, diff;
        int sizeStored = mPreferences.getSaveStored();


        String[] projection = new String[]{
                CONTACT_ID //0
                , DISPLAY_NAME
                , LOOKUP_KEY
                , HAS_PHONE_NUMBER
                , PHOTO_ID};//4


        String selection = HAS_PHONE_NUMBER + ">?";
        String[] selectionArgs = new String[]{"0"};


        Cursor contactsCursor = contentResolver.query(QUERY_URI, projection, selection, selectionArgs, CONTACT_ID + " ASC");


        int sizeDevice = contactsCursor.getCount();
        diff = sizeDevice - sizeStored;

        if (contactsCursor.moveToLast()) {

            do {

                BeanContact contacto = new BeanContact();
                int contactID = contactsCursor.getInt(0);
                String name = contactsCursor.getString(1);
                String lookupKey = contactsCursor.getString(2);
                int photo = contactsCursor.getInt(4);
                int hasNumbers = contactsCursor.getInt(3);

                contacto.setIdContacto(contactID);
                contacto.setNombre(name);
                contacto.setLookUpKey(lookupKey);
                contacto.setFoto(photo);


                //if can get number
                if (hasNumbers > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            PHONE_CONTENT_URI
                            , null
                            , PHONE_CONTACT_ID + " = ?"
                            , new String[]{String.valueOf(contactID)}
                            , PHONE_NUMBER + " ASC");

                    String chain = "", temp = null;
                    if (phoneCursor.moveToFirst()) {

                        do {
                            BeanTelefono telefono = new BeanTelefono();

                            String numero = phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER));
//                            numero = numero.replace(Const.CADENA_PREFIJO, Const.cad_vacia);
                            numero = numero.replace(Const.ESPACIO_BLANCO, Const.cad_vacia);
                            numero = numero.replace("*", Const.cad_vacia);
                            numero = numero.replace("-", Const.cad_vacia);
                            numero = numero.replace("(", Const.cad_vacia);
                            numero = numero.replace(")", Const.cad_vacia);

                            if (!numero.equals(temp)) {
                                temp = numero;
                                chain = chain + numero + Const.SEPARADOR_COMA;

                                telefono.setIdContacto(contactID);
                                telefono.setNumero(numero);
                                telefono.setPosContacto(pos);
                                telefono.setLookUpKey(contacto.getLookUpKey());
                                listaTelefonos.add(telefono);
                            }


                        } while (phoneCursor.moveToNext());

                        if (phoneCursor != null && !phoneCursor.isClosed()) {
                            phoneCursor.close();
                        }

                        contacto.setTelefono(chain);
                        listaContactos.add(contacto);
                        pos++;
                    }
                }

                count++;
            } while (contactsCursor.moveToPrevious() && count < diff);

        }

        if (contactsCursor != null && !contactsCursor.isClosed()) {
            contactsCursor.close();
        }

        contactoUsuario.setListaContactos(listaContactos);
        contactoUsuario.setListaTelefonos(listaTelefonos);
        mPreferences.saveContactsSize(sizeDevice);//Update preference

        return contactoUsuario;
    }


    public static BeanContactUser storeNewsContacts(Context c) {
        BeanContactUser user = getNewsContacts(c);
        List<BeanTelefono> telephones = user.getListaTelefonos();
        List<BeanContact> contactos = user.getListaContactos();


        for (BeanTelefono telephone : telephones) {
            LogicTelephone.insertTelephone(c, telephone.getIdContacto(), telephone.getLookUpKey(), Const.cad_vacia, telephone.getNumero());
        }

        for (BeanContact contacto : contactos) {
            new ContactoDAO(c, true).insertarContacto(contacto);
        }

        return user;
    }


    public BeanContact editContactDevice(Context c, Intent data, BeanContact contacto) {
        int contactID, havePhones, foto;
        String name, chain = "", lookUp_key = "";
        ContentResolver cr = c.getContentResolver();
        BeanContact contact = null;


        Uri contactData = data.getData();
        Cursor cur = cr.query(contactData, null, null, null, null);

        if (cur.moveToFirst()) {
            name = cur.getString(cur.getColumnIndex(DISPLAY_NAME));
            contactID = cur.getInt(cur.getColumnIndex(CONTACT_ID));
            lookUp_key = cur.getString(cur.getColumnIndex(LOOKUP_KEY));
            foto = cur.getInt(cur.getColumnIndex(PHOTO_ID));
            havePhones = cur.getInt(cur.getColumnIndex(HAS_PHONE_NUMBER));

            Log.e(TAG, "contact_ID before: " + contacto.getIdContacto());
            Log.e(TAG, "contact_ID after: " + contactID);
            contact = new BeanContact();
            //set
            contact.setIdContacto(contactID);
            contact.setNombre(name);
            contact.setFoto(foto);
            contact.setLookUpKey(lookUp_key);

            if (havePhones > 0) {
                Cursor phoneCursor = c.getContentResolver()
                        .query(
                                PHONE_CONTENT_URI
                                , null
                                , PHONE_CONTACT_ID + " = ?"
                                , new String[]{String.valueOf(contactID)}
                                , PHONE_NUMBER + " ASC");

                String temp = "santillán";

                if (phoneCursor.moveToFirst()) {
                    int i = LogicTelephone.delete(c, lookUp_key);

                    do {
                        String phone = phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER));
//                       phone = phone.replace(Const.CADENA_PREFIJO, Const.cad_vacia);
                        phone = phone.replace(Const.ESPACIO_BLANCO, Const.cad_vacia);
                        phone = phone.replace("*", Const.cad_vacia);
                        phone = phone.replace("-", Const.cad_vacia);
                        phone = phone.replace("(", Const.cad_vacia);
                        phone = phone.replace(")", Const.cad_vacia);

                        if (!phone.contains(temp)) {
                            int p = LogicTelephone.insertTelephone(c, contactID, lookUp_key, Const.CARACTER_VACIO, phone);
                            if ((i + p) > 0) {
                                temp = phone;
                                chain = chain + phone + ",";
                            }
                        }

                    } while (phoneCursor.moveToNext());
                }

                //set
                contact.setTelefono(chain);
                phoneCursor.close();
            }

            //Update changes
            LogicContact.updateContact(contact, c);
        }

        return contact;

    }


    public static BeanContact getLastContact(Context c) {
        return new ContactoDAO(c, false).getLastContact();
    }

    public static void updateContactSynchronized(Context c, BeanContact contact, List<OutputContact> ityAnnex) {
        ContactoDAO dao = new ContactoDAO(c, true);
        OutputContact itemITY = ityAnnex.get(0);

        //update flag contact = 1
        dao.actualizarFlagContactoSincronizado(contact.getLookUpKey());

        TelephoneDAO daoTelephone = new TelephoneDAO(c, true);
        List<Object> listPhones = daoTelephone.getTelephonesByKey(contact.getLookUpKey());

        //Update each phones by contact
        for (Object phone : listPhones) {
            //update only the correct phone
            if (((BeanTelefono) phone).getNumero().equals(itemITY.getCelular())
                    || ((BeanTelefono) phone).getNumero().contains(itemITY.getCelular()))
                daoTelephone.updateTelephoneAfterSync(((BeanTelefono) phone).getLookUpKey(), itemITY.getAnexo(), ((BeanTelefono) phone).getNumero(), itemITY.getNombre());
        }


    }

    public static void actualizarFlagITYContacto(String lookUpKey, Context c) {
        new ContactoDAO(c, true).actualizarFlagITYContacto(lookUpKey);
    }
}
