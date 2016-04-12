package com.italkyou.gui.mensajes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaEnviarSMS;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.contactos.ListaContactoFragment;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.LogApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class FragmentoMensajeria extends Fragment implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener, AdapterView.OnItemSelectedListener, Switch.OnCheckedChangeListener {

    private static final String TAG = FragmentoMensajeria.class.getSimpleName();
    private EditText etNumero, etMensaje;
    private TextView tvTitle;
    private Button btnEnviar;
    private ImageButton ibAddNumber;
    private DatePicker dtpFecha;
    private TimePicker tpHora;
    private ProgressDialog pd = null;
    private Switch swAgendar;
    private LinearLayout llContenedorFechaHora;
    private Spinner spLisPais;
    private FragmentTabHost thFechaHora;
    private boolean flagAgendar;
    private String fecha;
    private String codPais;
    private String fechaAgendamiento;
    private String horaAgendamiento;
    private String minutosAgendamiento;
    private Resources recursosApp;
    private AppiTalkYou app;
    private Context ctx;
    private List<Object> listaPaises;

    public static FragmentoMensajeria nuevaInstancia() {
        FragmentoMensajeria fragmento = new FragmentoMensajeria();
        return fragmento;
    }

    public static FragmentoMensajeria newInstance(String param1, String param2) {
        FragmentoMensajeria fragment = new FragmentoMensajeria();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentoMensajeria() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recursosApp = getActivity().getResources();
        app = (AppiTalkYou) getActivity().getApplication();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String phoneNumber = app.getAnyNumber();
        if (phoneNumber != "" && phoneNumber != null)
            etNumero.setText(phoneNumber);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmento_mensajeria, container, false);
        inicializarComponentes(view);
        return view;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        fecha = obtenerFecha(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAgendar:
                enviarMensajeSMS();
                break;

            case R.id.ib_add_number:
                cargarFragmentoContactos();
                break;
        }


    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        tpHora.setCurrentHour(hourOfDay);
        tpHora.setCurrentMinute(minute);
    }

    private void inicializarComponentes(View view) {
        setCtx(getActivity().getApplicationContext());


        thFechaHora = (FragmentTabHost) view.findViewById(R.id.tabHost);
        thFechaHora.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        tvTitle = (TextView) view.findViewById(R.id.tv_sms_title);

        tvTitle.setText(getString(R.string.title_sms));
        String tituloTabFecha = recursosApp.getString(R.string.lbl_fecha_sms);
        String tituloTabHora = recursosApp.getString(R.string.lbl_hora_sms);

        TabHost.TabSpec tabFechaSMS = thFechaHora.newTabSpec(tituloTabFecha);

        tabFechaSMS.setIndicator(tituloTabFecha);

        thFechaHora.addTab(tabFechaSMS, FragmentoFechaSMS.class, null);

        TabHost.TabSpec tabHoraSMS = thFechaHora.newTabSpec(tituloTabHora);
        tabHoraSMS.setIndicator(tituloTabHora);
        thFechaHora.addTab(tabHoraSMS, FragmentoHoraSMS.class, null);

        etNumero = (EditText) view.findViewById(R.id.etTelefonoAgendar);
        etMensaje = (EditText) view.findViewById(R.id.etMensajeAgendar);
        etMensaje.setSelection(0);

        swAgendar = (Switch) view.findViewById(R.id.swAgendar);
        swAgendar.setOnCheckedChangeListener(this);
        llContenedorFechaHora = (LinearLayout) view.findViewById(R.id.lnlyFechaHora);
        llContenedorFechaHora.setVisibility(View.GONE);

        btnEnviar = (Button) view.findViewById(R.id.btnAgendar);
        btnEnviar.setOnClickListener(this);
        dtpFecha = (DatePicker) view.findViewById(R.id.dtpFechaEnvio);
        tpHora = (TimePicker) view.findViewById(R.id.tpHora);

        Calendar c = Calendar.getInstance();
        int anho = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        dtpFecha.init(anho, mes, dia, this);
        tpHora.setCurrentHour(hora);
        tpHora.setCurrentMinute(minuto);
        tpHora.setOnTimeChangedListener(this);
        this.fecha = obtenerFecha(anho, mes, dia);
        fechaAgendamiento = obtenerFecha(anho, mes, dia);

        horaAgendamiento = String.valueOf(hora);
        minutosAgendamiento = String.valueOf(minuto);

    }


    @SuppressLint("SimpleDateFormat")
    private String obtenerFecha(int anho, int mes, int dia) {

        GregorianCalendar cal = new GregorianCalendar(anho, mes, dia);
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String fecha = df.format(date);
        LogApp.log("FechaMensaje=" + fecha);

        return fecha;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void capturarFechaHoraAgendamiento() {

        List<Fragment> frags = getChildFragmentManager().getFragments();

        for (int x = 0; x < frags.size(); x++) {
            if (x == 0) {
                FragmentoFechaSMS fragmetoFecha = (FragmentoFechaSMS) frags.get(x);
                fechaAgendamiento = fragmetoFecha.getStrFecha().trim();
            } else {
                FragmentoHoraSMS fragmetoHora = (FragmentoHoraSMS) frags.get(x);
                horaAgendamiento = String.valueOf(fragmetoHora.getTpHora().getCurrentHour());
                minutosAgendamiento = String.valueOf(fragmetoHora.getTpHora().getCurrentMinute());
            }
        }
    }

    private void enviarMensajeSMS() {
        String empty = "";
        String telefono = etNumero.getText().toString().trim().replace(Const.CADENA_PREFIJO, empty);
        String mensaje = etMensaje.getText().toString().trim();

        if (telefono.length() > 8) {
            if (!mensaje.equals(Const.cad_vacia)) {

                if (!flagAgendar)
                    agendarMensaje(telefono, mensaje);
                else {
                    capturarFechaHoraAgendamiento();
                    agendarMensaje(telefono, mensaje, fechaAgendamiento, horaAgendamiento, minutosAgendamiento);
                }

            } else {
                Crouton.showText(getActivity(), getString(R.string.msj_error_falta_mensaje), Style.ALERT);
            }
        } else {
            Crouton.showText(getActivity(), getString(R.string.msj_error_falta_telefono), Style.ALERT);
        }
    }


    private void agendarMensaje(String telefono, String mensaje) {


        BeanUsuario usuario = app.getUsuario();

        EntradaEnviarSMS entrada = new EntradaEnviarSMS();
        entrada.setAgendar(Const.SMS_AGENDAR);
        entrada.setCelular(telefono);
        entrada.setFecha(fecha);
        entrada.setHora(String.valueOf(tpHora.getCurrentHour()));
        entrada.setMinuto(String.valueOf(tpHora.getCurrentMinute()));
        entrada.setIdioma(usuario.getID_Idioma());
        entrada.setIdUsuario(usuario.getID_Usuario());
//        entrada.setMensaje(mensaje.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
        entrada.setMensaje(mensaje);

        if (AppUtil.existeConexionInternet(getActivity())) {

            pd = ProgressDialog.show(getActivity(), Const.TITULO_APP, getString(R.string.msj_enviando_sms), true, true);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);

            ExecuteRequest ejecutar = new ExecuteRequest(new ExecuteRequest.ResultadoOperacionListener() {

                @Override
                public void onOperationDone(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();

                        if (resultado.getResultado().equals(Const.RESULTADO_OK)) {
                            pd.dismiss();
                            etMensaje.setText(Const.cad_vacia);
                            Crouton.showText(getActivity(), getString(R.string.msj_enviar_sms), Style.CONFIRM);

                            BaseActivity base = new BaseActivity();
                            base.getBalanceIty();
                        } else {
                            pd.dismiss();
                            Crouton.showText(getActivity(), getString(R.string.msj_error_enviar_sms), Style.ALERT);
                        }

                    } else {
                        pd.dismiss();
                        Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }

            });
            ejecutar.enviarSMS(entrada, app.getUsuario());

        } else
            Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_internet), Style.ALERT);
    }

    private boolean isCorrectToSend(String date, String time) {
        boolean status = true;

        String currentDate = AppUtil.obtenerFecha();
        String currentTime = AppUtil.obtenerHora() + AppUtil.obtenerMinutos();

        if (currentDate.compareTo(date) == 0 && currentTime.compareTo(time) >= 0)
            return false;

        if (currentDate.compareTo(date) == 0 && currentTime.compareTo(time) > 0)
            return false;

        if (currentDate.compareTo(date) > 0)
            return false;


        return status;
    }


    private void agendarMensaje(String telefono, String mensaje, String fechaAgendamiento, String horaAgendamiento, String minutosAgendamiento) {

        Boolean yes = isCorrectToSend(fechaAgendamiento, horaAgendamiento + minutosAgendamiento);

        if (yes) {
            BeanUsuario usuario = app.getUsuario();
            EntradaEnviarSMS entrada = new EntradaEnviarSMS();

            entrada.setAgendar(Const.SMS_AGENDAR);
            entrada.setCelular(telefono);
            entrada.setFecha(fechaAgendamiento);
            entrada.setHora(horaAgendamiento);
            entrada.setMinuto(minutosAgendamiento);
            entrada.setIdioma(usuario.getID_Idioma());
            entrada.setIdUsuario(usuario.getID_Usuario());
//            entrada.setMensaje(mensaje.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
            entrada.setMensaje(mensaje);

            if (AppUtil.existeConexionInternet(getActivity())) {

                pd = ProgressDialog.show(getActivity(), Const.TITULO_APP, getString(R.string.msj_enviando_sms), true, true);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(false);

                ExecuteRequest ejecutar = new ExecuteRequest(new ExecuteRequest.ResultadoOperacionListener() {

                    @Override
                    public void onOperationDone(BeanRespuestaOperacion respuesta) {

                        if (respuesta.getError().equals(Const.cad_vacia)) {

                            SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();

                            if (resultado.getResultado().equals(Const.RESULTADO_OK)) {
                                pd.dismiss();
                                Crouton.showText(getActivity(), getString(R.string.msj_enviar_sms), Style.CONFIRM);
//                                app.setIsBalanceChanged(true);
                                BaseActivity base = new BaseActivity();
                                base.getBalanceIty();
                                limpiarControles();
                            } else {
                                pd.dismiss();
                                Crouton.showText(getActivity(), getString(R.string.msj_error_enviar_sms), Style.ALERT);
                            }

                        } else {
                            pd.dismiss();
                            Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
                        }
                    }

                });
                ejecutar.enviarSMS(entrada, app.getUsuario());

            } else
                Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_internet), Style.ALERT);

        } else
            Crouton.showText(getActivity(), R.string.msj_error_ambos_envio, Style.ALERT);

    }

    private void limpiarControles() {
        etMensaje.setText("");
        swAgendar.setChecked(false);
        etNumero.setText("");
        llContenedorFechaHora.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        BeanPais pais = (BeanPais)listaPaises.get(position);
//        codPais = pais.getID_Prefijo();
//        Log.e(TAG, "Cod: " + codPais);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*Setter and Getter*/
    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            flagAgendar = true;
            llContenedorFechaHora.setVisibility(View.VISIBLE);
            thFechaHora.setCurrentTab(1);
            thFechaHora.setCurrentTab(0);

            Log.e(TAG, "Status on" + isChecked);

        } else {
            flagAgendar = false;
            llContenedorFechaHora.setVisibility(View.GONE);

            Log.e(TAG, "Status on" + isChecked);
        }
    }

    /*rsantillan*/
    private void cargarFragmentoContactos() {

//        Fragment fragmento = ListaContactoAnexoFragment.nuevaInstancia(Const.PANTALLA_CHAT_SIMPLE,true);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
//        fragmentTransaction.commit();

        Fragment fragmento = ListaContactoFragment.nuevaInstancia(Const.PANTALLA_CONTACTO_SMS);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_fragmentos, fragmento);
        fragmentTransaction.commit();

    }

}
