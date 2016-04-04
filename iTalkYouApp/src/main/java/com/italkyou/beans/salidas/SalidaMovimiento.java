package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class SalidaMovimiento extends BeanBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID_Movimiento; 
	private String Fecha; 
	private String Descripcion;
	private String Monto;
	
	public String getID_Movimiento() {
		return ID_Movimiento;
	}
	public void setID_Movimiento(String iD_Movimiento) {
		ID_Movimiento = iD_Movimiento;
	}
	public String getFecha() {
		return Fecha;
	}
	public void setFecha(String fecha) {
		Fecha = fecha;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getMonto() {
		return Monto;
	}
	public void setMonto(String monto) {
		Monto = monto;
	} 

}
