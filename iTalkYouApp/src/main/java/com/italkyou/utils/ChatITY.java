package com.italkyou.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ChatITY {

	public static final String USER_EMAIL = "email";
	public static final String TABLE_USER = "Usuarios";
	public static String tabla_chats = "Chats";
	public static String tabla_mensajes = "MensajesChat";
	/**usuario**/
	public static final String USER_USER = "Usuario";
	public static final String USER_STATUS = "Estado";
	public static final String USER_ANNEX = "Anexo";
	public static final String USER_IMAGE = "ImagenUsuario";
	public static final String USER_PHONE = "Telefono";
	
	/**chat o salas de conversacion**/
	public static String identificador_nombre_chat = "NombreChat";
	public static String identificador_admin = "Administrador";
	public static String identificador_tipo = "Tipo";
	public static String identificador_miembros_id = "MiembrosId";
	public static String identificador_miembros = "Miembros";
	public static String identificador_miembros_nombre = "MiembrosNombre";
	public static String identificador_ultimo_mensaje = "UltimoMensaje";
	public static String identificador_imagen_chat = "ImagenChat";
	public static final String USER_FLAG_IMAGE = "ExisteImagen";
	
	/**mensajes**/
	public static String identificador_idchat = "IdemChat";
	public static String identificador_mensaje = "Mensaje";
	public static String identificador_envio_mensaje = "UsuarioEnvio";
	public static String identificador_archivo = "Archivo";
	
	/**Notificacion de mensajes**/
	public static String json_mensaje = "mensaje";
	public static String json_accion = "action";
	public static String json_chatId = "chatId";
	public static String json_nombre = "nombre";
	
	/**general**/
	public static String identificador_createdAt = "createdAt";
	public static String identificador_updatedAt = "updatedAt";
	public static String identificador_objectoId = "objectId";
	
	public static String tipo_privado = "PRIVADO";
	public static String tipo_grupal = "GRUPAL";
	
	public static String mensaje_texto = "TEXTO";
	public static String mensaje_archivo = "ARCHIVO";
	
	public static String texto_imagen = "Imagen";
	
	public static String prefijo_chat = "chat_";
	public static String tipo_dispositivo = "deviceType";
	public static String so_dispositivo = "android";
	
	@SuppressLint("SimpleDateFormat")
	public static String formatoFecha(Date fecha){
		String valor = "";
        SimpleDateFormat df;

        if (AppUtil.obtenerIdiomaLocal() ==  Const.IDIOMA_ES)
            df= new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        else
            df= new SimpleDateFormat("ddd MMM dd yyyy HH:mm", Locale.ENGLISH);


        String fechaMensaje = df.format(fecha);
	    
	    /**fecha actual**/
	    Calendar cal = new GregorianCalendar();
	    Date date = cal.getTime();
	    String fechaActual = df.format(date);
	    
	    if (fechaActual.equals(fechaMensaje)){
	    	 df = new SimpleDateFormat("HH:mm");
	    	valor = df.format(fecha);
	    }else{
	    	df = new SimpleDateFormat("dd MMM yyyy HH:mm");
	    	valor = df.format(fecha);
	    }
		return valor;
	}

	
}
