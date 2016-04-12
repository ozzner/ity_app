package com.italkyou.gui.inicio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanBase;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaInisionSesion;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.AsyncGuardarDAOTask;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.BaseInicioActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.Testing.ReportActivity;
import com.italkyou.gui.personalizado.DialogoEnviarCorreo;
import com.italkyou.gui.personalizado.DialogoEnviarCorreo.onEnviarCorreoListener;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.italkyou.utils.ExceptionHandler;
import com.italkyou.utils.ItyPreferences;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class InicioSesion extends BaseInicioActivity implements OnClickListener {

    private static final String TAG = InicioSesion.class.getSimpleName();
    private EditText etExtension;
    private EditText etClave;
    private TextView tvOlvidoClave;
    private TextView tvVersion;
    private Button btnIniciar;
    private Button btnRegistro;
    private String extension;
    private String clave;
    private AppiTalkYou app;
    private ImageView imgLogo;
    private ItyPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (AppiTalkYou) getApplication();
        initErrorReporting();
        personalizarBarraAcciones();
        inicializarPantallaInicioSesion();
        inicializarComponentes();

    }

    private void initErrorReporting() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, ReportActivity.class));
    }

    @Override
    public void onClick(View v) {

        if (v == btnIniciar) {
            validarCamposInicioSesion();

        } else if (v == btnRegistro) {
            LogicaPantalla.personalizarIntentActivity(InicioSesion.this, RegistroActivity.class);

        } else if (v == tvOlvidoClave) {
            recuperarClave();

        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (((AppiTalkYou) getApplication()).isFlagEliminarPantallas()) {
            finalizarPantallas();
        }
    }

    private void inicializarPantallaInicioSesion() {
        setContentView(R.layout.inicio_sesion);
        setTitle(Const.CARACTER_VACIO);
    }

    private void inicializarComponentes() {

        etExtension = (EditText) this.findViewById(R.id.etExtension);
        etClave = (EditText) this.findViewById(R.id.etClave);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        Drawable icon = resize(getResources().getDrawable(R.drawable.small_logo));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        imgLogo.setLayoutParams(params);
        imgLogo.setImageDrawable(icon);

        idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();

        tvOlvidoClave = (TextView) this.findViewById(R.id.tvOlvidoClave);
        tvOlvidoClave.setOnClickListener(this);

        btnIniciar = (Button) this.findViewById(R.id.btnEntrar);
        btnIniciar.setOnClickListener(this);

        btnRegistro = (Button) this.findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);

        extension = Const.CARACTER_VACIO;
        clave = Const.CARACTER_VACIO;

        setVersionApp();
        preloadAnnex();
    }

    private void preloadAnnex() {
        preferences = new ItyPreferences(getApplicationContext());
        etExtension.setText(preferences.getAnnex());
    }

    private void validarCamposInicioSesion() {

        extension = etExtension.getText().toString().trim();
        clave = etClave.getText().toString().trim();

        if (extension.equals(Const.CARACTER_VACIO) && clave.trim().equals(Const.CARACTER_VACIO)) {

            Crouton.showText(InicioSesion.this, getString(R.string.msj_error_datos_incompletos), Style.ALERT);

        } else if (extension.equals(Const.cad_vacia)) {

            Crouton.showText(InicioSesion.this, getString(R.string.msjis_error_falta_extension), Style.ALERT);

        } else if (clave.equals(Const.cad_vacia)) {

            Crouton.showText(InicioSesion.this, getString(R.string.msj_error_falta_clave), Style.ALERT);

        } else {

            if (AppUtil.existeConexionInternet(InicioSesion.this)) {
                iniciarSesion();

            } else {
                Crouton.showText(InicioSesion.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
            }

        }
    }

    /*
     * Obtiene datos del inicio de sesion
     * */
    private void iniciarSesion() {


        pdDialogoEspera = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msjis_inicio_sesion), true, true);
        pdDialogoEspera.setCanceledOnTouchOutside(false);
        pdDialogoEspera.setCancelable(false);

        EntradaInisionSesion entrada = new EntradaInisionSesion();
        entrada.setIdIdioma(idiomaSeleccionado);
        entrada.setAnexo(extension.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
        entrada.setClave(clave.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));

        //se valida si hay conexion a internet
        if (AppUtil.existeConexionInternet(InicioSesion.this)) {

            ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.CARACTER_VACIO)) {
                        BeanUsuario usuario = (BeanUsuario) respuesta.getObjeto();


                        if (usuario.getAnexo() == null) {
                            AppUtil.showMessage(getApplicationContext(), getString(R.string.msjis_error_usuario_no_existe), true);
                            pdDialogoEspera.dismiss();
                            return;

                        } else {
                            //Save the annex to load after on login
                            app.setUsuario(usuario);
                            assert preferences != null;
                            preferences = new ItyPreferences(getApplicationContext());
                            preferences.saveAnnex(usuario.getAnexo());
                            usuario.setClave(clave);
                            usuario.setEstado(Const.ESTADO_USUARIO_CONECTADO);
                        }


                        //buscar si existe el usuario en la bd
                        boolean existe = LogicaUsuario.existeUsuario(getApplicationContext(), usuario.getAnexo());
                        if (existe) {
                            LogicaUsuario.guardarUsuario(getApplicationContext(), usuario, Const.BD_MODIFICARIS);
                        } else {

                            usuario.setID_Idioma(idiomaSeleccionado);
                            respuesta.setObjeto(usuario);
                            respuesta.setNombreObjecto(Const.almacenar_usuario);

                            /**almacenar usuario**/
                            AsyncGuardarDAOTask almacenar = new AsyncGuardarDAOTask(getApplicationContext());
                            almacenar.execute(respuesta);
                        }


                        //get values user on parse
                        getUserChat(usuario.getAnexo());

                    } else {
                        pdDialogoEspera.dismiss();
                        Crouton.showText(InicioSesion.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }

            });
            ejecutar.obtenerDatosSesion(entrada);


        } else {
            pdDialogoEspera.dismiss();
            Crouton.showText(InicioSesion.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }


    }

