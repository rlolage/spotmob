package com.sellaspot.datamodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class UserLogin {

	  	@Element(name="username")
	    private final String username;

	  	@Element(name="password")
	    private final String password;

	    public UserLogin(@Element(name="username") String username, @Element(name="password") String password) {
	        super();
	        this.username = username;
	        this.password = password;
	    }

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
}
