package com.jhopesoft.framework.core.objectquery.navigate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.TreeValueText;
import com.jhopesoft.framework.core.objectquery.dao.ModuleDataDAO;
import com.jhopesoft.framework.core.objectquery.dao.TreeModuleDataDAO;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.ColumnField;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridnavigatescheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridnavigateschemedetail;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Repository
public class NavigateGenerateService {

	@Resource
	private ModuleDataDAO moduleDataDAO;

	@Resource
	private TreeModuleDataDAO treeModuleDataDAO;

	public JSONObject genNavigateTree(String moduleName, String navigateschemeId, String parentFilter,
			Boolean cascading, Boolean isContainNullRecord, JSONObject sqlparam) {

		isContainNullRecord = isContainNullRecord == null ? false : isContainNullRecord;
		FDataobject module = DataObjectUtils.getDataObject(moduleName);

		List<FovGridnavigateschemedetail> navigateDetails = new ArrayList<FovGridnavigateschemedetail>();

		FovGridnavigatescheme navigateScheme = Local.getDao().findById(FovGridnavigatescheme.class, navigateschemeId);

		if (navigateScheme != null) {
			navigateDetails.addAll(navigateScheme.getDetails());
		} else {
			FovGridnavigateschemedetail fieldDetail = new FovGridnavigateschemedetail();
			if (navigateschemeId.indexOf(Constants.DOT) > 0) {
				String[] ahead = navigateschemeId.split("\\.");
				FDataobject pmodule = module;
				for (int i = 0; i < ahead.length - 1; i++) {
					FDataobjectfield field = pmodule._getModuleFieldByFieldName(ahead[i]);
					pmodule = DataObjectUtils.getDataObject(field.getFieldtype());
				}
				String pfieldname = ahead[ahead.length - 1];
				FDataobjectfield mf = pmodule._getModuleFieldByFieldName(pfieldname);
				fieldDetail.setTitle(mf.getFieldtitle());
				fieldDetail.setFDataobjectfield(mf);
				fieldDetail.setOrderno(1);
				fieldDetail.setFieldahead(navigateschemeId.substring(0, navigateschemeId.lastIndexOf('.')));
				FDataobject m = DataObjectUtils.getDataObject(mf.getFieldtype());
				if (m != null && m._isCodeLevel()) {
					fieldDetail.setAddcodelevel(true);
				}
				navigateDetails.add(fieldDetail);
			} else {
				FDataobjectfield mf = module._getModuleFieldByFieldName(navigateschemeId);
				fieldDetail.setTitle(mf.getFieldtitle());
				fieldDetail.setFDataobjectfield(mf);
				fieldDetail.setOrderno(1);
				FDataobject m = DataObjectUtils.getDataObject(mf.getFieldtype());
				if (m != null && m._isCodeLevel()) {
					fieldDetail.setAddcodelevel(true);
				}
				navigateDetails.add(fieldDetail);
			}
		}
		List<UserParentFilter> userParentFilters = UserParentFilter.changeToParentFilters(parentFilter, moduleName);
		SqlGenerate sqlgenerate = new SqlGenerate(module, true);
		sqlgenerate.setAddIdField(true);
		sqlgenerate.setAddNameField(false);
		sqlgenerate.setAddBaseField(false);
		sqlgenerate.setAddAllFormScheme(false);
		sqlgenerate.setAddAllGridScheme(false);
		sqlgenerate.setDataobjectview(null);
		sqlgenerate.setUserParentFilters(userParentFilters);
		sqlgenerate.setSqlparam(sqlparam);
		JSONObject result = new JSONObject();
		result.put("expanded", true);
		JSONArray children = new JSONArray();
		if (navigateDetails.size() == 0) {
			return result;
		}
		if (cascading != null && cascading) {
			children.add(genAllLevel(sqlgenerate, isContainNullRecord, navigateDetails));
		} else
			for (FovGridnavigateschemedetail schemeDetail : navigateDetails) {
				List<FovGridnavigateschemedetail> sds = new ArrayList<FovGridnavigateschemedetail>();
				sds.add(schemeDetail);
				children.add(genAllLevel(sqlgenerate, isContainNullRecord, sds));
			}

		if (navigateDetails.size() > 1 && cascading) {
			JSONArray rootchildren = null;
			try {
				rootchildren = children.getJSONObject(0).getJSONArray(Constants.CHILDREN);
			} catch (Exception e) {

			}
			if (rootchildren != null && rootchildren.size() == 1) {
				children = rootchildren;
				children.getJSONObject(0).remove("fieldvalue");
				children.getJSONObject(0).remove(Constants.MODULE_NAME);
			}
		}
		result.put(Constants.CHILDREN, children);
		return result;

	}

