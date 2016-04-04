package com.italkyou.gui.chat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanMensajeChat;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.popup.MenuActionsChat;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorMensajeChat;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.gui.personalizado.CustomListAdapter;
import com.italkyou.sip.SIPManager;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.italkyou.utils.MenuColorizer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("SimpleDateFormat")
public class ChatMensajeActivity extends ActionBarActivity implements
        OnClickListener, CustomListAdapter.OnClickAlertListItem, AdapterView.OnItemLongClickListener {

    private static final String TAG = ChatMensajeActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static final int ACTIONBAR_SIZE = 128;
    private static final int LIMIT_MESSAGE = 333; /* it's 1/3 of limit*/
    private static boolean enabledClickControls = false;
    private EditText etMensaje;
    private ImageView ivSend;
    private static AdaptadorMensajeChat adaptador;
    private static List<BeanMensajeChat> listaMensajes;
    private static ListView lvConversacion;
    private String idChat;
    private static ParseObject usuarioChat;
    private static ParseObject chat;
    private List<ParseObject> members = null;
    public static boolean isActivo;
    public static String currentChatId;
    private boolean isArchived;
    private List<Object> userItems = null;
    private Bitmap imagenChat;
    private static int RESULT_LOAD_IMAGE = 1;
    private AppiTalkYou app;
    private static ActionBar mActionBar;
    private static ProgressBar pbLoader;
    private static Activity mActivity;
    private static Context _context;
    private static String type;
    private static int membersSize;
    private List<String> miembrosIdList;
    private static boolean isFromPush = false;
    private CustomAlertDialog customDialog;

    //Updated status Online/Offline
    private Timer mTimer;
    private TimerTask mTimerTask;
    private static final long EVERY_SECOND = 1000;//MILLISECONDS
    private static final long DELAY = 2500;
    private ClipboardManager clipboardManager;
    private CircleImageView civPhoto;
    private TextView tvSubTitle;
    private TextView tvTitle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_mensajes);
        //first
        app = (AppiTalkYou) getApplication();
        _context = getApplicationContext();
        isActivo = true;

        //get data
        Bundle bundle = getIntent().getExtras();
        idChat = bundle.getString(Const.identificador_chat);
        isFromPush = bundle.getBoolean(Const.TAG_IS_PUSH);
        currentChatId = idChat;
