package com.jhopesoft.framework.core.objectquery.filter;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.FieldAggregationType;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.sqlfield.GroupConditionUtils;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dictionary.FDictionary;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectFieldUtils;
import com.jhopesoft.framework.utils.OperateUtils;

/**
 * 
 * @author jiangfeng
 *
 */
public class UserDefineFilter {

	public static final String NOTIN = "not in";
	public static final String NOTLIKE = "not like";
	public static final String EQUALSING = "=";
	public static final String TWOEQUALSING = "==";
	public static final String EQ = "eq";
	public static final String GT = "gt";
	public static final String GE = "ge";
	public static final String LT = "lt";
	public static final String LE = "le";
	public static final String NE = "ne";
	public static final String BETWEEN = "between";
	public static final String NOTBETWEEN = "not between";
	public static final String ISNOT = "is not";
	public static final String ISNULL = "is null";
	public static final String ISNOTNULL = "is not null";

	public static final String YYYY = "yyyy";
	public static final String YYYY_MM = "yyyy-mm";
	public static final String YYYY_MM_DD = "yyyy-mm-dd";
	public static final String STARTWITH = "startwith";
	public static final String NOTSTARTWITH = "not startwith";
	public static final String REGEXP = "regexp";
	public static final String SUBTRACT = "-";
	public static final String HUAKUOHAOZUO = "{";
	public static final String FANKUOHAOZUO = "[";
	public static final String FANKUOHAOYOU = "]";
	private String property;
	private String operator;
	private String value;

	private List<UserDefineFilter> children;

	private String filterTitle;

	private String searchfor;

	private FDataobjectfield field;

	private String childFieldName;
	private FieldAggregationType aggregationType;

	private String tableAsName;
	private String condition;

	public UserDefineFilter() {

	}

	public UserDefineFilter(String property, String operator, String value) {
		super();
		this.property = property;
		this.operator = operator;
		this.value = value;
	}

	public boolean isDictionaryName() {
		if (property == null) {
			return false;
		}
		return property.endsWith(FDictionary.NAMEENDS);
	};

	@Override
	public String toString() {
		if (property != null && value != null) {
			if (property.equals(value)) {
				return property;
			}
		}
		return property + " " + operator + " " + value;
	}

	public String getSqlWhere(String asName, BaseModule baseModule) {
		if (field == null) {
			if (baseModule.getAllFieldsNameAndSql().containsKey(property)) {
				return this.getSqlWhere2(baseModule.getAllFieldsNameAndSql().get(property));
			} else {
				System.out.println("property:" + property + "的用户自定义定段未找到!");
			}
		}
		return this.getSqlWhere2(field._getSelectName(asName));

	}

	public String getChildSqlWhere() {
		if (aggregationType == FieldAggregationType.COUNT) {
			return getSqlWhere1(childFieldName);
		} else {
			return getSqlWhere2(childFieldName);
		}
	}

	public String getSqlWhere2(String name) {

		if (searchfor != null && Constants.DATE.equals(searchfor)) {
			return new DateSectionFilter(name, operator, value).getWhereSql();
		}

		if (field == null) {
			if (Constants.IN.equals(operator)) {
				return name + OperateUtils.valueChangeToInString(value, name);
			} else {
				return name + " like '%" + value + "%'";
			}
		}
		if (Constants.BOOLEAN.equals(field.getFieldtype().toLowerCase())) {
			if (Constants.TRUE.equals(value)) {
				return name;
			} else if (Constants.NULL.equals(value)) {
				return "(" + name + " is null )";
			} else {
				return "(" + "!(" + name + ")" + " or " + name + " is null )";
			}
		}
		if (Constants.STRING.equals(field.getFieldtype().toLowerCase()) && Constants.LIKE.equals(operator)) {
			String[] parts = value.split("[,|，]");
			if (parts.length <= 1)
				return name + " like '%" + value + "%'";
			else {
				StringBuilder str = new StringBuilder(" ( ");
				for (int i = 0; i < parts.length; i++) {
					str.append(name + " like '%" + parts[i] + "%'");
					str.append(i == parts.length - 1 ? "" : " or ");
				}
				str.append(" ) ");
				return str.toString();
			}
		}
		if (Constants.IS.startsWith(operator)) {
			return name + " " + operator;
		}

		if (Constants.IN.equals(operator)) {
			return name + OperateUtils.valueChangeToInString(value, name);
		}
		return getSqlWhere1(name);
	}

