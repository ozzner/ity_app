package com.italkyou.beans;

import java.io.Serializable;

public class BeanUsuario extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String ID_Usuario;
	private String Descripcion; // pais
	private String Nombres;
	private String Anexo;
	private String Imagen_Usuario;
	private String Pin_Sip;
	private String Pin_Llamada;
	private String o_ck;
	private String ID_Idioma;
	private String Celular;
	private String Id_prefijo;
	private String correo;
	private String notificacion;
	private String clave;
	private String estado;
	private String saldo;

	public BeanUsuario(){
		super();
		this.ID_Usuario = "";
		this.Anexo = "";
		this.Descripcion = "";
		this.ID_Idioma = "";
		this.Imagen_Usuario = "";
		this.Nombres = "";
		this.Celular = "";
		this.o_ck = "";
		this.Pin_Llamada = "";
		this.Pin_Sip = "";
		this.correo = "";
		this.notificacion = "1";
		this.clave = "";
		this.estado = "0";
        this.setSaldo("0.0");
	}

	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNotificacion() {
		return notificacion;
	}
	public void setNotificacion(String notificacion) {
		this.notificacion = notificacion;
	}
	public String getID_Usuario() {
		return ID_Usuario;
	}
	public void setID_Usuario(String iD_Usuario) {
		ID_Usuario = iD_Usuario;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getNombres() {
		return Nombres;
	}
	public void setNombres(String nombres) {
		Nombres = nombres;
	}
	public String getAnexo() {
		return Anexo;
	}
	public void setAnexo(String anexo) {
		Anexo = anexo;
	}
	public String getImagen_Usuario() {
		return Imagen_Usuario;
	}
	public void setImagen_Usuario(String imagen_Usuario) {
		Imagen_Usuario = imagen_Usuario;
	}
	public String getPin_Sip() {
		return Pin_Sip;
	}
	public void setPin_Sip(String pin_Sip) {
		Pin_Sip = pin_Sip;
	}
	public String getPin_Llamada() {
		return Pin_Llamada;
	}
	public void setPin_Llamada(String pin_Llamada) {
		Pin_Llamada = pin_Llamada;
	}
	public String getO_ck() {
		return o_ck;
	}
	public void setO_ck(String o_ck) {
		this.o_ck = o_ck;
	}
	public String getID_Idioma() {
		return ID_Idioma;
	}
	public void setID_Idioma(String iD_Idioma) {
		ID_Idioma = iD_Idioma;
	}
	public String getNumero() {
		return Celular;
	}
	public void setNumero(String numero) {
		Celular = numero;
	}
	public String getId_prefijo() {
		return Id_prefijo;
	}
	public void setId_prefijo(String id_prefijo) {
		Id_prefijo = id_prefijo;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
}