//        estado = bundle.getString(Const.identificador_notificador);
        isArchived = bundle.getBoolean(Const.TAG_FLAG_ARCHIVED);
        type = bundle.getString(Const.TAG_TYPE_CHAT);
        inicializarComponentes();
        obtenerUsuarioChat();
        obtenerDatosChat();
        setOnline(true);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        lvConversacion.setOnItemLongClickListener(this);

    }


    private void mostrarBarraAcciones() {
        String nombre = "";
        String participantes;

        ActionBar mActionBar = getSupportActionBar();
//        mActionBar.setDisplayShowHomeEnabled(false);
//        mActionBar.setDisplayShowTitleEnabled(false);


        LayoutInflater mInflater = LayoutInflater.from(this);
        View bar = mInflater.inflate(R.layout.custom_actionbar_chat, null);

        tvTitle = (TextView) bar.findViewById(R.id.tv_bar_title);
        tvSubTitle = (TextView) bar.findViewById(R.id.tv_bar_subtitle);
        civPhoto = (CircleImageView) bar.findViewById(R.id.profile_image);
        mActionBar.setCustomView(bar);
        mActionBar.setDisplayShowCustomEnabled(true);

        imagenChat = null;
        membersSize = chat.getList(ChatITY.identificador_miembros_id).size();

        if (chat.getString(ChatITY.identificador_tipo)
                .equals(ChatITY.tipo_grupal)) {

            nombre = chat.getString(ChatITY.identificador_nombre_chat);
            members = chat.getList(ChatITY.identificador_miembros);
            miembrosIdList = chat.getList(ChatITY.identificador_miembros_id);

            tvTitle.setText(nombre);
            participantes = chat.getString(ChatITY.identificador_miembros_nombre) + getString(R.string.texto_tu);
            ParseObject usuario = app.getUsuarioChat();
            participantes = chat.getString(ChatITY.identificador_miembros_nombre);
            participantes = participantes.replace(usuario.getString(ChatITY.USER_USER), getString(R.string.texto_tu));

//            mActionBar.setCustomView(bar);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (chat.getBoolean(ChatITY.USER_FLAG_IMAGE)) {
                        final ParseFile file = (ParseFile) chat.get(ChatITY.identificador_imagen_chat);

                        try {

                            byte[] data = file.getData();
                            imagenChat = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            imagenChat = AppUtil.getRoundedCornerImage(imagenChat, false);
//                            imagenChat = Bitmap.createScaledBitmap(imagenChat, ACTIONBAR_SIZE, ACTIONBAR_SIZE, false);
                            Drawable bm1 = new BitmapDrawable(getResources(), imagenChat);

                            civPhoto.setImageBitmap(imagenChat);
//                            mActionBar.setIcon(bm1);
//
                        } catch (ParseException e) {
                            e.printStackTrace();
                            civPhoto.setImageResource(R.drawable.ic_group);
                        }

                    } else {
                        civPhoto.setImageResource(R.drawable.ic_group);
//                        mActionBar.setIcon(R.drawable.ic_group);
                    }
                }
            });

            //Load members if is grupal
            findUsersItemsOnServer();

        } else {

            //Chat privado
            ParseObject contactoChat;

            miembrosIdList = chat.getList(ChatITY.identificador_miembros_id);
            members = chat.getList(ChatITY.identificador_miembros);

            final String identificador = miembrosIdList.get(0);
            String identificador_usuario = usuarioChat.getString(ChatITY.USER_ANNEX);
            if (identificador.equals(identificador_usuario))
                contactoChat = members.get(1);
            else
                contactoChat = members.get(0);

            //obtenemos la foto
            try {
                ParseObject contacto = contactoChat.fetchIfNeeded();
                nombre = contacto.getString(ChatITY.USER_USER);
                tvTitle.setText(nombre);
                if (contacto.getBoolean(ChatITY.USER_FLAG_IMAGE)) {
                    final ParseFile file = (ParseFile) contacto.get(ChatITY.USER_IMAGE);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] data = file.getData();
//                                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                Drawable bm1 = new BitmapDrawable(getResources(), AppUtil.getRoundedCornerImage(bm,true,360));

                                imagenChat = BitmapFactory.decodeByteArray(data, 0, data.length);
                                civPhoto.setImageBitmap(imagenChat);
//                                imagenChat = AppUtil.getRoundedCornerImage(imagenChat, false);
//                                imagenChat = Bitmap.createScaledBitmap(imagenChat, ACTIONBAR_SIZE, ACTIONBAR_SIZE, false);
//                                Drawable bm1 = new BitmapDrawable(getResources(), imagenChat);
//                                mActionBar.setIcon(bm1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (NullPointerException ex) {

                            }
                        }
                    });


                } else {
//                    mActionBar.setIcon(R.drawable.ic_person);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        updateStatusOnBar(nombre, bar);

    }

    void updateStatusOnBar(final String name, final View bar) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tvSubTitle.setText(Html.fromHtml("<medium>" + name + "</medium>"));

                if (type.equals(ChatITY.tipo_privado)) {
                    tvSubTitle.setText(Html.fromHtml("<small><small/>"));
                } else {
                    tvSubTitle.setText(Html.fromHtml("<small>" + membersSize +
                            Const.ESPACIO_BLANCO +
                            "miembros <small/>"));
                }
            }
        });
    }


    void setStatusChat(final String status) {

        if (type.equals(ChatITY.tipo_privado)) {

            if (status.equals(Const.status_connected)) {
                tvSubTitle.setText(Html.fromHtml("<small>" + mActivity.getResources().getString(R.string.label_chat_status_online) + "<small/>"));
                civPhoto.setBorderColorResource(R.color.GreenYellow);
            } else if (status.equals(Const.status_is_typing)) {
                tvSubTitle.setText(Html.fromHtml("<small>" + mActivity.getResources().getString(R.string.label_chat_status_is_typing) + "<small/>"));
                civPhoto.setBorderColorResource(R.color.GreenYellow);
            } else {
                tvSubTitle.setText(Html.fromHtml("<small><small/>"));
                civPhoto.setBorderColorResource(R.color.gray_color_0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (type.equals(ChatITY.tipo_privado))
            getMenuInflater().inflate(R.menu.menu_conversation, menu);
        else {
            getMenuInflater().inflate(R.menu.menu_conversation_group, menu);
            int color = getResources().getColor(R.color.White);
            MenuColorizer.colorMenu(this, menu, color);
        }

        return true;
    }

    private void obtenerUsuarioChat() {

        usuarioChat = app.getUsuarioChat();
        if (usuarioChat == null) {
            BeanUsuario busuario = LogicaUsuario.obtenerUsuario(getApplicationContext());
            app.setUsuario(busuario);
            getUserChat(busuario.getAnexo());
        }
    }

    private void getUserChat(String annex) {
        try {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
            query.whereEqualTo(ChatITY.USER_ANNEX, annex);
            usuarioChat = query.getFirst();
            app.setUsuarioChat(usuarioChat);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void obtenerDatosChat() {

        if (AppUtil.existeConexionInternet(ChatMensajeActivity.this)) {
            ParseQuery<ParseObject> currentQuery = ParseQuery.getQuery(ChatITY.tabla_chats);
            currentQuery.getInBackground(idChat, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        chat = parseObject;
                        mostrarBarraAcciones();
                        obtenerListaMensajes(idChat, false);
                    }
                }
            });

        } else {
            getFromLocalStore(idChat);
            Crouton.showText(ChatMensajeActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private static List<ParseObject> getFromLocalStore(String chatID) {
        List<ParseObject> parseObjects;

        ParseQuery<ParseObject> messagesLocal = ParseQuery.getQuery(ChatITY.tabla_mensajes);
        messagesLocal.fromPin(Const.TAG_CHAT_MESSAGE_LOCAL_STORE);
        messagesLocal.whereEqualTo(ChatITY.identificador_idchat, chatID);
        messagesLocal.orderByDescending(ChatITY.identificador_createdAt);
        messagesLocal.setLimit(LIMIT_MESSAGE);

        try {
            parseObjects = messagesLocal.find();
        } catch (ParseException e) {
            parseObjects = null;
            e.printStackTrace();
        }

        return parseObjects;
    }

    private static void obtenerListaMensajes(final String chatID, boolean isPush) {

        listaMensajes.clear();

        List<ParseObject> lstMensajes;
        if (isPush || isFromPush) {
            lstMensajes = getFromServer(chatID);
        } else {
            lstMensajes = getFromLocalStore(chatID);

            if (lstMensajes == null || lstMensajes.isEmpty())
                lstMensajes = getFromServer(chatID);
        }

        loopMessages(lstMensajes);
        showLoader(false);
    }

    public static void saveOnLocalStore(final String chatID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFromServer(chatID);
            }
        }).start();
    }

    private static void loopMessages(List<ParseObject> messages) {
        if (messages.size() > 0) {
            //Lee del menos reciente al mas reciente.
            for (int i = messages.size() - 1; i >= 0; i--) {

                ParseObject mensaje = messages.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm");
                String fecha = sdf.format(mensaje.getCreatedAt());

                //Valida si es tipo mensaje texto
                if (mensaje.getString(ChatITY.identificador_tipo).equals(ChatITY.mensaje_texto)) {

                    if ((mensaje.getParseObject(ChatITY.identificador_envio_mensaje).getObjectId()).equals(usuarioChat.getObjectId()))
                        listaMensajes.add(new BeanMensajeChat(mensaje.getString(ChatITY.identificador_mensaje), fecha, true, mensaje.getParseObject(ChatITY.identificador_envio_mensaje)));
                    else
                        listaMensajes.add(new BeanMensajeChat(mensaje.getString(ChatITY.identificador_mensaje), fecha, false, mensaje.getParseObject(ChatITY.identificador_envio_mensaje)));

                } else {

                    //Tipo archivo
                    ParseFile file = (ParseFile) mensaje.get(ChatITY.identificador_archivo);

                    try {
                        byte[] data = file.getData();

                        //Configure image
                        if (data != null) {
                            try {
                                Bitmap imagen = BitmapFactory.decodeByteArray(data, 0, data.length);
                                //Drawable bm1 = new BitmapDrawable(bm);

                                if ((mensaje.getParseObject(ChatITY.identificador_envio_mensaje).getObjectId()).equals(usuarioChat.getObjectId()))
                                    listaMensajes.add(new BeanMensajeChat(imagen, data, fecha, true, mensaje.getParseObject(ChatITY.identificador_envio_mensaje), mensaje));
                                else
                                    listaMensajes.add(new BeanMensajeChat(imagen, data, fecha, false, mensaje.getParseObject(ChatITY.identificador_envio_mensaje), mensaje));

                            } catch (Exception ex) {
                                showLoader(false);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        showLoader(false);
                    }
                }
            }

            adaptador = new AdaptadorMensajeChat(mActivity, listaMensajes, chat.getString(ChatITY.identificador_tipo));
            showLoader(false);

            //update list
            mActivity.runOnUiThread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    lvConversacion.setAdapter(adaptador);
                    lvConversacion.setFastScrollEnabled(true);
                    lvConversacion.setFastScrollAlwaysVisible(true);
                    adaptador.notifyDataSetChanged();
                }
            });

        } else {
            adaptador = new AdaptadorMensajeChat(mActivity, listaMensajes, chat.getString(ChatITY.identificador_tipo));
            showLoader(false);
        }

    }

    private static List<ParseObject> getFromServer(String chatID) {
        List<ParseObject> lstMensajes;
        ParseQuery<ParseObject> messages = new ParseQuery<>(ChatITY.tabla_mensajes);
        messages.whereEqualTo(ChatITY.identificador_idchat, chatID);
        messages.orderByDescending(ChatITY.identificador_createdAt);
        messages.setLimit(LIMIT_MESSAGE);

        try {
            lstMensajes = messages.find();
            ParseObject.pinAllInBackground(Const.TAG_CHAT_MESSAGE_LOCAL_STORE, lstMensajes);
        } catch (ParseException e) {
            lstMensajes = null;
            e.printStackTrace();
        }

        return lstMensajes;
    }

    private void inicializarComponentes() {
        //subscribe to receiver push notification
//        ParsePush.subscribeInBackground(Const.TAG_CHANNEL_PRIVATE + idChat);

        //Setup actionbar
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        CustomListAdapter.setAlertListItemListener(this);

        etMensaje = (EditText) findViewById(R.id.etMensaje);
        etMensaje.clearFocus();
        isTyping();

        ivSend = (ImageView) findViewById(R.id.ivEnviarMensaje);
        ivSend.setOnClickListener(this);

        mActivity = this;
        listaMensajes = new ArrayList<>();
        lvConversacion = (ListView) findViewById(R.id.listMensajes);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);

        showLoader(true);
    }

    private void agregarMensaje(BeanMensajeChat beanMensaje) {
//        try {

        listaMensajes.add(beanMensaje);
        adaptador = new AdaptadorMensajeChat(mActivity, listaMensajes, chat.getString(ChatITY.identificador_tipo));
        lvConversacion.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();

//        lvConversacion.setSelection(listaMensajes.size() - 1);

//        } catch (Exception ex) {
//            Log.e(Const.DEBUG_CHAT, TAG + "Error_ add " + ex.getMessage());
//        }

    }


    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {

        if (v == ivSend) {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.equals("")) {

                if (isArchived)
                    unarchiveChat(idChat);

                enviarMensaje(texto);


            } else {
                Crouton.showText(ChatMensajeActivity.this, getString(R.string.htn_texto), Style.ALERT);
            }

        }

    }

    private void unarchiveChat(String idChat) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
        query.whereEqualTo(LogicChat.CHATUSER_COLUMN_ANNEX, app.getUsuario().getAnexo());
        query.whereEqualTo(LogicChat.CHATUSER_COLUMN_CHATID, ParseObject.createWithoutData(LogicChat.TAG_CHAT, idChat));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject chatUser : list) {
                        chatUser.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                        chatUser.saveInBackground();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage;
        try {
            selectedImage = data.getData();

        } catch (Exception ex) {
            selectedImage = null;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msj_error_load_image), Toast.LENGTH_LONG).show();
        }

        if (selectedImage != null) {
            String rutaArchivo = getRuta(selectedImage);

            if (rutaArchivo != null) {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == this.RESULT_OK && null != data) {
                    try {
                        Bitmap rotated = AppUtil.getCorrectlyOrientedImage(getApplicationContext(), selectedImage);
                        enviarArchivo(rotated);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    Bitmap archivo = AppUtil.obtenerImagen(rutaArchivo, false);
//                    enviarArchivo(archivo);
                }

            } else {
                Toast.makeText(getApplicationContext(), R.string.msj_image_download_error, Toast.LENGTH_LONG).show();
            }

        }
    }

    private void enviarArchivo(final Bitmap archivo) {

        if (AppUtil.existeConexionInternet(ChatMensajeActivity.this)) {
            try {

                //creamos el mensaje con archivo adjunto y guardamos
                final ParseObject newArchive = new ParseObject(ChatITY.tabla_mensajes);
                newArchive.put(ChatITY.identificador_idchat, idChat);
                newArchive.put(ChatITY.identificador_mensaje, Const.cad_vacia);
                newArchive.put(ChatITY.identificador_envio_mensaje
                        , ParseObject.createWithoutData(ChatITY.TABLE_USER,
                                usuarioChat.getObjectId()));
                newArchive.put(ChatITY.identificador_tipo, ChatITY.mensaje_archivo);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                archivo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] data = stream.toByteArray();
                final ParseFile file = new ParseFile(obtenerNombre(), data);

                newArchive.put(ChatITY.identificador_archivo, file);
                newArchive.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            newArchive.pinInBackground(Const.TAG_CHAT_MESSAGE_LOCAL_STORE);
                    }
                });


                //modificamos el ultimo mensaje
                ParseObject updateChat = ParseObject.createWithoutData(ChatITY.tabla_chats, idChat);
