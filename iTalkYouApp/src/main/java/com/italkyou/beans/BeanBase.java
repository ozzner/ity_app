package com.italkyou.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeanBase implements Serializable {

	private static final long serialVersionUID = 1L;
    @SerializedName("Resultado")
    private String resultado;

    public BeanBase(){
        resultado="";
    }

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

}
