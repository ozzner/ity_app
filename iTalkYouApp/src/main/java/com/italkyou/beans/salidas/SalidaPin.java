package com.italkyou.beans.salidas;

import com.italkyou.beans.BeanBase;

import java.io.Serializable;

public class SalidaPin extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String channel_id;
	private String pin;
	private String idcall;

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getIdcall() {
		return idcall;
	}

	public void setIdcall(String idcall) {
		this.idcall = idcall;
	}

	public SalidaPin(){
		super();
		this.channel_id = "";
		this.pin = "";
		this.idcall = "";
	}
}
