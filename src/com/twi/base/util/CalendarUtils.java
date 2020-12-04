package com.twi.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.datetime.DateFormatter;

public class CalendarUtils {

	public static final long ONE_DAY_MS = 24 * 3600 * 1000;
	public static final long ONE_HOUR_MS = 3600 * 1000;
	
	/**
	 * makes the next even time counts from 00:00:00
	 * 
	 * @param date
	 * @param secondBase
	 * @return next even time counts from 00:00:00
	 */
	public static Date nextEvenSecondDate(Date date, int secondBase) {
		return evenSecondDateBase(date, secondBase, 1);
	}
	
	/**
	 * makes the previous even time counts from 00:00:00
	 * 
	 * @param date
	 * @param secondBase
	 * @return previous even time counts from 00:00:00
	 */
	public static Date evenSecondDate(Date date, int secondBase) {
		return evenSecondDateBase(date, secondBase, 0);
	}
	
	/**
	 * makes the previous even time counts from 00:00:00
	 * 
	 * @param date
	 * @param secondBase
	 * @return previous even time counts from 00:00:00
	 */
	public static Date previousEvenSecondDate(Date date, int secondBase) {
		return evenSecondDateBase(date, secondBase, -1);
	}
	
	/**
	 * makes the even time counts from 00:00:00 with adjust round
	 * 
	 * @param date
	 * @param secondBase
	 * @return even time counts from 00:00:00
	 */
	public static Date evenSecondDateBase(Date date, int secondBase,
	        int adjustRound) {
		if (date == null) {
			date = new Date();
		}
		long time = date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setLenient(true);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long nightTime = c.getTimeInMillis();
		int millisBase = secondBase * 1000;
		long millisDelta = ((time - nightTime) / millisBase + adjustRound)
		        * millisBase;
		long newTime = nightTime + millisDelta;
		return new Date(newTime);
	}
	
