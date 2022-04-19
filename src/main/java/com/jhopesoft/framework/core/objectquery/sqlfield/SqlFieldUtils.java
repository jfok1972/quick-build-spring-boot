package com.jhopesoft.framework.core.objectquery.sqlfield;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.filter.JsonToConditionField;
import com.jhopesoft.framework.core.objectquery.generate.AggregateSqlGenerate;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ParentModule;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;
import com.jhopesoft.framework.dao.entity.dataobject.FAdditionfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dictionary.FDictionary;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridscheme;
import com.jhopesoft.framework.dao.entityinterface.ParentChildField;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.MD5;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.framework.utils.ParentChildFieldUtils;

/**
 * 
 * @author jiangfeng
 *
 */
public class SqlFieldUtils {

	public static Set<SqlField> addIdField(BaseModule baseModule, Set<SqlField> result) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		addBaseField(baseModule, baseModule.getModule()._getPrimaryKeyField(), result, false, null, null);
		return result;
	}

	public static Set<SqlField> addShortNameField(BaseModule baseModule, Set<SqlField> result) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		addBaseField(baseModule, baseModule.getModule()._getShortNameField(), result, false, null, null);
		return result;
	}

	public static Set<SqlField> addNameField(BaseModule baseModule, Set<SqlField> result, boolean mainlinkage) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		FDataobjectfield namefield = baseModule.getModule()._getNameField();
		boolean isadd = false;
		if (mainlinkage) {
			FDataobjectfield linkagefield = baseModule.getModule()._getMainLinkageField();
			if (linkagefield != null) {
				ParentModule linkagepm = baseModule.getAllParents().get(linkagefield.getFieldname());
				if (linkagepm != null) {
					isadd = true;
					linkagepm.setAddToFromByFields(true);
					result.add(new SqlField(baseModule.getModule()._getNameField().getFieldname(),
							Local.getBusinessDao().getSf()
									.link(new String[] {
											linkagepm.getModule()._getNameField()._getSelectName(linkagepm.getAsName()),
											"' / '", namefield._getSelectName(baseModule.getAsName()) }),
							namefield));
				}
			}
		}
		if (!isadd) {
			addBaseField(baseModule, namefield, result, false, null, null);
		}
		return result;
	}

	public static Set<SqlField> addPidField(BaseModule baseModule, Set<SqlField> result) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		addBaseField(baseModule, baseModule.getModule()._getPidField(), result, false, null, null);
		return result;
	}

	public static Set<SqlField> addAllBaseField(BaseModule baseModule, Set<SqlField> result) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		for (FDataobjectfield field : baseModule.getModule().getFDataobjectfields()) {
			addBaseField(baseModule, field, result, false, null, null);
		}
		return result;
	}

	public static Set<SqlField> addBaseField(BaseModule baseModule, FDataobjectfield field, Set<SqlField> result,
			boolean isFilter, String condition, String asName) {
		if ("datamining".equals(asName)) {
			asName = null;
		}
		if (field.getIsdisable() == null || !field.getIsdisable()) {
			if (field._isAdditionField()) {
				result.add(new SqlField(asName == null ? field.getFieldname() : asName,
						addConditionToFieldSql(generateUserDefineField(baseModule, null, field, isFilter), condition),
						field));
			} else if (field._isBaseField()) {
				result.add(new SqlField(asName == null ? field.getFieldname() : asName,
						addConditionToFieldSql(field._getSelectName(baseModule.getAsName()), condition), field));
				if (field.getFDictionary() != null) {
					result.add(
							new SqlField((asName == null ? field.getFieldname() : asName) + FDictionary.NAMEENDS,
									DictionaryFieldGenerate.getDictionaryTextField(
											field._getSelectName(baseModule.getAsName()), field.getFDictionary()),
									field));
				}
			} else if (field._isManyToMany()) {
				result.add(
						new SqlField(field.getFieldname(), ManyToManyField._getSelectName(baseModule, field), field));
				result.add(new SqlField(field.getFieldname() + "_detail",
						ManyToManyField._getSelectDetailName(baseModule, field), field));
			} else if (field._isManyToOne() || field._isOneToOne()) {
				ParentModule pm = baseModule.getParents().get(field.getFieldname());
				if (isFilter) {
					pm.setAddToFromByFilter(true);
				} else {
					pm.setAddToFromByFields(true);
				}
				result.add(new SqlField(pm.getPrimarykeyField().getAsName(), pm.getPrimarykeyField().getFieldsql(),
						field));
				result.add(new SqlField(pm.getNameField().getAsName(), pm.getNameField().getFieldsql(), field));
			} else if (field._isOneToMany()) {
				result.add(new SqlField(field.getFieldname(),
						new AggregateSqlGenerate(baseModule, field).pretreatment().generateSelect(), field));
			}
		}
		return result;
	}

	public static Set<SqlField> addDataminingBaseField(BaseModule baseModule, FDataobjectfield field,
			Set<SqlField> result, boolean isFilter, FDataobjectcondition datacondition, String condition) {
		if (BooleanUtils.isNotTrue(field.getIsdisable())) {
			String cstr1 = null;
			if (datacondition != null) {
				cstr1 = datacondition._getConditionExpression();
			}
			String cstr2 = null;
			if (condition != null) {
				cstr2 = GroupConditionUtils.changeGroupConditionToJson(condition);
			}
			String cstr = "";
			if (cstr1 != null && cstr2 != null) {
				cstr = cstr1 + " and " + cstr2;
			} else {
				if (cstr1 != null) {
					cstr = cstr1;
				} else {
					cstr = cstr2;
				}
			}
			cstr = "(" + cstr + ")";
			String casewhen = generateSqlFormJsonFieldString(baseModule, null, cstr, isFilter);
			String asname = genAsName(
					field.getFieldname() + (datacondition == null ? "" : datacondition.getConditionid())
							+ (condition == null ? "" : condition));

			if (field._isAdditionField()) {
				result.add(new SqlField(asname,
						genCaseWhen(casewhen, generateUserDefineField(baseModule, null, field, isFilter)), field));
			} else if (field._isBaseField()) {
				result.add(new SqlField(asname, genCaseWhen(casewhen, field._getSelectName(baseModule.getAsName())),
						field));
			}
		}
		return result;
	}

	public static Set<SqlField> addDataminingParentField(Set<SqlField> result, BaseModule baseModule, String fieldahead,
			FDataobjectfield field, boolean isFilter, FDataobjectcondition datacondition, String condition,
			String asname) {
		if (BooleanUtils.isNotTrue(field.getIsdisable())) {
			String cstr1 = null;
			if (datacondition != null) {
				cstr1 = datacondition._getConditionExpression();
			}
			String cstr2 = null;
			if (condition != null) {
				cstr2 = GroupConditionUtils.changeGroupConditionToJson(condition);
			}
			String cstr = "";
			if (cstr1 != null && cstr2 != null) {
				cstr = cstr1 + " and " + cstr2;
			} else {
				if (cstr1 != null) {
					cstr = cstr1;
				} else {
					cstr = cstr2;
				}
			}
			cstr = "(" + cstr + ")";
			String casewhen = generateSqlFormJsonFieldString(baseModule, null, cstr, isFilter);
			ParentModule pm = baseModule.getAllParents().get(fieldahead);
			if (isFilter) {
				pm.setAddToFromByFilter(true);
			} else {
				pm.setAddToFromByFields(true);
			}
			if (field._isAdditionField()) {
				result.add(new SqlField(asname,
						genCaseWhen(casewhen, generateUserDefineField(baseModule, pm, field, isFilter)), field));
			} else if (field._isBaseField()) {
				result.add(new SqlField(asname, genCaseWhen(casewhen, field._getSelectName(pm.getAsName())), field));
			}
		}
		return result;
	}

	public static Set<SqlField> addDataminingChildField(BaseModule baseModule, ParentChildField field, String asname,
			String condition, Set<SqlField> result, boolean isFilter) {
		if (field.getFDataobjectfield().getIsdisable() == null || !field.getFDataobjectfield().getIsdisable()) {
			if (condition != null) {
				String cstr = null;
				cstr = GroupConditionUtils.changeGroupConditionToJson(condition);
				cstr = "(" + cstr + ")";
				String casewhen = generateSqlFormJsonFieldString(baseModule, null, cstr, isFilter);
				result.add(new SqlField(asname,
						genCaseWhen(casewhen,
								new AggregateSqlGenerate(baseModule, field).pretreatment().generateSelect()),
						field.getFDataobjectfield()));
			} else {
				result.add(new SqlField(asname,
						new AggregateSqlGenerate(baseModule, field).pretreatment().generateSelect(),
						field.getFDataobjectfield()));
			}
		}
		return result;
	}

	private static String genCaseWhen(String when, String value) {
		return "( case when " + when + " then " + value + " else null end )";
	}

	public static String genAsName(String fieldname) {
		return ("f_" + MD5.MD5Encode(fieldname)).substring(0, 30);
	}

	public static String generateUserDefineField(BaseModule baseModule, ParentModule pmodule, FDataobjectfield field,
			boolean isFilter) {
		FAdditionfield additionField = Local.getDao().findById(FAdditionfield.class,
				field.getFAdditionfield().getAdditionfieldid());
		return generateSqlFormJsonFieldString(baseModule, pmodule, additionField._getConditionExpression(), isFilter);
	}

	public static String addConditionToFieldSql(String sql, String condition) {
		if (condition == null || condition.length() == 0)
			return sql;
		else {
			StringBuffer resultBuffer = new StringBuffer();
			String s = "\\d+.\\d+|\\w+";
			Pattern patternthis = Pattern.compile(s);
			Matcher matcherthis = patternthis.matcher(condition);
			while (matcherthis.find()) {
				if (matcherthis.group().equals(Constants.THIS)) {
					matcherthis.appendReplacement(resultBuffer, sql);
				}
			}
			matcherthis.appendTail(resultBuffer);
			return resultBuffer.toString();
		}
	}

	public static String changeGloablParam(String str) {
		if (str == null) {
			return null;
		}
		String regex = "\\$\\{.*?\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		StringBuffer resultBuffer = new StringBuffer();
		while (matcher.find()) {
			String s = matcher.group();
			try {
				matcher.appendReplacement(resultBuffer, Ognl
						.getValue(s.substring(2, s.length() - 1), Local.getCriticalObject().getUserBean()).toString());
			} catch (OgnlException e) {
				throw new RuntimeException("在解析条件时：" + s + " 出错了！");
			}
		}
		matcher.appendTail(resultBuffer);
		return resultBuffer.toString();
	}

	public static String generateSqlFormJsonFieldString(BaseModule baseModule, ParentModule pmodule, String fieldString,
			boolean isFilter) {
		String regex = "\\{[^}]*\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(fieldString);
		StringBuffer resultBuffer = new StringBuffer();
		String preAhead = pmodule == null ? "" : pmodule.getFieldahead() + ".";
		while (matcher.find()) {
			JsonToConditionField f = JSONObject.parseObject(matcher.group(), JsonToConditionField.class);
			f._setfDataobjectfield();
			if (f.getAggregate() == null || f.getAggregate().length() == 0) {
				if (f.getFieldahead() != null && f.getFieldahead().length() > 0) {
					ParentModule pm = baseModule.getAllParents().get(preAhead + f.getFieldahead());
					if (isFilter) {
						pm.setAddToFromByFilter(true);
					} else {
						pm.setAddToFromByFields(true);
					}
					if (f.getFDataobjectfield()._isAdditionField()) {
						matcher.appendReplacement(resultBuffer, formatFieldWithCondition(f.getCondition(),
								generateUserDefineField(baseModule, pm, f.getFDataobjectfield(), isFilter)));
					} else {
						matcher.appendReplacement(resultBuffer, formatFieldWithCondition(f.getCondition(),
								f.getFDataobjectfield()._getSelectName(pm.getAsName())));
					}
				} else {
					if (f.getFDataobjectfield()._isAdditionField()) {
						matcher.appendReplacement(resultBuffer, formatFieldWithCondition(f.getCondition(),
								generateUserDefineField(baseModule, pmodule, f.getFDataobjectfield(), isFilter)));
					} else {
						matcher.appendReplacement(resultBuffer,
								formatFieldWithCondition(f.getCondition(), f.getFDataobjectfield()._getSelectName(
										pmodule == null ? baseModule.getAsName() : pmodule.getAsName())));
					}
				}
			} else {
				matcher.appendReplacement(resultBuffer,
						new AggregateSqlGenerate(baseModule, f).pretreatment().generateSelect());
			}
		}
		matcher.appendTail(resultBuffer);
		return resultBuffer.toString();

	}

	public static String formatFieldWithCondition(String condition, String field) {
		if (condition == null) {
			return field;
		}
		StringBuffer resultBuffer = new StringBuffer();
		String s = "\\d+.\\d+|\\w+";
		Pattern patternthis = Pattern.compile(s);
		Matcher matcherthis = patternthis.matcher(condition);
		while (matcherthis.find()) {
			if (matcherthis.group().equals(Constants.THIS)) {
				matcherthis.appendReplacement(resultBuffer, field);
			}
		}
		matcherthis.appendTail(resultBuffer);
		return resultBuffer.toString();
	}

	public static Set<SqlField> addParentField(BaseModule baseModule, FDataobjectfield field, String fieldahead,
			Set<SqlField> result, boolean isFilter, String condition, String asName) {
		if (BooleanUtils.isNotTrue(field.getIsdisable()) && BooleanUtils.isNotTrue(field.getUserdisable())) {
			ParentModule pm = baseModule.getAllParents().get(fieldahead);
			if (isFilter) {
				pm.setAddToFromByFilter(true);
			} else {
				pm.setAddToFromByFields(true);
			}
			if (field._isAdditionField()) {
				result.add(new SqlField(asName == null ? fieldahead + "." + field.getFieldname() : asName,
						addConditionToFieldSql(generateUserDefineField(baseModule, pm, field, isFilter), condition),
						field));
			} else if (field._isBaseField()) {
				result.add(new SqlField(asName == null ? fieldahead + "." + field.getFieldname() : asName,
						addConditionToFieldSql(field._getSelectName(pm.getAsName()), condition), field));
				if (field.getFDictionary() != null) {
					result.add(new SqlField(
							asName == null ? fieldahead + "." + field.getFieldname() + FDictionary.NAMEENDS
									: asName + FDictionary.NAMEENDS,
							DictionaryFieldGenerate.getDictionaryTextField(field._getSelectName(pm.getAsName()),
									field.getFDictionary()),
							field));
				}
			} else if (field._isManyToOne() || field._isOneToOne()) {
				ParentModule pmpm = pm.getParents().get(field.getFieldname());
				if (pmpm == null) {
					throw new RuntimeException(
							"模块：" + pm.getModule().getTitle() + " 的父模块路径：" + field.getFieldname() + " 没有找到。");
				}
				if (isFilter) {
					pmpm.setAddToFromByFilter(true);
				} else {
					pmpm.setAddToFromByFields(true);
				}
				result.add(new SqlField(pmpm.getPrimarykeyField().getAsName(), pmpm.getPrimarykeyField().getFieldsql(),
						field));
				result.add(new SqlField(pmpm.getNameField().getAsName(), pmpm.getNameField().getFieldsql(), field));
			}
			if (field._isManyToMany()) {
			}
		}
		return result;
	}

	public static Set<SqlField> addAttachmentField(BaseModule baseModule, Set<SqlField> result) {
		if (ObjectFunctionUtils.allowQueryAttachment(baseModule.getModule())) {
			result.add(
					new SqlField(FDataobjectattachment.COUNT, AttachmentFieldGenerate.getCountField(baseModule), null));
			result.add(new SqlField(FDataobjectattachment.TOOLTIP, AttachmentFieldGenerate.getTooltipField(baseModule),
					null));
		}
		return result;
	}

	public static Set<SqlField> addUserDefinedField(BaseModule baseModule, Set<?> fields, Set<SqlField> result) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		for (Object gridField : fields) {
			ParentChildField field = (ParentChildField) gridField;
			getSqlFieldFromParentChildField(baseModule, field, result, false);
		}
		return result;
	}

	public static Set<SqlField> getSqlFieldFromParentChildField(BaseModule baseModule, ParentChildField field,
			Set<SqlField> result, boolean isFilter) {
		if (result == null) {
			result = new LinkedHashSet<SqlField>();
		}
		if (field.getFieldahead() == null || field.getFieldahead().length() == 0) {
			if ("datamining".equals(field.getRemark())
					&& (field.getFDataobjectconditionBySubconditionid() != null || field.getCondition() != null)) {
				addDataminingBaseField(baseModule, field.getFDataobjectfield(), result, isFilter,
						field.getFDataobjectconditionBySubconditionid(), field.getCondition());
			} else {
				addBaseField(baseModule, field.getFDataobjectfield(), result, isFilter, field.getCondition(),
						field.getRemark());
			}
		} else if (field.getAggregate() == null || field.getAggregate().length() == 0) {
			addParentField(baseModule, field.getFDataobjectfield(), field.getFieldahead(), result, isFilter,
					field.getCondition(), field.getRemark());
		} else {
			if (field.getRemark() != null && field.getRemark().startsWith("datamining")) {
				if (field.getFieldahead().indexOf(".with") > 0) {
					addDataminingChildField(baseModule, field, field.getRemark().replaceFirst("datamining", ""),
							field.getCondition(), result, isFilter);
				} else {
					if (field.getCondition() != null) {
						addDataminingParentField(result, baseModule, field.getFieldahead(), field.getFDataobjectfield(),
								isFilter, null,
								field.getCondition(), field.getRemark().replaceFirst("datamining", ""));
					} else {
						addParentField(baseModule, field.getFDataobjectfield(), field.getFieldahead(), result, isFilter,
								field.getCondition(), field.getRemark().replaceFirst("datamining", ""));
					}
				}
			} else {
				result.add(new SqlField(
						field.getRemark() != null ? field.getRemark() : ParentChildFieldUtils.generateFieldName(field),
						new AggregateSqlGenerate(baseModule, field).pretreatment().generateSelect(),
						field.getFDataobjectfield()));
			}
		}
		return result;
	}

	public static Set<SqlField> addGridSchemeField(BaseModule baseModule, FovGridscheme scheme, Set<SqlField> result) {
		return addUserDefinedField(baseModule, scheme._getFields(), result);
	}

	public static Set<SqlField> addFormSchemeField(BaseModule baseModule, FovFormscheme scheme, Set<SqlField> result) {
		return addUserDefinedField(baseModule, scheme._getFields(), result);
	}

	public static SqlField getSqlFieldFromFields(Set<SqlField> fields, String fieldname) {
		for (SqlField f : fields) {
			if (f.getFieldname().equals(fieldname)) {
				return f;
			}
		}
		return null;
	}

}
