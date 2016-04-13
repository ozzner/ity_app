package com.italkyou.gui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.popup.MenuActionsChat;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorListaChat;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.utils.Const;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends BaseActivity implements
        LogicChat.OnParseListener,
        AdapterView.OnItemClickListener,
        CustomAlertDialog.OnDialogListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemLongClickListener {

    //Const
    private static final String TAG = ArchiveActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static final int[] ICONS = {R.color.pantone_240C, R.color.pantone_7684C};

    //Objects
    private static List<ParseObject> listChats = new ArrayList<>();
    private static AdaptadorListaChat adapter;
    private BeanUsuario usuario;
    private AppiTalkYou app;
    private ParseObject chatItem;
    private ParseObject chatUserItem;


    //Views
    private ListView mListView;
    private Context _context;
    private int currentPosition;
    private static ProgressBar pbLoader;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    //Config-special popup
    private CustomAlertDialog customDialog;
    private List<Object> listMenuActions;
    private static final int[] ICONS_CHAT = {R.drawable.ic_restore, R.drawable.ic_delete_white};
    private static String[] LABELS = null;
    private static final int ACC_UNARCHIVE = 0;
    private boolean flagChanged = false;
    private Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        initValues();
        initViews();
        configComponents();
    }

    private void initValues() {
        app = (AppiTalkYou) getApplication();
        _context = getApplicationContext();
        mActivity = this;

        LABELS = getResources().getStringArray(R.array.array_menu_actions_chat_archived);
        usuario = app.getUsuario();
        if (usuario == null)
            usuario = LogicaUsuario.obtenerUsuario(_context);

        LogicChat.getAllArchivedChats(usuario.getAnexo(), this, app.getUsuarioChat().getObjectId());
    }


    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_archived_chats);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    private void configComponents() {
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        showLoader(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_archive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else {
            cargarFragmentoPrincipal();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDone(ParseObject chatObject, List<ParseObject> parseObjects) {
        listChats = parseObjects;

        if (parseObjects.size() == 0)
            Toast.makeText(_context, R.string.message_chat_no_found, Toast.LENGTH_LONG).show();


        //Config listView
        adapter = new AdaptadorListaChat(_context, R.layout.celda_chat, listChats, usuario.getAnexo(), true);
        mListView.setAdapter(adapter);

        //hide
        showLoader(false);
        showRefresh(false);
    }

    @Override
    public void onError(ParseException ex) {

        //Get from local
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_ARCHIVED);
        query.fromPin(LogicChat.TAG_CHAT_ARCHIVED);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                listChats = parseObjects;

                //Config listView
                adapter = new AdaptadorListaChat(_context, R.layout.celda_chat, listChats, usuario.getAnexo(), true);
                mListView.setAdapter(adapter);

                //hide
                showLoader(false);
                showRefresh(false);
            }
        });

        Toast.makeText(_context, R.string.error_unknow, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentPosition = position;
        chatUserItem = (ParseObject) parent.getItemAtPosition(position);
        chatItem = chatUserItem.getParseObject(LogicChat.CHATUSER_COLUMN_CHATID);
        LogicaPantalla.personalizarIntentListaMensajes(this, chatItem.getObjectId(), true,chatUserItem.getString(LogicChat.CHATUSER_COLUMN_TYPE));

    }

    private void unarchiveChat(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {
                    //remote
                    parseObject.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                    parseObject.saveInBackground();
                    //local
                    parseObject.pinInBackground(LogicChat.CHAT_ACTIVE);
                    parseObject.unpinInBackground(LogicChat.TAG_CHAT_ARCHIVED);

                }

            }
        });
    }

    private void deleteChat(ParseObject chatObject) {
        //remote
        chatObject.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_DELETED);
        chatObject.saveInBackground();
        //local
        chatObject.unpinInBackground(LogicChat.TAG_CHAT_ARCHIVED);
        chatObject.unpinInBackground(LogicChat.CHAT_ACTIVE);
    }

    private void reloadList() {
        listChats.remove(currentPosition);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cargarFragmentoPrincipal();
    }

    protected void showCustomDialog(int type, String... array) {

        switch (type) {
            case Const.INDEX_DIALOG_CHAT:
                customDialog = CustomAlertDialog.newIntance(Const.INDEX_DIALOG_CHAT, listMenuActions);
                customDialog.setDialogListener(this);
                customDialog.show(getSupportFragmentManager(), Const.TAG_MENU_ACTIONS);
                break;
        }

    }

    private void cargarFragmentoPrincipal() {
        if (flagChanged)
            ConversacionFragment.reloadListFromOut();

        finish();
    }

    @Override
    public void onPositive(DialogInterface dialog, int index) {

    }

    @Override
    public void onNegative(DialogInterface dialog) {

    }

    @Override
    public void onNeutral(DialogInterface dialog, int index) {

    }

    @Override
    public void onItemClickAlert(int index) {
        flagChanged = true;
        if (index == ACC_UNARCHIVE) {
            unarchiveChat(chatUserItem.getObjectId());
        } else {
            deleteChat(chatUserItem);
        }

        //Always reload
        reloadList();

    }


    @Override
    public void onRadioButtonClickAlert(int index) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        listMenuActions = new ArrayList<>();
        currentPosition = position;

        for (int idx = 0; idx < LABELS.length; idx++)
            listMenuActions.add(new MenuActionsChat(LABELS[idx], ICONS_CHAT[idx]));

        chatUserItem = (ParseObject) parent.getItemAtPosition(position);
        chatItem = chatUserItem.getParseObject(LogicChat.CHATUSER_COLUMN_CHATID);
        showCustomDialog(Const.INDEX_DIALOG_CHAT);
        return false;
    }


    private void showLoader(final boolean isVisible) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isVisible) {
                    mListView.setVisibility(View.GONE);
                    pbLoader.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    pbLoader.setVisibility(View.GONE);
                }

            }
        });


    }


    @Override
    public void onRefresh() {
        fetchArchivedChats();
    }

    private void fetchArchivedChats() {
        showRefresh(true);
        LogicChat.getAllArchivedChats(usuario.getAnexo(), this, app.getUsuarioChat().getObjectId());
    }

    private void showRefresh(boolean flag) {

        try {
            mSwipeRefreshLayout.setRefreshing(flag);
        } catch (Exception ex) {
            Log.e("Refresh fail", "false");
        }
    }
}
