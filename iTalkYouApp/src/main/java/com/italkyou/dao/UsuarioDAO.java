package com.italkyou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.italkyou.beans.BeanUsuario;
import com.italkyou.utils.Const;

public class UsuarioDAO extends BaseDAO{

	private String USUARIO="TBL_USUARIO";
	private static final String SALDO = "SALDO" ;
	
	public UsuarioDAO(Context contexto) {
		super(contexto);
	}

	public void insertar(BeanUsuario usu){
		ContentValues valores = new ContentValues();
		valores.put("ID_USUARIO", usu.getID_Usuario());
		valores.put("NOMBRES", usu.getNombres());
		valores.put("ANEXO", usu.getAnexo());
		valores.put("DESCRIPCION", usu.getDescripcion());
		valores.put("RUTA_IMAGEN", usu.getImagen_Usuario());
		valores.put("PIN_SIP", usu.getPin_Sip());
		valores.put("PIN_LLAMADA", usu.getPin_Llamada());
		valores.put("ID_IDIOMA", usu.getID_Idioma());
		valores.put("NUMERO", usu.getNumero());
		valores.put("PREFIJO_PAIS", usu.getId_prefijo());
		valores.put("O_CK", usu.getO_ck());
		valores.put("CORREO", usu.getCorreo());
		valores.put("NOTIFICACION", usu.getNotificacion());
		valores.put("CLAVE", usu.getClave());
		valores.put("ESTADO", usu.getEstado());
        valores.put("SALDO",usu.getSaldo());
		getDb().insert(USUARIO, null, valores);
	}
	
	public void modificarInicioSesion(BeanUsuario usu){
		ContentValues cv=new ContentValues();
		cv.put("NOMBRES", usu.getNombres());
		cv.put("DESCRIPCION", usu.getDescripcion());
		cv.put("PIN_SIP", usu.getPin_Sip());
		cv.put("PIN_LLAMADA", usu.getPin_Llamada());
		cv.put("PREFIJO_PAIS", usu.getId_prefijo());
		cv.put("O_CK", usu.getO_ck());
		cv.put("CLAVE", usu.getClave());
		cv.put("ESTADO", usu.getEstado());
		getDb().update(USUARIO, cv, "ANEXO=?", new String[]{usu.getAnexo()});
	}

    public int actualizarSaldo(String anexo, String saldo){
		int row;
		try {
			ContentValues cv=new ContentValues();
			cv.put("SALDO",saldo);
			row = getDb().update(USUARIO,cv,"ANEXO=?",new String[]{anexo});
		}catch (Exception ex){
			row = 0;
		}

		return row;
    }
	
	public void modificarPerfil(BeanUsuario usu){

		ContentValues cv=new ContentValues();
		cv.put("NOMBRES", usu.getNombres());
		cv.put("RUTA_IMAGEN", usu.getImagen_Usuario());
		cv.put("ID_IDIOMA", usu.getID_Idioma());
		cv.put("PREFIJO_PAIS", usu.getId_prefijo());
		cv.put("CORREO", usu.getCorreo());
		cv.put("NOTIFICACION", usu.getNotificacion());
	 getDb().update(USUARIO, cv, "ANEXO=?", new String[]{usu.getAnexo()});

	}
	
	public void setDato(String columna, String dato, String anexo){
		ContentValues cv=new ContentValues();
		cv.put(columna, dato);
		getDb().update(USUARIO, cv, "ANEXO=?", new String[]{anexo});
	}
	
	public BeanUsuario obtenerUsuario(){

		BeanUsuario bUsuario = new BeanUsuario();
        String[] columnasTabla= new String[] {"ID_USUARIO", "NOMBRES", "ANEXO", "DESCRIPCION", "RUTA_IMAGEN", "PIN_SIP", "PIN_LLAMADA", "ID_IDIOMA",
                                              "NUMERO", "PREFIJO_PAIS", "O_CK", "CORREO", "NOTIFICACION", "CLAVE", "ESTADO","SALDO"};

		Cursor cursor = getDb().query(USUARIO,columnasTabla,"ESTADO=?", new String[]{Const.ESTADO_USUARIO_CONECTADO}, null, null, null);

		if (cursor.moveToFirst()) {

			do {

				bUsuario.setID_Usuario(cursor.getString(0));
				bUsuario.setNombres(cursor.getString(1));
				bUsuario.setAnexo(cursor.getString(2));
				bUsuario.setDescripcion(cursor.getString(3));
				bUsuario.setImagen_Usuario(cursor.getString(4));
				bUsuario.setPin_Sip(cursor.getString(5));
				bUsuario.setPin_Llamada(cursor.getString(6));
				bUsuario.setID_Idioma(cursor.getString(7));
				bUsuario.setNumero(cursor.getString(8));
				bUsuario.setId_prefijo(cursor.getString(9));
				bUsuario.setO_ck(cursor.getString(10));
				bUsuario.setCorreo(cursor.getString(11));
				bUsuario.setNotificacion(cursor.getString(12));
				bUsuario.setClave(cursor.getString(13));
				bUsuario.setEstado(cursor.getString(14));
                bUsuario.setSaldo(cursor.getString(15));

			} while (cursor.moveToNext());
		}

			cursor.close();

		return bUsuario;
	}
	
	public int existeUsuarioActivo(){
		int cantidadUsuario;
		Cursor cursor = getDb().query(USUARIO, new String[] {"ID"},"ESTADO=?", new String[]{Const.ESTADO_USUARIO_CONECTADO}, null, null, null);
		cantidadUsuario = cursor.getCount();
		return cantidadUsuario;
	}
	
	public int existeUsuario(String anexo){
		int count;
		Cursor cursor = getDb().query(USUARIO, new String[] {"ID"},"ANEXO=?", new String[]{anexo}, null, null, null);
		count = cursor.getCount();
		return count;
	}
	public void borrarDatos() {
		borrarTodo(USUARIO);
	}

	public Double getBalance(String userID){
		Double dBalance = 0.0;
		Cursor cu;
		cu = getDb().query(USUARIO,new String[]{SALDO}," ID_USUARIO = ?",new String[]{userID},null,null,null);

		if (cu.moveToFirst()){
			String balance = cu.getString(0);
			dBalance = Double.parseDouble(balance);
		}

		if (cu.isClosed())
			cu.close();

		return dBalance;
	}
}
