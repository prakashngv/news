package com.kisszo.news.dbpojo;

import java.sql.PreparedStatement;

public abstract class AbstractMysqlPojo {
	
	public AbstractMysqlPojo() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract PreparedStatement getPreparedStatement();
	public abstract void setPreparedStatement(PreparedStatement preparedStatement);
	public abstract String getClassName();
	public abstract String getId();
	public abstract void setId(String id);
	public abstract String getCreatedDate();
	public abstract void setCreatedDate(String createdDate);
	
	


}