//                updateChat.put(ChatITY.identificador_ultimo_mensaje, ChatITY.texto_imagen);
//                updateChat.saveInBackground();

                //Modificamos chatUsers
                ParseQuery<ParseObject> query = new ParseQuery(LogicChat.TAG_CHAT_USER);
                query.whereEqualTo(LogicChat.CHATUSER_COLUMN_CHATID, updateChat);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {

                        if (e == null) {
                            for (final ParseObject chatUser : list) {
                                chatUser.put(LogicChat.CHATUSER_COLUMN_LASTMESSAGE, ChatITY.texto_imagen);
                                chatUser.put(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE, new Date());
                                chatUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null
                                                && chatUser.getString(LogicChat.CHATUSER_COLUMN_ANNEX)
                                                .equals(app.getUsuario().getAnexo())) {
                                            chatUser.pinInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED);
                                        }
                                    }
                                });

                            }
                        }
                    }
                });


                //enviamos la notificacion del mensaje
                String accionEnviar = "{\"mensaje\": \"" + ChatITY.texto_imagen + "\"," +
                        " \"action\": \"com.iTalkYou.UPDATE_STATUS\", " +
                        " \"chatId\":\"" + idChat + "\"," +
                        " \"type\":\"" + chat.getString(LogicChat.COLUMN_TIPO) + "\"," +
                        " \"nombre\": \"" + usuarioChat.getString(ChatITY.USER_USER) + "\"}";

                JSONObject pushData = new JSONObject(accionEnviar);
                JSONArray miembrosID = chat.getJSONArray(LogicChat.COLUMN_MIEMBROS_ID);

                //Notification
                sendPushNotification(miembrosID, pushData);

                //add image
                agregarMensaje(new BeanMensajeChat(
                        archivo,
                        data,
                        obtenerFecha(),
                        true,
                        ParseObject.createWithoutData(ChatITY.TABLE_USER, usuarioChat.getObjectId()), newArchive));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        } else {
            Crouton.showText(ChatMensajeActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
        }
    }

    private void sendPushNotification(JSONArray miembrosID, JSONObject pushData) {


        for (int i = 0; i < miembrosID.length(); i++) {

            try {
                String annex = miembrosID.getString(i);
                if (!annex.equals(app.getUsuario().getAnexo())) {

                    ParseQuery pushQuery = ParseInstallation.getQuery();
                    pushQuery.whereEqualTo("channels", Const.TAG_MY_CHANNEL + annex); // Set the channel
                    pushQuery.orderByAscending(LogicChat.COLUMN_UPDATED_AT);
                    pushQuery.setLimit(1);

                    ParsePush push = new ParsePush();
                    push.setData(pushData);
                    push.setQuery(pushQuery);
                    push.sendInBackground();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private String obtenerNombre() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String fecha = df.format(date);
        String nombre = "IMG_" + fecha + "_" + usuarioChat.getString(ChatITY.USER_ANNEX) + ".png";
        return nombre;
    }

    private String getRuta(Uri selectedImage) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        String picturePath = null;
        Cursor cursor = this.getContentResolver().query(selectedImage, filePath, null, null, null);
        if (cursor.moveToFirst()) {
            picturePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
        }
        cursor.close();

        return picturePath;
    }

    private void enviarMensaje(final String texto) {

        if (AppUtil.existeConexionInternet(ChatMensajeActivity.this)) {
            try {

                // creamos y guardamos el objectParse mensaje
                final ParseObject newMessage = new ParseObject(ChatITY.tabla_mensajes);
                newMessage.put(ChatITY.identificador_idchat, idChat);
                newMessage.put(ChatITY.identificador_mensaje, texto);
                newMessage.put(ChatITY.identificador_envio_mensaje, ParseObject.createWithoutData(ChatITY.TABLE_USER, usuarioChat.getObjectId()));
                newMessage.put(ChatITY.identificador_tipo, ChatITY.mensaje_texto);
                newMessage.put("members", members);
                newMessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            newMessage.pinInBackground(Const.TAG_CHAT_MESSAGE_LOCAL_STORE);
                    }
                });


                //modificamos el ultimo mensaje chats
                ParseObject updateChat = ParseObject.createWithoutData(ChatITY.tabla_chats, idChat);
                final Date currentDate = new Date();

//                updateChat.put(ChatITY.identificador_ultimo_mensaje, texto);
//                updateChat.put(LogicChat.COLUMN_LAST_DATE_MESSAGE, currentDate);
//                updateChat.saveInBackground();

                //Modificamos chatUsers
                ParseQuery<ParseObject> query = new ParseQuery(LogicChat.TAG_CHAT_USER);
                query.whereEqualTo(LogicChat.CHATUSER_COLUMN_CHATID, updateChat);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            for (final ParseObject chatUser : list) {
                                chatUser.put(LogicChat.CHATUSER_COLUMN_LASTMESSAGE, texto);
                                chatUser.put(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE, currentDate);
                                chatUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null
                                                && chatUser.getString(LogicChat.CHATUSER_COLUMN_ANNEX)
                                                .equals(app.getUsuario().getAnexo())) {
                                            chatUser.pinInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });


                //enviamos la notificacion del mensaje
                String accionEnviar = "{\"mensaje\": \"" + texto + "\"," +
                        " \"action\": \"com.iTalkYou.UPDATE_STATUS\"," +
                        " \"chatId\":\"" + idChat + "\"," +
                        " \"type\":\"" + chat.getString(LogicChat.COLUMN_TIPO) + "\"," +
                        " \"nombre\": \"" + usuarioChat.getString(ChatITY.USER_USER) + "\"}";

                JSONObject pushData = new JSONObject(accionEnviar);
                JSONArray miembrosId = chat.getJSONArray(LogicChat.COLUMN_MIEMBROS_ID);

                //Send notification
                sendPushNotification(miembrosId, pushData);
                etMensaje.setText("");

                agregarMensaje(new BeanMensajeChat(texto, obtenerFecha(), true, ParseObject.createWithoutData(ChatITY.TABLE_USER, usuarioChat.getObjectId())));
            } catch (JSONException e) {
                e.printStackTrace();
                Crouton.showText(ChatMensajeActivity.this, getString(R.string.msj_error_enviar_mensaje), Style.ALERT);
            }
        } else {
            Crouton.showText(ChatMensajeActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
        }
    }

    private String obtenerFecha() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM hh:mm");
        String fecha = df.format(date);
        return fecha;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivo = true;
        Log.e("Activity cycle", "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getIntent().getExtras().clear();
        isActivo = false;
        stopTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivo = false;
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setOnline(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isActivo = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivo = true;
        startTimer();
        setOnline(true);
    }

    private void setOnline(boolean status) {

        try {
            ParseObject po = app.getUsuarioChat();

            if (status)
                po.put(ChatITY.USER_STATUS, Const.status_connected);
            else
                po.put(ChatITY.USER_STATUS, Const.status_no_connected);

            po.saveInBackground();
        } catch (NullPointerException np) {
            Log.e(TAG, "Error al actualizar status ", np);

        } catch (Exception ex) {
            Log.e(TAG, "Error al actualizar status ", ex);
        }

    }

    private void sendTypingStatusChat() {

        try {
            ParseObject po = app.getUsuarioChat();
            po.put(ChatITY.USER_STATUS, Const.status_is_typing);
            po.saveInBackground();
        } catch (NullPointerException np) {
            Log.e(TAG, "Error al actualizar status ", np);

        } catch (Exception ex) {
            Log.e(TAG, "Error al actualizar status ", ex);
        }

    }

    private void startTimer() {
        if (type.equals(ChatITY.tipo_privado)) {
            inizializeTimerTask();
            mTimer = new Timer();
            mTimer.schedule(mTimerTask, DELAY, EVERY_SECOND);
        }

    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void inizializeTimerTask() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getStatusChat();
                    }
                });
            }
        };
    }

