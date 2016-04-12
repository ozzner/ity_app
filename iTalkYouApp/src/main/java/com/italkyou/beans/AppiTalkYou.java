package com.italkyou.beans;

import android.app.Application;
import android.util.Log;

import com.italkyou.controladores.LogicContact;
import com.italkyou.gui.VistaPrincipalActivity;
import com.italkyou.gui.llamada.CallActivity;
import com.italkyou.gui.llamada.IncomingCallActivity;
import com.italkyou.services.ItalkYouService;
import com.italkyou.sip.FileHelper;
import com.italkyou.utils.ActivityUtil;
import com.italkyou.utils.Const;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.List;

public class AppiTalkYou extends Application {

    private static final String TAG = AppiTalkYou.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static final String ITY_CHANNEL = "iTalkYouChannel";
    //Aplicacion
    public static AppiTalkYou instance = null;
    private BeanUsuario usuario;
    private String idioma;
    private String saldo;

    //Lllamadas
//    private BeanConfiguracionPBX servidorPBX;
//    private BeanAnexoSip anexoSip;
//    private ControladorPBX controladorPBX;
    private boolean anexoEnLinea;

    //Chat
    private ParseObject usuarioChat;
    private boolean isChatEnable = false;

    //Contactos
    private List<Object> lstContactosTlf;
    private boolean flagEliminarPantallas;


    //on runtime
    private String anyNumber;
    private String anyAnnex;
    private boolean isSipEnabled = false;
    private BeanContact currentContact;


    /**
     * **************************
     * CREACION
     * **********************************
     */
    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        iniciarParse();
        FileHelper.init(this);
        iniciarDataITalkYou();
        ItalkYouService.initActivities(new ActivityUtil(VistaPrincipalActivity.class, IncomingCallActivity.class, CallActivity.class));
    }


    private void iniciarParse() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Const.APPLICATION_ID, Const.CLIENT_KEY); // italkyouMovil
        ParseInstallation.getCurrentInstallation().saveInBackground();

        //Generic channel
        ParsePush.subscribeInBackground(ITY_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null)
                    Log.e(Const.DEBUG_PUSH, TAG + "Successfully subscribed to Parse!");
                else
                    Log.e(Const.DEBUG_PUSH, TAG + "Error subscribed to Parse!");

            }
        });


    }


    public ParseObject getUsuarioChat() {
        return usuarioChat;
    }

    public void setUsuarioChat(ParseObject usuarioChat) {
        this.usuarioChat = usuarioChat;
    }

    public BeanUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(BeanUsuario usuario) {
        this.usuario = usuario;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }


//    public void setInterfacePBX(OnEventosLlamadasPBX interfacePBX) {
//
//        if (interfacePBX == null)
//            interfacePBX = this.interfacePBX;
//        else
//            this.interfacePBX = interfacePBX;
//
//        controladorPBX.setInterfaceLlamadaPBX(interfacePBX);
//    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public boolean isFlagEliminarPantallas() {
        return flagEliminarPantallas;
    }

    public void setFlagEliminarPantallas(boolean flagEliminarPantallas) {
        this.flagEliminarPantallas = flagEliminarPantallas;
    }

    public boolean isAnexoEnLinea() {
        return anexoEnLinea;
    }

    public void setAnexoEnLinea(boolean anexoEnLinea) {
        this.anexoEnLinea = anexoEnLinea;
    }

    /******************  INICIO DE CONFIGURACIONES ************************/
    /**
     * Inicio de la configuracion del servidor de comunicacion.
     */
//    private void inicioConfiguracion() {
//        cargarDatosServidor();
//    }
    private void iniciarDataITalkYou() {
        setLstContactosTlf(LogicContact.obtenerListadoContactos(getApplicationContext()));
    }

    /**
     * Carga de datos del servidor de comunicacacion.
     */
//    private void cargarDatosServidor() {
//
//        setServidorPBX(new BeanConfiguracionPBX());
//        getServidorPBX().setPuerto(SipConfig.SIP_PORT);
//        getServidorPBX().setProtocolo(SipConfig.SIP_PROTOCOL);
//        getServidorPBX().setIpSvrComunicacion(SipConfig.SIP_DOMAIN);
//
//        if (controladorPBX == null)
//            controladorPBX = new ControladorPBX(getApplicationContext(), getServidorPBX());
//    }

    /**
     * Si es falso quiere decir que no se necesita valiar el registro
     */
//    public boolean verificarRegistroAnexo(BeanAnexoSip anexoSip, OnEventosLlamadasPBX listenerPBX) {
//
//        if (controladorPBX != null) {
//            return controladorPBX.verificarRegistroAnexo(anexoSip, listenerPBX);
//        }
//
//        return false;
//    }

