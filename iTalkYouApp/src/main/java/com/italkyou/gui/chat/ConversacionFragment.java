package com.italkyou.gui.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.popup.MenuActionsChat;
import com.italkyou.beans.salidas.SalidaDatosChatGrupal;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorListaChat;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ConversacionFragment extends Fragment
        implements
        OnClickListener,
        OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        CustomAlertDialog.OnDialogListener {

    private static final String TAG = ConversacionFragment.class.getSimpleName();
    private static final int ACC_DELETE = 1;
    private static final int ACC_ARCHIVE = 0;
    private static final int[] ICONS = {R.color.italkyou_primary_purple, R.color.iTalkYou_Azul_Suave};

    private ImageView btnAgregarContacto, btnCrearGrupo;
    private static AdaptadorListaChat adaptador;
    private static List<ParseObject> chatsList = new ArrayList<>();
    private static String[] LABELS = null;
    private static final int[] ICONS_CHAT = {R.drawable.ic_archive, R.drawable.ic_delete_white};
    private List<Object> listMenuActions;
    private BeanUsuario usuario;
    private static AppiTalkYou app;
    private CustomAlertDialog customDialog;
    private ParseObject chatItem;
    private int currentPosition;
    private ProgressBar pbLoader;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isFromReresh = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.conversacion, container, false);
        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        mListView = (ListView) rootView.findViewById(R.id.listConversaciones);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS);
        pbLoader.setVisibility(View.GONE);
//        mListView.setVisibility(View.GONE);

        app = (AppiTalkYou) getActivity().getApplication();
        usuario = app.getUsuario();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LABELS = getActivity().getResources().getStringArray(R.array.array_menu_actions_chat);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);//Enable menu
        inicializarComponentes();
        getChats();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_chat, menu);
//        int color = getResources().getColor(R.color.pantone_240C);
//        int alpha = 255; //100%
//        MenuColorizer.colorMenu(getActivity(), menu, color, alpha);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.acc_archive:
                opentArchivedChats();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void opentArchivedChats() {
        Intent ar = new Intent(getActivity(), ArchiveActivity.class);
        startActivity(ar);
    }

    private void inicializarComponentes() {

        try {
            isFromReresh = false;
            btnAgregarContacto = (ImageView) getActivity().findViewById(R.id.btnAgregarContacto);
            btnAgregarContacto.setOnClickListener(this);
            btnCrearGrupo = (ImageView) getActivity().findViewById(R.id.btnCrearGrupo);
            btnCrearGrupo.setOnClickListener(this);


            adaptador = new AdaptadorListaChat(getActivity().getApplicationContext(), R.layout.celda_chat, chatsList, usuario.getAnexo());
            mListView.setAdapter(adaptador);
            mListView.setOnItemClickListener(this);
            mListView.setOnItemLongClickListener(this);

        } catch (Exception e) {

        }
    }

    public void getChats() {

        if (AppUtil.existeConexionInternet(getActivity())) {

            if (LogicChat.countChats(getActivity()) > 0)
                loadChatsFromLocalStore();
            else {
                fetchChats();
            }

        } else {
            Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_internet), Style.ALERT);
            loadChatsFromLocalStore();
        }
    }

    private void loadChatsFromLocalStore() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
        query.fromPin(LogicChat.TAG_CHAT_NO_ARCHIVED);
        query.orderByDescending(LogicChat.CHATUSER_COLUMN_UPDATEDAT);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> currentChatList, ParseException e) {

                if (e == null) {
                    chatsList.clear();
                    chatsList.addAll(currentChatList);
                    adaptador.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    AppUtil.mostrarAvisoInformativo(getActivity(), getActivity().getString(R.string.message_chat_no_found));
                }

            }
        });


