package com.jhopesoft.platform.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.UserBean;
import com.jhopesoft.framework.bean.UserCanSelectDataRole;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.favorite.FUserobjectfavorite;
import com.jhopesoft.framework.dao.entity.favorite.FovUserdefaultfilterscheme;
import com.jhopesoft.framework.dao.entity.favorite.FovUserdefaultgridscheme;
import com.jhopesoft.framework.dao.entity.favorite.FovUserdefaultnavigatescheme;
import com.jhopesoft.framework.dao.entity.limit.FUserdatacanselectrole;
import com.jhopesoft.framework.dao.entity.system.FPersonnel;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFilterscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridnavigatescheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.Globals;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */
@Service
public class UserFavouriteService {

	@Resource
	private DaoImpl dao;

	/**
	 * 只改变session中role的状态，并未更新到faviorite中
	 * 
	 * @param roleid
	 * @param checked
	 * @return
	 */
	public ActionResult toggleDataRole(String roleid, boolean checked) {
		UserBean userBean = (UserBean) Local.getRequest().getSession().getAttribute(Globals.SYSTEM_USER);
		if (userBean.getCanselectdatarole() != null) {
			for (UserCanSelectDataRole datarole : userBean.getCanselectdatarole()) {
				if (datarole.getRoleId().equals(roleid)) {
					datarole.setChecked(checked);
					break;
				}
			}
		}
		return new ActionResult();
	}

	/**
	 * 用户保存可选数据角色的选中设置
	 * 
	 * @param rolestates
	 * @return
	 */
	public ActionResult updateDefaultDataRole(List<UserCanSelectDataRole> rolestates) {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		for (FUserdatacanselectrole role : user.getFUserdatacanselectroles()) {
			for (UserCanSelectDataRole dr : rolestates) {
				if (role.getFDatacanselectfilterrole().getRoleid().equals(dr.getRoleId())) {
					role.setChecked(dr.isChecked());
					dao.update(role);
					break;
				}
			}
		}
		return new ActionResult();
	}

	public ActionResult resetDefaultDataRole() {
		UserBean userBean = (UserBean) Local.getRequest().getSession().getAttribute(Globals.SYSTEM_USER);
		if (userBean.getCanselectdatarole() != null) {
			for (UserCanSelectDataRole datarole : userBean.getCanselectdatarole()) {
				datarole.setChecked(false);
			}
		}
		FUser user = dao.findById(FUser.class, Local.getUserid());
		for (FUserdatacanselectrole role : user.getFUserdatacanselectroles()) {
			role.setChecked(false);
			dao.update(role);
		}
		return new ActionResult();
	}

	public ActionResult setDefaultGridScheme(String schemeid) {
		FovGridscheme scheme = dao.findById(FovGridscheme.class, schemeid);
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FovUserdefaultgridscheme ownscheme = null;
		for (FovUserdefaultgridscheme us : user.getFovUserdefaultgridschemes()) {
			if (us.getFovGridscheme().getFDataobject() == scheme.getFDataobject()) {
				ownscheme = us;
				break;
			}
		}
		if (ownscheme == null) {
			ownscheme = new FovUserdefaultgridscheme();
			ownscheme.setFUser(user);
		}
		ownscheme.setFovGridscheme(scheme);
		dao.save(ownscheme);
		return new ActionResult();
	}

	public ActionResult setDefaultFilterScheme(String schemeid) {
		FovFilterscheme scheme = dao.findById(FovFilterscheme.class, schemeid);
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FovUserdefaultfilterscheme ownscheme = null;
		for (FovUserdefaultfilterscheme us : user.getFovUserdefaultfilterschemes()) {
			if (us.getFovFilterscheme().getFDataobject() == scheme.getFDataobject()) {
				ownscheme = us;
				break;
			}
		}
		if (ownscheme == null) {
			ownscheme = new FovUserdefaultfilterscheme();
			ownscheme.setFUser(user);
		}
		ownscheme.setFovFilterscheme(scheme);
		dao.save(ownscheme);
		return new ActionResult();
	}

