package com.italkyou.gui.llamada;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.configuraciones.SIPConfig;
import com.italkyou.controladores.LogicaPais;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.controladores.interfaces.OnEventosLlamadasPBX;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.sip.SIPServiceCommunicator;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import org.linphone.core.LinphoneCall;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class  CallActivity extends Activity implements
        OnClickListener,
        AdapterView.OnItemSelectedListener,
        SensorEventListener {


    private static final String TAG = CallActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private AppiTalkYou appiTalkYou;
    private BeanUsuario bUsuario;
    private BeanContact contactoDestino;
    private TextView tvTextoMarcando;
    private EditText etNumeroMarcar;
    private String nroLlamar;
    private String prefijoNumero = null;
    private String tipo;
    private String flagHistorial;
    private ImageView btnTerminarLLamada;
    private ImageView btnAltavoz;
    //    private ImageButton btnLlamar;
    private LinearLayout llContenedorBotones;

    //Sensores
    private Sensor mSensorProximity;
    private SensorManager mSensorManager;


//    private TextView tvSeleccionPais;
//    private Spinner spListaPaises;

    private List<Object> listaPaises;
    private boolean flagAltavoz = false;
    private OnEventosLlamadasPBX onEventosLlamadasPBX;
    private String name;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private AudioManager audioManager;


    private SIPServiceCommunicator communicator = new CommunicatorCallActivity();
    private String sipAddress = null;


    public final class CommunicatorCallActivity extends SIPServiceCommunicator {

        @Override
        public void onBoundConnected() {
            Log.e(Const.DEBUG, "CallActivity onBoundConnected -> ok");
            CallActivity.this.onStatusChanged(-1, null);
            CallActivity.this.speakerDown();
        }

        @Override
        public void onUpdateCallState(int value, String message) {
            Log.e(Const.DEBUG, "CallActivity onUpdateCallState -> " + message);
            CallActivity.this.onStatusChanged(value, message);
        }
    }

    private void onStatusChanged(int value, String status) {

        if (LinphoneCall.State.CallEnd.value() == value || LinphoneCall.State.CallReleased.value() == value) {
            tvTextoMarcando.setText(R.string.call_status_ended);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();
                }
            }, 3000);

        } else if (LinphoneCall.State.Connected.value() == value) {
            tvTextoMarcando.setText(R.string.call_status_connected);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sipAddress = getIntent().getStringExtra(SIPConfig.SIP_SESSION_ID);

        inicializarPantallaLlamadas();
        appiTalkYou = ((AppiTalkYou) getApplication());

//        Energy
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

    }

    @Override
    public void onClick(View v) {

        if (v == btnTerminarLLamada) {
            terminarLlamada();

        } else if (v == btnAltavoz) {
            activarAltavozLlamada();

        }

//        else if (v == btnLlamar) {
//            habilitarControlesLlamadaInternacional(false);
//            if (bUsuario != null)
//                realizarLlamadaInternacional(bUsuario);
//        }
    }

    @Override
    public void onBackPressed() {

        unregisterSensor();
        terminarLlamada();
        Toast.makeText(getApplicationContext(), R.string.end_call, Toast.LENGTH_LONG).show();

    }

    private void unregisterSensor() {
        mSensorManager.unregisterListener(CallActivity.this, mSensorProximity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSensor();
        terminarLlamada();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        BeanPais paisSeleccionado = (BeanPais) listaPaises.get(position);

        if (prefijoNumero == null || flagHistorial.equals("2")) {
            prefijoNumero = paisSeleccionado.getID_Prefijo();
            etNumeroMarcar.setText(prefijoNumero + nroLlamar);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void iniciarMarcacionLlamada() {
        appiTalkYou = (AppiTalkYou) getApplication();
        //Codigos para llamar, solo para pruebas.
        if (appiTalkYou.isSipEnabled()) {

            TimerTask tkLlamada = new TimerTask() {
                @Override
                public void run() {
//                realizarMarcacionTelefono();
                    validarTipoLlamada();
                }
            };

            Timer tmLlamada = new Timer();
            tmLlamada.schedule(tkLlamada, 1000);

        } else {
            CustomAlertDialog.showSingleAlert(this, getResources().getString(R.string.message_alert_call_no_supported));
        }


    }

    /*****************
     * METODOS PARA LLAMADAS
     ***************/
    private void realizarMarcacionTelefono() {

        final BeanUsuario usuario = LogicaUsuario.obtenerUsuario(getApplicationContext());
        BeanPais paisSeleccionado = (BeanPais) listaPaises.get(0);

        prefijoNumero = paisSeleccionado.getID_Prefijo();

        if (nroLlamar.length() > 0) {

            Thread thLlamada = new Thread(new Runnable() {

                @Override
                public void run() {
                    appiTalkYou = (AppiTalkYou) getApplication();
                    //Codigos para llamar, solo para pruebas.
                    if (appiTalkYou.isSipEnabled()) {
                        if (nroLlamar.length() == 6) {// Llamada SIP
                            habilitarControlesLlamadaSip();
//                            sipManager.makeAudioCall("9916" + usuario.getAnexo() + "00003" + nroLlamar);

                        } else if (nroLlamar.length() > 6) {
                            bUsuario = appiTalkYou.getUsuario();
                            habilitarControlesLlamadaInternacional(true);
                            //appiTalkYou.makeAudioCall("9916" + usuario.getAnexo() + "00001" + prefijoNumero+nroLlamar);
                            //appiTalkYou.makeAudioCall("9916" + usuario.getAnexo() + "00001" + nroLlamar);
                        }

                    } else {
                        CustomAlertDialog.showSingleAlert(getApplicationContext(), getString(R.string.message_alert_call_no_supported));
                    }

                }
            });
            thLlamada.start();
        } else {
            Crouton.showText(this, getString(R.string.msj_error_falta_telefono), Style.ALERT);
        }

    }

    /***************
     * METODO PROPIOS
     **********/

    private void inicializarPantallaLlamadas() {

        setContentView(R.layout.pantalla_llamada);

        // make sure this activity is shown even if the phone is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setUpSensor();

//        spListaPaises = (Spinner)this.findViewById(R.id.spListaPaises);
        tvTextoMarcando = (TextView) findViewById(R.id.tvTextoMarcando);
//        tvSeleccionPais = (TextView)findViewById(R.id.tvSeleccionPais);

//        btnLlamar = (ImageButton) findViewById(R.id.btnLlamadaBoton);
//        btnLlamar.setOnClickListener(this);

        llContenedorBotones = (LinearLayout) findViewById(R.id.lnlyLlamadaBotones);

        btnTerminarLLamada = (ImageView) findViewById(R.id.btnFinalizar);
        btnTerminarLLamada.setOnClickListener(this);

        btnAltavoz = (ImageView) findViewById(R.id.btnSpeaker);
        btnAltavoz.setOnClickListener(this);

        obtenerParametrosLlamadas();
        establecerDatosContacto();

        appiTalkYou = (AppiTalkYou) getApplication();

        iniciarMarcacionLlamada();
    }

    private void setUpSensor() {
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sipAddress = getIntent().getStringExtra(SIPConfig.SIP_SESSION_ID);
        getIntent().removeExtra(SIPConfig.SIP_SESSION_ID);
        communicator.startITYService(getApplicationContext(), CallActivity.class, sipAddress, sipAddress != null ? true : false);
        mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void obtenerParametrosLlamadas() {
        String empty = "";
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            if (bundle.getBoolean("BASIC")) {
                nroLlamar = bundle.getString("NUMERO").replace(Const.CADENA_PREFIJO, empty);
                tipo = bundle.getString("TIPO");//Si es 1 es llamado desde el historiaalaa
                name = bundle.getString("NAME");
                contactoDestino = null;


            } else {
                nroLlamar = bundle.getString("NUMERO").replace(Const.CADENA_PREFIJO, empty);
                tipo = bundle.getString("TIPO");//Si es 1 es llamado desde el historiaalaa
                contactoDestino = (BeanContact) bundle.get("CONTACTO");
                flagHistorial = bundle.getString("HISTORIAL");   //Si es 1 es llamado desde el historiaalaa
                prefijoNumero = bundle.getString("PREFIJO");


            }

        }

    }

    private void establecerDatosContacto() {

        etNumeroMarcar = (EditText) findViewById(R.id.etNumeroMarcar);

        if (nroLlamar != null) {

            if (tipo.equals(Const.tipo_llamada_internacional)) {
//                spListaPaises.setVisibility(View.GONE);
                etNumeroMarcar.setText(nroLlamar);
            } else {
                etNumeroMarcar.setText(nroLlamar);
            }

        }


        TextView tvNombreContacto = (TextView) findViewById(R.id.tvLlamadaNombreContacto);

        if (contactoDestino != null)
            tvNombreContacto.setText(contactoDestino.getNombre());
        else
            tvNombreContacto.setText(name);

        String idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();
        listaPaises = LogicaPais.obtenerListadoPaises(getApplicationContext(), idiomaSeleccionado);

        final AdaptadorLista adaptadorPaises = new AdaptadorLista(getApplicationContext(), R.layout.celda_pais, listaPaises,
                BeanPais.class.getSimpleName(), AppUtil.obtenerIdiomaLocal());
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                spListaPaises.setAdapter(adaptadorPaises);
//                spListaPaises.setOnItemSelectedListener(CallActivity.this);
//                spListaPaises.setSelection(0);
//
//            }
//
//        });

    }

    private void habilitarControlesLlamadaSip() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTextoMarcando.setVisibility(View.VISIBLE);
                llContenedorBotones.setVisibility(View.VISIBLE);
            }
        });

    }

    private void habilitarControlesLlamadaInternacional(final boolean flagHabilitar) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (flagHabilitar) {

                    if ((flagHistorial.equals("1"))) {

//                        tvSeleccionPais.setVisibility(View.GONE);
//                        spListaPaises.setVisibility(View.GONE);

                    } else {

                        if (flagHistorial.equals("2")) {

//                            tvSeleccionPais.setVisibility(View.VISIBLE);
//                            spListaPaises.setVisibility(View.VISIBLE);

                        } else {

                            if (etNumeroMarcar != null)
                                etNumeroMarcar.setEnabled(true);

                        }
                    }

//                    btnLlamar.setVisibility(View.VISIBLE);

                } else {

//                    tvSeleccionPais.setVisibility(View.GONE);
//                    spListaPaises.setVisibility(View.GONE);
//                    btnLlamar.setVisibility(View.GONE);
                    llContenedorBotones.setVisibility(View.VISIBLE);

                }

            }
        });

    }

    public void terminarLlamada() {

        communicator.getService().cancelCall();

        if (mWakeLock.isHeld())
            mWakeLock.release();

        finish();

    }

    private void activarAltavozLlamada() {

        turnBluetooth(flagAltavoz);
        flagAltavoz = !flagAltavoz;
        communicator.getService().speakerOn(flagAltavoz);

        if (flagAltavoz) {
            btnAltavoz.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_active));
        } else {
            btnAltavoz.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_unactive));
        }
    }

    void speakerDown(){
        communicator.getService().speakerInitCall();
    }
    private void turnBluetooth(boolean on) {

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.isBluetoothScoAvailableOffCall()){

            if (on){

                audioManager.setMode(0);
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                audioManager.setMode(AudioManager.MODE_IN_CALL);

                Log.e(Const.DEBUG_CALLS, TAG + "BLUETOOTH : " + on);


            }else{

                audioManager.setBluetoothScoOn(false);
                audioManager.stopBluetoothSco();
                audioManager.setMode(AudioManager.MODE_NORMAL);
                Log.e(Const.DEBUG_CALLS, TAG + "BLUETOOTH : " + on);
            }

        }


        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothA2dpOn: " + audioManager.isBluetoothA2dpOn());
        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothScoAvailableOffCall: " + audioManager.isBluetoothScoAvailableOffCall());
        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothScoOn: " + audioManager.isBluetoothScoOn());


    }

    private void realizarLlamadaInternacional(BeanUsuario usuario) {

//        AppiTalkYou appiTalkYou =(AppiTalkYou)getApplication();
//        nroLlamar=etNumeroMarcar.getText().toString().trim();

        if (flagHistorial.equals("1")) {/*Este numero vine del historial */
            //nroLlamar=etNumeroMarcar.getText().toString().trim();
//            sipManager.makeAudioCall("9916" + usuario.getAnexo() + "00001" + nroLlamar);
        } else {
            //appiTalkYou.makeAudioCall("9916" + usuario.getAnexo() + "00001" + prefijoNumero + nroLlamar);
//            sipManager.makeAudioCall("9916" + usuario.getAnexo() + "00001" + nroLlamar);
        }

    }

    private void validarTipoLlamada() {

        final BeanUsuario usuario = LogicaUsuario.obtenerUsuario(getApplicationContext());

        if (tipo.equals(Const.tipo_llamada_anexoVOIP)) {
            habilitarControlesLlamadaSip();
            realizarLlamadaSip(usuario.getAnexo());

        } else if (tipo.equals(Const.tipo_llamada_internacional)) {
            bUsuario = appiTalkYou.getUsuario();
            habilitarControlesLlamadaInternacional(false);
            if (bUsuario != null)
                realizarLlamadaInternacional(bUsuario);
//              habilitarControlesLlamadaInternacional(true);
        }
    }

    //00003 llamada anexo
    private void realizarLlamadaSip(String nroOrigen) {
//        sipManager.makeAudioCall("9916" + nroOrigen + "00003" + nroLlamar);
    }


    public void turnOnScreen() {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        // turn on screen
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void turnOffScreen() {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
                Log.e(Const.DEBUG_CALLS, TAG + "turnOffScreen release: " + true);
            }
        }
        // turn off screen
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
        mWakeLock.acquire();


    }

    //--------------[ SENSORES LISTENER ]


    @Override
    public void onSensorChanged(SensorEvent event) {
        float proximity = event.values[0];
        Log.e(Const.DEBUG_CALLS, TAG + "onSensorChanged proximity: " + proximity);

        if (proximity == 0 || proximity == 3)
            turnOffScreen();
        else
            turnOnScreen();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
