package com.italkyou.gui.llamada;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.salidas.SalidaHistorialLlamadas;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.sip.SIPManager;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.MenuColorizer;
import com.italkyou.utils.TelephoneUtil;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

//import android.telecom.Call;

public class DialActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int TIEMPO_VIBRACION = 50;
    private static final String TAG = DialActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private ImageButton btnCero;
    private ImageButton btnUno;
    private ImageButton btnDos;
    private ImageButton btnTres;
    private ImageView btnCuatro;
    private ImageView btnCinco;
    private ImageView btnSeis;
    private ImageView btnSiete;
    private ImageView btnOcho;
    private ImageView btnNueve;
    private Button btnNumeral;
    private Button btnAsterisco;
    private ImageButton btnBack;

    private TextView tvNumero;
    private TextView tvSaldoAnexo;

    private ImageButton btnHistorial;
    private ImageButton btnLlamar;
    private String numero = Const.cad_vacia;

    private ActionBar actionbar;
    private AppiTalkYou app;
    private Context _context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        _context = getApplicationContext();
        app = (AppiTalkYou) getApplication();
        initViews();
        setUpActionBar();

    }


    private void setUpActionBar() {

        actionbar = getSupportActionBar();
        //config
//        configBalance();
    }

    private void initViews() {

        btnCero = (ImageButton) findViewById(R.id.num_0);
        btnCero.setOnClickListener(this);
        btnUno = (ImageButton) findViewById(R.id.num_1);
        btnUno.setOnClickListener(this);
        btnDos = (ImageButton) findViewById(R.id.num_2);
        btnDos.setOnClickListener(this);
        btnTres = (ImageButton) findViewById(R.id.num_3);
        btnTres.setOnClickListener(this);
        btnCuatro = (ImageView) findViewById(R.id.num_4);
        btnCuatro.setOnClickListener(this);
        btnCinco = (ImageView) findViewById(R.id.num_5);
        btnCinco.setOnClickListener(this);
        btnSeis = (ImageView) findViewById(R.id.num_6);
        btnSeis.setOnClickListener(this);
        btnSiete = (ImageView) findViewById(R.id.num_7);
        btnSiete.setOnClickListener(this);
        btnOcho = (ImageView) findViewById(R.id.num_8);
        btnOcho.setOnClickListener(this);
        btnNueve = (ImageView) findViewById(R.id.num_9);
        btnNueve.setOnClickListener(this);
        btnBack = (ImageButton) findViewById(R.id.back);
        btnBack.setOnClickListener(this);
        btnBack.setOnLongClickListener(this);
        btnAsterisco = (Button) findViewById(R.id.num_ast);
        btnAsterisco.setOnClickListener(this);
        btnNumeral = (Button) findViewById(R.id.boton_numeral);
        btnNumeral.setOnClickListener(this);

        btnLlamar = (ImageButton) findViewById(R.id.btn_realizar_llamada);
        btnLlamar.setOnClickListener(this);
        btnHistorial = (ImageButton) findViewById(R.id.btnHistorialLlamadas);
        btnHistorial.setOnClickListener(this);

//        tvSaldoAnexo = (TextView) findViewById(R.id.tvSaldoLlamadas);
        tvNumero = (TextView) findViewById(R.id.tvNumeroMarcado);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_dial, menu);
        int color = getResources().getColor(R.color.White);
        int alpha = 255; //100%
        MenuColorizer.colorMenu(this, menu, color, alpha);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.acc_dial_info) {
            Intent i = new Intent(getApplicationContext(), HowToCallActivity.class);
            startActivity(i);
        } else {
            LogicaPantalla.personalizarIntentVistaPrincipal(DialActivity.this, Const.PANTALLA_CONTACTO, DialActivity.class.getSimpleName());
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        tvNumero.setCursorVisible(true);
        tvNumero.setSelected(true);
        tvNumero.forceLayout();
        tvNumero.moveCursorToVisibleOffset();

        switch (v.getId()) {

            case R.id.num_0:
                numero = tvNumero.getText().toString().trim() + "0";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_1:
//                numero += btnUno.getText().toString();
                numero = tvNumero.getText().toString().trim() + "1";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_2:
                numero = tvNumero.getText().toString().trim() + "2";
//                numero += btnDos.getText().toString();
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_3:
                numero = tvNumero.getText().toString().trim() + "3";
//                numero += btnTres.getText().toString();
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_4:
                numero = tvNumero.getText().toString().trim() + "4";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_5:
                numero = tvNumero.getText().toString().trim() + "5";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_6:
                numero = tvNumero.getText().toString().trim() + "6";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_7:
                numero = tvNumero.getText().toString().trim() + "7";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_8:
                numero = tvNumero.getText().toString().trim() + "8";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.num_9:
                numero = tvNumero.getText().toString().trim() + "9";
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.btn_realizar_llamada:
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);

                String balance = app.getSaldo();
                Double dBalance;

                if (balance == null) {
                    dBalance = LogicaUsuario.getLocalBalance(getApplicationContext(), app.getUsuario().getID_Usuario());
                } else {
                    dBalance = Double.parseDouble(balance);
                }

                if (dBalance > 0) {
                    realizarLlamada();
                } else if (dBalance <= 0 && numero.length() == 6) {
                    realizarLlamada();
                } else {
                    String message = getString(R.string.message_alert_balance_none);
                    CustomAlertDialog.showSingleAlert(this, message);
                }


                break;

            case R.id.btnHistorialLlamadas:
                obtenerHistorialLlamadas();
                break;

            case R.id.num_ast:
                numero = tvNumero.getText().toString().trim() + btnAsterisco.getText().toString();
                tvNumero.setText(numero);
                break;

            case R.id.boton_numeral:
                numero = tvNumero.getText().toString().trim() + btnNumeral.getText().toString();
                tvNumero.setText(numero);
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            case R.id.back:

                if (!numero.equals(Const.cad_vacia)) {
                    numero = numero.substring(0, numero.length() - 1);

                    if (numero.equals(Const.cad_vacia)) {
                        tvNumero.setText("");
//                        tvNumero.setTextColor(getActivity().getResources().getColor(R.color.Gray));
                    } else {
                        tvNumero.setText(numero);
                    }
                }
                TelephoneUtil.realizarVibracionTelefono(getApplicationContext().getApplicationContext(), TIEMPO_VIBRACION);
                break;

            default:

                break;
        }
    }

    private void obtenerHistorialLlamadas() {
        final ProgressDialog pd = ProgressDialog.show(this, Const.TITULO_APP,
                getString(R.string.msj_obtener_historial_Llamadas), true, true);

        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        AppiTalkYou app = (AppiTalkYou) getApplicationContext().getApplicationContext();

        if (AppUtil.existeConexionInternet(getApplicationContext())) {
            ExcecuteRequest ejecutar = new ExcecuteRequest(new ExcecuteRequest.ResultadoOperacionListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                    List<Object> lista = new ArrayList<>();
                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        lista = (List<Object>) respuesta.getObjeto();
                    }

                    obtenerHistorialMensajesVoz(pd, lista);
                }
            });

            ejecutar.obtenerHistorialLlamadas(app.getUsuario().getID_Idioma(), app.getUsuario().getAnexo());

        } else {
            pd.dismiss();
            Crouton.showText(this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }

    private void obtenerHistorialMensajesVoz(final ProgressDialog pd, final List<Object> listaLlamadas) {

        AppiTalkYou app = (AppiTalkYou) getApplicationContext();
        if (AppUtil.existeConexionInternet(getApplicationContext())) {
            ExcecuteRequest ejecutar = new ExcecuteRequest(new ExcecuteRequest.ResultadoOperacionListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                    List<Object> listaVoz = new ArrayList<>();

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        listaVoz = (List<Object>) respuesta.getObjeto();
                    }

                    SalidaHistorialLlamadas historial = new SalidaHistorialLlamadas();
                    historial.setListaLlamadas(listaLlamadas);
                    historial.setListaMensajesVoz(listaVoz);

                    pd.dismiss();

                    LogicaPantalla.personalizarIntentHistorialLlamadas(DialActivity.this, historial);
                }
            });
            ejecutar.obtenerHistorialMensajesVoz(app.getUsuario().getID_Idioma(), app.getUsuario().getAnexo());

        } else {

            pd.dismiss();
            SalidaHistorialLlamadas historial = new SalidaHistorialLlamadas();
            Crouton.showText(this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
            LogicaPantalla.personalizarIntentHistorialLlamadas(this, historial);

        }
    }

    private void realizarLlamada() {

        numero = tvNumero.getText().toString().trim();
        numero = numero.replace(" ", "");
        numero = numero.replace("+", "");

        if (numero.length() == 6) {
            LogicaPantalla.personalizarIntentRealizarLlamada(SIPManager.newInstance().buildAddress(numero).asStringUriOnly(), this, Const.tipo_llamada_anexoVOIP, numero, null, "0", Const.cad_vacia);

        } else if (numero.length() > 6) {
            LogicaPantalla.personalizarIntentRealizarLlamada(SIPManager.newInstance().buildAddress(numero).asStringUriOnly(), this, Const.tipo_llamada_internacional, numero, null, "0", Const.cad_vacia);

        } else {
            Toast.makeText(getApplicationContext(), R.string.incorrect_number, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {

        tvNumero.setText(Const.cad_vacia);
        numero = Const.cad_vacia;
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogicaPantalla.personalizarIntentVistaPrincipal(DialActivity.this, Const.PANTALLA_CONTACTO, DialActivity.class.getSimpleName());
    }

}

