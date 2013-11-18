package com.sears.SYWL.p2p.api;

public class ReturnMessage implements IJSONable {
	
	String message;
	
	public ReturnMessage(String message) {
		this.message=message;
	}
	
	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
