package com.italkyou.controladores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.italkyou.beans.BeanUsuario;
import com.italkyou.utils.Const;

public class IniciarAppItk extends BroadcastReceiver {

    private static final String TAG = IniciarAppItk.class.getSimpleName() + Const.ESPACIO_BLANCO;

    /**
     *
     * Metodo para recibir los eventos del telefono.
     * @param context, es el contexto de la aplicacion.
     * @param intent, es el intent filtrada.
     */
	@Override
	public void onReceive(Context context, Intent intent) {

//        Log.e(Const.DEBUG_PUSH, TAG + intent.getData().toString());
//        JSONObject json;
//        try {
//            Log.e(Const.DEBUG_PUSH, TAG + intent.getExtras());
//            json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
//            Log.e(Const.DEBUG_PUSH, TAG + json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//	        if(verificarInicioServicioITalkYou(context)){
//                iniciarServicioITalkYou(context);
//            }
	}

    /**
     * Metodo para verificar si es necesario iniciar el servicio de ITalkYou
     * Si el usuario esta logueado a la app se inicia el servicio despues de un reinicio del dispositivo.
     * Si el usuario no esta logueado a la app no se inicia el servicio despues de un reinicio del dispositivo.
     * @param contextoApp, es el contexto de la aplicacion.
     * @return un flag que indica si se inicia o no el servicio.
     */
    private boolean verificarInicioServicioITalkYou(Context contextoApp){

        BeanUsuario bUsuario= LogicaUsuario.obtenerUsuario(contextoApp);

        if(bUsuario!=null && bUsuario.getEstado().equals(Const.ESTADO_USUARIO_CONECTADO))
            return true;
        else
            return false;

    }

    /**
     * Metodo para iniciar el Servicio ITalkYou
     * @param contextoApp, es el contexto de la aplicacion.
     */
//    private void iniciarServicioITalkYou(Context contextoApp){
//        Intent miIntent = new Intent(contextoApp, ItalkYouService.class);
//        contextoApp.registerSIP(miIntent);
//    }
}
