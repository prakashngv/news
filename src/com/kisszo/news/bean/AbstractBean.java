package com.kisszo.news.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.netty.handler.codec.http.Cookie;
import lombok.Data;

@Data
public abstract class AbstractBean {

	protected String role;
	protected SimpleDateFormat currentDate = new SimpleDateFormat("y-M-d H:m:s");
	protected SimpleDateFormat currentDateMilliSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	protected String bodyContent;
	protected String path;
	protected String timeZone;
	protected String  uploadFilePath;
	protected String country;
	protected String browser;
	protected String ipAddress;
	protected String dataSize;
	protected String methodName;
	private Set<Cookie> cookies = new HashSet<Cookie>();
	protected String startDate;
	protected String endDate;
	protected String currentLang;
	
	public String getCurrentDate() {
		return currentDate.format(new Date());
	}
	public void setCurrentDate(SimpleDateFormat currentDate) {
		this.currentDate = currentDate;
	}
	public String getCurrentDateMilliSec() {
		return currentDateMilliSec.format(new Date());
	}
	public void setCurrentDateMilliSec(SimpleDateFormat currentDateMilliSec) {
		this.currentDateMilliSec = currentDateMilliSec;
	}
	
	
}
