package com.italkyou.sip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.gui.llamada.IncomingCallActivity;
import com.italkyou.utils.Const;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.Reason;
import org.linphone.core.SubscriptionState;

import java.nio.ByteBuffer;


/**
 * Created by rsantillanc on 18/11/2015.
 */
public class SIPManager implements LinphoneCoreListener {

    static SIPManager mSIPManager;
    LinphoneCore core;
    LinphoneProxyConfig proxyConfig;
    LinphoneCall call = null;
    SIPListener sipListener;
    private Context _context = null;


    public static SIPManager newInstance() {
        if (mSIPManager == null)
            mSIPManager = new SIPManager();

        return mSIPManager;
    }


    public void connectSIP(BeanUsuario user, Context c) {

        if (!((AppiTalkYou) c.getApplicationContext()).isSipEnabled()) {
            try {

                this._context = c;

//                final String linphoneInitialConfigFile = FileHelper.getLinphoneInitialConfigFile();
//                final String rootCaFile = FileHelper.getRootCaFileName();
//                final String zrtpSecretsCacheFile = FileHelper.getZrtpSecretsCacheFileName();
//                final String ringbackSoundFile = FileHelper.getRingbackSoundFile();
//                final String pauseSoundFile = FileHelper.getPauseSoundFile();
//
//
//
//                if (AppUtil.isNullOrEmpty(linphoneInitialConfigFile)) {
//                    Log.e("SIP_Manager","Error: linphoneInitialConfigFile not set");
//                    return;
//                }
//
//                if (AppUtil.isNullOrEmpty(rootCaFile)) {
//                    Log.e("SIP_Manager","Error: rootCaFile not set");
//                    return;
//                }
//
//                if (AppUtil.isNullOrEmpty(zrtpSecretsCacheFile)) {
//                    Log.e("SIP_Manager","Error: zrtpSecretsCacheFile not set");
//                    return;
//                }
//
//                if (AppUtil.isNullOrEmpty(pauseSoundFile)) {
//                    Log.e("SIP_Manager","Error: pauseSoundFile not set");
//                    return;
//                }


                //Core & address
                core = LinphoneCoreFactory.instance().createLinphoneCore(SIPManager.this, c);
                LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress("sip:" + user.getAnexo() + "@sip.italkyou.com");
                address.setDisplayName(user.getNombres());

                // Use TLS for registration with random port
                final LinphoneCore.Transports transports = new LinphoneCore.Transports();
                transports.udp = (int) (Math.random() * (65535 - 1024)) + 1024;
                transports.tcp = 0;
                transports.tls = 0;
                core.setSignalingTransportPorts(transports);

                //Proxy configuration
                proxyConfig = core.createProxyConfig();
                proxyConfig.setIdentity(address.asStringUriOnly());
                proxyConfig.setProxy(address.getDomain());
                proxyConfig.enableRegister(true);
                proxyConfig.setRealm(address.getDomain());

                //Adding
                core.addAuthInfo(LinphoneCoreFactory.instance().createAuthInfo(address.getUserName(), user.getPin_Sip(), null, null));
                core.addProxyConfig(proxyConfig);
                core.setDefaultProxyConfig(proxyConfig);


//                // set audio port range
//                core.setAudioPortRange(6000, 8000);
//
//                //Root CA File
//                core.setRootCA(rootCaFile);
//
//                // enable zrtp
//                core.setMediaEncryption(LinphoneCore.MediaEncryption.ZRTP);
//                core.setZrtpSecretsCache(zrtpSecretsCacheFile);
//                core.setMediaEncryptionMandatory(true);
//
//
//                // set sound files
//                core.setRingback(ringbackSoundFile);
//                core.setPlayFile(pauseSoundFile);
//
//
//                // enable echo cancellation
//                core.enableEchoCancellation(true);
//                core.enableEchoLimiter(false);


                // set number of threads for MediaStreamer
                int cpuCount = Runtime.getRuntime().availableProcessors();
                core.setCpuCount(cpuCount);

                //Iteration shut sip register
                iteratorCore();

                LinphoneCoreFactory.instance().setDebugMode(true, "DEBUG");

                Log.e(Const.DEBUG, "init success! ");
            } catch (LinphoneCoreException e) {
                Log.e(Const.DEBUG, "init error!", e);
            }

//            catch (FileHelper.NotInitedException e) {
//                Log.e(Const.DEBUG, "init NotInitedException!", e);
//            }
        }
    }


