package com.italkyou.gui.personalizado;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.italkyou.beans.entradas.CeldaImagen;
import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;
import com.italkyou.utils.Const;

import java.util.List;

public class DialogoLista extends DialogFragment implements OnItemClickListener{

	private List<Object> listaOpciones;
	public String titulo;
	public String pantalla;
	public String flag;
	public onSeleccionarOpcionListener onSeleccionarOpcionListener;
	
	public interface onSeleccionarOpcionListener {  
		  void setSeleccionarOpcionListener(String texto);  
	} 
	
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
	    personalizarDialogo(dialog);
        inicializarDialogoOpciones(dialog);

		return dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

		String valor = ((CeldaImagen)listaOpciones.get(pos)).getDescripcion();
		onSeleccionarOpcionListener.setSeleccionarOpcionListener(valor);
		dismiss();
	}

    private void personalizarDialogo(Dialog dlgOpciones){

        dlgOpciones.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dlgOpciones.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dlgOpciones.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dlgOpciones.setContentView(R.layout.dialogo_listado);
        dlgOpciones.setCanceledOnTouchOutside(false);
        dlgOpciones.show();
    }

    private void inicializarDialogoOpciones(Dialog dialogo){
        TextView tvTitulo = (TextView)dialogo.findViewById(R.id.tvTituloDialogo);
        tvTitulo.setText(titulo);

        ListView lstOpciones = (ListView)dialogo.findViewById(R.id.dialoglist);
        lstOpciones.setOnItemClickListener(this);

        if (pantalla.equals(Const.PANTALLA_PERFIL))
            listaOpciones = AppUtil.obtenerListaImagen(getActivity());

        else if (pantalla.equals(Const.PANTALLA_CONTACTO))
            listaOpciones = AppUtil.obtenerListaLlamadas(getActivity(),flag);

        else if (pantalla.equals(Const.PANTALLA_HISTORIAL_LLAMADAS))
            listaOpciones = AppUtil.obtenerListaOpcionesLlamada(getActivity());

        else if (pantalla.equals(Const.PANTALLA_HISTORIAL_MENSAJES))
            listaOpciones = AppUtil.obtenerListaOpcionesMensaje(getActivity());

        AdaptadorLista adpLstOpciones = new AdaptadorLista(getActivity(), R.layout.celda_imagen, listaOpciones, CeldaImagen.class.getSimpleName());
        lstOpciones.setAdapter(adpLstOpciones);
    }
}
