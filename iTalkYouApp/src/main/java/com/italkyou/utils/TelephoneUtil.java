package com.italkyou.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

/**
 * Created by Moises on 04/02/2015.
 */
public class TelephoneUtil {

    /**
     * Metodo para obtener la densidad del dispositivo
     * @param contexto, Es el contexto de la aplicacion.
     * @return la densidad del equipo en una cadena.
     */
    public static String obtenerDensidad(Context contexto){

        return Float.toString(contexto.getResources().getDisplayMetrics().density);

    }

    public static void realizarVibracionTelefono(Context contexto){
        Vibrator vibrador = (Vibrator) contexto.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        vibrador.vibrate(pattern,0);

    }

    public static void realizarVibracionTelefono(Context contexto,int tiempoVibracion){
        Vibrator vibrador = (Vibrator) contexto.getSystemService(Context.VIBRATOR_SERVICE);
        //long[] pattern = {0, 100, 1000};
        vibrador.vibrate(tiempoVibracion);

    }


    public static String getCountry(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return String.format("Country: %s",
                mTelephonyMgr.getNetworkCountryIso());
    }


    public static void generarSonidoBeep(boolean flagConectado){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);

        if(flagConectado)
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 200);
        else
            toneG.startTone(ToneGenerator.TONE_SUP_ERROR,200);
    }


}
