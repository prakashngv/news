package com.kisszo.news.bean;

import com.kisszo.news.models.NewsModel;

import lombok.Data;

@Data
public class NewsBean extends AbstractBean{

	private String id;
	private String headLine;
	private String tags;
	private String content;
	private String imageIds;
	private String metaData;
	private String categoryId;
	private String geoLocationId;
	private Boolean isDeleted;
	private String createdDate;
	private String updatedDate;
	private String status;
	private String newsType;
	
	private NewsModel news;
	
	
}
