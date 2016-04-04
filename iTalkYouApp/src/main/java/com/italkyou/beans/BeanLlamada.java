package com.italkyou.beans;

import java.io.Serializable;

public class BeanLlamada extends BeanBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID_Movimiento;
	private String Telefono;
	private String Nro_Origen;
	private String Nombre_Origen;
	private String Nro_Destino;
	private String Nombre_Destino;
	private String Fecha;
	private String Descripcion;
	private String Monto;
	private String Tipo_llamada;
	
	
	public String getTipo_Llamada() {
		return Tipo_llamada;
	}
	public void setTipo_Llamada(String tipo_Llamada) {
		Tipo_llamada = tipo_Llamada;
	}
	public String getID_Movimiento() {
		return ID_Movimiento;
	}
	public void setID_Movimiento(String iD_Movimiento) {
		ID_Movimiento = iD_Movimiento;
	}
	public String getTelefono() {
		return Telefono;
	}
	public void setTelefono(String telefono) {
		Telefono = telefono;
	}
	public String getNro_Origen() {
		return Nro_Origen;
	}
	public void setNro_Origen(String nro_Origen) {
		Nro_Origen = nro_Origen;
	}
	public String getNombre_Origen() {
		return Nombre_Origen;
	}
	public void setNombre_Origen(String nombre_Origen) {
		Nombre_Origen = nombre_Origen;
	}
	public String getNro_Destino() {
		return Nro_Destino;
	}
	public void setNro_Destino(String nro_Destino) {
		Nro_Destino = nro_Destino;
	}
	public String getNombre_Destino() {
		return Nombre_Destino;
	}
	public void setNombre_Destino(String nombre_Destino) {
		Nombre_Destino = nombre_Destino;
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