	public ActionResult setDefaultNavigateScheme(String schemeid) {

		FovGridnavigatescheme scheme = dao.findById(FovGridnavigatescheme.class, schemeid);
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FovUserdefaultnavigatescheme ownscheme = null;
		for (FovUserdefaultnavigatescheme us : user.getFovUserdefaultnavigateschemes()) {
			if (us.getFovGridnavigatescheme().getFDataobject() == scheme.getFDataobject()) {
				ownscheme = us;
				break;
			}
		}
		if (ownscheme == null) {
			ownscheme = new FovUserdefaultnavigatescheme();
			ownscheme.setFUser(user);
		}
		ownscheme.setFovGridnavigatescheme(scheme);
		dao.save(ownscheme);
		return new ActionResult();
	}

	/**
	 * 取得当前用户所有的收藏模块,加入的模块，必须在菜单中有才行。
	 * 
	 * @return
	 */
	public JSONArray getUserObjects() {
		JSONArray result = new JSONArray();
		List<FUserobjectfavorite> userobjectfavorites = dao.findByProperty(FUserobjectfavorite.class, "userid",
				Local.getUserid(), "hasfavorite", true);
		for (FUserobjectfavorite e : userobjectfavorites) {
			JSONObject object = new JSONObject();
			if (e.getFDataobject() != null) {
				object.put(Constants.OBJECTID, e.getFDataobject().getObjectid());
				object.put("objectname", e.getFDataobject().getObjectname());
				object.put(Constants.TITLE, e.getFDataobject().getTitle());
				if (BooleanUtils.isTrue(e.getIsdatamining())) {
					object.put("isdatamining", true);
				}
			} else if (e.getFovHomepagescheme() != null) {
				object.put("moduleschemeid", e.getFovHomepagescheme().getHomepageschemeid());
				object.put(Constants.TITLE, e.getFovHomepagescheme().getSchemename());
			}
			result.add(object);
		}
		return result;
	}

