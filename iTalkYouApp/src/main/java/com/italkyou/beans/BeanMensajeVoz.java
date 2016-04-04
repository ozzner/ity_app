package com.italkyou.beans;

import java.io.Serializable;

public class BeanMensajeVoz extends BeanBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID_Mensajes_Voz;
	private String Fecha;
	private String Escuchado;
	private String Quien_Llama;
	private String Nombre_Contacto;
	private String Nombre_Archivo;
	private String Estado;
	private String Ruta;
	private String Descripcion;
	
	
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getID_Mensajes_Voz() {
		return ID_Mensajes_Voz;
	}
	public void setID_Mensajes_Voz(String iD_Mensajes_Voz) {
		ID_Mensajes_Voz = iD_Mensajes_Voz;
	}
	public String getFecha() {
		return Fecha;
	}
	public void setFecha(String fecha) {
		Fecha = fecha;
	}
	public String getEscuchado() {
		return Escuchado;
	}
	public void setEscuchado(String escuchado) {
		Escuchado = escuchado;
	}
	public String getQuien_Llama() {
		return Quien_Llama;
	}
	public void setQuien_Llama(String quien_Llama) {
		Quien_Llama = quien_Llama;
	}
	public String getNombre_Contacto() {
		return Nombre_Contacto;
	}
	public void setNombre_Contacto(String nombre_Contacto) {
		Nombre_Contacto = nombre_Contacto;
	}
	public String getNombre_Archivo() {
		return Nombre_Archivo;
	}
	public void setNombre_Archivo(String nombre_Archivo) {
		Nombre_Archivo = nombre_Archivo;
	}
	public String getEstado() {
		return Estado;
	}
	public void setEstado(String estado) {
		Estado = estado;
	}
	public String getRuta() {
		return Ruta;
	}
	public void setRuta(String ruta) {
		Ruta = ruta;
	}

}
