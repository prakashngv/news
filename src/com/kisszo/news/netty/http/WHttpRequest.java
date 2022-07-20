package com.kisszo.news.netty.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.ailis.pherialize.MixedArray;
import de.ailis.pherialize.Pherialize;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

public class WHttpRequest {

	private HttpRequest request;
	private StringBuilder bodyBuf;
	private Map<String, List<String>> params;
	private Set<Cookie> cookies = new HashSet<Cookie>();
	private Map<String, String> headers;
	private String hostName;
	private String userAgent;
	private boolean isConnectionKeepAlive = false;
	private HttpMethod method;
	private MixedArray session = null;
	private String filePath;
	private String ipAddress;
	private String country;
	private String city;
	private String browser;
	private String methodName;

	public WHttpRequest(HttpRequest request, StringBuilder bodyBuf) {
		this.request = request;
		this.bodyBuf = bodyBuf;
		init();
	}

	private void init(){
		setRequestParam();
		setCookie();
		setHeader();
		setActionMethod();
		setSession();
		setIpAddress();
		setCountry();
		setCity();
		setBrowser();
		setMethodName("");
	}
	
	public void setIpAddress() {
		this.ipAddress = "0.0.0.0";
		try {
			String ip = request.headers().get("X-Forwarded-For");
			if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("Proxy-Client-IP");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("WL-Proxy-Client-IP");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_X_FORWARDED_FOR");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_X_FORWARDED");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_X_CLUSTER_CLIENT_IP");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_CLIENT_IP");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_FORWARDED_FOR");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_FORWARDED");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("HTTP_VIA");  
		    }  
		    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
		        ip = request.headers().get("REMOTE_ADDR");  
		    }  
		    this.ipAddress = ip;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getIpAddress() {
		return this.ipAddress!=null?this.ipAddress:"";
	}
	
	public void setCountry() {
		this.country = "india";
	}
	
	public String getCountry(String networkId) {
		return this.country;
	}
	
	public void setCity() {
		this.city = this.getSessionValue("defaultstore", "city")!=null?this.getSessionValue("defaultstore", "city"):"";
	}
	
	public String getCity() {
		return this.city!=null?this.city:"";
	}
	
	public void setBrowser() {
		if(this.getUserAgent()!=null){
			UserAgent userAgent = UserAgent.parseUserAgentString(this.getUserAgent());
			Browser browsers = userAgent.getBrowser();
			this.browser = browsers.getName();
		}
	}
	
	public String getBrowser() {
		return this.browser!=null?this.browser:"";
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMethodName() {
		return this.methodName!=null?this.methodName:"";
	}
	
	public String getBodyContent(){
		if(bodyBuf!=null){
			return this.bodyBuf.toString();
		}
		return null;
	}
	
	private void setActionMethod(){
		this.method = this.request.method();
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	private void setRequestParam(){
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
        params = queryStringDecoder.parameters();
	}
	
	private void setCookie(){
        String cookieString = request.headers().get(COOKIE);
        if (cookieString != null){ 
            cookies.addAll(CookieDecoder.decode(cookieString));
        }
	}
	
	public Set<Cookie> getCookies(){
		return cookies;
	}
	
	private void setSession(){
		String decryptedData = null;
		String secretKey = "!QAZ1qaz!!123456";
		String initialVectorString = "9999999999999999";
		String rawdata = this.getCookieString("ci_session");
		if(rawdata !=null){
			String encryptedData = URLDecoder.decode(rawdata);
		
		    try {
		    	
		        SecretKeySpec skeySpec = new SecretKeySpec(md5(secretKey).getBytes(), "AES");
		        IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
		        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		        cipher.init(Cipher.DECRYPT_MODE, skeySpec, initialVector);
		        byte[] encryptedByteArray = (new org.apache.commons.codec.binary.Base64()).decode(encryptedData.getBytes());
		        byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
		        decryptedData = new String(decryptedByteArray, "UTF8");
			 	session = Pherialize.unserialize(decryptedData).toArray();
		    } catch (Exception e) {
		    	System.err.println("Session Parse error :"+e.getMessage());
		    }
		}
	}
	
	public String  encryptCookie(MixedArray session){
		String secretKey = "!QAZ1qaz!!123456";
		String initialVectorString = "9999999999999999";
		try {
			byte[] data = 	Pherialize.serialize(session).getBytes();
			
			
	        SecretKeySpec skeySpec = new SecretKeySpec(md5(secretKey).getBytes(), "AES");
	        IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
	        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initialVector);
	        byte[] decryptedByteArray = cipher.doFinal(data);
	        byte[] encryptedByteArray = (new org.apache.commons.codec.binary.Base64()).encode(decryptedByteArray);
	        String encryptedData = new String(encryptedByteArray, "UTF8");
		 	return URLEncoder.encode(encryptedData);
		 	
	    } catch (Exception e) {
	    	System.err.println("Session Parse error :"+e.getMessage());
	    	return null;
	    }
	}
	
	private String md5(String input) throws NoSuchAlgorithmException {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] messageDigest = md.digest(input.getBytes());
	    BigInteger number = new BigInteger(1, messageDigest);
	    return number.toString(16);
	}
	
	
	private void setHeader(){
		headers = new HashMap<String, String>();
        List<Map.Entry<String, String>> headerObj = request.headers().entries();
        if (!headerObj.isEmpty()) {
            for (Map.Entry<String, String> h: headerObj) {
                String key = h.getKey();
                String value = h.getValue();
                headers.put(key, value);
                if(key.equalsIgnoreCase("Host")){
                	this.hostName = value;
                }else if(key.equalsIgnoreCase("User-Agent")){
                	this.userAgent = value;
                }else if(key.equalsIgnoreCase("Connection")){
                	if(value.equalsIgnoreCase("keep-alive")){
                		this.isConnectionKeepAlive = true;
                	}
                }
            }
        }
	}
	
	public String getSigHeader(){
		return headers.get("Stripe-Signature");
	}
	
	public Map<String, String> getHeaders(){
		return headers;
	}
	
	public boolean isConnectionKeepAlive(){
		return isConnectionKeepAlive;
	}
	
	public String getHost(){
		return hostName; 
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public Map<String, List<String>> getRequestParameterMap(){
		return params;
	}
	
	public String getParameter(String name){
		if(params != null){
			List<String> values = params.get(name);
			if(values != null){
				return values.get(0);
			}
		}
		return null;
	}
	
	public String getCookieString(String name){
		if(cookies != null){
			Cookie cookie = getCookie(name);
			if(cookie != null){
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public Cookie getCookie(String name){
		if(cookies != null){
			for (Iterator iterator = cookies.iterator(); iterator.hasNext();) {
				Cookie cookie = (Cookie) iterator.next();
				if(cookie.getName().equals(name)){
					return cookie;
				}
			}
		}
		return null;
	}
	
	public MixedArray getCommonSession(){
		return session;
	}
	
	public MixedArray getSession(String name){
		if(session !=null){
			return session.getArray(name);
		}
		return null;
	}
	
	public String getSessionValue(String name, String key){
		try{
			return session.getArray(name).getString(key);
		}catch(Throwable e){
			return null;
		}
	}
	
	public String getURI(){
		String uri =request.getUri();
		if(uri.indexOf('?')!=-1){
			uri = uri.substring(0, uri.indexOf('?'));
		}
		return uri;
	}
	
	public void setUploadFilePath(String path){
		filePath = path;
	}
	
	public String getUploadFilePath(){
		return filePath;
	}
	
}
