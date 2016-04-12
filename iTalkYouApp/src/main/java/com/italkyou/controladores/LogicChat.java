package com.italkyou.controladores;

import android.content.Context;

import com.italkyou.dao.ChatDAO;
import com.italkyou.utils.Const;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by RenzoD on 09/06/2015.
 */
public class LogicChat {

    public static final String TAG_CHAT = "Chats";

    //Remote columns
    public static final String COLUMN_ADMINISTRATOR = "Administrador";
    public static final String COLUMN_FLAG_ARCHIVED = "flagFiled";
    public static final String COLUMN_TIPO = "Tipo";
    public static final String COLUMN_ANNEX = "Anexo";
    public static final String COLUMN_UPDATED_AT = "updatedAt";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String TAG_CHAT_ARCHIVED = "chat_archived";
    public static final String TAG_CHAT_NO_ARCHIVED = "chat_no_archived";
    public static final String COLUMN_LAST_DATE_MESSAGE = "lastDateMessage";
    public static final String COLUMN_MIEMBROS_ID = "MiembrosId";
    public static final String TAG_CHAT_USER = "ChatUser";

    //Local columns
    public static final String CHATUSER_COLUMN_USERID = "userId";
    public static final String CHATUSER_COLUMN_CHATID = "chatId";
    public static final String CHATUSER_COLUMN_STATUS = "status";
    public static final String CHATUSER_COLUMN_ANNEX = "annex";
    public static final String CHATUSER_COLUMN_FLAGIMAGE = "flagImage";
    public static final String CHATUSER_COLUMN_TYPE = "type";
    public static final String CHATUSER_COLUMN_LASTMESSAGE = "lastMessage";
    public static final String CHATUSER_COLUMN_NAME = "name";
    public static final String CHATUSER_COLUMN_IMAGE = "imageChat";
    public static final String CHATUSER_COLUMN_MEMBERSID = "membersId";
    public static final String CHATUSER_COLUMN_MEMBERS = "members";
    public static final String CHATUSER_COLUMN_ADMINISTRATOR = "administrator";
    public static final String CHATUSER_COLUMN_LASTDATEMESSAGE = "lastDateMessage";
    public static final String CHATUSER_COLUMN_UPDATEDAT = "updatedAt";


    /**
     * @param c Context of the application,
     * @return affected rows
     */
    public static int insertChats(Context c, List<ParseObject> objs) {
        return new ChatDAO(c).insertChat(objs);
    }



    /**
     * Obtiene el n�mero de registros
     * @param c Contexto de la aplicaci�n.
     * @return un entero que indica la cantidad de registros
     */
    public static int countChats(Context c) {
        return new ChatDAO(c).getNumberChats();
    }

    public static void archiveChat(String objectId, final OnParseListener listener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TAG_CHAT_USER);
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    listener.onDone(parseObject, null);
                } else {
                    listener.onError(e);
                }
            }
        });


    }

    public static void getAllArchivedChats(String administrator, final OnParseListener listener, String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
        query.whereEqualTo(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData("Usuarios", userId));
        query.whereEqualTo(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ARCHIVED);
        query.orderByDescending(COLUMN_LAST_DATE_MESSAGE);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(TAG_CHAT_ARCHIVED, parseObjects);
                    listener.onDone(null, parseObjects);
                } else
                    listener.onError(e);
            }
        });
    }

    public static void deleteChat(ParseObject chatObject, final OnParseListener listener) {
        chatObject.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_DELETED);
        chatObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    listener.onDone(null, null);
                else
                    listener.onError(e);
            }
        });
    }



    /**
     * Este metodo lista los chat registrados.
     * @param c Contexto de la aplicacion
     * @return Lista de objectos Parse.
     */
    public static List<ParseObject> getAllChats(Context c) {
        return new ChatDAO(c).listAllChats();
    }

    public static void dropAll(Context c) {
        new ChatDAO(c).drop(true);
    }


    /**
     * Inteface para los callbacks de parse.
     */
    public interface OnParseListener {
        void onDone(ParseObject chatObject, List<ParseObject> parseObjects);
        void onError(ParseException ex);
    }

}
