package com.italkyou.beans;

import com.italkyou.utils.Const;

public class BeanTelefono {
	
	private int idContacto;
	private String nombreIty;
	private String lookUpKey;
	private String numero;
	private String anexo;
	private int posContacto;
	private String conectado;
	
	public BeanTelefono(){
		this.lookUpKey = Const.NONE;
		this.nombreIty = Const.cad_vacia;
		this.numero = "";
		this.anexo = "";
		this.idContacto = 0;
		this.posContacto = 0;
		this.conectado = "0"; 
	}


	public int getIdContacto() {
		return idContacto;
	}

	public void setIdContacto(int idContacto) {
		this.idContacto = idContacto;
	}

	public String getNombreIty() {
		return nombreIty;
	}

	public void setNombreIty(String nombreIty) {
		this.nombreIty = nombreIty;
	}

	public String getLookUpKey() {
		return lookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getAnexo() {
		return anexo;
	}

	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}

	public int getPosContacto() {
		return posContacto;
	}

	public void setPosContacto(int posContacto) {
		this.posContacto = posContacto;
	}

	public String getConectado() {
		return conectado;
	}

	public void setConectado(String conectado) {
		this.conectado = conectado;
	}
}