	/**
	 * get the datetime start from 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date dayBegin(Date date) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date dayBegin(String date) {
		Date d = parseDate(date);
		return dayBegin(d);
	}
	
	public static String dayBeginStr(String date) {
		Date d = parseDate(date);
		return format(dayBegin(d));
	}
	
	
	/**
	 * get the datetime start from 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date dayEnd(Date date) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	public static Date dayEnd(String date) {
		Date d = parseDate(date);
		return dayEnd(d);
	}
	
	public static String dayEndStr(String date) {
		Date d = parseDate(date);
		return format(dayEnd(d));
	}
	
	public static Date getDate(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}
	
	public static Date getDate(Date date, int interval, int field) {
		if (date == null)
			return null;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(field, interval);
		return calendar.getTime();
	}
	
	public static Date setValue(Date date, int field, int value) {
		if (date == null)
			return null;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(field, value);
		return calendar.getTime();
	}
	
	/**
	 * 
	 * @author zhengjc
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
	 * @author zhengjc
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
	 * @author zhengjc
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
	
	public static int getValue(Date date, int field) {
		if (date == null)
			return 0;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(field);
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取时间是星期几 0-6)
	 * @param date
	 * @return 设定文件 int 返回类型
	 * @throws
	 */
	public static int getDayOfWeek(Date date) {
		if (date == null)
			return 0;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK) - 1;
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取当天是星期几 （0-6）)
	 * @param date
	 * @return 设定文件 int 返回类型
	 * @throws
	 */
	public static int getToDayOfWeek() {
		return getDayOfWeek(new Date());
	}
	
	private static ThreadLocal<DateFormat> simpleDateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	private static ThreadLocal<DateFormat> timeFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};
	
	private static ThreadLocal<DateFormat> dayFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	public static final String formatDisplayDate(Date date) {
		String result = null;
		if (date != null) {
			Date now = new Date();
			int day = CalendarUtils.getValue(now, Calendar.DATE);
			int month = CalendarUtils.getValue(now, Calendar.MONTH);
			int year = CalendarUtils.getValue(now, Calendar.YEAR);
			int dayAppoint = CalendarUtils.getValue(date, Calendar.DATE);
			int monthAppoint = CalendarUtils.getValue(date, Calendar.MONTH);
			int yearAppoint = CalendarUtils.getValue(date, Calendar.YEAR);
			int diff = dayAppoint - day;
			if (yearAppoint == year && month == monthAppoint) {
				if (diff == 0) {
					result = CalendarUtils.timeFormat(date);
				} else if (diff == 1) {
					result = "明天 " + CalendarUtils.timeFormat(date);
				} else if (diff == 2) {
					result = "后天 " + CalendarUtils.timeFormat(date);
				} else {
					
					if (year == yearAppoint) {
						result = CalendarUtils.format(date, "MM-dd HH:mm");
					} else {
						result = CalendarUtils.format(date, "yyyy-MM-dd HH:mm");
					}
				}
			} else {
				if (year == yearAppoint) {
					result = CalendarUtils.format(date, "MM-dd HH:mm");
				} else {
					result = CalendarUtils.format(date, "yyyy-MM-dd HH:mm");
				}
			}
		}
		return result;
	}
	
	public static final String format(Date date) {
		try {
			return dateFormat.get().format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String format(Date date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String format(String dateStr, String pattern) {
		try {
			SimpleDateFormat formats = new SimpleDateFormat(pattern);
        	// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        	formats.setLenient(false);
			Date date = formats.parse(dateStr);
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String simpleFormat(Date date) {
		try {
			return simpleDateFormat.get().format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String timeFormat(Date date) {
		try {
			return timeFormat.get().format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final Date parse(String date) {
		try {
			return dateFormat.get().parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final Date simpleparse(String date) {
		try {
			return simpleDateFormat.get().parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final Date parseTime(String date) {
		try {
			return timeFormat.get().parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final Date parseDate(String date) {
		try {
			return dayFormat.get().parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final String formatTime(long time) {
		long seconds = time % (1000 * 60);
		int delta = (seconds == 0l) ? 0 : 1; //
		time = time / (1000 * 60) + delta; // in mins
		long hour = time / 60;
		String result = hour > 0 ? hour + "小时" : "";
		result += time % 60 + "分钟";
		return result;
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取相应格式的当前机器时间)
	 * @param format
	 * @return String
	 * @throws
	 */
	public static String getDateTime(String format) {
		Calendar cale = Calendar.getInstance();
		DateFormatter dateFormatter = new DateFormatter(format);
		return dateFormatter.print(cale.getTime(), Locale.getDefault());
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取默认格式(yyyy-MM-dd HH:mm:ss)的当前机器时间)
	 * @return String
	 * @throws
	 */
	public static String getDateTimeWithLine() {
		return getDateTime("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (返回年月日)
	 * @param dateTime
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return yyyy-MM-dd
	 * @throws
	 */
	public static String getDateYMD(String dateTime) {
		if (dateTime == null || dateTime.length() < 10) {
			return dateTime;
		}
		return dateTime.substring(0, 10);
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取当前日期小时)
	 * @param dateTime
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return String
	 * @throws
	 */
	public static String getDateHour(String dateTime) {
		return dateTime.substring(11, 13);
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取年[yyyy])
	 * @return String
	 * @throws
	 */
	public static String getDateYear() {
		return getDateTimeWithLine().substring(0, 4);
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (返回年月日)
	 * @return yyyy-MM-dd String
	 * @throws
	 */
	public static String getDateYMD() {
		return getDateTimeWithLine().substring(0, 10);
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (返回date1距离date2的天数)
	 * @param date1
	 *            格式:yyyy-MM-dd
	 * @param date2
	 *            格式:yyyy-MM-dd
	 * @return String
	 * @throws
	 */
	public static int getDiffDateDay(String date1, String date2) {
		try {
			if (StringUtils.isEmpty(date1)) {
				date1 = getDateYMD();
			}
			if (StringUtils.isEmpty(date2)) {
				date2 = getDateYMD();
			}
			if (!date1.equalsIgnoreCase(date2)) {
				date1 = date1.trim();
				date2 = date2.trim();
				String[] date1Str = { date1.substring(0, 4),
				        date1.substring(5, 7), date1.substring(8, 10) };
				String[] date2Str = { date2.substring(0, 4),
				        date2.substring(5, 7), date2.substring(8, 10) };
				Calendar cNow = Calendar.getInstance();
				Calendar cReturnDate = Calendar.getInstance();
				cNow.set(Calendar.YEAR, Integer.valueOf(date1Str[0]));
				cNow.set(Calendar.MONTH, Integer.valueOf(date1Str[1]));
				cNow.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date1Str[2]));
				cNow.set(Calendar.HOUR_OF_DAY, 0);
				cNow.set(Calendar.MINUTE, 0);
				cNow.set(Calendar.SECOND, 0);
				
				cReturnDate.set(Calendar.YEAR, Integer.valueOf(date2Str[0]));
				cReturnDate.set(Calendar.MONTH, Integer.valueOf(date2Str[1]));
				cReturnDate.set(Calendar.DAY_OF_MONTH,
				        Integer.valueOf(date2Str[2]));
				cReturnDate.set(Calendar.HOUR_OF_DAY, 0);
				cReturnDate.set(Calendar.MINUTE, 0);
				cReturnDate.set(Calendar.SECOND, 0);
				
				long todayMs = cNow.getTimeInMillis();
				long returnMs = cReturnDate.getTimeInMillis();
				long intervalMs = returnMs - todayMs;
				return (int) (intervalMs / (1000 * 86400));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (将日期字符串转化为MM/dd格式)
	 * @param dateStr
	 *            (yyyy-MM-dd)
	 * @return String
	 * @throws
	 */
	public static String getMonthDayFromDate(String dateStr) {
		if (StringUtils.isEmpty(dateStr) || dateStr.length() < 10) {
			return "";
		}
		return dateStr.substring(5, 7) + "/" + dateStr.substring(8, 10);
	}
	
	/**
	 * 时间加减
	 * @author zhengjc
	 * @Description: (时间加减)
	 * @param date
	 * @param field
	 * @param amount
	 * @return 设定文件 Date 返回类型
	 * @throws
	 */
	public static Date dateAddOrSub(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
		
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (获取当前日期数值)
	 * @return long
	 * @throws
	 */
	public static long getDateTime() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (当前日期相应秒数后日期[yyyy-MM-dd HH:mm:ss])
	 * @param seconds
	 * @return String
	 * @throws
	 */
	public static String getDateAddSeconds(int seconds) {
		Calendar calendar = Calendar.getInstance();
		return format(getDate(calendar.getTime(), seconds, Calendar.SECOND));
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (返回到当前的毫秒数)
	 * @param dateStr
	 * @return long
	 * @throws
	 */
	public static long diffNowMilliseconds(String dateStr) {
		long dateTime = parse(dateStr).getTime();
		return dateTime - getDateTime();
	}
	
	/**
	 * 
	 * @author zhengjc
	 * @Description: (返回页面显示日期[当年不显示年份与秒,往年不显示秒])
	 * @param dateStr
	 * @return String
	 * @throws
	 */
	public static String getDateFormat(String year, String dateStr) {
		if (!StringUtils.isEmpty(dateStr) && !StringUtils.isEmpty(year)) {
			if (dateStr.startsWith(year)) {
				// 当年
				return dateStr.substring(5, 16);
			} else {
				return dateStr.substring(0, dateStr.length() - 3);
			}
		}
		
		return null;
	}
	
	/**
	 * 获取上个月第一天
	 * @return
	 */
	public static Date getLastMonthFisrtDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	} 
	
	/**
	 * 获取上个月最后一天
	 * @return
	 */
	public static Date getLastMonthLastDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1); 
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}
	
	
	/**
	 * 获取当前月第一天
	 * @return
	 */
	public static Date getCurrentMonthFisrtDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
		return calendar.getTime();
	} 
	
	/**
	 * 获取当前月最后一天
	 * @return
	 */
	public static Date getCurrentMonthLastDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
		return calendar.getTime();
	}
	
	
	
	public static List<Map<String,Object>> getKeyValueForDate(String startDate,String endDate) {
        List<Map<String,Object>> list = null;
        try {
            list = new ArrayList<Map<String,Object>>();

            String firstDay = "";
            String lastDay = "";
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);// 定义起始日期

            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);// 定义结束日期

            Calendar dd = Calendar.getInstance();// 定义日期实例
            dd.setTime(d1);// 设置日期起始时间
            Calendar cale = Calendar.getInstance();

            Calendar c = Calendar.getInstance();
            c.setTime(d2);

            int startDay = d1.getDate();
            int endDay = d2.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Map<String,Object> keyValueForDate = null;

            while (dd.getTime().before(d2)) {// 判断是否到结束日期
                keyValueForDate = new HashMap<String,Object>();
                cale.setTime(dd.getTime());

                if(dd.getTime().equals(d1)){
                    cale.set(Calendar.DAY_OF_MONTH, dd
                            .getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastDay = sdf.format(cale.getTime());
                    keyValueForDate.put("startDate", sdf.format(d1));
                    keyValueForDate.put("endDate", lastDay);

                }else if(dd.get(Calendar.MONTH) == d2.getMonth() && dd.get(Calendar.YEAR) == c.get(Calendar.YEAR)){
                    cale.set(Calendar.DAY_OF_MONTH,1);//取第一天
                    firstDay = sdf.format(cale.getTime());

                    keyValueForDate.put("startDate", firstDay);
                    keyValueForDate.put("endDate", sdf.format(d2));

                }else {
                    cale.set(Calendar.DAY_OF_MONTH,1);//取第一天
                    firstDay = sdf.format(cale.getTime());

                    cale.set(Calendar.DAY_OF_MONTH, dd
                            .getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastDay = sdf.format(cale.getTime());

                    keyValueForDate.put("startDate", firstDay);
                    keyValueForDate.put("endDate", lastDay);

                }
                list.add(keyValueForDate);
                dd.add(Calendar.MONTH, 1);// 进行当前日期月份加1

            }

            if(endDay<startDay){
                keyValueForDate = new HashMap<String,Object>();

                cale.setTime(d2);
                cale.set(Calendar.DAY_OF_MONTH,1);//取第一天
                firstDay = sdf.format(cale.getTime());

                keyValueForDate.put("startDate", firstDay);
                keyValueForDate.put("endDate", sdf.format(d2));
                list.add(keyValueForDate);
            }
        } catch (ParseException e) {
            return null;
        }

        return list;
    }



}
