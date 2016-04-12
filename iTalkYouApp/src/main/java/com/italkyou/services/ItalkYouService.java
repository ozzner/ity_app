package com.italkyou.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.italkyou.gui.R;
import com.italkyou.sip.SIPListener;
import com.italkyou.sip.SIPServiceBroadcastHandler;
import com.italkyou.sip.SipManager;
import com.italkyou.utils.ActivityUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.RegistrationStateUtil;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;


public class ItalkYouService extends Service implements SIPListener {

    //    public static final String EXTRA_SIP_NUMBER_TO_CALL = "rsantillanc.extra.sip.address";
//    public static final String DISPLAY_NAME = "rsantillanc.extra.sip.displayName";
    private static final int NOTIFICATION_ID = 2;
    private static ActivityUtil ACTIVITIES = null;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    final SIPServiceBinder iBinder = new SIPServiceBinder();
    public static boolean isRunning = false;
    private static volatile Class<? extends Activity> mNotificationActivity = null;
    private RegistrationStateUtil status = RegistrationStateUtil.NONE;
    final NetworkStatusChanged mNetworkReceiver = new NetworkStatusChanged();
    private String displayName = "";

    public static void registerActivity(Class<? extends Activity> activity) {
        mNotificationActivity = activity;
    }

//    public void buildNotification(Class<? extends Activity> _activity, int resourceIdByState) {
//        mNotificationActivity = _activity;
//        final NotificationManager cm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
//        cm.notify(NOTIFICATION_ID, createNotification(resourceIdByState));
//    }


    public final class NetworkStatusChanged extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ItalkYouService.this.refreshRegisterForNetworkChange(context);
        }
    }


    public class SIPServiceBinder extends Binder {
        public ItalkYouService getService() {
            return ItalkYouService.this;
        }
    }

    @Override
    public void onCreate() {
//        super.onCreate();

        //Register receiver networkInfo
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, intentFilter);
        isRunning = true;
//        startForeground(NOTIFICATION_ID, createNotification(R.string.registration_progress));
        Log.e(Const.DEBUG, "ItalkYouService isRunning -> " + isRunning);
    }

    void removeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        stopForeground(false);
    }

    @Override
    public synchronized void onDestroy() {
        isRunning = false;
        unregisterReceiver(mNetworkReceiver);
        SipManager.newInstance().setSipListener(null);
        Log.e(Const.DEBUG, "ItalkYouService isRunning -> " + isRunning);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(Const.DEBUG, "ItalkYouService onBind -> " + true);
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Const.DEBUG, "ItalkYouService onStartCommand -> " + true);

        // releasing wakelock WakefulBroadcastReceiver if needed
        if (intent != null)
