package com.kisszo.news.netty.manager;

public class BeanContextManager {

	private String[] contextXML = {"application.xml", "cache-Interceptor.xml"};
	
	private static BeanContextManager mgr = null;
	
	private BeanContextManager() {
		// TODO Auto-generated constructor stub
	}

	protected static BeanContextManager getInstance(){
		if(mgr == null){
			synchronized (BeanContextManager.class) {
				if(mgr == null){
					mgr = new BeanContextManager();
				}
			}
		}
		return mgr;
	}
	
	public String[] getContentPaths(){
		return contextXML;
	}
	
}


