package com.jhopesoft.framework.core.objectquery.generate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ParentModule;
import com.jhopesoft.framework.core.objectquery.workflow.WorkFlowSqlUtils;

/**
 * 生成查询sql的 from子句
 * 
 * @author jiangfeng
 *
 */
public class FromGenerate {

	public static List<String> generateFrom(SqlGenerate sqlGenerate, BaseModule baseModule, boolean isCount,
			boolean addActViewLeftOuterJoin) {
		List<String> froms = new ArrayList<String>();
		if (baseModule.getModule().getHassqlparam() && sqlGenerate != null) {
			froms.add(genFromSqlStatment(sqlGenerate, baseModule) + " " + baseModule.getAsName());
		} else {
			froms.add(baseModule.getModule()._getTablename() + " " + baseModule.getAsName());
		}
		for (String pmkey : baseModule.getParents().keySet()) {
			ParentModule pm = baseModule.getParents().get(pmkey);
			addParentToFroms(pm, froms, isCount);
		}
		if (addActViewLeftOuterJoin && BooleanUtils.isTrue(baseModule.getModule().getHasapprove())
				&& sqlGenerate != null
				&& !sqlGenerate.getDataobject().getObjectname().toLowerCase().startsWith("vact")) {
			froms.add(WorkFlowSqlUtils.getLeftOuterJoin(baseModule));
		}

		return froms;
	}

	private static String genFromSqlStatment(SqlGenerate sqlGenerate, BaseModule baseModule) {
		String sql = baseModule.getModule().getSqlstatement();
		if (sqlGenerate.getSqlparam() != null) {
			for (String key : sqlGenerate.getSqlparam().keySet()) {
				String value = sqlGenerate.getSqlparam().getString(key);
				if (value != null) {
					value = "'" + value.replaceAll("'", "") + "'";
				} else {
					value = " null ";
				}
				sql = sql.replaceAll(":" + key, value);
			}
		}
		return "(" + sql + ")";
	}

	private static void addParentToFroms(ParentModule pmodule, List<String> froms, boolean isCount) {
		if (isCount) {
			if (pmodule.isAddToFromByFilter()) {
				froms.add(pmodule.getLeftoutterjoin());
				for (String pmkey : pmodule.getParents().keySet()) {
					addParentToFroms(pmodule.getParents().get(pmkey), froms, isCount);
				}
			}
		} else {
			if (pmodule.isAddToFromByFields() || pmodule.isAddToFromByFilter()) {
				froms.add(pmodule.getLeftoutterjoin());
				for (String pmkey : pmodule.getParents().keySet()) {
					addParentToFroms(pmodule.getParents().get(pmkey), froms, isCount);
				}
			}
		}
	}

	public List<String> generateAggregateFrom1(BaseModule baseModule, String aheadField) {
		List<String> froms = new ArrayList<String>();
		froms.add(baseModule.getModule()._getTablename() + " " + baseModule.getAsName());
		for (String pmkey : baseModule.getParents().keySet()) {
			ParentModule pm = baseModule.getParents().get(pmkey);
			if (pm.isAddToFromByFilter()) {
				addAggregateParentToFroms(pm, froms, aheadField);
			}
		}
		return froms;
	}

	private void addAggregateParentToFroms(ParentModule pmodule, List<String> froms, String aheadField) {
		if (aheadField.startsWith(pmodule.getFieldahead())) {
			if (aheadField.equals(pmodule.getFieldahead())) {
			} else { 
				pmodule.setAddToFromByFilter(true); 
				froms.add(pmodule.getLeftoutterjoin());
				for (String pmkey : pmodule.getParents().keySet()) {
					addAggregateParentToFroms(pmodule.getParents().get(pmkey), froms, aheadField);
				}
			}
		} else if (pmodule.isAddToFromByFilter()) {
			froms.add(pmodule.getLeftoutterjoin());
			for (String pmkey : pmodule.getParents().keySet()) {
				addAggregateParentToFroms(pmodule.getParents().get(pmkey), froms, aheadField);
			}
		}
	}

}
