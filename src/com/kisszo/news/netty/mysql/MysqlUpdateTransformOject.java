package com.kisszo.news.netty.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.kisszo.news.dbpojo.AbstractMysqlPojo;
import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.exceptions.KisszoLogger;
import com.kisszo.news.netty.manager.KisszoCalendar;


public class MysqlUpdateTransformOject extends AbstractMysqlDatabaseManager{
	
	public Object handleTransaction(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("Transform DBObject Interceptor being called");
		System.out.println("------- Start -----"+Thread.currentThread().getId());
		int res = updateDbObject(pjp);
		System.out.println("------- End -----"+Thread.currentThread().getId());
		Object result = pjp.proceed(pjp.getArgs());
		return result;
	}
	
	private int updateDbObject(ProceedingJoinPoint pjp) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, KisszoException{
	String startDate = KisszoCalendar.getInstance().getCurrentDateByFormat("GMT+05:30", "yyyy-MM-dd'T'HH:mm:ssZZZ");
	long startTime = System.currentTimeMillis();
	String empty = " ";
		
    Object[] params = pjp.getArgs();
    AbstractMysqlPojo bean = (AbstractMysqlPojo) params[0];
    System.out.println("Class NAME : "+ bean.getClassName());
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    MysqlUpdate updateAnnotation = method.getAnnotation(MysqlUpdate.class);
    String[] updateFileds = updateAnnotation.updateFlieds();
    String[] conditions = updateAnnotation.condition();
    if(conditions.length > 0){
        Connection conn = getConnection();
        HashMap<String, Object> hashMap = resolveSql(updateFileds,conditions,bean);
        String sql = (String) hashMap.get("sql");
        System.out.println("Update SQL Query : "+sql);
        bean.setPreparedStatement(conn.prepareStatement(sql));
        if(updateFileds.length==0){
                @SuppressWarnings("unchecked")
                ArrayList<String> haSet =  (ArrayList<String>) hashMap.get("updatedfields");
                if(haSet.size() > 0){
                        updateFileds = new String[haSet.size()];
                        haSet.toArray(updateFileds);
                        haSet.clear();
                }
        }
       mapProperties(updateFileds,conditions,bean);
       int res =  bean.getPreparedStatement().executeUpdate();
       
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
	   
       return res;
    }else{
         throw new KisszoException("Atleast need one field to update record");
    }
   
}

private HashMap<String, Object> resolveSql(String[] updateFileds,String[] conditions,AbstractMysqlPojo bean) throws ClassNotFoundException, KisszoException{
        boolean flag = false;
        ArrayList<String> fieldArray = new ArrayList<String>();
         String sql="UPDATE "+bean.getClassName()+" SET ";
            if(updateFileds.length > 0){
                    for (String field : updateFileds) {
                                sql+=field+"=?, ";
                                flag = true;
                        }
                    if(updateFileds.length > 0 && flag)
                                sql = sql.substring(0,sql.length()-2);
            }else{
                    String clazzName = bean.getClass().getName();
                        Class clazz = Class.forName(clazzName);
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field1 : fields) {
                                DBProperty dbProperty = field1.getAnnotation(DBProperty.class);
                                if(dbProperty!=null){
                                        String fieldname = field1.getName();
                                        sql+=fieldname+"=?, ";
                                        flag = true;
                                        fieldArray.add(field1.getName());
                                }
                        }
                        if(fields.length > 0 && flag)
                                sql = sql.substring(0,sql.length()-2);
            }
            
            if(!flag){
                    throw new KisszoException("Atleast need one field to update record");
            }
            
            sql +=" where ";
            for (String cond : conditions) {
                        sql+=cond+"=? AND ";
                }
            sql = sql.substring(0,sql.length()-5);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sql", sql);
            hashMap.put("updatedfields", fieldArray);
            return hashMap;
	}
	
	
	private void mapProperties(String[] updateFileds,String[] conditions,AbstractMysqlPojo bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, SQLException, KisszoException{
		Class clazz= bean.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		HashMap<String, Method> methodMap = new HashMap<String,Method>();
		for (Method method : methods) {
			methodMap.put(method.getName().toLowerCase(),method);
		}
		Object obj = null;
		PreparedStatement st = bean.getPreparedStatement();
		int i = 1;
		setVariable(updateFileds,methodMap,st,obj,bean,i);
		setVariable(conditions,methodMap,st,obj,bean,updateFileds.length+1);
		
	}
	
	private void setVariable(String[] fileds,HashMap<String, Method> methods,PreparedStatement st,Object obj,AbstractMysqlPojo bean,int i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException{
		for (String  confieldName: fileds) {
			Method method = methods.get("get"+confieldName.toLowerCase())!=null?methods.get("get"+confieldName.toLowerCase()):(methods.get(confieldName.toLowerCase())!=null?methods.get(confieldName.toLowerCase()):null);
			if(method!=null){
				obj = method.invoke(bean);
				if(obj instanceof Integer){
					st.setInt(i++, (Integer)obj);
				}else if(obj instanceof Float){
					st.setFloat(i++, (Float)obj);
				}else if(obj instanceof Double){
					st.setDouble(i++, (Double)obj);
				}else if(obj instanceof String){
					st.setString(i++, (String)obj);
				}else if(obj instanceof String){
					st.setString(i++, (String)obj);
				}else if(obj instanceof Boolean){
					st.setBoolean(i++, (Boolean)obj);
				}else if(obj instanceof Long){
					st.setLong(i++, (Long)obj);
				}else if(obj==null){
					st.setString(i++, null);
				}
				System.out.println("Method "+method.getName()+" : "+obj);
			}
		}
	}

}
