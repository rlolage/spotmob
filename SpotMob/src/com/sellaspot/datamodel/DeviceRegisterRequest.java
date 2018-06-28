package com.sellaspot.datamodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "DeviceRegisterRequest", strict = false)
public class DeviceRegisterRequest {

	@Element(required=false)
	private String token;
	@Element(required=false)
	private String devicetype;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDeviceType() {
		return devicetype;
	}
	public void setDeviceType(String deviceType) {
		this.devicetype = deviceType;
	}
}
