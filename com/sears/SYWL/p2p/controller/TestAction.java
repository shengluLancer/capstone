package com.sears.SYWL.p2p.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestAction extends Action {
	
	public String ACTION_NAME="test.do";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return ACTION_NAME;
	}

	@Override
	public String perform(HttpServletRequest request, PrintWriter writer) {
		// TODO Auto-generated method stub
		String testMsg=request.getParameter("msg");
		writer.write(testMsg);
		return "test.out";
	}

}
