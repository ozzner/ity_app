package com.italkyou.dao;

import android.os.Environment;

public class TablasBD {

    //Base de Datos
    //public static String BD_NOMBRE_ITY  = "ity_database.db";    /*ruta interna */

    public static int BD_VERSION_ITY = 3;
        public static String BD_NOMBRE_ITY = Environment.getExternalStorageDirectory().getPath() + "/ity_database_v" + BD_VERSION_ITY + ".db";

    //campos de la base de datos
    public static String COLUMNA_CLAVE = "CLAVE";
    public static String COLUMNA_ESTADO = "ESTADO";
    public static String TBL_CONTACTO = "TBL_CONTACTO";

    // TABLA DE USUARIO
    public static String CREACION_TBL_USUARIO = "CREATE TABLE TBL_USUARIO (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ID_USUARIO    	TEXT," +
            "NOMBRES    	TEXT," +
            "ANEXO    		TEXT," +
            "DESCRIPCION    TEXT," +
            "RUTA_IMAGEN    TEXT," +
            "PIN_SIP    	TEXT," +
            "PIN_LLAMADA    TEXT," +
            "ID_IDIOMA    	TEXT," +
            "NUMERO    		TEXT," +
            "PREFIJO_PAIS 	TEXT," +
            "NOTIFICACION   TEXT," +
            "CORREO		    TEXT," +
            "O_CK       	TEXT," +
            "CLAVE       	TEXT," +
            "ESTADO       	TEXT," +
            "SALDO          TEXT)";

    // TABLA DE PAISES
    public static String TBL_PAIS = "CREATE TABLE TBL_PAIS (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ID_PREFIJO   		TEXT," +
            "DESCRIPCION_ES 	TEXT," +
            "DESCRIPCION_EN 	TEXT," +
            "ID_GTM 			TEXT," +
            "MCC	 			TEXT," +
            "ID_PAIS	    	TEXT)";

    public static String CREACION_TBL_CONTACTO = "CREATE TABLE TBL_CONTACTO (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ID_CONTACTO   		INTEGER," +
            "NOMBRE			 	TEXT," +
            "NUMERO			 	TEXT," +
            "LOOKUP_KEY			TEXT," +
            "FLAG_ITY			TEXT," +
            "FLAG_FOTO	    	INTEGER," +
            "FLAG_SINITY        INTEGER)";

    public static String TBL_TELEFONO = "CREATE TABLE TBL_TELEFONO (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ID_CONTACTO   		INTEGER," +
            "NOMBRE_ITY			TEXT," +
            "LOOKUP_KEY			TEXT," +
            "NUMERO			 	TEXT," +
            "ANEXO			 	TEXT," +
            "CONECTADO			TEXT)";


    //rsantillanc
    public static String PAYMENT_CREATE = "CREATE TABLE `PAYMENT` (\n" +
            "\t`id`\tINTEGER DEFAULT 0 PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`paymentID`\tTEXT NOT NULL DEFAULT 'none',\n" +
            "\t`monto`\tREAL NOT NULL DEFAULT 0.0,\n" +
            "\t`description`\tTEXT,\n" +
            "\t`status`\tINTEGER NOT NULL DEFAULT 0\n" +
            ");";


    public static String CHAT_USER_CREATE = "CREATE TABLE \"CHAT_USER\" (\n" +
            "\t`objectId`\tTEXT NOT NULL UNIQUE,\n" +
            "\t`administrator`\tTEXT NOT NULL DEFAULT 000000,\n" +
            "\t`annex`\tINTEGER NOT NULL DEFAULT 000000 UNIQUE,\n" +
            "\t`chatObjectId`\tREAL NOT NULL,\n" +
            "\t`flagImage`\tINTEGER NOT NULL DEFAULT 0,\n" +
            "\t`imagePath`\tTEXT,\n" +
            "\t`lastDateMessage`\tTEXT ,\n" +
            "\t`lastMessage`\tTEXT ,\n" +
            "\t`members`\tINTEGER NOT NULL,\n" +
            "\t`membersId`\tTEXT NOT NULL,\n" +
            "\t`name`\tTEXT,\n" +
            "\t`status`\tINTEGER NOT NULL DEFAULT 0,\n" +
            "\t`type`\tTEXT NOT NULL DEFAULT 'PRIVADO',\n" +
            "\t`userObjectId`\tTEXT NOT NULL,\n" +
            "\t`createdAt`\tTEXT NOT NULL,\n" +
            "\t`updatedAt`\tTEXT\n" +
            ");";


    public static String REDIRECT_CREATE = "CREATE TABLE `REDIRECT` (\n" +
            "\t`redirectId`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`zipcode`\tINTEGER NOT NULL,\n" +
            "\t`phoneNumber`\tINTEGER NOT NULL,\n" +
            "\t`userId`\tINTEGER NOT NULL UNIQUE,\n" +
            "\t`fromDate`\tTEXT,\n" +
            "\t`toDate`\tTEXT,\n" +
            "\t`flagPermanent`\tINTEGER NOT NULL DEFAULT 0,\n" +
            "\t`flagActive`\tINTEGER NOT NULL DEFAULT 0\n" +
            ");";


    public static String CHAT_MESSAGE_CREATE = "CREATE TABLE `CHAT_MESSAGES` (\n" +
            "\t`objectId`\tTEXT NOT NULL UNIQUE,\n" +
            "\t`filePath`\tTEXT,\n" +
            "\t`chatObjectId`\tTEXT,\n" +
            "\t`message`\tTEXT NOT NULL,\n" +
            "\t`type`\tTEXT NOT NULL DEFAULT 'TEXTO',\n" +
            "\t`userObjectId`\tTEXT NOT NULL,\n" +
            "\t`members`\tTEXT NOT NULL,\n" +
            "\t`createdAt`\tTEXT NOT NULL,\n" +
            "\t`updatedAt`\tTEXT NOT NULL,\n" +
            "\tPRIMARY KEY(objectId)\n" +
            ");";


}
