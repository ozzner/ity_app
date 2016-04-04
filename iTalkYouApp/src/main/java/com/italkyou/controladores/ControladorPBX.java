package com.italkyou.controladores;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.net.sip.SipSession;
import android.util.Log;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanAnexoSip;
import com.italkyou.beans.BeanConfiguracionPBX;
import com.italkyou.controladores.interfaces.OnEventosLlamadasPBX;
import com.italkyou.utils.Const;

public class ControladorPBX {

    private static final String TAG = ControladorPBX.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private Context contextoApp = null;
    private Intent intentLlamadaEntrante = null;
    private BeanConfiguracionPBX cfgSrvCom = null;
    private SipSession mSipSession = null;
    private SipManager sipManager = null;
    private SipProfile mSipProfile = null;
    private SipAudioCall mAudioCall = null;
    private SipAudioCall.Listener mAudioCallListener = null;
    //    private SipRegistrationListener mRegistrationListener = null;
    private OnEventosLlamadasPBX interfaceLlamadaPBX;
    private static BeanAnexoSip configAnexo;
    private PendingIntent pendingIntent;
    private Intent mIncomingCallIntent;

    private static final String ACCION_LLAMADA_ENTRANTE = "android.SipDemo.INCOMING_CALL";
    private static final int TIEMPO_REGISTRO = 60 * 60 * 12; /* Tiempo de Registro 1 dia */
    private static String CARACTER_ARROBA = "@";
    private AppiTalkYou app;

    public ControladorPBX(Context contexto, BeanConfiguracionPBX cfgSrvComunicacion) {

        this.contextoApp = contexto;
        this.cfgSrvCom = cfgSrvComunicacion;

        if (sipManager == null)
            sipManager = SipManager.newInstance(contextoApp);
    }

    /**
     * Metodo para verificar, la existencia de un perfil sip.
     *
     * @return flag, true: si hay perfil, false en caso no lo haya.
     */

    private boolean checkProfile() {

        if (mSipProfile == null)
            return false;
        else
            return true;
    }

    /**
     * Metodo para crear el perfil de un anexo
     *
     * @param config, son los datos del anexo
     * @return el SipProfile con los datos de la sesion.
     */


    private SipProfile createSipProfile(BeanAnexoSip config) {

        configAnexo = config;
        SipProfile.Builder builder;

        try {

            builder = new SipProfile.Builder(config.getNroAnexo(), cfgSrvCom.getIpSvrComunicacion());
//            builder.setPort(cfgSrvCom.getPuerto());
//            builder.setProtocol(cfgSrvCom.getProtocolo());
            builder.setPassword(config.getClaveAnexo());
//            builder.setAuthUserName(config.getNroAnexo());//Edited
//            builder.setSendKeepAlive(true);
//            builder.setAutoRegistration(false);
//            builder.setProfileName(config.getNroAnexo() + CARACTER_ARROBA + cfgSrvCom.getIpSvrComunicacion());

            mSipProfile = builder.build();
        } catch (java.text.ParseException e) {
            Log.e(Const.DEBUG_SIP, TAG + "sipManager failed creating profile. ", e);
            mSipProfile = null;
        }

        return mSipProfile;
    }

    /**
     * Metodo para registrar un anexo con la central de comunicacion.
     */

