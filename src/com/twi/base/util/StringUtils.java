package com.twi.base.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	
	
	/**
	 * 字符串  不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean strIsNotNull(String str) {
		 if(str!=null && !str.trim().equals(""))
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
	
	/**
	 * list 不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean arrayIsNotNull(String[] array) {
		 if(array!=null && array.length>0)
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
	
	/**
	 * 数组 不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean listIsNotNull(List list) {
		 if(list!=null && list.size()>0)
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
	
	
	
	/**
	 * 扩展自org.apache.commons.lang3.StringUtils<br>
	 * 支持将字符串拆分为字符数组形式。<br>
	 * eg: split("abc","") = ["a","b","c"]
	 */
	public static String[] split(String str, String separatorChars) {
		if ("".equals(separatorChars)) {
			str = trimToEmpty(str);
			str = deleteWhitespace(str);
			String[] result = new String[str.length()];
			char[] chars = str.toCharArray();
			for (int i = 0, j = chars.length; i < j; i++) {
				result[i] = String.valueOf(chars[i]);
			}
			return result;
		} else {
			return org.apache.commons.lang3.StringUtils.split(str, separatorChars);
		}
	}

	/**
	 * 比较两字符串是否相等<br>
	 * null equals null = true<br>
	 * null equals "" = true<br>
	 * "" equals null = true<br>
	 * " " equals "" = true<br>
	 * "A" equals "a" = false
	 */
	public static boolean equals(String str1, String str2) {
		str1 = (str1 == null) ? "" : str1.trim();
		str2 = (str2 == null) ? "" : str2.trim();
		return str1.equals(str2);
	}

	/**
	 * 移除字符串中指定的一组字符串
	 * 
	 * @param str
	 * @param searchList
	 * @param max
	 *            最大移除数量
	 * @return
	 */
	public static String remove(String str, String[] searchList, int max) {
		for (String sub : searchList) {
			str = replace(str, sub, "", max);
		}
		return str;
	}

	/**
	 * 压缩字符串
	 */
	public static String compress(String str) {
		if (isBlank(str)) {
			return str;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			return out.toString("ISO-8859-1");
		} catch (IOException e) {
			return str;
		}
	}

	/**
	 * 解压缩字符串
	 */
	public static String uncompress(String str) {
		if (isBlank(str)) {
			return str;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
			return out.toString();
		} catch (IOException e) {
			return str;
		}
	}
	
	public static String getSendMsgTypeName(int type){
		if (type == 0) {
			return "全校家长";
		}else if (type == 1) {
			return "全校老师";
		}else if (type == 2) {
			return "全校";
		}else if (type == 3) {
			return "电话号码";
		}
		return "";
	}
	
	public static String randomUUID(){
        String uuid = UUID.randomUUID().toString();
        return StringUtils.remove(uuid, "-");
    }
	
	 public static String getOrderNoByTime() {
		 String nowDate = CalendarUtils.format(new Date(), "yyyyMMddHHmmss");
		 String result="";
		 Random random=new Random();
		 for(int i=0;i<5;i++){
			 result+=random.nextInt(10);
		 }
		 return "O"+nowDate+result;
	}
		 
}
