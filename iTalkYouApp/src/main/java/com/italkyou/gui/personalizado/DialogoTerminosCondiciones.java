package com.italkyou.gui.personalizado;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.italkyou.gui.R;

public class DialogoTerminosCondiciones extends DialogFragment {
	
	
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		 final Dialog dlgTerminosCondiciones = new Dialog(getActivity());
		 dlgTerminosCondiciones.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 dlgTerminosCondiciones.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 dlgTerminosCondiciones.setContentView(R.layout.terminos_condiciones);
		 dlgTerminosCondiciones.show();

		 TextView tvTituloTerminosCondiciones = (TextView)dlgTerminosCondiciones.findViewById(R.id.tvTituloDialogo);
		 tvTituloTerminosCondiciones.setText(getActivity().getString(R.string.titulo_terminos_condiciones));

		 return dlgTerminosCondiciones;
	     
	}

}
