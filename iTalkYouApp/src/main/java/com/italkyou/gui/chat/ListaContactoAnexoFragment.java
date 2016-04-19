package com.italkyou.gui.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.salidas.SalidaDatosChatGrupal;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.PrincipalFragment;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.AdaptadorListaCheckBox;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ListaContactoAnexoFragment extends Fragment
        implements
        OnItemClickListener,
        OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ListaContactoAnexoFragment.class.getSimpleName();
    private SearchView searchView;
    private List<Object> listaContactos;
    private List<Object> listaContactosBusq;
    private AdaptadorLista adaptador;
    private AdaptadorListaCheckBox adaptadorchk;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String pantalla;
    private SalidaDatosChatGrupal seleccionados;
    private BeanUsuario usuario;
    public String phoneNumber;
    private ListView listItyContacts;
    private int LIMIT_USERS = 1000;
    private ProgressBar pbLoader;
    private AppiTalkYou app;
    private ParseObject userSelected;
    private static final int[] ICONS = {R.color.italkyou_primary_purple, R.color.iTalkYou_Azul_Suave};
    private ImageView ivDial;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listado_contacto, container, false);
        app = (AppiTalkYou) getActivity().getApplication();
        usuario = app.getUsuario();
        ivDial = (ImageView) rootView.findViewById(R.id.iv_contact_dial);

        pantalla = getArguments().getString(Const.DATOS_TIPO);//TIPO_PANTALLA


        if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)) {
            seleccionados = (SalidaDatosChatGrupal) getArguments().getSerializable(Const.DATOS_GRUPO_CHAT);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(ICONS);

        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        listItyContacts = (ListView) rootView.findViewById(R.id.listcontactos);
        listItyContacts.setVisibility(View.GONE);
        listItyContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listItyContacts.setItemsCanFocus(false);

        pbLoader.setVisibility(View.VISIBLE);
        ivDial.setVisibility(View.GONE);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        ActionBar mActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        obtenerUsuariosChat();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL))
            getActivity().getMenuInflater().inflate(R.menu.mnbuscar_agregar, menu);
        else getActivity().getMenuInflater().inflate(R.menu.mnanexo, menu);

        MenuItem buscarItem = menu.findItem(R.id.itBuscar);
        searchView = (SearchView) MenuItemCompat.getActionView(buscarItem);
        searchView.setQueryHint(getString(R.string.hnt_buscar_contactos));
        searchView.setOnQueryTextListener(this);

    }

    private void obtenerUsuariosChat() {

        mSwipeRefreshLayout.setRefreshing(true);
        listaContactos = new ArrayList<>();

        ParseQuery<ParseObject> query;
        if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)
                && !seleccionados.getIdChat().equals(Const.cad_vacia)) {

            Object[] lst = seleccionados.getLstMiembros().toArray();
            query = new ParseQuery<>(ChatITY.TABLE_USER);
            query.whereNotContainedIn(ChatITY.USER_ANNEX, Arrays.asList(lst));
            query.orderByAscending(ChatITY.USER_USER);

        } else {

            query = ParseQuery.getQuery(ChatITY.TABLE_USER);
            query.setLimit(LIMIT_USERS);
            query.orderByAscending(ChatITY.USER_USER);

        }


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {

                if (e == null) {
                    if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)
                            && !seleccionados.getIdChat().equals(Const.cad_vacia)) {

                        List<ParseObject> lista = new ArrayList();

                        for (int i = 0; i < users.size(); i++) {
                            ParseObject usuarioITY = users.get(i);
                            /* add only where status is connect*/
//							if (usuarioITY.getString(ChatITY.USER_STATUS).equals(Const.status_connected))
                            lista.add(usuarioITY);
                        }

                        users.clear();
                        users.addAll(lista);

                    } else {

                        int posEliminar = 0;

                        for (int i = 0; i < users.size(); i++) {
                            ParseObject usuarioITY = users.get(i);

                            if (usuarioITY.getString(ChatITY.USER_ANNEX).equals(usuario.getAnexo())) {
                                posEliminar = i;
                                break;
                            }
                        }
                        users.remove(posEliminar);
                    }


                    listaContactos = new ArrayList<>();
                    listaContactos.addAll(users);
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.message_list_updated, Toast.LENGTH_SHORT).show();
                }


                listItyContacts.setVisibility(View.VISIBLE);
                pbLoader.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                inicializarComponentes();

            }
        });


    }

    private void inicializarComponentes() {
        listaContactosBusq = new ArrayList<>();
        listaContactosBusq.addAll(listaContactos);

        if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)) {
            adaptadorchk = new AdaptadorListaCheckBox(getActivity().getApplicationContext(), R.layout.celda_contacto_italkyou_chatgrupal, listaContactosBusq, ParseObject.class.getSimpleName(), Const.USER_ITY);
            adaptadorchk.setActivar(Const.mostrar_check);
            listItyContacts.setAdapter(adaptadorchk);
            realizarCheck();

        } else {

            adaptador = new AdaptadorLista(getActivity().getApplicationContext(), R.layout.celda_contacto_italkyou, listaContactosBusq, ParseObject.class.getSimpleName(), Const.USER_ITY);
            listItyContacts.setAdapter(adaptador);
            listItyContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listItyContacts.setOnItemClickListener(this);

        }

    }

    private void realizarCheck() {

        List<Object> listado = seleccionados.getListaContactos();

        if (listado.size() > 0) {
            for (int i = 0; i < listado.size(); i++) {
                ParseObject contacto = (ParseObject) listado.get(i);
                int pos = obtenerPosicionContacto(contacto.getString(ChatITY.USER_ANNEX));
                if (pos >= 0)
                    adaptadorchk.activarCheck(pos);
            }
            adaptadorchk.notifyDataSetChanged();
        }
    }

    private int obtenerPosicionContacto(String anexo) {

        int pos = -1;
        for (int i = 0; i < listaContactosBusq.size(); i++) {
            ParseObject contacto = (ParseObject) listaContactosBusq.get(i);
            if (contacto.getString(ChatITY.USER_ANNEX).equals(anexo)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public boolean onQueryTextChange(String texto) {

        if (!texto.trim().equals(Const.cad_vacia)) {

            List<Object> lista = new ArrayList<>();

            for (Object item : listaContactos) {
                ParseObject contacto = (ParseObject) item;
                String nombre = (contacto.getString(ChatITY.USER_USER)).toUpperCase();
                String text = texto.toUpperCase();
                if (nombre.contains(text)) {
                    lista.add(contacto);
                } else if ((contacto.getString(ChatITY.USER_PHONE)).contains(texto)) {
                    lista.add(contacto);
                } else if ((contacto.getString(ChatITY.USER_ANNEX)).contains(texto)) {
                    lista.add(contacto);
                }
            }
            listaContactosBusq.clear();
            if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)) {
                adaptadorchk.notifyDataSetChanged();
                listaContactosBusq.addAll(lista);
                adaptadorchk.notifyDataSetChanged();
            } else {
                adaptador.notifyDataSetChanged();
                listaContactosBusq.addAll(lista);
                adaptador.notifyDataSetChanged();
            }
        } else {
            if (pantalla.equals(Const.PANTALLA_CHAT_GRUPAL)) {
                listaContactosBusq.clear();
                adaptadorchk.notifyDataSetChanged();
                listaContactosBusq.addAll(listaContactos);
                adaptadorchk.notifyDataSetChanged();
            } else {
                listaContactosBusq.clear();
                adaptador.notifyDataSetChanged();
                listaContactosBusq.addAll(listaContactos);
                adaptador.notifyDataSetChanged();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {

        Bundle bundle = getArguments();
        boolean b = bundle.getBoolean(Const.DATOS_IS_SMS);

        //Se envia a SMS
        if (b) {

            ParseObject obj = (ParseObject) listaContactosBusq.get(posicion);
            phoneNumber = obj.getString("Telefono");

            Bundle arg = new Bundle();
            arg.putString(Const.KEY_PHONENUMBER, phoneNumber);
            arg.putString(Const.KEY_PHONENUMBER, phoneNumber);

            Fragment fragmento = PrincipalFragment.nuevaInstancia(Const.indice_3, arg);
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.contenedor_fragmentos, fragmento);
            transaction.commit();


            Log.e(TAG, "Number : " + phoneNumber);

        } else {
            ParseObject opcion = (ParseObject) listaContactosBusq.get(posicion);
            iniciarChat(opcion);
        }


    }

    private void iniciarChat(ParseObject userSelected) {
        this.userSelected = userSelected;

        app = (AppiTalkYou) getActivity().getApplication();
        final ParseObject myUserParse = app.getUsuarioChat();

        if (myUserParse == null)
            initUserParse(app.getUsuario().getAnexo());
        else
            initConversation(userSelected);

    }

    private void initUserParse(String anexo) {
        ParseQuery<ParseObject> queryUsuarios = ParseQuery.getQuery(ChatITY.TABLE_USER);
        queryUsuarios.whereEqualTo(ChatITY.USER_ANNEX, anexo);
        queryUsuarios.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(final ParseObject myUserParse, ParseException e) {

                if (e == null) {
                    app.setUsuarioChat(myUserParse);
                    initConversation(userSelected);
                }
            }
        });
    }

    private void initConversation(final ParseObject userSelected) {

        if (AppUtil.existeConexionInternet(getActivity())) {
            ParseQuery<ParseObject> queryLstChats = ParseQuery.getQuery(ChatITY.tabla_chats);
            queryLstChats.whereContainsAll(ChatITY.identificador_miembros_id,
                    Arrays.asList(app.getUsuario().getAnexo(),
                            userSelected.getString(ChatITY.USER_ANNEX)));
            queryLstChats.whereContains(LogicChat.COLUMN_TIPO, ChatITY.tipo_privado);
            queryLstChats.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject chat, ParseException e) {
                    {

                        if (e == null) {
                            //Existe el chat
                            LogicaPantalla.personalizarIntentListaMensajes(getActivity(), chat.getObjectId(), false, chat.getString(LogicChat.COLUMN_TIPO), true);
                        } else {

                            //creamos el chat
                            final ParseObject currentChat = new ParseObject(ChatITY.tabla_chats);
                            currentChat.put(ChatITY.identificador_admin, app.getUsuario().getAnexo());
                            currentChat.put(ChatITY.identificador_miembros_id, Arrays.asList(app.getUsuario().getAnexo(), userSelected.getString(ChatITY.USER_ANNEX)));
                            currentChat.put(ChatITY.identificador_miembros, Arrays.asList(app.getUsuarioChat(), userSelected));
                            currentChat.put(ChatITY.identificador_nombre_chat, ChatITY.tipo_privado);
                            currentChat.put(ChatITY.identificador_tipo, ChatITY.tipo_privado);
                            currentChat.put(ChatITY.identificador_ultimo_mensaje, Const.cad_vacia);
                            currentChat.put(ChatITY.USER_FLAG_IMAGE, false);
//                            chatObject.put(ChatITY.identificador_miembros_nombre, Const.cad_vacia);
//                            chatObject.put(LogicChat.COLUMN_FLAG_ARCHIVED, false);

                            currentChat.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {
                                        makePushNotification(currentChat);
                                        LogicaPantalla.personalizarIntentListaMensajes(getActivity(), currentChat.getObjectId(), false, currentChat.getString(LogicChat.COLUMN_TIPO), true);
                                        //Me
                                        final ParseObject chat_user1 = new ParseObject(LogicChat.TAG_CHAT_USER);
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR, app.getUsuario().getAnexo());
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_CHATID, ParseObject.createWithoutData(LogicChat.TAG_CHAT, currentChat.getObjectId()));
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData("Usuarios", app.getUsuarioChat().getObjectId()));
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_ANNEX, app.getUsuario().getAnexo());
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, false);
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_TYPE, "PRIVADO");
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_MEMBERSID, Arrays.asList(app.getUsuario().getAnexo(), userSelected.getString(ChatITY.USER_ANNEX)));
                                        chat_user1.put(LogicChat.CHATUSER_COLUMN_MEMBERS, Arrays.asList(app.getUsuarioChat(), userSelected));

                                        //You
                                        ParseObject chat_user2 = new ParseObject(LogicChat.TAG_CHAT_USER);
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR, app.getUsuario().getAnexo());
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_CHATID, ParseObject.createWithoutData(LogicChat.TAG_CHAT, currentChat.getObjectId()));
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData("Usuarios", userSelected.getObjectId()));
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_ANNEX, userSelected.getString("Anexo"));
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, false);
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_TYPE, "PRIVADO");
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_MEMBERSID, Arrays.asList(app.getUsuario().getAnexo(), userSelected.getString(ChatITY.USER_ANNEX)));
                                        chat_user2.put(LogicChat.CHATUSER_COLUMN_MEMBERS, Arrays.asList(app.getUsuarioChat(), userSelected));


                                        //Save
                                        chat_user1.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null)
                                                    //Save local
                                                    chat_user1.pinInBackground(LogicChat.CHAT_ACTIVE);

                                            }
                                        });
                                        chat_user2.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null)
                                                    Log.e("Erro chat_user", "chat");
                                            }
                                        });

                                    }

                                }
                            });
                        }
                    }
                }
            });


        } else {
            Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cargarFragmento();
                break;
            case R.id.itAgregar:
                agregarParticipantes();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    public void makePushNotification(ParseObject currentChat) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("channels", Const.TAG_MY_CHANNEL + userSelected.get("Anexo"));
        String message = "Chat aperturado.";

        String accionEnviar =
                "{\"mensaje\": \"" + message + "\"," +
                        " \"action\": \"com.iTalkYou.UPDATE_STATUS\"," +
                        " \"chatId\":\"" + currentChat.getObjectId() + "\"," +
                        " \"type\":\"" + currentChat.getString(LogicChat.COLUMN_TIPO) + "\"," +
                        " \"nombre\": \"" + userSelected.getString("Usuario") + "\"}";

        JSONObject data;
        try {
            data = new JSONObject(accionEnviar);
            ParsePush androidPush = new ParsePush();
            androidPush.setData(data);
            androidPush.setQuery(pushQuery);
            androidPush.sendInBackground();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    private void agregarParticipantes() {

        boolean[] valores = adaptadorchk.getValores();
        List<Object> lista = new ArrayList<>();
//        if (seleccionados.getLstMiembros().size() > 0) {
//            lista.addAll(seleccionados.getListaContactos().subList(0, seleccionados.getLstMiembros().size()));
//        } else {
//            lista.addAll(seleccionados.getListaContactos());
//        }

        for (int i = 0; i < valores.length; i++) {
            if (valores[i]) {
                lista.add(listaContactosBusq.get(i));
            }
        }

        seleccionados.setListaContactos(lista);
        if (lista.size() > 0)
            cargarFragmento();
        else
            Crouton.showText(getActivity(), getString(R.string.msj_no_seleccionados), Style.ALERT);
    }

    public static ListaContactoAnexoFragment nuevaInstancia(String pantalla, boolean isSMS) {
        ListaContactoAnexoFragment fragmento = new ListaContactoAnexoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.DATOS_TIPO, pantalla);//Chat simple
        bundle.putBoolean(Const.DATOS_IS_SMS, isSMS);
        fragmento.setArguments(bundle);
        return fragmento;
    }

    public static ListaContactoAnexoFragment nuevaInstancia(String pantalla, SalidaDatosChatGrupal datosChat) {

        ListaContactoAnexoFragment fragmento = new ListaContactoAnexoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.DATOS_TIPO, pantalla);
        bundle.putSerializable(Const.DATOS_GRUPO_CHAT, datosChat);
        fragmento.setArguments(bundle);

        return fragmento;
    }

    private void cargarFragmento() {

        Fragment fragmento;

        if (pantalla.equals(Const.PANTALLA_CHAT_SIMPLE)) {

            Bundle b = getArguments();
            boolean on = b.getBoolean(Const.DATOS_IS_SMS);

            if (on) {
                Bundle arg = new Bundle();
                arg.putString(Const.KEY_PHONENUMBER, phoneNumber);
                fragmento = PrincipalFragment.nuevaInstancia(Const.indice_3, arg);

            } else {
                fragmento = PrincipalFragment.nuevaInstancia(Const.indice_0);
            }


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
            fragmentTransaction.commit();


        } else {

            fragmento = CrearChatGrupalFragment.nuevaInstancia(seleccionados);
            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
            fragmentTransaction.commit();
        }

    }

    public void onBackPressed() {
        cargarFragmento();
    }


    @Override
    public void onRefresh() {
        obtenerUsuariosChat();
    }
}
