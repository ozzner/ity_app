package com.italkyou.beans.entradas;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParametrosWS {
	
	private String ruta;
	private List<NameValuePair> parametros;
	private String mascara;
	private String metodo;
    private boolean flagJSON;
    private JSONObject jsonObjectParams;
	
	public ParametrosWS(){
		this.ruta = "";
		this.parametros = new ArrayList<>();
		this.mascara = "";
		this.metodo = "";
        this.flagJSON = false;
	}
	
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public List<NameValuePair> getParametros() {
		return parametros;
	}
	public void setParametros(List<NameValuePair> parametros) {
		this.parametros = parametros;
	}
	public String getMascara() {
		return mascara;
	}
	public void setMascara(String mascara) {
		this.mascara = mascara;
	}
	public String getMetodo() {
		return metodo;
	}
	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

    public boolean isFlagJSON() {
        return flagJSON;
    }

    public void setFlagJSON(boolean isJson) {
        this.flagJSON = isJson;
    }

    public JSONObject getJsonObjectParams() {
        return jsonObjectParams;
    }

    public void setJsonObjectParams(JSONObject jsonObjectParams) {
        this.jsonObjectParams = jsonObjectParams;
    }
}
