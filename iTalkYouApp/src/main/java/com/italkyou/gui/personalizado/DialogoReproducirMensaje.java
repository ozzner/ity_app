package com.italkyou.gui.personalizado;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.italkyou.beans.AppiTalkYou;
import com.italkyou.beans.BeanMensajeVoz;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.salidas.SalidaResultado;
import com.italkyou.conexion.ExecuteRequest;
import com.italkyou.conexion.ExecuteRequest.ResultadoOperacionListener;
import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;
import com.italkyou.utils.LogApp;

import java.io.IOException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DialogoReproducirMensaje extends DialogFragment implements OnClickListener{
	
	private BeanMensajeVoz mensaje;
	private ImageButton btnAccion;
	private ImageButton btnCerrar;
	private boolean detener;
	private boolean modificoEstado = false;
	private static MediaPlayer mPlayer;
	public onEstadoEscuchadoListener mlistener;
	
	public interface onEstadoEscuchadoListener {  
		  void setEscuchadoListener(boolean estado);  
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		 mensaje =  (BeanMensajeVoz)getArguments().getSerializable(Const.DATOS_MENSAJE);
		 final Dialog dialog = new Dialog(getActivity());  
		 dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		 dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		 dialog.setContentView(R.layout.reproducir_mensaje_voz);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.setCancelable(false);
		 dialog.show();  
		 TextView titulo = (TextView)dialog.findViewById(R.id.tvTituloDialogo);
		 titulo.setText(getActivity().getString(R.string.titulo_reproducir_mensaje));
		 TextView tvContacto = (TextView)dialog.findViewById(R.id.tvDatosMensaje);
		 tvContacto.setText(mensaje.getNombre_Contacto() + "  -  "+mensaje.getQuien_Llama());
		 btnAccion = (ImageButton )dialog.findViewById(R.id.btnAccion);
		 btnAccion.setOnClickListener(this);
		 btnCerrar = (ImageButton)dialog.findViewById(R.id.btnCerrar);
		 btnCerrar.setOnClickListener(this);
		 return dialog;
	     
	}
	
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		detener = false;
		reproducirMensajeVoz();
	}
	
	private void reproducirMensajeVoz() {
		// TODO Auto-generated method stub
		String url = mensaje.getRuta();
		LogApp.log("[DIALOGOREPRODUCIRMENSAJE] ruta mensaje "+url);
		//url = "http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3"; 
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
		} catch (SecurityException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
		} catch (IllegalStateException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
		} catch (IOException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
		}
		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
			btnAccion.setImageResource(0);
			btnAccion.setImageResource(R.drawable.audio_play);
			detener = false;
		} catch (IOException e) {
			Crouton.showText(getActivity(), getString(R.string.msj_error_reproducir_mensaje), Style.ALERT);
			detener = false;
			btnAccion.setImageResource(0);
			btnAccion.setImageResource(R.drawable.audio_play);
		}
		detener = true;
		btnAccion.setImageResource(0);
		btnAccion.setImageResource(R.drawable.audio_stop);
		mPlayer.start();
		if (mensaje.getEscuchado().equals(Const.mensaje_no_escuchado)){
			modificarEstadoMensajeVoz(mensaje.getID_Mensajes_Voz());
		}
		
		
	}

	private void modificarEstadoMensajeVoz(String idMensaje){
		if (AppUtil.existeConexionInternet(getActivity())){
			final ProgressDialog pd = ProgressDialog.show(getActivity(), Const.TITULO_APP,
					getString(R.string.msj_modificar_estado_escuchado), true, true);
			pd.setCanceledOnTouchOutside(false);
			pd.setCancelable(false);
			AppiTalkYou app = (AppiTalkYou)getActivity().getApplication();
			ExecuteRequest ejecutar = new ExecuteRequest(new ResultadoOperacionListener() {
				
				@Override
				public void onOperationDone(BeanRespuestaOperacion respuesta) {
					// TODO Auto-generated method stub
					pd.dismiss();
					if (respuesta.getError().equals(Const.cad_vacia)){
						SalidaResultado salida = (SalidaResultado)respuesta.getObjeto();
						if (salida.getResultado().equals(Const.RESULTADO_OK)){
							modificoEstado = true;
							Crouton.showText(getActivity(), getString(R.string.msj_estado_escuchado_exito), Style.INFO);
						}else{
							Crouton.showText(getActivity(), getString(R.string.msj_error_estado_escuchado), Style.ALERT);
						}
					}else{
						Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_ws), Style.ALERT);
					}
				}
			});
			ejecutar.modificarEstadoEscuchado(idMensaje, "687642", app.getUsuario().getO_ck());
		}else{
			Crouton.showText(getActivity(), getString(R.string.msj_error_conexion_internet), Style.ALERT);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v==btnAccion){
			if (detener){
				detenerMensajeVoz();
			}else{
				reproducirMensajeVoz();
			}
		}else if (v== btnCerrar){
			Log.i("Intico", "boton cerrar");
			if(mPlayer!=null && mPlayer.isPlaying()){
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			}
			mlistener.setEscuchadoListener(modificoEstado);
			dismiss();
		}
	}
	
	private void detenerMensajeVoz() {
		// TODO Auto-generated method stub
		if(mPlayer!=null && mPlayer.isPlaying()){
			mPlayer.stop();
		}
		detener = false;
		btnAccion.setImageResource(0);
		btnAccion.setImageResource(R.drawable.audio_play);
	}
}
