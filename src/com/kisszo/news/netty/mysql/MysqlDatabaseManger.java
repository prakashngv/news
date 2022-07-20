package com.kisszo.news.netty.mysql;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.kisszo.news.bean.AbstractBean;
import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.netty.manager.KisszoCalendar;

public class MysqlDatabaseManger extends AbstractMysqlDatabaseManager{
	private AbstractBean bean;
	
	public Object handleTransaction(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("Database Interceptor being called");
		String uniqueId = "";
		String clazzName = pjp.getSignature().getDeclaringTypeName(); 
		String methodName = pjp.getSignature().getName();
		Boolean hasException = false;
		Object result = null;
		String waffExc = "failed";
		String message = "";
		long startTime = System.currentTimeMillis();
		String startDate = KisszoCalendar.getInstance().getCurrentDate("GMT+05:30");
		
		MethodSignature signature = (MethodSignature) pjp.getSignature();
	    Method method = signature.getMethod();
	    MysqlDatabase mysqlAnnotation = method.getAnnotation(MysqlDatabase.class);
	    boolean isNonTx = mysqlAnnotation.IsNonTX();
	    try {
	    	if(!isNonTx){
		    	openConnection();
		    }else{
		    	openNonTXConnection();
		    }
			
			try{
				for(int i=0;i<3;i++){
					try{
						try {
							result = pjp.proceed(pjp.getArgs());
						
						}catch (KisszoException e) {
							hasException = true;
							// TODO: handle exception
							waffExc = e.getMessage();
							System.out.println("WafforExp "+e.getMessage());
						}catch (Throwable e) {
							// TODO: handle exception
							e.printStackTrace();
							new KisszoException("Exception occured in trace "+clazzName+"."+methodName+"-"+e.getMessage(),e);hasException = true;
							waffExc = e.getMessage();
						}finally {
							if(hasException){
								if(!isNonTx){
									rollBack();
								}
								result = waffExc;
							}else{
								if(!isNonTx){
									commit();
								}
								
							}
						}
						break;
					}
					finally {
						
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				new KisszoException("Exception occured in Transaction : "+e.getMessage(),e);
			}finally{
				endConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
	    
		
		return result;
	}
	
	
}
