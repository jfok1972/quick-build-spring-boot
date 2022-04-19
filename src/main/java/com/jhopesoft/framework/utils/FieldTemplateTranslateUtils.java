package com.jhopesoft.framework.utils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;

/**
 * 字段模板转换的一些函数
 * 
 * @author jiangfeng
 *
 */
public class FieldTemplateTranslateUtils {

	public static final String NOSUCHPROPERTY = "_NOSUCHPROPERTY_";

	public static Object getValue(String express, Object record, Map<String, Object> recordMap,
			FDataobject dataobject) {
		// 表达式里面也可能有{},用「」代替
		express = express.replace("{", "").replace("}", "").replace("「", "{").replace("」", "}");
		if (dataobject != null) {
			FDataobjectfield field = dataobject._getModuleFieldByFieldTitle(express);
			if (field != null) {
				express = field.getFieldname();
			}
		}
		if (recordMap != null
				&& (express.equals(Constants.CREATER) || express.equalsIgnoreCase(Constants.LASTMODIFIER))) {
			return recordMap.get(express);
		}
		// 如果是数据字典的话，取得描述
		if (recordMap != null && recordMap.containsKey(express + "_dictname")) {
			return recordMap.get(express + "_dictname");
		}
		Object result = null;
		boolean found = false;
		if (record != null) {
			found = true;
			try {
				result = Ognl.getValue(express, record);
			} catch (OgnlException e) {
				System.out.println(express + "在bean中未找到，将在map中寻找");
				found = false;
			} catch (IndexOutOfBoundsException e) {
				System.out.println(express + ":下标越界");
				result = null;
				found = true;
			} catch (IllegalArgumentException e) {
				System.out.println(express + "在bean中未找到，将在map中寻找,可能是有静态函数");
				found = false;
			}
		}
		if (!found && recordMap != null) {
			if (recordMap.containsKey(express))
				result = recordMap.get(express);
			else {
				try {
					result = Ognl.getValue(express, recordMap);
				} catch (Exception e) {
					result = NOSUCHPROPERTY;
				}
			}
		}
		return result;
	}

	// 给excel中的单元格提供数据，格式都写在单元格里了
	public static String getStringValue(String express, Object record, Map<String, Object> recordMap,
			FDataobject dataobject) {
		express = express.replace("{", "").replace("}", "");
		String[] parts = express.split("::");
		Object result = getValue(parts[0], record, recordMap, dataobject);
		if (result == null) {
			return "";
		}
		// System.out.println(result.getClass().getName());
		if (parts.length == 2) {
			if (result instanceof Date || result instanceof java.sql.Date || result instanceof Timestamp) {
				Date _result = (Date) result;
				result = DateUtils.format(_result, parts[1]);
			} else if (result instanceof Number) {
				if ("大写".equals(parts[1])) {
					result = TypeChange.moneyFormatToUpper(((Number) result).doubleValue());
				} else {
					DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
					df.applyPattern(parts[1]);
					result = df.format(result);
				}
			}
		} else if (result instanceof Date || result instanceof java.sql.Date || result instanceof Timestamp) {
			Date _result = (Date) result;
			result = DateUtils.format(_result, Constants.DATE_FORMAT);
		}
		return result.toString();
	}

	// 给html打印提供数据
	public static String getPrintStringValue(String express, Object record, Map<String, Object> recordMap,
			FDataobject dataobject) {
		express = express.replace("{", "").replace("}", "");
		if (express.length() == 0) {
			return "";
		}
		String[] parts = express.split("::");
		Object result = getValue(parts[0], record, recordMap, dataobject);
		if (result == null) {
			return "";
		}
		// System.out.println(result.getClass().getName());
		if (parts.length == 2) {
			if (result instanceof Date || result instanceof java.sql.Date || result instanceof Timestamp) {
				Date _result = (Date) result;
				result = DateUtils.format(_result, parts[1]);
			} else if (result instanceof Number) {
				if ("大写".equals(parts[1])) {
					result = TypeChange.moneyFormatToUpper(((Number) result).doubleValue());
				} else {
					DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
					df.applyPattern(parts[1]);
					result = df.format(result);
				}
			}
		} else if (result instanceof Date || result instanceof java.sql.Date || result instanceof Timestamp) {
			Date _result = (Date) result;
			result = DateUtils.format(_result, Constants.DATE_FORMAT);
		} else if (result instanceof Number) {
			if (result instanceof Integer || result instanceof BigInteger) {

			} else {
				DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
				df.applyPattern("#,##0.00");
				result = df.format(result);
				if ("0.00".equals(result)) {
					result = "";
				}
			}
		}
		return result.toString();
	}
}
