package com.italkyou.dao;

import android.content.Context;

/**
 * Created by RenzoD on 09/06/2015.
 */
public class PaymentDAO extends BaseDAO {

    private static final String TAG = PaymentDAO.class.getSimpleName();
    private static final String PAYMENT_TABLE = "PAYMENT";

    private static final String SELECT_IF_EXIST = "SELECT * FROM "
            + PAYMENT_TABLE + " WHERE LOOKUP_KEY = ? AND NUMERO = ?;";


    private static final int INDEX_ID = 0;
    private static final int INDEX_ID_CONTACTO = 1;
    private static final int INDEX_NOMBRE_ITY = 2;
    private static final int INDEX_LOOKUP_KEY = 3;
    private static final int INDEX_NUMERO = 4;
    private static final int INDEX_ANEXO = 5;
    private static final int INDEX_CONECTADO = 6;


    public PaymentDAO(Context contexto) {
        super(contexto);
    }
}
