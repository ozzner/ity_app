package com.italkyou.beans;

import java.io.Serializable;

public class BeanSaldo extends BeanBase implements Serializable{

	private static final long serialVersionUID = 1L;
	private String Balance;


	public String getBalance() {
		return Balance;
	}

	public void setBalance(String balance) {
		Balance = balance;
	}

}
