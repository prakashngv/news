package com.kisszo.news.netty.mysql;

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


public class MysqlDeleteTransformObject extends AbstractMysqlDatabaseManager{
	
	public Object handleTransaction(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("Transform DBObject Interceptor being called");
		System.out.println("------- Start -----"+Thread.currentThread().getId());
		int res = deleteDbObject(pjp);
		System.out.println("------- End -----"+Thread.currentThread().getId());
		Object result = pjp.proceed(pjp.getArgs());
		return result;
	}
	
	private int deleteDbObject(ProceedingJoinPoint pjp) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, KisszoException{
    Object[] params = pjp.getArgs();
    AbstractMysqlPojo bean = (AbstractMysqlPojo) params[0];
    System.out.println("Class NAME : "+ bean.getClassName());
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    MysqlDelete deleteAnnotation = method.getAnnotation(MysqlDelete.class);
    String[] deleteFileds = deleteAnnotation.deleteFlieds();
    String[] conditions = deleteAnnotation.condition();
    if(conditions.length > 0){
            Connection conn = getConnection();
            HashMap<String, Object> hashMap = resolveSql(deleteFileds,conditions,bean);
            String sql = (String) hashMap.get("sql");
            System.out.println("delete SQL Query : "+sql);
            bean.setPreparedStatement(conn.prepareStatement(sql));
            if(deleteFileds.length==0){
                    @SuppressWarnings("unchecked")
                    ArrayList<String> haSet =  (ArrayList<String>) hashMap.get("deletedfields");
                    if(haSet.size() > 0){
                            deleteFileds = new String[haSet.size()];
                            haSet.toArray(deleteFileds);
                            haSet.clear();
                    }
            }
           mapProperties(deleteFileds,conditions,bean);
           int res =  bean.getPreparedStatement().executeUpdate();
           return res;
    }else{
            throw new KisszoException("Atleast need one field to delete record");
    }
   
}

private HashMap<String, Object> resolveSql(String[] deleteFileds,String[] conditions,AbstractMysqlPojo bean) throws ClassNotFoundException, KisszoException{
        boolean flag = false;
        ArrayList<String> fieldArray = new ArrayList<String>();
        String sql="delete from "+bean.getClassName();
        if(conditions.length<=0){
        	  throw new KisszoException("Atleast need one field to delete record");
        }      
        sql +=" where ";
        for (String cond : conditions) {
                    sql+=cond+"=? AND ";
            }
        sql = sql.substring(0,sql.length()-5);
        HashMap<String, Object> hashMap = new HashMap<>();
        System.out.println("sql:"+sql);
        hashMap.put("sql", sql);
        hashMap.put("deletedfields", fieldArray);
        return hashMap;
	}
	
	
	private void mapProperties(String[] deleteFileds,String[] conditions,AbstractMysqlPojo bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, SQLException, KisszoException{
		Class clazz= bean.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		HashMap<String, Method> methodMap = new HashMap<String,Method>();
		for (Method method : methods) {
			methodMap.put(method.getName().toLowerCase(),method);
		}
		Object obj = null;
		PreparedStatement st = bean.getPreparedStatement();
		int i = 1;
		setVariable(deleteFileds,methodMap,st,obj,bean,i);
		setVariable(conditions,methodMap,st,obj,bean,deleteFileds.length+1);
		
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
