package com.italkyou.controladores;

import android.content.Context;

import com.italkyou.beans.BeanPais;
import com.italkyou.dao.PaisDAO;
import com.italkyou.utils.Const;

import java.util.List;

public class LogicaPais {
	
	public static void guardarListadoPaises(Context contexto, List<BeanPais> listado, String tipo){

		PaisDAO lstDao = new PaisDAO(contexto);

		for (int i=0; i<listado.size(); i++ ){
			if (tipo.equals(Const.BD_INSERTAR))
				lstDao.insertarPais(listado.get(i));
			else
				lstDao.modificarPais(listado.get(i));
		}
//		lstDao.cerrar();
	}
	
	public static List<Object> obtenerListadoPaises(Context contexto,String idioma_int){

		PaisDAO lstDao = new PaisDAO(contexto);
		List<Object> lista = lstDao.obtenerListadoPais(idioma_int);
//		lstDao.cerrar();
		return lista;
	}


	public static BeanPais getCountryByZipCode(Context c,String zip){
		PaisDAO dao = new PaisDAO(c);
		BeanPais pais = dao.getCountryByZip(zip);
		return pais;
	}
	
	public static BeanPais obtenerPais (Context contexto, String cmm){
		PaisDAO paisDao = new PaisDAO(contexto);
		BeanPais pais = paisDao.obtenerPais(cmm);
//		paisDao.cerrar();
		return pais;
	}
	
	public static boolean existeListado(Context contexto){
		boolean existe = false;
		PaisDAO lstDao = new PaisDAO(contexto);
		int cont = lstDao.numeroDatos();
		if (cont > 0) existe = true;
//		lstDao.cerrar();
		return existe;
	}
	
	public static void borrarDatos(Context contexto){
		PaisDAO lstDao = new PaisDAO(contexto);
		lstDao.borrarDatos();
//		lstDao.cerrar();
	}

}
