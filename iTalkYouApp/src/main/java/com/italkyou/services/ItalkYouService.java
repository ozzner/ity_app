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
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.italkyou.gui.R;
import com.italkyou.sip.SIPListener;
import com.italkyou.sip.SIPManager;
import com.italkyou.sip.SIPServiceBroadcastHandler;
import com.italkyou.utils.ActivityUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.RegistrationStateUtil;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;


public class ItalkYouService extends Service implements SIPListener {

    public static final String EXTRA_SIP_ADDRESS = "rsantillanc.extra.sip.address";
    private static final int NOTIFICATION_ID = 2;
    private static ActivityUtil ACTIVITIES = null;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    final SIPServiceBinder iBinder = new SIPServiceBinder();
    public static boolean isRunning = false;
    private String sipAddress = null;
    private static volatile Class<? extends Activity> mNotificationActivity = null;
    private RegistrationStateUtil status = RegistrationStateUtil.NONE;
    final NetworkStatusChanged mNetworkReceiver = new NetworkStatusChanged();

    public static void registerActivity(Class<? extends Activity> activity) {
        mNotificationActivity = activity;
    }

    public void buildNotification(Class<? extends Activity> _activity, int resourceIdByState) {
        mNotificationActivity = _activity;
        final NotificationManager cm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        cm.notify(NOTIFICATION_ID, createNotification(resourceIdByState));
    }


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
        SIPManager.newInstance().setSipListener(null);
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
            WakefulBroadcastReceiver.completeWakefulIntent(intent);

        //Listener
        startSIPManagerListener();


        //START_STICKY, el sistema intentará volver a crear el servicio después de que se mató
        //START_NOT_STICKY, el sistema NO trata de volver a crear el servicio después de que se mató

        if (intent != null) {
            sipAddress = intent.getStringExtra(EXTRA_SIP_ADDRESS);

//            if (sipAddress == null)
//                Toast.makeText(getApplicationContext(), "ItalkYouService sipAddress is null", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(getApplicationContext(), "ItalkYouService sipAddress is no null -> " + sipAddress, Toast.LENGTH_SHORT).show();

            handlerAudioCall(sipAddress);
        }
        return START_STICKY;
    }

    private void handlerAudioCall(String sipAddress) {
        if (sipAddress == null)
            return;

        makeCall(sipAddress);
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
            SIPManager.newInstance().refresh();

    }

    private void startSIPManagerListener() {
        Log.e(Const.DEBUG, "ItalkYouService startSIPManagerListener -> " + true);
        assert SIPManager.newInstance().getSipListener() == null;
        SIPManager.newInstance().setSipListener(ItalkYouService.this);
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
        SIPManager.newInstance().accept();
    }

    public void cancelCall() {
        SIPManager.newInstance().hangout();
    }

    public void declineCall() {
        SIPManager.newInstance().decline();
    }

    public void speakerOn(boolean on) {
        SIPManager.newInstance().speaker(on);
    }

    public void speakerRinging(){
        SIPManager.newInstance().speakerRinging();
    }


    public void speakerInitCall() {
        SIPManager.newInstance().speakerInitCall();
    }


    public void makeCall(String uri) {
        SIPManager.newInstance().call(uri);
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

        if (LinphoneCore.RegistrationState.RegistrationOk.equals(state))
            buildNotification(mNotificationActivity, status.getResourceIdByState(state));
        else
            removeNotification();
    }


    @Override
    public void onCallStatusChanged(LinphoneCall.State state) {
        SIPServiceBroadcastHandler.sendCallStatusChanged(getApplicationContext(), state);
    }


}
