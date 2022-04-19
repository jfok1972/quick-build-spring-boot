package com.jhopesoft.framework.core.datamining.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.AggregateField;
import com.jhopesoft.framework.core.objectquery.sqlfield.ColumnField;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ParentChildFieldUtils;

/**
 * 
 * 生成用于聚合字段查询使用的sql语句
 * 
 * @author jiangfeng
 * 
 */
public class DataminingSqlViewGenerate {

	public static String generateSqlView(DataminingSqlGenerate miningGen) {

		SqlGenerate generate = new SqlGenerate();
		generate.setDatamining(true);
		generate.setDataobject(miningGen.getDataobject());
		generate.disableAllBaseFields();
		generate.setAddIdField(true);
		generate.setDataobjectview(miningGen.getDataobjectview());
		generate.setSqlparam(miningGen.getSqlparam());
		generate.setDisableOrder(true);
		Set<ColumnField> columnFields = new LinkedHashSet<ColumnField>();
		if (miningGen.getGroupfieldid() != null) {
			ColumnField field = new ColumnField();
			ParentChildFieldUtils.updateToField(field, miningGen.getGroupfieldid());
			ColumnField groupfield = new ColumnField();
			groupfield.setFieldahead(field.getFieldahead());
			groupfield.setFDataobjectfield(
					Local.getDao().findById(FDataobjectfield.class, field.getFDataobjectfield().getFieldid()));
			if (miningGen.getfFunction() != null) {
				groupfield.setCondition(miningGen.getfFunction().getSqlExpression(miningGen.getDataobject()));
			}
			generate.setDataMiningGroupField(groupfield);
			generate.setDataMiningGroupFieldLeveltype(miningGen.getLeveltype());
		}

		for (AggregateField afield : miningGen.getAggretageFields()) {
			ColumnField field = new ColumnField();
			field.setRemark("datamining");

			if (afield.getFieldahead() == null) {
				field.setFDataobjectfield(miningGen.getDataobject()._getModuleFieldByFieldName(afield.getFieldname()));
				FDataobjectfield f = field.getFDataobjectfield();
				if (f._isPercentField() && f._hasDivisior_Denominator()) {
					FDataobjectfield fz = miningGen.getDataobject()._getModuleFieldByFieldName(f.getDivisor());
					ColumnField fzfield = new ColumnField();
					fzfield.setRemark("datamining");
					fzfield.setFDataobjectfield(fz);
					fzfield.setCondition(afield.getCondition());
					if (afield.getConditionid() != null) {
						fzfield.setFDataobjectconditionBySubconditionid(
								Local.getDao().findById(FDataobjectcondition.class, afield.getConditionid()));
					}
					columnFields.add(fzfield);

					FDataobjectfield fm = miningGen.getDataobject()._getModuleFieldByFieldName(f.getDenominator());
					ColumnField fmfield = new ColumnField();
					fmfield.setFDataobjectfield(fm);
					fmfield.setRemark("datamining");
					fmfield.setCondition(afield.getCondition()); 
					if (afield.getConditionid() != null) {
						fmfield.setFDataobjectconditionBySubconditionid(
								Local.getDao().findById(FDataobjectcondition.class, afield.getConditionid()));
					}
					columnFields.add(fmfield);
				} else {
					field.setCondition(afield.getCondition()); 
					if (afield.getConditionid() != null) {
						field.setFDataobjectconditionBySubconditionid(
								Local.getDao().findById(FDataobjectcondition.class, afield.getConditionid()));
					}
				}
			} else { 
				FDataobject subobject = DataObjectUtils.getDataObject(afield.getObjectname());
				field.setAggregate(afield.getAggregate());
				field.setFDataobjectfield(subobject._getModuleFieldByFieldName(afield.getFieldname()));
				field.setFieldahead(afield.getFieldahead());
				field.setCondition(afield.getCondition());
				field.setRemark(field.getRemark() + afield.getChildFieldMD5Scale());
				if (afield.getConditionid() != null) {
					field.setFDataobjectconditionBySubconditionid(
							Local.getDao().findById(FDataobjectcondition.class, afield.getConditionid()));
				}
			}
			columnFields.add(field);
		}
		generate.setColumnFields(columnFields);
		generate.setDataobjectview(miningGen.getDataobjectview());
		generate.setUserDefineFilters(miningGen.getParentConditions());
		if (miningGen.getUserDefineFilters() != null) {
			if (generate.getUserDefineFilters() == null) {
				generate.setUserDefineFilters(miningGen.getUserDefineFilters());
			} else {
				generate.getUserDefineFilters().addAll(miningGen.getUserDefineFilters());
			}
		}
		generate.pretreatment();
		return generate.generateSelect();
	}

}
