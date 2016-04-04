package com.italkyou.beans;

import java.io.Serializable;

public class BeanPais implements Serializable{

	private static final long serialVersionUID = 1L; 
	private String ID;
	private String ID_Prefijo;
	private String ID_Pais;
	private String DescripcionES;
	private String DescripcionEN;
	private String ID_Gtm;
	private String MCC;
		
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getID_Prefijo() {
		return ID_Prefijo;
	}
	public void setID_Prefijo(String iD_Prefijo) {
		ID_Prefijo = iD_Prefijo;
	}
	public String getID_Pais() {
		return ID_Pais;
	}
	public void setID_Pais(String iD_Pais) {
		ID_Pais = iD_Pais;
	}
	public String getDescripcionES() {
		return DescripcionES;
	}
	public void setDescripcionES(String descripcion_ES) {
		DescripcionES = descripcion_ES;
	}
	public String getDescripcionEN() {
		return DescripcionEN;
	}
	public void setDescripcionEN(String descripcion_EN) {
		DescripcionEN = descripcion_EN;
	}
	public String getID_Gtm() {
		return ID_Gtm;
	}
	public void setID_Gtm(String iD_Gtm) {
		ID_Gtm = iD_Gtm;
	}
	public String getMCC() {
		return MCC;
	}
	public void setMCC(String mCC) {
		MCC = mCC;
	}


}
