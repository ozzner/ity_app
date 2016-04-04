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
import com.italkyou.utils.Const;
import com.italkyou.utils.AppUtil;

public class DialogoEnviarCorreo extends DialogFragment implements OnClickListener{
	
	private EditText etCorreo;
	private Button btnEnviar;
	public onEnviarCorreoListener mlistener;
	
	public interface onEnviarCorreoListener {  
		  void setEnviarCorreoListener(String texto);  
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		 final Dialog dialog = new Dialog(getActivity());  
		 dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		 dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		 dialog.setContentView(R.layout.enviar_correo);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.show();  
		 TextView titulo = (TextView)dialog.findViewById(R.id.tvTituloDialogo);
		 titulo.setText(getActivity().getString(R.string.titulo_recuperar_clave));
		 etCorreo = (EditText)dialog.findViewById(R.id.etCorreo);
		 btnEnviar = (Button)dialog.findViewById(R.id.btnEnviarCorreo);
		 btnEnviar.setOnClickListener(this);
		 return dialog;
	     
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String email = etCorreo.getText().toString();
		if (email.trim().equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjis_error_falta_email));
		}else{
			mlistener.setEnviarCorreoListener(email);
			dismiss();
		}
	}

}
