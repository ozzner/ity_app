package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class OutputContact extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String Nombre;
	private String Celular;
	private String Anexo;
	private String Pais;
	private int Flag;
		
	public OutputContact(){
		this.Nombre = "";
		this.Celular = "";
		this.Anexo = "";
		//this.Flag = 0;
		this.Pais = "";
	}
	
	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		Nombre = nombre;
	}

	public String getCelular() {
		return Celular;
	}

	public void setCelular(String celular) {
		Celular = celular;
	}

	public String getAnexo() {
		return Anexo;
	}

	public void setAnexo(String anexo) {
		Anexo = anexo;
	}

	public String getPais() {
		return Pais;
	}

	public void setPais(String pais) {
		Pais = pais;
	}

	public int getFlag() {
		return Flag;
	}

	public void setFlag(int flag) {
		Flag = flag;
	}
	
}
