package com.kisszo.news.netty.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.kisszo.news.netty.http.WHttpRequest;
import com.kisszo.news.netty.manager.KisszoParser;

public class BeanPopulator {

	private static BeanPopulator instance;
	private Logger log = LogManager.getLogger(BeanPopulator.class);
	
	public static BeanPopulator getInstance(){
		if(instance == null){
			synchronized (BeanPopulator.class) {
				if(instance == null){
					instance = new BeanPopulator();
				}
			}
		}
		return instance;
	}

	
	public Object populateBeanByParam(WHttpRequest httpRequest, Class beanClass){
		
		Object beanObject = null;
		try {
			beanObject = beanClass.newInstance();
		} catch (InstantiationException e1) {
			log.error("Error in creating object for bean class : " + beanClass, e1);
			return null;
		} catch (IllegalAccessException e1) {
			log.error("Error in creating object for bean class : " + beanClass, e1);
			return null;
		}
		
		Map<String, List<String>> reqParamMap = httpRequest.getRequestParameterMap();
		System.out.println("Request parameter : "+httpRequest.getRequestParameterMap().size());
		Set<Entry<String, List<String>>> entries = reqParamMap.entrySet();
		
		for(Entry<String, List<String>> entry : entries){
			String paramName = entry.getKey();
			List<String> paramValues = entry.getValue();
			if(paramValues != null){
				for(String value : paramValues){
					try {
						if(KisszoParser.getInstance().isJSONValid(value)){
							BeanUtils.copyProperty(beanObject, paramName, KisszoParser.getInstance().convertJsontoMapper(value));
						}else{
							BeanUtils.copyProperty(beanObject, paramName, value);
						}
						System.out.println("Populating : "+ paramName + " With value "+ value + " in bean "+ beanClass);
					} catch (IllegalAccessException e) {
						log.error("Error in populating " + paramName + " with value " + value + " in bean " + beanClass, e);
					} catch (InvocationTargetException e) {
						log.error("Error in populating " + paramName + " with value " + value + " in bean " + beanClass, e);
					}
				}
			}
		}
		beanObject = populateBeanBySession(httpRequest,beanObject);
		return beanObject;
	}
	
	public Object populateBeanByJSON(WHttpRequest request, Class bean){
		
		String bodyConent = request.getBodyContent();
		System.err.println("body:"+bodyConent);
		Gson gson = new Gson();
		Object beanObject = gson.fromJson(bodyConent, bean);
		if(beanObject==null){
			try {
				beanObject = bean.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		beanObject = populateBeanBySession(request,beanObject);
		
		return beanObject;
		
	}

	public Object populateBeanBySession(WHttpRequest request,Object bean){
		System.out.println("Session bean populating");
		try {
						
			
			BeanUtils.copyProperty(bean, "path", request.getURI());
			BeanUtils.copyProperty(bean, "uploadFilePath", request.getUploadFilePath());
			BeanUtils.copyProperty(bean, "bodyContent", request.getBodyContent());
			if(request.getSessionValue("applogged_in", "networkId")!=null)
				BeanUtils.copyProperty(bean, "networkId", request.getSessionValue("applogged_in", "networkid"));
			if(request.getSessionValue("applogged_in", "storeId")!=null)
				BeanUtils.copyProperty(bean, "storeId", request.getSessionValue("applogged_in", "storeId"));
			if(request.getSessionValue("applogged_in", "role")!=null){
				BeanUtils.copyProperty(bean, "role", request.getSessionValue("applogged_in", "role"));
			}
			if(request.getSessionValue("applogged_in", "userid")!=null){
				BeanUtils.copyProperty(bean, "userId", request.getSessionValue("applogged_in", "userid"));
			}
			if(request.getSessionValue("applogged_in", "senderId")!=null)
				BeanUtils.copyProperty(bean, "senderId", request.getSessionValue("applogged_in", "sender_id"));
			if(request.getSessionValue("applogged_in", "userContactNo")!=null)
				BeanUtils.copyProperty(bean, "userContactNo", request.getSessionValue("applogged_in", "contact_no"));
			if(request.getSessionValue("applogged_in", "loginEmailId")!=null)
				BeanUtils.copyProperty(bean, "loginEmailId", request.getSessionValue("applogged_in", "emailid"));
			if(request.getSessionValue("applogged_in", "currentPackId")!=null)
				BeanUtils.copyProperty(bean, "currentPackId", request.getSessionValue("applogged_in", "packageid"));
			if(request.getSessionValue("applogged_in", "mobileCode")!=null)
				BeanUtils.copyProperty(bean, "mobileCode", request.getSessionValue("applogged_in", "mobileCode"));
			if(request.getSessionValue("applogged_in", "isIndia")!=null)
				BeanUtils.copyProperty(bean, "isIndia", request.getSessionValue("applogged_in", "isIndia"));
			if(request.getSessionValue("applogged_in", "timeZone")!=null)
				BeanUtils.copyProperty(bean, "timeZone", request.getSessionValue("applogged_in", "timeZone"));
			BeanUtils.copyProperty(bean, "methodName", request.getMethodName());
			if(request.getSessionValue("applogged_in", "country")!=null) {
				BeanUtils.copyProperty(bean, "country", request.getCountry(request.getSessionValue("applogged_in", "country")));
			}
			BeanUtils.copyProperty(bean, "browser", request.getBrowser());
			BeanUtils.copyProperty(bean, "ipAddress", request.getIpAddress());
			BeanUtils.copyProperty(bean, "cookies", request.getCookies());
			BeanUtils.copyProperty(bean, "dataSize", request.getBodyContent().getBytes().length);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.println("error "+e.getMessage());
			log.error("Error in populating session in bean " + bean.getClass(), e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.out.println("error "+e.getMessage());
			log.error("Error in populating session in bean " + bean.getClass(), e);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("error "+e.getMessage());
		}
		return bean;
	}
}
