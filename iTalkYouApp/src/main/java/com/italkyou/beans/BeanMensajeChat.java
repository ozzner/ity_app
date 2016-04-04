package com.italkyou.beans;

import android.graphics.Bitmap;

import com.italkyou.utils.ChatITY;
import com.parse.ParseObject;

import java.io.Serializable;

public class BeanMensajeChat implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ParseObject chatMessage;
	private String mensaje;
	private boolean isEnviado;
	private String fecha;
	private ParseObject propietarioMsj;
	private String tipo; // tipo de mensaje texto o photo
	private Bitmap photo;
	private byte[] dataFile;
	
	public BeanMensajeChat(String mensaje, String fecha, boolean enviado){
		super();
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.isEnviado = enviado;
	}
	
	public BeanMensajeChat(String mensaje, String fecha, boolean enviado, ParseObject propietario){
		super();
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.isEnviado = enviado;
		this.propietarioMsj = propietario;
		this.tipo = ChatITY.mensaje_texto;
	}
	
	public BeanMensajeChat(Bitmap photo,byte[] bytes, String fecha, boolean enviado, ParseObject propietario,ParseObject object){
		super();
		this.photo = photo;
		this.dataFile = bytes;
		this.fecha = fecha;
		this.isEnviado = enviado;
		this.propietarioMsj = propietario;
		this.tipo = ChatITY.mensaje_archivo;
		this.chatMessage = object;
	}


	
	public ParseObject getPropietarioMsj() {
		return propietarioMsj;
	}
	public void setPropietarioMsj(ParseObject propietarioMsj) {
		this.propietarioMsj = propietarioMsj;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public boolean isEnviado() {
		return isEnviado;
	}
	public void setEnviado(boolean isEnviado) {
		this.isEnviado = isEnviado;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public byte[] getDataFile() {
		return dataFile;
	}

	public void setDataFile(byte[] dataFile) {
		this.dataFile = dataFile;
	}

	public void setIsEnviado(boolean isEnviado) {
		this.isEnviado = isEnviado;
	}

	public ParseObject getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(ParseObject chatMessage) {
		this.chatMessage = chatMessage;
	}
}
