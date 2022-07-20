package com.kisszo.news.netty.mysql;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;

import com.kisszo.news.dbpojo.AbstractMysqlPojo;
import com.kisszo.news.exceptions.KisszoLogger;
import com.kisszo.news.netty.manager.KisszoCalendar;


public class MysqlTransFormDBObject extends AbstractMysqlDatabaseManager{
	public Object handleTransaction(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("Transform DBObject Interceptor being called");
		System.out.println("------- Start -----"+Thread.currentThread().getId());
		persistDbObject(pjp);
		System.out.println("------- End -----"+Thread.currentThread().getId());
		Object result = pjp.proceed(pjp.getArgs());
		return result;
	}
	
	private void persistDbObject(ProceedingJoinPoint pjp) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, ParseException{
		Object[] params = pjp.getArgs();
		AbstractMysqlPojo bean = (AbstractMysqlPojo) params[0];
		System.out.println("Class NAME : "+ bean.getClassName());
		createVertex(bean);
	}
	
	private void createVertex(Object val) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, ParseException{
		
		String startDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
		long startTime = System.currentTimeMillis();
		String empty = " ";
		
		String clazzName = val.getClass().getName();
		AbstractMysqlPojo bean = (AbstractMysqlPojo) val;
		Class clazz = Class.forName(clazzName);
		Connection conn = getConnection();
		String sql = buildSql(clazz,bean);
		bean.setPreparedStatement(conn.prepareStatement(sql));
		mapProperties(clazz,bean);
		System.out.println("Statment : "+bean.getPreparedStatement());
		bean.getPreparedStatement().executeUpdate();
		
		String endDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
		long endTime = System.currentTimeMillis();
		
		try {
			String methodPath = "";
			for (StackTraceElement ele : Thread.currentThread().getStackTrace()) {
				if(ele.getClassName().toString().toLowerCase().contains("svc")) {
					methodPath+=ele.getMethodName()+"/";
				}
			}
			System.err.println(methodPath);
			String log = "writemysql;"+methodPath+";"+startDate+";"+endDate+";"+startTime+";"+endTime+";"+empty+";"+empty+";"+empty+";"+empty+";"+empty+";"+empty+";"+(endTime-startTime)+";"+true+";"+empty+";"+true;
			KisszoLogger.getInstance().writelog(log,"usagelog");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	private void buildVertex(AbstractMysqlPojo bean,Object val) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, ArrayIndexOutOfBoundsException, ParseException{
		ResultSet rs= bean.getPreparedStatement().executeQuery();
		bean.setId(rs.getString("id"));
		rs.close();
		if(val!=null){
			if(val instanceof List){
				List<Object> array = (List<Object>)val;
				for (Object object : array) {
					resolveBulidVertex(bean, object);
				}
			}else if(!val.getClass().isArray()){
				resolveBulidVertex(bean, val);
			}else{
				for(int i=0;i<Array.getLength(val);i++){
					resolveBulidVertex(bean, Array.get(val, i));
				}
			}
		}
	}
	
	private void resolveBulidVertex(AbstractMysqlPojo bean,Object obj) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, ParseException{
		AbstractMysqlPojo obj1 = (AbstractMysqlPojo)obj;
		obj1.setCreatedDate(bean.getCreatedDate());
		createVertex(obj);
	}
	
		
		//Map Business Object to DB Object
	private void mapProperties(Class clazz,AbstractMysqlPojo bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, SQLException, ParseException{
		Field[] fields = clazz.getDeclaredFields();
		int i=1;
		for (Field field : fields) {
			DBProperty dbProperty = field.getAnnotation(DBProperty.class);
			if(dbProperty!=null){
				String fieldName = field.getName();
				AttributeType attributeType = dbProperty.type();
				Object obj = getPropetyValue(bean,fieldName);
				PreparedStatement st = bean.getPreparedStatement();
				System.out.println("obj:"+obj);
				if(obj!=null){
					switch(attributeType){
						case VERTEX:
							buildVertex(bean,obj);
							break;
						case DOUBLE:
							st.setDouble(i,(Double)obj);	 
							break;
						case STRING:
							st.setString(i,(String)obj);	 
							break;
						case INTEGER:
							st.setInt(i,(Integer)obj);	 
							break;
						case BOOLEAN:
							st.setBoolean(i,(Boolean)obj);	 
							break;
						case LONG:
							st.setLong(i,(Long)obj);	 
							break;
						case FLOAT:
							st.setFloat(i,(Float)obj);	 
							break;
						case DATETIME:
							System.err.println("DATETIME:"+obj.toString());
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							java.util.Date date = sdf1.parse(obj.toString());
							Timestamp timestamp = new java.sql.Timestamp(date.getTime());
							st.setTimestamp(i,timestamp);
					default:
						break;
					}
				}else{
					st.setString(i,null);
				}
				i++;
			}
			
		}
		
	}
		

		private Object getPropetyValue(AbstractMysqlPojo bean,String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			Class clazz= bean.getClass();
			Method[] methods = clazz.getDeclaredMethods();
			Object obj = null;
			for (Method method : methods) {
				if(method.getName().equalsIgnoreCase("get"+fieldName) || method.getName().equalsIgnoreCase("is"+fieldName) || method.getName().equalsIgnoreCase(fieldName)){
					obj = method.invoke(bean);
					System.out.println("Method "+method.getName()+" : "+obj);
				}
			}
			return obj;
		}
		
		
		private String buildSql(Class clazz, AbstractMysqlPojo bean) {
			String sql = "INSERT INTO "+bean.getClassName();
			String fieldsstr = "(";
			String values = " VALUES (";		
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				DBProperty dbProperty = field.getAnnotation(DBProperty.class);
				if(dbProperty!=null){
					fieldsstr += field.getName()+",";
					values += "?,";
				}
			}
			fieldsstr = fieldsstr.substring(0, fieldsstr.length()-1)+")";
			values = values.substring(0, values.length()-1)+")";
			sql+=fieldsstr+values;
			return sql;
		}
	
}
