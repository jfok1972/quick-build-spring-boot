package com.jhopesoft.framework.core.objectquery.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.alibaba.fastjson.JSONObject;

import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.bean.SortParameter;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.bean.TreeValueText;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.ColumnField;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Repository
public class TreeModuleDataDAO {

	@Autowired
	private ModuleDataDAO moduleDataDAO;

	@Autowired
	private DaoImpl dao;

	public static final Integer PARENTWITHCODELEVEL = 1;
	public static final Integer PARENTWITHPARENTID = 2;

	/**
	 * 取得树型的值，分为codelevel 和 idpid类型
	 * 
	 * @param moduleName
	 * @param dsRequest
	 * @param gridFilterData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getTreeModuleData(String moduleName, List<UserDefineFilter> userDefineFilters,
			List<UserNavigateFilter> userNavigateFilters, List<UserParentFilter> userParentFilters,
			List<SortParameter> sortParameters) {
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (!ObjectFunctionUtils.allowQuery(module)) {
			return null;
		}
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(module);
		generate.addAllFields();
		generate.setUserDefineFilters(userDefineFilters);
		generate.setSortParameters(sortParameters);
		generate.setUserNavigateFilters(userNavigateFilters);
		generate.setUserParentFilters(userParentFilters);
		generate.pretreatment();
		List<?> result = getTreeData(generate.generateSelect(), generate.getFieldNames());
		if (BooleanUtils.isTrue(module.getHasattachment())) {
			CommonUtils.changeAttachmentsToObject((List<Map<String, Object>>) result);
		}
		String keyfieldname = module._getPrimaryKeyField().getFieldname();
		if (module._isCodeLevel()) {
			Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>(0);
			for (Object object : result) {
				Map<String, Object> dataMap = (Map<String, Object>) object;
				maps.put(dataMap.get(keyfieldname).toString(), dataMap);
			}
			String[] levelstrs = module.getCodelevel().split(Constants.COMMA);
			int[] levellen = new int[levelstrs.length];
			for (int i = 0; i < levelstrs.length; i++) {
				levellen[i] = Integer.parseInt(levelstrs[i]);
				if (i > 0) {
					levellen[i] += levellen[i - 1];
				}
			}
			for (int k = result.size() - 1; k >= 0; k--) {
				Map<String, Object> dataMap = (Map<String, Object>) result.get(k);
				String key = dataMap.get(keyfieldname).toString();
				int keylen = key.length();
				if (keylen == levellen[0]) {
					continue;
				}
				for (int i = 1; i < levellen.length; i++) {
					if (levellen[i] == keylen) {
						Map<String, Object> parent = maps.get(key.substring(0, levellen[i - 1]));
						if (parent != null) {
							parent.put("leaf", false);
							parent.put("expanded", true);
							if (!parent.containsKey(Constants.CHILDREN)) {
								parent.put(Constants.CHILDREN, new ArrayList<Map<String, Object>>());
							}
							((List<Map<String, Object>>) parent.get(Constants.CHILDREN)).add(0, dataMap);
							result.remove(k);
						}
						break;
					}
				}
			}
		} else {
			String parentkeyname = module._getParentKeyField().getFieldname();
			Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>(0);
			for (Object object : result) {
				Map<String, Object> dataMap = (Map<String, Object>) object;
				maps.put(dataMap.get(keyfieldname).toString(), dataMap);
			}
			for (int k = result.size() - 1; k >= 0; k--) {
				Map<String, Object> dataMap = (Map<String, Object>) result.get(k);
				String parentkey = dataMap.get(parentkeyname) != null ? dataMap.get(parentkeyname).toString() : null;
				if (parentkey != null) {
					Map<String, Object> parent = maps.get(parentkey);
					if (parent != null) {
						parent.put("leaf", false);
						parent.put("expanded", true);
						if (!parent.containsKey(Constants.CHILDREN)) {
							parent.put(Constants.CHILDREN, new ArrayList<Map<String, Object>>());
						}
						((List<Map<String, Object>>) parent.get(Constants.CHILDREN)).add(0, dataMap);
						result.remove(k);
					}
				}
			}
		}
		JSONObject jsonobject = new JSONObject();
		jsonobject.put(Constants.TEXT, "root");
		jsonobject.put(Constants.CHILDREN, result);
		return jsonobject;
	}

	/**
	 * 取得 模块的树形结构
	 * 
	 * @param moduleName
	 * @param userDefineFilters
	 * @param userNavigateFilters
	 * @return
	 */
	public List<TreeValueText> getRecordWithTreeData(String moduleName, Boolean allowParentValue, boolean addCodeToText,
			List<UserDefineFilter> userDefineFilters, List<UserNavigateFilter> userNavigateFilters, boolean shortName) {

		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (!ObjectFunctionUtils.allowQuery(module)) {
			return null;
		}
		List<TreeValueText> results = new ArrayList<TreeValueText>();
		if (module._isIdPidLevel()) {
			JSONObject object = getTreeModuleData(moduleName, userDefineFilters, userNavigateFilters, null, null);
			addToTreeValueText(results, (List<?>) object.get(Constants.CHILDREN), module.getPrimarykey(),
					shortName ? module.getShortNameField() : module.getNamefield(), allowParentValue);
		} else {
			List<ValueText> vts = moduleDataDAO.getRecordWithIdAndName(moduleName, userDefineFilters,
					userNavigateFilters, false , shortName);
			for (ValueText vt : vts) {
				results.add(new TreeValueText(vt.getValue(),
						vt.getText() + (addCodeToText ? "(" + vt.getValue() + ")" : "")));
			}
			List<TreeValueText> deleted = new ArrayList<TreeValueText>();
			for (int i = results.size() - 1; i > 0; i--) {
				TreeValueText record = results.get(i);
				for (int j = i - 1; j >= 0; j--) {
					TreeValueText p = results.get(j);
					if (record.getValue().startsWith(p.getValue())) {
						p.getChildren().add(0, record);
						p.setExpanded(true);
						p.setDisabled(!allowParentValue && true);
						p.setLeaf(false);
						deleted.add(record);
						break;
					}
				}
			}
			results.removeAll(deleted);
		}
		return results;

	}

