package com.kisszo.news.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class KisszoException extends Exception{

	public KisszoException(String message) {
		super(message);
	}

	public KisszoException(String message, Throwable e) {
		super(message, e);
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		KisszoLogger.getInstance().writelog(errors.toString(),"errorlog");
	}
	
}
