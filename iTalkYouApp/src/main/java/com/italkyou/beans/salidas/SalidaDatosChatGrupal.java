package com.italkyou.beans.salidas;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SalidaDatosChatGrupal implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Object> listaContactos;
	private List<String > lstMiembros;
	private String nombre;
	private Bitmap imagenGrupo;
	private String idChat;
	
	public List<Object> getListaContactos() {
		return listaContactos;
	}

	public void setListaContactos(List<Object> listaContactos) {
		this.listaContactos = listaContactos;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Bitmap getImagenGrupo() {
		return imagenGrupo;
	}

	public void setImagenGrupo(Bitmap imagenGrupo) {
		this.imagenGrupo = imagenGrupo;
	}
	
	public String getIdChat() {
		return idChat;
	}

	public void setIdChat(String idChat) {
		this.idChat = idChat;
	}

	public List<String> getLstMiembros() {
		return lstMiembros;
	}

	public void setLstMiembros(List<String> lstMiembros) {
		this.lstMiembros = lstMiembros;
	}

	public SalidaDatosChatGrupal(){
		this.listaContactos = new ArrayList<Object>();
		this.nombre = "";
		this.imagenGrupo = null;
		this.idChat = "";
		this.lstMiembros = new ArrayList<String>();
	}

}
