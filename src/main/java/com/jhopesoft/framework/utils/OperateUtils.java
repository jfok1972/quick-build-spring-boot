package com.jhopesoft.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.jhopesoft.framework.bean.HierarchyIDPID;
import com.jhopesoft.framework.core.objectquery.filter.DateSectionFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class OperateUtils {

	/**
	 * 根据dataobject 的树级属性来生成一个条件树。包括二种，一种是codelevel, 一种是id-pid型的
	 * 
	 * @param dataobject
	 * @param name
	 * @param value
	 * @return
	 */
	public static String getIdPidOrCodeLevelCondition(FDataobject dataobject, String fieldname, String value) {
		if (dataobject._isIdPidLevel()) {
			// id-pid的模块
			List<HierarchyIDPID> idpids = HierarchyIDPIDUtils.getHierarchyIDPID_(dataobject);
			Set<String> allid = new HashSet<String>();
			for (String v : value.split(Constants.COMMA)) {
				for (HierarchyIDPID hidpid : idpids) {
					if (hidpid.getId().equals(v)) {
						for (String s : hidpid.getAllChildrenId(false)) {
							allid.add(s);
						}
						break;
					}
				}
			}
			return OperateUtils.getCondition(fieldname, Constants.IN,
					StringUtils.join(allid.toArray(new String[allid.size()]), ','));
		} else {
			// codelevel模块
			// 把所有长度相同的都放在一起
			Map<Integer, List<String>> map = new HashMap<Integer, List<String>>(0);
			for (String v : value.split(Constants.COMMA)) {
				Integer i = v.length();
				if (!map.containsKey(i)) {
					map.put(i, new ArrayList<String>());
				}
				map.get(i).add(v);
			}
			List<String> list = new ArrayList<String>();
			for (Integer i : map.keySet()) {
				String aStr = OperateUtils.getCondition(
						Local.getBusinessDao().getSf().substring(fieldname, "1", i.toString()), Constants.IN,
						StringUtils.join(map.get(i).toArray(new String[map.get(i).size()]), ','));
				list.add(aStr);
			}
			return "(" + StringUtils.join(list.toArray(new String[list.size()]), " or ") + ")";
		}
	}

	/**
	 * 根据属性，操作符，值来返回条件表达式
	 * 
	 * @param fieldname
	 * @param operator
	 * @param value
	 * @return
	 */
	public static String getCondition(String name, String operator, String value) {

		if (operator == null) {
			if (value == null) {
				return name;
			} else {
				return name + " " + value;
			}
		}
		if (value != null) {
			value = value.replaceAll("'", "");
			if (Constants.TRUE.equalsIgnoreCase(value)) {
				value = "1";
			}
			if (Constants.FALSE.equalsIgnoreCase(value)) {
				value = "0";
			}
			if (Constants.NULL.equalsIgnoreCase(value)) {
				operator = "is null";
			}
		}
		operator = operator.toLowerCase();
		if (operator.equals(UserDefineFilter.EQ) || operator.equals(UserDefineFilter.TWOEQUALSING)
				|| operator.equals(UserDefineFilter.EQUALSING)) {
			return name + " = " + translateValue(value);
		} else if (UserDefineFilter.GT.equals(operator)) {
			return name + " > " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.GE)) {
			return name + " >= " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.LT)) {
			return name + " < " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.LE)) {
			return name + " <= " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.NE)) {
			return name + " <> " + translateValue(value);
		} else if (Constants.IS.equals(operator)) {
			return name + " is " + value + "";
		} else if (operator.equals(UserDefineFilter.ISNOT)) {
			return name + " is not " + value + "";
		} else if (operator.equals(UserDefineFilter.ISNULL)) {
			return name + " is null ";
		} else if (operator.equals(UserDefineFilter.ISNOTNULL)) {
			return name + " is not null";
		} else if (Constants.IN.equals(operator)) {
			return name + valueChangeToInString(value, name);
		} else if (operator.equals(UserDefineFilter.NOTIN)) {
			// ************ 如果有 null 就会出错了
			return "not (" + name + valueChangeToInString(value, name) + ")";
		} else if (operator.equals(Constants.LIKE)) {
			return generateLikeString(name, value);
		} else if (operator.equals(UserDefineFilter.NOTLIKE)) {
			return name + " not like " + translateValue("%" + value + "%");
		} else if (operator.equals(UserDefineFilter.BETWEEN)) {
			return name + valueChangeToBetweenString(value);
		} else if (operator.equals(UserDefineFilter.NOTBETWEEN)) {
			return name + " not " + valueChangeToBetweenString(value);
		} else if (operator.equals(UserDefineFilter.STARTWITH)) {
			return valueChangeToStartWithString(value, name);
		} else if (operator.equals(UserDefineFilter.NOTSTARTWITH)) {
			return " not " + valueChangeToStartWithString(value, name);
		} else if (operator.equals(UserDefineFilter.REGEXP)) {
			return name + " regexp " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.YYYY)) {
			return " year(" + name + ") = " + translateValue(value);
		} else if (operator.equals(UserDefineFilter.YYYY_MM)) {
			return getYearMonthFilter(name, value);
		} else if (operator.equals(UserDefineFilter.YYYY_MM_DD)) {
			return getYearMonthDayFilter(name, value);
		} else if (DateSectionFilter.isDataSectionFilter(operator)) {
			return new DateSectionFilter(name, operator, value).getWhereSql();
		} else {
			return name + " " + operator + " '" + value + "'";
		}

	}

	public static String translateValue(String value) {
		if (value == null) {
			return "'null'";
		} else {
			Map<String, Object> params = DataObjectUtils.getSqlParameter();
			if (params != null) {
				String key = "jxy_" + MD5.MD5Encode(value);
				params.put(key, value);
				return ":" + key + " ";
			} else {
				return " '" + value + "' ";
			}
		}

	}

	/**
	 * startwith 转换成sql , 可以是单个值，也可以是数组值
	 * 
	 * @param value
	 * @param fn
	 * @return
	 */
	public static String valueChangeToStartWithString(String value, String fn) {
		// value 有可能是如下形式：["男","女"],要判断一个字符串是否是一个数组形式
		if (value != null && value.length() > Constants.INT_2 && value.startsWith(UserDefineFilter.FANKUOHAOZUO)
				&& value.endsWith(UserDefineFilter.FANKUOHAOYOU)) {
			value = value.substring(1, value.length() - 2);
			value = value.replaceAll("\"", "").replaceAll("'", "");
			;
		}
		String[] values = value.split(Constants.COMMA);
		StringBuilder sb = new StringBuilder("");
		for (String s : values) {
			sb.append(fn + " like " + translateValue(s + "%") + " or ");
		}
		sb.delete(sb.length() - 4, sb.length());
		return "(" + sb.toString() + ")";
	}

	/**
	 * 如果like中的字符中有逗号(半角或全角都可以)，表示可以包含多个文本
	 * name 1,2
	 * 
	 * @param name
	 * @param value
	 * @return ( name like "%1% or name like "%2%")
	 */
	public static String generateLikeString(String name, String value) {
		String[] parts = value.split("[,|，]");
		if (parts.length <= 1)
			return name + " like " + translateValue("%" + value + "%");
		else {
			StringBuilder str = new StringBuilder(" ( ");
			for (int i = 0; i < parts.length; i++) {
				str.append(name + " like " + translateValue("%" + parts[i] + "%"));
				str.append(i == parts.length - 1 ? "" : " or ");
			}
			str.append(" ) ");
			return str.toString();
		}
	}

	/**
	 * in 中要么只有null，或者undefined,要么就不能有null,有null的话null的值将不会加入。现在有null也可以了
	 * 
	 * @param value
	 * @return
	 */
	public static String valueChangeToInString(String value, String fn) {
		// value 有可能是如下形式：["男","女"],要判断一个字符串是否是一个数组形式
		if (value != null && value.length() > Constants.INT_2 && value.startsWith(UserDefineFilter.FANKUOHAOZUO)
				&& value.endsWith(UserDefineFilter.FANKUOHAOYOU)) {
			value = value.substring(1, value.length() - 2);
			value = value.replaceAll("\"", "").replaceAll("'", "");
			;
		}
		Map<String, Object> params = DataObjectUtils.getSqlParameter();
		if (params != null) {
			if (value != null && !Constants.NULL.equalsIgnoreCase(value)) {
				boolean hasnull = false;
				String[] values = value.split(Constants.COMMA);
				String key = "jxy_in" + MD5.MD5Encode(value);
				List<String> pvalues = new ArrayList<String>();
				for (String s : values) {
					if (Constants.NULL.equalsIgnoreCase(s)) {
						hasnull = true;
					} else {
						pvalues.add(s);
					}
				}
				params.put(key, pvalues);
				return " in :" + key + " " + (hasnull ? " or " + fn + " is null " : "");
			} else {
				return " is null ";
			}
		} else {
			if (value != null && !Constants.NULL.equalsIgnoreCase(value)) {
				boolean hasnull = false;
				String[] values = value.split(Constants.COMMA);
				StringBuilder sb = new StringBuilder("");
				for (int i = 0; i < values.length; i++) {
					if (Constants.NULL.equalsIgnoreCase(values[i])) {
						hasnull = true;
					} else {
						sb.append("'" + values[i] + "'" + Constants.COMMA);
					}
				}
				sb.delete(sb.length() - 1, sb.length());
				return " in (" + sb.toString() + ") " + (hasnull ? " or " + fn + " is null " : "");
			} else {
				return " is null ";
			}
		}
	}

	public static String valueChangeToBetweenString(String value) {
		String[] v = value.split(Constants.COMMA);
		if (v.length < Constants.INT_2) {
			// 可以用-号来分隔二个数，但是得是0123456789后面的减号
			int pos = -1;
			for (int i = 0; i <= Constants.INT_9; i++) {
				int p = value.indexOf(i + "-");
				if (p >= 0) {
					pos = p;
				}
			}
			if (pos > -1) {
				v = new String[2];
				v[0] = value.substring(0, pos + 1);
				v[1] = value.substring(pos + 2);
			}
		}
		if (v.length < Constants.INT_2) {
			return " between '" + v[0] + "' and '" + v[0] + "'";
		} else {
			return " between '" + v[0] + "' and '" + v[1] + "'";
		}
	}

	public static String getYearMonthFilter(String name, String value) {
		String[] values = value.split("-");
		return String.format("(year(%s) = %s and month(%s) = %s)", name, values[0], name, values[1]);
	}

	public static String getYearMonthDayFilter(String name, String value) {
		String[] values = value.split("-");
		return String.format("(year(%s) = %s and month(%s) = %s and day(%s) = %s)", name, values[0], name, values[1],
				name, values[2]);
	}
}
