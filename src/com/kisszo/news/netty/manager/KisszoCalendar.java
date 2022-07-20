package com.kisszo.news.netty.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.kisszo.news.exceptions.KisszoException;


public class KisszoCalendar {
	private final long ONE_MINUTE_IN_MILLIS=60000;
	private String kisszoDateFormat = "yyyy-MM-dd HH:mm:ss";
	private static KisszoCalendar instance = null;
	private KisszoCalendar(){
		
	}
	
	public static KisszoCalendar getInstance(){
		if(instance == null){
			synchronized (KisszoCalendar.class) {
				if(instance == null){
					instance = new KisszoCalendar();
				}
			}
			
		}
		
		return instance;
	}
	
	public String addDay(int noDay){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, noDay);
		return convertedFormat.format(calendar.getTime());
	}
	
	public long addDayMilliSeconds(int noDay){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, noDay);
		return calendar.getTimeInMillis() / 1000;
	}
	
	public String addDay(int noDay,Date date){
		Calendar calendar = Calendar.getInstance();
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTime(date);
		calendar.add(Calendar.DATE, noDay);
		return convertedFormat.format(calendar.getTime());
	}
	public String addMonthStartDate(int noMon){
		Calendar calendar = Calendar.getInstance();
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// This function is used for add/subtract by months from current month start date
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, noMon);
		return convertedFormat.format(calendar.getTime());
	}
	public Date addMonth(int noMon){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, noMon);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public Date addTimetoDate(String time,Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Date startWorkingTime = convertDate(time, "h:mm a");
		calendar.set(Calendar.HOUR_OF_DAY,startWorkingTime.getHours());
		calendar.set(Calendar.MINUTE,startWorkingTime.getMinutes());
		Date sTime = calendar.getTime();
		return sTime;
	}
	
	public String addTimetoDate(String time, String date){
		Calendar calendar = Calendar.getInstance();
		if(date!=null && time!=null){
			DateFormat realFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date current = realFormat.parse(date);
				calendar.setTime(current);
				Date startWorkingTime = convertDate(time, "h:mm a");
				calendar.set(Calendar.HOUR_OF_DAY,startWorkingTime.getHours());
				calendar.set(Calendar.MINUTE,startWorkingTime.getMinutes());
				Date sTime = calendar.getTime();
				return realFormat.format(sTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String addCurrentTimetoDate(String timeZone, String date, String format){
		if(date!=null && format!=null){
			DateFormat realFormat = new SimpleDateFormat(format);
			try {
				Date current = realFormat.parse(date);
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				if(timeZone != null && !timeZone.equalsIgnoreCase("")){
					dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				}
				Calendar cal = Calendar.getInstance();
				DateFormat userDate = new SimpleDateFormat("yyyy-MM-dd");
				return userDate.format(current)+" "+dateFormat.format(cal.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String subtractHoursByDate(int hours, Date date, String format){
		DateFormat realFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, -+hours);
		Date sTime = cal.getTime();
		return realFormat.format(sTime);
	}
	
	public Date addDuration(int mins,Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		//calendar.set(Calendar.MINUTE,mins);
		//Date sTime = calendar.getTime();
		long t= calendar.getTimeInMillis();
		Date sTime = new Date(t + (mins * ONE_MINUTE_IN_MILLIS));
		return sTime;
	}
	
	public Date getDateOfDays(int days, String date, String format){
		Calendar calendar = Calendar.getInstance();
		try {
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			calendar.add(Calendar.DATE, +days);
			Date startWorkingTime = convertDate("11:59 PM", "h:mm a");
			calendar.set(Calendar.HOUR_OF_DAY,startWorkingTime.getHours());
			calendar.set(Calendar.MINUTE,startWorkingTime.getMinutes());
			Date sTime = calendar.getTime();
			return sTime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getDateOfDaysByString(int days, String date, String format){
		Calendar calendar = Calendar.getInstance();
		try {
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			calendar.add(Calendar.DATE, +days);
			Date startWorkingTime = convertDate("11:59 PM", "h:mm a");
			calendar.set(Calendar.HOUR_OF_DAY,startWorkingTime.getHours());
			calendar.set(Calendar.MINUTE,startWorkingTime.getMinutes());
			Date sTime = calendar.getTime();
			return realFormat.format(sTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Date convertStringToDate(String date, String format){
		Calendar calendar = Calendar.getInstance();
		try {
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			Date sTime = calendar.getTime();
			return sTime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String subDay(int noDay){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -+noDay);
		return convertedFormat.format(calendar.getTime());
	}
	
	public String subDayByDate(int noDay, Date date){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -+noDay);
		return convertedFormat.format(calendar.getTime());
	}
	
	public String subDay(int noDay, String timezone){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -+noDay);
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(timezone!=null && !timezone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		return currentDate.format(calendar.getTime());
	}
	
	
	public String getFirstDayOfMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return convertedFormat.format(calendar.getTime());
	}
	
	public String getFirstDayOfMonth(String timezone){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(timezone!=null && !timezone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		return currentDate.format(calendar.getTime());
	}
	
	public String getFirstDayOfLastMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return convertedFormat.format(calendar.getTime());
	}
	
	public String getFirstDayOfLastMonth(String timezone){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(timezone!=null && !timezone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		return currentDate.format(calendar.getTime());
	}
	
	public String getFirstDayOfNextMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return convertedFormat.format(calendar.getTime());
	}
	
	public String getLastDayOfMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertedFormat.format(calendar.getTime());
	}
	
	public String getLastDayOfNextMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertedFormat.format(calendar.getTime());
	}
	
	public String getLastDayOfLastMonth(){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertedFormat.format(calendar.getTime());
	}
	public String getLastDayOfGivenMonth(int month){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertedFormat.format(calendar.getTime());
	}
	public String getCurrentDate(String timeZone){
		
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(timeZone!=null && !timeZone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		//System.out.println("current date "+currentDate.format(new Date()));
		return currentDate.format(new Date());
	}
	
	public String getCurrentDay(String timeZone, String date){
		SimpleDateFormat currentDate = new SimpleDateFormat(date);
		if(timeZone!=null && !timeZone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		return currentDate.format(new Date());
	}
	
	public String getCurrentDateByFormat(String timeZone, String format){
		SimpleDateFormat currentDate = new SimpleDateFormat(format);
		if(timeZone!=null && !timeZone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		//System.out.println("current date "+currentDate.format(new Date()));
		return currentDate.format(new Date());
	}
	
	public long getCurrentMilliseconds(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long time = calendar.getTimeInMillis();
		return time;
	}
	
	public String getIndiaCurrentDate(){
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return currentDate.format(new Date());
	}
	
	//Convert string to date with format, return with string format
	public String convertDateFormatasString(String date,String format){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date!=null && format!=null){
			DateFormat realFormat = new SimpleDateFormat(format);
			try {
				Date current = realFormat.parse(date);
				return convertedFormat.format(current);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//Convert string to date with format, return with string format
	public String convertDateFormat(String date,String format,String conFormat){
		//System.out.println("Before Converted Date time : "+date);
		if(date!=null && format!=null){
			DateFormat realFormat = new SimpleDateFormat(format);
			DateFormat conFormat1 = new SimpleDateFormat(conFormat);
			try {
				Date current = realFormat.parse(date);
				return conFormat1.format(current);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//Convert string to date with format, return with date format
	public Date getconvertDateFormat(String date,String format,String conFormat){
		//System.out.println("Before Converted Date time : "+date);
		if(date!=null && format!=null){
			DateFormat realFormat = new SimpleDateFormat(format);
			DateFormat conFormat1 = new SimpleDateFormat(conFormat);
			try {
				Date current = realFormat.parse(date);
				System.out.println("Converted Date time : "+current.toLocaleString());
				return conFormat1.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//Convert string to date, return with date format 
	public Date convertDate(String date,String format){
		if(date!=null && format!=null){
			DateFormat realFormat = new SimpleDateFormat(format);
			try {
				return realFormat.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String convertDateFormat(Date date){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return convertedFormat.format(date);
	}
	
	public String convertDateFormat(Date date,String format){
		DateFormat realFormat = new SimpleDateFormat(format);
		return realFormat.format(date);
	}
	
	public int getDayOfWeak(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day;
	}
	
	public String getFirstDayOfWeak(Date date){
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		return convertedFormat.format(calendar.getTime());
	}
	
	public int getDaysbetweenDate(Date date1,Date date2){
		final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
		int diffInDays = (int) ((date1.getTime() - date2.getTime())/ DAY_IN_MILLIS );
		return (diffInDays * -1) + 1;
	}
	
	public int getHoursbetweenDate(Date date1,Date date2){
		final long HOURS_IN_MILLIS = 1000 * 60 * 60;
		int diffInHours = (int) ((date1.getTime() - date2.getTime())/ HOURS_IN_MILLIS );
		return diffInHours * -1;
	}
	
	public int getMinsbetweenDate(Date date1,Date date2){
		final long MINS_IN_MILLIS = 1000 * 60;
		int diffInMins = (int) ((date1.getTime() - date2.getTime()) / MINS_IN_MILLIS );
		return diffInMins * -1;
	}
	
	public String changeDateFormat(String date,String parseFormat,String changeForamt) {
		if(date!=null && changeForamt!=null){
			DateFormat realFormat = new SimpleDateFormat(parseFormat);
			DateFormat changeParseFormat = new SimpleDateFormat(changeForamt);
			try {
				Date d = realFormat.parse(date);
				return changeParseFormat.format(d);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public int differenceBetweenDate(String parseFormat, String fromDate ,String toDate) throws ParseException {
		if(parseFormat!=null && fromDate!=null && toDate!=null){
			DateFormat realFormat = new SimpleDateFormat(parseFormat);
			Date from = realFormat.parse(fromDate);
			Date to = realFormat.parse(toDate);
			long difference = from.getTime() - to.getTime();
			int daysBetween = (int) (difference / (1000*60*60*24));
			return daysBetween;
		}
		return 0;
	}
	
	public String addMonthStartDate(int noMon, String format, String timezone){
		Calendar calendar = Calendar.getInstance();
		// This function is used for add/subtract by months from current month start date
		SimpleDateFormat currentDate = new SimpleDateFormat(format);
		if(timezone!=null && !timezone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, noMon);
		return currentDate.format(calendar.getTime());
	}
	
	public String addDaysWithDate(int days, String date, String format) throws ParseException{
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		if(date != null){
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			calendar.add(Calendar.DATE, days);
			return convertedFormat.format(calendar.getTime());
		}
		return null;
	}
	
	public String addWeeksWithDate(int weeks, String date, String format) throws ParseException{
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		if(date != null){
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			calendar.add(Calendar.DATE, weeks*2);
			return convertedFormat.format(calendar.getTime());
		}
		return null;
	}
	
	public String addMonthsWithDate(int month, String date, String format) throws ParseException{
		DateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		if(date != null){
			DateFormat realFormat = new SimpleDateFormat(format);
			Date current = realFormat.parse(date);
			calendar.setTime(current);
			calendar.add(Calendar.MONTH, month);
			return convertedFormat.format(calendar.getTime());
		}
		return null;
	}
	
	public String convertGMTToIST(String date1, String timezone){
		System.out.println("date1 "+date1+" timezone "+timezone);
		if(date1 != null && timezone != null){
			try {
				DateFormat realFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				realFormat.setTimeZone(TimeZone.getTimeZone(timezone)); // better than using IST
				Date date = realFormat.parse(date1);
				
				realFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
				Date date2 = realFormat.parse(realFormat.format(date));
				realFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30")); // UTC timezone
				System.out.println("date2 "+realFormat.format(date2));
				return realFormat.format(date2);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return "";
	}
		
	public String addMonthWithTimeZone(int noMon,String timeZone){		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, noMon);
		calendar.set(Calendar.DAY_OF_MONTH, 1);		
		SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(timeZone!=null && !timeZone.equalsIgnoreCase("")){
			currentDate.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		return currentDate.format(calendar.getTime());		
	}
	
	public String addDay(String timeZone, int noDay){
		Calendar calendar = Calendar.getInstance();
		DateFormat convertedFormat = new SimpleDateFormat(kisszoDateFormat);
		if(timeZone != null && !timeZone.equalsIgnoreCase("")){
			convertedFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, noDay);
		return convertedFormat.format(calendar.getTime());
	}
	
	public Map<String,String> dateRange(String timeZone, String filters,String startDate, String endDate){
		switch (filters) {
				case "month":
					startDate = this.getFirstDayOfMonth();
					endDate = this.addDay(timeZone,0);
					break;
				case "today":
					startDate = this.addDay(timeZone,0);
					endDate = this.addDay(timeZone,0);
					break;
				case "yesterday":
					startDate = this.addDay(timeZone,-1);
					endDate = this.addDay(timeZone,-1);
					break;
				case "last7":
					startDate = this.addDay(timeZone,-7);
					endDate = this.addDay(timeZone,-1);
					break;
				case "last14":
					startDate = this.addDay(timeZone,-14);
					endDate = this.addDay(timeZone,-1);
					break;
				case "last30":
					startDate = this.addDay(timeZone,-30);
					endDate = this.addDay(timeZone,-1);
					break;
				case "tomorrow":
					startDate = this.addDay(timeZone,1);
					endDate = this.addDay(timeZone,1);
					break;
				case "next7":
					startDate = this.addDay(timeZone,1);
					endDate = this.addDay(timeZone,7);
					break;
				case "next30":
					startDate = this.addDay(timeZone,1);
					endDate = this.addDay(timeZone,30);
					break;
				case "nextmonth":
					startDate = this.getFirstDayOfNextMonth();
					endDate = this.getLastDayOfNextMonth();
					break;
				case "custom":
					startDate = this.convertDateFormatasString(startDate, "dd MMM, yyyy");
					endDate = this.convertDateFormatasString(endDate, "dd MMM, yyyy");
					break;
				default:
					break;
		}
		startDate = startDate.replace(startDate.substring(11, 19), "00:00:00");
		endDate = endDate.replace(endDate.substring(11, 19), "23:59:59");
		Map<String, String> dateRange = new HashMap<String,String>();
		dateRange.put("startDate", startDate);
		dateRange.put("endDate", endDate);
		return dateRange;
	}
	
	public String convertToEndDateTime(String date) {
		try {
			date = date.replace(date.substring(11, 19), "23:59:59");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public String convertToStartDateTime(String date) {
		try {
			date = date.replace(date.substring(11, 19), "00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public String addTimetoGivenDate(String time, String date){
		if(date!=null && time!=null){
			Calendar calendar = Calendar.getInstance();
			DateFormat realFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date current = realFormat.parse(date);
				calendar.setTime(current);
				System.out.println("time:"+time);
				Date startWorkingTime = convertDate(time, "h:mm:ss a");
				calendar.set(Calendar.HOUR_OF_DAY,startWorkingTime.getHours());
				calendar.set(Calendar.MINUTE,startWorkingTime.getMinutes());
				calendar.set(Calendar.SECOND,startWorkingTime.getSeconds());
				Date sTime = calendar.getTime();
				return realFormat.format(sTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				new KisszoException("addTimetoGivenDate", e);
				e.printStackTrace();
			}
		}
		return "";
	}
}
