package com.italkyou.gui.inicio;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.entradas.EntradaRegistarUsuario;
import com.italkyou.beans.salidas.SalidaPin;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicaPais;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseInicioActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.Testing.ReportActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.ExceptionHandler;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RegistroActivity extends BaseInicioActivity implements OnClickListener, OnItemSelectedListener, CustomAlertDialog.OnPositiveNegativeListener {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    private Spinner cbxPaises;
    private EditText etTelefono;
    private BeanPais country;
    private EditText etClave;
    private EditText etRegisterEmail;
    //    private EditText etRepetirClave;
    private EditText etNombreUsuario;
    private CheckedTextView chkTerminosCondiciones;
    private Button btnRegistrar;
    private TextView tvTerminosCondiciones;
    private TextView tvPrefijo;
    private String telefono;
    private String clave;
    private String nombre_usuario;
    private LinearLayout mLinearlnlyCombo;
    private EntradaRegistarUsuario entrada;
    private TextView tvTituloDialogo;
    private boolean isSimCardEnabled = false;
    private ImageView ivShowPassword;
    private boolean isShowingPassword = false;
    private TextView tvRestorePwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_usuario);
        idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();

        initErrorReporting();
        iniciarComponentes();
        //mostrarBarraAcciones();
        //iniciarComponentes();
        //ocultarBarraAcciones();
        readAccountEmail();
    }


    private void initErrorReporting() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, ReportActivity.class));
    }

