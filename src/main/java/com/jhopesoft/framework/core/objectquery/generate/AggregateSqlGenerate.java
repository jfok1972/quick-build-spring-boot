package com.jhopesoft.framework.core.objectquery.generate;

import java.util.List;

import com.jhopesoft.framework.bean.FieldAggregationType;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ModuleHierarchyGenerate;
import com.jhopesoft.framework.core.objectquery.module.ParentModule;
import com.jhopesoft.framework.core.objectquery.sqlfield.SqlFieldUtils;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 聚合字段sql语句的生成器
 * 
 * @author jiangfeng jfok1972@qq.com
 *
 */
public class AggregateSqlGenerate implements ParentChildField {

	private static final String CR = "\r\n";
	private static final String TAB = "\t";

	private BaseModule parentBaseModule;
	private BaseModule baseModule;
	private FDataobject dataobject;

	private FDataobjectfield FDataobjectfield;
	private FDataobjectcondition FDataobjectconditionBySubconditionid;
	private String condition;
	private String fieldahead;
	private String aggregate;
	private String remark;
	private List<UserDefineFilter> userDefineFilters;

	private List<String> wheres;

	public AggregateSqlGenerate() {

	}

	public AggregateSqlGenerate(BaseModule parentBaseModule, FDataobjectfield onetomanyField) {
		this.parentBaseModule = parentBaseModule;
		String fieldtype = onetomanyField.getFieldtype();
		FDataobject manyobject = DataObjectUtils
				.getDataObject(fieldtype.substring(fieldtype.indexOf('<') + 1, fieldtype.indexOf('>')));
		FDataobjectfield manyfield = manyobject._getPrimaryKeyField();
		this.dataobject = manyobject;
		this.FDataobjectfield = manyfield;
		this.fieldahead = onetomanyField.getFieldahead().split(Constants.DOTWITHDOT)[1];
		this.aggregate = Constants.COUNT;
	}

	public AggregateSqlGenerate(BaseModule parentBaseModule, ParentChildField parentChildField) {
		this.parentBaseModule = parentBaseModule;
		this.FDataobjectfield = parentChildField.getFDataobjectfield();
		this.dataobject = this.FDataobjectfield.getFDataobject();
		this.fieldahead = parentChildField.getFieldahead();
		if (this.fieldahead.indexOf(Constants.DOTWITHDOT) > 0) {
			this.fieldahead = this.fieldahead.split(Constants.DOTWITHDOT)[1];
			this.aggregate = parentChildField.getAggregate();
			this.FDataobjectconditionBySubconditionid = parentChildField.getFDataobjectconditionBySubconditionid();
		} else {
			this.aggregate = parentChildField.getAggregate();
			this.FDataobjectconditionBySubconditionid = parentChildField.getFDataobjectconditionBySubconditionid();
		}
	}

	public AggregateSqlGenerate(BaseModule parentBaseModule, String fieldname) {
		this.parentBaseModule = parentBaseModule;
		String[] part = fieldname.split("\\|");
		if (part.length == Constants.INT_2) {
			this.FDataobjectconditionBySubconditionid = Local.getDao().findById(FDataobjectcondition.class, part[1]);
		}
		part = part[0].split(Constants.DOTWITHDOT);
		this.fieldahead = part[1];
		part = part[0].split(".");
		this.aggregate = part[0];
		dataobject = DataObjectUtils.getDataObject(part[1]);
		FDataobjectfield = dataobject._getModuleFieldByFieldName(part[2]);

	}

	public AggregateSqlGenerate pretreatment() {
		baseModule = ModuleHierarchyGenerate.genModuleHierarchy(dataobject, "aggregate_", false);
		ParentModule parentModule = baseModule.getAllParents().get(fieldahead);
		parentModule.setDonotAddUserDataFilter(true);
		if (fieldahead.indexOf(Constants.DOT) != -1) {
			Object sModule = parentModule.getSonModuleHierarchy();
			if (sModule instanceof ParentModule) {
				((ParentModule) sModule).setAddToFromByFilter(true);
			}
		}
		wheres = WhereGenerate.generateAgreegateWhere(this);
		return this;
	}

