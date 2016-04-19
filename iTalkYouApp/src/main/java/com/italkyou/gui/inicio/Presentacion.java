package com.italkyou.gui.inicio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.AsyncGuardarDAOTask;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaPais;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.R;
import com.italkyou.gui.Testing.ReportActivity;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.italkyou.utils.ExceptionHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Presentacion extends Activity {

    private static final String TAG = Presentacion.class.getSimpleName();
    private ProgressDialog pdEspera;
    private static int TIEMPO_PRESENTACION = 3500;
    private AppiTalkYou app;
    private IntentFilter intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inicializarPantallaPresentacion();
        app = (AppiTalkYou) getApplication();

        //Init error report
        initErrorReporting();

        //se inicializa la pantalla


        //se valida la configuracion
        validarConfiguracion();


    }

    private void initErrorReporting() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, ReportActivity.class));
    }


    private void inicializarPantallaPresentacion() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        ((AppiTalkYou) getApplication()).setFlagEliminarPantallas(false);
    }

    /**
     * Metodo para registrar la actividad actual.
     */
//    private void registrarActividad() {
//
//        try {
//
//            intent = new IntentFilter(SipConfig.FILTRO_LLAMADAS_SIP);
//            receptorLlamadas = new CallStatusReceiver();
//            this.registerReceiver(receptorLlamadas, intent);//tem`poralmente borrado para probar notificaciones parse
//        } catch (Exception e) {
//
//        }
//
//    }
    private void validarConfiguracion() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                boolean existe = LogicaPais.existeListado(getApplicationContext());

                if (existe) {

                    boolean existeUsuario = LogicaUsuario.existeUsuarioActivo(getApplicationContext());

                    if (existeUsuario) {
                        Log.e(TAG, "[SPLASHACTIVITY] validarConfiguracion existe usuario");

                        //Update message
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                pdEspera = ProgressDialog.show(Presentacion.this, Const.TITULO_APP, getString(R.string.msj_actualizando_informacion), true, true);
                                pdEspera.setCanceledOnTouchOutside(false);
                                pdEspera.setCancelable(false);

                            }
                        });


                        BeanUsuario usuario = LogicaUsuario.obtenerUsuario(getApplicationContext());
                        app.setUsuario(usuario);

                        //Init value userChat
                        getUserChat(usuario.getAnexo());
                        LogicaPantalla.personalizarIntentVistaPrincipal(Presentacion.this, Const.PANTALLA_PRINCIPAL, Presentacion.class.getSimpleName(),false);

                    } else {
                        cambiarPantallaPresentacion();
                    }
                } else {

                    if (AppUtil.existeConexionInternet(Presentacion.this)) {
                        descargarConfiguracion();
                    } else {
                        AppUtil.MostrarMensaje(Presentacion.this, getString(R.string.msj_error_conexion_internet));
                    }

                }

            }
        }).start();


    }

    private void obtenerSaldo(String anexo) {

        ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {

                AppiTalkYou app = (AppiTalkYou) getApplication();

                if (respuesta.getError().equals(Const.CARACTER_VACIO)) {

                    BeanSaldo saldo = (BeanSaldo) respuesta.getObjeto();

                    if (saldo.getResultado().equals(Const.RESULTADO_OK)) {
                        app.setSaldo(saldo.getBalance());
                    } else {
                        //app.setSaldo(SipConfig.SALDO_DEFECTO);
                    }

                } else {
                    //app.setSaldo(SipConfig.SALDO_DEFECTO);
                }
//                pdEspera.dismiss();
                LogicaPantalla.personalizarIntentVistaPrincipal(Presentacion.this, Const.PANTALLA_PRINCIPAL, Presentacion.class.getSimpleName(),false);
            }
        });
        ejecutar.obtenerSaldo(anexo);

    }

    private void descargarConfiguracion() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdEspera = ProgressDialog.show(Presentacion.this, Const.TITULO_APP, getString(R.string.msj_descargando_informacion), true, true);
                pdEspera.setCanceledOnTouchOutside(false);
                pdEspera.setCancelable(false);
            }
        });


        ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {

                   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pdEspera != null)
                            if (pdEspera.isShowing())
                                pdEspera.dismiss();

                    }
                });


                if (respuesta.getError().equals(Const.CARACTER_VACIO)) {
                    @SuppressWarnings("unchecked")
                    List<BeanPais> listado = (List<BeanPais>) respuesta.getObjeto();

                    if (listado.size() > 0) {

                        respuesta.setNombreObjecto(Const.almacenar_paises);
                        AsyncGuardarDAOTask almacenar = new AsyncGuardarDAOTask(getApplicationContext());
                        almacenar.execute(respuesta);

                    } else {

                        AppUtil.MostrarMensaje(Presentacion.this, getString(R.string.msj_error_producido_servidor));
                    }

                } else {
                    AppUtil.MostrarMensaje(Presentacion.this, getString(R.string.msj_error_conexion_ws));
                }

                LogicaPantalla.personalizarIntent(Presentacion.this, InicioSesion.class);

            }
        });
        ejecutar.obtenerListadoPaises();

    }

    private void cambiarPantallaPresentacion() {

        TimerTask ttTarea = new TimerTask() {
            @Override
            public void run() {
                irPantallaLogin();
            }
        };
        Timer temporizador = new Timer();
        temporizador.schedule(ttTarea, TIEMPO_PRESENTACION);
    }

    private void irPantallaLogin() {
        Intent intentInicio = new Intent().setClass(Presentacion.this, InicioSesion.class);
        startActivity(intentInicio);
        finish();

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        try {
//            unregisterReceiver(receptorLlamadas);
//        } catch (Exception ex) {
//            Log.e(TAG, "unregisterReceiver, ERROR", ex);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            unregisterReceiver(receptorLlamadas);
//        } catch (Exception ex) {
//            Log.e(TAG, "unregisterReceiver, ERROR", ex);
//        }
//    }

    private void obtenerListaChat() {
        final Context _context = getApplicationContext();
        if (app.getUsuarioChat() != null)
            if (AppUtil.existeConexionInternet(_context)) {

                ParseQuery<ParseObject> queryLstChats = ParseQuery.getQuery(LogicChat.TAG_CHAT_USER);
                queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_USERID, ParseObject.createWithoutData(ChatITY.TABLE_USER, app.getUsuarioChat().getObjectId()));
                queryLstChats.whereEqualTo(LogicChat.CHATUSER_COLUMN_STATUS, Const.CHATUSER_STATUS_ACTIVE);
                queryLstChats.orderByDescending(LogicChat.CHATUSER_COLUMN_UPDATEDAT);
                queryLstChats.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> chatList, ParseException e) {

                        if (e == null) {
                            app.setIsChatEnable(true);
                            ParseObject.unpinAllInBackground(LogicChat.CHAT_ACTIVE);
                            ParseObject.pinAllInBackground(LogicChat.CHAT_ACTIVE, chatList);
                            LogicChat.insertChats(getApplicationContext(), chatList);
                        }
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Crouton.showText(Presentacion.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
                    }
                });
            }
    }

    private void getUserChat(final String annex) {

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
            query.whereEqualTo(ChatITY.USER_ANNEX, annex);
            ParseObject usuarioChat = query.getFirst();
            app.setUsuarioChat(usuarioChat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Obtiene lista de chats
        obtenerListaChat();


    }

}
