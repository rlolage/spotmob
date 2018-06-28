package com.sellaspot.datamodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class User {

		@Element(name="sessionId")
	    private final String sessionId;

		@Element(name="status")
	    private final String status;
	    
		@Element(name="username")
	    private final String username;
	    
	    public User(@Element(name="sessionId") String sessionId, @Element(name="status") String status, @Element(name="username") String username) {
	        super();
	        this.sessionId = sessionId;
	        this.status = status;
	        this.username = username;
	    }

		public String getSessionId() {
			return sessionId;
		}

		public String getStatus() {
			return status;
		}

		public String getUserName() {
			return username;
		}
}
