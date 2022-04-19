package com.jhopesoft.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.FieldAggregationType;
import com.jhopesoft.framework.core.objectquery.module.BaseModule;
import com.jhopesoft.framework.core.objectquery.module.ModuleHierarchyGenerate;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.utils.FFunction;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class DataObjectFieldUtils {
	/**
	 * 加在子模块名称和 路径之间的分隔符
	 */
	public static final String CHILDSEPARATOR = ".with";

	/**
	 * 取得聚合字段的名称
	 * 
	 * @param FDataobjectfield
	 * @param fieldahead
	 * @param aggregate
	 * @param objectname         基准模块的名称
	 * @param addToAdditionField
	 * @return
	 */
	public static String getAdditionFieldname(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition, String objectname, boolean addToAdditionField) {
		// {isAdditionField: true, tf_moduleadditionfieldId: 768, tf_aggregate:
		// "min",…}
		// isAdditionField:true
		// namePath:"订单明细(订单--目的地市--省份)"
		// orginalField:{tf_fieldId: 60500040, tf_fieldName: "tf_name",
		// tf_title: "明细描述", tf_fieldType: "String",…}
		// tf_aggregate:"min"
		// tf_fieldahead:"OrdersDetail.with.tf_Orders.tf_ToCity.tf_Province"
		// tf_fieldName:"min.OrdersDetail.tf_name.with.tf_Orders.tf_ToCity.tf_Province"
		// "订单明细(订单--目的地市--省份)--明细描述--最小值"
		String fieldname = null;
		if (fieldahead != null && fieldahead.length() > 0 && fDataobjectfield != null) {
			if (aggregate == null || aggregate.equals(Constants.NORMAL)) {
				// 父模块的附加字段
				fieldname = fieldahead + "." + fDataobjectfield.getFieldname();
			} else {
				// 子模块的聚合字段
				fieldname = aggregate + "." + fieldahead.replaceFirst(CHILDSEPARATOR,
						"." + fDataobjectfield.getFieldname() + CHILDSEPARATOR);
				if (condition != null) {
					fieldname = fieldname + "|" + condition.getConditionid();
				}
			}
			// 判断一下这个附加字段在 FDataobject 里面有没有 ,这个 Fdataobject 是在缓存中的
			if (addToAdditionField) {
				FDataobject dataobject = DataObjectUtils.getDataObject(objectname);
				dataobject.addAdditionField(fieldname, fDataobjectfield, fieldahead, aggregate, condition);
			}
		}
		return fieldname;
	}

	/**
	 * 父模块或子模块的字段缺省名称
	 * 
	 * @param FDataobjectfield
	 * @param fieldahead
	 * @param aggregate
	 * @param objectname         基准模块的名称
	 * @param addToAdditionField
	 * @return
	 */
	public static String getDefaulttitle(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition, String objectname) {
		if (fieldahead != null && fieldahead.length() > 0 && fDataobjectfield != null) {
			BaseModule baseModule = DataObjectUtils.getBaseModule(objectname);
			if (aggregate == null || aggregate.length() == 0 || aggregate.equals(Constants.NORMAL))
				return baseModule.getAllParents().get(fieldahead)._getNamePath() + "--"
						+ fDataobjectfield.getFieldtitle();
			else {
				String result = baseModule.getAllChilds().get(fieldahead).getNamePath() + "--"
						+ fDataobjectfield.getFieldtitle() + "--"
						+ FieldAggregationType.AGGREGATION.get(FieldAggregationType.valueOf(aggregate.toUpperCase()));
				if (condition != null) {
					result = result + " (" + condition.getTitle() + ")";
				}
				return result;
			}
			// "订单明细(订单--客户--市)--orderdetailid--计数 (金牌客户的)"
		} else {
			return null;
		}
	}

	/**
	 * 返回父模块或者是子模块的中文描述，包括整个路径
	 * 
	 * @param objectname
	 * @param fieldahead
	 * @return
	 */
	public static String getPCModuletitle(String objectname, String fieldahead) {
		if (fieldahead != null && fieldahead.length() > 0 && objectname != null) {
			BaseModule baseModule = DataObjectUtils.getBaseModule(objectname);
			if (fieldahead.indexOf(Constants.DOTWITHDOT) > 0) {
				return baseModule.getAllChilds().get(fieldahead).getNamePath();
			} else {
				return baseModule.getAllParents().get(fieldahead)._getNamePath();
			}
		} else {
			return null;
		}
	}

	public static String getItemId(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition) {
		if (fieldahead == null) {
			return fDataobjectfield.getFieldid();
		} else {
			if (aggregate == null) {
				return fieldahead + "|" + fDataobjectfield.getFieldid();
			} else if (condition == null) {
				return fieldahead + "|" + fDataobjectfield.getFieldid() + "|" + aggregate;
			} else {
				return fieldahead + "|" + fDataobjectfield.getFieldid() + "|" + aggregate + "|"
						+ condition.getConditionid();
			}
		}
	}

	public static String getTitle(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition, FDataobject baseModule) {
		if (fieldahead == null) {
			return fDataobjectfield.getFieldtitle();
		} else {
			return getDefaulttitle(fDataobjectfield, fieldahead, aggregate, condition, baseModule.getObjectname());
		}
	}

	public static String getFieldname(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition, FDataobject baseModule) {
		if (fieldahead == null) {
			return fDataobjectfield.getFieldname();
		} else {
			return getAdditionFieldname(fDataobjectfield, fieldahead, aggregate, condition, baseModule.getObjectname(),
					false);
		}
	}

	public static JSONObject getFieldnameJson(FDataobjectfield fDataobjectfield, String fieldahead, String aggregate,
			FDataobjectcondition condition, FDataobject baseModule) {
		JSONObject object = new JSONObject();
		if (fDataobjectfield._isManyToOne() || fDataobjectfield._isOneToOne()) {
			FDataobject pobject = DataObjectUtils.getDataObject(fDataobjectfield.getFieldtype());
			object.put("fieldname", pobject._getPrimaryKeyField().getFieldname());
			object.put("objectname", pobject.getObjectname());
			// 这里不是fieldtype,而是fieldname
			object.put("fieldahead", (fieldahead != null ? fieldahead + "." : "") + fDataobjectfield.getFieldname());
		} else {
			object.put("fieldname", fDataobjectfield.getFieldname());
			object.put("objectname", fDataobjectfield.getFDataobject().getObjectname());
			if (fieldahead != null) {
				object.put("fieldahead", fieldahead);
				if (aggregate != null) {
					object.put("aggregate", aggregate);
				}
				if (condition != null) {
					object.put("subconditionid", condition.getConditionid());
				}
			}
		}
		return object;
	}

	/**
	 * 根据字段和baseModue,取得当前字段的object,如果是manytoone字段则是manytoone，如果不是的话，则是当前模块
	 * 
	 * @param FDataobjectfield
	 * @param baseModule
	 * @return
	 */
	public static FDataobject getFieldDataobject(FDataobjectfield fDataobjectfield, FDataobject baseModule) {
		if (fDataobjectfield._isManyToOne() || fDataobjectfield._isOneToOne()) {
			return DataObjectUtils.getDataObject(fDataobjectfield.getFieldtype());
		} else {
			return fDataobjectfield.getFDataobject();
		}
	}

	/**
	 * 
	 * groupfieldid 可以用以下形式来保定义 {
	 * 
	 * fieldahead :
	 * 
	 * fieldname : 有 fieldahead ，可以没有此字段，
	 * 
	 * codelevel :
	 * 
	 * function : id or title }
	 * 
	 * ,将其转换成原来的格式 fieldahead1.fieldahead2|fieldid-functionid 或 (分级 1 2 3 all)
	 */
	public static String parseFieldStrFromObject(String moduleName, JSONObject parseObject) {
		String result = null;
		FDataobject tObject = DataObjectUtils.getDataObject(moduleName);
		FDataobject pObject = null;
		FDataobjectfield field = null;

		// 要处理fieldahead的情况
		String fieldahead = parseObject.getString("fieldahead");

		if (fieldahead != null) {
			BaseModule baseModule = ModuleHierarchyGenerate.genModuleHierarchy(tObject, "main_", false);
			pObject = baseModule.getAllParents().get(fieldahead).getModule();
		}
		String fieldname = parseObject.getString("fieldname");
		if (fieldname != null) {
			if (pObject != null) {
				field = pObject._getModuleFieldByFieldName(fieldname);
			} else {
				field = tObject._getModuleFieldByFieldName(fieldname);
			}
		} else {
			// 有 fieldahead , 没有 fieldname ，则field 是 pobject 的 id
			if (pObject != null) {
				field = pObject._getPrimaryKeyField();
			}
		}
		if (field == null) {
			throw new RuntimeException("没有找到字段名称：" + fieldname);
		}
		FFunction function = null;
		if (parseObject.containsKey("function")) {
			String functionIdOrTitle = parseObject.getString("function");
			function = Local.getDao().findById(FFunction.class, functionIdOrTitle);
			if (function == null) {
				function = Local.getDao().findByPropertyFirst(FFunction.class, Constants.TITLE, functionIdOrTitle);
				if (function == null) {
					throw new RuntimeException("自定义函数id或title未找到：" + functionIdOrTitle);
				}
			}
		}
		// function 和 codelevel 只有一个生效
		result = (fieldahead != null ? fieldahead + "|" : "") + field.getFieldid();
		if (function != null) {
			result += '-' + function.getFunctionid();
		} else if (parseObject.containsKey("codelevel")) {
			result += '-' + parseObject.getString("codelevel");
		}
		return result;
	}

}
