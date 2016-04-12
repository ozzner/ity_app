package com.italkyou.gui.inicio;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaInisionSesion;
import com.italkyou.beans.entradas.EntradaRegistarUsuario;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.beans.salidas.SalidaUsuario;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.AsyncGuardarDAOTask;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseInicioActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.Testing.ReportActivity;
import com.italkyou.services.ItalkYouService;
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

import java.util.Timer;
import java.util.TimerTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ValidarPin extends BaseInicioActivity implements OnClickListener {

    private static final String TAG = ValidarPin.class.getSimpleName();
    private static EntradaRegistarUsuario entrada;
    private Button btByCall;
    //    private Button btBySMS;
    private TextView labelInformation;
    private TextView tvTimerPin;
    private boolean encontrado;
    protected ProgressDialog pd;
    private ProgressBar pbLoader;
    private int nroIntento;
    private static TextView tvPin;
    private static Activity mActivity;
    private static Context mContext;
    private static AppiTalkYou app;
    private boolean isByEmail = false;


    public ValidarPin() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.validar_pin);
        initErrorReporting();
        setTitle(Const.cad_vacia);
        mContext = this;
        mActivity = this;
        app = ((AppiTalkYou) getApplication());
        //ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);
        entrada = (EntradaRegistarUsuario) this.getIntent().getSerializableExtra(Const.DATOS_REGISTRO);
//        entrada.toString();
        inicializarComponentes();
        ocultarBarraAcciones();

        //this send automatically a request sms.
        sendSMSVerification();

    }


    private void initErrorReporting() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, ReportActivity.class));
    }


    private void inicializarComponentes() {

        TextView tvTituloDialogo = (TextView) findViewById(R.id.tvTituloDialogo);
        tvTituloDialogo.setText(mActivity.getString(R.string.titulo_validar_pin));

        tvPin = (TextView) findViewById(R.id.tvValorPin);
        tvPin.setText(entrada.getValorPin().getPin());

        labelInformation = (TextView) findViewById(R.id.tvTextoPin);
        tvTimerPin = (TextView) findViewById(R.id.tvTimerPin);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);


