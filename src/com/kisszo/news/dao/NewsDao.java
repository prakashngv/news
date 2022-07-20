package com.kisszo.news.dao;

import java.sql.SQLException;

import com.google.gson.JsonArray;
import com.kisszo.news.databaseschema.News;
import com.kisszo.news.dbpojo.AbstractMysqlPojo;
import com.kisszo.news.dbpojo.NewsPojo;
import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.netty.mysql.AbstractMysqlDatabaseManager;
import com.kisszo.news.netty.mysql.MySqlTransform;
import com.kisszo.news.netty.mysql.MysqlUpdate;

public class NewsDao extends AbstractMysqlDatabaseManager{

	@MySqlTransform
	public String createNews(NewsPojo newsPojo) {
		return "success";
	}
	
	@MySqlTransform
	public String persistPojo(AbstractMysqlPojo pojo) {
		return "success";
	}

	@MysqlUpdate(condition={News.ID},updateFlieds= {News.UPDATED_DATE,News.NAME})
	public String updateNews(NewsPojo newsPojo) {
		return "success";
	}

	public JsonArray getNews() throws ClassNotFoundException, SQLException, KisszoException {
		String sql = "select * from news order by createdDate desc";
		JsonArray response = executeQuery(sql);
		return response;
	}
}