	public ActionResult addUserObject(String objectid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserobjectfavorite(objectid);
		userobjectfavorite.setHasfavorite(true);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	/**
	 * 取消用户编好设置，并不是删除，里面可能有其他偏好设置
	 * 
	 * @param objectid
	 * @return
	 */
	public ActionResult removeUserObject(String objectid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserobjectfavorite(objectid);
		userobjectfavorite.setHasfavorite(false);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	public ActionResult addUserObjectDatamining(String objectid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserobjectdataminingfavorite(objectid);
		userobjectfavorite.setHasfavorite(true);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	/**
	 * 取消用户编好设置，并不是删除，里面可能有其他偏好设置
	 * 
	 * @param objectid
	 * @return
	 */
	public ActionResult removeUserObjectDatamining(String objectid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserobjectdataminingfavorite(objectid);
		userobjectfavorite.setHasfavorite(false);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	public ActionResult addUserModuleScheme(String moduleschemeid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserModuleSchemefavorite(moduleschemeid);
		userobjectfavorite.setHasfavorite(true);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	/**
	 * 取消用户编好设置，并不是删除，里面可能有其他偏好设置
	 * 
	 * @param objectid
	 * @return
	 */
	public ActionResult removeUserModuleScheme(String moduleschemeid) {
		FUserobjectfavorite userobjectfavorite = getOwnerFuserModuleSchemefavorite(moduleschemeid);
		userobjectfavorite.setHasfavorite(false);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	/**
	 * 保存当前用户的module的设置。
	 * 
	 * @param objectid
	 * @param gridtype,modulepanel的类型
	 * @param param
	 * @return
	 * 
	 * @throws IOException
	 */
	public ActionResult saveModuleSetting(String objectid, String gridtype, String param, boolean moduleDefault)
			throws IOException {
		FUserobjectfavorite userobjectfavorite = moduleDefault ? getOwnerDefaultFuserobjectfavorite()
				: getOwnerFuserobjectfavorite(objectid);
		Properties properties = new Properties();
		InputStream inStream = new ByteArrayInputStream(
				userobjectfavorite.getModulesetting() != null ? userobjectfavorite.getModulesetting().getBytes()
						: "".getBytes());
		properties.load(inStream);
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(gridtype + ".")) {
				properties.remove(key);
			}
		}
		JSONObject object = JSONObject.parseObject(param);
		Map<String, String> values = changeJsonToMap(object);
		for (String name : values.keySet()) {
			properties.setProperty(gridtype + "." + name, values.get(name));
		}
		OutputStream os = new ByteArrayOutputStream();
		properties.store(os, null);
		String str = os.toString();
		os.flush();
		os.close();
		userobjectfavorite.setModulesetting(str);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	public ActionResult clearModuleSetting(String objectid, String gridType, String clearType) throws IOException {
		if (Constants.DEFAULT.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerDefaultFuserobjectfavorite();
			favorite.setModulesetting(clearSettingWithType(favorite.getModulesetting(), gridType));
			dao.update(favorite);
		} else if (Constants.THIS.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerFuserobjectfavorite(objectid);
			favorite.setModulesetting(clearSettingWithType(favorite.getModulesetting(), gridType));
			dao.update(favorite);
		} else if (Constants.ALL.equals(clearType)) {
			FUser user = dao.findById(FUser.class, Local.getUserid());
			for (FUserobjectfavorite favorite : user.getFUserobjectfavorites()) {
				if (favorite.getFDataobject() != null && BooleanUtils.isNotTrue(favorite.getIsdatamining())) {
					favorite.setModulesetting(clearSettingWithType(favorite.getModulesetting(), gridType));
					dao.update(favorite);
				}
			}
		}
		return new ActionResult();
	}

	public ActionResult saveFormSetting(String objectid, String formtype, String param, boolean formDefault)
			throws IOException {
		FUserobjectfavorite userobjectfavorite = formDefault ? getOwnerDefaultFuserobjectfavorite()
				: getOwnerFuserobjectfavorite(objectid);
		Properties properties = new Properties();
		InputStream inStream = new ByteArrayInputStream(
				userobjectfavorite.getFormsetting() != null ? userobjectfavorite.getFormsetting().getBytes()
						: "".getBytes());
		properties.load(inStream);
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(formtype + ".")) {
				properties.remove(key);
			}
		}
		JSONObject object = JSONObject.parseObject(param);
		Map<String, String> values = changeJsonToMap(object);
		for (String name : values.keySet()) {
			properties.setProperty(formtype + "." + name, values.get(name));
		}
		OutputStream os = new ByteArrayOutputStream();
		properties.store(os, null);
		String str = os.toString();
		os.flush();
		os.close();
		userobjectfavorite.setFormsetting(str);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	public ActionResult clearFormSetting(String objectid, String formType, String clearType) throws IOException {
		if (Constants.DEFAULT.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerDefaultFuserobjectfavorite();
			favorite.setFormsetting(clearSettingWithType(favorite.getFormsetting(), formType));
			dao.update(favorite);
		} else if (Constants.THIS.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerFuserobjectfavorite(objectid);
			favorite.setFormsetting(clearSettingWithType(favorite.getFormsetting(), formType));
			dao.update(favorite);
		} else if (Constants.ALL.equals(clearType)) {
			FUser user = dao.findById(FUser.class, Local.getUserid());
			for (FUserobjectfavorite favorite : user.getFUserobjectfavorites()) {
				if (favorite.getFDataobject() != null && BooleanUtils.isNotTrue(favorite.getIsdatamining())) {
					favorite.setFormsetting(clearSettingWithType(favorite.getFormsetting(), formType));
					dao.update(favorite);
				}
			}
		}
		return new ActionResult();
	}

	public ActionResult saveDataminingSetting(String objectid, String dataminingType, String param,
			boolean dataminingDefault) throws IOException {
		FUserobjectfavorite userobjectfavorite = dataminingDefault ? getOwnerDefaultFuserobjectfavorite()
				: getOwnerFuserobjectfavorite(objectid);
		Properties properties = new Properties();
		InputStream inStream = new ByteArrayInputStream(
				userobjectfavorite.getDataminingsetting() != null ? userobjectfavorite.getDataminingsetting().getBytes()
						: "".getBytes());
		properties.load(inStream);
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(dataminingType + ".")) {
				properties.remove(key);
			}
		}
		JSONObject object = JSONObject.parseObject(param);
		Map<String, String> values = changeJsonToMap(object);
		for (String name : values.keySet()) {
			properties.setProperty(dataminingType + "." + name, values.get(name));
		}
		OutputStream os = new ByteArrayOutputStream();
		properties.store(os, null);
		String str = os.toString();
		os.flush();
		os.close();
		userobjectfavorite.setDataminingsetting(str);
		dao.update(userobjectfavorite);
		return new ActionResult();
	}

	public ActionResult clearDataminingSetting(String objectid, String dataminingType, String clearType)
			throws IOException {
		if (Constants.DEFAULT.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerDefaultFuserobjectfavorite();
			favorite.setDataminingsetting(clearSettingWithType(favorite.getDataminingsetting(), dataminingType));
			dao.update(favorite);
		} else if (Constants.THIS.equals(clearType)) {
			FUserobjectfavorite favorite = getOwnerFuserobjectfavorite(objectid);
			favorite.setDataminingsetting(clearSettingWithType(favorite.getDataminingsetting(), dataminingType));
			dao.update(favorite);
		} else if (Constants.ALL.equals(clearType)) {
			FUser user = dao.findById(FUser.class, Local.getUserid());
			for (FUserobjectfavorite favorite : user.getFUserobjectfavorites()) {
				if (favorite.getFDataobject() != null && BooleanUtils.isNotTrue(favorite.getIsdatamining())) {
					favorite.setDataminingsetting(
							clearSettingWithType(favorite.getDataminingsetting(), dataminingType));
					dao.update(favorite);
				}
			}
		}
		return new ActionResult();
	}

	/**
	 * 把 json 用递归的办法，一级级的化为一层的值。如
	 * "toolbar":{"dock":"top","buttonScale":"small","topbottomMode":"normal",
	 * "leftrightMode":"compact","leftrightArrowAlign":"bottom"} 转化成
	 * toolbar.dock:top
	 * 
	 * @param jsonobject
	 * @return
	 */
	private Map<String, String> changeJsonToMap(JSONObject jsonobject) {
		Map<String, String> result = new HashMap<String, String>(0);
		changeAjsonToMap(jsonobject, null, result);
		return result;
	}

	private void changeAjsonToMap(JSONObject jsonobject, String head, Map<String, String> result) {
		for (String key : jsonobject.keySet()) {
			if (jsonobject.get(key) instanceof JSONObject) {
				changeAjsonToMap(jsonobject.getJSONObject(key), (head == null ? "" : head + ".") + key, result);
			} else {
				if (head != null) {
					result.put(head + "." + key, jsonobject.getString(key));
				} else {
					result.put(key, jsonobject.getString(key));
				}
			}
		}

	}

	private FUserobjectfavorite getOwnerDefaultFuserobjectfavorite() {
		FUserobjectfavorite userobjectfavorite = Local.getDao().findByPropertyFirst(FUserobjectfavorite.class, "userid",
				Local.getUserid(), "isuserdefault", true);
		if (userobjectfavorite == null) {
			userobjectfavorite = new FUserobjectfavorite();
			userobjectfavorite.setFUser(dao.findById(FUser.class, Local.getUserid()));
			userobjectfavorite.setIsuserdefault(true);
			dao.save(userobjectfavorite);
		}
		return userobjectfavorite;
	}

	private FUserobjectfavorite getOwnerFuserobjectfavorite(String objectid) {
		FUserobjectfavorite userobjectfavorite = Local.getDao().findByPropertyFirst(FUserobjectfavorite.class, "userid",
				Local.getUserid(), Constants.OBJECTID, objectid, "isdatamining", false);
		if (userobjectfavorite == null) {
			userobjectfavorite = new FUserobjectfavorite(dao.findById(FDataobject.class, objectid),
					dao.findById(FUser.class, Local.getUserid()));
			dao.save(userobjectfavorite);
		}
		return userobjectfavorite;
	}

	private FUserobjectfavorite getOwnerFuserobjectdataminingfavorite(String objectid) {
		FUserobjectfavorite userobjectfavorite = Local.getDao().findByPropertyFirst(FUserobjectfavorite.class, "userid",
				Local.getUserid(), Constants.OBJECTID, objectid, "isdatamining", true);
		if (userobjectfavorite == null) {
			userobjectfavorite = new FUserobjectfavorite(dao.findById(FDataobject.class, objectid),
					dao.findById(FUser.class, Local.getUserid()));
			userobjectfavorite.setIsdatamining(true);
			dao.save(userobjectfavorite);
		}
		return userobjectfavorite;
	}

	private FUserobjectfavorite getOwnerFuserModuleSchemefavorite(String moduleschemeid) {
		FUserobjectfavorite userobjectfavorite = Local.getDao().findByPropertyFirst(FUserobjectfavorite.class, "userid",
				Local.getUserid(), "homepageschemeid", moduleschemeid);
		if (userobjectfavorite == null) {
			userobjectfavorite = new FUserobjectfavorite(dao.findById(FovHomepagescheme.class, moduleschemeid),
					dao.findById(FUser.class, Local.getUserid()));
			dao.save(userobjectfavorite);
		}
		return userobjectfavorite;
	}

	/**
	 * 从配置字符串中删除某种类型的配置
	 * 
	 * @param value
	 * @param type
	 * @return
	 * @throws IOException
	 */
	private String clearSettingWithType(String value, String type) throws IOException {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		Properties properties = new Properties();
		InputStream inStream = new ByteArrayInputStream(value.getBytes());
		properties.load(inStream);
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(type + ".")) {
				properties.remove(key);
			}
		}
		OutputStream os = new ByteArrayOutputStream();
		properties.store(os, null);
		return os.toString();
	}

