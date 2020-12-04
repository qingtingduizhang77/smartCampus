package com.twi.base.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据处理工具类
 */
public class DataUtils {
	/**
	 * 格式化double类型值
	 * 
	 * @param value
	 *            待格式化的数值
	 * @param scale
	 *            精度。比如：2表示保留小数点后2位
	 * @return
	 */
	public static double format(double value, int scale) {
		BigDecimal decimal = new BigDecimal(value);
		return format(decimal, scale);
	}

	private static double format(BigDecimal decimal, int scale) {
		return format(decimal, scale, RoundingMode.HALF_UP);
	}

	private static double format(BigDecimal decimal, int scale, RoundingMode model) {
		return decimal.setScale(scale, model).doubleValue();
	}

	/**
	 * Double数值加法运算
	 * 
	 * @param scale
	 *            精度。比如：2表示保留小数点后2位
	 */
	public static double add(double value1, double value2, int scale) {
		BigDecimal bd1 = new BigDecimal(value1);
		BigDecimal bd2 = new BigDecimal(value2);
		return format(bd1.add(bd2), scale);
	}

	/**
	 * Double数值减法运算
	 * 
	 * @param scale
	 *            精度。比如：2表示保留小数点后2位
	 */
	public static double subtract(double value1, double value2, int scale) {
		BigDecimal bd1 = new BigDecimal(value1);
		BigDecimal bd2 = new BigDecimal(value2);
		return format(bd1.subtract(bd2), scale);
	}

	/**
	 * Double数值乘法运算
	 * 
	 * @param scale
	 *            精度。比如：2表示保留小数点后2位
	 */
	public static double multiply(double value1, double value2, int scale) {
		BigDecimal bd1 = new BigDecimal(value1);
		BigDecimal bd2 = new BigDecimal(value2);
		return format(bd1.multiply(bd2), scale);
	}

	/**
	 * Double数值除法运算
	 * 
	 * @param scale
	 *            精度。比如：2表示保留小数点后2位
	 */
	public static double divide(double value1, double value2, int scale) {
		BigDecimal bd1 = new BigDecimal(value1);
		BigDecimal bd2 = new BigDecimal(value2);
		return format(bd1.divide(bd2, 4, RoundingMode.HALF_UP), scale);
	}

	/**
	 * 统计length选count的组合数量
	 * 
	 * @param length
	 * @param count
	 * @return
	 */
	public static int getTotalCombination(int length, int count) {
		if (count <= 0 || length < count) {
			return 0;
		}
		if (length == count) {
			return 1;
		}
		int tempTotal = 1;
		int tempCount = 1;
		for (int i = 0; i < count; i++) {
			tempTotal *= (length - i);
			tempCount *= (count - i);
		}
		return tempTotal / tempCount;
	}

	/**
	 * 获取阶加
	 */
	public static int getFactorAdd(int num) {
		int result = 0;
		for (int i = num; i > 0; i--) {
			result += i;
		}
		return result;
	}

	
	

	
	/**
	 * 小数点位数移动
	 * @param value
	 * @param bit    移动位数
	 * @param path   移动方位
	 * @return
	 */
	public static BigDecimal convert(String value,int bit,int path){
		if(null != value){
			try {
				BigDecimal big = new BigDecimal(value);
				switch (path) {
				case 1:
					return big.movePointLeft(bit);
				case 2:
					return big.movePointRight(bit);
				default:
					return big;
				}
			}catch(Exception e) {
				
			}
		}
		return new BigDecimal(0);
	}
	/**
	 * 将分转元
	 * @param yuan
	 * @return
	 */
	public static double fen2yuan(long yuan){
		BigDecimal big = new BigDecimal(yuan);
		return big.movePointLeft(2).doubleValue();
	}

	/**
	 * 将分转元
	 * @param yuan
	 * @return
	 */
	public static double fen2yuan(String yuan){
		BigDecimal big = new BigDecimal(yuan);
		return fen2yuan(big.longValue());
	}

	/**
	 * 将厘转元  抹掉厘部分
	 * @param li
	 * @return
	 */
	public static double li2yuan(long li){
		li = li/10*10;
		BigDecimal big = new BigDecimal(li);
		return big.movePointLeft(3).doubleValue();
	}

