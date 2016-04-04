package com.italkyou.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.italkyou.utils.Const;

public class BaseDatosSQLiteHelper extends SQLiteOpenHelper {


    private static final String CHAT_MESSAGES = "CHAT_MESSAGES";
    private static final String CHAT_USER = "CHAT_USER";
    private static final String PAYMENT = "PAYMENT";
    private static final String REDIRECT = "REDIRECT";

    public BaseDatosSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(TablasBD.CREACION_TBL_USUARIO);
            db.execSQL(TablasBD.TBL_PAIS);
            db.execSQL(TablasBD.CREACION_TBL_CONTACTO);
            db.execSQL(TablasBD.TBL_TELEFONO);
            db.execSQL(TablasBD.PAYMENT_CREATE);
            db.execSQL(TablasBD.CHAT_USER_CREATE);
            db.execSQL(TablasBD.CHAT_MESSAGE_CREATE);
            db.execSQL(TablasBD.REDIRECT_CREATE);

            Log.e(Const.DEBUG_DATABASE, "[BaseDatos]= DATABASE CREATED");

        }catch (Exception ex){
            Log.e(Const.DEBUG_DATABASE, "[BaseDatos]= DATABASE ALREADY EXIST");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS `TBL_USUARIO`");
        db.execSQL("DROP TABLE IF EXISTS `TBL_PAIS`");
        db.execSQL("DROP TABLE IF EXISTS `TBL_CONTACTO`");
        db.execSQL("DROP TABLE IF EXISTS `TBL_TELEFONO`");
        db.execSQL("DROP TABLE IF EXISTS `" + CHAT_MESSAGES + "`");
        db.execSQL("DROP TABLE IF EXISTS `" + CHAT_USER + "`");
        db.execSQL("DROP TABLE IF EXISTS `" + PAYMENT + "`");
        db.execSQL("DROP TABLE IF EXISTS `" + REDIRECT + "`");
        onCreate(db);
    }

}