    public boolean registerAnnex(BeanAnexoSip configAnexo, OnEventosLlamadasPBX interfacePBX) {
        boolean registroValido;

        //Set Interface.
        setInterfaceLlamadaPBX(interfacePBX);

        //Se verifica que se tiene soporte a la API y VOIP en el dispositivo
        if (SipManager.isVoipSupported(contextoApp) && SipManager.isApiSupported(contextoApp)) {
//        if (SipManager.isApiSupported(contextoApp)) {

            Log.e(Const.DEBUG_SIP, TAG + "isVoipSupported: " + SipManager.isVoipSupported(contextoApp));
            Log.e(Const.DEBUG_SIP, TAG + "isApiSupported: " + SipManager.isApiSupported(contextoApp));
            Log.e(Const.DEBUG_SIP, TAG + "isSipWifiOnly: " + SipManager.isSipWifiOnly(contextoApp));

            try {

                if (!checkProfile())
                    mSipProfile = createSipProfile(configAnexo);

                if (sipManager == null)
                    sipManager = SipManager.newInstance((contextoApp));

                //Init intent pending
                inicializeIntentIncomingCall();

                //Check and set true if is correct.
                registroValido = sipManager.isRegistered(mSipProfile.getUriString());
            } catch (ParseException pe) {
                Log.e(Const.DEBUG_SIP, TAG + "SIP Connection ParseError. " + pe.getMessage());
                registroValido = false;
            } catch (Exception x) {
                Log.e(Const.DEBUG_SIP, TAG + "SIP Exception ", x);
                registroValido = false;
            }

            Log.e(Const.DEBUG_SIP, TAG + "FINAL Registrando Anexo");
            return registroValido;

        } else {

            Log.e(Const.DEBUG_SIP, TAG + "SIP no es soportado :S ");
            Log.e(Const.DEBUG_SIP, TAG + "isVoipSupported: " + SipManager.isVoipSupported(contextoApp));
            Log.e(Const.DEBUG_SIP, TAG + "isApiSupported: " + SipManager.isApiSupported(contextoApp));
            Log.e(Const.DEBUG_SIP, TAG + "isSipWifiOnly: " + SipManager.isSipWifiOnly(contextoApp));
            Log.e(Const.DEBUG_SIP, TAG + "Error Registrando Anexo");
            return false;
        }


    }

    private void openMySipSession() {
        openLocalProfile();
//        openPeerProfile();
    }


    /**
     * Inicializa los valores para lanzar un intent cuando una llamada es recibida.
     * Ademas este metodo apertura el perfil local.
     */
    void inicializeIntentIncomingCall() {
        mIncomingCallIntent = new Intent();
        mIncomingCallIntent.setAction(ACCION_LLAMADA_ENTRANTE);
        pendingIntent = PendingIntent.getBroadcast(contextoApp, 0, mIncomingCallIntent, Intent.FILL_IN_DATA);
        openMySipSession();
    }


    /**
     * Abre el perfil local para recibir llamadas.
     */
    void openLocalProfile() {
        if (sipManager == null)
            sipManager = SipManager.newInstance(contextoApp);

        try {
            if (mSipProfile != null) {
                sipManager.open(mSipProfile, pendingIntent, new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String localProfileUri) {
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistering localProfileUri: " + localProfileUri);

                    }

                    @Override
                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistrationDone localProfileUri: " + localProfileUri);
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistrationDone expiryTime: " + expiryTime);
                        openPeerProfile();
                    }

                    @Override
                    public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistrationFailed localProfileUri: " + localProfileUri);
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistrationFailed errorCode: " + errorCode);
                        Log.e(Const.DEBUG_SIP, TAG + "openLocalProfile onRegistrationFailed errorMessage: " + errorMessage);
                    }
                });
            } else
                Log.e(Const.DEBUG_SIP, TAG + "Sip local open Error mSipProfile is null!");

        } catch (Exception ex) {
            Log.e(Const.DEBUG_SIP, TAG + "Sip local open error", ex);
        }
    }


    /**
     * Cierra el perfil local para No recibir llamadas.
     */
    public void closeLocalProfile() {
        if (sipManager == null)
            sipManager = SipManager.newInstance(contextoApp);

        try {

            if (mSipProfile != null) {
                sipManager.close(mSipProfile.getUriString());
                Log.e(Const.DEBUG_SIP, TAG + "Sip local close OK.");
            } else
                Log.e(Const.DEBUG_SIP, TAG + "Sip local close error mSipProfile is null!");

        } catch (Exception ex) {
            Log.e(Const.DEBUG_SIP, TAG + "Sip local open error", ex);
        }
    }


    /**
     * Abre perfil peer en el servidor SIP para recibir llamadas si autoregistration no está activada.
     */
    void openPeerProfile() {
        if (sipManager == null)
            return;

        try {
            if (mSipProfile != null) {
                sipManager.register(mSipProfile, TIEMPO_REGISTRO, new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String localProfileUri) {
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistering localProfileUri: " + localProfileUri);

                    }

                    @Override
                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistrationDone localProfileUri: " + localProfileUri);
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistrationDone expiryTime: " + expiryTime);
                    }

                    @Override
                    public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistrationFailed localProfileUri: " + localProfileUri);
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistrationFailed errorCode: " + errorCode);
                        Log.e(Const.DEBUG_SIP, TAG + "openPeerProfile onRegistrationFailed errorMessage: " + errorMessage);

                    }
                });

            } else {
                Log.e(Const.DEBUG_SIP, TAG + "Sip peer profile error mSipProfile is null!");
            }

        } catch (Exception ex) {
//            sipManager.unregister(mSipProfile,mRegistrationListener);
            Log.e(Const.DEBUG_SIP, TAG + "Sip peer profile open error", ex);
        }
    }

    /**
     * Cierra perfil peer en el servidor SIP para recibir llamadas. Este o no activado el autoregistro.
     */
    public void closePeerProfile() {
        if (sipManager == null)
            sipManager = SipManager.newInstance(contextoApp);


        try {

            if (mSipProfile != null) {
                sipManager.unregister(mSipProfile, new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String localProfileUri) {
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistering localProfileUri: " + localProfileUri);
                    }

                    @Override
                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistrationDone localProfileUri: " + localProfileUri);
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistrationDone expiryTime: " +expiryTime );
                        closeLocalProfile();
                    }

                    @Override
                    public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistrationFailed localProfileUri: " +localProfileUri );
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistrationFailed errorCode: " +errorCode );
                        Log.e(Const.DEBUG_SIP, TAG + "closePeerProfile onRegistrationFailed errorMessage: " +errorMessage );
                        closeLocalProfile();
                    }
                });
            } else
                Log.e(Const.DEBUG_SIP, TAG + "Sip peer profile Error mSipProfile is null!");

        } catch (Exception ex) {
            Log.e(Const.DEBUG_SIP, TAG + "Sip peer profile close error", ex);
        }




