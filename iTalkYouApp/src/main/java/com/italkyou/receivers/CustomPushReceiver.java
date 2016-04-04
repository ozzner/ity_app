package com.italkyou.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.italkyou.utils.Const;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsantillanc on 23/07/2015.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = CustomPushReceiver.class.getSimpleName() + Const.ESPACIO_BLANCO;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;


        try {

            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.e(Const.DEBUG_PUSH, TAG + json);

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}