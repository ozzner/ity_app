package com.italkyou.gui.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.salidas.SalidaDatosChatGrupal;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.PrincipalFragment;
import com.italkyou.gui.R;
import com.italkyou.gui.VistaPrincipalActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.DialogoLista;
import com.italkyou.gui.personalizado.DialogoLista.onSeleccionarOpcionListener;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CrearChatGrupalFragment extends Fragment implements OnClickListener {

    private static final String TAG = CrearChatGrupalFragment.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private ImageButton imagenGrupo;
    private ImageView ivAddContact;
    private EditText etNombreGrupo;
    private static AdaptadorLista adaptador;
    private static SalidaDatosChatGrupal datosGrupo;
    private static List<Object> lstParticipantes;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;
    private Bitmap imgGrupo;
    private AppiTalkYou app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datosGrupo = (SalidaDatosChatGrupal) getArguments().getSerializable(Const.DATOS_GRUPO_CHAT);
        getArguments().putSerializable(Const.DATOS_GRUPO_CHAT, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.crear_chat_grupal, container, false);
        VistaPrincipalActivity.setPantalla(Const.PANTALLA_CHAT_GRUPAL);
        return rootView;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar mActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.mnchat_grupal, menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                cargarFragmento();
                break;

            case R.id.itCrearGrupo:
                crearGrupo();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void inicializarComponentes() {

        imgGrupo = null;
        etNombreGrupo = (EditText) getActivity().findViewById(R.id.etNombreGrupo);
        imagenGrupo = (ImageButton) getActivity().findViewById(R.id.btnImagenGrupo);
        imagenGrupo.setOnClickListener(this);

        ivAddContact = (ImageView) getActivity().findViewById(R.id.btnAgregarParticipantes);
        ivAddContact.setOnClickListener(this);

        if (!datosGrupo.getNombre().equals(Const.CARACTER_VACIO))
            etNombreGrupo.setText(datosGrupo.getNombre());

        if (datosGrupo.getImagenGrupo() != null) {
            imgGrupo = datosGrupo.getImagenGrupo();
            imagenGrupo.setImageBitmap(imgGrupo);
        }

        if (!datosGrupo.getIdChat().equals(Const.CARACTER_VACIO)) {

            if (datosGrupo.getListaContactos().size() == 0) {

                lstParticipantes = new ArrayList<>();
                Object[] lst = datosGrupo.getLstMiembros().toArray();
                ParseQuery<ParseObject> queryLstParticipantes = new ParseQuery<>(ChatITY.TABLE_USER);
                queryLstParticipantes.whereContainedIn(ChatITY.USER_ANNEX, Arrays.asList(lst));

                try {
                    List<ParseObject> listado = queryLstParticipantes.find();
                    List<Object> miembros = new ArrayList<>();
                    miembros.addAll(listado);
                    lstParticipantes.addAll(listado);
                    datosGrupo.setListaContactos(miembros);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                lstParticipantes = datosGrupo.getListaContactos();
            }

        } else
            lstParticipantes = datosGrupo.getListaContactos();
        ListView lista = (ListView) getActivity().findViewById(R.id.listParticipantes);

        if (!datosGrupo.getIdChat().equals(Const.CARACTER_VACIO)) {
            adaptador = new AdaptadorLista(getActivity(), R.layout.celda_participante, lstParticipantes, ParseObject.class.getSimpleName(), Const.USUARIO_ITALKYOU_SELECCIONADO, datosGrupo.getLstMiembros());
        } else {
            adaptador = new AdaptadorLista(getActivity(), R.layout.celda_participante, lstParticipantes, ParseObject.class.getSimpleName(), Const.USUARIO_ITALKYOU_SELECCIONADO);
        }
        lista.setAdapter(adaptador);
    }

    public static CrearChatGrupalFragment nuevaInstancia(SalidaDatosChatGrupal datosGrupo) {
        CrearChatGrupalFragment fragmento = new CrearChatGrupalFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.DATOS_GRUPO_CHAT, datosGrupo);
        fragmento.setArguments(bundle);
        return fragmento;
    }

    public void onBackPressed() {
        cargarFragmento();
    }

    private void cargarFragmento() {
        Fragment fragmento = PrincipalFragment.nuevaInstancia(Const.indice_0);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {

        if (v == imagenGrupo) {
            FragmentManager fm = getFragmentManager();
            DialogoLista dialogo = new DialogoLista();

            onSeleccionarOpcionListener listener = new onSeleccionarOpcionListener() {

                @Override
                public void setSeleccionarOpcionListener(String texto) {

                    if (texto.equals(Const.DESCRIPCION_GALERIA)) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            };

            dialogo.onSeleccionarOpcionListener = listener;
            dialogo.titulo = getString(R.string.titulo_seleccionar);
            dialogo.pantalla = Const.PANTALLA_PERFIL;
            dialogo.show(fm, "");

        } else if (v == ivAddContact) {
            String nombre = etNombreGrupo.getText().toString().trim();
            if (!nombre.equals(Const.cad_vacia)) datosGrupo.setNombre(nombre);
            if (imgGrupo != null) datosGrupo.setImagenGrupo(imgGrupo);
            cargarFragmentoContactos();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            AppUtil.showMessage(getActivity(), getString(R.string.msj_error_load_image), false);
            return;
        }


        Uri selectedImage = data.getData();
        String picturePath = getRuta(selectedImage);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {

            Glide.with(this)
                    .load(picturePath).centerCrop().into(imagenGrupo);

        } else if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK && null != data) {
            imgGrupo = (Bitmap) data.getExtras().get("data");
            imagenGrupo.setImageBitmap(imgGrupo);

        }
    }

    private String getRuta(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    private void cargarFragmentoContactos() {
        Fragment fragmento = ListaContactoAnexoFragment.nuevaInstancia(Const.PANTALLA_CHAT_GRUPAL, datosGrupo);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();
    }

    public static void actualizarParticipantes(int pos) {
        lstParticipantes.remove(pos);
        adaptador.notifyDataSetChanged();
        datosGrupo.setListaContactos(lstParticipantes);
    }

    private void crearGrupo() {

        final String nombre = etNombreGrupo.getText().toString().trim();

        if (!nombre.equals(Const.cad_vacia)) {
            if (lstParticipantes.size() > 0) {

                if (AppUtil.existeConexionInternet(getActivity())) {

                    String anexos[] = obtenerAnexosParticipantes();
                    ParseQuery<ParseObject> queryLstParticipantes = new ParseQuery(ChatITY.TABLE_USER);
                    queryLstParticipantes.whereContainedIn(ChatITY.USER_ANNEX, Arrays.asList(anexos));
                    queryLstParticipantes.findInBackground(new FindCallback<ParseObject>() {

                        @Override
                        public void done(List<ParseObject> listado, ParseException e) {
                            if (e == null) {
                                crearChatGrupal(listado, nombre);
                            } else {
                                Crouton.showText(getActivity(), "No existen todos los anexos " + listado.size(), Style.ALERT);
                            }
                        }
                    });
                } else {
                    Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
                }
            } else {
                Crouton.showText(getActivity(), getString(R.string.msj_error_falta_participantes), Style.ALERT);
            }
        } else {
            Crouton.showText(getActivity(), getString(R.string.msj_error_falta_nombre), Style.ALERT);
        }
    }


    private void crearChatGrupal(final List<ParseObject> listado, final String nombre) {
        ParseFile imageGroupFile = null;
        app = (AppiTalkYou) getActivity().getApplication();
        final ParseObject usuParse = app.getUsuarioChat();

        if (datosGrupo.getIdChat().equals(Const.CARACTER_VACIO)) {
            String nombreParticipantes = "";

            final String lst_id[] = new String[listado.size() + 1];
            final ParseObject lst_miembros[] = new ParseObject[listado.size() + 1];

            lst_id[0] = usuParse.getString(ChatITY.USER_ANNEX);
            lst_miembros[0] = usuParse;

            /*Entiendo que este metodo permite sacar los nombres, usuarios y obtener los anexos :S */
            for (int i = 1; i <= listado.size(); i++) {

                ParseObject usuario = listado.get(i - 1);
                lst_id[i] = usuario.getString(ChatITY.USER_ANNEX);
                lst_miembros[i] = usuario;
                nombreParticipantes += usuario.getString(ChatITY.USER_USER) + ", ";
            }


            nombreParticipantes += app.getUsuarioChat().getString(ChatITY.USER_USER);

            //nombreParticipantes += getString(R.string.texto_tu);
            //creamos el chat
            final ParseObject chatObjects = new ParseObject(ChatITY.tabla_chats);
            chatObjects.put(ChatITY.identificador_admin, usuParse.getString(ChatITY.USER_ANNEX));
            chatObjects.put(ChatITY.identificador_miembros_id, Arrays.asList(lst_id));
            chatObjects.put(ChatITY.identificador_miembros, Arrays.asList(lst_miembros));
            chatObjects.put(ChatITY.identificador_miembros_nombre, nombreParticipantes);
            chatObjects.put(ChatITY.identificador_nombre_chat, nombre);
            chatObjects.put(ChatITY.identificador_tipo, ChatITY.tipo_grupal);
            chatObjects.put(ChatITY.identificador_ultimo_mensaje, Const.cad_vacia);
//            chatObjects.put(LogicChat.COLUMN_FLAG_ARCHIVED, false);


            if (imgGrupo != null) {
                chatObjects.put(ChatITY.USER_FLAG_IMAGE, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imgGrupo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();
                imageGroupFile = new ParseFile(nombre + ".png", data);
                chatObjects.put(ChatITY.identificador_imagen_chat, imageGroupFile);

            } else
                chatObjects.put(ChatITY.USER_FLAG_IMAGE, false);


            final ParseFile finalImageGroupFile = imageGroupFile;
            chatObjects.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        chatObjects.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject chatGroup, ParseException e) {
                                if (e == null) {

                                    //Me
                                    ParseObject singleChat = new ParseObject(LogicChat.TAG_CHAT_USER);
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_CHATID, ParseObject.createWithoutData(ChatITY.tabla_chats, chatGroup.getObjectId()));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData(ChatITY.TABLE_USER, app.getUsuarioChat().getObjectId()));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR, usuParse.getString(ChatITY.USER_ANNEX));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_MEMBERSID, Arrays.asList(lst_id));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_MEMBERS, Arrays.asList(lst_miembros));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_NAME, nombre);
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_TYPE, ChatITY.tipo_grupal);
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_LASTMESSAGE, Const.cad_vacia);
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE, new Date());
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_ANNEX, app.getUsuarioChat().get(ChatITY.USER_ANNEX));
                                    singleChat.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);

                                    if (finalImageGroupFile != null) {
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, true);
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_IMAGE, finalImageGroupFile);
                                    } else
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, false);

                                    //Save on Parse
                                    final ParseObject finalSingleChat = singleChat;
                                    finalSingleChat.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null)
                                                finalSingleChat.pinInBackground(Const.TAG_CHAT_MESSAGE_LOCAL_STORE);
                                        }
                                    });


                                    //Another
                                    for (ParseObject userMember : listado) {

                                        singleChat = new ParseObject(LogicChat.TAG_CHAT_USER);
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_CHATID, ParseObject.createWithoutData(ChatITY.tabla_chats, chatGroup.getObjectId()));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData(ChatITY.TABLE_USER, userMember.getObjectId()));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_ADMINISTRATOR, usuParse.getString(ChatITY.USER_ANNEX));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_MEMBERSID, Arrays.asList(lst_id));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_MEMBERS, Arrays.asList(lst_miembros));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_NAME, nombre);
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_TYPE, ChatITY.tipo_grupal);
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_LASTMESSAGE, Const.cad_vacia);
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_LASTDATEMESSAGE, new Date());
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_ANNEX, userMember.get(ChatITY.USER_ANNEX));
                                        singleChat.put(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);

                                        if (finalImageGroupFile != null) {
                                            singleChat.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, true);
                                            singleChat.put(LogicChat.CHATUSER_COLUMN_IMAGE, finalImageGroupFile);
                                        } else
                                            singleChat.put(LogicChat.CHATUSER_COLUMN_FLAGIMAGE, false);

                                        //Save on Parse
                                        singleChat.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e != null)
                                                    Log.e(Const.DEBUG_CHAT, TAG + e.getCode() + " / " + e.getMessage());
                                            }
                                        });

                                    }//End for

                                    //Opening chat group
                                    LogicaPantalla.personalizarIntentListaMensajes(getActivity(), chatGroup.getObjectId(), false, ChatITY.tipo_grupal);

                                }
                            }
                        });
                    }
                }
            });


        } else {

            //El chat grupal existe
            Log.e("Intico", "modificar chat");

            String lst_id[] = new String[listado.size()];
            ParseObject lst_miembros[] = new ParseObject[listado.size()];
            String nombreParticipantes = "";

            for (int i = 0; i < listado.size(); i++) {
                ParseObject usuario = listado.get(i);
                lst_id[i] = usuario.getString(ChatITY.USER_ANNEX);
                lst_miembros[i] = usuario;

                if (!usuario.getString(ChatITY.USER_ANNEX).equals(usuParse.getString(ChatITY.USER_ANNEX))) {
                    nombreParticipantes += usuario.getString(ChatITY.USER_USER) + ", ";
                }
            }
            nombreParticipantes += app.getUsuarioChat().getString(ChatITY.USER_USER);
            //nombreParticipantes += getString(R.string.texto_tu);
            ParseObject updateChat = ParseObject.createWithoutData(ChatITY.tabla_chats, datosGrupo.getIdChat());
            updateChat.put(ChatITY.identificador_miembros_id, Arrays.asList(lst_id));
            updateChat.put(ChatITY.identificador_miembros, Arrays.asList(lst_miembros));
            updateChat.put(ChatITY.identificador_miembros_nombre, nombreParticipantes);
            updateChat.put(ChatITY.identificador_nombre_chat, nombre);

            if (imgGrupo != null) {
                updateChat.put(ChatITY.USER_FLAG_IMAGE, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imgGrupo.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] data = stream.toByteArray();
                ParseFile file = new ParseFile(nombre + ".png", data);
                updateChat.put(ChatITY.identificador_imagen_chat, file);
            } else
                updateChat.put(ChatITY.USER_FLAG_IMAGE, false);

            updateChat.saveInBackground();
            LogicaPantalla.personalizarIntentListaMensajes(getActivity(), datosGrupo.getIdChat(), false, ChatITY.tipo_grupal);
        }
    }

    private String[] obtenerAnexosParticipantes() {

        String[] lista = new String[lstParticipantes.size()];

        for (int i = 0; i < lstParticipantes.size(); i++) {
            String anexo = ((ParseObject) lstParticipantes.get(i)).getString(ChatITY.USER_ANNEX);
            lista[i] = anexo;
        }

        return lista;
    }
}