	private List<?> getTreeData(String sql, String[] scales) {
		Dao dao = Local.getBusinessDao();
		NativeQuery<?> query = dao.getCurrentSession().createNativeQuery(sql);
		Map<String, Object> param = DataObjectUtils.getSqlParameter();
		if (param != null) {
			for (String key : param.keySet()) {
				if (sql.indexOf(":" + key) != -1) {
					query.setParameter(key, param.get(key));
				}
			}
		}
		List<?> result = query.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < result.size(); i++) {
			Object obj = result.get(i);
			Map<String, Object> dataMap = new HashMap<String, Object>(0);
			if (obj instanceof Object[]) {
				Object[] datas = (Object[]) obj;
				for (int j = 0; j < scales.length; j++) {
					if (scales[j] != null) {
						dataMap.put(scales[j], j < datas.length ? datas[j] : null);
					}
				}
			} else {
				dataMap.put(scales[0], obj);
			}
			dataMap.put("leaf", true);
			dataList.add(dataMap);
		}
		return dataList;
	}

	@SuppressWarnings("unchecked")
	private void addToTreeValueText(List<TreeValueText> results, List<?> array, String primarykey, String namefield,
			Boolean allowParentValue) {
		for (int i = 0; i < array.size(); i++) {
			Map<String, Object> object = (Map<String, Object>) array.get(i);
			TreeValueText valuetext = new TreeValueText(object.get(primarykey).toString(),
					object.get(namefield).toString());
			if (object.containsKey(Constants.CHILDREN)) {
				valuetext.setParenttype(PARENTWITHPARENTID);
				valuetext.setChildren(new ArrayList<TreeValueText>());
				valuetext.setExpanded(true);
				valuetext.setLeaf(false);
				valuetext.setDisabled(!allowParentValue && true);
				addToTreeValueText(valuetext.getChildren(), (List<?>) object.get(Constants.CHILDREN), primarykey,
						namefield, allowParentValue);
			}
			results.add(valuetext);
		}

	}

	public List<TreeNode> getTreeSelectPathData(String objectname, boolean addcheck, boolean disablenotleaf,
			String treeselectpath) {
		FDataobject module = DataObjectUtils.getDataObject(objectname);
		if (StringUtils.isBlank(treeselectpath)) {
			treeselectpath = module.getTreeselectpath();
		}
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(module);
		generate.disableAllBaseFields();
		generate.setAddIdField(true);
		generate.setAddNameField(true);
		String idfield = module._getPrimaryKeyField().getFieldname();
		String namefield = module._getNameField().getFieldname();
		String pidfield = treeselectpath;
		String pnamefield = treeselectpath;
		FDataobjectfield headfield;
		FDataobject pobject = null;
		String fieldahead = null;
		Set<ColumnField> columnFields = new LinkedHashSet<ColumnField>();
		String[] paths = treeselectpath.split("\\.");
		if (paths.length == 1) {
			headfield = module._getModuleFieldByFieldName(paths[0]);
			ColumnField field = new ColumnField();
			field.setFDataobjectfield(headfield);
			columnFields.add(field);
		} else {
			fieldahead = treeselectpath.substring(0, treeselectpath.lastIndexOf('.'));
			FDataobject pmodule = module;
			for (int i = 0; i < paths.length - 1; i++) {
				FDataobjectfield field = pmodule._getModuleFieldByFieldName(paths[i]);
				pmodule = DataObjectUtils.getDataObject(field.getFieldtype());
			}
			headfield = pmodule._getModuleFieldByFieldName(paths[paths.length - 1]);
			ColumnField field = new ColumnField();
			field.setFDataobjectfield(headfield);
			field.setFieldahead(fieldahead);
			columnFields.add(field);
		}
		if (headfield.getFDictionary() != null) {
			pnamefield = pnamefield + "_dictname";
		}
		List<SortParameter> sortParameters = new ArrayList<SortParameter>();
		SortParameter sortParameter = new SortParameter();
		if (headfield._isManyToOne() || headfield._isOneToOne()) {
			pobject = DataObjectUtils.getDataObject(headfield.getFieldtype());
			pidfield = pidfield + "." + pobject._getPrimaryKeyField().getFieldname();
			pnamefield = pnamefield + "." + pobject._getNameField().getFieldname();
			if (StringUtils.isNotBlank(pobject.getOrderfield())) {
				sortParameter.setProperty(treeselectpath + '.' + pobject.getOrderfield());
			} else {
				sortParameter.setProperty(treeselectpath);
			}
			sortParameters.add(sortParameter);
		} else {
			sortParameters.add(new SortParameter(treeselectpath, "asc"));
		}
		if (!StringUtils.isNotBlank(module.getOrderfield())) {
			sortParameters.add(new SortParameter(module.getOrderfield(), "asc"));
		}
		generate.setSortParameters(sortParameters);
		generate.setColumnFields(columnFields);
		generate.pretreatment();
		String sql = generate.generateSelect();
		String[] fields = generate.getFieldNames();
		PageInfo<Map<String, Object>> records = dao.executeSQLQueryPage(sql, fields, 0, Integer.MAX_VALUE,
				Integer.MAX_VALUE, new Object[] {});
		List<Map<String, Object>> data = records.getData();

		List<TreeNode> allresult = new ArrayList<TreeNode>();
		TreeNode currentpNode = null;
		for (Map<String, Object> map : data) {
			String pid = map.get(pidfield) != null ? map.get(pidfield).toString() : "未设置";
			if (currentpNode != null && currentpNode.getObjectid().equals(pid)) {
			} else {
				currentpNode = new TreeNode(map.get(pnamefield) != null ? map.get(pnamefield).toString() : "未设置");
				currentpNode.setObjectid(pid);
				currentpNode.setChildren(new ArrayList<TreeNode>());
				currentpNode.setLeaf(false);
				currentpNode.setDisabled(disablenotleaf ? true : null);
				currentpNode.setExpanded(true);
				currentpNode.setChecked(addcheck ? false : null);
				allresult.add(currentpNode);
			}
			TreeNode node = new TreeNode((String) map.get(namefield));
			node.setObjectid(map.get(idfield).toString());
			node.setLeaf(true);
			node.setChecked(addcheck ? false : null);
			if (StringUtils.isNotBlank(module.getIconcls())) {
				node.setIconCls(module.getIconcls());
			}
			currentpNode.getChildren().add(node);
		}
		if (pobject != null && BooleanUtils.isTrue(pobject.getIstreemodel())) {
			List<TreeValueText> alltree = getRecordWithTreeData(pobject.getObjectid(), true, false, null, null, false);
			allresult = createTreeNode(alltree, addcheck, disablenotleaf, allresult);
		}
		if (allresult != null && allresult.size() > 1) {
			TreeNode root = new TreeNode(pobject != null ? pobject.getTitle() : headfield.getFieldtitle());
			root.setLeaf(false);
			root.setExpanded(true);
			root.setDisabled(disablenotleaf ? true : null);
			root.setChecked(addcheck ? false : null);
			List<TreeNode> result = new ArrayList<TreeNode>();
			root.setChildren(allresult);
			result.add(root);
			return result;
		} else {
			return allresult;
		}
	}

	/**
	 * 把当前的父模块--当前模块，的结果加到父模块的树形中，不包括明细节点的都删除。
	 * 
	 * @param sourceTree
	 * @param addcheck
	 * @param disablenotleaf
	 * @param allresult
	 * @return
	 */
	private List<TreeNode> createTreeNode(List<TreeValueText> sourceTree, boolean addcheck, boolean disablenotleaf,
			List<TreeNode> allresult) {
		if (sourceTree == null || sourceTree.size() == 0) {
			return null;
		}
		List<TreeNode> result = new ArrayList<TreeNode>();
		for (TreeValueText source : sourceTree) {
			TreeNode pNode = new TreeNode(source.getText());
			pNode.setObjectid(source.getValue());
			pNode.setChildren(new ArrayList<TreeNode>());
			for (TreeNode tn : allresult) {
				if (tn.getObjectid().equals(source.getValue())) {
					pNode.getChildren().addAll(tn.getChildren());
					break;
				}
			}
			if (source.getChildren() != null && source.getChildren().size() > 0) {
				List<TreeNode> list = createTreeNode(source.getChildren(), addcheck, disablenotleaf, allresult);
				if (list != null) {
					pNode.getChildren().addAll(list);
				}
			}
			pNode.setLeaf(false);
			pNode.setDisabled(disablenotleaf ? true : null);
			pNode.setExpanded(true);
			pNode.setChecked(addcheck ? false : null);
			if (pNode.getChildren().size() > 0) {
				result.add(pNode);
			}
		}
		return result;
	}

}