	public String generateFmSelect() {
		AggregateSqlGenerate aPart = new AggregateSqlGenerate();
		aPart.aggregate = Constants.SUM;
		aPart.parentBaseModule = this.parentBaseModule;
		aPart.dataobject = this.dataobject;
		aPart.FDataobjectfield = dataobject._getModuleFieldByFieldName(this.FDataobjectfield.getDenominator());
		aPart.fieldahead = this.fieldahead;
		aPart.FDataobjectconditionBySubconditionid = this.FDataobjectconditionBySubconditionid;
		return aPart.pretreatment().generateSelect();
	}

	public String generateFzSelect() {
		AggregateSqlGenerate aPart = new AggregateSqlGenerate();
		aPart.aggregate = Constants.SUM;
		aPart.parentBaseModule = this.parentBaseModule;
		aPart.dataobject = this.dataobject;
		aPart.FDataobjectfield = dataobject._getModuleFieldByFieldName(this.FDataobjectfield.getDivisor());
		aPart.fieldahead = this.fieldahead;
		aPart.FDataobjectconditionBySubconditionid = this.FDataobjectconditionBySubconditionid;
		return aPart.pretreatment().generateSelect();
	}

	public String generateSelect() {
		StringBuilder sql = null;
		if (FieldAggregationType.WAVG.getValue().equals(aggregate)) {
			sql = new StringBuilder();
			String fz = generateFzSelect();
			String fm = generateFmSelect();
			sql.append("( case when " + fm + " = 0 then null else " + fz + " / " + fm + " end )");
		} else {
			sql = new StringBuilder(" (((((((((( select ");
			String fieldStr = "aggregate_." + FDataobjectfield._getSelectName(null);
			if (FDataobjectfield._isAdditionField()) {
				fieldStr = SqlFieldUtils.generateUserDefineField(baseModule, null, FDataobjectfield, false);
			}
			sql.append(aggregate + "(" + fieldStr + ") " + CR);
			sql.append(TAB + TAB + " from " + CR);
			for (String from : FromGenerate.generateFrom(null, baseModule, false, hasActFieldinCondition())) {
				sql.append(TAB + from + CR);
			}
			if (wheres.size() > 0) {
				sql.append(TAB + TAB + " where " + CR);
				for (String where : wheres) {
					sql.append(TAB + "(" + where + ")");
					sql.append((where == wheres.get(wheres.size() - 1) ? "" : " and ") + CR);
				}
			}
			sql.append(" )))))))))) ");
		}
		return Local.getBusinessDao().getSf().adjustSqlstatment(sql.toString());
	}

	private boolean hasActFieldinCondition() {
		if (wheres.size() == 0) {
			return false;
		}
		for (String where : wheres) {
			if (where.indexOf("act_") >= 0) {
				return true;
			}
		}
		return false;
	}

	public FDataobjectfield getFDataobjectfield() {
		return FDataobjectfield;
	}

	public void setFDataobjectfield(FDataobjectfield fDataobjectfield) {
		FDataobjectfield = fDataobjectfield;
	}

	public FDataobjectcondition getFDataobjectconditionBySubconditionid() {
		return FDataobjectconditionBySubconditionid;
	}

	public void setFDataobjectconditionBySubconditionid(FDataobjectcondition fDataobjectconditionBySubconditionid) {
		FDataobjectconditionBySubconditionid = fDataobjectconditionBySubconditionid;
	}

	public String getFieldahead() {
		return fieldahead;
	}

	public void setFieldahead(String fieldahead) {
		this.fieldahead = fieldahead;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	public FDataobject getDataobject() {
		return dataobject;
	}

	public void setDataobject(FDataobject dataobject) {
		this.dataobject = dataobject;
	}

	public BaseModule getParentBaseModule() {
		return parentBaseModule;
	}

	public void setParentBaseModule(BaseModule parentBaseModule) {
		this.parentBaseModule = parentBaseModule;
	}

	public BaseModule getBaseModule() {
		return baseModule;
	}

	public void setBaseModule(BaseModule baseModule) {
		this.baseModule = baseModule;
	}

	public List<UserDefineFilter> getUserDefineFilters() {
		return userDefineFilters;
	}

	public void setUserDefineFilters(List<UserDefineFilter> userDefineFilters) {
		this.userDefineFilters = userDefineFilters;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String value) {
		this.remark = value;
	}

}