	private JSONObject genAllLevel(SqlGenerate sqlgenerate, Boolean isContainNullRecord,
			List<FovGridnavigateschemedetail> navigateSchemeDetails) {
		FovGridnavigateschemedetail detail1 = navigateSchemeDetails.get(0);
		JSONArray datas = null;
		boolean cond = isContainNullRecord && navigateSchemeDetails.size() == 1
				&& !(detail1.getFieldahead() == null && detail1.getFDataobjectfield()._isBaseField());
		if (cond) {
			String[] paths = detail1._getFactAheadPath().split("\\.");
			FDataobject module = sqlgenerate.getBaseModule().getModule();
			FDataobject pm = sqlgenerate.getBaseModule().getModule();
			for (String path : paths) {
				pm = DataObjectUtils.getDataObject(pm._getModuleFieldByFieldName(path).getFieldtype());
			}
			String ahead = module.getObjectname() + Constants.DOTWITHDOT + detail1._getFactAheadPath();
			Set<ColumnField> cFields = new LinkedHashSet<ColumnField>();
			ColumnField aField = new ColumnField();
			aField.setRemark("count_");
			aField.setAggregate(Constants.COUNT);
			aField.setFDataobjectfield(module._getPrimaryKeyField());
			aField.setFieldahead(ahead);
			cFields.add(aField);
			SqlGenerate sqlgenerate1 = new SqlGenerate(pm);

			sqlgenerate1.setAddIdField(false);
			sqlgenerate1.setAddNameField(false);
			sqlgenerate1.setAddBaseField(false);
			sqlgenerate1.setAddAllFormScheme(false);
			sqlgenerate1.setAddAllGridScheme(false);
			sqlgenerate1.setDataobjectview(null);
			sqlgenerate1.setColumnFields(cFields);

			NavigateSQLGenerate sqlGenerate = new NavigateSQLGenerate(sqlgenerate1, navigateSchemeDetails);
			datas = moduleDataDAO.getData(sqlGenerate.generageNavigateSqlWithAllParentField(),
					sqlGenerate.getFields().keySet().toArray(new String[] {}), -1, -1, module);

		} else {
			NavigateSQLGenerate sqlGenerate = new NavigateSQLGenerate(sqlgenerate, navigateSchemeDetails);
			datas = moduleDataDAO.getData(sqlGenerate.generageNavigateSql(),
					sqlGenerate.getFields().keySet().toArray(new String[] {}), -1, -1,
					sqlgenerate.getBaseModule().getModule());
		}
		List<NavigateData> navigateDatas = new ArrayList<NavigateData>();
		for (int i = 0; i < datas.size(); i++) {
			NavigateData data = new NavigateData(datas.getJSONObject(i), 1);
			navigateDatas.add(data);
		}
		navigateDatas = genNavigateTree(navigateDatas, 1, navigateSchemeDetails.size(), navigateSchemeDetails,
				isContainNullRecord);
		int allcount = 0;
		JSONArray firstlevels = new JSONArray();
		for (NavigateData data : navigateDatas) {
			allcount += data.getCount();
			firstlevels.add(data.genJsonObject(sqlgenerate, navigateSchemeDetails));
		}

		JSONObject result = new JSONObject();
		result.put(Constants.TEXT, navigateSchemeDetails.get(0).getTitle());
		result.put("iconCls", navigateSchemeDetails.get(0).getIconcls());
		result.put("leaf", false);
		result.put("expanded", true);
		result.put(Constants.COUNT, allcount);
		result.put(Constants.CHILDREN, firstlevels);
		return result;
	}

