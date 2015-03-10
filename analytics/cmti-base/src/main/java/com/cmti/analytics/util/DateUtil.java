package com.cmti.analytics.util;

import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil extends org.apache.commons.lang.time.DateUtils {

	public static final long ONE_DAY = 3600000L*24L;
	
	//SimpleDateFormat is not thread safe
	private static final ThreadLocal<SimpleDateFormat> GMT_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {			
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> DATE_KEY_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {			
			return new SimpleDateFormat("yyyyMMdd");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> MINUTE_KEY_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {			
			return new SimpleDateFormat("yyyyMMddHHmm");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> YEAR_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {			
			return new SimpleDateFormat("yyyy");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> YEAR_MONTH_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {			
			return new SimpleDateFormat("yyyyMM");
		}
	};

	/*
	 * month is 0 based
	 */
	public static Date createDate(int year, int month, int day) {	
		GregorianCalendar cal = new GregorianCalendar(year, month, day);
		return cal.getTime();
	}
	
	public static String[] toKeyStrings(Date date) {
		String[] dateKeys = new String[4];
		dateKeys[0] = toKeyString(date);//day
		dateKeys[1] = toSaturdayKeyString(date);//week
		dateKeys[2] = toFirstDayOfMonthKeyString(date);//month
		dateKeys[3] = toFirstDayOfYearKeyString(date);//year
		
		return dateKeys;
	}

	public static int[] toKeyWeekHour(Date date) {
		 Calendar calendar = new GregorianCalendar();
		 calendar.setTime(date);

		 
		int[] dateKeys = new int[2];
		dateKeys[0] = calendar.get(Calendar.DAY_OF_WEEK);//Sun 1, Sat 7
		dateKeys[1] = calendar.get(Calendar.HOUR_OF_DAY); //14 for 14:43:43 
		
		return dateKeys;
	}
	
	
	public static String getYearString(Date d) {
		return YEAR_FORMAT.get().format(d);
	}

	public static String getYearMonthString(Date d) {
		return YEAR_MONTH_FORMAT.get().format(d);
	}

	public static String toKeyString(Date d) {
		return DATE_KEY_FORMAT.get().format(d);
	}

	public static String toKeyStringMinute(Date d) {
		return MINUTE_KEY_FORMAT.get().format(d);
	}

	public static Date parseKeyStringMinute(String s) {
		try {
			return MINUTE_KEY_FORMAT.get().parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	public static int toKeyInt(Date d) {
		return Integer.parseInt(toKeyString(d));
	}

	public static String toFirstDayOfMonthKeyString(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH , 1);
		
		Date firstDay=calendar.getTime();
		
		return toKeyString(firstDay);
	}

	public static String toFirstDayOfYearKeyString(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH , 1);
		calendar.set(Calendar.MONTH , 0);
		
		Date firstDay=calendar.getTime();
		
		return toKeyString(firstDay);
	}

	public static String toSaturdayKeyString(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		
		int diff = Calendar.SATURDAY - weekday;
		calendar.add(Calendar.DAY_OF_MONTH , diff);
		
		Date saturday=calendar.getTime();
		
		return toKeyString(saturday);
	}

	public static Date parseKeyString(String s) {
		try {
			return DATE_KEY_FORMAT.get().parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;		
	}
	/**
	 * Represent the input date in the form of "yyyy/MM/dd HH:mm:ss.SSS Z"
	 * 2014-03-18 17:07:43.272 -0700
	 * @param d
	 */
	public static String toGMTString(Date d) {
		return GMT_DATE_FORMAT.get().format(d);
	}

	public static Date parseGMTString(String s) {
		s=s.replace('_', ' ');//allow "2014-03-18+17:07:43.272_-0700"
		try {
			return GMT_DATE_FORMAT.get().parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	//s=2014-07-06 16:25:06
	public static Date parseNYString(String s) {
		s=s + ".000 -0500";//FIXME daylight saving
		return parseGMTString(s);
	}

	public static Date getIntervalBase(Date date, int min) {
		long newTime = getIntervalBase(date.getTime(), min);
		return new Date(newTime);
	}

	public static Date getCurrentIntervalBase(int min) {
		return new Date(getIntervalBase(System.currentTimeMillis(), min));
	}

	public static long getIntervalBase(long time, int min) {
		long res = time % (60000L*min);				
		return time - res;
	}

	public static String getRedisHistoryKey(int cell, Date date) {
		int[] dateKeys = toKeyWeekHour(date);
		return String.format("history:%s:%s:%s", cell, dateKeys[0], dateKeys[1]);
	}
	
	public static void main(String[] args) {
		String gmt="2014-07-06 16:19:06";
		Date gmtDate = parseNYString(gmt);
		System.out.println("gmtDate1="+gmtDate);
		
		int[] r = toKeyWeekHour(gmtDate);

		System.out.println("day in week="+r[0] +" h="+r[1]);
		
		
		long time = gmtDate.getTime();
		
		long res = time % 300000L;
		
		time -=res;
		gmtDate = new Date(time);

		System.out.println("10 min? gmtDate1="+gmtDate);

		gmt="2014-4-11 00:00:00.000 -0000";
		gmtDate = parseGMTString(gmt);
		System.out.println("gmtDate1="+gmtDate);

		gmt="2014-4-11 00:00:00.000 +0000";
		gmtDate = parseGMTString(gmt);
		System.out.println("gmtDate2="+gmtDate);
		System.out.println("gmtDate2="+toGMTString(gmtDate));

		Date oldest = new Date(2013-1900, 0, 1);//FIXME compare w new Date(System.currentTimeMillis() - 365L * 2L* DateUtil.ONE_DAY);
		System.out.println("oldest="+oldest);
		System.out.println("oldest2="+new Date(oldest.getTime()));
	
		GregorianCalendar cal = new GregorianCalendar(2013, 0, 1);		
		System.out.println("cal="+cal);
		System.out.println("cal2="+cal.getTime());
		System.out.println("cal2="+new Date(cal.getTime().getTime()));
		
		long now = System.currentTimeMillis();
		
		for(int i=-100; i<100; i++) {
			long that = now + i * 24* 3600000L;
			Date date = new Date(that);
			//String sat = toSaturdayKeyString(date);
			String sat = toFirstDayOfMonthKeyString(date);
			System.out.println(String.format("i=%s,  date=%s, sat=%s", i, date, sat));
			
			r = toKeyWeekHour(date);
			System.out.println("day in week="+r[0] +" h="+r[1]);
		}
	}
}




