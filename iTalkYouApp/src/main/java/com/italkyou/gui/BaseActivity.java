package com.italkyou.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.salidas.SalidaHistorialSaldo;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.configuraciones.SipConfig;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.controladores.interfaces.OnEventosLlamadasPBX;
import com.italkyou.gui.Testing.ReportActivity;
import com.italkyou.gui.Testing.TestingActivity;
import com.italkyou.gui.menu.PerfilUsuarioActivity;
import com.italkyou.gui.menu.RedireccionarAnexoActivity;
import com.italkyou.gui.personalizado.DialogoCerrarSesion;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.ChatITY;
import com.italkyou.utils.Const;
import com.italkyou.utils.ExceptionHandler;
import com.italkyou.utils.StringUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final String DASH = "-";
    protected ProgressDialog pd;
    protected String tipoMenu;
    public static TextView tvEstadoAnexo;
    private static AppiTalkYou app;
    public static Activity mActivity;
    private BeanUsuario usuarioITY;
    protected OnEventosLlamadasPBX interfaceLlamada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initErrorReporting();
//        tvEstadoAnexo = (TextView) findViewById(R.id.tvEstadoAnexo);
        mActivity = this;
        app = (AppiTalkYou) getApplication();

        inicializarPantalla();
        getBalanceIty();
        updatedChatStatus(true);
    }

    private void initErrorReporting() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, ReportActivity.class));
    }


    public BaseActivity() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (tipoMenu.equals(Const.MENU_GENERAL))
            getMenuInflater().inflate(R.menu.mngeneral, menu);
        else if (tipoMenu.equals(Const.MENU_VACIA))
            getMenuInflater().inflate(R.menu.sinmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.itConfiguracion:
                LogicaPantalla.goToProfile(this, PerfilUsuarioActivity.class);
                break;

            case R.id.itCerrarSesion:
                cerrarSesion();
                break;

            case R.id.itSaldo:
                obtenerHistorialSaldo();
                break;

            case R.id.itRedireccionar:
                LogicaPantalla.personalizarIntentActivity(this, RedireccionarAnexoActivity.class);
                break;

            //Temp enabled
            case R.id.itTesting:
                launchTesting();
                break;

//	        case R.id.itAgendarSMS:
//	        	LogicaPantalla.personalizarIntentActivity(this, AgendarSMSActivity.class);

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }

    private void launchTesting() {
        Intent testing = new Intent(getApplicationContext(), TestingActivity.class);
        startActivity(testing);
    }

    /*Lifecycle*/
    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        updatedChatStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        updatedChatStatus(false);
    }


    protected void updatedChatStatus(boolean isOnline) {
        try {
            ParseObject po = app.getUsuarioChat();

            if (po == null)
                po = getCurrentUserChat(app.getUsuario().getAnexo());

            po.put(ChatITY.USER_STATUS, (isOnline) ? Const.status_connected : Const.status_no_connected);
            po.saveInBackground();
        } catch (NullPointerException np) {
            Log.e(TAG, "Error al actualizar status ", np);

        } catch (Exception ex) {
            Log.e(TAG, "Error al actualizar status ", ex);
        }

    }

    private ParseObject getCurrentUserChat(String annex) {
        try {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ChatITY.TABLE_USER);
            query.whereEqualTo(ChatITY.USER_ANNEX, annex);
            ParseObject usuarioChat = query.getFirst();
            app.setUsuarioChat(usuarioChat);

            return usuarioChat;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**************
     * Metodos Propios
     ****************/
    private void inicializarPantalla() {
        setContentView(R.layout.conversacion);
        usuarioITY = LogicaUsuario.obtenerUsuario(getApplicationContext());
        app.setUsuario(usuarioITY);
        mostrarBarraAcciones(usuarioITY.getAnexo());

    }

    private void obtenerHistorialSaldo() {

        if (AppUtil.existeConexionInternet(BaseActivity.this)) {

            pd = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msj_obtener_historial_saldo), true, true);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            app = (AppiTalkYou) getApplication();

            ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {
                @SuppressWarnings({"unchecked"})
                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    pd.dismiss();
                    SalidaHistorialSaldo saldo = new SalidaHistorialSaldo();

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        List<Object> listaMovimiento = (List<Object>) respuesta.getObjeto();
                        saldo.setListaMovimientos(listaMovimiento);
                    } else {
                        saldo.setSaldoActual(SipConfig.SALDO_DEFECTO);
                    }

                    LogicaPantalla.personalizarIntentMovimientos(BaseActivity.this, saldo);
                }
            });

            ejecutar.obtenerHistorialSaldo(app.getUsuario().getID_Idioma(), app.getUsuario().getAnexo(), app.getUsuario().getO_ck());

        } else {
            //pdDialogoEspera.dismiss();
            Crouton.showText(BaseActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private void cerrarSesion() {

        FragmentManager fm = getSupportFragmentManager();
        DialogoCerrarSesion dialogo = new DialogoCerrarSesion();
        dialogo.show(fm, "");

    }


    /**
     * Metodo para personalizar el Action Bar
     *
     * @param anexo es el numero de extension en el servidor de comunicaciones.
     */

    public void mostrarBarraAcciones(String anexo) {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(BaseActivity.this);
        View vBarraAcciones = mInflater.inflate(R.layout.barra_acciones, null);

        TextView tvAnexo = (TextView) vBarraAcciones.findViewById(R.id.tvAnexoUsuario);
        tvAnexo.setText(getString(R.string.bar_annex) + Const.ESPACIO_BLANCO + AppUtil.formatAnnex(anexo));

        tvEstadoAnexo = (TextView) vBarraAcciones.findViewById(R.id.tvEstadoAnexo);
        mActionBar.setCustomView(vBarraAcciones);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.italkyou_primary_blue)));

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatedChatStatus(true);
        getBalanceIty();
    }


    public void getBalanceIty() {
        String annex;
        try {
            annex = app.getUsuario().getAnexo();
        } catch (NullPointerException ExNull) {
            app = ((AppiTalkYou) getApplication());
            BeanUsuario user = LogicaUsuario.obtenerUsuario(getApplicationContext());
            app.setUsuario(user);
            annex = app.getUsuario().getAnexo();
        }

        final String finalAnnex = annex;
        ExecuteRequest ejecutar = new ExecuteRequest(new ExecuteRequest.ResultadoOperacionListener() {

            @Override
            public void onOperationDone(BeanRespuestaOperacion respuesta) {

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    final BeanSaldo saldo = (BeanSaldo) respuesta.getObjeto();

                    if (saldo.getResultado().equals(Const.RESULTADO_OK)) {
                        Log.e(TAG,"balance, Result_OK true");

                        app.setSaldo(saldo.getBalance());
                        LogicaUsuario.actualizarSaldo(mActivity, finalAnnex, saldo.getBalance());

                        //Show in actionbar
                        printBalance();
                    }else{
                        Log.e(TAG,"Error balance, Result_OK false");
                    }
                }else
                {
                    Log.e(TAG,"Error balance");
                }
            }
        });
        ejecutar.obtenerSaldo(finalAnnex);
    }

    public void printBalance() {
        String sBalance = StringUtil.format(Double.parseDouble(app.getSaldo()));
        tvEstadoAnexo.setText(sBalance + Const.TAG_CURRENCY);
        tvEstadoAnexo.invalidate();
        Log.e(Const.DEBUG_BALANCE, TAG + " Saldo--> " + sBalance);

    }

}
