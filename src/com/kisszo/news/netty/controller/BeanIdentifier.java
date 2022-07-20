package com.kisszo.news.netty.controller;

public interface BeanIdentifier {

	public Class beanClass(); 
	
	public Object getBean();
	
	public void setBean(Object bean);
	
}
