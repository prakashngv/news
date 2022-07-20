package com.kisszo.news.dbpojo;

import java.sql.PreparedStatement;

import com.kisszo.news.models.NewsModel;
import com.kisszo.news.netty.manager.KisszoCalendar;
import com.kisszo.news.netty.mysql.AttributeType;
import com.kisszo.news.netty.mysql.DBProperty;

import lombok.Data;

@Data
public class NewsPojo extends AbstractMysqlPojo{
	
	public NewsPojo(NewsModel newsModel) {
		KisszoCalendar calendar = KisszoCalendar.getInstance();
		this.headLine = newsModel.getHeadLine();
		this.tags = newsModel.getTags();
		this.content = newsModel.getContent();
		this.imageIds = newsModel.getImageIds();
		this.metaData = newsModel.getMetaData();
		this.categoryId = newsModel.getCategoryId();
		this.geoLocationId = newsModel.getGeoLocationId();
		this.isDeleted = false;
		this.createdDate = calendar.getIndiaCurrentDate();
		this.updatedDate = calendar.getIndiaCurrentDate();
		this.status = newsModel.getStatus();
		this.newsType = newsModel.getNewsType();
	}
	
	private String className = "news";
	private PreparedStatement preparedStatement;
	
	@DBProperty(type=AttributeType.STRING)
	private String id;
	@DBProperty(type=AttributeType.STRING)
	private String headLine;
	@DBProperty(type=AttributeType.STRING)
	private String tags;
	@DBProperty(type=AttributeType.STRING)
	private String content;
	@DBProperty(type=AttributeType.STRING)
	private String imageIds;
	@DBProperty(type=AttributeType.STRING)
	private String metaData;
	@DBProperty(type=AttributeType.STRING)
	private String categoryId;
	@DBProperty(type=AttributeType.STRING)
	private String geoLocationId;
	@DBProperty(type=AttributeType.BOOLEAN)
	private Boolean isDeleted;
	@DBProperty(type=AttributeType.DATETIME)
	private String createdDate;
	@DBProperty(type=AttributeType.DATETIME)
	private String updatedDate;
	@DBProperty(type=AttributeType.STRING)
	private String status;
	@DBProperty(type=AttributeType.STRING)
	private String newsType;


}
