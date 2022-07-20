package com.kisszo.news.netty.controller;

import com.kisszo.news.exceptions.KisszoException;

public class URIElementBuilder {

	// format of URI /context/module/command/method.ext
	public static URIElement buildURIElement(String uri) throws KisszoException{
		int questionIndex = uri.indexOf('?');
		if(questionIndex > 0){
			uri = uri.substring(0, questionIndex);
		}
		String[] uriElements = uri.split("/");
		if(uriElements.length < 4){
			throw new KisszoException("InCompatible URI: " + uri);
		}
		String context = uriElements[1];
		String module = uriElements[2];
		String command = uriElements[3];
		String method = "execute";
		if(uriElements.length == 5 ){
			if(uriElements[4] != null || !uriElements[4].isEmpty()){
				method = uriElements[4];
			}
		}
		int dotIndex = method.indexOf('.');
		if(dotIndex > 0){
			method = method.substring(0, dotIndex);
		}
		
		return new URIElement(context, module, command, method);
	}
	
	public static void main(String[] args) throws KisszoException {
		URIElementBuilder builder = new URIElementBuilder();
		URIElement urlElement = builder.buildURIElement("/entryContext/module/command/test");
		System.out.println(urlElement.getContext() + "/" + urlElement.getModule() + "/" + urlElement.getCommand() + "/" + urlElement.getMethod()); 
		urlElement = builder.buildURIElement("/entryContext/module/command");
		System.out.println(urlElement.getContext() + "/" + urlElement.getModule() + "/" + urlElement.getCommand() + "/" + urlElement.getMethod()); 
		urlElement = builder.buildURIElement("/entryContext/module");
		System.out.println(urlElement.getContext() + "/" + urlElement.getModule() + "/" + urlElement.getCommand() + "/" + urlElement.getMethod()); 
	}
	
}
