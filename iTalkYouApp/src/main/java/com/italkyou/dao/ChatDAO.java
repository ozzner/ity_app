package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.italkyou.controladores.LogicChat;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by RenzoD on 09/06/2015.
 */
public class ChatDAO extends BaseDAO {

    //Vars
    private static final String TAG = ChatDAO.class.getSimpleName();
    private static final String CHAT_USER_TABLE = "CHAT_USER";
    public static final String PARSE_OBJECT_TAG = "ChatUser";

    //Index for location
    private static final int OBJECT_ID_INDEX = 0;
    private static final int ADMINISTRATOR_INDEX = 1;
    private static final int ANNEX_INDEX = 2;
    private static final int CHAT_ID_INDEX = 3;
    private static final int FLAG_IMAGE_INDEX = 4;
    private static final int IMAGE_PATH_INDEX = 5;
    private static final int LAST_DATE_MESSAGE_INDEX = 6;
    private static final int LAST_MESSAGE_INDEX = 7;
    private static final int MEMBERS_INDEX = 8;
    private static final int MEMBERS_ID_INDEX = 9;
    private static final int NAME_INDEX = 10;
    private static final int STATUS_INDEX = 11;
    private static final int TYPE_INDEX = 12;
    private static final int USER_OBJECT_ID_INDEX = 13;
    private static final int CREATED_AT_INDEX = 14;
    private static final int UPDATED_AT_INDEX = 15;

    //fields
    private static final String OBJECT_ID = "objectId";
    private static final String ADMINISTRATOR = "administrator";
    private static final String ANNEX = "annex";
    private static final String CHAT_ID = "chatObjectId";
    private static final String FLAG_IMAGE = "flagImage";
    private static final String IMAGE_PATH = "imagePath";
    private static final String LAST_DATE_MESSAGE = "lastDateMessage";
    private static final String LAST_MESSAGE = "lastMessage";
    private static final String MEMBERS = "members";
    private static final String MEMBERS_ID = "membersId";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String TYPE = "type";
    private static final String USER_OBJECT_ID = "userObjectId";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";

    //Query
    private static final String SELECT_ALL = "SELECT * FROM " + CHAT_USER_TABLE;

    //Columns
    private String[] allColumns = {null};


    public ChatDAO(Context contexto) {
        super(contexto);
    }

    /**
     * Este mutodo inserta una lista de objetos (chats)
     *
     * @param chatObjects
     * @return numero de filas afectadas.
     */
    public int insertChat(List<ParseObject> chatObjects) {
        int affected_rows = 0;

        for (ParseObject chat : chatObjects) {

            ContentValues values = new ContentValues();
            values.put(OBJECT_ID, chat.getObjectId());
            values.put(ADMINISTRATOR, chat.getString(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR));
            values.put(ANNEX, chat.getString(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR));
            values.put(CHAT_ID, chat.getParseObject(LogicChat.CHATUSER_COLUMN_CHATID).getObjectId());
            values.put(FLAG_IMAGE, (chat.getBoolean(LogicChat.CHATUSER_COLUMN_FLAGIMAGE)) ? 1 : 0);//True or false

            if (chat.getBoolean(LogicChat.CHATUSER_COLUMN_FLAGIMAGE))
                values.put(IMAGE_PATH, chat.getParseFile(LogicChat.CHATUSER_COLUMN_IMAGE).getUrl());

            if (chat.getDate(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE) != null)
                values.put(LAST_DATE_MESSAGE, chat.getDate(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE).toString());

            values.put(LAST_MESSAGE, chat.getString(LogicChat.CHATUSER_COLUMN_LASTMESSAGE));
            values.put(MEMBERS, chat.getJSONArray(LogicChat.CHATUSER_COLUMN_MEMBERS).toString());
            values.put(MEMBERS_ID, chat.getJSONArray(LogicChat.CHATUSER_COLUMN_MEMBERSID).toString());
            values.put(NAME, chat.getString(LogicChat.CHATUSER_COLUMN_NAME));
            values.put(STATUS, chat.getInt(LogicChat.CHATUSER_COLUMN_STATUS));
            values.put(TYPE, chat.getString(LogicChat.CHATUSER_COLUMN_TYPE));
            values.put(USER_OBJECT_ID, chat.getParseObject(LogicChat.CHATUSER_COLUMN_USERID).getObjectId());
            values.put(CREATED_AT, chat.getCreatedAt().toString());
            values.put(UPDATED_AT, chat.getUpdatedAt().toString());

            affected_rows = (int) db.insert(CHAT_USER_TABLE, null, values);
        }

        return affected_rows;
    }


    /**
     * Metodo para obtener el numero de registros.
     *
     * @return entero con la cantidad de registros
     */
    public int getNumberChats() {
        Cursor count = getDb().rawQuery(SELECT_ALL, null);
        return count.getCount();
    }

    public List<ParseObject> listAllChats() {
        List<ParseObject> chatList = new ArrayList<>();
        Cursor cu = db.rawQuery(SELECT_ALL, null);

        if (cu.moveToFirst()) {
            do {
                ParseObject chat = ParseObject.createWithoutData(PARSE_OBJECT_TAG, cu.getString(OBJECT_ID_INDEX));
                chat.put(ADMINISTRATOR, cu.getString(ADMINISTRATOR_INDEX));
                chat.put(ANNEX, cu.getString(ANNEX_INDEX));
                chat.put(CHAT_ID, cu.getString(CHAT_ID_INDEX));
                chat.put(FLAG_IMAGE, (cu.getInt(FLAG_IMAGE_INDEX) > 0) ? true : false);//Change 1 = true AND 0 = false

                if (cu.getInt(FLAG_IMAGE_INDEX) > 0)
                    chat.put(IMAGE_PATH, cu.getString(IMAGE_PATH_INDEX));

                chat.put(LAST_DATE_MESSAGE, cu.getString(LAST_DATE_MESSAGE_INDEX));
                chat.put(LAST_MESSAGE, cu.getString(LAST_MESSAGE_INDEX));
                chat.put(MEMBERS, getArray(cu.getString(MEMBERS_INDEX)));
                chat.put(MEMBERS_ID, getArray(cu.getString(MEMBERS_ID_INDEX)));

                if (cu.getString(NAME_INDEX) != null)
                    chat.put(NAME, cu.getString(NAME_INDEX));

                chat.put(STATUS, cu.getInt(STATUS_INDEX));
                chat.put(TYPE, cu.getString(TYPE_INDEX));
                chat.put(USER_OBJECT_ID, cu.getString(USER_OBJECT_ID_INDEX));
                chatList.add(chat);

            } while (cu.moveToNext());

            if (!cu.isClosed())
                cu.close();
        }
        return chatList;
    }


    private Object getArray(String asArray) {
        JSONArray array;
        try {
            array = new JSONArray(asArray);
        } catch (JSONException e) {
            e.printStackTrace();
            array = null;
        }
        return Arrays.asList(array);
    }

    public void drop(boolean isAll) {
        if (isAll)
            db.delete(CHAT_USER_TABLE, null, null);
    }
}
