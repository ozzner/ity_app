package com.italkyou.beans.entradas;

public class EntradaCambiarClave {
	
	private String anexo;
	private String claveAntigua;
	private String claveNueva;
	private String confirmaClave;
	private String c_ok;
	
	public EntradaCambiarClave(){
		this.anexo = "";
		this.claveAntigua = "";
		this.claveNueva = "";
		this.confirmaClave = "";
	}
	
	
	public String getC_ok() {
		return c_ok;
	}
	public void setC_ok(String c_ok) {
		this.c_ok = c_ok;
	}
	public String getAnexo() {
		return anexo;
	}
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	public String getClaveAntigua() {
		return claveAntigua;
	}
	public void setClaveAntigua(String claveAntigua) {
		this.claveAntigua = claveAntigua;
	}
	public String getClaveNueva() {
		return claveNueva;
	}
	public void setClaveNueva(String claveNueva) {
		this.claveNueva = claveNueva;
	}
	public String getConfirmaClave() {
		return confirmaClave;
	}
	public void setConfirmaClave(String confirmaClave) {
		this.confirmaClave = confirmaClave;
	}
	

}
