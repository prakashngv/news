package com.kisszo.news.netty.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AbstractComponentManager {

	protected static ApplicationContext ctx;
	
	public AbstractComponentManager() {
		BeanContextManager mgr = BeanContextManager.getInstance();
		String[] contextXML = mgr.getContentPaths(); 
		initializeApplicationContext(contextXML);
	}
	
	private void initializeApplicationContext(String[] contextXML){
		if(ctx == null){
			synchronized (AbstractComponentManager.class) {
				if(ctx == null){
					ctx = new ClassPathXmlApplicationContext(contextXML); 
				}
			}
		}
	}
	
	protected Object getBean(String beanName){
		return ctx.getBean(beanName);
	}
}
