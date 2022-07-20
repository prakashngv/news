package com.kisszo.news.netty.manager;

import com.kisszo.news.bl.NewsSvc;
import com.kisszo.news.dao.NewsDao;
import com.kisszo.news.usecase.NewsUseCase;

public class ApiManager extends AbstractComponentManager{
	private static ApiManager mgr = null;
	
	public static ApiManager getInstance(){
		if(mgr == null){
			synchronized (ApiManager.class) {
				if(mgr == null){
					mgr = new ApiManager();
				}
			}
		}
		return mgr;
	}
	
	public NewsUseCase getNewsUseCase(){
		return (NewsUseCase)getBean("newsUseCase");
	}
	
	public NewsSvc getNewsSvc(){
		return (NewsSvc)getBean("newsSvc");
	}
	
	public NewsDao getNewsDao(){
		return (NewsDao)getBean("newsDao");
	}
	
}
