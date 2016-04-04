package com.italkyou.gui.contactos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanSaldo;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaBuscarContactos;
import com.italkyou.beans.entradas.EntradaEnviarSMS;
import com.italkyou.beans.salidas.OutputContact;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.chat.ChatMensajeActivity;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.gui.personalizado.DialogoLista;
import com.italkyou.gui.personalizado.DialogoLista.onSeleccionarOpcionListener;
import com.italkyou.gui.personalizado.DialogoMensajeSMS;
import com.italkyou.gui.personalizado.DialogoMensajeSMS.onMensajeSMSListener;
import com.italkyou.sip.SIPManager;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ContactoItalkYouActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnCheckedChangeListener {

    private static final String TAG = ContactoItalkYouActivity.class.getSimpleName() + Const.ESPACIO_BLANCO;
    private List<Object> listaContactos;
    private ListView lista;
    private AdaptadorLista adaptador;
    private ImageButton btnBuscar;
    private RadioGroup rgFiltro;
    private EditText etDato;
    private EntradaBuscarContactos entrada;
    private BeanUsuario usuario;
    private AppiTalkYou app;
    private SIPManager sipManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactos_italkyou);
        this.tipoMenu = Const.MENU_VACIA;
        app = (AppiTalkYou) getApplication();
        usuario = app.getUsuario();
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        iniciarComponentes();
        getBalanceIty();
    }

    private void iniciarComponentes() {

        listaContactos = new ArrayList<Object>();
        lista = (ListView) findViewById(R.id.listcontactosiTalkYou);
        adaptador = new AdaptadorLista(getApplicationContext(), R.layout.row_contact_italkyou, listaContactos, OutputContact.class.getSimpleName());
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(this);
        btnBuscar = (ImageButton) findViewById(R.id.imgBuscarContactos);
        btnBuscar.setOnClickListener(this);
        rgFiltro = (RadioGroup) findViewById(R.id.rgContactos);
        rgFiltro.setOnCheckedChangeListener(this);
        etDato = (EditText) findViewById(R.id.etDatoContacto);
        entrada = new EntradaBuscarContactos();
        entrada.setTipo(Const.TIPO_ANEXO);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (group == rgFiltro) {
            etDato.setText(Const.cad_vacia);
            if (checkedId == R.id.rbAnexo) {
                entrada.setTipo(Const.TIPO_ANEXO);
                etDato.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (checkedId == R.id.rbNombre) {
                entrada.setTipo(Const.TIPO_NOMBRE);
                etDato.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (checkedId == R.id.rbCorreo) {
                entrada.setTipo(Const.TIPO_CORREO);
                etDato.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnBuscar) {

            String dato = etDato.getText().toString().trim();

            if (!dato.equals(Const.cad_vacia)) {
                entrada.setDato(dato);
                entrada.setAnexo(usuario.getAnexo());

                if (AppUtil.existeConexionInternet(ContactoItalkYouActivity.this)) {
                    pd = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msjbc_enviando_consulta_contactos), true, true);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setCancelable(false);
                    buscarContactos();
                } else {
                    Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
                }

            } else {
                Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msjbc_error_falta_dato), Style.ALERT);
            }
        }
    }

    private void buscarContactos() {

        ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

            @Override
            public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                pd.dismiss();

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    @SuppressWarnings("unchecked")
                    List<Object> listado = (List<Object>) respuesta.getObjeto();

                    if (listado.size() > 0) {
                        listaContactos.clear();
                        adaptador.notifyDataSetChanged();
                        listaContactos.addAll(listado);
                    } else {
                        Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msjbc_no_hay_coincidencia), Style.INFO);
                        listaContactos.clear();
                        adaptador.notifyDataSetChanged();
                    }

                } else {
                    Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                }
            }
        });
        ejecutar.busquedaContactos(entrada);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {

        final OutputContact contacto = (OutputContact) listaContactos.get(posicion);
        FragmentManager fm = getSupportFragmentManager();
        DialogoLista dialogo = new DialogoLista();

        String balance = app.getUsuario().getSaldo();
        final Double dBalance;

        if (balance == null) {
            dBalance = LogicaUsuario.getLocalBalance(getApplicationContext(), app.getUsuario().getID_Usuario());
        } else {
            dBalance = Double.parseDouble(balance);
        }


        onSeleccionarOpcionListener listener = new onSeleccionarOpcionListener() {

            @Override
            public void setSeleccionarOpcionListener(String texto) {

                //Call annex
                if (texto.equals(Const.descripcion_llamada_gratis)) {
                    LogicaPantalla.personalizarIntentRealizarLlamada(SIPManager.newInstance().buildAddress(contacto.getAnexo()).asStringUriOnly(), ContactoItalkYouActivity.this, Const.tipo_llamada_anexoVOIP, contacto.getAnexo(), null, "0", "");
                } else if (texto.equals(Const.descripcion_llamada_pago)) {
                    if (dBalance > 0) {
                        LogicaPantalla.personalizarIntentRealizarLlamada(SIPManager.newInstance().buildAddress(contacto.getCelular()).asStringUriOnly(), ContactoItalkYouActivity.this, Const.tipo_llamada_internacional, contacto.getCelular(), null, "0", "");
                    } else {
                        String message = getString(R.string.message_alert_balance_none);
                        CustomAlertDialog.showSingleAlert(ContactoItalkYouActivity.this, message);
                    }
                } else if (texto.equals(Const.descripcion_llamada_sms)) {

                    if (dBalance > 0)
                        cargarDialogoSMS(contacto.getCelular());
                    else {
                        String message = getString(R.string.message_alert_balance_sms);
                        CustomAlertDialog.showSingleAlert(ContactoItalkYouActivity.this, message);
                    }
                }
            }
        };

        dialogo.onSeleccionarOpcionListener = listener;
        dialogo.titulo = getString(R.string.titulo_seleccionar);
        dialogo.pantalla = Const.PANTALLA_CONTACTO;
        dialogo.flag = Const.USER_ITY;
        dialogo.show(fm, "");
    }

    private void cargarDialogoSMS(final String numero) {

        FragmentManager fm = getSupportFragmentManager();
        DialogoMensajeSMS dialogo = new DialogoMensajeSMS();
        onMensajeSMSListener mlistener = new onMensajeSMSListener() {

            @Override
            public void setMensajeSMSListener(String texto) {
                enviarSMS(numero, texto);
            }
        };

        dialogo.mlistener = mlistener;
        dialogo.show(fm, "");
    }

    private void enviarSMS(String numero, String mensaje) {

        pd = ProgressDialog.show(ContactoItalkYouActivity.this, Const.TITULO_APP, getString(R.string.msj_enviando_sms), true, true);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        EntradaEnviarSMS entrada = new EntradaEnviarSMS();
        entrada.setIdioma(usuario.getID_Idioma());
        entrada.setIdUsuario(usuario.getID_Usuario());
        entrada.setCelular(numero);
        entrada.setMensaje(mensaje);
        entrada.setAgendar(Const.SMS_NO_AGENDAR);
        entrada.setFecha(AppUtil.obtenerFecha());
        entrada.setHora(AppUtil.obtenerHora());
        entrada.setMinuto(AppUtil.obtenerMinutos());

        if (AppUtil.existeConexionInternet(ContactoItalkYouActivity.this)) {
            ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

                @Override
                public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                    if (respuesta.getError().equals(Const.cad_vacia)) {
                        SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();
                        if (resultado.getResultado().equals(Const.RESULTADO_OK)) {
                            pd.dismiss();
                            Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_enviar_sms), Style.CONFIRM);
                        } else {
                            pd.dismiss();
                            Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_error_enviar_sms), Style.ALERT);
                        }
                    } else {
                        pd.dismiss();
                        Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                    }
                }

            });
            ejecutar.enviarSMS(entrada, app.getUsuario());
        } else {
            Crouton.showText(ContactoItalkYouActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LogicaPantalla.personalizarIntentVistaPrincipal(ContactoItalkYouActivity.this, Const.PANTALLA_CONTACTO, ChatMensajeActivity.class.getSimpleName());
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void getBalanceIty() {
        String annex;
        try {
            annex = app.getUsuario().getAnexo();
        } catch (NullPointerException ExNull) {
            app = ((AppiTalkYou) getApplication());
            app.setUsuario(LogicaUsuario.obtenerUsuario(getApplicationContext()));
            annex = app.getUsuario().getAnexo();
        }


        final String finalAnnex = annex;
        ExcecuteRequest ejecutar = new ExcecuteRequest(new ExcecuteRequest.ResultadoOperacionListener() {

            @Override
            public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                if (respuesta.getError().equals(Const.cad_vacia)) {
                    final BeanSaldo saldo = (BeanSaldo) respuesta.getObjeto();

                    if (saldo.getResultado().equals(Const.RESULTADO_OK)) {
                        app.setSaldo(saldo.getBalance());
                        LogicaUsuario.actualizarSaldo(getApplicationContext(), finalAnnex, saldo.getBalance());

                        //Show in actionbar
                        printBalance();
                    }
                }

            }
        });
        ejecutar.obtenerSaldo(finalAnnex);
    }

    private void printBalance() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String sBalance = StringUtil.format(Double.parseDouble(app.getSaldo()));
                tvEstadoAnexo.setText(getString(R.string.bar_balance) + sBalance + Const.TAG_CURRENCY);
                Log.e(Const.DEBUG_BALANCE, TAG + "Saldo--> " + sBalance);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogicaPantalla.personalizarIntentVistaPrincipal(ContactoItalkYouActivity.this, Const.PANTALLA_CONTACTO, ChatMensajeActivity.class.getSimpleName());
    }
}
