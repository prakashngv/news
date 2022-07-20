package com.kisszo.news.netty.controller;

public class URIElement {
	private String context;
	private String module;
	private String command;
	private String method;
	private String actionPath;
	public URIElement(String context, String module, String command,
			String method) {
		super();
		this.context = context;
		this.module = module;
		this.command = command;
		this.method = method;
		StringBuilder builder = new StringBuilder(module.length() + 1 + command.length());
		this.actionPath = builder.append(context).append("/").append(module).append("/").append(command).toString(); 
	}
	public String getContext() {
		return context;
	}
	public String getModule() {
		return module;
	}
	public String getCommand() {
		return command;
	}
	public String getMethod() {
		return method;
	}

	public String getActionPath(){
		return this.actionPath;
	}
	
}
