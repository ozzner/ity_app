package com.italkyou.beans;

import com.italkyou.utils.Const;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BeanContact extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String Nombre;
	private String Pais;
	private int idContacto;
	private int foto; //0: no tiene una imagen asociada
	private String Telefono;
	private String anexo;
	private String lookUpKey;
	private String usuarioITY; //0: si no es usuario ITY 1: si es usuario ITY
	private int flagValidacion;
	private List<Object> phones;

    public BeanContact(){
        this.Nombre = "";
        this.Pais = "";
        this.Telefono = "";
        this.usuarioITY = Const.NO_USER_ITY;
        this.flagValidacion=0;
		this.lookUpKey = "none";
		 phones = new ArrayList<>();
	}

    public BeanContact(String anexo, String nombres, String numero){
        super();
        this.Nombre = nombres;
        this.anexo = anexo;
        this.Telefono = numero;
        this.usuarioITY = Const.USER_ITY;
		this.lookUpKey = "none";
		phones = new ArrayList<>();
    }

	public List<Object> getPhones() {
		return phones;
	}

	public void setPhones(List<Object> phones) {
		this.phones = phones;
	}

	public String getLookUpKey() {
		return lookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}

	public String getTelefono() {
		return Telefono;
	}

    public void setTelefono(String celular) {
		Telefono = celular;
	}

	public String getAnexo() {
		return anexo;
	}

    public void setAnexo(String anexo) {
		this.anexo = anexo;
	}

	public int getIdContacto() {
		return idContacto;
	}

	public void setIdContacto(int id) {
		this.idContacto = id;
	}

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		this.Nombre = nombre;
	}

	public String getPais() {
		return Pais;
	}

	public void setPais(String pais) {
		this.Pais = pais;
	}

	public int getFoto() {
		return foto;
	}

	public void setFoto(int foto) {
		this.foto = foto;
	}
	
	public String getUsuarioITY() {
		return usuarioITY;
	}
	
	public void setUsuarioITY(String usuarioITY) {
		this.usuarioITY = usuarioITY;
	}

    public int getFlagValidacion() {
        return flagValidacion;
    }

    public void setFlagValidacion(int flagValidacion) {
        this.flagValidacion = flagValidacion;
    }

}
