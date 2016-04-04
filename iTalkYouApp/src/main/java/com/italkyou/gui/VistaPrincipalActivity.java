package com.italkyou.gui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.gui.chat.ChatMensajeActivity;
import com.italkyou.sip.SIPServiceCommunicator;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class VistaPrincipalActivity extends BaseActivity {

    private static final String TAG = VistaPrincipalActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    /**
     * ***
     */
    private BeanUsuario usuario;
    private AppiTalkYou app;
    public static VistaPrincipalActivity instance = null;
    private boolean backPressedToExitOnce;
    private static Context _context;

    //Communicator service
    SIPServiceCommunicator communicator = new SIPServiceCommunicator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        iniciarParse();
        instance = this;
        _context = getApplicationContext();
        setContentView(R.layout.contenedor_principal);
        this.tipoMenu = Const.MENU_GENERAL;
        app = (AppiTalkYou) getApplication();
        usuario = app.getUsuario();


//        registrarUsuarioChat();
        getChatUser();

        cargarFragmentoPrincipal();


        //Se registra para chat personal
        registerPersonalChannel();

        //Inicio el servicio de Italkyou
//        initServiceItalkyou(getApplicationContext());

        //Config Balance
//        configBalance();

        //UpdateFlag
        updatedChatStatus(true);


        //Sip
        registerSIP();


    }

    public void registerSIP() {
        Log.e(Const.DEBUG, "VistaPrincipalActivity registerSIP");
        communicator.registerSIP(app.getUsuario(), getApplicationContext());
    }


    private void getChatUser() {

        if (app.getUsuarioChat() == null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
            query.whereEqualTo(ChatITY.USER_ANNEX, usuario.getAnexo());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject currentChatUser, ParseException e) {
                    if (e == null) {
                        app.setUsuarioChat(currentChatUser);
                    } else {
                        app.setUsuarioChat(null);
                    }
                }
            });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app == null)
            app = ((AppiTalkYou) getApplication());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Sip
        registerSIP();
        communicator.doBindService(this);
        String activity = getIntent().getExtras().getString(Const.FROM_ACTIVITY);

        if (activity != null)
            if (activity.equals(ChatMensajeActivity.class.getSimpleName()))
                cargarFragmentoPrincipal();
    }


    private void cargarFragmentoPrincipal() {
        String screen = null;
        String index;

        try {
            screen = getIntent().getExtras().getString(Const.DATOS_TIPO);
        } catch (Exception x) {

        }


        if (screen == null)
            index = Const.indice_0;
        else {
            if (screen.equals(Const.PANTALLA_CONTACTO))
                index = Const.indice_1;
            else if (screen.equals(Const.PANTALLA_CONTACTO_SMS))
                index = Const.indice_3;
            else {
                index = Const.indice_0;
            }
        }


        Fragment fragmento = PrincipalFragment.nuevaInstancia(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();

    }


    public static void setPantalla(String nombre) {
//        pantalla = nombre;
    }


    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            super.onBackPressed();
        } else {
            this.backPressedToExitOnce = true;
            Toast.makeText(getApplicationContext(), R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 3000);
        }
    }


    private void registerPersonalChannel() {
        //Personal channel
        ParsePush.subscribeInBackground(Const.TAG_MY_CHANNEL + app.getUsuario().getAnexo(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(Const.DEBUG_PUSH, "Personal channel correct.");
            }
        });
    }


}
