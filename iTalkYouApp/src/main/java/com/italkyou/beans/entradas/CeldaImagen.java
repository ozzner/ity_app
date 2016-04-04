package com.italkyou.beans.entradas;

public class CeldaImagen {

	private int idImagen;
	private String textoCelda;
	private String descripcion;
	
	public CeldaImagen(){
		this.idImagen = 0;
		this.textoCelda = "";
		this.descripcion = "";
	}

	public int getIdImagen() {
		return idImagen;
	}

	public void setIdImagen(int idImagen) {
		this.idImagen = idImagen;
	}

	public String getTextoCelda() {
		return textoCelda;
	}

	public void setTextoCelda(String textoCelda) {
		this.textoCelda = textoCelda;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