//    private void mostrarBarraAcciones() {
//        setTitle("Registro de Usuario");
//        setTitle(Const.cad_vacia);
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
//    }

    private void iniciarComponentes() {

        tvTituloDialogo = (TextView) findViewById(R.id.tvTituloDialogo);
        mLinearlnlyCombo = (LinearLayout) findViewById(R.id.lnlyCombo);
        ivShowPassword = (ImageView) findViewById(R.id.iv_show_password);
        tvRestorePwd = (TextView) findViewById(R.id.tv_restore_password_2);

        tvRestorePwd.setOnClickListener(this);
        ivShowPassword.setOnClickListener(this);

        etTelefono = (EditText) findViewById(R.id.etRegTelefono);
        etClave = (EditText) findViewById(R.id.etRegClave);
        etRegisterEmail = (EditText) findViewById(R.id.et_register_email);
//        etRepetirClave = (EditText) findViewById(R.id.etRepetirClave);
        etNombreUsuario = (EditText) findViewById(R.id.etNombreUsuario);
        cbxPaises = (Spinner) findViewById(R.id.cbxPaises);
        tvPrefijo = (TextView) findViewById(R.id.tvPrefijo);

        if (cbxPaises == null)
            cbxPaises = (Spinner) findViewById(R.id.cbxPaises);


        tvTerminosCondiciones = (TextView) findViewById(R.id.tvAceptaCondiciones);
        tvTerminosCondiciones.setOnClickListener(this);

        chkTerminosCondiciones = (CheckedTextView) findViewById(R.id.chkTerminosCondiciones);
        chkTerminosCondiciones.setOnClickListener(this);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrarse);
        btnRegistrar.setOnClickListener(this);

        telefono = "";
        clave = "";
        nombre_usuario = "";
        entrada = new EntradaRegistarUsuario();
        etTelefono.setText(getSimCardNumber());
        etRegisterEmail.setText(readAccountEmail());
        tvTituloDialogo.setText(getResources().getString(R.string.btn_registro));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (((AppiTalkYou) getApplication()).isFlagEliminarPantallas()) {
            finalizarPantallas();
        }

    }

    public void setupAdapterPais() {

        List<Object> listaPaises = LogicaPais.obtenerListadoPaises(getApplicationContext(), idiomaSeleccionado);
        int pos = obtenerPaisUsuario(listaPaises, isSimCardEnabled);
        country = ((BeanPais) listaPaises.get(pos));

        AdaptadorLista adaptadorPaises = new AdaptadorLista(getApplicationContext(), R.layout.celda_pais, listaPaises, BeanPais.class.getSimpleName(), idiomaSeleccionado);
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cbxPaises.setAdapter(adaptadorPaises);
        cbxPaises.setOnItemSelectedListener(this);
        cbxPaises.setSelection(pos);
    }

    @SuppressLint("DefaultLocale")
    private int obtenerPaisUsuario(List<Object> listado, boolean isSimCardEnabled) {

        int pos = 0;
        TelephonyManager teleMgr = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String cmm;

        if (isSimCardEnabled)
            cmm = teleMgr.getSimCountryIso();
        else
            cmm = teleMgr.getNetworkCountryIso();

        Log.e(Const.DEBUG, "ConutryISO: " + cmm.toUpperCase());


        for (int i = 0; i < listado.size(); i++) {
            BeanPais pais = (BeanPais) listado.get(i);
            //Log.i("Intico", "cmm "+pais.getMCC());
            if (pais.getMCC().toUpperCase().equals(cmm.toUpperCase())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

        if (parent.getId() == R.id.cbxPaises) {

            if (!isSimCardEnabled) {
                country = ((BeanPais) cbxPaises.getItemAtPosition(pos));
                tvPrefijo.setText(Const.CADENA_PREFIJO + country.getID_Prefijo());
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {


    }

    @Override
    public void onClick(View v) {

        if (v == btnRegistrar) {
            registrarCuenta();

        } else if (v == tvTerminosCondiciones) {
            mostrarTerminosCondiciones();

        } else if (v == chkTerminosCondiciones) {

            if (chkTerminosCondiciones.isChecked())
                chkTerminosCondiciones.setChecked(false);
            else
                chkTerminosCondiciones.setChecked(true);

        } else if (v == ivShowPassword) {
            isShowingPassword = !isShowingPassword;
            setPasswordImage(isShowingPassword);
            showPasswordText(isShowingPassword);
        } else if (tvRestorePwd == v) {
            receiverPwdBySMS();
        }
    }

    private void receiverPwdBySMS() {
        pdDialogoEspera = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.sending), true, true);
        pdDialogoEspera.setCanceledOnTouchOutside(false);
        pdDialogoEspera.setCancelable(false);

        new ExcecuteRequest(new ResultadoOperacionListener() {
            @Override
            public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {
                pdDialogoEspera.dismiss();
                Toast.makeText(getApplicationContext(), R.string.message_send_success, Toast.LENGTH_LONG).show();
            }
        }).requestPasswordBySMS(entrada.getIdPrefijo(), entrada.getTelefono());

    }

    private void showPasswordText(boolean on) {
        if (on)
            etClave.setInputType(InputType.TYPE_CLASS_TEXT);
        else
            etClave.setInputType(129);

    }

    private void setPasswordImage(boolean on) {
        if (on)
            ivShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.vec_visibility_on));
        else
            ivShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.vec_visibility_off));

    }

    private void registrarCuenta() {


        telefono = etTelefono.getText().toString().trim();

        if (isSimCardEnabled ) {
            telefono = telefono.replace(Const.CADENA_PREFIJO, Const.CARACTER_VACIO);
            telefono = String.valueOf(telefono.subSequence(country.getID_Prefijo().length(), telefono.length()));
        }


        clave = etClave.getText().toString().trim();
//        re_clave = etRepetirClave.getText().toString().trim();
        nombre_usuario = etNombreUsuario.getText().toString().trim();

        if (telefono.equals(Const.cad_vacia) && clave.equals(Const.cad_vacia)
                && nombre_usuario.equals(Const.cad_vacia)
                && !chkTerminosCondiciones.isChecked()) {

            Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_datos_incompletos), Style.ALERT);

        } else if (telefono.equals(Const.cad_vacia)) {
            Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_falta_telefono), Style.ALERT);

        } else if (etRegisterEmail.getText().length() == 0) {
            Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_required_email), Style.ALERT);

        } else if (clave.trim().equals(Const.cad_vacia)) {
            Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_falta_clave), Style.ALERT);

        } else if (nombre_usuario.equals(Const.cad_vacia)) {
            Crouton.showText(RegistroActivity.this, getString(R.string.msjrc_error_falta_nombre), Style.ALERT);

        } else if (!chkTerminosCondiciones.isChecked()) {
            Crouton.showText(RegistroActivity.this, getString(R.string.msjrc_error_falta_aceptar_condiciones), Style.ALERT);

        } else {


            if (AppUtil.existeConexionInternet(RegistroActivity.this)) {
                validarTelefono();

//                String HTML_BODY = "<p style=\"text-align: justify;\">" +
//                        "        <center><h1>Datos a enviar</h1></center>" +
//                        "        Solo numero celular: <font color='#B0008E'><b>" + telefono + "</b></font> <br>\n" +
//                        "        Solo codigo de pais: <font color='#02A902'><b>" + country.getID_Prefijo() + "</b></font><br><br><br>" +
//                        "        Son los datos correctos?\n" +
//                        "        </p>" +
//                        "";
//                CustomAlertDialog.showPositiveNegativeAlert(this,
//                        Html.fromHtml(HTML_BODY),
//                        (AppUtil.obtenerIdiomaLocal().equals(Const.IDIOMA_ES) ? country.getDescripcionES() : country.getDescripcionEN()), this);

            } else
                Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }

    }

	/*
     * Validando Telefono y contrasena
	 */

    private void validarTelefono() {

        entrada.setClave(clave.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
        entrada.setIdioma(idiomaSeleccionado);
        entrada.setNombre(nombre_usuario.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
        entrada.setIdPais(country.getID_Pais());
        entrada.setTelefono(telefono.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
        entrada.setZonaHoraria(country.getID_Gtm());
        entrada.setIdPrefijo(country.getID_Prefijo());
        entrada.setCorreo(etRegisterEmail.getText().toString().trim());


        if (AppUtil.existeConexionInternet(RegistroActivity.this)) {

            pdDialogoEspera = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msjrc_validando_telefono), true, true);
            pdDialogoEspera.setCanceledOnTouchOutside(false);
            pdDialogoEspera.setCancelable(false);

            ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {
                @Override
                public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                    //Si no hay error
                    if (respuesta.getError().equals(Const.CARACTER_VACIO)) {

                        SalidaPin pin = (SalidaPin) respuesta.getObjeto();

                        if (pin.getResultado().equals(Const.RESULTADO_ERROR)) {
                            pdDialogoEspera.dismiss();
                            Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_producido_servidor), Style.ALERT);

                        } else if (pin.getResultado().equals(Const.RESULTADO_OTRO_CASO)) {
                            pdDialogoEspera.dismiss();
                            tvRestorePwd.setVisibility(View.VISIBLE);
                            Crouton.showText(RegistroActivity.this, getString(R.string.msjrc_error_existe_telefono), Style.ALERT);

                        } else if (pin.getResultado().equals(Const.RESULTADO_OK)) {

                            pdDialogoEspera.dismiss();
                            entrada.setValorPin(pin);
                            LogicaPantalla.personalizarIntentValidarPin(RegistroActivity.this, entrada);

                        } else {
                            Crouton.showText(RegistroActivity.this, getString(R.string.error_unknow), Style.ALERT);

                        }
                    } else {

                        pdDialogoEspera.dismiss();
                        Crouton.showText(RegistroActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }
            });
            ejecutar.validarTelefono(entrada);

        } else
            AppUtil.MostrarMensaje(RegistroActivity.this, getString(R.string.msj_error_conexion_internet));
    }

    private void mostrarTerminosCondiciones() {

        String url_terminos = "http://www.italkyou.com/terminos.asp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url_terminos));
        startActivity(i);

//		FragmentManager fm = getSupportFragmentManager();
//		DialogoTerminosCondiciones dialogo = new DialogoTerminosCondiciones();
//		dialogo.show(fm, "");

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                finalizarPantallas();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public String readAccountEmail() {
        String email = null;

        AccountManager man = ((AccountManager) getSystemService(ACCOUNT_SERVICE));
        Account[] accounts = man.getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].type.contains("google") || accounts[i].type.contains("hotmail")) {
                email = accounts[i].name;
//                Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
                break;
            }
        }

        return email;
    }

    public CharSequence getSimCardNumber() {
        CharSequence simCardNumber = "";

        try {
            TelephonyManager man = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            simCardNumber = man.getLine1Number();

//            Log.e(Const.DEBUG, "getNetworkCountryIso: " + man.getNetworkCountryIso());
//            Log.e(Const.DEBUG, "getNetworkOperator: " + man.getNetworkOperator());
//            Log.e(Const.DEBUG, "getSimOperator: " + man.getSimOperator());
//            Log.e(Const.DEBUG, "getSimOperatorName: " + man.getSimOperatorName());

            //No obtiene el numero.
            if (simCardNumber.equals("") || simCardNumber.length() == 0) {
                tvPrefijo.setVisibility(View.VISIBLE);
                mLinearlnlyCombo.setVisibility(View.VISIBLE);
                isSimCardEnabled = false;
                setupAdapterPais();

            } else {

                //Obtiene el número
                mLinearlnlyCombo.setVisibility(View.GONE);
                tvPrefijo.setVisibility(View.GONE);
                isSimCardEnabled = true;
//                tvPrefijo.setText(Const.CADENA_PREFIJO + country.getID_Prefijo());/**/
                country = LogicaPais.obtenerPais(this, man.getSimCountryIso());

            }
//                simCardNumber = simCardNumber.subSequence(2,simCardNumber.length());
        } catch (Exception ex) {
            setupAdapterPais();
            tvPrefijo.setVisibility(View.VISIBLE);
            mLinearlnlyCombo.setVisibility(View.VISIBLE);
            isSimCardEnabled = false;
            simCardNumber = "";
            Log.e(Const.DEBUG, "Error al obtener simCard ", ex);
        }

        return (simCardNumber).length() > 0 ? Const.CADENA_PREFIJO + simCardNumber : Const.CARACTER_VACIO;
    }

    @Override
    public void onPositive() {
        validarTelefono();
    }

    @Override
    public void onNegative() {

    }
}
