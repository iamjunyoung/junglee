package com.bbeaggoo.junglee.menudb;

import java.io.Serializable;

public class Customer implements Serializable{
	private String name;
	private String phone;
	private int hotkey;
	
	public Customer(String name, String phone, int hotkey) {
		this.name = name;
		this.phone = phone;
		this.hotkey = hotkey;
	}

	public Customer(String name, String phone) {
		this(name,phone,100);
	}

	public Customer() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getHotkey() {
		return hotkey;
	}

	public void setHotkey(int hotkey) {
		this.hotkey = hotkey;
	}
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(name);
		sb.append("   ");
		sb.append(phone);
		sb.append("   ");
		if(hotkey!=100) sb.append(hotkey);
		return sb.toString();
	}

}
