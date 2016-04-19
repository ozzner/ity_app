package com.italkyou.controladores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanContact;
import com.italkyou.beans.entradas.EntradaRegistarUsuario;
import com.italkyou.beans.salidas.SalidaHistorialLlamadas;
import com.italkyou.beans.salidas.SalidaHistorialSaldo;
import com.italkyou.gui.VistaPrincipalActivity;
import com.italkyou.gui.chat.ChatMensajeActivity;
import com.italkyou.gui.chat.VisualizarImagenActivity;
import com.italkyou.gui.contactos.ContactoActivity;
import com.italkyou.gui.inicio.InicioSesion;
import com.italkyou.gui.inicio.Presentacion;
import com.italkyou.gui.inicio.ValidarPin;
import com.italkyou.gui.llamada.CallActivity;
import com.italkyou.gui.llamada.HistorialLlamadasActivity;
import com.italkyou.gui.llamada.IncomingCallActivity;
import com.italkyou.gui.menu.SaldoActivity;
import com.italkyou.sip.SipManager;
import com.italkyou.utils.Const;
import com.italkyou.utils.ItyPreferences;

import java.io.ByteArrayOutputStream;

public class LogicaPantalla {

    public static void goToProfile(Activity activity, Class<?> klassOut) {

        Intent intent = new Intent(activity, klassOut);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);

    }

    public static void personalizarIntent(Activity activity, Class<?> klassOut) {

        Intent intent = new Intent(activity, klassOut);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void personalizarIntentActivity(Activity activity, Class<?> klassOut) {
        Intent intent = new Intent(activity, klassOut);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }


    public static void irRegistroVistaPrincipal(Activity activity, String pantalla) {
        Intent intent = new Intent(activity, VistaPrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.DATOS_TIPO, pantalla);
        activity.startActivity(intent);
        activity.finish();
    }

//    public static void personalizarIntentVistaPrincipal(Activity activity, String pantalla, SalidaDatosChatGrupal datos) {
//        Intent intent = new Intent(activity, VistaPrincipalActivity.class);
//        intent.putExtra(Const.DATOS_TIPO, pantalla);
//        intent.putExtra(Const.DATOS_GRUPO_CHAT, datos);
//        activity.startActivity(intent);
//        activity.finish();
//    }

    public static void personalizarIntentValidarPin(Activity activity, EntradaRegistarUsuario entrada) {
        Intent intent = new Intent(activity, ValidarPin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.DATOS_REGISTRO, entrada);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void personalizarIntentValidarPinExitosamente(Activity activity, EntradaRegistarUsuario entrada) {
        Intent intent = new Intent(activity, ValidarPin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.DATOS_REGISTRO, entrada);
        activity.startActivity(intent);
        activity.finish();
    }


    public static void personalizarIntentDatosContacto(Activity activity, BeanContact contacto) {
        Intent intent = new Intent(activity, ContactoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        ((AppiTalkYou) activity.getApplication()).setCurrentContact(contacto);
        activity.startActivity(intent);
    }

    public static void personalizarIntentMovimientos(Activity activity, SalidaHistorialSaldo saldo) {
        Intent intent = new Intent(activity, SaldoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.DATOS_MOVIMIENTOS, saldo);
        activity.startActivity(intent);
    }

    public static void personalizarIntentHistorialLlamadas(Activity activity, SalidaHistorialLlamadas historial) {
        Intent intent = new Intent(activity, HistorialLlamadasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.DATOS_LLAMADAS, historial);
        intent.putExtra("BASIC", false);
        activity.startActivity(intent);
    }

    //Make audio call
    public static void makeAudioCallIntent(Activity activity, String tipo, String numberToCall, BeanContact contacto, String name, String flagHistorial, String prefijo) {

        Intent intent = new Intent(activity, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("PREFIJO", prefijo);
        intent.putExtra("NUMERO", numberToCall);
        intent.putExtra("BASIC", false);
        intent.putExtra("TIPO", tipo);
        intent.putExtra(SipManager.SIP_NUMBER_TO_CALL, numberToCall);
        intent.putExtra("HISTORIAL", flagHistorial);//1 Si es historial 2 si es contactos, el resto si viene del marcador.7
//       Update contact
//        ((AppiTalkYou) activity.getApplication()).setCurrentContact(contacto);
        activity.startActivity(intent);

    }

    public static void intentBasicCall(Activity activity, String tipo, String numberToCall, String nombre) {
        Intent intent = new Intent(activity, CallActivity.class);
        intent.putExtra("NUMERO", numberToCall);
        intent.putExtra("BASIC", true);
        intent.putExtra("TIPO", tipo);
        intent.putExtra("NAME", nombre);
        intent.putExtra(SipManager.SIP_NUMBER_TO_CALL, numberToCall);
        intent.putExtra(SipManager.SIP_DISPLAY_NAME, nombre);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);

    }

    //click
    public static void personalizarIntentListaMensajes(Activity activity, String idChat, boolean isArchived, String typeChat, boolean isFromMakeChat) {
        Intent pantalla = new Intent(activity, ChatMensajeActivity.class);
        pantalla.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pantalla.putExtra(Const.identificador_chat, idChat);
        pantalla.putExtra(Const.TAG_IS_PUSH, false);
        pantalla.putExtra(Const.TAG_FROM_MAKE_CHAT, isFromMakeChat);
        pantalla.putExtra(Const.TAG_FLAG_ARCHIVED, isArchived);
        pantalla.putExtra(Const.TAG_TYPE_CHAT, typeChat);
        pantalla.putExtra(Const.TAG_TYPE_CHAT, typeChat);
        activity.startActivity(pantalla);

    }

    //back
    public static void personalizarIntentVistaPrincipal(Activity activity, String pantalla, String simpleName, boolean isFromMakeChat) {
        Intent intent = new Intent(activity, VistaPrincipalActivity.class);
        intent.putExtra(Const.DATOS_TIPO, pantalla);
        intent.putExtra(Const.FROM_ACTIVITY, simpleName);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ItyPreferences p = new ItyPreferences(activity);
        p.putBoolean(Const.TAG_FROM_MAKE_CHAT, isFromMakeChat);

        activity.startActivity(intent);
        if (simpleName.equals(Presentacion.class.getSimpleName())
                || simpleName.equals(IncomingCallActivity.class.getSimpleName())
                || simpleName.equals(ChatMensajeActivity.class.getSimpleName())
                || simpleName.equals(InicioSesion.class.getSimpleName()))
            activity.finish();
    }

    public static void personalizarIntentVisualizarImagen(Activity activity, Bitmap archivo, String messageChatId) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        archivo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        Intent pantalla = new Intent(activity, VisualizarImagenActivity.class);
        pantalla.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pantalla.putExtra(Const.datos_imagen, data);
        pantalla.putExtra(Const.TAG_CHATMESSAGE_ID, messageChatId);
        activity.startActivity(pantalla);
//		activity.finish();
    }

    public static void IntentIncommingCall(Context c, long id) {
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setClass(c, IncomingCallActivity.class);
        i.putExtra(Const.SIP_SESSION_ID, id);
        ItyPreferences p = new ItyPreferences(c);
        p.putString(id + "");
        c.startActivity(i);
    }


    public static void makeAudioCallIntent(Activity activity, String numberToCall, String displayName) {
        Intent call = new Intent(activity, CallActivity.class);
        call.putExtra(SipManager.SIP_NUMBER_TO_CALL, numberToCall);
        call.putExtra(SipManager.SIP_DISPLAY_NAME, displayName);
        call.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(call);
    }


}
