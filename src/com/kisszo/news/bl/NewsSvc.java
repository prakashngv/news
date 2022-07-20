package com.kisszo.news.bl;

import com.kisszo.news.bean.NewsBean;
import com.kisszo.news.dao.NewsDao;
import com.kisszo.news.dbpojo.NewsPojo;
import com.kisszo.news.netty.manager.ApiManager;
import com.kisszo.news.netty.mysql.AbstractMysqlDatabaseManager;
import com.kisszo.news.netty.mysql.MysqlDatabase;

public class NewsSvc extends AbstractMysqlDatabaseManager{


	@MysqlDatabase
	public String addNews(NewsBean bean) {
		NewsDao newsDao = ApiManager.getInstance().getNewsDao();
		NewsPojo newsPojo = new NewsPojo(bean.getNews());
		newsPojo.setId(GenerateUniqueId());
		return newsDao.createNews(newsPojo);
	}


}
