package com.variable;

import java.io.Serializable;

public class TemperatureObs implements Serializable {

	private static final long serialVersionUID = 1L;
	public double TC;
	public double TCmDetlatProfil;
	public double TE;
	public double TI;
	
	public double getTC() {
		return TC;
	}
	public void setTC(double tC) {
		TC = tC;
	}
	public double getTCmDetlatProfil() {
		return TCmDetlatProfil;
	}
	public void setTCmDetlatProfil(double tCmDetlatProfil) {
		TCmDetlatProfil = tCmDetlatProfil;
	}
	public double getTE() {
		return TE;
	}
	public void setTE(double tE) {
		TE = tE;
	}
	public double getTI() {
		return TI;
	}
	public void setTI(double tI) {
		TI = tI;
	}
	
}
