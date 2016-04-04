package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SalidaHistorialSaldo extends BeanBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Object> listaMovimientos;
	private String saldoActual;
	
	public SalidaHistorialSaldo(){
		this.listaMovimientos = new ArrayList<Object>();
		this.saldoActual = "0.0000";
	}
	
	public List<Object> getListaMovimientos() {
		return listaMovimientos;
	}
	public void setListaMovimientos(List<Object> listaMovimientos) {
		this.listaMovimientos = listaMovimientos;
	}
	public String getSaldoActual() {
		return saldoActual;
	}
	public void setSaldoActual(String saldoActual) {
		this.saldoActual = saldoActual;
	}
	

}
