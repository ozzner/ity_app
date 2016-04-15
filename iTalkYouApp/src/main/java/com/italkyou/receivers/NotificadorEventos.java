package com.italkyou.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.italkyou.beans.BeanUsuario;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.R;
import com.italkyou.gui.chat.ChatMensajeActivity;
import com.italkyou.gui.llamada.IncomingCallActivity;
import com.italkyou.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificadorEventos extends BroadcastReceiver {

    private static final String TAG = NotificadorEventos.class.getSimpleName() + Const.ESPACIO_BLANCO;

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {


        BeanUsuario usuario = LogicaUsuario.obtenerUsuario(context);
        if (intent.getAction().equals("com.iTalkYou.UPDATE_STATUS")) {

            try {
//				String action = intent.getAction();
//				String channel = intent.getExtras().getString("com.parse.Channel");

                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.e(Const.DEBUG_PUSH, TAG + json);


                String chatId = json.getString("chatId");
                if (ChatMensajeActivity.isActivo && ChatMensajeActivity.currentChatId.equals(chatId)) {
                    ChatMensajeActivity.actualizarListaMensajes(chatId, true);
                    LogicChat.updateChatUser(context, chatId, json.getString("mensaje"), false);
                } else {
                    LogicChat.updateChatUser(context, chatId, json.getString("mensaje"), true);
                    ChatMensajeActivity.saveOnLocalStore(chatId);
                    String type = json.getString("type");

                    //Notificacion chat
                    Intent notifyIntent = new Intent(context, ChatMensajeActivity.class);
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notifyIntent.putExtra(Const.identificador_chat, chatId);
                    notifyIntent.putExtra(Const.TAG_TYPE_CHAT, type);
                    notifyIntent.putExtra(Const.TAG_IS_PUSH, true);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    /**NOTIFICACION**/

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(context);

                    builder
                            .setSmallIcon(R.drawable.ic_logo_ity)
                            .setContentTitle(json.getString("nombre"))
                            .setContentText(json.getString("mensaje"))
                            .setTicker("Notificacion iTalkYou")
                            .setLights(0xff00ff00, 300, 1000)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    if (usuario.getNotificacion().equals(Const.SOUND)) {
                        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        builder.setSound(soundUri);
                    }

                    Notification notification = builder.getNotification();
                    notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(0x1, notification);

                    if (usuario.getNotificacion().equals(Const.VIBRATOR)) {
                        Vibrator vibrador = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrador.vibrate(3000);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (intent.getAction().equals("com.iTalkYou.INCOMING_CALL")) {

            /*****************************************LLamada***********************************************/
            Intent notifyIntent = new Intent(context, IncomingCallActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

            //*********** NOTIFICACION ****************************
            NotificationManager notificationManager
                    = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);

            builder
                    .setSmallIcon(R.drawable.ic_logo_ity)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.llamada_entrando))
                    .setTicker("Notificacion iTalkYou")
                    .setLights(0xff00ff00, 300, 1000)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (usuario.getNotificacion().equals(Const.SOUND)) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(soundUri);
            }

            Notification notification = builder.getNotification();
            notification.flags = notification.flags | Notification.FLAG_INSISTENT
                    | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notification);

            if (usuario.getNotificacion().equals(Const.VIBRATOR)) {
                Vibrator vibrador = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrador.vibrate(3000);
            }
        }
    }
}

