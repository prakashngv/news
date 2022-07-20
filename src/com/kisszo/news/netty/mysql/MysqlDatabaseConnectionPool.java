package com.kisszo.news.netty.mysql;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kisszo.news.netty.controller.AnnotatedPathManager;
import com.kisszo.news.netty.manager.KisszoProperties;

public class MysqlDatabaseConnectionPool {
	private static MysqlDatabaseConnectionPool instance = null;
	
	Logger log = LogManager.getLogger(AnnotatedPathManager.class);

	private BasicDataSource factory = null;
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
//	public static final String DRIVER = "com.mysql.jdbc.Driver";
	static KisszoProperties wafforProp = KisszoProperties.getInstance();
	static String HostIp = wafforProp.getEnvPropertyValue("news.hostIp");
	static String Port = wafforProp.getEnvPropertyValue("news.port");
	static String dbName = wafforProp.getEnvPropertyValue("news.dbName");
    public static String URL = "jdbc:mysql://"+HostIp+":"+Port+"/"+dbName+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    public static final String USERNAME = wafforProp.getEnvPropertyValue("news.userName");
    public static final String PASSWORD = wafforProp.getEnvPropertyValue("news.password");
    
    private Map<Long, MysqlDBsate> states = new HashMap<Long,MysqlDBsate>();
	
	private MysqlDatabaseConnectionPool(){
		init();
	}
	
	public static MysqlDatabaseConnectionPool getInstance(){
		if(instance == null){
			synchronized (MysqlDatabaseConnectionPool.class) {
				if(instance == null){
					instance = new MysqlDatabaseConnectionPool();
				}
			}
		}
		return instance;
	}
	
	
	
	private void init(){
		System.err.println("URL:"+URL);
		factory = new BasicDataSource();
		factory.setDriverClassName(DRIVER);
		factory.setUrl(URL);
		factory.setUsername(USERNAME);
		factory.setPassword(PASSWORD);
		factory.setInitialSize(10);
		factory.setMaxActive(10);
		factory.setTestOnBorrow(true);
		factory.setValidationQuery("SELECT 1");
		factory.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		factory.setMaxIdle(3600);
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Long token  = Thread.currentThread().getId();
		Connection conn = states.get(token).getConnection();
		if(conn.isClosed()){
			openConnetion();
			return states.get(token).getConnection();
		}else{
			return conn;
		}
		
	}
	
	
	
	public void openConnetion() throws SQLException, ClassNotFoundException {
		Long token  = Thread.currentThread().getId();
		String label = Thread.currentThread().getName();
		System.out.println("Open connection for Thread id:"+ token+", name:"+label);
		Connection  conn =  factory.getConnection();
		if(conn!=null){
			conn.setAutoCommit(false);
			states.put(token, new MysqlDBsate(conn, token,label));
		}else{
			getConnection();
			openConnetion();
		}
	}
	
	public void openNonTXConnetion() throws SQLException, ClassNotFoundException {
		Long token  = Thread.currentThread().getId();
		String label = Thread.currentThread().getName();
		System.out.println("Open connection for Thread id:"+ token+", name:"+label);
		Connection  conn =  factory.getConnection();
		if(conn!=null){
			conn.setAutoCommit(true);
			states.put(token, new MysqlDBsate(conn, token,label));
		}
	}
	
	public void closeConnection() throws SQLException{
		Long token  = Thread.currentThread().getId();
		System.out.println("Close connection for Thread id:"+ token);
		//states.get(token).commit();
		states.get(token).close();
		states.remove(token);
	}
	
	public void commit() throws SQLException{
		Long token  = Thread.currentThread().getId();
		System.out.println("Commit connection for Thread id:"+ token);
		if(states.get(token)!=null)
		states.get(token).commit();
	}
	
	public void rollback() throws SQLException{
		Long token  = Thread.currentThread().getId();
		System.out.println("Rollback connection for Thread id:"+ token);
		if(states.get(token)!=null)
		states.get(token).rollback();
	}
	
	public String getUUID(){
		Long token  = Thread.currentThread().getId();
		return states.get(token).getUUID();
	}
	
}
