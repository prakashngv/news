package com.kisszo.news.netty.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ValidationMgr {
	private static ValidationMgr instance = null;
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern PHONE_REGEX = 
		    Pattern.compile("\\d{10}");
	public static final Pattern PRICE_REGEX = 
		    Pattern.compile("[0-9]+([.][0-9]{1,2})?");
	
	private ValidationMgr(){
	}
	
	public static ValidationMgr getInstance(){
		if(instance == null){
			synchronized (ValidationMgr.class) {
				if(instance == null){
					instance = new ValidationMgr();
				}
			}
		}
		
		return instance;
	}
	
	
	public String validation(Object bean,Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		System.out.println("Trying to validate params");
		StringBuilder error = new StringBuilder();
		Ignore ignore  = method.getAnnotation(Ignore.class); // get ignore not null field
		Validation valditaion  = method.getAnnotation(Validation.class);
		if(valditaion!=null){
			Method[] methods = bean.getClass().getDeclaredMethods();
			for(Method m : methods) {
				String mName = m.getName().substring(3);
				Annotation[] annotations = m.getAnnotations();
				for (Annotation annotation : annotations) {
					if(annotation instanceof NotNull){
						System.out.println("Notnull Validation");
						if(!notNullValidation(bean, m)){
							String msg = ((NotNull) annotation).msg();
							error.append(msg+"<br>");
						}
					}else if(annotation instanceof Size){
						System.out.println("Size Validation");
						sizeValidation(bean, m, annotation);
					}else if(annotation instanceof EmailID){
						System.out.println("Email id validation");
						emailIdValidation(bean, m);
					}else if(annotation instanceof Phone){
						System.out.println("Phone Number Validation");
						phoneValidation(bean, m);
					}else if(annotation instanceof PatternMatch){
						System.out.println("Pattern Validation");
						patternValidation(bean, m, annotation);
					}else if(annotation instanceof Digits){
						System.out.println("Digits Validation");
					}
					
					
				}
			}
		}
		return error.toString();
	} 
	
	
	private Boolean notNullValidation(Object bean,Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object value = method.invoke(bean, null);
		if(value !=null){
			return true;
		}else{
			return false;
		}
	}
	
	private Boolean sizeValidation(Object bean, Method method,Annotation annotation) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		int min = ((Size)annotation).min();
		int max = ((Size)annotation).max();
		String value = (String)method.invoke(bean, null);
		if(value!=null){
			int length = value.length();
			if(min <= length && max >=length){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}
	
	private Boolean emailIdValidation(Object bean, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String value = (String)method.invoke(bean, null);
		if(value!=null){
			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(value);
		    return matcher.find();
		}
		return true;
	}
	
	private Boolean phoneValidation(Object bean, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String value = (String)method.invoke(bean, null);
		if(value!=null){
			Matcher matcher = PHONE_REGEX .matcher(value);
		    return matcher.find();
		}
		
		return true;
	}
	
	private Boolean patternValidation(Object bean, Method method,Annotation annotation) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String regexp = ((PatternMatch)annotation).regexp();
		String value = (String)method.invoke(bean, null);
		if(value!=null){
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(value);
			return matcher.find();
		}
		return true;
	}
	
	private Boolean digitsValidation(Object bean, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		String value = (String)method.invoke(bean, null);
		if(value !=null){
			Matcher matcher = PRICE_REGEX.matcher(value);
			return matcher.find();
		}
		
		return true;
	}
	
}
