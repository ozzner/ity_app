package com.italkyou.beans;

import java.util.List;

public class BeanContactUser {

	private List<BeanContact> listaContactos;
	private List<BeanTelefono> listaTelefonos;
	
	public List<BeanContact> getListaContactos() {
		return listaContactos;
	}
	public void setListaContactos(List<BeanContact> listaContactos) {
		this.listaContactos = listaContactos;
	}
	public List<BeanTelefono> getListaTelefonos() {
		return listaTelefonos;
	}
	public void setListaTelefonos(List<BeanTelefono> listaTelefonos) {
		this.listaTelefonos = listaTelefonos;
	}
	
	
}
