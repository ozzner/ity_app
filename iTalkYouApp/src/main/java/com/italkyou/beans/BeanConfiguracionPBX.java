package com.italkyou.beans;

public class BeanConfiguracionPBX {

	private  String ipSvrComunicacion;
	private  int    puertoSrvComunicacion;
	private  String protocolo;
	private  int    puerto;

	public String getIpSvrComunicacion() {
		return ipSvrComunicacion;
	}
	
	public void setIpSvrComunicacion(String ipSvrComunicacion) {
		this.ipSvrComunicacion = ipSvrComunicacion;
	}
	
	public int getPuertoSrvComunicacion() {
		return puertoSrvComunicacion;
	}
	
	public void setPuertoSrvComunicacion(int puertoSrvComunicacion) {
		this.puertoSrvComunicacion = puertoSrvComunicacion;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	
}
