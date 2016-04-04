package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class SalidaUsuario extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String Anexo;
	
	public String getAnexo() {
		return Anexo;
	}
	public void setAnexo(String anexo) {
		Anexo = anexo;
	}
	
	

}
