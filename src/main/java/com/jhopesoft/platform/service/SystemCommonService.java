package com.jhopesoft.platform.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectgroup;
import com.jhopesoft.framework.dao.entity.module.FCompanymodule;
import com.jhopesoft.framework.dao.entity.module.FCompanymodulegroup;
import com.jhopesoft.framework.dao.entity.module.FModule;
import com.jhopesoft.framework.dao.entity.system.FCompany;
import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.dao.entity.viewsetting.FovDataobjectwidget;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class SystemCommonService {
	@Resource
	private DaoImpl dao;

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private DataObjectService dataObjectService;

	/**
	 * 取得某个定义在系统设置中的属性值
	 * 
	 * @param propName
	 * @return 属性值，未找到返回null
	 */
	public String getProperty(String propName) {
		FCompany company = dao.findAll(FCompany.class).get(0);
		List<FSysteminfo> infos = new ArrayList<FSysteminfo>(company.getFSysteminfos());
		FSysteminfo systeminfo = infos.get(0);
		if (StringUtils.isBlank(systeminfo.getProperites())) {
			return null;
		}
		Properties properties = new Properties();
		InputStreamReader reader = null;
		InputStream inStream = new ByteArrayInputStream(systeminfo.getProperites().getBytes());
		try {
			reader = new InputStreamReader(inStream, "UTF-8");
			properties.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(propName);
	}

	/**
	 * 取得某个定义在系统设置中的属性值
	 * 
	 * @param propName
	 * @return 属性值，未找到返回null
	 */
	public String getProperty(String propName, String propString) {
		if (StringUtils.isBlank(propString)) {
			return null;
		}
		Properties properties = new Properties();
		InputStreamReader reader = null;
		InputStream inStream = new ByteArrayInputStream(propString.getBytes());
		try {
			reader = new InputStreamReader(inStream, "UTF-8");
			properties.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(propName);
	}

	/**
	 * 获取全部(模块分组+模块)信息
	 * 
	 * @return
	 */
	public List<TreeNode> getModuleTree(String companyid) {
		String sql = "select a.modulegroupid as id, a.groupname as text, a.parentId, a.orderno, '1' as type,'x-fa fa-book' as iconCls,"
				+ "	(case (select count(1) from f_companymodulegroup a1 where a1.companyid = '" + companyid
				+ "' and a.groupname = a1.groupname " + "         ) when 0 then 0 else 1 end) checked "
				+ " from f_modulegroup a " + "    union all "
				+ "  select b.moduleid as id, b.modulename as text, b.modulegroupid as parentId, b.orderno, '2' as type,'x-fa fa-gear' as iconCls,"
				+ "	(case (select count(1) from f_companymodule b1 where b1.companyid = '" + companyid
				+ "' and b1.moduleid = b.moduleid " + "         ) when 0 then 0 else 1 end) checked "
				+ "  from f_module b";
		return dao.executeSQLQuery(sql, TreeNode.class);
	}

	/**
	 * 获取指定公司的(公司模块分组+公司模块)信息
	 * 
	 * @param companyid
	 * @return
	 */
	public List<Map<String, Object>> getCompanyModuleTree(String companyid) {
		String sql = "select a.cmodulegroupid as id,a.groupname as text,a.parentId,a.orderno,'' as moduleid,"
				+ " '1' as type,'x-fa fa-book' as iconCls " + "  from f_companymodulegroup a where a.companyid = '"
				+ companyid + "' " + "   union all "
				+ " select b.cmoduleid as id,c.modulename as text,b.cmodulegroupid as parentId,1 as orderno,b.moduleid as moduleid,"
				+ " '2' as type,'x-fa fa-gear' as iconCls " + "  from f_companymodule b "
				+ "  left join f_module c on b.moduleid = c.moduleid" + "  where b.companyid = '" + companyid + "'";
		return dao.executeSQLQuery(sql);
	}

	public ResultBean saveCompanyModule(List<Map<String, Object>> datalist, String companyid) {
		ResultBean result = new ResultBean();
		if (datalist == null) {
			return result;
		}
		FCompanymodulegroup tempgroup = new FCompanymodulegroup();
		tempgroup.setGroupname("临时组");
		tempgroup.setOrderno(0);
		tempgroup.setFCompany(dao.findById(FCompany.class, companyid));
		dao.save(tempgroup);
		String tempgrouid = tempgroup.getCmodulegroupid();
		// 临时组
		dao.executeUpdate("update FCompanymodule set cmodulegroupid = ?0 where companyid = ?1 ", tempgrouid, companyid);
		dao.executeUpdate("delete FCompanymodulegroup where companyid = ?0 and cmodulegroupid <> ?1 ", companyid,
				tempgrouid);
		Map<String, String> groupids = new HashMap<String, String>(0);
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i < datalist.size(); i++) {
			Map<String, Object> map = datalist.get(i);
			String id = (String) map.get(Constants.ID);
			String text = (String) map.get(Constants.TEXT);
			String parentid = (String) map.get("parentid");
			String moduleid = (String) map.get("moduleid");
			String type = (String) map.get(Constants.TYPE);
			Integer orderno = (int) map.get("orderno");
			if ("1".equals(type)) {
				// 分组
				FCompanymodulegroup group = new FCompanymodulegroup();
				group.setGroupname(text);
				group.setOrderno(orderno);
				group.setFCompany(dao.findById(FCompany.class, companyid));
				group.setFCompanymodulegroup(
						dao.findByPropertyFirst(FCompanymodulegroup.class, "cmodulegroupid", groupids.get(parentid)));
				dao.save(group);
				groupids.put(id, group.getCmodulegroupid());
			} else if ("2".equals(type)) {
				FCompanymodule cmodule = dao.findByPropertyFirst(FCompanymodule.class, "moduleid", moduleid,
						Constants.COMPANYID, companyid);
				if (CommonUtils.isEmpty(cmodule)) {
					cmodule = new FCompanymodule();
					cmodule.setFCompany(dao.findById(FCompany.class, companyid));
					cmodule.setFCompanymodulegroup(dao.findByPropertyFirst(FCompanymodulegroup.class, "cmodulegroupid",
							groupids.get(parentid)));
					cmodule.setFModule(dao.findById(FModule.class, moduleid));
					// System.out.println(cmodule.getFModule().getModuleid());
					cmodule.setOrderno(orderno);
					dao.save(cmodule);
				} else {
					cmodule.setFCompanymodulegroup(dao.findByPropertyFirst(FCompanymodulegroup.class, "cmodulegroupid",
							groupids.get(parentid)));
					cmodule.setOrderno(orderno);
				}
				ids.add(cmodule.getCmoduleid());
			}
		}
		List<FCompanymodule> clist = dao.findByProperty(FCompanymodule.class, Constants.COMPANYID, companyid);
		for (int i = 0; i < clist.size(); i++) {
			FCompanymodule cm = clist.get(i);
			if (!ids.contains(cm.getCmoduleid())) {
				dao.delete(cm);
			}
		}
		groupids.clear();
		dao.executeUpdate("delete FCompanymodulegroup where cmodulegroupid = '" + tempgrouid + "' ");
		return result;
	}

	public JSONArray getObjectgGroups() throws SQLException {
		JSONArray result = new JSONArray();
		JSONObject object = new JSONObject();
		List<FDataobjectgroup> groups = dao.findAll(FDataobjectgroup.class);
		for (FDataobjectgroup group : groups) {
			object = new JSONObject();
			object.put(Constants.TEXT, group.getGroupname());
			object.put("value", group.getObjectgroupid());
			result.add(object);
		}
		return result;
	}

	/**
	 * 保存模块的帮助信息markdown格式
	 * 
	 * @param moduleName
	 * @param text
	 * @return
	 */
	public ActionResult saveObjectMarkDown(String moduleName, String text) {
		FDataobject object = dao.findById(FDataobject.class, moduleName);
		object.setHelpmarkdown(text);
		dao.saveOrUpdate(object);
		return new ActionResult();
	}

	/**
	 * 执行前台发送的sql命令，有很大的安全风险
	 * 
	 * @param sql
	 * @return
	 */
	public ActionResult executeSql(String sql) {
		ActionResult result = new ActionResult();
		try {
			dataObjectService.saveOperateLog(DataObjectUtils.getDataObject(FSysteminfo.class.getSimpleName()),
					"执行sql语句", "执行sql语句", "执行sql语句", sql);
			if (Constants.ADMINISTRATOR.equals(Local.getUsercode())) {
				jdbcTemplate.execute(sql);
			} else {
				result.setSuccess(false);
				result.setMsg("无权执行此操作！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg(CommonUtils.getThrowableOriginalMessage(e));
		}
		return result;
	}

	public FovDataobjectwidget getWidgetDefine(String widgetid) {
		return dao.findById(FovDataobjectwidget.class, widgetid);
	}

    public FovHomepagescheme getHomepageSchemeDefine(String homepageschemeid) {
        return dao.findById(FovHomepagescheme.class, homepageschemeid);
    }

}
