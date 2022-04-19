package com.jhopesoft.framework.core.objectquery.sqlfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.MD5;

/**
 * 用于处理查询分组条件的类
 * 
 * @author jiangfeng 2017.03.03
 * 
 */
public class GroupConditionUtils {

	public static List<UserDefineFilter> changeGroupConditionTo(String groupcondition) {
		List<UserDefineFilter> result = new ArrayList<UserDefineFilter>();
		String[] conditions = groupcondition.split("\\|\\|\\|");
		for (String s : conditions) {
			result.add(changeaGroupConditionTo(s));
		}
		return result;
	}

	public static List<UserDefineFilter> changeGroupConditionTo(List<String> groupcondition) {
		List<UserDefineFilter> result = new ArrayList<UserDefineFilter>();
		for (String s : groupcondition) {
			result.add(changeaGroupConditionTo(s));
		}
		return result;
	}

	public static UserDefineFilter changeaGroupConditionTo(String s) {
		int pos = s.indexOf('=');
		String fieldahead = null;
		String field = s.substring(0, pos);
		String value = s.substring(pos + 1);
		String[] part = field.split("\\|");
		if (part.length > 1) {
			fieldahead = part[0];
			field = part[1];
		}
		String leveltype = null;
		String[] spart = field.split("-");
		FFunction function = null;
		if (spart.length == Constants.INT_2) {
			field = spart[0];
			if (spart[1].length() < Constants.INT_3)
				leveltype = spart[1];
			else {
				function = (Local.getDao().findById(FFunction.class, spart[1]));
			}
		}
		FDataobjectfield objectfield = Local.getDao().findById(FDataobjectfield.class, field);
		if (objectfield._isManyToOne() || objectfield._isOneToOne()) {
			fieldahead = (fieldahead == null ? "" : fieldahead + ".") + objectfield.getFieldname();
			FDataobject dataobject = Local.getDao().findById(FDataobject.class, objectfield.getFieldtype());
			objectfield = dataobject._getNameField();
		}
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty((fieldahead == null ? "" : fieldahead + ".") + objectfield.getFieldname());
		if (leveltype != null && leveltype.length() == 1) {
			filter.setCondition(
					objectfield.getFDataobject()._getLevelExpression(Integer.parseInt(leveltype), Constants.THIS));
		}
		if (function != null) {
			filter.setCondition(function.getSqlExpression(objectfield.getFDataobject()));
		}
		filter.setOperator("eq");
		if (value.indexOf(Constants.COMMA) != -Constants.INT_1) {
			filter.setOperator(Constants.IN);
		}
		if (value.startsWith(Constants.GANTANHAO)) {
			filter.setOperator("not in");
			value = value.substring(1);
		}
		filter.setValue(value);
		return filter;
	}

	public static String changeaGroupField(String field) {
		String fieldahead = null;
		String[] part = field.split("\\|");
		if (part.length > 1) {
			fieldahead = part[0];
			field = part[1];
		}
		FDataobjectfield objectfield = Local.getDao().findById(FDataobjectfield.class, field);
		if (objectfield._isManyToOne() || objectfield._isOneToOne()) {
			fieldahead = (fieldahead == null ? "" : fieldahead + ".") + objectfield.getFieldname();
			FDataobject dataobject = Local.getDao().findById(FDataobject.class, objectfield.getFieldtype());
			objectfield = dataobject._getNameField();
		}
		return (fieldahead == null ? "" : fieldahead + ".") + objectfield.getFieldname();
	}

	public static String getCodeLevelCondition(String field, int level) {
		String[] part = field.split("\\|");
		if (part.length > 1) {
			field = part[1];
		}
		FDataobjectfield objectfield = Local.getDao().findById(FDataobjectfield.class, field);
		return objectfield.getFDataobject()._getLevelExpression(level, Constants.THIS);
	}

	public static String changeGroupConditionToJson(String groupcondition) {
		StringBuilder result = new StringBuilder("(");
		String[] conditions = groupcondition.split("\\|\\|\\|");
		for (int i = 0; i < conditions.length; i++) {
			String s = conditions[i];
			result.append("(" + changeaGroupConditionToJson(s) + ")");
			if (i != conditions.length - 1) {
				result.append(" and ");
			}
		}
		result.append(")");
		return result.toString();
	}

