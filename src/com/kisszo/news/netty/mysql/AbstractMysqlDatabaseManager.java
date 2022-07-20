package com.kisszo.news.netty.mysql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.exceptions.KisszoLogger;
import com.kisszo.news.netty.manager.KisszoCalendar;



public abstract class AbstractMysqlDatabaseManager {
	public void openConnection() throws SQLException, ClassNotFoundException{
		MysqlDatabaseConnectionPool.getInstance().openConnetion();
	}
	
	public void openNonTXConnection() throws SQLException, ClassNotFoundException{
		MysqlDatabaseConnectionPool.getInstance().openNonTXConnetion();
	}
	
	public void endConnection() throws SQLException{
		MysqlDatabaseConnectionPool.getInstance().closeConnection();
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		return MysqlDatabaseConnectionPool.getInstance().getConnection();
	}
	
	public void commit() throws SQLException{
		MysqlDatabaseConnectionPool.getInstance().commit();
	}
	
	public void rollBack() throws SQLException{
		MysqlDatabaseConnectionPool.getInstance().rollback();
	}
	
	public JsonArray executeQuery(String sql) throws SQLException, ClassNotFoundException, KisszoException{
		String date = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
		long startTime1 = System.currentTimeMillis();
		String empty = " ";
		boolean status=true;
		String exceptionName = " ";
		String exceptionMessage = " ";
		
		
		Connection connection = MysqlDatabaseConnectionPool.getInstance().getConnection();
		PreparedStatement preparedStatement  = connection.prepareStatement(sql);
		ResultSet rs = preparedStatement.executeQuery();
		
		String endDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
		long endTime1 = System.currentTimeMillis();
		
		JsonArray array = null;
		try{
			array = convertToJSON(rs);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new KisszoException("Result set parse error");
		}finally {
			preparedStatement.close();
			rs.close();
			try {
				String log = "readmysql;"+Thread.currentThread().getStackTrace()[2].getMethodName()+";"+date+";"+endDate+";"+startTime1+";"+endTime1+";"+empty+";"+empty+";"+empty+";"+empty+";"+empty+";"+sql+";"+(endTime1-startTime1)+";"+true+";"+empty+";"+status;
				KisszoLogger.getInstance().writelog(log,"usagelog");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return array;
	}
	
	public String getUUID(){
		return MysqlDatabaseConnectionPool.getInstance().getUUID();
	}
	
	public String GenerateUniqueId(){
		UUID uuid = null;
		synchronized (AbstractMysqlDatabaseManager.class) {
			uuid = Generators.timeBasedGenerator().generate();
		}
		return uuid.toString();
	}
	
	
	private JsonArray convertToJSON(ResultSet resultSet)throws Exception {
	     JsonArray jsonArray = new JsonArray();
	     while (resultSet.next()) {
	         int total_rows = resultSet.getMetaData().getColumnCount();
	         JsonObject obj = new JsonObject();
	         for (int i = 0; i < total_rows; i++) {
	             JsonElement element = convertObjectToJsonElement(resultSet.getObject(i+1));
	             if(element!=null){
		        	 obj.add(resultSet.getMetaData().getColumnLabel(i + 1)
		        		, element);
	         	 }
	         }
	         jsonArray.add(obj);
	     }
	     return jsonArray;	    
	 }
	 
	 private JsonElement convertObjectToJsonElement(Object obj){
		 
		 System.out.println("obj:"+obj);
		 JsonElement je = null;
		 if(obj!=null){
			 if(obj instanceof String){
				 je = new JsonPrimitive((String)obj);
			 }else if(obj instanceof Integer){
				 je = new JsonPrimitive((int)obj);
			 }else if(obj instanceof Boolean){
				 je = new JsonPrimitive((boolean)obj);
			 }else if(obj instanceof Float){
				 je = new JsonPrimitive((float)obj);
			 }else if(obj instanceof Double){
				je = new JsonPrimitive((double)obj);
			 }else if(obj instanceof Long){
				je = new JsonPrimitive((long)obj);
			 }else if(obj instanceof Timestamp){
					Timestamp timestamp = (Timestamp)obj;
					je = new JsonPrimitive(timestamp.toString());
			 }else {
				 je = new JsonPrimitive((String)obj);
			 }
		 }
		 return je;
	 }
	
}
