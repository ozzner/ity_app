package com.italkyou.gui.personalizado;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.italkyou.beans.entradas.EntradaCambiarClave;
import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

public class DialogoCambiarClave  extends DialogFragment implements OnClickListener{
	
	private EditText etClaveActual;
	private EditText etClaveNueva;
	private EditText etClaveConfirmar;
	private Button btnEnviar;
	public onCambiarClave mlistener;
	
	public interface onCambiarClave{
		void setCambiarClaveListener(EntradaCambiarClave entrada);
	}
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		 final Dialog dialog = new Dialog(getActivity());  
		 dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		 dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		 dialog.setContentView(R.layout.cambiar_clave);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.show();  
		 TextView titulo = (TextView)dialog.findViewById(R.id.tvTituloDialogo);
		 titulo.setText(getActivity().getString(R.string.titulo_cambiar_clave));
		 etClaveActual = (EditText)dialog.findViewById(R.id.etClaveActual);
		 etClaveNueva = (EditText)dialog.findViewById(R.id.etClaveNueva);
		 etClaveConfirmar = (EditText)dialog.findViewById(R.id.etClaveConfirmar);
		 btnEnviar = (Button)dialog.findViewById(R.id.btnEnviarClave);
		 btnEnviar.setOnClickListener(this);
		 return dialog;
	     
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String claveActual = etClaveActual.getText().toString().trim();
		String claveNueva = etClaveNueva.getText().toString().trim(); 
		String claveConfirmar = etClaveConfirmar.getText().toString().trim();
		if (claveActual.equals(Const.cad_vacia) && claveNueva.equals(Const.cad_vacia) && claveConfirmar.equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjcp_error_falta_datos_clave));
		}else if (claveActual.equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjcp_error_falta_clave_actual));
		}else if (claveNueva.equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjcp_error_falta_clave_nueva));
		}else if (claveConfirmar.equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjcp_error_falta_clave_confirmar));
		}else if (!claveNueva.equals(claveConfirmar)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjrc_error_claves_diferente));
		}else{
			EntradaCambiarClave entrada = new EntradaCambiarClave();
			entrada.setClaveAntigua(claveActual);
			entrada.setClaveNueva(claveNueva);
			entrada.setConfirmaClave(claveConfirmar);
			mlistener.setCambiarClaveListener(entrada);
			dismiss();
		}
		
	}

}