	/**
	 * 给当前的人员加一个tag
	 * 
	 * @param label
	 * @return 附加信息都是放在 additionstr5 里面
	 * @throws IOException
	 */
	private static final String TAGS = "tags";

	public ActionResult addTag(String label) throws IOException {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FPersonnel personnel = user.getFPersonnel();
		Properties properties = CommonUtils.getPropertiesFromString(personnel.getAdditionstr5());
		OutputStream stream = new ByteArrayOutputStream();
		if (StringUtils.isNotBlank(properties.getProperty(TAGS))) {
			properties.setProperty(TAGS, properties.getProperty(TAGS) + Constants.COMMA + label);
		} else {
			properties.setProperty(TAGS, label);
		}
		properties.store(new OutputStreamWriter(stream, "utf-8"), null);
		personnel.setAdditionstr5(stream.toString());
		dao.update(personnel);
		return null;
	}

	public ActionResult removeTag(String label) throws UnsupportedEncodingException, IOException {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FPersonnel personnel = user.getFPersonnel();
		Properties properties = CommonUtils.getPropertiesFromString(personnel.getAdditionstr5());
		OutputStream stream = new ByteArrayOutputStream();
		if (properties.containsKey(TAGS)) {
			List<String> list = new ArrayList<String>(Arrays.asList(properties.getProperty(TAGS).split(Constants.COMMA)));
			list.remove(label);
			properties.setProperty(TAGS, String.join(Constants.COMMA, list));
		} else {
			properties.setProperty(TAGS, "");
		}
		properties.store(new OutputStreamWriter(stream, "utf-8"), null);
		personnel.setAdditionstr5(stream.toString());
		dao.update(personnel);
		return null;
	}

	private static final String SIGNATURE = "signature";

	public ActionResult updateSignature(String text) throws UnsupportedEncodingException, IOException {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FPersonnel personnel = user.getFPersonnel();
		Properties properties = CommonUtils.getPropertiesFromString(personnel.getAdditionstr5());
		OutputStream stream = new ByteArrayOutputStream();
		properties.setProperty(SIGNATURE, text.trim());
		properties.store(new OutputStreamWriter(stream, "utf-8"), null);
		personnel.setAdditionstr5(stream.toString());
		dao.update(personnel);
		return null;
	}

}
