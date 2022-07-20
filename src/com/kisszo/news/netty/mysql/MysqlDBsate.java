package com.kisszo.news.netty.mysql;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.uuid.Generators;
import com.kisszo.news.netty.controller.BeanPopulator;

public class MysqlDBsate {
	private Connection conn;
	private Long token;
	private String label;
	private Logger log = LogManager.getLogger(BeanPopulator.class);
	private UUID uuid;
	public MysqlDBsate(Connection conn,Long token,String label) throws SQLException{
		this.conn = conn;
		this.token = token;
		this.label = label;
	}
	
	private void generateUUID(){
		uuid = Generators.timeBasedGenerator().generate();
	}
	
	
	public void rollback() throws SQLException{
		if(conn!=null){
			conn.rollback();
		}
	}
	
	public void commit() throws SQLException{
		if(conn!=null){
			conn.commit();
		}
	}
	
	public void close() throws SQLException{
		if(conn!=null){
			conn.close();
		}
	}
	
	public Connection getConnection(){
		return this.conn; 
	}
	
	public String getUUID() {
		return uuid.toString();
	}
}