	/**
	 * 根据传进来的数组，生成一个合并组数的数据，再返回，直到最后一级
	 * 
	 * @param navigateDatas
	 * @return
	 */
	private List<NavigateData> genNavigateTree(List<NavigateData> navigateDatas, int level, int maxlevel,
			List<FovGridnavigateschemedetail> schemeDetails, Boolean isContainNullRecord) {
		List<NavigateData> result = new ArrayList<NavigateData>();
		if (level != maxlevel) {
			NavigateData nowNavigate = null;
			for (NavigateData navigateData : navigateDatas) {
				if (navigateData.equals(nowNavigate)) {
					NavigateData child = new NavigateData(navigateData.getJsonObject(), level + 1);
					nowNavigate.getChildren().add(child);
					nowNavigate.setCount(nowNavigate.getCount() + child.getCount());
				} else {
					nowNavigate = navigateData;
					result.add(nowNavigate);
					nowNavigate.setChildren(new ArrayList<NavigateData>());
					nowNavigate.getChildren().add(new NavigateData(navigateData.getJsonObject(), level + 1));
				}
			}
			for (NavigateData navigateData : result) {
				navigateData.setChildren(genNavigateTree(navigateData.getChildren(), level + 1, maxlevel, schemeDetails,
						isContainNullRecord));
			}
		} else {
			result = navigateDatas;
		}
		FovGridnavigateschemedetail schemeDetail = schemeDetails.get(level - 1);
		if (schemeDetail.getFDataobjectfield()._isManyToOne() || schemeDetail.getFDataobjectfield()._isOneToOne()) {
			if (schemeDetail.getAddcodelevel()) {
				List<TreeValueText> valueTexts = treeModuleDataDAO.getRecordWithTreeData(
						schemeDetail.getFDataobjectfield().getFieldtype(), true, false, null, null, false);
				List<NavigateData> treeNavigateData = fromTreeValueToNavigateData(valueTexts, result, level,
						isContainNullRecord);
				return treeNavigateData;
			}
		}
		return result;
	}

	public List<NavigateData> fromTreeValueToNavigateData(List<TreeValueText> valueTexts,
			List<NavigateData> navigateDatas, int level, Boolean isContainNullRecord) {
		List<NavigateData> result = new ArrayList<NavigateData>();
		for (TreeValueText treeValue : valueTexts) {
			NavigateData navigateData = null;
			for (NavigateData navData : navigateDatas) {
				if (navData.getKey() == null) {
					if (treeValue.getValue() == null) {
						navigateData = navData;
						break;
					}
				} else if (navData.getKey().equals(treeValue.getValue())) {
					navigateData = navData;
					break;
				}
			}
			if (navigateData == null) {
				navigateData = new NavigateData();
				navigateData.setKey(treeValue.getValue());
				navigateData.setLevel(level);
				navigateData.setCount(0);
				navigateData.setName(treeValue.getText());
				navigateData.setOperator("=");
			}
			if (treeValue.hasChildren()) {
				List<NavigateData> children = fromTreeValueToNavigateData(treeValue.getChildren(), navigateDatas, level,
						isContainNullRecord);
				if (navigateData.getChildren() != null) {
					navigateData.getChildren().addAll(children);
				} else {
					navigateData.setChildren(children);
				}
				for (NavigateData na : navigateData.getChildren()) {
					{
						navigateData.setCount(navigateData.getCount() + na.getCount());
					}
				}
				if (treeValue.getParenttype() != null
						&& treeValue.getParenttype() == TreeModuleDataDAO.PARENTWITHPARENTID) {
					navigateData.setOperator(Constants.ALLCHILDREN);
				} else {
					navigateData.setOperator(Constants.STARTWITH);
				}
			}
			if (isContainNullRecord || navigateData.getCount() > 0) {
				result.add(navigateData);
			}
		}
		return result;
	}

}
