package com.jhopesoft.platform.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.limit.FDatafilterrole;
import com.jhopesoft.framework.dao.entity.limit.FDatafilterrolelimit;
import com.jhopesoft.framework.dao.entity.utils.FFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ParentChildFieldUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataFilterRoleService {

	@Resource
	private DaoImpl dao;

	public JSONObject getdataFilterRoleLimit(String roleId) {
		JSONArray array = new JSONArray();
		FDatafilterrole role = dao.findById(FDatafilterrole.class, roleId);
		for (FDatafilterrolelimit detail : role.getLimits()) {
			array.add(detail.genJsonObject(role.getFDataobject()));
		}
		JSONObject result = new JSONObject();
		result.put("leaf", false);
		result.put(Constants.CHILDREN, array);
		return result;
	}

	public ActionResult updateRoleLimitDetails(String roleid, String schemeDefine) {
		FDatafilterrole datafilterrole = dao.findById(FDatafilterrole.class, roleid);
		// 删除所有的条件设置，由于有缓存，是这样删除的。
		Iterator<FDatafilterrolelimit> it = datafilterrole.getLimits().iterator();
		while (it.hasNext()) {
			FDatafilterrolelimit pd = it.next();
			it.remove();
			pd.setFDatafilterrole(null);
			dao.delete(pd);
		}
		JSONObject object = JSONObject.parseObject("{ children :" + schemeDefine + "}");
		JSONArray arrays = (JSONArray) object.get(Constants.CHILDREN);
		saveNewDetails(datafilterrole, arrays, null);

		ActionResult result = new ActionResult();
		return result;
	}

	private void saveNewDetails(FDatafilterrole datafilterrole, JSONArray arrays, FDatafilterrolelimit p) {
		for (int i = 0; i < arrays.size(); i++) {
			JSONObject detailObject = arrays.getJSONObject(i);
			FDatafilterrolelimit detail = new FDatafilterrolelimit();
			detail.setFDatafilterrole(datafilterrole);
			detail.setFDatafilterrolelimit(p);
			if (detailObject.containsKey(Constants.TITLE)) {
				detail.setTitle(detailObject.getString(Constants.TITLE));
			}
			if (detailObject.containsKey(Constants.OPERATOR)) {
				detail.setOperator(detailObject.getString(Constants.OPERATOR));
			}
			if (detailObject.containsKey(Constants.OVALUE)) {
				detail.setOvalue(detailObject.getString(Constants.OVALUE));
			}
			if (detailObject.containsKey(Constants.ISTREERECORD)) {
				detail.setIstreerecord(detailObject.getBoolean(Constants.ISTREERECORD));
			}
			if (detailObject.containsKey(Constants.RECORDIDS)) {
				detail.setRecordids(detailObject.getString(Constants.RECORDIDS));
			}
			if (detailObject.containsKey(Constants.RECORDNAMES)) {
				detail.setRecordnames(detailObject.getString(Constants.RECORDNAMES));
			}
			if (detailObject.containsKey(Constants.USERFUNCTION)) {
				detail.setUserfunction(detailObject.getString(Constants.USERFUNCTION));
			}
			if (detailObject.containsKey(Constants.REMARK)) {
				detail.setRemark(detailObject.getString(Constants.REMARK));
			}
			if (detailObject.containsKey(Constants.FUNCTIONID)) {
				detail.setFFunction(new FFunction(detailObject.getString(Constants.FUNCTIONID)));
			}
			detail.setOrderno((i + 1) * 10);
			if (detailObject.containsKey(Constants.FIELDID)) {
				ParentChildFieldUtils.updateToField(detail, detailObject.getString(Constants.FIELDID));
			}
			dao.save(detail);
			if (datafilterrole != null) {
				datafilterrole.getLimits().add(detail);
			}
			if (p != null) {
				p.getLimits().add(detail);
			}
			if (detailObject.containsKey(Constants.CHILDREN)) {
				saveNewDetails(null, (JSONArray) detailObject.get(Constants.CHILDREN), detail);
			}
		}
	}

	public ActionResult saveasScheme(String roleId) {
		FDatafilterrole role = dao.findById(FDatafilterrole.class, roleId);
		FDatafilterrole saveas = new FDatafilterrole();
		saveas.setFDataobject(role.getFDataobject());
		saveas.setRolename(role.getRolename() + "--副本");
		saveas.setCreatedate(new Timestamp(System.currentTimeMillis()));
		saveas.setCreater(Local.getUserid());
		saveas.setOrderno(role.getOrderno() + 10);
		dao.save(saveas);

		for (FDatafilterrolelimit c : role.getLimits()) {
			FDatafilterrolelimit newcolumn = new FDatafilterrolelimit(saveas,
					c.getFDataobjectconditionBySubconditionid(), c.getFDatafilterrolelimit(), c.getFDataobjectfield(),
					c.getFFunction(), c.getTitle(), c.getOrderno(), c.getUserfunction(), c.getFieldahead(),
					c.getAggregate(), c.getOperator(), c.getOvalue(), c.getRemark(), c.getRecordids(),
					c.getRecordnames());
			dao.save(newcolumn);
			if (c.getLimits() != null && c.getLimits().size() > 0) {
				copySchemeDetail(c, newcolumn);
			}
		}
		ActionResult result = new ActionResult();
		return result;
	}

	private void copySchemeDetail(FDatafilterrolelimit column, FDatafilterrolelimit pcolumn) {
		for (FDatafilterrolelimit c : column.getLimits()) {
			FDatafilterrolelimit newcolumn = new FDatafilterrolelimit(null, c.getFDataobjectconditionBySubconditionid(),
					pcolumn, c.getFDataobjectfield(), c.getFFunction(), c.getTitle(), c.getOrderno(),
					c.getUserfunction(), c.getFieldahead(), c.getAggregate(), c.getOperator(), c.getOvalue(),
					c.getRemark(), c.getRecordids(), c.getRecordnames());
			dao.save(newcolumn);
			if (c.getLimits() != null && c.getLimits().size() > 0) {
				copySchemeDetail(c, newcolumn);
			}
		}
	}

	public ActionResult testDataFilterRole(String conditionid, JSONObject msg) {
		FDatafilterrole role = dao.findById(FDatafilterrole.class, conditionid);
		ActionResult result = new ActionResult();
		String ctext = role._getConditionText();
		Map<String, Object> param = DataObjectUtils.getSqlParameter();
		if (param != null) {
			for (String key : param.keySet()) {
				if (ctext.indexOf(":" + key) != -1) {
					ctext = ctext.replaceAll(":" + key, param.get(key).toString());
				}
			}
		}
		SqlGenerate generate = new SqlGenerate(role.getFDataobject(), true);
		generate.setDatafilterrole(role);
		generate.disableAllBaseFields();
		generate.setAddIdField(true);
		generate.pretreatment();
		msg.put("msg", ctext);
		Dao dao = Local.getBusinessDao();
		int total = dao.selectSQLCount(generate.generateSelectCount());
		result.setMsg(ctext);
		result.setTag(total);
		return result;
	}

	public List<ValueText> getSubCondition(String moduleName) {
		List<ValueText> result = new ArrayList<ValueText>();
		FDataobject object = DataObjectUtils.getDataObject(moduleName);
		for (FDataobjectcondition condition : dao.findByProperty(FDataobjectcondition.class, Constants.OBJECTID,
				object.getObjectid())) {
			result.add(new ValueText(condition.getConditionid(), condition.getTitle()));
		}
		return result;
	}

}
