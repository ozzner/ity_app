package com.italkyou.gui.personalizado;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicChat;
import com.italkyou.controladores.LogicContact;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.dao.TablasBD;
import com.italkyou.gui.R;
import com.italkyou.gui.inicio.InicioSesion;
import com.italkyou.sip.SipManager;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.italkyou.utils.AppUtil;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DialogoCerrarSesion extends DialogFragment implements OnClickListener {
    private static final String TAG = DialogoCerrarSesion.class.getSimpleName();
    private Button btnAceptar;
    private Button btnCancelar;
    private AppiTalkYou app;
    private SipManager sipManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.cerrar_sesion);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView titulo = (TextView) dialog.findViewById(R.id.tvTituloDialogo);
        titulo.setText(getActivity().getString(R.string.titulo_cerrar_sesion));

        btnAceptar = (Button) dialog.findViewById(R.id.btnAceptarCerrarSesion);
        btnAceptar.setOnClickListener(this);

        btnCancelar = (Button) dialog.findViewById(R.id.btnCancelarCerrarSesion);
        btnCancelar.setOnClickListener(this);

        app = (AppiTalkYou) getActivity().getApplication();
        return dialog;

    }


    @Override
    public void onClick(View v) {

        if (v == btnAceptar) {
            cerrarSesion();

        } else if (v == btnCancelar) {
            dismiss();
        }
    }

    private void cerrarSesion() {

        app = (AppiTalkYou) getActivity().getApplication();

        final BeanUsuario usuario = app.getUsuario();
        final ParseObject usuarioChat = app.getUsuarioChat();

        if (AppUtil.existeConexionInternet(getActivity())) {

            final ProgressDialog pd = ProgressDialog.show(getActivity(), Const.TITULO_APP, getString(R.string.msj_cerrando_sesion), true, true);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);

            SipManager.newInstance().unregister();

            ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.CARACTER_VACIO)) {
                        SalidaResultado salida = (SalidaResultado) respuesta.getObjeto();

                        if (salida.getResultado().equals(Const.RESULTADO_OK)) {
                            pd.dismiss();
                            LogicaPantalla.personalizarIntent(getActivity(), InicioSesion.class);

                            //Borramos los canales y chats del usuario
                            removeChannels();
                            removeFromLocalStore();

                            //Actualizamos el flag de estado de manera local.
                            LogicaUsuario.modificarDato(getActivity().getApplicationContext(), TablasBD.COLUMNA_ESTADO, Const.ESTADO_USUARIO_DESCONECTADO, usuario.getAnexo());

                            //modificamos estado del usuario en parse.
                            ParseObject updateUsuario = ParseObject.createWithoutData(ChatITY.TABLE_USER, usuarioChat.getObjectId());
                            updateUsuario.put(ChatITY.USER_STATUS, Const.status_no_connected);
                            updateUsuario.saveInBackground();

                            app.setUsuarioChat(null);
                            app.setUsuario(null);
                            app.setIsSipEnabled(false);

                            //Borrar contactos sqlite
                            borrarContactos();

                            //Delete all chats
                            dropChats();

                            //appiTalkYoux.setFlagEliminarPantallas(true);
                            unregisterAnnex();


                            //Detenemos el servicio
                            stopItyService();


                        } else {

                            pd.dismiss();
                            dismiss();
                            Crouton.showText(getActivity(), salida.getMensaje(), Style.ALERT);
                        }
                    } else {
                        pd.dismiss();
                        dismiss();
                        Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }

            });
            ejecutar.cerrarSesion(usuario.getAnexo(), usuario.getO_ck());

        } else {
            AppUtil.MostrarMensaje(getActivity(), getString(R.string.msj_error_conexion_internet));
        }
    }

    private void dropChats() {
        LogicChat.dropAll(getActivity());
    }

    private void stopItyService() {
        AppUtil.detenerServicio(getActivity().getApplicationContext());
//        getActivity().stopService(new Intent(getActivity(), ItalkYouService.class));
    }

    private void unregisterAnnex() {
        SipManager.newInstance().unregister();
    }


    private void removeChannels() {

        //Remove personal Channel
        ParsePush.unsubscribeInBackground(Const.TAG_MY_CHANNEL + app.getUsuario().getAnexo(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.e(Const.DEBUG_PUSH, TAG + "Unsubscribe Correct! Personal channel");
            }
        });

        //Removing chats channels
        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");

        for (final String channel : subscribedChannels) {
            if (channel.contains(Const.TAG_CHANNEL_GROUP)
                    || channel.contains(Const.TAG_CHANNEL_PRIVATE)
                    || channel.contains(Const.TAG_MY_CHANNEL)) {

                ParsePush.unsubscribeInBackground(channel, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e(Const.DEBUG_PUSH, TAG + "Removing channel : " + channel);
                        }
                    }
                });


            }
        }
    }

    private void removeFromLocalStore() {
        ParseObject.unpinAllInBackground(LogicChat.TAG_CHAT_ARCHIVED);
        ParseObject.unpinAllInBackground(LogicChat.TAG_CHAT_NO_ARCHIVED);
        ParseObject.unpinAllInBackground(Const.TAG_CHAT_MESSAGE_LOCAL_STORE);
    }


    private void borrarContactos() {
        LogicContact logicaContactos = new LogicContact();
        logicaContactos.borrarDatos(getActivity().getApplicationContext());
    }
}
