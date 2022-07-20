package com.kisszo.news.usecase;

import com.kisszo.news.bean.NewsBean;
import com.kisszo.news.bl.NewsSvc;
import com.kisszo.news.netty.manager.ApiManager;

public class NewsUseCase {
	
	public String addNews(NewsBean bean) {
		NewsSvc newsSvc = ApiManager.getInstance().getNewsSvc();
		String response = newsSvc.addNews(bean);
		return response;
	}

}
