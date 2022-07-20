package com.kisszo.news.netty.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.exceptions.KisszoLogger;
import com.kisszo.news.netty.controller.AnnotatedPathManager.PathImplementor;
import com.kisszo.news.netty.http.WHTTPResponse;
import com.kisszo.news.netty.http.WHTTPResponse.ContentType;
import com.kisszo.news.netty.http.WHttpRequest;
import com.kisszo.news.netty.manager.KisszoCalendar;

import io.netty.handler.codec.http.HttpMethod;

public class CommandController {

	private static CommandController instance = null;
	private CommandController(){
	}
	
	public static CommandController getInstance(){
		if(instance == null){
			synchronized (CommandController.class) {
				if(instance == null){
					instance = new CommandController();
				}
			}
		}
		return instance;
	}
	public void execute(WHttpRequest request, WHTTPResponse response) throws KisszoException{
		long startTime = System.currentTimeMillis();
		String startDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
		String uri = request.getURI();
		System.out.println("Request uri : "+uri);
		String status = " ";
		String exceptionName=" ";
		String exceptionMessage = " ";
		try {
			AnnotatedPathManager annPath = AnnotatedPathManager.getInstance();
			PathImplementor pathIml = annPath.getActionImplementor(uri);
			String methodName = uri;
			if(pathIml != null){
				methodName = pathIml.getMethod().getName();
			}
			String log = "incomingrequest;"+methodName+";"+startDate+";"+startDate+";"+startTime+";"+startTime+";"+request.getCountry(request.getSessionValue("applogged_in", "networkid"))+";"+request.getCity()+";"+request.getIpAddress()+";"+request.getBrowser()+";"+request.getBodyContent().getBytes().length+";"+request.getBodyContent()+";"+(startTime-startTime)+";"+true+";"+request.getSessionValue("defaultstore", "storename")+";"+true;
			KisszoLogger.getInstance().writelog(log,"usagelog");

			if(uri.equals("/favicon.ico") || request.getMethod() == HttpMethod.OPTIONS){ 
				throw new KisszoException("FavIcon to be implememted");
			}
			
			if(pathIml == null){
				StringBuffer message = new StringBuffer("URI : " + uri + " is not defined.");
				if("true".equalsIgnoreCase(System.getProperty("pathDebug"))){
					message .append("<br><br> Supported Path <br><br>");
					Set<String> allPaths = annPath.getAllURI();
					for(String s : allPaths){
						message.append(s).append("<br>");
					}
				}
				throw new KisszoException(message.toString());
			}
			System.out.println("call clazz:"+pathIml.getClassDec().getName()+", Called method :"+ pathIml.getMethod().getName());
			Constructor constructor = pathIml.getClassDec().getConstructor(WHttpRequest.class, WHTTPResponse.class);
			Object action = constructor.newInstance(request, response);
			request.setMethodName(pathIml.getMethod().getName());
			setBeanObject(action, request); 
				String error = validation(request);
				System.out.println("Error : "+error);
				if(error==null || error.length()==0){
					Object responseContent = executeMethod(action, pathIml.getMethod());
					if(responseContent instanceof String){
						response.writeResponse((String)responseContent);
						status = "success";
					}else if(responseContent instanceof File){
						response.writeResponse((File)responseContent);
						status = "success";
					}
				}else{
					response.writeResponse(error);
				}
		} catch (Throwable e) {
			exceptionName = e.getClass().getName();
			exceptionMessage = e.getMessage();
			e.printStackTrace();
			response.writeResponse("Internal server error try again", ContentType.TEXT_HTML);
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("message", errors.toString()+"<br>"+request.getBodyContent());
			params.put("subject", "Error in "+request.getURI());
			String[] ids = {"jayaraj@waffor.com","ramachandran@waffor.com"};
			params.put("emailId", ids);
		
			new KisszoException("Internal server error try again",e);
		}
		try {
			String endDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
			long endTime = System.currentTimeMillis();
			String log = "server-start;"+request.getMethodName()+";"+startDate+";"+endDate+";"+startTime+";"+endTime+";"+request.getCountry(request.getSessionValue("applogged_in", "networkid"))+";"+request.getCity()+";"+request.getIpAddress()+";"+request.getBrowser()+";"+request.getBodyContent().getBytes().length+";"+request.getURI()+";"+(endTime-startTime)+";"+true+";"+request.getSessionValue("defaultstore", "storename")+";"+status;
			KisszoLogger.getInstance().writelog(log,"usagelog");
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private String validation(WHttpRequest request){
		try {
				String body = request.getBodyContent();
				String url = request.getRequestParameterMap().toString();
				System.out.println("URL Params :"+url);
				boolean error = XssSanitizerUtil.stripXSS(body);
				boolean geterror = XssSanitizerUtil.stripXSS(url);
				
				if(error || geterror)
				{
					return "Input XSS Validation Error";
				}else{
					return "";
				}
		
		} catch (IllegalArgumentException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			new KisszoException("validation", e);
			System.out.println(e.getMessage());	
			return "Validation error";
		}
	}

	private void setBeanObject(Object action, WHttpRequest request){
		
		if(!(action instanceof BeanIdentifier)){
			return;
		}
		
		Class beanClass = ((BeanIdentifier)action).beanClass();
		
		Object beanObject = null;
		if(request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.OPTIONS){
			beanObject = BeanPopulator.getInstance().populateBeanByParam(request, beanClass);
		}else if(request.getMethod() == HttpMethod.PUT || request.getMethod() == HttpMethod.POST){
			beanObject = BeanPopulator.getInstance().populateBeanByJSON(request, beanClass);
		}
		
		((BeanIdentifier)action).setBean(beanObject);
		
	}
	
	private Object executeMethod(Object action, Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return (Object)method.invoke(action, null);
	}
	
	public URIElement resolveCommand(String uri) throws KisszoException{
		return URIElementBuilder.buildURIElement(uri); 
	}

	
	
	
}
