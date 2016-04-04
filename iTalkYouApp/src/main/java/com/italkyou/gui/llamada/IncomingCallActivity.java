package com.italkyou.gui.llamada;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.entradas.EntradaBuscarContactos;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.R;
import com.italkyou.sip.SIPServiceCommunicator;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import org.linphone.core.LinphoneCall;

import java.util.List;

public class IncomingCallActivity extends Activity implements SensorEventListener {

    private static final String TAG = CallActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private ImageView btnAceptar;
    private ImageView btnIgnorar;
    private ImageView ivSpeaker;
    private ImageButton btnFinalizar;
    private TextView txt_Anexo_Entrante;
    private TextView txt_Celular_Anexo_Entrante;
    private TextView tvCallStatus;


    private LinearLayout controls;
    private LinearLayout layEndCall;
    private EntradaBuscarContactos entrada;
    private String anexo_entrante = "";

    private AppiTalkYou appiTalkYou;
    private MediaPlayer mPlayer;


    public boolean flagSpeaker = false;
    private boolean isKilled = false;

    private int sound = 0;

    //vibrador
    private Vibrator vibrador = null;
    private long[] patronVibracion = {0, 250, 200, 250, 150, 150, 75, 150, 75, 150};
    private Context _context;

    //Sensor
    private Sensor mSensorProximity;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private AudioManager audioManager;
    private SIPServiceCommunicator communicator = new CommunicatorIncomingCall();


    public final class CommunicatorIncomingCall extends SIPServiceCommunicator {

        @Override
        public void onBoundConnected() {
            Log.e(Const.DEBUG, "IncomingCallActivity onBoundConnected -> ok");
            IncomingCallActivity.this.onStatusChanged(-1, null);
            IncomingCallActivity.this.startRinging();
        }

        @Override
        public void onUpdateCallState(int value, String status) {
            Log.e(Const.DEBUG, "IncomingCallActivity onUpdateCallState -> ok");
            IncomingCallActivity.this.onStatusChanged(value, status);
        }
    }

    private void onStatusChanged(int value, String status) {
        if (status != null)
            if (LinphoneCall.State.CallEnd.value() == value) {
                Log.e(Const.DEBUG, "IncomingCallActivity onStatusChanged -> " + status);
                if (!isKilled)
                    finishCall();
            }

        Log.e(Const.DEBUG, "IncomingCallActivity onStatusChanged -> " + status);
    }

