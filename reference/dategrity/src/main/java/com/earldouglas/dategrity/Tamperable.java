package com.earldouglas.dategrity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Tamperable {

	@Column(name = "tamperCode")
	int tamperCode;

	public int getTamperCode() {
		if (tamperCode == 0) {
			tamperCode = hashCode();
		}
		return tamperCode;
	}

	public void setTamperCode(int tamperCode) {
		this.tamperCode = tamperCode;
	}

	public boolean tampered() {
		return hashCode() != tamperCode;
	}
}
