package com.kisszo.news.action;

import java.net.URLEncoder;

import com.kisszo.news.netty.controller.BeanIdentifier;
import com.kisszo.news.netty.http.WHTTPResponse;
import com.kisszo.news.netty.http.WHttpRequest;

import de.ailis.pherialize.MixedArray;
import io.netty.handler.codec.http.Cookie;

public abstract class BaseAction implements BeanIdentifier{

	protected WHttpRequest httpRequest;
	protected WHTTPResponse httpResponse;
	
	public BaseAction(WHttpRequest httpRequest, WHTTPResponse httpResponse){
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}
	
	public MixedArray getSession(){
		return httpRequest.getCommonSession();
	}
	
	public void updateSession(MixedArray session,String name){
			String encrypt = httpRequest.encryptCookie(session);
			Cookie cookie = httpRequest.getCookie(name);
			cookie.setValue(encrypt);
			cookie.setPath("/");
			httpResponse.addUpdateCookie(cookie);
		
	}
	
	public void updateCookie(String feature,String name){
		Cookie cookie = httpRequest.getCookie(name);
		cookie.setValue(URLEncoder.encode(feature));
		cookie.setPath("/");
		httpResponse.addUpdateCookie(cookie);
	
	}
	
}
