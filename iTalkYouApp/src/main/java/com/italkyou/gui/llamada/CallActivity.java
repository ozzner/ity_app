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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.gui.R;
import com.italkyou.sip.SIPServiceCommunicator;
import com.italkyou.sip.SipManager;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import org.linphone.core.LinphoneCall;


public class CallActivity extends Activity implements
        OnClickListener,
        SensorEventListener {


    private static final String TAG = CallActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private ImageView btnTerminarLLamada;
    private ImageView btnAltavoz;

    //Sensores
    private Sensor mSensorProximity;
    private SensorManager mSensorManager;


    private boolean flagAltavoz = false;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private AudioManager audioManager;


    private SIPServiceCommunicator communicator = new CommunicatorCallActivity();
    private String numberToCall = null;
    private String displayName = "";

    //Display
    private TextView tvDisplayName;
    private TextView tvNumberToCall;
    private TextView tvCallStatus;


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

        //Print call status
        if (tvCallStatus != null)
            tvCallStatus.setText(status);

        if (LinphoneCall.State.CallEnd.value() == value || LinphoneCall.State.CallReleased.value() == value) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreen();
        getExtraData();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

    }

    private void getExtraData() {
        numberToCall = getIntent().getStringExtra(SipManager.SIP_NUMBER_TO_CALL);
        displayName = getIntent().getStringExtra(SipManager.SIP_DISPLAY_NAME);

        //Put data
        tvNumberToCall.setText(numberToCall.length() == 6 ? AppUtil.formatAnnex(numberToCall) : numberToCall);
        tvDisplayName.setText(displayName);

    }

    @Override
    public void onClick(View v) {
        if (v == btnTerminarLLamada) {
            endCAll();
        } else if (v == btnAltavoz) {
            turnSpeaker();
        }
    }

    @Override
    public void onBackPressed() {

        unregisterSensor();
        endCAll();
        Toast.makeText(getApplicationContext(), R.string.end_call, Toast.LENGTH_LONG).show();

    }

    private void unregisterSensor() {
        mSensorManager.unregisterListener(CallActivity.this, mSensorProximity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSensor();
        endCAll();
    }

    private void initScreen() {
        //Main
        setContentView(R.layout.pantalla_llamada);

        // make sure this activity is shown even if the phone is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        setUpSensor();

    }

    private void initView() {

        //Display
        tvDisplayName = (TextView) findViewById(R.id.displayName);
        tvNumberToCall = (TextView) findViewById(R.id.number_to_call);
        tvCallStatus = (TextView) findViewById(R.id.call_status);

        //Buttons
        btnTerminarLLamada = (ImageView) findViewById(R.id.btn_end_call);
        btnAltavoz = (ImageView) findViewById(R.id.btn_speaker_turn);

        //Listeners
        btnTerminarLLamada.setOnClickListener(this);
        btnAltavoz.setOnClickListener(this);

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
        communicator.startITYService(getApplicationContext(), CallActivity.class, numberToCall, displayName, numberToCall != null ? true : false);
        mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_FASTEST);

        //Clear data and free memory
        removeExtras();
    }

    private void removeExtras() {
        getIntent().removeExtra(SipManager.SIP_NUMBER_TO_CALL);
        getIntent().removeExtra(SipManager.SIP_DISPLAY_NAME);
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


    public void endCAll() {
        communicator.getService().cancelCall();
        if (mWakeLock.isHeld())
            mWakeLock.release();
        finish();
    }

    private void turnSpeaker() {
        turnBluetooth(flagAltavoz);
        flagAltavoz = !flagAltavoz;
        communicator.getService().speakerOn(flagAltavoz);

        if (flagAltavoz) {
            btnAltavoz.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_active));
        } else {
            btnAltavoz.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_speaker_call_unactive));
        }
    }

    void speakerDown() {
        communicator.getService().speakerInitCall();
    }

    private void turnBluetooth(boolean on) {

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.isBluetoothScoAvailableOffCall()) {

            if (on) {

                audioManager.setMode(0);
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                audioManager.setMode(AudioManager.MODE_IN_CALL);

//                Log.e(Const.DEBUG_CALLS, TAG + "BLUETOOTH : " + on);

            } else {

                audioManager.setBluetoothScoOn(false);
                audioManager.stopBluetoothSco();
                audioManager.setMode(AudioManager.MODE_NORMAL);
                Log.e(Const.DEBUG_CALLS, TAG + "BLUETOOTH : " + on);
            }
        }
//
//        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothA2dpOn: " + audioManager.isBluetoothA2dpOn());
//        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothScoAvailableOffCall: " + audioManager.isBluetoothScoAvailableOffCall());
//        Log.e(Const.DEBUG_CALLS, TAG + "isBluetoothScoOn: " + audioManager.isBluetoothScoOn());


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
//                Log.e(Const.DEBUG_CALLS, TAG + "turnOffScreen release: " + true);
            }
        }

        // turn off screen
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
        mWakeLock.acquire();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float proximity = event.values[0];
//        Log.e(Const.DEBUG_CALLS, TAG + "onSensorChanged proximity: " + proximity);

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
