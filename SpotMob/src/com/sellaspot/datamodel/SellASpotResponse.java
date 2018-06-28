package com.sellaspot.datamodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "SellASpotResponse", strict = false)
public class SellASpotResponse {

	@Element(required=false)
	private String status;
	@Element(required=false)
	private String reason;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
