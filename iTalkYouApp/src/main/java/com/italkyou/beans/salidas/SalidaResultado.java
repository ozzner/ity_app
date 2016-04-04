package com.italkyou.beans.salidas;

import com.google.gson.annotations.SerializedName;
import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class SalidaResultado extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
    @SerializedName("Mensaje")
    private String mensaje;

    public SalidaResultado(){
        this.mensaje ="";
    }

	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
