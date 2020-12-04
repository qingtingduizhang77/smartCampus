package com.twi.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.util.Assert;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils{
	public static final String C_TIME_PATTON_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String C_DATE_PATTON_DEFAULT = "yyyy-MM-dd";

	public static SimpleDateFormat TIME11 = new SimpleDateFormat("MM-dd HH:mm");
	public static SimpleDateFormat TIME19 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat DATE8 = new SimpleDateFormat("yyyyMMdd");
	public static String[] dataStringFormats = { "yyyyMMddHHmmss", "yyyyMMdd","yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };

	public static String longToString8(long date) {
		return DATE8.format(new Date(date));
	}

	public static String long14ToString19(Long date) {
		if (date == null)
			return null;
		return TIME19.format(stringToDate(date.toString()));
	}

	public static String longToString19(long date) {
		return TIME19.format(new Date(date));
	}

	public static String todayString8() {
		return longToString8(System.currentTimeMillis());
	}

	public static String yestodayString8() {
		return longToString8(System.currentTimeMillis() - 24 * 3600 * 1000);
	}

	public static long longToLong14(long date) {
		return Long.parseLong(dateToString(new Date(date), "yyyyMMddHHmmss"));
	}

	public static String dateToString(Date date, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}

	public static long long8ToLong(Long date) {
		return stringToDate(date.toString()).getTime();
	}

	public static Date stringToDate(String dateString) {
		Assert.notNull(dateString, "param is null in stringToDate()");
		Date date = null;
		try {
			date = parseDate(dateString, dataStringFormats);
		} catch (ParseException e) {
			// throw new Exception("string to Date format failed:" +
			// dateString);
		}
		return date;
	}

	

	public static void string14ToUtc(long time) {
	}


	
	/**
	 * 日期格式化模板：只包含日期
	 */
	public static final String FORMAT_DATE = "yyyy-MM-dd";

	/**
	 * 日期格式化模板：只包含日期
	 */
	public static final String FORMAT_DATE_OTHER = "yyyy/MM/dd";
	
	/**
	 * 日期格式化模板：包含日期和时间
	 */
	public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 日期格式化模板：包含日期和时间且无间隔
	 */
	public static final String FORMAT_DATE_TIME2 = "yyyyMMddHHmmss";

	/**
	 * 日期格式化模板：包含日期和时间（精确到毫秒）
	 */
	public static final String FORMAT_DATE_MS = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 日期格式化模板：包含日期和时间且无间隔（精确到毫秒）
	 */
	public static final String FORMAT_DATE_MS2 = "yyyyMMddHHmmssSSS";

	/**
	 * 将字符串格式的日期解析成日期对象
	 * 
	 * @param source
	 *            日期字符串
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String source) throws ParseException {
		try {
			return parse(source, FORMAT_DATE_TIME);
		} catch (Exception e) {
			return parse(source, FORMAT_DATE);
		}
	}

	/**
	 * 将字符串格式的日期解析成日期对象
	 * 
	 * @param source
	 *            日期字符串
	 * @param format
	 *            字符串的格式化模板
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String source, String format)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(source);
	}

	/**
	 * 获取指定日期处于一年中的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}


	/**
	 * 判断第一个时间是否处于第二个时间之前
	 */
	public static boolean isBefore(Date firstDate, Date secondDate) {
		return getSecondsBetween(firstDate, secondDate) > 0;
	}

	/**
	 * 判断第一个时间是否处于第二个时间之后
	 */
	public static boolean isAfter(Date firstDate, Date secondDate) {
		return getSecondsBetween(firstDate, secondDate) < 0;
	}

  	

	/**
	 * 将日期转换为字符串格式（"yyyy-MM-dd HH:mm:ss"）
	 * 
	 * @param date
	 * @return
	 */
	public static String getAsString(Date date) {
		return getAsString(date, C_TIME_PATTON_DEFAULT);
	}

	/**
	 * 将日期转换为字符串格式
	 * 
	 * @param date
	 * @param format
	 *            格式化字符串
	 * @return
	 */
	public static String getAsString(Date date, String format) {
		if (date == null) {
			return null;
		}

		if (format == null || format.length() == 0) {
			format = C_TIME_PATTON_DEFAULT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	
	/**
	 * 将时间调整为0点0分0秒<br>
	 * eg: 2013-07-03 13:51:23 --> 2013-07-03 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date aAndsDay(Date date,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+day);
		return calendar.getTime();
	}

	/**
	 * 将时间调整为23点59分59秒<br>
	 * eg: 2013-07-03 13:51:23 --> 2013-07-03 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 获取两个时间之间相差的天数
	 * 
	 * @param startDate
	 *            较早的时间
	 * @param endDate
	 *            较晚的时间
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * 获取指定时间为当天的第x分钟
	 * 
	 * @param date
	 * @return
	 */
	public static Long getMinuteOfDay(Date date) {
		Date startTime = getStartOfDay(date);
		return (date.getTime() - startTime.getTime()) / 60000;
	}
	
	/**
	 * 获取两个时间之间相差的秒数
	 * 
	 * @param startDate
	 *            较早的时间
	 * @param endDate
	 *            较晚的时间
	 */
	public static Long getSecondsBetween(Date startDate, Date endDate) {
		return (endDate.getTime() - startDate.getTime()) / 1000;
	}
	
	
	/**
	 * @Description: (时间加减)
	 * @param date
	 * @param field
	 * @param amount
	 * @return
	 */
	public static Date dateAddOrSub(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}
	
	/**
	 * 
	 * @Description: (根据一个开始和结束时间获取这段时间列表，按天递增)
	 * @param startDate
	 * @param endDate
	 * @return 设定文件 List<String> 返回类型
	 * @throws
	 */
	public static List<String> getDayList(Date startDate, Date endDate) {
		if (startDate == null || endDate == null)
			return null;
		List<String> dates = new ArrayList<String>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		Date tempTime = calendar.getTime();
		while (!tempTime.after(endDate)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			dates.add(format(tempTime, "yyyy/MM/dd"));
			tempTime = calendar.getTime();
		}
		return dates;
	}
	
	/**
	 * 
	 * @Description: (根据一个开始和结束时间获取这段时间列表，按天递增)
	 * @param startDate
	 * @param endDate
	 * @param formatStr
	 * @return 设定文件 List<String> 返回类型
	 * @throws
	 */
	public static List<String> getDayList(Date startDate, Date endDate,
	        String formatStr) {
		if (startDate == null || endDate == null)
			return null;
		List<String> dates = new ArrayList<String>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		Date tempTime = calendar.getTime();
		while (!tempTime.after(endDate)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			dates.add(format(tempTime, formatStr));
			tempTime = calendar.getTime();
		}
		return dates;
	}
	
	/**
	 * 
	 * @Description: (根据一个开始和结束时间获取这段时间列表，按天递增)
	 * @param startDate
	 *            (yyyy-MM-dd)
	 * @param endDate
	 *            (yyyy-MM-dd)
	 * @return List<String>
	 * @throws
	 */
	public static List<String> getDayList(String startDate, String endDate,
	        String formatStr) {
		if (startDate == null || endDate == null)
			return null;
		return getDayList(parseDate(startDate), parseDate(endDate), formatStr);
	}
	
	public static final Date parseDate(String date) {
		try {
			return dayFormat.get().parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static ThreadLocal<DateFormat> dayFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	public static final String format(Date date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String format(String dateStr, String pattern) {
		try {
			Date date = parse(dateStr);
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
}
