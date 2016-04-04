package com.italkyou.gui.contactos;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanContact;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.entradas.EntradaEnviarSMS;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.controladores.LogicTelephone;
import com.italkyou.controladores.LogicaPantalla;
import com.italkyou.controladores.LogicaUsuario;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.gui.personalizado.AdaptadorLista;
import com.italkyou.gui.personalizado.CustomAlertDialog;
import com.italkyou.gui.personalizado.DialogoLista;
import com.italkyou.gui.personalizado.DialogoLista.onSeleccionarOpcionListener;
import com.italkyou.gui.personalizado.DialogoMensajeSMS;
import com.italkyou.gui.personalizado.DialogoMensajeSMS.onMensajeSMSListener;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import java.io.InputStream;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ContactoActivity extends BaseActivity implements OnItemClickListener {//ActionBarActivity {

    private BeanContact contacto;
    private List<Object> listaTelefonos;
    private Context _context;
    private AppiTalkYou app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_contacto);
        this.tipoMenu = Const.MENU_VACIA;
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        app = ((AppiTalkYou) getApplication());
//        contacto = (BeanContact) this.getIntent().getSerializableExtra(Const.DATOS_CONTACTO);
        contacto = app.getCurrentContact();
        _context = getApplicationContext();
        app = (AppiTalkYou) getApplication();
        iniciarComponentes();
    }

    private void iniciarComponentes() {

        ImageView imagen = (ImageView) findViewById(R.id.imgFotoContacto);

        if (contacto.getFoto() > 0) {
            Uri.Builder newUriBuilder = ContactsContract.Contacts.CONTENT_LOOKUP_URI.buildUpon();
            Uri uri = newUriBuilder.appendPath(contacto.getLookUpKey()).build();

            InputStream input;
            input = AppUtil.openDisplayPhoto(getApplicationContext(), uri);

            if (input != null) {
                imagen.setImageBitmap(BitmapFactory.decodeStream(input));
                imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
// 				imagen.setBackgroundResource(R.drawable.foto_contacto);
            }

        } else {
//				imagen.setBackgroundResource(R.drawable.foto_contacto);
        }

        TextView tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvNombre.setText(contacto.getNombre());
//		listaTelefonos= LogicContact.obtenerListadoTelefono(getApplicationContext(), contacto.getIdContacto() + "");
        listaTelefonos = (LogicTelephone.getTelephonesByKey(_context, contacto.getLookUpKey()));

        ListView lista = (ListView) findViewById(R.id.listNumerosTelefonicos);
        AdaptadorLista adaptador = new AdaptadorLista(getApplicationContext(), R.layout.celda_dato, listaTelefonos, BeanTelefono.class.getSimpleName());
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int posicion, long id) {

        final BeanTelefono telefono = (BeanTelefono) listaTelefonos.get(posicion);
        String flag;

        if (telefono.getAnexo().equals(Const.CARACTER_VACIO))
            flag = Const.NO_USER_ITY;
        else
            flag = Const.USER_ITY;

        FragmentManager fm = getSupportFragmentManager();
        DialogoLista dlgOpcionesContacto = new DialogoLista();


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

                if (texto.equals(Const.descripcion_llamada_gratis)) {
                    LogicaPantalla.personalizarIntentRealizarLlamada("", ContactoActivity.this, Const.tipo_llamada_anexoVOIP, telefono.getAnexo(), contacto, "0", "");

                } else if (texto.equals(Const.descripcion_llamada_pago)) {

                    if (dBalance > 0)
                        LogicaPantalla.personalizarIntentRealizarLlamada("", ContactoActivity.this, Const.tipo_llamada_internacional, telefono.getNumero(), contacto, "2", "");
                    else {
                        String message = getString(R.string.message_alert_balance_none);
                        CustomAlertDialog.showSingleAlert(ContactoActivity.this, message);
                    }

                } else if (texto.equals(Const.descripcion_llamada_sms)) {
                    if (dBalance > 0)
                        cargarDialogoSMS(telefono.getNumero());
                    else {
                        String message = getString(R.string.message_alert_balance_sms);
                        CustomAlertDialog.showSingleAlert(ContactoActivity.this, message);
                    }

                }
            }

        };

        dlgOpcionesContacto.onSeleccionarOpcionListener = listener;
        dlgOpcionesContacto.titulo = getString(R.string.titulo_seleccionar);
        dlgOpcionesContacto.pantalla = Const.PANTALLA_CONTACTO;
        dlgOpcionesContacto.flag = flag;
        dlgOpcionesContacto.show(fm, "");
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

        final ProgressDialog pd = ProgressDialog.show(ContactoActivity.this, Const.TITULO_APP,
                getString(R.string.msj_enviando_sms), true, true);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        final EntradaEnviarSMS entrada = new EntradaEnviarSMS();
        entrada.setCelular(numero);
        entrada.setMensaje(mensaje);
        entrada.setAgendar(Const.SMS_NO_AGENDAR);
        entrada.setFecha(AppUtil.obtenerFecha());
        entrada.setHora(AppUtil.obtenerHora());
        entrada.setMinuto(AppUtil.obtenerMinutos());

        if (AppUtil.existeConexionInternet(ContactoActivity.this)) {

            new Thread(new Runnable() {
                public void run() {
                    ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

                        @Override
                        public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {

                            if (respuesta.getError().equals(Const.cad_vacia)) {
                                SalidaResultado resultado = (SalidaResultado) respuesta.getObjeto();

                                if (resultado.getResultado().equals(Const.RESULTADO_OK)) {
                                    pd.dismiss();
                                    Crouton.showText(ContactoActivity.this, getString(R.string.msj_enviar_sms), Style.CONFIRM);
                                } else {
                                    pd.dismiss();
                                    Crouton.showText(ContactoActivity.this, getString(R.string.msj_error_enviar_sms), Style.ALERT);
                                }

                            } else {
                                pd.dismiss();
                                Crouton.showText(ContactoActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
                            }
                        }

                    });
                    ejecutar.enviarSMS(entrada, app.getUsuario());
                }
            }).start();

        } else {
            Crouton.showText(ContactoActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
        }
    }
}
