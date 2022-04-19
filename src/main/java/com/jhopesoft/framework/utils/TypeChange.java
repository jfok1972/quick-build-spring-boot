package com.jhopesoft.framework.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * 各种数据类型转换的类
 * 
 * @author jfok
 * 
 */
public class TypeChange {

	public static BigDecimal objectToBigDecimal(Object object) {
		if (object == null) {
			return BigDecimal.ZERO;
		} else if (StringUtils.isBlank(object.toString())) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(object.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}

	/**
	 * 一个判断son是否继承自father的函数
	 */
	public static boolean superClassCheck(Class<?> son, Class<?> father) {
		if (son.getSuperclass() == Object.class || son.getSuperclass() == null) {
			return false;
		} else if (son.getSuperclass() == father) {
			return true;
		} else {
			return superClassCheck(son.getSuperclass(), father);
		}
	}

	/**
	 * 字符串类型转换成日期型 字符串格式 yyyy-MM-dd
	 * 
	 * @param date
	 * @return 日期型
	 */
	public static Date stringToDate(String date) {
		Date result = null;
		try {
			if ((date == null) || ("".equals(date)) || Constants.NULL.equals(date)) {
				return null;
			}
			date = date.replaceAll("/", "-");
			if (date.length() > Constants.INT_10) {
				// datetime
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
					result = dateFormat.parse(date);
				} catch (Exception e) {
				}
			} else {
				result = new SimpleDateFormat(Constants.DATE_FORMAT).parse(date);
			}
		} catch (Exception e) {
			// 如果出错了，再处理一下格式
			if (date.indexOf("-") == Constants.INT_2) {
				date = "20" + date;
			}
			if (date.length() == Constants.INT_4) {
				date += "-01-01";
			}
			if (date.length() == Constants.INT_7) {
				date += "-01";
			}
			try {
				result = new SimpleDateFormat(Constants.DATE_FORMAT).parse(date);
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 一个对象转换成日期型
	 * 
	 * @param date
	 * @return 日期
	 */
	public static Date stringToDate(Object date) {
		Date result = null;
		try {
			if ((date == null) || ("".equals(date.toString())) || (Constants.NULL.equals(date.toString()))) {
				return null;
			}
			if (date.toString().length() > Constants.INT_10) {
				// datetime
				result = new SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(date.toString());
			} else {
				result = new SimpleDateFormat(Constants.DATE_FORMAT).parse(date.toString());
			}
		} catch (ParseException e) {

		}
		return result;
	}

	/**
	 * 一个对象转换成日期型
	 * 
	 * @param date
	 * @return 日期
	 */
	public static Date stringToDateFormat(Object date, SimpleDateFormat Format) {
		Date tf_result = null;
		try {
			if ((date == null) || ("".equals(date))) {
				return null;
			}
			tf_result = Format.parse(date.toString());
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return tf_result;
	}

	/**
	 * 字符型转换为double 类型
	 * 
	 * @param str
	 * @return Double
	 */
	public static Double stringtoDouble(String str) {
		try {
			return Double.parseDouble(str.replaceAll(Constants.COMMA, ""));
		} catch (Exception e) {
			return 0.;
		}
	}

	/**
	 * 字符串转换为整型
	 * 
	 * @param str
	 * @return Integer
	 */
	public static Integer StringtoInteger(String str) {
		try {
			return Integer.parseInt(str.replaceAll(Constants.COMMA, ""));
		} catch (Exception e) {
			try {
				return stringtoDouble(str).intValue();
			} catch (Exception e1) {
				return 0;
			}
		}
	}

	/**
	 * 字符串转换为整型
	 * 
	 * @param str
	 * @return Integer
	 */
	public static Boolean stringtoBoolean(String str) {
		if (str == null) {
			return null;
		}
		str = str.toLowerCase();
		if (Constants.TRUE.equals(str) || Constants.YES.equals(str) || Constants.ONE.equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static Double dtod(Object d) {
		if (d == null) {
			return 0.0;
		} else if (d instanceof BigDecimal) {
			return ((BigDecimal) d).doubleValue();
		} else if (d instanceof Double) {
			return (Double) d;
		} else {
			return stringtoDouble(d.toString());
		}

	}

	public static Double dtod(BigDecimal d) {
		if (d == null) {
			return 0.0;
		} else {
			return d.doubleValue();
		}
	}

	public static Double dtod(Double d) {
		if (d == null) {
			return 0.0;
		} else {
			return d;
		}
	}

	public static Integer itoi(Integer d) {
		if (d == null) {
			return 0;
		} else {
			return d;
		}
	}

	public static Integer itoi(Object d) {
		if (d == null) {
			return 0;
		} else
			try {
				return Integer.parseInt(d.toString());
			} catch (Exception e) {
				return stringtoDouble(d.toString()).intValue();
			}
	}

	/**
	 * Double转换为字符串,二位小数，逗号分隔，0返回空
	 * 
	 * @param money
	 * @return
	 */
	public static String doubletoString(Double money) {
		if (money == null) {
			return null;
		}
		Format format = new DecimalFormat("#,##0.00");
		String result = format.format(money);
		if ("0.00".equals(result)) {
			return "";
		}
		return format.format(money);
	}

	/**
	 * Double转换为字符串,二位小数，逗号分隔，0返回空
	 * 
	 * @param money
	 * @return
	 */
	public static String doubletoString(Double money, String formatStr) {
		if (money == null) {
			return null;
		}
		Format format = new DecimalFormat(formatStr);
		return format.format(money);
	}

	/**
	 * Double转换为字符串 带货币符号
	 * 
	 * @param money
	 * @return
	 */
	public static String doubletoStringCurrency(Double money) {
		try {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			String result = nf.format(money);
			if (money == 0) {
				return "";
			} else {
				return result;
			}
		} catch (Exception e) {
			return "";
		}
	}

	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 日期转换为字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		String result = "";
		if (date != null) {
			result = new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
		}
		return result;
	}

	public static String dateTimeToString(Date date) {
		String result = "";
		if (date != null) {
			result = new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(date);
		}
		return result;
	}

	public static String zerotoSpace(Object value) {
		if (value == null) {
			return "";
		}
		String s = value.toString();
		if (s.equals("0") || s.equals("0.0") || s.equals("0.00")) {
			return "";
		} else {
			return s;
		}
	}

	public static double jsbl(double v1, double v2) {
		double vv1 = dtod(v1);
		double vv2 = dtod(v2);
		double r = 0.0;
		try {
			r = round(vv1 / vv2, 4);
		} catch (Exception e) {
			return 0.0;
		}
		return r;
	}

	public static int toInt(Object v) {
		if (v instanceof Integer) {
			return ((Integer) v).intValue();
		}
		if (v instanceof BigInteger) {
			return ((BigInteger) v).intValue();
		}
		if (v instanceof BigDecimal) {
			return ((BigDecimal) v).toBigInteger().intValue();
		}
		return 0;
	}

	private static final String[] PATTERN = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	private static final String[] CPATTERN = { "", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿" };
	private static final String[] CFPATTERN = { "", "角", "分" };
	private static final String ZEOR = "零";

	public static String moneyFormatToUpper(Object money) {
		if (money == null) {
			return "";
		}
		try {
			return moneyFormatToUpper(Double.parseDouble(money.toString()));
		} catch (Exception e) {
			return money.toString();
		}
	}

	/**
	 * 金额大写
	 * 
	 * @param money
	 * @return
	 */
	public static String moneyFormatToUpper(Double money) {

		String moneyString = doubletoString(money).replaceAll(Constants.COMMA, "");
		if (moneyString.length() == 0) {
			return "零元整";
		}
		// 判断是否为小数
		int dotPoint = moneyString.indexOf(".");
		String moneyStr;
		if (dotPoint != -1) {
			moneyStr = moneyString.substring(0, moneyString.indexOf("."));
		} else {
			moneyStr = moneyString;
		}
		// 小数部分的处理,以及最后的yuan.
		StringBuffer fraction = null;
		StringBuffer ms = new StringBuffer();
		for (int i = 0; i < moneyStr.length(); i++) {
			// 按数组的编号加入对应大写汉字
			ms.append(PATTERN[moneyStr.charAt(i) - 48]);
		}

		int cpCursor = 1;
		for (int j = moneyStr.length() - 1; j > 0; j--) {
			// 在j之后加字符,不影响j对原字符串的相对位置
			ms.insert(j, CPATTERN[cpCursor]);
			// 只是moneyStr.length()不断增加
			// insert(j,"string")就在j位置处插入,j=0时为第一位
			// 亿位之后重新循环
			cpCursor = cpCursor == 8 ? 1 : cpCursor + 1;
		}

		while (ms.indexOf("零拾") != -1) {
			// 当十位为零时用一个"零"代替"零拾"
			// replace的起始于终止位置
			ms.replace(ms.indexOf("零拾"), ms.indexOf("零拾") + 2, ZEOR);
		}
		while (ms.indexOf("零佰") != -1) {
			// 当百位为零时,同理
			ms.replace(ms.indexOf("零佰"), ms.indexOf("零佰") + 2, ZEOR);
		}
		while (ms.indexOf("零仟") != -1) {
			// 同理
			ms.replace(ms.indexOf("零仟"), ms.indexOf("零仟") + 2, ZEOR);
		}
		while (ms.indexOf("零万") != -1) {
			// 万需保留，中文习惯
			ms.replace(ms.indexOf("零万"), ms.indexOf("零万") + 2, "万");
		}
		while (ms.indexOf("零亿") != -1) {
			// 同上
			ms.replace(ms.indexOf("零亿"), ms.indexOf("零亿") + 2, "亿");
		}
		while (ms.indexOf("零零") != -1) {
			// 有连续数位出现零，即有以下情况，此时根据习惯保留一个零即可
			ms.replace(ms.indexOf("零零"), ms.indexOf("零零") + 2, ZEOR);
		}
		while (ms.indexOf("亿万") != -1) {
			// 特殊情况，如:100000000,根据习惯保留高位
			ms.replace(ms.indexOf("亿万"), ms.indexOf("亿万") + 2, "亿");
		}
		if (ms.length() > 1) {
			while (ms.lastIndexOf("零") == ms.length() - 1) {
				// 当结尾为零j，不必显示,经过处理也只可能出现一个零
				ms.delete(ms.lastIndexOf("零"), ms.lastIndexOf("零") + 1);
			}
		}
		int end;
		if ((dotPoint = moneyString.indexOf(".")) != -1) {
			// 是小数的进入
			String fs = moneyString.substring(dotPoint + 1, moneyString.length());
			if (fs.indexOf(Constants.ZEROZERO) == -1 || fs.indexOf(Constants.ZEROZERO) >= Constants.INT_2) {
				// 若前两位小数全为零，则跳过操作
				// 仅保留两位小数
				end = fs.length() > 2 ? 2 : fs.length();
				fraction = new StringBuffer(fs.substring(0, end));
				for (int j = 0; j < fraction.length(); j++) {
					// 替换大写汉字
					fraction.replace(j, j + 1, PATTERN[fraction.charAt(j) - 48]);
				}
				for (int i = fraction.length(); i > 0; i--) {
					// 插入中文标识
					fraction.insert(i, CFPATTERN[i]);
				}
				// 为整数部分添加标识
				fraction.insert(0, "元");
			} else {
				fraction = new StringBuffer("元整");
			}

		} else {
			fraction = new StringBuffer("元整");
		}
		// 加入小数部分
		ms.append(fraction);
		return ms.toString();
	}

}