	public static String changeaGroupConditionToJson(String s) {
		JSONObject object = new JSONObject();
		String fieldahead = null;
		int pos = s.indexOf('=');
		String field = null;
		String value = null;
		if (pos != -1) {
			field = s.substring(0, pos);
			value = s.substring(pos + 1);
		} else {
			field = s;
		}
		String[] part = field.split("\\|");
		if (part.length > 1) {
			fieldahead = part[0];
			field = part[1];
		}
		String leveltype = null;
		String[] spart = field.split("-");
		FFunction function = null;
		if (spart.length == Constants.INT_2) {
			field = spart[0];
			if (spart[1].length() < Constants.INT_3)
				leveltype = spart[1];
			else {
				function = Local.getDao().findById(FFunction.class, spart[1]);
			}
		}

		FDataobjectfield objectfield = Local.getDao().findById(FDataobjectfield.class, field);
		if (objectfield._isManyToOne() || objectfield._isOneToOne()) {
			fieldahead = (fieldahead == null ? "" : fieldahead + ".") + objectfield.getFieldname();
			FDataobject dataobject = Local.getDao().findById(FDataobject.class, objectfield.getFieldtype());
			objectfield = dataobject._getNameField();
			object.put("fieldname", objectfield.getFieldname());
			object.put("objectname", dataobject.getObjectname());
			object.put("fieldahead", fieldahead);
		} else {
			object.put("fieldname", objectfield.getFieldname());
			object.put("objectname", objectfield.getFDataobject().getObjectname());
			object.put("fieldahead", fieldahead);
			if (leveltype != null && leveltype.length() > 0) {
				object.put(Constants.CONDITION,
						objectfield.getFDataobject()._getLevelExpression(Integer.parseInt(leveltype), Constants.THIS));
			}
			if (function != null) {
				object.put(Constants.CONDITION, function.getSqlExpression(objectfield.getFDataobject()));
			}
		}
		if (pos != -1) {
			String fn = object.toJSONString();
			return fn + genEqOrInStr(value, leveltype, fn);
		} else {
			return object.toJSONString();
		}
	}

	public static FDataobjectfield getGroupField(String s, String moduleName) {
		int pos = s.indexOf('=');
		String field = null;
		if (pos != -1) {
			field = s.substring(0, pos);
		} else {
			field = s;
		}
		String[] part = field.split("\\|");
		if (part.length > 1) {
			field = part[1];
		}
		FDataobjectfield ffield = Local.getDao().findById(FDataobjectfield.class, field);
		if (ffield == null && moduleName != null) {
			FDataobject object = DataObjectUtils.getDataObject(moduleName);
			for (FDataobjectfield afield : object.getFDataobjectfields()) {
				if (field.equals(afield.getFieldname())) {
					ffield = afield;
				}
			}
		}
		return ffield;
	}

	private static String genEqOrInStr(String value, String leveltype, String fn) {
		Map<String, Object> param = DataObjectUtils.getSqlParameter();
		value = value.replaceAll("'", "");
		if (value.equals(Constants.NULL)) {
			return " is null ";
		}
		boolean hasnull = false;
		if (param == null) {
			if (value.indexOf(Constants.COMMA) != -1) {
				StringBuilder sb = new StringBuilder(" in (");
				String[] values = value.split(Constants.COMMA);
				for (int i = 0; i < values.length; i++) {
					if (Constants.NULL.equalsIgnoreCase(values[i])) {
						hasnull = true;
					} else {
						sb.append("'" + values[i] + "'" + Constants.COMMA);
					}
				}
				sb.delete(sb.length() - 1, sb.length());
				sb.append(")");
				return sb.toString() + (hasnull ? " or " + fn + " is null " : "");
			} else {
				return " = '" + value + "'";
			}
		} else {
			String key = "jxy_" + MD5.MD5Encode(value);
			if (value.indexOf(Constants.COMMA) != -1) {
				List<String> pvalues = new ArrayList<String>();
				for (String s : value.split(Constants.COMMA)) {
					if (Constants.NULL.equalsIgnoreCase(s)) {
						hasnull = true;
					} else {
						pvalues.add(s);
					}
				}
				param.put(key, pvalues);
				return " in :" + key + " " + (hasnull ? " or " + fn + " is null " : "");
			} else {
				param.put(key, value);
				return " = :" + key + " ";
			}
		}
	}

}