//    private void iniciarServicioITalkYou() {
//
//        Log.e(Const.DEBUG_SERVICE,"[INICIOSESIONACTIVITY] iniciar servicio");
//        Intent intent = new Intent(getApplicationContext(), ItalkYouService.class);
//        registerSIP(intent);
//
//    }

    private void enviarCorreo(String texto) {

        pdDialogoEspera = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msjis_enviando_mail), true, true);
        pdDialogoEspera.setCanceledOnTouchOutside(false);

        ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {

                if (respuesta.getError().equals(Const.cad_vacia)) {

                    BeanBase salida = (BeanBase) respuesta.getObjeto();
                    pdDialogoEspera.dismiss();

                    if (salida.getResultado().equals(Const.RESULTADO_OK)) {
                        Crouton.showText(InicioSesion.this, getString(R.string.msjis_info_envio_mail), Style.CONFIRM);
                    } else {
                        Crouton.showText(InicioSesion.this, getString(R.string.msjis_error_recuperar_clave), Style.ALERT);
                    }

                } else {
                    pdDialogoEspera.dismiss();
                    Crouton.showText(InicioSesion.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                }
            }
        });
        ejecutar.recuperarClave(texto.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
    }

    private void recuperarClave() {

        FragmentManager fm = getSupportFragmentManager();
        DialogoEnviarCorreo dialogo = new DialogoEnviarCorreo();

        onEnviarCorreoListener mlistener = new onEnviarCorreoListener() {

            @Override
            public void setEnviarCorreoListener(String texto) {

                if (AppUtil.existeConexionInternet(InicioSesion.this)) {
                    enviarCorreo(texto);
                } else {
                    Crouton.showText(InicioSesion.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
                }
            }
        };

        dialogo.mlistener = mlistener;
        dialogo.show(fm, Const.CARACTER_VACIO);
    }

    private void registerUser(final BeanUsuario user) {

        //Make user again
        final ParseObject usuario = new ParseObject(ChatITY.TABLE_USER);
        usuario.put(ChatITY.USER_USER, user.getNombres());
        usuario.put(ChatITY.USER_STATUS, Const.status_connected);
        usuario.put(ChatITY.USER_ANNEX, user.getAnexo());
        usuario.put(ChatITY.USER_PHONE, user.getNumero());
        usuario.put(ChatITY.USER_FLAG_IMAGE, false);
        usuario.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                pdDialogoEspera.dismiss();

                if (e == null) {
                    app.setUsuarioChat(usuario);
                    LogicaPantalla.personalizarIntentVistaPrincipal(InicioSesion.this, Const.PANTALLA_PRINCIPAL, InicioSesion.class.getSimpleName());
                } else {
                    //Error to trying save user. Save eventually
                    usuario.saveEventually();
                    Crouton.showText(InicioSesion.this, getString(R.string.try_again), Style.ALERT);
                }
            }
        });
    }


    private void obtenerListaChat() {
        final Context _context = getApplicationContext();
        if (AppUtil.existeConexionInternet(_context)) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    ParseQuery<ParseObject> queryLstChats = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
                    queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_ANNEX, app.getUsuario().getAnexo());
                    queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                    queryLstChats.orderByDescending(LogicChat.COLUMN_LAST_DATE_MESSAGE);
                    try {
                        List<ParseObject> chatList = queryLstChats.find();
                        if (!chatList.isEmpty())
                            LogicChat.insertChats(_context, chatList);

                        ParseObject.pinAllInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED, chatList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {
            Crouton.showText(this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private void getUserChat(final String annex) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
        query.whereEqualTo(ChatITY.USER_ANNEX, annex);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject user, ParseException e) {

                if (e == null) {
                    app.setUsuarioChat(user);
//                    getChats();
                    pdDialogoEspera.dismiss();
                    LogicaPantalla.personalizarIntentVistaPrincipal(InicioSesion.this, Const.PANTALLA_PRINCIPAL, InicioSesion.class.getSimpleName());
                } else {
                    //User no exist
                    registerUser(app.getUsuario());
                }
            }
        });

    }


    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 1000, 761, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    private String setVersionApp() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        tvVersion.append(Const.ESPACIO_BLANCO + version);
        return version;
    }


}//End class
