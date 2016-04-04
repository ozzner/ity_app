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

import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

public class DialogoMensajeSMS extends DialogFragment implements OnClickListener{
	
	private EditText etMensaje;
	private Button btnEnviar;
	public onMensajeSMSListener mlistener;
	
	public interface onMensajeSMSListener {  
		  void setMensajeSMSListener(String texto);  
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		 final Dialog dialog = new Dialog(getActivity());  
		 dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		 dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		 dialog.setContentView(R.layout.enviar_sms);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.show();  
		 TextView titulo = (TextView)dialog.findViewById(R.id.tvTituloDialogo);
		 titulo.setText(getActivity().getString(R.string.titulo_sms));
		 etMensaje = (EditText)dialog.findViewById(R.id.etMensajeSMS);
		 btnEnviar = (Button)dialog.findViewById(R.id.btnEnviarSMS);
		 btnEnviar.setOnClickListener(this);
		 return dialog;
	     
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String mensaje = etMensaje.getText().toString();
		if (mensaje.trim().equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msj_error_enviar_sms));
		}else{
			mlistener.setMensajeSMSListener(mensaje.replace(Const.ESPACIO_BLANCO, Const.ESPACIO_BLANCO_URL));
			dismiss();
		}
	}

}
