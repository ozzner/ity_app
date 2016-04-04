package com.italkyou.beans;

public class BeanRespuestaOperacion {
	
	private String error;
	private Object objeto;
	private String nombreObjecto;
	
	public String getNombreObjecto() {
		return nombreObjecto;
	}

	public void setNombreObjecto(String nombreObjecto) {
		this.nombreObjecto = nombreObjecto;
	}

	public BeanRespuestaOperacion(){
		this.error = "";
		this.objeto = new Object();
		this.nombreObjecto = "";
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getObjeto() {
		return objeto;
	}

	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}


}
