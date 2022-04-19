package com.jhopesoft.framework.core.datamining.utils;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ModuleHierarchyGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.AggregateField;
import com.jhopesoft.framework.core.objectquery.sqlfield.SqlFieldUtils;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.HierarchyIDPIDUtils;

/**
 * 
 * 用于生成数据挖掘的sql语句，mysql中可以将子select语句放在from中，这样可以不用建立临时表了
 * 
 * @author jiangfeng
 *
 */

public class DataminingSqlGenerate {

	public static final String DATAMININGGROUP_ = "datamininggroup_";

	private static final String CR = "\r\n";
	private FDataobject dataobject;
	private BaseModule baseModule;
	private List<AggregateField> aggretageFields;
	private FDataobjectview dataobjectview;
	private JSONObject sqlparam;
	private List<UserDefineFilter> userDefineFilters;
	private List<UserDefineFilter> parentConditions;
	private String groupfieldid;
	private String groupDefine;
	private FDataobjectfield groupfield;
	private FFunction fFunction;
	private String leveltype;

	public String generateSelect() {
		baseModule = ModuleHierarchyGenerate.genModuleHierarchy(dataobject, "main_", true);
		StringBuilder sqlsb = new StringBuilder();

		groupDefine = groupfieldid == null ? "'总计'" : DATAMININGGROUP_;

		sqlsb.append("select " + groupDefine + " " + DATAMININGGROUP_ + "_" + " " + CR);

		for (AggregateField field : aggretageFields) {

			FDataobjectfield f = dataobject._getModuleFieldByFieldName(field.getFieldname());
			if (f != null && f._isPercentField() && f._hasDivisior_Denominator()) {
				FDataobjectfield fz = dataobject._getModuleFieldByFieldName(f.getDivisor());
				FDataobjectfield fm = dataobject._getModuleFieldByFieldName(f.getDenominator());

				if (field.getConditionid() == null && (field.getCondition() == null)) {

					sqlsb.append(" , sum(" + baseModule.getAsName() + "." + fz.getFieldname() + ") " + field.getAsName()
							+ "1");
					sqlsb.append(" , sum(" + baseModule.getAsName() + "." + fm.getFieldname() + ") " + field.getAsName()
							+ "2");
				} else {
					sqlsb.append(
							" , sum(" + baseModule.getAsName() + "."
									+ SqlFieldUtils.genAsName(fz.getFieldname()
											+ (field.getConditionid() == null ? "" : field.getConditionid())
											+ (field.getCondition() == null ? "" : field.getCondition()))
									+ ") " + field.getAsName() + "1");
					sqlsb.append(
							" , sum(" + baseModule.getAsName() + "."
									+ SqlFieldUtils.genAsName(fm.getFieldname()
											+ (field.getConditionid() == null ? "" : field.getConditionid())
											+ (field.getCondition() == null ? "" : field.getCondition()))
									+ ") " + field.getAsName() + "2");
				}

			} else if (field.getFieldahead() != null) {
				if (field.getFieldahead().indexOf(Constants.DOTWITHDOT) > 0) {

					sqlsb.append(" , "
							+ (Constants.COUNT.equalsIgnoreCase(field.getAggregate()) ? "sum" : field.getAggregate())
							+ "(" + baseModule.getAsName() + "." + field.getChildFieldMD5Scale() + ") "
							+ field.getAsName());
				} else {
					sqlsb.append(" , " + field.getAggregate() + "(" + baseModule.getAsName() + "."
							+ field.getChildFieldMD5Scale() + ") " + field.getAsName());
				}
			} else if (field.getConditionid() == null && (field.getCondition() == null)) {
				sqlsb.append(" , " + field.getAggregate() + "(" + baseModule.getAsName() + "." + field.getFieldname()
						+ ") " + field.getAsName());
			} else {
				sqlsb.append(
						" , " + field.getAggregate() + "(" + baseModule.getAsName() + "."
								+ SqlFieldUtils.genAsName(field.getFieldname()
										+ (field.getConditionid() == null ? "" : field.getConditionid())
										+ (field.getCondition() == null ? "" : field.getCondition()))
								+ ") " + field.getAsName());
			}
		}
		sqlsb.append(CR);
		sqlsb.append("from (");
		sqlsb.append(DataminingSqlViewGenerate.generateSqlView(this));
		sqlsb.append(") " + baseModule.getAsName() + " " + CR);
		if (leveltype != null) {
			if (getGroupfield().getFDataobject()._isIdPidLevel()) {
				sqlsb.append(" where " + DATAMININGGROUP_ + "<> '" + HierarchyIDPIDUtils.UNDEFINED + "'");
			} else {
				sqlsb.append(" where " + Local.getBusinessDao().getSf().length(DATAMININGGROUP_) + " = "
						+ getGroupfield().getFDataobject()._getCodeLevelLength(Integer.parseInt(leveltype)));
			}
		}
		if (groupfieldid != null) {
			sqlsb.append(" group by " + DATAMININGGROUP_ + " ");
		}
		return sqlsb.toString();
	}

	public FDataobject getDataobject() {
		return dataobject;
	}

	public void setDataobject(FDataobject dataobject) {
		this.dataobject = dataobject;
	}

	public BaseModule getBaseModule() {
		return baseModule;
	}

	public void setBaseModule(BaseModule baseModule) {
		this.baseModule = baseModule;
	}

	public List<AggregateField> getAggretageFields() {
		return aggretageFields;
	}

	public void setAggretageFields(List<AggregateField> aggretageFields) {
		this.aggretageFields = aggretageFields;
	}

	public FDataobjectview getDataobjectview() {
		return dataobjectview;
	}

	public void setDataobjectview(FDataobjectview dataobjectview) {
		this.dataobjectview = dataobjectview;
	}

	public List<UserDefineFilter> getUserDefineFilters() {
		return userDefineFilters;
	}

	public void setUserDefineFilters(List<UserDefineFilter> userDefineFilters) {
		this.userDefineFilters = userDefineFilters;
	}

	public JSONObject getSqlparam() {
		return sqlparam;
	}

	public void setSqlparam(JSONObject sqlparam) {
		this.sqlparam = sqlparam;
	}

	public List<UserDefineFilter> getParentConditions() {
		return parentConditions;
	}

	public void setParentConditions(List<UserDefineFilter> parentConditions) {
		this.parentConditions = parentConditions;
	}

	public String getGroupDefine() {
		return groupDefine;
	}

	public void setGroupDefine(String groupDefine) {
		this.groupDefine = groupDefine;
	}

	public String getGroupfieldid() {
		return groupfieldid;
	}

	public void setGroupfieldid(String groupfieldid) {
		this.groupfieldid = groupfieldid;
	}

	public FDataobjectfield getGroupfield() {
		return groupfield;
	}

	public void setGroupfield(FDataobjectfield groupfield) {
		this.groupfield = groupfield;
	}

	public String getLeveltype() {
		return leveltype;
	}

	public void setLeveltype(String leveltype) {
		this.leveltype = leveltype;
	}

	public FFunction getfFunction() {
		return fFunction;
	}

	public void setfFunction(FFunction fFunction) {
		this.fFunction = fFunction;
	}

}
