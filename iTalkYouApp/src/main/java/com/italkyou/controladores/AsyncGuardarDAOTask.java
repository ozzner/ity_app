package com.italkyou.controladores;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.italkyou.beans.BeanPais;
import com.italkyou.beans.BeanRespuestaOperacion;
import com.italkyou.beans.BeanTelefono;
import com.italkyou.beans.BeanUsuario;
import com.italkyou.utils.Const;

import java.util.List;

public class AsyncGuardarDAOTask extends AsyncTask<BeanRespuestaOperacion, Void, String>{

	private static final String TAG = AsyncGuardarDAOTask.class.getSimpleName();
	private Context contexto;

	public AsyncGuardarDAOTask(Context contexto){
		this.contexto = contexto;
	}

	@Override
	protected String doInBackground(BeanRespuestaOperacion... params) {

		BeanRespuestaOperacion dato = params[0];

		if (dato.getNombreObjecto().equals(Const.almacenar_paises)){
			@SuppressWarnings("unchecked")
			List<BeanPais> listado = (List<BeanPais>)dato.getObjeto();
			LogicaPais.guardarListadoPaises(contexto, listado, Const.BD_INSERTAR);

		}else if (dato.getNombreObjecto().equals(Const.almacenar_usuario)){
			BeanUsuario usuario = (BeanUsuario)dato.getObjeto();
			LogicaUsuario.guardarUsuario(contexto, usuario, Const.BD_INSERTAR);

		}
//		else if (dato.getNombreObjecto().equals(Const.almacenar_contactos)){
//			Log.e(TAG,"guardar contactos");
//			@SuppressWarnings("unchecked")
//			List<BeanContact> listado = (List<BeanContact>)dato.getObjeto();
//			LogicContact.guardarListadoContactos(contexto, listado, Const.BD_INSERTAR);
//
//		}
		else if (dato.getNombreObjecto().equals(Const.almacenar_numeros)){
			Log.e(TAG,"guardar numeros");
			@SuppressWarnings("unchecked")
			List<BeanTelefono> listado = (List<BeanTelefono>)dato.getObjeto();
			LogicContact.guardarListadoTelefono(contexto, listado);

		}
		return null;
	}

}
