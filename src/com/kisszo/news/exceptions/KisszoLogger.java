package com.kisszo.news.exceptions;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.kisszo.news.netty.manager.KisszoProperties;


public class KisszoLogger {
	private static KisszoLogger instance = null;
	private static Logger auditLog  =  LogManager.getLogger("AuditLogger");
	private static Logger transLog  =  LogManager.getLogger("TransactionLogger");
	private static Logger cronLog  =  LogManager.getLogger("CronLogger");
	private static Logger readLog  =  LogManager.getLogger("ReadLogger");
	private static Logger usageLog  =  LogManager.getLogger("UsageLogger");
	private static Logger dataLog  =  LogManager.getLogger("DataLogger");
	private static Logger mqLog  =  LogManager.getLogger("MQLogger");
	private static Logger errorLog  =  LogManager.getLogger("ErrorLogger");
	
	private KisszoLogger(){
//		String log4jConfigFile = System.getProperty("user.dir")
//                + File.separator + "properties/log4j.properties";
//		//System.out.println("properties file "+log4jConfigFile);
//		PropertyConfigurator.configure(log4jConfigFile);
		
		File jarPath=new File(KisszoProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath()+"/../properties/log4j.properties";
        System.err.println("propertiesPath=>"+propertiesPath);
		PropertyConfigurator.configure(propertiesPath);
		
		
	}
	
	public static KisszoLogger getInstance(){
		if(instance==null){
			synchronized (KisszoLogger.class) {
				if(instance == null){
					instance = new KisszoLogger();
				}
			}
		}
		return instance;
	}
	
	public void writelog(String msgs,String type){
		
		KisszoProperties property = KisszoProperties.getInstance();
		
		if(type.equalsIgnoreCase("auditlog")){
			auditLog.debug(msgs);
		}else if(type.equalsIgnoreCase("tlog")){
			transLog.error(msgs);
		}else if(type.equalsIgnoreCase("cronlog")){
			cronLog.info(msgs);
		}else if(type.equalsIgnoreCase("readLog")){
			readLog.info(msgs);
		}else if(type.equalsIgnoreCase("usageLog")){
			usageLog.info(msgs);
		}else if(type.equalsIgnoreCase("dataLog")){
			dataLog.info(msgs);
		}else if(type.equalsIgnoreCase("mqLog")){
			mqLog.info(msgs);
		}else if(type.equalsIgnoreCase("errorLog")){
			errorLog.info(msgs);
		}
		
	}
	
	
	public class LoggerHandler implements Runnable{
		private String msgs;
		private String type;
		public  LoggerHandler(KisszoLogger logger,String msgs,String type) {
			// TODO Auto-generated constructor stub
			this.msgs = msgs;
			this.type = type;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(type.equalsIgnoreCase("auditlog")){
				auditLog.debug(msgs);
			}else if(type.equalsIgnoreCase("tlog")){
				transLog.error(msgs);
			}else if(type.equalsIgnoreCase("cronlog")){
				cronLog.info(msgs);
			}else if(type.equalsIgnoreCase("readLog")){
				readLog.info(msgs);
			}else if(type.equalsIgnoreCase("usageLog")){
				usageLog.info(msgs);
			}else if(type.equalsIgnoreCase("dataLog")){
				dataLog.info(msgs);
			}
		}
	}
	
	
}


