package com.italkyou.beans.entradas;

public class EntradaEnviarSMS {
	private String idioma;
	private String idUsuario;
	private String celular;
	private String mensaje;
	private String fecha;
	private String hora;
	private String minuto;
	private String agendar; //0 o 1
	
	public EntradaEnviarSMS(){
		this.idioma = "";
		this.idUsuario = "";
		this.mensaje = "";
		this.celular = "";
		this.fecha = "";
		this.hora = "";
		this.minuto = "";
		this.agendar = "0";
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getMinuto() {
		return minuto;
	}

	public void setMinuto(String minuto) {
		this.minuto = minuto;
	}

	public String getAgendar() {
		return agendar;
	}

	public void setAgendar(String agendar) {
		this.agendar = agendar;
	}

	
	
}