//        SipSession session;
//        try {
//            session = sipManager.createSipSession(mSipProfile, new SipSession.Listener());
//            Log.e(Const.DEBUG_SIP, TAG + "session: " + session.getPeerProfile());
//            Log.e(Const.DEBUG_SIP, TAG + "session: " + session.getLocalProfile());
//            Log.e(Const.DEBUG_SIP, TAG + "session: " + session.getState());
//            session.unregister();
//
//            Log.e(Const.DEBUG_SIP, TAG + "Sip closePeerProfile correct. ");
//
//        } catch (SipException e) {
//            Log.e(Const.DEBUG_SIP, TAG + "Sip closePeerProfile error", e);
//        }
    }

    /**
     * Cierra la session en el proveedor sip
     */
    public void closeSessionSip() {
        try {
//            closeLocalProfile();
            closePeerProfile();

            if (sipManager.isRegistered(mSipProfile.getUriString())) {
            }
        } catch (SipException e) {
            Log.e(Const.DEBUG_SIP, TAG + "Error al cerrar SIP. ", e);

        }
    }


//    public boolean verificarRegistroAnexo(BeanAnexoSip anexoSip, OnEventosLlamadasPBX listener) {
//
//        boolean flagRegistroAnexo = false;
//
//        try {
//
//            if (mSipProfile == null)
//                mSipProfile = createSipProfile(anexoSip);
//
//            flagRegistroAnexo = sipManager.isRegistered(mSipProfile.getUriString());
//        } catch (SipException e) {
//            Log.e(Const.DEBUG_SIP, TAG + "SipException:", e);
//            flagRegistroAnexo = false;
//        } catch (NullPointerException ex) {
//            flagRegistroAnexo = false;
//            Log.e(Const.DEBUG_SIP, TAG + "NullPointerException: ", ex);
//        } finally {
//
//            if (!flagRegistroAnexo) {
//                Log.e(Const.DEBUG_SIP, TAG + "Finally. Anexo desregistrado. Volviendo a registrar...");
//                flagRegistroAnexo = registerAnnex(anexoSip, listener);
//            }
//        }
//        return flagRegistroAnexo;
//    }
//

    /**
     * Metodo para realizar una llamada al anexo
     *
     * @param nroAnexo es el numero de anexo que se marcara.
     */
    public void makeAudioCall(String nroAnexo) {

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(false);
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    interfaceLlamadaPBX.llamadaColgadaUsuario();
                }

            };

            mAudioCall = sipManager.makeAudioCall(mSipProfile.getUriString(), nroAnexo + "@" + cfgSrvCom.getIpSvrComunicacion(), listener, 30);

        } catch (Exception e) {

            if (mSipProfile != null) {
                try {
                    sipManager.close(mSipProfile.getUriString());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

            if (mAudioCall != null) {
                mAudioCall.close();
            }
        }
    }

    public void colgarLlamada() {

        try {
            if (mAudioCall != null)
                mAudioCall.endCall();

        } catch (SipException e) {
            e.printStackTrace();

        } finally {
            if (mAudioCall != null)
                mAudioCall.close();
        }

    }

    public void activarAltavoz(boolean flagAltavoz) {
        if (mAudioCall != null)
            mAudioCall.setSpeakerMode(flagAltavoz);
    }

    public String recibirLlamada(Intent intent) {

        intentLlamadaEntrante = intent;
        String numeroEntrante = "";

        try {
            numeroEntrante = sipManager.getSessionFor(intent).getPeerProfile().getUserName();
        } catch (SipException e) {
            Log.e(Const.DEBUG_SIP, "Recibir llamada exception ", e);
        }

        setmAudioCallListener(new SipAudioCall.Listener() {

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {

                try {
                    getInterfaceLlamadaPBX().llamadaEntrante(caller.getUserName());
                    call.answerCall(30);
                } catch (Exception e) {
                    Log.e(Const.DEBUG_SIP, TAG + "SIP CallStatusReceiver" + e.getMessage());
                }
            }

            @Override
            public void onCallEstablished(SipAudioCall call) {
                super.onCallEstablished(call);
                Log.e(Const.DEBUG_SIP, TAG + "******* llamada establecida *******");
            }


            @Override
            public void onChanged(SipAudioCall llamada) {
                Log.e(Const.DEBUG_SIP, TAG + "******* OnChanged *******");

                if (llamada.getState() == 0) {
                    getInterfaceLlamadaPBX().llamadaColgadaUsuario();
                } else if (llamada.getState() == 8) {      //Llamada Establecida

                }
                super.onChanged(llamada);
            }

        });

        try {

            mAudioCall = sipManager.takeAudioCall(intent, getmAudioCallListener());

        } catch (SipException e) {
            e.printStackTrace();
        }
        return numeroEntrante;
    }

    public void contestarLlamada(OnEventosLlamadasPBX interfaceUsuario) {

        if (interfaceUsuario != null)
            setInterfaceLlamadaPBX(interfaceUsuario);

        try {
            mAudioCall = sipManager.takeAudioCall(intentLlamadaEntrante, getmAudioCallListener());
            mAudioCall.answerCall(30);
            mAudioCall.startAudio();
            mAudioCall.setSpeakerMode(false);
        } catch (SipException e) {
            e.printStackTrace();
        }


    }


//    public void cerrarConexionSip() {
//
//        try {
////            sipManager.close(mSipProfile.getUriString());
//            sipManager.unregister(mSipProfile, mRegistrationListener);
//        } catch (SipException e) {
//            e.printStackTrace();
//            Log.e(Const.DEBUG_SIP, TAG + "SIP Error Cerrando SipManager " + e.getMessage());
//        } finally {
//            sipManager = null;
//            mSipProfile = null;
//        }
//    }

    public OnEventosLlamadasPBX getInterfaceLlamadaPBX() {
        return interfaceLlamadaPBX;
    }

    public void setInterfaceLlamadaPBX(OnEventosLlamadasPBX interfaceLlamadaPBX) {
        this.interfaceLlamadaPBX = interfaceLlamadaPBX;
    }

    public SipAudioCall.Listener getmAudioCallListener() {
        return mAudioCallListener;
    }

    public void setmAudioCallListener(SipAudioCall.Listener mAudioCallListener) {
        this.mAudioCallListener = mAudioCallListener;
    }

}
