package com.italkyou.gui.menu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.beans.entradas.EntradaEnviarSMS;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExcecuteRequest;
import com.italkyou.conexion.ExcecuteRequest.ResultadoOperacionListener;
import com.italkyou.gui.BaseActivity;
import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AgendarSMSActivity extends BaseActivity implements OnClickListener, OnDateChangedListener, OnTimeChangedListener {

	private EditText etNumero, etMensaje;
	private Button btnEnviar;
	private DatePicker dtpFecha;
	private TimePicker tpHora;
	private String fecha;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agendar_sms);
		this.tipoMenu = Const.MENU_VACIA;
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		inicializarComponentes();
	}

	private void inicializarComponentes() {
		// TODO Auto-generated method stub
		etNumero = (EditText)findViewById(R.id.etTelefonoAgendar);
		etMensaje = (EditText)findViewById(R.id.etMensajeAgendar);
		btnEnviar = (Button)findViewById(R.id.btnAgendar);
		btnEnviar.setOnClickListener(this);
		dtpFecha = (DatePicker)findViewById(R.id.dtpFechaEnvio);
		tpHora = (TimePicker)findViewById(R.id.tpHora);
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
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		fecha = obtenerFecha(year, monthOfYear, dayOfMonth);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnEnviar){
			String telefono = etNumero.getText().toString().trim();
			String mensaje = etMensaje.getText().toString().trim();
			if (!telefono.equals(Const.cad_vacia)){
				if (!mensaje.equals(Const.cad_vacia)){
					agendarMensaje(telefono, mensaje);
				}else{
					Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_error_falta_mensaje), Style.ALERT);
				}
			}else{
				Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_error_falta_telefono), Style.ALERT);
			}
		}
	}

	private void agendarMensaje(String telefono, String mensaje) {
		// TODO Auto-generated method stub
		AppiTalkYou app = (AppiTalkYou)getApplication();
		BeanUsuario usuario = app.getUsuario();
		EntradaEnviarSMS entrada = new EntradaEnviarSMS();
		entrada.setAgendar(Const.SMS_AGENDAR);
		entrada.setCelular(telefono);
		entrada.setFecha(fecha);
		entrada.setHora(String.valueOf(tpHora.getCurrentHour())); 
		entrada.setMinuto(String.valueOf(tpHora.getCurrentMinute()));
		entrada.setIdioma(usuario.getID_Idioma());
		entrada.setIdUsuario(usuario.getID_Usuario());
		entrada.setMensaje(mensaje);
		if (AppUtil.existeConexionInternet(AgendarSMSActivity.this)){
			pd = ProgressDialog.show(this, Const.TITULO_APP, getString(R.string.msj_enviando_sms), true, true);
			pd.setCanceledOnTouchOutside(false);
			pd.setCancelable(false);
			ExcecuteRequest ejecutar = new ExcecuteRequest(new ResultadoOperacionListener() {

				@Override
				public void onResultadoOperacion(BeanRespuestaOperacion respuesta) {
					// TODO Auto-generated method stub
					if (respuesta.getError().equals(Const.cad_vacia)){
						SalidaResultado resultado = (SalidaResultado)respuesta.getObjeto();
						if (resultado.getResultado().equals(Const.RESULTADO_OK)){
							pd.dismiss();
							Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_enviar_sms), Style.CONFIRM);
						}
						else{
							pd.dismiss();
							Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_error_enviar_sms), Style.ALERT);
						}
					}else{
						pd.dismiss();
						Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_error_conexion_ws), Style.ALERT);
					}
				}
				
			});
			ejecutar.enviarSMS(entrada, app.getUsuario());
		}else
			Crouton.showText(AgendarSMSActivity.this, getString(R.string.msj_error_conexion_internet), Style.ALERT);
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		tpHora.setCurrentHour(hourOfDay);
		tpHora.setCurrentMinute(minute);
	}



	@SuppressLint("SimpleDateFormat")
	private String obtenerFecha(int anho, int mes, int dia){
		GregorianCalendar cal = new GregorianCalendar(anho, mes, dia);
		Date date = cal.getTime();
	    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	    String fecha = df.format(date);
		return fecha;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())
	    { case android.R.id.home:
	    	onBackPressed();
        	break;
        default:
            return super.onOptionsItemSelected(item);
	    }
 
	    return true;
	}

}
