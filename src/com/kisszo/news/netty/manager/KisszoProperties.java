package com.kisszo.news.netty.manager;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KisszoProperties {
	
	private static KisszoProperties instance = null;
	Properties prop = new Properties();
	InputStream input = null;
	
	
	private KisszoProperties(){
		init();
	}
	
	
	public static KisszoProperties getInstance(){
		if(instance == null){
			synchronized (KisszoProperties.class) {
				if(instance == null){
					instance = new KisszoProperties();
				}
			}
		}
		return instance;
	}
	
	private void init(){
		try {
			 File jarPath=new File(KisszoProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
	        System.out.println(" propertiesPath-"+propertiesPath);
			//input = getClass().getClassLoader().getResourceAsStream(propertiesPath+"/properties/config.properties");
			input = new FileInputStream(propertiesPath+"/../properties/config.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String getDbHost(){
		String dbHost = prop.getProperty("activeDB");
		return dbHost;
	}
	
	public String getPassiveDbHost(){
		String passiveDbHost = prop.getProperty("passiveDB");
		return passiveDbHost;
	}
	
	public String getbillingDBDbHost(){
		String dbHost = prop.getProperty("billingDB");
		return dbHost;
	}
	
	public String getapplicationDBDbHost(){
		String dbHost = prop.getProperty("applicationDB");
		return dbHost;
	}
	
	public String getDBName(){
		String dbName = prop.getProperty("dbname");
		return dbName;
	}
	
	public String getbillingDBName(){
		String dbName = prop.getProperty("billingdbname");
		return dbName;
	}
	
	public String getapplicationDBName(){
		String dbName = prop.getProperty("applicationdbname");
		return dbName;
	}
	
	public String getDbUsername(){
		String dbUsername = prop.getProperty("username");
		return dbUsername;
	}
	
	
	public String getDbPassword(){
		String dbPassword = prop.getProperty("password");
		return dbPassword;
	}
	
	public String getbillingDbUsername(){
		String dbUsername = prop.getProperty("billingdbusername");
		return dbUsername;
	}
	
	public String getbillingDbPassword(){
		String dbPassword = prop.getProperty("billingdbpassword");
		return dbPassword;
	}
	
	public String getapplicationDbUsername(){
		String dbUsername = prop.getProperty("applicationdbusername");
		return dbUsername;
	}
	
	public String getapplicationDbPassword(){
		String dbPassword = prop.getProperty("applicationdbpassword");
		return dbPassword;
	}
	
	public String getDbPort(){
		String dbPort = prop.getProperty("port");
		return dbPort;
	}
	
	public String getbillingDbPort(){
		String dbPort = prop.getProperty("billingdbport");
		return dbPort;
	}
	
	public String getapplicationDbPort(){
		String dbPort = prop.getProperty("applicationdbport");
		return dbPort;
	}
	
	public String getDBHttpPort(){
		String dbhttpPort = prop.getProperty("httpport");
		return dbhttpPort;
	}
	
	public String getbillingDBHttpPort(){
		String dbhttpPort = prop.getProperty("billingdbhttpport");
		return dbhttpPort;
	}
	
	public String getapplicationDBHttpPort(){
		String dbhttpPort = prop.getProperty("applicationdbhttpport");
		return dbhttpPort;
	}
	
	public String getCronFlag(){
		String cronFlag = prop.getProperty("cronflag");
		return cronFlag;
	}
	
	
	public String getPropertyValue(String key){
		return prop.getProperty(key);
	}
	
	public String getEnvPropertyValue(String param){
		String env = System.getProperty("app.env")!=null?System.getProperty("app.env"):"";
		String reg = System.getProperty("app.reg")!=null?System.getProperty("app.reg"):"";
		String key = reg+"."+env+"."+param;
		System.err.println("key:"+key);
		return this.getPropertyValue(key);
	}
	
}