//            WakefulBroadcastReceiver.completeWakefulIntent(intent);

            //Listener
            startSIPManagerListener();


        //START_STICKY, el sistema intentará volver a crear el servicio después de que se mató
        //START_NOT_STICKY, el sistema NO trata de volver a crear el servicio después de que se mató

        if (intent != null) {
            String sipNumberToCall = intent.getStringExtra(SipManager.SIP_NUMBER_TO_CALL);
            String sipDisplayName = intent.getStringExtra(SipManager.SIP_DISPLAY_NAME);

//            if (sipAddress == null)
//                Toast.makeText(getApplicationContext(), "ItalkYouService sipAddress is null", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(getApplicationContext(), "ItalkYouService sipAddress is no null -> " + sipAddress, Toast.LENGTH_SHORT).show();

            handlerAudioCall(sipNumberToCall, sipDisplayName);
        }
        return START_STICKY;
    }

    private void handlerAudioCall(String numberToCall, String sipDisplayName) {
        if (numberToCall == null || numberToCall == Const.CARACTER_VACIO)
            return;
        makeCall(numberToCall, sipDisplayName);
    }

    private void refreshRegisterForNetworkChange(Context context) {
        NetworkInfo netInf = ((ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        Log.e(Const.DEBUG, "NetworkInfo -> " + netInf);

        if (netInf == null)
            return;

        Log.e(Const.DEBUG, "NetworkInfo getTypeName-> " + netInf.getTypeName());
        Log.e(Const.DEBUG, "NetworkInfo getReason -> " + netInf.getReason());
        Log.e(Const.DEBUG, "NetworkInfo isAvailable -> " + netInf.isAvailable());
        Log.e(Const.DEBUG, "NetworkInfo isConnected -> " + netInf.isConnected());
        Log.e(Const.DEBUG, "NetworkInfo isConnectedOrConnecting -> " + netInf.isConnectedOrConnecting());
        Log.e(Const.DEBUG, "NetworkInfo isRoaming -> " + netInf.isRoaming());

        if (netInf.isConnected())
            SipManager.newInstance().refresh();

    }

    private void startSIPManagerListener() {
        Log.e(Const.DEBUG, "ItalkYouService startSIPManagerListener -> " + true);
        assert SipManager.newInstance().getSipListener() == null;
        SipManager.newInstance().setSipListener(ItalkYouService.this);
    }

    private Notification createNotification(int resId) {
        if (mNotificationActivity == null) {
            mNotificationActivity = ACTIVITIES.getMainActivity();

//            if (mSimlarStatus == SimlarStatus.ONGOING_CALL) {
//
//                if (mSimlarCallState.isRinging()) {
//                    mNotificationActivity = ACTIVITIES.getRingingActivity();
//                } else {
//                    mNotificationActivity = ACTIVITIES.getCallActivity();
//                }
//
//            } else {
//                mNotificationActivity = ACTIVITIES.getMainActivity();
//            }
//            Lg.i("no activity registered based on mSimlarStatus=", mSimlarStatus, " we now take: ", mNotificationActivity.getSimpleName());
        }

        /// Note: we do not want the TaskStackBuilder here
        final PendingIntent activity = PendingIntent.getActivity(this, 0,
                new Intent(this, mNotificationActivity)
                        .addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        .putExtra(Const.DATOS_TIPO, Const.PANTALLA_CONTACTO), 0);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.contacto_italkyou);
//        notificationBuilder.setLargeIcon(getDrawable(R.drawable.ic_action_call));
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText(getString(resId));
        notificationBuilder.setOngoing(true);
        notificationBuilder.setContentIntent(activity);
        notificationBuilder.setWhen(System.currentTimeMillis());
        return notificationBuilder.build();
    }

    /**
     * <------------Call manager------------>
     */

    public void acceptCall() {
        SipManager.newInstance().accept();
    }

    public void cancelCall() {
        SipManager.newInstance().hangout();
    }

    public void declineCall() {
        SipManager.newInstance().decline();
    }

    public void speakerOn(boolean on) {
        SipManager.newInstance().speaker(on);
    }

    public void speakerRinging() {
        SipManager.newInstance().speakerRinging();
    }


    public void speakerInitCall() {
        SipManager.newInstance().speakerInitCall();
    }


    public void makeCall(String numberToCall, String sipDisplayName) {
        SipManager.newInstance().call(numberToCall,sipDisplayName);
    }

    public static void startService(Context ctx, Intent in) {
        ctx.startService(in);
        isRunning = true;
    }

    public static void initActivities(final ActivityUtil activities) {
        if (ACTIVITIES == null) {
            ACTIVITIES = activities;
        }
    }


    // SIP LISTENER CALLBACKS

    @Override
    public void onRegistrationChange(LinphoneCore.RegistrationState state) {

//        if (LinphoneCore.RegistrationState.RegistrationOk.equals(state))
//            buildNotification(mNotificationActivity, status.getResourceIdByState(state));
//        else
//            removeNotification();
    }


    @Override
    public void onCallStatusChanged(LinphoneCall.State state) {
        SIPServiceBroadcastHandler.sendCallStatusChanged(getApplicationContext(), state);
    }


}
