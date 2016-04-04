package com.italkyou.beans.salidas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SalidaHistorialLlamadas implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Object> listaLlamadas;
	private List<Object> listaMensajesVoz;
	
	public SalidaHistorialLlamadas(){
		this.listaLlamadas = new ArrayList<Object>();
		this.listaMensajesVoz = new ArrayList<Object>();
	}
	
	public List<Object> getListaLlamadas() {
		return listaLlamadas;
	}
	public void setListaLlamadas(List<Object> listaLlamadas) {
		this.listaLlamadas = listaLlamadas;
	}
	public List<Object> getListaMensajesVoz() {
		return listaMensajesVoz;
	}
	public void setListaMensajesVoz(List<Object> listaLlamadasVoz) {
		this.listaMensajesVoz = listaLlamadasVoz;
	}
	
}
