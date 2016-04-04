package com.italkyou.beans.entradas;

public class EntradaPerfilUsuario {
	private String idioma;
	private String idPais;
	private String nombre;
	private String correo;
	private String pin;
	private String anexo;
	private String flagImagen;
	private String o_ck;
	
	public EntradaPerfilUsuario(){
		this.idioma = "";
		this.idPais = "";
		this.nombre = "";
		this.correo = "-";
		this.pin = "";
		this.anexo = "";
		this.flagImagen = "0";
		this.o_ck = "";
	}
	
	
	public String getO_ck() {
		return o_ck;
	}
	public void setO_ck(String o_ck) {
		this.o_ck = o_ck;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getIdPais() {
		return idPais;
	}
	public void setIdPais(String idPais) {
		this.idPais = idPais;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getAnexo() {
		return anexo;
	}
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	public String getFlagImagen() {
		return flagImagen;
	}
	public void setFlagImagen(String flagImagen) {
		this.flagImagen = flagImagen;
	}
	
	
}