    private void finishCall() {
        stopRinging();
        disabledButtons(true);
        updateStatus(R.string.call_status_ended);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                LogicaPantalla.personalizarIntentVistaPrincipal(IncomingCallActivity.this, Const.PANTALLA_CONTACTO, IncomingCallActivity.class.getSimpleName());
            }
        }, 3000);
    }

    private void disabledButtons(boolean on) {

        if (on) {
            btnAceptar.setEnabled(!on);
            btnIgnorar.setEnabled(!on);
        }
    }

    private void updateStatus(int call_status_id) {
        tvCallStatus.setText(call_status_id);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appiTalkYou = ((AppiTalkYou) getApplication());
        _context = getApplicationContext();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        inicializarPantallaRecibirLlamadas();
        buscarContactos();
        inicializarComponentes();
        setUpSensor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_FASTEST);
        communicator.doBindService(this);
    }

    @Override
    protected void onPause() {
        communicator.unBindService(true);
        super.onPause();
        unregisterSensor();
    }


    private void initSession() {
        //Enabled bluetooth if is available
        turnBluetooth(true);
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

    private void setUpSensor() {
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        stopRinging();
        Toast.makeText(_context, getString(R.string.message_finish_calling), Toast.LENGTH_LONG).show();
    }

    /*************************************************************************************/

    private void inicializarPantallaRecibirLlamadas() {
        setContentView(R.layout.recibir_llamada);

        // make sure this activity is shown even if the phone is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        anexo_entrante = getIntent().getStringExtra("anexo_entrante");
        entrada = new EntradaBuscarContactos();
        entrada.setDato(anexo_entrante);

        if (appiTalkYou.getUsuario() != null && appiTalkYou.getUsuario().getAnexo() != null)
            entrada.setAnexo(appiTalkYou.getUsuario().getAnexo());

        entrada.setTipo("2");

        vibrador = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mPlayer = MediaPlayer.create(IncomingCallActivity.this, R.raw.italkyou);

        sound = Integer.parseInt(appiTalkYou.getUsuario().getNotificacion());
    }

    private void startRinging() {

        if (sound == Integer.parseInt(Const.WITHOUT_SOUND))
            return;

        if (vibrador != null && sound == Integer.parseInt(Const.VIBRATOR)) {
            vibrador.vibrate(patronVibracion, 0);
            return;
        }

        if (mPlayer != null && sound == Integer.parseInt(Const.SOUND)) {
            speakerRinging();
            vibrador.vibrate(patronVibracion, 0);
            mPlayer.start();
        }

    }

    private void stopRinging() {

        if (vibrador != null)
            vibrador.cancel();

        if (mPlayer != null)
            mPlayer.stop();

    }

    private void unregisterSensor() {
        mSensorManager.unregisterListener(IncomingCallActivity.this, mSensorProximity);
    }

    private void inicializarComponentes() {

        btnAceptar = (ImageView) findViewById(R.id.btnAceptarLlamada);
        btnIgnorar = (ImageView) findViewById(R.id.btnIgnorarLlamada);
        btnFinalizar = (ImageButton) findViewById(R.id.btnFinalizarLlamada);
        controls = (LinearLayout) findViewById(R.id.ll_buttons);
        layEndCall = (LinearLayout) findViewById(R.id.lay_end_call);
        ivSpeaker = (ImageView) findViewById(R.id.iv_speaker);
        tvCallStatus = (TextView) findViewById(R.id.tvCallStatus);


        ivSpeaker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                turnSpeaker();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                acceptCall();
            }

        });

        btnIgnorar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                declineIncomingCall();
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

    }

    private void turnSpeaker() {
        turnBluetooth(flagSpeaker);
        flagSpeaker = !flagSpeaker;
        communicator.getService().speakerOn(flagSpeaker);

        if (flagSpeaker)
            ivSpeaker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_active));
        else
            ivSpeaker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_unactive));

    }

   void speakerRinging(){
       communicator.getService().speakerRinging();
   }

    private void acceptCall() {
        communicator.getService().acceptCall();
        controls.setVisibility(View.GONE);
        layEndCall.setVisibility(View.VISIBLE);
        stopRinging();
    }

    private void endCall() {
        isKilled = true;
        btnFinalizar.setVisibility(View.GONE);
        communicator.getService().cancelCall();
        finish();
    }

    private void declineIncomingCall() {
        stopRinging();
        isKilled = true;
        communicator.getService().declineCall();
        LogicaPantalla.personalizarIntentVistaPrincipal(IncomingCallActivity.this, Const.PANTALLA_CONTACTO, IncomingCallActivity.class.getSimpleName());
    }

    private void buscarContactos() {

        txt_Anexo_Entrante = (TextView) findViewById(R.id.NroAnexoEntrante);
        txt_Celular_Anexo_Entrante = (TextView) findViewById(R.id.CelularAnexoEntrante);

        ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {


                if (respuesta.getError().equals(Const.CARACTER_VACIO)) {
                    @SuppressWarnings("unchecked")
                    List<Object> listado = (List<Object>) respuesta.getObjeto();
                    if (listado.size() > 0) {
                        OutputContact contacto = (OutputContact) listado.get(0);
                        txt_Anexo_Entrante.setText(contacto.getNombre() + "\n" + AppUtil.formatAnnex(contacto.getAnexo()));

                        if (contacto.getCelular().length() == 6)
                            txt_Celular_Anexo_Entrante.setText(AppUtil.formatAnnex(contacto.getCelular()));
                        else
                            txt_Celular_Anexo_Entrante.setText((contacto.getCelular()));

                    }
                } else {

                }
            }
        });
        ejecutar.busquedaContactos(entrada);
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

    //-----------------[ SENSOR LISTENER ]

    @Override
    public void onSensorChanged(SensorEvent event) {
        float proximity = event.values[0];

        if (proximity == 0 || proximity == 3)
            turnOffScreen();
        else
            turnOnScreen();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
