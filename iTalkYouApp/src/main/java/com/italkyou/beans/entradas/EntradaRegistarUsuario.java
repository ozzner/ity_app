package com.italkyou.beans.entradas;

import com.italkyou.beans.salidas.SalidaPin;

import java.io.Serializable;

public class EntradaRegistarUsuario implements Serializable{

	private static final long serialVersionUID = 1L;

	private String idioma;
	private String idPais;
	private String zonaHoraria;
	private String telefono;
	private String clave;
	private String nombre;
	private String correo;
	private String idPrefijo;
	private SalidaPin valorPin;
	private String anexo;
	
	public String getAnexo() {
		return anexo;
	}

	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}

	public SalidaPin getValorPin() {
		return valorPin;
	}

	public void setValorPin(SalidaPin valorPin) {
		this.valorPin = valorPin;
	}

	public String getIdPrefijo() {
		return idPrefijo;
	}

	public void setIdPrefijo(String idPrefijo) {
		this.idPrefijo = idPrefijo;
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

	public String getZonaHoraria() {
		return zonaHoraria;
	}

	public void setZonaHoraria(String zonaHoraria) {
		this.zonaHoraria = zonaHoraria;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
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

	public EntradaRegistarUsuario(){
		this.clave = "";
		this.correo = "";
		this.idioma = "";
		this.nombre = "";
		this.idPais = "";
		this.telefono = "";
		this.zonaHoraria = "";
	}

	@Override
	public String toString(){
		String chain;

		chain =  "\n\n";
		chain +=  "+-------------------- EntradaRegistarUsuario --------------------\n";
		chain += "| idioma: " +idioma+ "\n";
		chain += "| idPais: " + idPais + "\n";
		chain += "| zonaHoraria: " +zonaHoraria+ "\n";
		chain += "| telefono: " +telefono+ "\n";
		chain += "| clave: " +clave+ "\n";
		chain += "| nombre: " +nombre+ "\n";
		chain += "| correo: " +correo+ "\n";
		chain += "| idPrefijo: " +idPrefijo+ "\n";
		chain += "| valorPin: " +valorPin+ "\n";
		chain += "| anexo: " +anexo+ "\n";
		chain += "+-------------------- EntradaRegistarUsuario --------------------\n";

		return chain;
	}

}