	public static String valueChangeToBetweenString(String value) {
		String[] v = value.split(Constants.COMMA);
		if (v.length < Constants.INT_2) {
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

	public String getSqlWhere1(String name) {
		if (Constants.IN.equals(operator)) {
			return name + OperateUtils.valueChangeToInString(value, name);
		} else if (operator.equals(NOTIN)) {
			return "not (" + name + OperateUtils.valueChangeToInString(value, name) + ")";
		} else if (operator.equals(GT)) {
			return name + " > '" + value + "'";
		} else if (operator.equals(GE)) {
			return name + " >= '" + value + "'";
		} else if (operator.equals(LT)) {
			return name + " < '" + value + "'";
		} else if (operator.equals(LE)) {
			return name + " <= '" + value + "'";
		} else if (operator.equals(NE)) {
			return name + " <> '" + value + "'";
		} else if (operator.equals(BETWEEN)) {
			return name + valueChangeToBetweenString(value);
		} else if (operator.equals(NOTBETWEEN)) {
			return name + " not " + valueChangeToBetweenString(value);
		} else if (operator.equals(YYYY)) {
			return " year(" + name + ") = " + value;
		} else if (operator.equals(YYYY_MM)) {
			return getYearMonthFilter(name, value);
		} else if (operator.equals(STARTWITH)) {
			return OperateUtils.valueChangeToStartWithString(value, name);
		} else if (operator.equals(NOTSTARTWITH)) {
			return " not " + OperateUtils.valueChangeToStartWithString(value, name);
		} else if (operator.equals(REGEXP)) {
			return name + " regexp '" + value + "'";
		} else {
			return name + " = '" + value + "'";
		}

	}

	/**
	 * 根据字符串返回grid用户自定义条件，生成一个数组
	 * 
	 * @param str
	 * @return
	 */
	public static List<UserDefineFilter> changeToUserDefineFilter(String str) {
		if (str != null && str.length() > 1) {
			return JSON.parseArray(str, UserDefineFilter.class);
		} else {
			return null;
		}
	}

	public String getYearMonthFilter(String name, String value) {
		String[] values = value.split(SUBTRACT);
		return String.format("(year(%s) = %s and month(%s) = %s)", name, values[0], name, values[1]);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setProperty_(String propertyStr) {
		if (propertyStr.startsWith(HUAKUOHAOZUO)) {
			JSONObject jsonObject = JSONObject.parseObject(propertyStr);
			String moduleName = jsonObject.getString(Constants.MODULE_NAME);
			if (moduleName == null) {
				throw new RuntimeException("条件字段property中没有定义moduleName的值。" + propertyStr);
			}
			propertyStr = DataObjectFieldUtils.parseFieldStrFromObject(moduleName, jsonObject);
		}
		String leveltype = null;
		String[] spart = propertyStr.split("-");
		if (spart.length == Constants.INT_2) {
			propertyStr = spart[0];
			if (spart[1].length() < Constants.INT_3) {
				leveltype = spart[1];
			} else {
				this.condition = Local.getDao().findById(FFunction.class, spart[1])
						.getSqlExpression(GroupConditionUtils.getGroupField(spart[0], null).getFDataobject());
			}
		}
		this.setProperty(GroupConditionUtils.changeaGroupField(propertyStr));
		if (leveltype != null) {
			this.setCondition(GroupConditionUtils.getCodeLevelCondition(propertyStr, Integer.parseInt(leveltype)));
		}
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FDataobjectfield getField() {
		return field;
	}

	public void setField(FDataobjectfield field) {
		this.field = field;
	}

	public String getChildFieldName() {
		return childFieldName;
	}

	public void setChildFieldName(String childFieldName) {
		this.childFieldName = childFieldName;
	}

	public FieldAggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(FieldAggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	public String getFilterTitle() {
		return filterTitle;
	}

	public void setFilterTitle(String filterTitle) {
		this.filterTitle = filterTitle;
	}

	public String getTableAsName() {
		return tableAsName;
	}

	public void setTableAsName(String tableAsName) {
		this.tableAsName = tableAsName;
	}

	public String getSearchfor() {
		return searchfor;
	}

	public void setSearchfor(String searchfor) {
		this.searchfor = searchfor;
	}

	public List<UserDefineFilter> getChildren() {
		return children;
	}

	public void setChildren(List<UserDefineFilter> children) {
		this.children = children;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
