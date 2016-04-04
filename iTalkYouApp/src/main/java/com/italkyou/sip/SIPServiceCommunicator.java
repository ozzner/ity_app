package com.italkyou.sip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.italkyou.beans.BeanUsuario;
import com.italkyou.services.ItalkYouService;
import com.italkyou.utils.Const;


/**
 * Created by RenzoD on 26/02/2016.
 */
public class SIPServiceCommunicator {

    ItalkYouService mService = null;
    final ServiceConnection connection = new SIPServiceConnection();
    final BroadcastReceiver broadcastReceiver = new SIPServiceBroadcastReceiver();
    private Context _context;
    private Class<? extends Activity> _activity;

    public void startITYService(Context context, Class<? extends Activity> activity, String sipUriToCall, boolean flagRegister) {
        this._activity = activity;
        this._context = context;

        ItalkYouService.registerActivity(_activity);
        Intent intent = new Intent(context, ItalkYouService.class);

        if (flagRegister) {
            intent.putExtra(ItalkYouService.EXTRA_SIP_ADDRESS, sipUriToCall);
            ItalkYouService.startService(context, intent);
        }

        context.bindService(intent, connection, 0);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(SIPServiceBroadcastHandler.EXTRA_NAME_BROADCAST));

    }

    public void doBindService(Context c) {

        if (connection == null)
            Log.e(Const.DEBUG, "doBindService connection is null");

        Intent intent = new Intent(c, ItalkYouService.class);

        if (intent == null)
            Log.e(Const.DEBUG, "doBindService intent is null");

        if (!getService().isRunning) {
            Log.e(Const.DEBUG, "doBindService isRunning-> " + getService().isRunning);
        }

        c.startService(intent);
        c.bindService(intent, connection, 0);
        LocalBroadcastManager.getInstance(c).registerReceiver(broadcastReceiver, new IntentFilter(SIPServiceBroadcastHandler.EXTRA_NAME_BROADCAST));

    }

    public void unBindService(boolean startService) {
        if (_context == null) {
            return;
        }

        LocalBroadcastManager.getInstance(_context).unregisterReceiver(broadcastReceiver);
        if (ItalkYouService.isRunning && mService != null) {
            _context.unbindService(connection);
        }

        _context = null;
    }

    public void registerSIP(BeanUsuario user, Context context) {
        SIPManager.newInstance().connectSIP(user, context);
    }

    /*
    * Service connection
    * */
    private final class SIPServiceConnection implements ServiceConnection {

        public SIPServiceConnection() {
            super();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.

            Log.e(Const.DEBUG, "SIPServiceCommunicator onServiceConnected " + name.getPackageName());
            mService = ((ItalkYouService.SIPServiceBinder) service).getService();
//            mService.buildNotification(_activity, );
            onBoundConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            Log.e(Const.DEBUG, "SIPServiceCommunicator onServiceDisconnected " + name.getPackageName());
            mService = null;
        }

    }

    /*
    * Broadcast receiver
    * */
    public class SIPServiceBroadcastReceiver extends BroadcastReceiver {

        public SIPServiceBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            Log.e(Const.DEBUG, "SIPServiceBroadcastReceiver -> ok");

            SIPServiceBroadcastHandler broadcastH = (SIPServiceBroadcastHandler) intent.getExtras().getSerializable(SIPServiceBroadcastHandler.ACTION_CALL_STATUS);

            if (mService == null) {
                Log.e(Const.DEBUG, "mService -> null");
                return;
            }

            switch (broadcastH.getType()) {
                case TYPE_CALL:
                    int value = intent.getExtras().getInt(SIPServiceBroadcastHandler.EXTRA_CALL_STATE_VALUE);
                    String message = intent.getExtras().getString(SIPServiceBroadcastHandler.EXTRA_CALL_STATE_MESSAGE);

                    onUpdateCallState(value, message);
                    Log.e(Const.DEBUG, "TYPE_CALL broadcastH.getType() -> " + broadcastH.getType());
                    return;
                default:
                    onUpdateCallState(-1, null);
                    Log.e(Const.DEBUG, "default broadcastH.getType() -> " + broadcastH.getType());
                    return;
            }


        }
    }


    public void onBoundConnected() {

    }

    public void onUpdateCallState(int value, String message) {

    }


    public ItalkYouService getService() {
        return mService;
    }

}