//    public BeanConfiguracionPBX getServidorPBX() {
//        return servidorPBX;
//    }
//
//    public void setServidorPBX(BeanConfiguracionPBX servidorPBX) {
//        this.servidorPBX = servidorPBX;
//    }
//
//    public BeanAnexoSip getAnexoSip() {
//        return anexoSip;
//    }
//
//    public void setAnexoSip(BeanAnexoSip anexoSip) {
//        this.anexoSip = anexoSip;
//    }

//    public ControladorPBX getControladorPBX() {
//        return controladorPBX;
//    }
//
//    public void setControladorPBX(ControladorPBX controladorPBX) {
//        this.controladorPBX = controladorPBX;
//    }
    public List<Object> getLstContactosTlf() {
        return lstContactosTlf;
    }

    public void setLstContactosTlf(List<Object> lstContactosTlf) {
        this.lstContactosTlf = lstContactosTlf;
    }

    public void actualizarContactosTelefono() {
        iniciarDataITalkYou();
    }

    /**
     * ***************
     * LLAMADAS
     * *********************
     */
//    public boolean realizarRegistroAnexo(BeanAnexoSip anexoSip, OnEventosLlamadasPBX interfacePBX) {
//        controladorPBX = new ControladorPBX(getApplicationContext(), servidorPBX);
//        return controladorPBX.registerAnnex(anexoSip, interfacePBX);
//
//    }

//    public void unregisterAnnex() {
//        controladorPBX.closeSessionSip();
//    }

//    public void realizarLlamada(String nroAnexoLlamar) {
//        if (controladorPBX != null) {
//            //controladorPBX.setInterfaceLlamadaPBX(interfacePBX);
//            controladorPBX.makeAudioCall(nroAnexoLlamar);
//        }
//    }

//    public String realizarRecepcionLlamada(Intent intent) {
//
//        String nroAnexo = "";
//
//        if (controladorPBX != null)
//            nroAnexo = controladorPBX.recibirLlamada(intent);
//
//        return nroAnexo;
//    }

//    public void contestarLlamada(OnEventosLlamadasPBX interfacePBX) {
//        if (controladorPBX != null)
//            controladorPBX.contestarLlamada(interfacePBX);
//    }
//
//    public void colgarLlamada() {
//        controladorPBX.colgarLlamada();
//    }
//
//    public void activarAltavoz(boolean flagAltavoz) {
//        controladorPBX.activarAltavoz(flagAltavoz);
//    }

//    public void cerrarConexionSip() {
//
//        if (controladorPBX != null)
//            controladorPBX.cerrarConexionSip();
//
//    }

    /**
     * **********
     * CHAT_CREATE  PARSE
     * **************
     */

    private void iniciarChat() {
        //Parse.initialize(this, "ZLMLOJIHnzjBSWaqjVyD5P40eE93pgBkBs21BfML", "2E9ygU0jZX2pjGpwNmPIUH6CQeuUtfe3mfo9g6KS"); // demoITY
//        Parse.initialize(this, "3AP4fcLfIdECpxm9oQ4TUL4LrioFoYaClQPU1vpZ", "Ts5sIDdkuSjy1NwUA6pXPwEbjZ7P1gbyvlAu29R4"); // italkyouMovil
    }

    /**
     * ***********
     * NOTIFIACIONES
     * ***********************
     */
//
//    private void notificarLlamada(String message, String action) {
//
//        Intent broadIntent = new Intent(action);
//        broadIntent.putExtra("description", message);
//        sendBroadcast(broadIntent);
//
//    }


    //Renzo Santillan
    public String getAnyNumber() {
        return anyNumber;
    }

    public void setAnyNumber(String anyNumber) {
        this.anyNumber = anyNumber;
    }

    public String getAnyAnnex() {
        return anyAnnex;
    }

    public void setAnyAnnex(String anyAnnex) {
        this.anyAnnex = anyAnnex;
    }

    public boolean isChatEnable() {
        return isChatEnable;
    }

    public void setIsChatEnable(boolean isChatEnable) {
        this.isChatEnable = isChatEnable;
    }

    public boolean isSipEnabled() {
        return isSipEnabled;
    }

    public void setIsSipEnabled(boolean isSipEnabled) {
        this.isSipEnabled = isSipEnabled;
    }


    public BeanContact getCurrentContact() {
        return currentContact;
    }

    public void setCurrentContact(BeanContact currentContact) {
        this.currentContact = currentContact;
    }
}