//    if (type.equals(ChatITY.tipo_privado))
//            mActionBar.setSubtitle(Html.fromHtml("<small>" + mActivity.getResources().getString(R.string.label_chat_status_online) + "<small/>"));
//    else
//            mActionBar.setSubtitle(Html.fromHtml("<small>" + membersSize +
//            " miembros <small/>"));

    public static void actualizarListaMensajes(final String chatID) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                obtenerListaMensajes(chatID, true);
            }
        }).start();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (!enabledClickControls) {
            Toast.makeText(getApplicationContext(), R.string.msj_descargando_informacion, Toast.LENGTH_LONG).show();
            return false;
        }

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.acc_attach:
                openAttachFile();
                break;
            case R.id.acc_call:
                singleAudioCall();
                break;
            case R.id.acc_view_members:
                showMembersList();

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void singleAudioCall() {
        try {
            for (ParseObject member : members) {
                if (!member.getObjectId().equals(usuarioChat.getObjectId())) {
                    callFromChat(
                            member.getString(ChatITY.USER_ANNEX),
                            member.getString(ChatITY.USER_USER),
                            SIPManager.newInstance().buildAddress(member.getString(ChatITY.USER_ANNEX)).asStringUriOnly());
                    break;
                }
            }
        }catch (NullPointerException ex){
            Toast.makeText(getApplicationContext(),R.string.error_unknow,Toast.LENGTH_LONG).show();
        }

    }

    private void showMembersList() {

        try {

            String groupName = chat.getString(ChatITY.identificador_nombre_chat);
            customDialog = CustomAlertDialog.newIntance(Const.INDEX_DIALOG_CUSTOM_LIST, getItems(), groupName);
            customDialog.show(getSupportFragmentManager(), Const.TAG_MENU_ACTIONS_CHAT);

        } catch (Exception ex) {
            Toast.makeText(_context, getString(R.string.msj_actualizando_informacion), Toast.LENGTH_SHORT).show();
            Log.e(Const.DEBUG_CHAT, TAG + "Error al abrir chat grupal", ex);
        }

    }

    private void callFromChat(String annex, String name, String sessionID) {
        LogicaPantalla.intentBasicCall(
                this,
                Const.tipo_llamada_anexoVOIP,
                annex,
                name,
                sessionID);

    }


    private void findUsersItemsOnServer() {
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();


        for (int i = 0; i < miembrosIdList.size(); i++) {
            ParseQuery<ParseObject> user = ParseQuery.getQuery(ChatITY.TABLE_USER);
            user.whereEqualTo(ChatITY.USER_ANNEX, miembrosIdList.get(i));
            queries.add(i, user);
        }

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> members, ParseException e) {
                if (e == null) {
                    userItems = new ArrayList<>();
                    for (ParseObject member : members) {
                        userItems.add(new MenuActionsChat(
                                member.getString(ChatITY.USER_USER),
                                member.getString(ChatITY.USER_ANNEX),
                                0,
                                0,
                                (member.getString(ChatITY.USER_STATUS).equals(Const.status_no_connected) ? false : true)));
                    }
                }
            }
        });
    }

    private List<Object> getItems() {
        return userItems;
    }


    private void openAttachFile() {
        //AppUtil.MostrarMensaje(this, "Adjuntar Archivo");
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void onBackPressed() {
        LogicaPantalla.personalizarIntentVistaPrincipal(ChatMensajeActivity.this, Const.PANTALLA_PRINCIPAL,ChatMensajeActivity.class.getSimpleName());
    }

    private void getStatusChat() {
        if (members != null)
            for (ParseObject member : members) {
                if (!member.getObjectId().equals(usuarioChat.getObjectId())) {
                    member.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject user, ParseException e) {
                            if (e == null) {
                                setStatusChat(user.getString(ChatITY.USER_STATUS));
                            }
                        }
                    });
                }
            }
    }

    private static void showLoader(final boolean isVisible) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isVisible) {
                    lvConversacion.setVisibility(View.GONE);
                    pbLoader.setVisibility(View.VISIBLE);
                } else {
                    lvConversacion.setVisibility(View.VISIBLE);
                    pbLoader.setVisibility(View.GONE);
                    enabledClickControls = true;
                }

            }
        });
    }

    private void checkIfTextChanged() {
        final int[] cont = {0};
        final Timer tm = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (cont[0] > 3) {
                    tm.cancel();
                    setOnline(true);
                }
                cont[0]++;
            }
        };

        tm.schedule(task, 0, 1000);
    }


    /**
     * CustomListAdapter methods
     */
    @Override
    public void onCallItemList(String annex, String name) {
        callFromChat(annex, name, SIPManager.newInstance().buildAddress(annex).asStringUriOnly());
    }

    /**
     * Typing method
     */

    public void isTyping() {
        final int[] size = {0};
        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                size[0] = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (size[0] < s.length())
                    sendTypingStatusChat();
                else
                    setOnline(true);

                checkIfTextChanged();

            }
        });


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        BeanMensajeChat oMessage = listaMensajes.get(position);
        String message = oMessage.getMensaje();

        if (message != null) {
            ClipData cd = ClipData.newPlainText(oMessage.getTipo(), message);
            clipboardManager.setPrimaryClip(cd);
            Toast.makeText(getApplicationContext(), R.string.chat_copy, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), R.string.chat_copy_no_found, Toast.LENGTH_SHORT).show();

        return true;
    }
}