    private void iteratorCore() {
        core.iterate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iteratorCore();
            }
        }, 20);
    }

    public void refresh() {
        if (core != null)
            core.refreshRegisters();
    }

    public void unregister() {
        LinphoneProxyConfig config = core.getDefaultProxyConfig();
        config.edit();
        config.enableRegister(false);
        config.done();
    }


    public void call(String uri) {
        try {
            call = core.invite(uri);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }


    private LinphoneCall getCurrentCall() {
        return core.getCalls()[0];
    }

    public void decline() {
        core.declineCall(getCurrentCall(), Reason.Declined);
    }

    public void accept() {

        try {
            core.acceptCall(getCurrentCall());
        } catch (LinphoneCoreException e) {
            Log.e(Const.DEBUG, "acceptCall error!", e);
        }
    }

    public void hangout() {
        core.terminateAllCalls();
    }

    public void speaker(boolean on) {
        core.enableSpeaker(on);
    }

    public void speakerRinging() {
        if (!core.isSpeakerEnabled()) {
            core.enableSpeaker(true);
        }
    }
    public void speakerInitCall() {
        if (core.isSpeakerEnabled()) {
            core.enableSpeaker(false);
        }
    }
    public LinphoneAddress buildAddress(String numberToCall) {
        numberToCall = numberToCall.replace(Const.CADENA_PREFIJO,Const.CARACTER_VACIO);
        numberToCall = numberToCall.replace(Const.ESPACIO_BLANCO,Const.CARACTER_VACIO);

        try {
            if (numberToCall.length() > 6)
                return LinphoneCoreFactory.instance().createLinphoneAddress("sip:" + Const.CALL_PHONE_CODE + numberToCall + "@sip.italkyou.com");
            else
                return LinphoneCoreFactory.instance().createLinphoneAddress("sip:" + Const.CALL_ANNEX_CODE + numberToCall + "@sip.italkyou.com");
        } catch (LinphoneCoreException e) {
            return null;
        }
    }


//    SIP LISTENER

    public SIPListener getSipListener() {
        return sipListener;
    }

    public void setSipListener(SIPListener sipListener) {
        this.sipListener = sipListener;
    }


//    ------------------- REGISTERING LISTENER CORE -------------------------


    @Override
    public void authInfoRequested(LinphoneCore linphoneCore, String s, String s1, String s2) {

    }

    @Override
    public void callStatsUpdated(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCallStats linphoneCallStats) {

    }

    @Override
    public void newSubscriptionRequest(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend, String s) {

    }

    @Override
    public void notifyPresenceReceived(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend) {

    }

    @Override
    public void dtmfReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, int i) {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneAddress linphoneAddress, byte[] bytes) {

    }

    @Override
    public void transferState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state) {

    }

    @Override
    public void infoReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneInfoMessage linphoneInfoMessage) {

    }

    @Override
    public void subscriptionStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, SubscriptionState subscriptionState) {

    }

    @Override
    public void publishStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, PublishState publishState) {

    }

    @Override
    public void show(LinphoneCore linphoneCore) {

    }

    @Override
    public void displayStatus(LinphoneCore linphoneCore, String s) {

    }

    @Override
    public void displayMessage(LinphoneCore linphoneCore, String s) {

    }

    @Override
    public void displayWarning(LinphoneCore linphoneCore, String s) {

    }

    @Override
    public void fileTransferProgressIndication(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, int i) {

    }

    @Override
    public void fileTransferRecv(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, byte[] bytes, int i) {

    }

    @Override
    public int fileTransferSend(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, ByteBuffer byteBuffer, int i) {
        return 0;
    }

    @Override
    public void globalState(LinphoneCore linphoneCore, LinphoneCore.GlobalState globalState, String s) {

    }

    @Override
    public void registrationState(LinphoneCore linphoneCore, LinphoneProxyConfig linphoneProxyConfig, LinphoneCore.RegistrationState registrationState, String s) {
        Log.e(Const.DEBUG, "registrationState registrationState-> " + registrationState.toString());
        Log.e(Const.DEBUG, "registrationState registrationState message-> " + s);


        if (LinphoneCore.RegistrationState.RegistrationOk.equals(registrationState)) {
            Log.e(Const.DEBUG, "registrationState setIsSipEnabled-> " + true);
            ((AppiTalkYou) _context).setIsSipEnabled(true);
        } else {
            ((AppiTalkYou) _context).setIsSipEnabled(false);
            Log.e(Const.DEBUG, "registrationState setIsSipEnabled-> " + false);
        }
        sipListener.onRegistrationChange(registrationState);
    }

    @Override
    public void configuringStatus(LinphoneCore linphoneCore, LinphoneCore.RemoteProvisioningState remoteProvisioningState, String s) {

    }

    @Override
    public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage) {

    }

    @Override
    public void callState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s) {
//        Log.e(Const.DEBUG,
//                "callState \naddress getRemoteAddress-> " + linphoneCall.getRemoteAddress()
//                        + "\n State -> " + state.toString()
//                        + "\n Message -> " + s
//                        + "\n Display name -> " + linphoneCall.getRemoteAddress().getDisplayName());

        if (LinphoneCall.State.IncomingReceived.equals(state)) {
            Log.e(Const.DEBUG, "callState IncomingReceived -> " + state);
            Bundle bundle = new Bundle();
            bundle.putString("address.displayName", linphoneCall.getRemoteAddress().getDisplayName());
            bundle.putString("address.userName", linphoneCall.getRemoteAddress().getUserName());
            bundle.putString("reason.message", s);

            this._context.startActivity(new Intent(_context, IncomingCallActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtras(bundle));
            Log.e(Const.DEBUG, "Launch activity -> " + state);

        } else if (LinphoneCall.State.CallEnd.equals(state) || LinphoneCall.State.CallReleased.equals(state)) {
            sipListener.onCallStatusChanged(state);
            Log.e(Const.DEBUG, "callState onCallStatusChanged -> " + state);
        }

    }

    @Override
    public void callEncryptionChanged(LinphoneCore linphoneCore, LinphoneCall linphoneCall, boolean b, String s) {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, String s, LinphoneContent linphoneContent) {

    }

    @Override
    public void isComposingReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom) {

    }

    @Override
    public void ecCalibrationStatus(LinphoneCore linphoneCore, LinphoneCore.EcCalibratorStatus ecCalibratorStatus, int i, Object o) {

    }

    @Override
    public void uploadProgressIndication(LinphoneCore linphoneCore, int i, int i1) {

    }

    @Override
    public void uploadStateChanged(LinphoneCore linphoneCore, LinphoneCore.LogCollectionUploadState logCollectionUploadState, String s) {

    }



}