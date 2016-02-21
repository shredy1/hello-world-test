package com.freedomotic.plugins.devices.hello;

import java.io.Serializable;

public class Etat implements Serializable{

	
	private static final long serialVersionUID = 1L;
	public String mode;
	public String valueThermoExt;
	public String valueThermoInt;
	public String porte;
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getValueThermoExt() {
		return valueThermoExt;
	}
	public void setValueThermoExt(String valueThermoExt) {
		this.valueThermoExt = valueThermoExt;
	}
	public String getValueThermoInt() {
		return valueThermoInt;
	}
	public void setValueThermoInt(String valueThermoInt) {
		this.valueThermoInt = valueThermoInt;
	}
	public String getPorte() {
		return porte;
	}
	public void setPorte(String porte) {
		this.porte = porte;
	}

	
}