//        btBySMS = (Button) findViewById(R.id.bt_send_sms);
//        btBySMS.setOnClickListener(this);
        btByCall = (Button) findViewById(R.id.btnEnviarPin);
        btByCall.setOnClickListener(this);

        encontrado = false;
        showLoader(true);
    }

    private void showLoader(boolean on) {
        if (on)
            pbLoader.setVisibility(View.VISIBLE);
        else
            pbLoader.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
//        int id = v.getId();

        if (isByEmail) {
            sendSupportEmail();
        } else {
            btByCall.setVisibility(View.GONE);
            if (AppUtil.existeConexionInternet(mActivity)) {
//            pd = ProgressDialog.show(mActivity, Const.TITULO_APP, mContext.getString(R.string.msjrc_realizar_llamada), true, true);
//            pd.setCancelable(false);

                //Realiza la llamada al número indicado
                ExecuteRequest ejecutarValidarPin = new ExecuteRequest(new ResultadoOperacionListener() {

                    @Override
                    public void onOperationDone(BeanRespuestaOperacion respuesta) {
                        Log.e(Const.DEBUG, "onOperationDone: ok" );
                    }
                });
                ejecutarValidarPin.validarPin(entrada);

                //Ejecuta si o si
                procesoVerificarEstadoPin();

            } else {
                AppUtil.MostrarMensaje(mActivity, mActivity.getString(R.string.msj_error_conexion_internet));
            }

    /*    if (id == R.id.bt_send_sms) {
            labelInformation.setText(mActivity.getString(R.string.message_wait_sms));
            sendSMSVerification();

        } else {


        }*/


        }


    }

    private void sendSupportEmail() {

        new Thread(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        pd = ProgressDialog.show(mActivity, Const.TITULO_APP, getString(R.string.sending), true);
                    }
                });

                ExecuteRequest exc = new ExecuteRequest(new ResultadoOperacionListener() {
                    @Override
                    public void onOperationDone(BeanRespuestaOperacion respuesta) {
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                if (pd != null)
                                    if (pd.isShowing())
                                        pd.dismiss();

                                Toast.makeText(getApplicationContext(), R.string.message_send_success, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });

                    }
                });
                exc.sendEmailSupport(entrada);
            }
        }).start();


    }

    private void sendSMSVerification() {
        final long timeToSchedule = (long) (1000 * 60 * 1.5); //minutes
        final int[] decrement = {0};

        CountDownTimer countDown = new CountDownTimer(timeToSchedule, 1000) {
            @Override
            public void onTick(long l) {
                int seg = (int) (l / 1000);
                int min = seg / 60;
                seg = seg % 60;

                Log.e(Const.DEBUG, "Seconds: " + seg);
                if (min > 0)
                    tvTimerPin.setText(String.format("%d:%02d min", min, seg));
                else
                    tvTimerPin.setText(String.format("%d:%02d s", min, seg));
            }

            @Override
            public void onFinish() {
                updateInformation(getString(R.string.message_option_call));
                showLoader(false);
                enabledCallOption();
            }
        };

        //Start
        countDown.start();

        //Request SMS
        ExecuteRequest oper = new ExecuteRequest(new ResultadoOperacionListener() {
            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {
                Log.e(Const.DEBUG, "SMS enviado ok");
//                Toast.makeText(mActivity, "Sms enviado.", Toast.LENGTH_SHORT).show();
            }
        });

        //Make request
        String phoneNumber = entrada.getIdPrefijo() + entrada.getTelefono();
        String pinNumber = entrada.getValorPin().getPin();
        oper.requestSmsVerification(phoneNumber, pinNumber);
    }

    private void updateInformation(String message) {
        labelInformation.setText(message);
    }

    private void enabledCallOption() {
        btByCall.setVisibility(View.VISIBLE);
        tvTimerPin.setVisibility(View.INVISIBLE);
    }

    /**
     * Metodo para verificar el estado de PIN, a ver si ya se valido con la llamada.
     */
    private void procesoVerificarEstadoPin() {

//        pd.setMessage(mActivity.getString(R.string.msjrc_validar_pin));
        final Timer temporizadotTarea = new Timer();
        TimerTask ttTareaAgendada;
        Log.e(TAG, "procesoVerificarEstadoPin");
        nroIntento = 1;

        //Loading again
        showLoader(true);
        //update message
        updateInformation(getString(R.string.message_option_call_start));

        ttTareaAgendada = new TimerTask() {

            @Override
            public void run() {
                Log.e(TAG, "nro Intento " + nroIntento);
                verificarEstadoPin();
                nroIntento++;

                if (encontrado || nroIntento > 10) {

                    Log.e(TAG, "Se cancela el temporizador de validar Pin. Estado: encontrado");
                    temporizadotTarea.cancel();

                    if (encontrado) {

                        if (AppUtil.existeConexionInternet(mActivity)) {
                            procesoRegistrarUsuario();

                        } else {
//                            pd.dismiss();
                            AppUtil.MostrarMensaje(mActivity, mActivity.getString(R.string.msj_error_conexion_internet));
                        }

                    } else {
//                        pd.dismiss();

                        runOnUiThread(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run() {
                                //Hide loader
                                showLoader(false);

                                //Update message
                                updateInformation(getString(R.string.message_option_email));

                                //Update
                                btByCall.setVisibility(View.VISIBLE);

                                //update button
                                final int sdk = android.os.Build.VERSION.SDK_INT;
                                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    btByCall.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_cancel_selector));
                                } else {
                                    btByCall.setBackground(getResources().getDrawable(R.drawable.button_cancel_selector));
                                }

                                btByCall.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                                btByCall.getD.setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.MULTIPLY);
                                btByCall.setText(getString(R.string.button_support));
                                isByEmail = true;
//                                AppUtil.mostrarAvisoInformativo(mActivity, mActivity.getString(R.string.msjrc_error_vali dar_pin));
                            }
                        });

                    }
                }

            }
        };

        temporizadotTarea.schedule(ttTareaAgendada, 3000, 5000);
    }

    private void verificarEstadoPin() {

        ExecuteRequest ejecutar = new ExecuteRequest();
        SalidaResultado salida = ejecutar.obtenerEstadoPin(entrada.getValorPin().getPin());

        if (salida.getResultado() != null && salida.getResultado().equals(Const.RESULTADO_OK)) {
//            pd.dismiss();
            encontrado = true;
        }

    }

	/*
     * Registrar usuario en la base de datos
	 */

    private void procesoRegistrarUsuario() {

        //Update progressbar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = ProgressDialog.show(mActivity, Const.TITULO_APP, mActivity.getString(R.string.msjrc_registrar_usuario), true, true);
                pd.setCancelable(false);
            }
        });


        ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {
                Log.e(TAG, "registrar usuario respuesta " + respuesta.getError() + "-");

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    SalidaUsuario respuestaRegistro = (SalidaUsuario) respuesta.getObjeto();

                    if (respuestaRegistro.getResultado().equals(Const.RESULTADO_ERROR)) {
                        pd.dismiss();
                        Crouton.showText(mActivity, mActivity.getString(R.string.msj_error_producido_servidor), Style.ALERT);

                    } else {
                        Crouton.showText(mActivity, mActivity.getString(R.string.message_user_created_success) + respuestaRegistro.getAnexo(), Style.INFO);
                        entrada.setAnexo(respuestaRegistro.getAnexo());

                        EntradaInisionSesion entradais = new EntradaInisionSesion();
                        entradais.setIdIdioma(entrada.getIdioma());
                        entradais.setAnexo(entrada.getAnexo());
                        entradais.setClave(entrada.getClave());
                        procesoIniciarSesion(entradais);
                    }

                } else {
                    pd.dismiss();
                    AppUtil.mostrarAvisoInformativo(mActivity, mActivity.getString(R.string.msj_error_conexion_ws));
                }
            }

        });
        ejecutar.registrarUsuario(entrada);
    }


    private void procesoIniciarSesion(EntradaInisionSesion entradais) {


        pd.setMessage(mActivity.getString(R.string.msjis_inicio_sesion));

        if (AppUtil.existeConexionInternet(mActivity)) {

            ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        BeanUsuario usuario = (BeanUsuario) respuesta.getObjeto();

                        usuario.setID_Idioma(entrada.getIdioma());
                        usuario.setClave(entrada.getClave());
                        usuario.setEstado(Const.ESTADO_USUARIO_CONECTADO);
                        respuesta.setObjeto(usuario);
                        respuesta.setNombreObjecto(Const.almacenar_usuario);

                        AsyncGuardarDAOTask almacenar = new AsyncGuardarDAOTask(mActivity);
                        almacenar.execute(respuesta);

                        //Save the annex to load after on login
                        ItyPreferences preferences = new ItyPreferences(mActivity);
                        preferences.saveAnnex(usuario.getAnexo());

                        //se almacenan los contactos en la base de datos interna.
//                            almacenarContactosDispositivo();

//                            iniciarServicioiTalkYou();

                        /**obtener saldo**/
                        registrarUsuarioChat(usuario);


//                        if (usuario.getResultado().equals(Const.RESULTADO_OK)) {
//
//
//                        } else {
//                            pd.dismiss();
//                            Crouton.showText(mActivity, mActivity.getString(R.string.msjis_error_usuario_no_existe), Style.ALERT);
//                        }
                    } else {
                        pd.dismiss();
                        Crouton.showText(mActivity, mActivity.getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }
            });

            ejecutar.obtenerDatosSesion(entradais);
        } else {
            pd.dismiss();
            AppUtil.MostrarMensaje(mActivity, mActivity.getString(R.string.msj_error_conexion_internet));
        }
    }

    private void registrarUsuarioChat(final BeanUsuario user) {

        pd.setMessage(mActivity.getString(R.string.message_chat_registering));


        ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
        query.whereEqualTo(ChatITY.USER_ANNEX, user.getAnexo());
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject currentChatUser, ParseException e) {
                if (e == null) {
                    if (pd.isShowing())
                        pd.dismiss();
                    currentChatUser.put(ChatITY.USER_STATUS,Const.status_connected);
                    app.setUsuarioChat(currentChatUser);
                    LogicaPantalla.irRegistroVistaPrincipal(mActivity, Const.PANTALLA_PRINCIPAL);
                    app.setFlagEliminarPantallas(true);
                    procesObtenerSaldo(currentChatUser.getString(ChatITY.USER_ANNEX));
                } else {

                    final ParseObject newUser = new ParseObject(ChatITY.TABLE_USER);
                    newUser.put(ChatITY.USER_USER, user.getNombres());
                    newUser.put(ChatITY.USER_STATUS, Const.status_connected);
                    newUser.put(ChatITY.USER_ANNEX, user.getAnexo());
                    newUser.put(ChatITY.USER_PHONE, user.getId_prefijo() + user.getNumero());
                    newUser.put(ChatITY.USER_FLAG_IMAGE, false);
                    newUser.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {

                            if (pd.isShowing())
                                pd.dismiss();

                            if (e == null) {
                                app.setUsuarioChat(newUser);
                                LogicaPantalla.irRegistroVistaPrincipal(mActivity, Const.PANTALLA_PRINCIPAL);
                                app.setFlagEliminarPantallas(true);
                                procesObtenerSaldo(newUser.getString(ChatITY.USER_ANNEX));

                            } else {
                                app.setUsuarioChat(null);
                                LogicaPantalla.irRegistroVistaPrincipal(mActivity, Const.PANTALLA_PRINCIPAL);
                            }
                        }
                    });


                }

            }
        });



    }


    private void procesObtenerSaldo(String anexo) {

        ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {


//                app.setLstContactosTlf(LogicContact.obtenerListadoContactos(getApplicationContext()));
                if (respuesta.getError().equals(Const.CARACTER_VACIO)) {

                    BeanSaldo saldo = (BeanSaldo) respuesta.getObjeto();

                    if (saldo.getResultado().equals(Const.RESULTADO_OK)) {
                        app.setSaldo(saldo.getBalance());
                    } else {
                        //app.setSaldo("0.0000");
                    }

                } else {
                    //app.setSaldo("0.0000");
                }

            }
        });
        ejecutar.obtenerSaldo(anexo);
    }

    private void iniciarServicioiTalkYou() {
        Intent intent = new Intent(mActivity, ItalkYouService.class);
        startService(intent);
    }

    private void almacenarContactosDispositivo() {

//        LogicContact logicaContactos=new LogicContact();
//        //se borran los contactos silos hubieran
//        logicaContactos.borrarDatos(getApplicationContext());
//        //se procede a almacenar los contactos.
//        logicaContactos.almacenarContactosDispositivo(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    public void comparePin(String pin, Context context) {
        if (pin.equals(tvPin.getText().toString()))
            procesoRegistrarUsuario();
        else {
            Toast.makeText(context, getString(R.string.incorrect_pin) + pin, Toast.LENGTH_LONG).show();
        }


    }
}


