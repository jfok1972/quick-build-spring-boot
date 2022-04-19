package com.jhopesoft.framework.core.datamining.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingcolumngroup;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingfilter;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingrowgroup;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingrowgrouppath;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingscheme;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingselectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.MD5;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataminingDataService {

	private static final String VALUE = "value";
	private static final String LEAF = "leaf";

	@Autowired
	private DaoImpl dao;

	@Autowired
	private DataminingService dataminingService;

	@SuppressWarnings("unchecked")
	public List<?> fetchPivotData(String dataminingschemeid, String schemetitle, String additionviewschemeid,
			String additionuserfilters, boolean addTotal) {
		List<Map<String, Object>> treeAllResult = (List<Map<String, Object>>) fetchDataminingData(dataminingschemeid,
				true,
				additionviewschemeid,
				additionuserfilters, schemetitle, true);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (addTotal) {
			Map<String, Object> totalMap = new HashMap<String, Object>(0);
			totalMap.putAll(treeAllResult.get(0));
			totalMap.remove(Constants.CHILDREN);
			result.add(totalMap);
		}
		List<Map<String, Object>> treeResult = (List<Map<String, Object>>) treeAllResult.get(0).get(Constants.CHILDREN);
		addLeafToResult(treeResult, new ArrayList<>(), result, 1, addTotal);
		return result;
	}

	/**
	 * 
	 * 第一层的指标为 text1,value1,第二层的指标为text2,value2
	 * 
	 * @param records
	 * @param parentInfo
	 * @param result
	 * @param level
	 */
	@SuppressWarnings("unchecked")
	public void addLeafToResult(List<Map<String, Object>> records, List<Map<String, Object>> parentInfo,
			List<Map<String, Object>> result, int level, boolean addTotal) {
		records.forEach(record -> {
			if (addTotal || !record.containsKey(Constants.CHILDREN)) {
				record.put("text" + level, record.get("text"));
				record.put("value" + level, record.get("value"));
				parentInfo.forEach(p -> {
					record.putAll(p);
				});
				record.remove(Constants.ROWID);
				record.remove("leaf");
				record.remove(Constants.CONDITION);
				record.remove(Constants.MODULE_NAME);
				Map<String, Object> addResult = new HashMap<String, Object>(0);
				addResult.putAll(record);
				addResult.remove(Constants.CHILDREN);
				result.add(addResult);
			}
			if (record.containsKey(Constants.CHILDREN)) {
				List<Map<String, Object>> parent = new ArrayList<Map<String, Object>>();
				parent.addAll(parentInfo);
				Map<String, Object> thisMap = new HashMap<String, Object>(1);
				thisMap.put("text" + level, record.get("text"));
				thisMap.put("value" + level, record.get("value"));
				parent.add(thisMap);
				addLeafToResult((List<Map<String, Object>>) record.get(Constants.CHILDREN), parent, result, level + 1,
						addTotal);
			}
		});
	}

	/**
	 * 注意，有sqlparam 的，并没有加入条件进去，现在也没有保存sqlparam在查询方案中
	 * 
	 * @param dataminingschemeid
	 * @param treemodel
	 * @param additionviewschemeid
	 * @param additionuserfilters
	 * @param schemetitle          // 可以根据 title 来查找
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<?> fetchDataminingData(String dataminingschemeid, boolean treemodel, String additionviewschemeid,
			String additionuserfilters, String schemetitle, boolean isnumberordername) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		FDataminingscheme scheme = null;
		if (StringUtils.isNotBlank(dataminingschemeid)) {
			scheme = dao.findById(FDataminingscheme.class, dataminingschemeid);
		}
		if (scheme == null && StringUtils.isNotBlank(schemetitle)) {
			scheme = dao.findByPropertyFirst(FDataminingscheme.class, Constants.TITLE, schemetitle);
		}
		if (scheme == null) {
			return result;
		}
		if (!ObjectFunctionUtils.allowQuery(scheme.getFDataobject())) {
			return result;
		}
		List<FDataminingcolumngroup> columnGroups = new ArrayList<FDataminingcolumngroup>();
		getAllLeafColumnGroup(scheme.getFDataminingcolumngroups(), columnGroups);
		List<String> columnArray = new ArrayList<String>();
		for (FDataminingcolumngroup group : columnGroups) {
			columnArray.add(group.getGroupcondition());
		}
		List<FDataminingselectfield> selectfields = new ArrayList<FDataminingselectfield>();
		getAllLeafSelectField(scheme.getFDataminingselectfields(), selectfields);
		List<String> selectfieldArray = new ArrayList<String>();
		for (FDataminingselectfield field : selectfields) {
			selectfieldArray.add(field._getAggregateFieldame());
		}
		String filterStr = null;
		for (FDataminingfilter filter : scheme.getFDataminingfilters()) {
			filterStr = filter.getOthersetting();
			break;
		}
		List<UserDefineFilter> navigatefilters = new ArrayList<UserDefineFilter>();
		List<UserDefineFilter> userfilters = new ArrayList<UserDefineFilter>();
		if (StringUtils.isNotBlank(additionuserfilters)) {
			JSONArray filterArray = JSONArray.parseArray(additionuserfilters);
			for (int i = 0; i < filterArray.size(); i++) {
				userfilters.add(filterArray.getJSONObject(i).toJavaObject(UserDefineFilter.class));
			}
		}
		String viewschemeid = additionviewschemeid;
		if (StringUtils.isNotEmpty(filterStr)) {
			JSONArray filterArray = JSONArray.parseArray(filterStr);
			for (int i = 0; i < filterArray.size(); i++) {
				JSONObject object = filterArray.getJSONObject(i);
				JSONObject originFitler = object.getJSONObject("originfilter");
				if ("navigatefilter".equals(object.getString("conditiontype"))) {
					navigatefilters.add(originFitler.toJavaObject(UserDefineFilter.class));
				} else if ("userfilter".equals(object.getString("conditiontype"))) {
					userfilters.add(originFitler.toJavaObject(UserDefineFilter.class));
				} else {
					// 如果指定了视图方案，那么datamining中的视图方案就失效
					if (StringUtils.isBlank(viewschemeid)) {
						viewschemeid = originFitler.getString(Constants.VIEWSCHEMEID);
					}
				}
			}
		}
		List<?> rootresult = dataminingService.fetchData(scheme.getFDataobject().getObjectname(), columnArray,
				selectfieldArray, null, null, userfilters, viewschemeid, navigatefilters, false, null,
				isnumberordername);
		DefaultMutableTreeNode root = null;
		if (rootresult.size() > 0) {
			root = new DefaultMutableTreeNode(rootresult.get(0));
		} else {
			return result;
		}
		if (scheme.isRowexpandpath()) {
			expandAllRowPath(scheme, root, columnArray, selectfieldArray, navigatefilters, viewschemeid, userfilters,
					isnumberordername);
		} else {
			Set<FDataminingrowgroup> group = scheme.getFDataminingrowgroups();
			if (group.size() > 0) {
				FDataminingrowgroup rootgroup = (FDataminingrowgroup) group.toArray()[0];
				((Map<String, Object>) ((DefaultMutableTreeNode) root).getUserObject()).put(Constants.TEXT,
						StringUtils.isNotEmpty(rootgroup.getOrgintitle()) ? rootgroup.getOrgintitle()
								: rootgroup.getTitle());
				expandRow(root, rootgroup.getFDataminingrowgroups(), scheme.getFDataobject(), columnArray,
						selectfieldArray, navigatefilters, viewschemeid, userfilters, false, isnumberordername);
			}
		}
		Map<String, Object> treeData = (Map<String, Object>) root.getUserObject();
		result.add(treeData);
		if (treemodel) {
			generateTreeChildrenData(treeData, root);
		} else {
			generateListChildrenData(treeData, root, result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void expandAllRowPath(FDataminingscheme scheme, DefaultMutableTreeNode root, List<String> columnArray,
			List<String> selectfieldArray, List<UserDefineFilter> navigatefilters, String viewschemeid,
			List<UserDefineFilter> userfilters, boolean isnumberordername) {
		Set<FDataminingrowgrouppath> rowGroupPaths = scheme.getFDataminingrowgrouppaths();
		for (FDataminingrowgrouppath rowpath : rowGroupPaths) {
			String rowid = rowpath.getConditionpath();
			if ("expand".equals(rowpath.getPathtype())) {
				DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, rowid);
				if (targetnode != null) {
					expandPath(targetnode, scheme.getFDataobject(), rowpath, root, columnArray, selectfieldArray,
							navigatefilters, viewschemeid, userfilters, isnumberordername);
				}
			} else if ("expandwithnavigaterecords".equals(rowpath.getPathtype())) {
				DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, rowid);
				if (targetnode != null) {
					expandWithNavigateRecords(targetnode, scheme.getFDataobject(), rowpath, root, columnArray,
							selectfieldArray, navigatefilters, viewschemeid, userfilters, isnumberordername);
				}
			} else if ("expandallleaf".equals(rowpath.getPathtype())) {
				List<DefaultMutableTreeNode> leafnodes = getAllLeafNode(root, null);
				for (DefaultMutableTreeNode node : leafnodes) {
					expandPath(node, scheme.getFDataobject(), rowpath, root, columnArray, selectfieldArray,
							navigatefilters, viewschemeid, userfilters, isnumberordername);
				}
			} else if ("edittext".equals(rowpath.getPathtype())) {
				DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, rowid);
				if (targetnode != null) {
					((Map<String, Object>) ((DefaultMutableTreeNode) targetnode).getUserObject())
							.put(Constants.TEXT, rowpath.getTitle());
				}
			} else if ("deleterow".equals(rowpath.getPathtype())) {
				DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, rowid);
				if (targetnode != null) {
					targetnode.removeFromParent();
				}
			} else if ("deletechildren".equals(rowpath.getPathtype())) {
				DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, rowid);
				if (targetnode != null) {
					targetnode.removeAllChildren();
				}
			} else if ("combinerows".equals(rowpath.getPathtype())) {
				String[] rowids = rowpath.getConditionpath().split(Constants.COMMA);
				List<DefaultMutableTreeNode> selected = new ArrayList<DefaultMutableTreeNode>();
				DefaultMutableTreeNode firstnode = null;
				for (String s : rowids) {
					DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, s);
					if (targetnode != null) {
						if (firstnode == null) {
							firstnode = targetnode;
						}
						selected.add(targetnode);
					}
				}
				if (firstnode != null) {
					TreeNode[] paths = firstnode.getPath();
					List<String> parentConditions = new ArrayList<String>();
					for (int i = 1; i < paths.length - 1; i++) {
						parentConditions
								.add(((Map<String, Object>) ((DefaultMutableTreeNode) paths[i]).getUserObject())
										.get(Constants.CONDITION).toString());
					}
					parentConditions.add(rowpath.getGroupcondition());
					List<?> list = dataminingService.fetchData(scheme.getFDataobject().getObjectname(), columnArray,
							selectfieldArray, null, parentConditions, userfilters, viewschemeid, navigatefilters,
							false, null, isnumberordername);
					if (list.size() > 0) {
						Map<String, Object> combinemap = (Map<String, Object>) list.get(0);
						combinemap.put(Constants.TEXT, rowpath.getTitle());
						combinemap.put(Constants.CONDITION, rowpath.getGroupcondition());
						DefaultMutableTreeNode combinenode = new DefaultMutableTreeNode(combinemap);
						((DefaultMutableTreeNode) firstnode.getParent()).insert(combinenode,
								firstnode.getParent().getIndex(firstnode));
						if (BooleanUtils.isTrue(rowpath.getAddselectedchildrens())) {
							for (DefaultMutableTreeNode s : selected) {
								combinenode.add(s);
							}
						} else
							for (DefaultMutableTreeNode s : selected) {
								s.removeFromParent();
							}
					}
				}
			} else if ("combineotherrows".equals(rowpath.getPathtype())) {
				String[] rowids = rowpath.getConditionpath().split(Constants.COMMA);
				List<DefaultMutableTreeNode> selected = new ArrayList<DefaultMutableTreeNode>();
				DefaultMutableTreeNode firstnode = null;
				for (String s : rowids) {
					DefaultMutableTreeNode targetnode = findTreeNodeByRowid(root, s);
					if (targetnode != null) {
						if (firstnode == null) {
							firstnode = targetnode;
						}
						selected.add(targetnode);
					}
				}
				if (firstnode != null) {
					List<DefaultMutableTreeNode> notselected = new ArrayList<DefaultMutableTreeNode>();
					Enumeration<DefaultMutableTreeNode> children = (Enumeration<DefaultMutableTreeNode>) firstnode
							.getParent().children();
					while (children.hasMoreElements()) {
						DefaultMutableTreeNode child = children.nextElement();
						if (selected.indexOf(child) == -1) {
							notselected.add(child);
						}
					}
					TreeNode[] paths = firstnode.getPath();
					List<String> parentConditions = new ArrayList<String>();
					for (int i = 1; i < paths.length - 1; i++) {
						parentConditions
								.add(((Map<String, Object>) ((DefaultMutableTreeNode) paths[i]).getUserObject())
										.get(Constants.CONDITION).toString());
					}
					parentConditions.add(rowpath.getGroupcondition());
					List<?> list = dataminingService.fetchData(scheme.getFDataobject().getObjectname(), columnArray,
							selectfieldArray, null, parentConditions, userfilters, viewschemeid, navigatefilters,
							false, null, isnumberordername);
					if (list.size() > 0) {
						Map<String, Object> combinemap = (Map<String, Object>) list.get(0);
						combinemap.put(Constants.TEXT, "其他");
						combinemap.put(Constants.CONDITION, rowpath.getGroupcondition());
						DefaultMutableTreeNode combinenode = new DefaultMutableTreeNode(combinemap);
						((DefaultMutableTreeNode) firstnode.getParent()).add(combinenode);
						if (BooleanUtils.isTrue(rowpath.getAddselectedchildrens())) {
							for (DefaultMutableTreeNode s : notselected) {
								combinenode.add(s);
							}
						} else
							for (DefaultMutableTreeNode s : notselected) {
								s.removeFromParent();
							}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void generateListChildrenData(Map<String, Object> parent, DefaultMutableTreeNode parentnode,
			List<Map<String, Object>> result) {
		if (parentnode.getChildCount() > 0) {
			Enumeration<TreeNode> enumeration = parentnode.children();
			while (enumeration.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
				Map<String, Object> nodemap = (Map<String, Object>) node.getUserObject();
				nodemap.remove(Constants.CHILDREN);
				nodemap.put(LEAF, true);
				nodemap.remove(Constants.CONDITION);
				nodemap.remove(VALUE);
				result.add(nodemap);
				generateListChildrenData(nodemap, node, result);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void generateTreeChildrenData(Map<String, Object> parent, DefaultMutableTreeNode parentnode) {
		if (parentnode.getChildCount() > 0) {
			Enumeration<TreeNode> enumeration = parentnode.children();
			Map<String, Object> pnodemap = (Map<String, Object>) parentnode.getUserObject();
			pnodemap.put("leaf", false);
			while (enumeration.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
				Map<String, Object> nodemap = (Map<String, Object>) node.getUserObject();
				nodemap.remove(Constants.CHILDREN);
				if (!parent.containsKey(Constants.CHILDREN)) {
					parent.put(Constants.CHILDREN, new ArrayList<Map<String, Object>>());
				}
				((ArrayList<Map<String, Object>>) parent.get(Constants.CHILDREN)).add(nodemap);
				generateTreeChildrenData(nodemap, node);
			}
		}
	}

	/**
	 * 根据行展开，这是一个树形结构的表，先计算上层的，然后再递归计算下层的。
	 * 
	 * @param targetnode
	 * @param object
	 * @param rowpath
	 * @param root
	 * @param columnArray
	 * @param selectfieldArray
	 * @param navigatefilters
	 * @param viewschemeid
	 * @param userfilters
	 * @param alreadyZero      是否无数据了,如果上级为zero, 那么下级也不用查了，肯定无数据
	 */
	@SuppressWarnings("unchecked")
	private void expandRow(DefaultMutableTreeNode parent, Set<FDataminingrowgroup> rowgroups, FDataobject object,
			List<String> columnArray, List<String> selectfieldArray, List<UserDefineFilter> navigatefilters,
			String viewschemeid, List<UserDefineFilter> userfilters, boolean alreadyZero, boolean isnumberordername) {

		TreeNode[] paths = parent.getPath();
		List<String> parentConditions = new ArrayList<String>();
		for (int i = 1; i < paths.length; i++) {
			parentConditions.add(((Map<String, Object>) ((DefaultMutableTreeNode) paths[i]).getUserObject())
					.get(Constants.CONDITION).toString());
		}
		for (FDataminingrowgroup group : rowgroups) {
			List<String> thisparentConditions = new ArrayList<String>();
			thisparentConditions.addAll(parentConditions);
			thisparentConditions.add(group.getGroupcondition());
			List<?> list = dataminingService.fetchData(object.getObjectname(), columnArray, selectfieldArray, null,
					thisparentConditions, userfilters, viewschemeid, navigatefilters, false, null, isnumberordername);
			DefaultMutableTreeNode node;
			if (list.size() > 0) {
				node = new DefaultMutableTreeNode(list.get(0));
			} else {
				node = new DefaultMutableTreeNode(new HashMap<String, Object>(0));
				((Map<String, Object>) ((DefaultMutableTreeNode) node).getUserObject()).put(Constants.ROWID,
						MD5.MD5Encode(StringUtils.join(thisparentConditions.toArray(), "|||")));
			}
			((Map<String, Object>) ((DefaultMutableTreeNode) node).getUserObject()).put(Constants.TEXT,
					StringUtils.isNotEmpty(group.getOrgintitle()) ? group.getOrgintitle() : group.getTitle());
			((Map<String, Object>) ((DefaultMutableTreeNode) node).getUserObject()).put(Constants.CONDITION,
					group.getGroupcondition());
			parent.add(node);
			if (group.getFDataminingrowgroups().size() > 0) {
				expandRow(node, group.getFDataminingrowgroups(), object, columnArray, selectfieldArray, navigatefilters,
						viewschemeid, userfilters, false, isnumberordername);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void expandWithNavigateRecords(DefaultMutableTreeNode targetnode, FDataobject object,
			FDataminingrowgrouppath rowpath, DefaultMutableTreeNode root, List<String> columnArray,
			List<String> selectfieldArray, List<UserDefineFilter> navigatefilters, String viewschemeid,
			List<UserDefineFilter> userfilters, boolean isnumberordername) {
		TreeNode[] paths = targetnode.getPath();
		List<String> parentConditions = new ArrayList<String>();
		for (int i = 1; i < paths.length; i++) {
			parentConditions.add(((Map<String, Object>) ((DefaultMutableTreeNode) paths[i]).getUserObject())
					.get(Constants.CONDITION).toString());
		}
		String groupfieldid = rowpath._getFieldidahead();
		String groupfieldbehind = "";
		if (StringUtils.isNotEmpty(rowpath.getFieldgrouptype())) {
			groupfieldbehind = "-" + rowpath.getFieldgrouptype();
		}
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty_(groupfieldid + groupfieldbehind);
		filter.setOperator(Constants.IN);
		JSONArray records = JSONArray.parseArray(rowpath.getConditionpaths());
		String[] values = new String[records.size()];
		for (int i = 0; i < records.size(); i++) {
			values[i] = records.getJSONObject(i).getString("value");
		}
		filter.setValue(String.join(Constants.COMMA, values));
		List<UserDefineFilter> thisuserfilters = new ArrayList<UserDefineFilter>();
		thisuserfilters.addAll(userfilters);
		thisuserfilters.add(filter);
		List<?> list = null;
		list = dataminingService.fetchData(object.getObjectname(), columnArray, selectfieldArray,
				groupfieldid + groupfieldbehind, parentConditions, thisuserfilters, viewschemeid, navigatefilters,
				false, null, isnumberordername);
		addChildrenToTreeNode(targetnode, list, groupfieldid, groupfieldbehind);
	}

	private static final String ALL = "-all";

	@SuppressWarnings("unchecked")
	private void expandPath(DefaultMutableTreeNode targetnode, FDataobject object, FDataminingrowgrouppath rowpath,
			DefaultMutableTreeNode root, List<String> columnArray, List<String> selectfieldArray,
			List<UserDefineFilter> navigatefilters, String viewschemeid, List<UserDefineFilter> userfilters,
			boolean isnumberordername) {
		TreeNode[] paths = targetnode.getPath();
		List<String> parentConditions = new ArrayList<String>();
		for (int i = 1; i < paths.length; i++) {
			parentConditions.add(((Map<String, Object>) ((DefaultMutableTreeNode) paths[i]).getUserObject())
					.get(Constants.CONDITION).toString());
		}
		String groupfieldid = rowpath._getFieldidahead();
		String groupfieldbehind = "";
		if (StringUtils.isNotEmpty(rowpath.getFieldgrouptype())) {
			groupfieldbehind = "-" + rowpath.getFieldgrouptype();
		}
		List<?> list = null;
		if (ALL.equals(groupfieldbehind)) {
			list = dataminingService.fetchLevelAllData(object.getObjectname(), columnArray, selectfieldArray,
					groupfieldid, parentConditions, navigatefilters, viewschemeid, userfilters, false, null,
					isnumberordername);
		} else {
			list = dataminingService.fetchData(object.getObjectname(), columnArray, selectfieldArray,
					groupfieldid + groupfieldbehind, parentConditions, userfilters, viewschemeid, navigatefilters,
					false, null, isnumberordername);
		}
		addChildrenToTreeNode(targetnode, list, groupfieldid, groupfieldbehind);
	}

	@SuppressWarnings("unchecked")
	private void addChildrenToTreeNode(DefaultMutableTreeNode parentnode, List<?> list, String groupfieldid,
			String groupfieldbehind) {
		for (Object object : list) {
			Map<String, Object> map = (Map<String, Object>) object;
			String behind = map.get("level_") != null ? "-" + map.get("level_").toString() : null;
			if (behind == null) {
				// 如果不是分级的，判断一下有无函数，有的话，会放在groupfieldbehind里带过来
				if (StringUtils.isNotBlank(groupfieldbehind)) {
					behind = groupfieldbehind;
				}
			}
			map.put(Constants.CONDITION, groupfieldid + (behind == null ? "" : behind) + "=" + map.get("value"));
			DefaultMutableTreeNode thisnode = new DefaultMutableTreeNode(object);
			parentnode.add(thisnode);
			if (map.containsKey(Constants.CHILDREN)) {
				addChildrenToTreeNode(thisnode, (List<?>) map.get(Constants.CHILDREN), groupfieldid, groupfieldbehind);
			}
		}
	}

	private List<DefaultMutableTreeNode> getAllLeafNode(DefaultMutableTreeNode treenode,
			List<DefaultMutableTreeNode> result) {
		if (result == null) {
			result = new ArrayList<DefaultMutableTreeNode>();
		}
		if (treenode.getChildCount() > 0) {
			Enumeration<?> enumeration = treenode.children();
			while (enumeration.hasMoreElements()) {
				getAllLeafNode((DefaultMutableTreeNode) enumeration.nextElement(), result);
			}
		} else {
			result.add(treenode);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private DefaultMutableTreeNode findTreeNodeByRowid(DefaultMutableTreeNode treenode, String rowid) {
		Map<String, Object> map = (Map<String, Object>) treenode.getUserObject();
		if (map.get(Constants.ROWID).equals(rowid))
			return treenode;
		else {
			if (treenode.getChildCount() > 0) {
				Enumeration<TreeNode> enumeration = treenode.children();
				while (enumeration.hasMoreElements()) {
					DefaultMutableTreeNode rowidnode = findTreeNodeByRowid(
							(DefaultMutableTreeNode) enumeration.nextElement(), rowid);
					if (rowidnode != null) {
						return rowidnode;
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void printTree(DefaultMutableTreeNode treenode) {
		for (int i = 0; i < treenode.getLevel(); i++) {
			System.out.print("--" + treenode.getLevel());
		}
		System.out.println(((Map<String, Object>) treenode.getUserObject()).get(Constants.TEXT) + "--"
				+ ((Map<String, Object>) treenode.getUserObject()).get(Constants.ROWID) + "--"
				+ ((Map<String, Object>) treenode.getUserObject()).get(Constants.CONDITION) + "--" + treenode);
		if (treenode.getChildCount() > 0) {
			Enumeration<TreeNode> enumeration = treenode.children();
			while (enumeration.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
				printTree(node);
			}
		}
	}

	private void getAllLeafColumnGroup(Set<FDataminingcolumngroup> group, List<FDataminingcolumngroup> result) {
		for (FDataminingcolumngroup columngroup : group) {
			if (columngroup.getFDataminingcolumngroups().size() > 0) {
				getAllLeafColumnGroup(columngroup.getFDataminingcolumngroups(), result);
			} else {
				result.add(columngroup);
			}
		}
	}

	private void getAllLeafSelectField(Set<FDataminingselectfield> group, List<FDataminingselectfield> result) {
		for (FDataminingselectfield columngroup : group) {
			if (columngroup.getFDataminingselectfields().size() > 0) {
				getAllLeafSelectField(columngroup.getFDataminingselectfields(), result);
			} else {
				result.add(columngroup);
			}
		}
	}

}
