package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class SalidaDatos extends BeanBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String NroVMail;
	private String NroInvitaciones;
	private String Saldo;
	
	public SalidaDatos(){
		this.NroInvitaciones = "0";
		this.NroVMail = "0";
		this.Saldo = "0.0000";
	}
	public String getNroVMail() {
		return NroVMail;
	}
	public void setNroVMail(String nroVMail) {
		NroVMail = nroVMail;
	}
	public String getNroInvitaciones() {
		return NroInvitaciones;
	}
	public void setNroInvitaciones(String nroInvitaciones) {
		NroInvitaciones = nroInvitaciones;
	}
	public String getSaldo() {
		return Saldo;
	}
	public void setSaldo(String saldo) {
		Saldo = saldo;
	}
	
	

}