//        chatsList.clear();
//    chatsList.addAll(LogicChat.getAllChats(getActivity()));
//    adaptador.notifyDataSetChanged();
    }


    private List<ParseObject> loadChatsFromServer() {
        List<ParseObject> ChatList = new ArrayList<>();

        try {
            ParseQuery<ParseObject> queryLstChats = new ParseQuery(LogicChat.TAG_CHAT_USER);
            queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData("Usuarios", app.getUsuarioChat().getObjectId()));
            queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
            queryLstChats.orderByDescending(LogicChat.CHATUSER_COLUMN_UPDATEDAT);
            ChatList = queryLstChats.find();
            app.setIsChatEnable(true);

            if (!ChatList.isEmpty())
                LogicChat.insertChats(getActivity(), ChatList);

            //Save in the LocalStore all objects, Excellent!!!!.
            try {
                ParseObject.unpinAll(LogicChat.TAG_CHAT_NO_ARCHIVED);
                ParseObject.pinAllInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED, ChatList);
            } catch (ParseException e1) {
                e1.printStackTrace();
                ParseObject.pinAllInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED, ChatList);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ChatList;
    }

    public static ConversacionFragment nuevaInstancia() {
        ConversacionFragment fragmento = new ConversacionFragment();
        return fragmento;
    }

    @Override
    public void onItemClick(AdapterView<?> adv, View view, int posicion, long id) {
        currentPosition = posicion;
//		updateAlFlags();
        ParseObject item = (ParseObject) adv.getItemAtPosition(posicion);
        ParseObject chatObject = item.getParseObject("chatId");
        String chatId = chatObject.getObjectId();
        LogicaPantalla.personalizarIntentListaMensajes(getActivity(), chatId, false, item.getString(LogicChat.CHATUSER_COLUMN_TYPE));

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        listMenuActions = new ArrayList<>();
        currentPosition = position;

        for (int idx = 0; idx < LABELS.length; idx++)
            listMenuActions.add(new MenuActionsChat(LABELS[idx], ICONS_CHAT[idx]));

        chatItem = (ParseObject) parent.getItemAtPosition(position);
        showCustomDialog(Const.INDEX_DIALOG_CHAT);
        return true;
    }



    /*Borrar este metodod despues de realizar pruebas*/
    private void updateAlFlags() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Chats");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (ParseObject chat : list) {
                    chat.put("flagFiled", false);
                    chat.saveInBackground();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == btnAgregarContacto) {
            cargarFragmentoContactos();
        } else if (v == btnCrearGrupo) {
            cargarFragmentoCrearGrupo();
        }

    }


    /**
     * Este metodo actualiza la mListView desde otras actividades o framentos
     */
    public static void reloadListFromOut() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
        query.fromPin(LogicChat.TAG_CHAT_NO_ARCHIVED);
        query.orderByDescending(LogicChat.CHATUSER_COLUMN_UPDATEDAT);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> chatList, ParseException e) {

                if (e == null) {
                    chatsList.clear();
                    chatsList.addAll(chatList);
                    adaptador.notifyDataSetChanged();
                }
            }
        });
    }

    private void cargarFragmentoCrearGrupo() {
        Fragment fragmento = CrearChatGrupalFragment.nuevaInstancia(new SalidaDatosChatGrupal());
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();
    }


    private void cargarFragmentoContactos() {

        Fragment fragmento = ListaContactoAnexoFragment.nuevaInstancia(Const.PANTALLA_CHAT_SIMPLE, false);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();
    }

    protected void showCustomDialog(int type, String... array) {

        switch (type) {
            case Const.INDEX_DIALOG_CHAT:
                customDialog = CustomAlertDialog.newIntance(Const.INDEX_DIALOG_CHAT, listMenuActions);
                customDialog.setDialogListener(this);
                customDialog.show(getFragmentManager(), Const.TAG_MENU_ACTIONS);
                break;
        }

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
        if (index == ACC_ARCHIVE) {

            LogicChat.archiveChat(chatItem.getObjectId(), new LogicChat.OnParseListener() {
                @Override
                public void onDone(ParseObject chatObject, List<ParseObject> parseObjects) {
                    reloadList();
                    chatObject.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ARCHIVED);
                    chatObject.saveInBackground();
                    chatObject.unpinInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED);
                }

                @Override
                public void onError(ParseException ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        } else {

            LogicChat.deleteChat(chatItem, new LogicChat.OnParseListener() {

                @Override
                public void onDone(ParseObject chatObject, List<ParseObject> parseObjects) {
                    reloadList();
                    chatItem.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_DELETED);
                    chatItem.unpinInBackground(LogicChat.TAG_CHAT_ARCHIVED);
                    chatItem.unpinInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED);
                }

                @Override
                public void onError(ParseException ex) {
                    Toast.makeText(getActivity(), R.string.error_deleting, Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void reloadList() {
        chatsList.remove(currentPosition);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onRadioButtonClickAlert(int index) {

    }

    @Override
    public void onRefresh() {
        fetchChats();
    }

    private void fetchChats() {
        AsyncFetchChats async = new AsyncFetchChats();
        async.execute();


    }


    /*AsyncTask*/
    class AsyncFetchChats extends AsyncTask<Boolean, List<ParseObject>, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isFromReresh = true;
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<ParseObject> doInBackground(Boolean... params) {
            return loadChatsFromServer();
        }

        @Override
        protected void onPostExecute(List<ParseObject> results) {
            super.onPostExecute(results);

            mSwipeRefreshLayout.setRefreshing(false);
            chatsList.clear();
            chatsList.addAll(results);
            adaptador.notifyDataSetChanged();
            if (results.size() > 0)
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_list_updated), Toast.LENGTH_SHORT).show();

        }

    } //end AsyncTask

//
//    void showLoader(boolean flag) {
//        //Set visibility
//        if (flag) {
////            pbLoader.setVisibility(View.VISIBLE);
//            mListView.setVisibility(View.GONE);
//        } else {
////            pbLoader.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//        }
//
//    }
}
