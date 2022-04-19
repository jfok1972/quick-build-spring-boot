package com.jhopesoft.framework.core.objectquery.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;

import com.jhopesoft.framework.core.objectquery.filter.UserRoleFilterUtils;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.sqlfield.SqlField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class FieldGenerate {

	public static List<String> generateSelectFields(BaseModule baseModule, Set<SqlField> fields) {
		List<String> result = new ArrayList<String>();
		Set<FDataobjectfield> userHiddenFields = UserRoleFilterUtils.getUserHiddenFields();
		for (SqlField field : fields) {
			if (field.getObjectfield() != null) {
				if (userHiddenFields.contains(field.getObjectfield())) {
					result.add("0" + " as " + field.getScale());
					continue;
				}
			}
			result.add(field.getSqlstatment() + " as " + field.getScale());
		}
		return result;
	}

	public static List<String> generateSelectRemoteTotalFields(BaseModule baseModule, Set<SqlField> fields,
			Set<SqlField> aggregatefields, List<String> scales) {
		List<String> result = new ArrayList<String>();
		Set<FDataobjectfield> userHiddenFields = UserRoleFilterUtils.getUserHiddenFields();
		for (SqlField field : fields) {
			FDataobjectfield objectfield = field.getObjectfield();
			boolean cond = field.getFieldname().indexOf(Constants.DOTWITHDOT) > 0
					|| (objectfield != null && objectfield.getFDataobject().equals(baseModule.getModule())
							&& objectfield._isBaseField() && objectfield._isNumberField()
							&& !objectfield._isPercentField() && BooleanUtils.isTrue(objectfield.getAllowsummary()));
			if (cond) {
				if (userHiddenFields.contains(field.getObjectfield())) {
					continue;
				}
				aggregatefields.add(field);
				scales.add(field.getScale());
				result.add(field.getSqlstatment() + " as " + field.getScale());
			}
		}
		return result;
	}

	public static void adjustScale(BaseModule baseModule, Set<SqlField> fields) {
		int count = 1001;
		for (SqlField field : fields) {
			if (field.getFieldname().length() > 30 || field.getFieldname().indexOf('.') != -1) {
				field.setScale("scale_" + count++);
			}
		}
	}

}
