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

public class DialogoComprarSaldo extends DialogFragment implements OnClickListener{
	
	private EditText etMonto;
	private Button btnEnviar;
	public onPayPalListener mlistener;
	
	public interface onPayPalListener {
		  void setPagarListener(String monto);  
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		 final Dialog dialog = new Dialog(getActivity());  
		 dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		 dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		 dialog.setContentView(R.layout.comprar_saldo);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.show();

		 TextView titulo = (TextView)dialog.findViewById(R.id.tvTituloDialogo);
		 titulo.setText(getActivity().getString(R.string.titulo_comprar_saldo));

		 etMonto = (EditText)dialog.findViewById(R.id.etMontoSaldo);
		 btnEnviar = (Button)dialog.findViewById(R.id.btnPayPal);
		 btnEnviar.setOnClickListener(this);
		 return dialog;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String monto = etMonto.getText().toString().trim();

		if (monto.equals(Const.cad_vacia)){
			AppUtil.MostrarMensaje(getActivity(), getString(R.string.msjis_error_falta_monto));
		}else{
			mlistener.setPagarListener(monto);
			dismiss();
		}
	}
}
