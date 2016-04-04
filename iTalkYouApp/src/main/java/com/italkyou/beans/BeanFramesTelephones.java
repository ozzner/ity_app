package com.italkyou.beans;

import java.io.Serializable;

public class BeanFramesTelephones extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String frameTelephone;
	private int count;

	public BeanFramesTelephones(String frameTelephone, int count) {
		this.frameTelephone = frameTelephone;
		this.count = count;
	}

	public BeanFramesTelephones() {
	}

	public String getFrameTelephone() {
		return frameTelephone;
	}

	public void setFrameTelephone(String frameTelephone) {
		this.frameTelephone = frameTelephone;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
