package com.italkyou.gui.menu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRedirect;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaRedireccionarAnexo;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicRedirect;
import com.italkyou.controladores.LogicaPais;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RedireccionarAnexoActivity extends BaseActivity
        implements
        OnClickListener,
        OnItemSelectedListener,
        OnDateChangedListener {

    //Statics
    private static final String TAG = RedireccionarAnexoActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private static final String TAG_ACTIVE = "1";
    private static final String TAG_INACTIVE = "0";
    private static final String TAG_PERMANENT = "1";
    private static final String TAG_NO_PERMANENT = "0";
    private static final int TYPE_UPDATE = 1;
    private static final int TYPE_CANCEL = 0;
    private static final int TYPE_DEFAULT = -1;

    private BeanUsuario usuario;
    private DatePicker dtpFechaIn, dtpFechaFin;
    private Button btnRedireccionar;
    private EditText etTelefono;
    private TextView tvPrefijo;
    private TextView tvRedirectPermanet;
    private Spinner cbxPaises;
    private String id_pais;
    private String fechaIni = "", fechaFin = "";
    private String telefono;
    private Switch swRedireccionar;
    private AppiTalkYou app;
    private int posIdPais;
    private Context _context;

    //ViewGroups
    private LinearLayout layCountry;
    private LinearLayout layNumber;
    private LinearLayout layAlways;
    private LinearLayout layDates;

    //Runtime
    private boolean flagPermanente = true;
    private boolean flagActivo = true;
    private RadioButton rbEnabled;
    private RadioButton rbDisabled;
    private String prefijo_pais;
    private boolean isMe = true;
    private boolean isCanceled = false;
    private boolean flagCancel = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redireccionar_anexo);
        this.tipoMenu = Const.MENU_VACIA;
        _context = getApplicationContext();
        app = (AppiTalkYou) getApplication();
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        usuario = app.getUsuario();
        inicializarComponentes();
    }


    private void inicializarComponentes() {

        etTelefono = (EditText) findViewById(R.id.etTelefonoRedireccionar);
        tvRedirectPermanet = (TextView)findViewById(R.id.tv_redirect_permanent);

        String idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();
        List<Object> listaPaises = LogicaPais.obtenerListadoPaises(getApplicationContext(),idiomaSeleccionado);
        id_pais = ((BeanPais) listaPaises.get(0)).getID_Pais();
        posIdPais = Integer.parseInt(((BeanPais) listaPaises.get(0)).getID());
        AdaptadorLista adaptadorPaises = new AdaptadorLista(getApplicationContext(), R.layout.celda_pais, listaPaises, BeanPais.class.getSimpleName(), usuario.getID_Idioma());
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbxPaises = (Spinner) this.findViewById(R.id.cbxPaisesRedireccionar);
        cbxPaises.setAdapter(adaptadorPaises);
        cbxPaises.setOnItemSelectedListener(this);
        setUpCountry(app.getUsuario().getId_prefijo(),listaPaises);

        layCountry = (LinearLayout) findViewById(R.id.lnlyCombo);
        layNumber = (LinearLayout) findViewById(R.id.lnlyTelefono);
        layAlways = (LinearLayout) findViewById(R.id.laySwitch);
        layDates = (LinearLayout) findViewById(R.id.lnlyFechas);
        rbEnabled = (RadioButton) findViewById(R.id.rbtn_active);
        rbDisabled = (RadioButton) findViewById(R.id.rbtn_disactive);


        tvPrefijo = (TextView) findViewById(R.id.tvPrefijoNumero);
        prefijo_pais = ((BeanPais) listaPaises.get(0)).getID_Prefijo();

        tvPrefijo.setText(Const.CADENA_PREFIJO + prefijo_pais);
        dtpFechaIn = (DatePicker) findViewById(R.id.dtpFechaInicio);
        dtpFechaFin = (DatePicker) findViewById(R.id.dtpFechaFin);
        btnRedireccionar = (Button) findViewById(R.id.btnRedireccionar);
        btnRedireccionar.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        dtpFechaIn.init(year, month, day, this);
        dtpFechaFin.init(year, month, day, this);
        this.fechaFin = obtenerFecha(year, month, day);
        this.fechaIni = obtenerFecha(year, month, day);
        this.telefono = "";

        swRedireccionar = (Switch) findViewById(R.id.swRedireccionar);
        swRedireccionar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    enableRedirectBetweenDates(false);
                else {
                    if (flagActivo)
                        enableRedirectBetweenDates(true);
                    else {
                        swRedireccionar.setChecked(true);
                    }
                }
            }
        });

        //Config
        etTelefono.setText(usuario.getNumero());
        usuario.getId_prefijo();
        activeRedirect(false);

        if (LogicRedirect.checkIfExist(usuario.getID_Usuario(), _context))
            loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        BeanRedirect bean = LogicRedirect.getRedirect(usuario.getID_Usuario(), _context);

        //Active
        if (bean.getFlagActive() > 0)
            enableRadioButtonActive(true);
        else
            enableRadioButtonActive(false);

        //Country
        String idiomaSeleccionado = AppUtil.obtenerIdiomaLocal();
        List<Object> listaPaises = LogicaPais.obtenerListadoPaises(getApplicationContext(),idiomaSeleccionado);
        setUpCountry(String.valueOf(bean.getZipcode()),listaPaises);

        //PhoneNumber
        etTelefono.setText(String.valueOf(bean.getPhoneNumber()));

        //Permanent
        if (bean.getFlagPermanent() > 0)
            enableSwitchAsPermanent(true);
        else {
            enableSwitchAsPermanent(false);
            configDataPicker(bean.getFromDate(), bean.getToDate());
        }

        //Button settings
        if (flagActivo && usuario.getNumero().equals(String.valueOf(bean.getPhoneNumber()))) {
            settingButtonStyle(getResources(), TYPE_DEFAULT);
        } else if (flagActivo && !usuario.getNumero().equals(String.valueOf(bean.getPhoneNumber()))) {
            settingButtonStyle(getResources(), TYPE_UPDATE);
            isMe = false;
        } else {
            isMe = false;
            settingButtonStyle(getResources(), TYPE_CANCEL);
        }


    }

    private void configDataPicker(String fromDate, String toDate) {
        //from
        int yearFrom = Integer.parseInt(fromDate.substring(0, 4));
        int monthFrom = Integer.parseInt(fromDate.substring(4, 6)) - 1;
        int dayFrom = Integer.parseInt(fromDate.substring(6));
        //to
        int yearTo = Integer.parseInt(toDate.substring(0, 4));
        int monthTo = Integer.parseInt(toDate.substring(4, 6)) - 1;
        int dayTo = Integer.parseInt(toDate.substring(6));

        dtpFechaIn.init(yearFrom, monthFrom, dayFrom, this);
        dtpFechaFin.init(yearTo, monthTo, dayTo, this);
    }

    private void settingButtonStyle(Resources src, int type) {
        switch (type) {
            case TYPE_CANCEL:
                btnRedireccionar.setText(src.getString(R.string.action_cancel));
                btnRedireccionar.setBackground(src.getDrawable(R.drawable.button_cancel_selector));
                break;

            case TYPE_UPDATE:
                btnRedireccionar.setText(src.getString(R.string.action_update));
                btnRedireccionar.setBackground(src.getDrawable(R.drawable.button_update_selector));
                break;

            default:
                btnRedireccionar.setText(src.getString(R.string.action_redirect));
                btnRedireccionar.setBackground(src.getDrawable(R.drawable.button_dial_selector));
                break;

        }

    }

    private void enableSwitchAsPermanent(boolean b) {
        if (b)
            swRedireccionar.setChecked(true);
        else
            swRedireccionar.setChecked(false);

    }

    private void enableRadioButtonActive(boolean on) {
        if (on)
            rbEnabled.setChecked(true);
        else
            rbDisabled.setChecked(true);

    }


    @Override
    public void onClick(View v) {

        if (v == btnRedireccionar) {
            telefono = etTelefono.getText().toString().trim();
            if (!telefono.equals(Const.cad_vacia)) {
                if (Integer.parseInt(fechaIni) <= Integer.parseInt(fechaFin))
//                    Toast.makeText(getApplicationContext(),"El anexo se ha redireccionado.",Toast.LENGTH_LONG);
                    redireccionarAnexo();
                else {
                    Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_error_fechas), Style.ALERT);
                }
            } else {
                Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_error_falta_telefono), Style.ALERT);
            }
        }
    }


    private void redireccionarAnexo() {

        EntradaRedireccionarAnexo entrada = new EntradaRedireccionarAnexo();
        entrada.setIdUsuario(usuario.getID_Usuario());
        entrada.setTelefono(telefono);
        entrada.setIdPais(id_pais);
        entrada.setFechaIni(fechaIni);
        entrada.setFechaFin(fechaFin);
        //HardCode
        entrada.setFlagActivo((flagActivo) ? TAG_ACTIVE : TAG_INACTIVE);
        entrada.setFlagPermanente((flagPermanente) ? TAG_PERMANENT : TAG_NO_PERMANENT);

        if (AppUtil.existeConexionInternet(RedireccionarAnexoActivity.this)) {
            pd = ProgressDialog.show(this, Const.TITULO_APP,
                    getString(R.string.msj_enviando_informacion), true, true);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);

            ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    pd.dismiss();
                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();
                        if (resultado.getResultado().equals(Const.RESULTADO_OK)) {

                            //Is no my number
                            isMe = false;

                            //Is cancel?
                            if (flagCancel) {
                                isCanceled = true;
                                rbDisabled.setChecked(false);
                                rbEnabled.setChecked(true);
                                settingButtonStyle(getResources(), TYPE_DEFAULT);
                            } else
                                isCanceled = false;

                            //Object to store
                            BeanRedirect bean = new BeanRedirect(
                                    Long.parseLong(telefono),
                                    Integer.parseInt(prefijo_pais),
                                    Integer.parseInt(usuario.getID_Usuario()),
                                    fechaIni,
                                    fechaFin,
                                    (flagPermanente) ? 1 : 0,
                                    (flagActivo) ? 1 : 0
                            );

                            int rows = 0;
                            if (LogicRedirect.checkIfExist(usuario.getID_Usuario(), _context)) {
                                if (!flagActivo) {
                                    //Setup default values
                                    bean.setPhoneNumber(Long.parseLong(usuario.getNumero()));
                                    bean.setZipcode(Integer.parseInt(usuario.getId_prefijo()));
                                    bean.setFlagActive(Integer.parseInt(TAG_ACTIVE));
                                    settingButtonStyle(getResources(), TYPE_DEFAULT);
                                } else
                                    settingButtonStyle(getResources(), TYPE_UPDATE);

                                rows = LogicRedirect.update(bean, _context);
                                Log.e(Const.DEBUG, TAG + "Existe: true, row updated: " + rows);
                            } else {
                                Log.e(Const.DEBUG, TAG + "Existe: false, row inserted: " + rows);
                                rows = LogicRedirect.insert(bean, _context);
                                //Setting button cancel
                                if (flagActivo)
                                    settingButtonStyle(getResources(), TYPE_UPDATE);
                            }

                            Log.e(Const.DEBUG, TAG + bean.toString());
                            Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_redireccionar_exito), Style.CONFIRM);
                        } else if (resultado.getResultado().equals(Const.RESULTADO_ERROR)) {
                            Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_redireccionar_existe), Style.ALERT);
                        } else
                            Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_redireccionar_invalido), Style.ALERT);

                    } else {
                        Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }
            });
            ejecutar.redireccionarAnexo(entrada);
        } else
            Crouton.showText(RedireccionarAnexoActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
    }

    public void setUpCountry(String zip,List<Object> listCountry) {
//        BeanPais country = LogicaPais.getCountryByZipCode(getApplicationContext(), zip);
        int c=0;
        for (Object beanPais : listCountry) {
            if (zip.equals(((BeanPais) beanPais).getID_Prefijo())){
                cbxPaises.setSelection(c);
                break;
            }
            c++;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        if (parent.getId() == R.id.cbxPaises) ;
        {
            id_pais = ((BeanPais) cbxPaises.getItemAtPosition(pos)).getID_Pais();
            prefijo_pais = ((BeanPais) cbxPaises.getItemAtPosition(pos)).getID_Prefijo();
            tvPrefijo.setText(Const.CADENA_PREFIJO + prefijo_pais);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @SuppressLint("SimpleDateFormat")
    private String obtenerFecha(int anho, int mes, int dia) {
        GregorianCalendar cal = new GregorianCalendar(anho, mes, dia);
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String fecha = df.format(date);
        return fecha;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        if (view == dtpFechaIn) {
            fechaIni = obtenerFecha(year, monthOfYear, dayOfMonth);
        } else {
            fechaFin = obtenerFecha(year, monthOfYear, dayOfMonth);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbtn_active:
                if (checked) {
                    activeRedirect(true);

                    //Other number
                    if (!isMe && !isCanceled)
                        settingButtonStyle(getResources(), TYPE_UPDATE);

                    flagActivo = true;
                    flagCancel = false;

                    rbDisabled.setTextColor(getResources().getColor(R.color.Gray));
                    rbEnabled.setTextColor(getResources().getColor(R.color.my_green));
                }
                break;
            case R.id.rbtn_disactive:
                if (checked) {
                    //Setting button cancel
                    activeRedirect(false);

                    //Other number
                    if (!isMe && !isCanceled)
                        settingButtonStyle(getResources(), TYPE_CANCEL);

                    if (isCanceled) {
                        rbDisabled.setChecked(false);
                        rbEnabled.setChecked(true);
                        Crouton.showText(this, getResources().getString(R.string.msj_redirect_can_redirect), Style.INFO);
                    }


                    flagActivo = false;
                    flagCancel = true;

                    //Colors
                    rbDisabled.setTextColor(getResources().getColor(R.color.my_red));
                    rbEnabled.setTextColor(getResources().getColor(R.color.Gray));
                }
                break;
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void activeRedirect(boolean yes) {

        if (yes) {
//            layCountry.setVisibility(View.VISIBLE);
//            layNumber.setVisibility(View.VISIBLE);
//            layAlways.setVisibility(View.VISIBLE);
            btnRedireccionar.setEnabled(true);
//            swRedireccionar.setChecked(true);
//            btnRedireccionar.setBackground(getResources().getDrawable(R.drawable.button_selector));
        } else {
//            layCountry.setVisibility(View.GONE);
//            layNumber.setVisibility(View.GONE);
//            layAlways.setVisibility(View.GONE);
            layDates.setVisibility(View.GONE);
            swRedireccionar.setChecked(true);
//            btnRedireccionar.setBackgroundColor(getResources().getColor(R.color.gray_color_3));
        }
    }

    private void enableRedirectBetweenDates(boolean on) {
        Resources src = getResources();
        if (on) {
            flagPermanente = false;
            layDates.setVisibility(View.VISIBLE);
            tvRedirectPermanet.setTextColor(src.getColor(R.color.my_green));
            tvRedirectPermanet.setText(src.getString(R.string.label_redirect_temp));
        } else {
            flagPermanente = true;
            layDates.setVisibility(View.GONE);
            tvRedirectPermanet.setTextColor(src.getColor(R.color.pantone_306C));
            tvRedirectPermanet.setText(src.getString(R.string.label_redirect_always));

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogicaPantalla.personalizarIntentVistaPrincipal(RedireccionarAnexoActivity.this, Const.PANTALLA_PRINCIPAL, RedireccionarAnexoActivity.class.getSimpleName(),false);
    }
}