	/**
	 * 将厘转元
	 * @param yuan
	 * @return
	 */
	public static double li2yuan(String li){
		BigDecimal big = new BigDecimal(li);
		return li2yuan(big.longValue());
	}
	
	
	/**
	 * 将元转分
	 * @param fen
	 * @return
	 */
	public static long yuan2fen(double fen){
		BigDecimal big = new BigDecimal(fen);
		return big.movePointRight(2).longValue();
	}

	/**
	 * 将元转分
	 * @param fen
	 * @return
	 */
	public static long yuan2fen(String yuan){
		Double y = Double.valueOf(yuan);
		return yuan2fen(y.doubleValue());
	}

	/**
	 * 将元转厘
	 * @param yuan
	 * @return
	 */
	public static long yuan2li(double yuan){
		BigDecimal big = new BigDecimal(yuan);
		return big.movePointRight(3).longValue();
	}

	/**
	 * 将元转厘
	 * @param 元
	 * @return
	 */
	public static long yuan2li(String yuan){
		Double y = Double.valueOf(yuan);
		return yuan2li(y.doubleValue());
	}
	
	
	/**
	 * 压缩号码。即将常规号码转换为号码段
	 * 
	 * @param numbers
	 *            待压缩的号码
	 * @return
	 */
	public static String zipNumbers(String numbers) {
		String[] strs = StringUtils.split(numbers, ";");
		int length = strs.length;
		if (length < 2) {
			strs = StringUtils.split(numbers, " ");
		}
		List<String> list = new ArrayList<String>();
		int beginNum = -1;
		int preNum = -1;
		int numCount = strs[0].length();
		for (String str : strs) {
			int num = Integer.valueOf(str);
			if (beginNum == -1) {
				beginNum = num;
				preNum = num;
				continue;
			}

			if (num - preNum != 1) {
				String part = "";
				if (beginNum != preNum) {
					part = String.format("%0" + numCount + "d", beginNum);
					part += "~";
					part += String.format("%0" + numCount + "d", preNum);
				} else {
					part = String.format("%0" + numCount + "d", beginNum);
				}
				list.add(part);
				beginNum = num;
			}
			preNum = num;
		}
		if (beginNum != -1) {
			String part;
			if (beginNum != preNum) {
				part = String.format("%0" + numCount + "d", beginNum);
				part += "~";
				part += String.format("%0" + numCount + "d", preNum);
			} else {
				part = String.format("%0" + numCount + "d", beginNum);
			}
			list.add(part);
		}

		String result = StringUtils.join(list, ";");
		return result;
	}

	/**
	 * 解压号码。即将号码段还原为常规号码
	 * 
	 * @param numbers
	 *            压缩后的号码
	 * @return
	 */
	public static String unzipNumbers(String numbers) {
		List<String> list = new ArrayList<String>();
		String[] strs = StringUtils.split(numbers, ";");
		for (String str : strs) {
			if (StringUtils.indexOf(str, "~") > 0) {
				String[] items = StringUtils.split(str, "~");
				int numCount = items[0].length();
				int i = Integer.valueOf(items[0]);
				int j = Integer.valueOf(items[1]);
				for (; i <= j; i++) {
					String num = String.format("%0" + numCount + "d", i);
					list.add(num);
				}
			} else {
				list.add(str);
			}
		}
		return StringUtils.join(list, ";");
	}
	
	public static void main(String[] args) {
		double v1 = 4.123468;
		double v2 = 2.892;

		double v3 = add(v1, v2, 3);
		System.out.println("加法运算，保留3位小数：" + v1 + " ＋ " + v2 + " = " + v3);
		v3 = subtract(v1, v2, 3);
		System.out.println("减法运算，保留3位小数：" + v1 + " － " + v2 + " = " + v3);
		v3 = multiply(v1, v2, 3);
		System.out.println("乘法运算，保留3位小数：" + v1 + " × " + v2 + " = " + v3);
		v3 = divide(v1, v2, 3);
		System.out.println("除法运算，保留3位小数：" + v1 + " ÷ " + v2 + " = " + v3);

		int length = 10, count = 5;
		System.out.println(length + "选" + count + "共" + getTotalCombination(length, count) + "注");
		
		String source = "123 466 467 468 469 470 471 490 491 492 493 494 001 002 003 004";
		String target = zipNumbers(source);
		System.out.println(target);
		System.out.println(unzipNumbers(target));
	}
}
