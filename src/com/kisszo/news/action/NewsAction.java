package com.kisszo.news.action;

import java.sql.SQLException;

import com.kisszo.news.bean.NewsBean;
import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.netty.controller.Path;
import com.kisszo.news.netty.http.WHTTPResponse;
import com.kisszo.news.netty.http.WHttpRequest;
import com.kisszo.news.netty.manager.ApiManager;
import com.kisszo.news.usecase.NewsUseCase;

@Path(path="/news")
public class NewsAction extends BaseAction{
	
	private NewsBean bean;

	public NewsAction(WHttpRequest httpRequest, WHTTPResponse httpResponse) {
		super(httpRequest, httpResponse);
	}

	@Override
	public Class beanClass() {
		return NewsBean.class;
	}

	@Override
	public Object getBean() {
		return this.bean;
	}

	@Override
	public void setBean(Object bean) {
		this.bean = (NewsBean) bean;
	}
	
	
	@Path(path="/addNews")
	public String addNews() throws ClassNotFoundException, SQLException {
		NewsUseCase newsUseCase = ApiManager.getInstance().getNewsUseCase();
		String response = newsUseCase.addNews(bean);
		return response;
	}
	
	
}
