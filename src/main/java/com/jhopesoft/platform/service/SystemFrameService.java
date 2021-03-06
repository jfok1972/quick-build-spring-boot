package com.jhopesoft.platform.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.bean.PopupMessage;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.bean.UploadFileBean;
import com.jhopesoft.framework.bean.UserBean;
import com.jhopesoft.framework.bean.ValueText;
import com.jhopesoft.framework.context.contextAware.AppContextAware;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.core.objectquery.sqlfield.SqlField;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.common.FUsernotification;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjecthintmessage;
import com.jhopesoft.framework.dao.entity.dataobject.FNotification;
import com.jhopesoft.framework.dao.entity.limit.FUserdataobjecthintmessage;
import com.jhopesoft.framework.dao.entity.log.FUsernotificationresult;
import com.jhopesoft.framework.dao.entity.system.FPersonnel;
import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.dao.entity.viewsetting.FovBackgroundimage;
import com.jhopesoft.framework.dao.entity.workflow.VActRuTask;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.DateUtils;
import com.jhopesoft.framework.utils.FileUtils;
import com.jhopesoft.framework.utils.Globals;
import com.jhopesoft.framework.utils.MD5;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.framework.utils.Sm4Util;
import com.jhopesoft.framework.utils.TypeChange;
import com.jhopesoft.platform.logic.define.PopupMessageInterface;

import ognl.OgnlException;

/**
 * 
 * @author ?????? jfok1972@qq.com
 * 
 */

@Service
public class SystemFrameService {

	@Autowired
	private DataObjectService dataObjectService;

	@Resource
	private DaoImpl dao;

	@Autowired
	public TaskService taskService;

	@SystemLogs("??????????????????????????????????????????quick-build-antp")
	@Transactional(readOnly = true)
	public JSONObject getCurrentUser() {
		JSONObject result = new JSONObject();
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FPersonnel personnel = user.getFPersonnel();
		JSONObject userObject = new JSONObject();
		userObject.put(Constants.ID, user.getUserid());
		userObject.put(Constants.NAME, user.getUsername());
		userObject.put(Constants.CODE, user.getUsercode());
		userObject.put(Constants.TYPE, user.getUsertype());
		// ????????????
		userObject.put("security", user.getAdditionstr5());
		List<String> roles = new ArrayList<String>();
		user.getFUserroles().forEach(role -> {
			roles.add(role.getFRole().getRolename());
		});
		userObject.put("roles", roles);
		result.put("user", userObject);

		Map<String, Object> personnalMap = dataObjectService.getObjectRecordMap(FPersonnel.class.getSimpleName(),
				personnel.getPersonnelid());

		JSONObject personnelObject = new JSONObject();
		personnelObject.put(Constants.ID, personnel.getPersonnelid());
		personnelObject.put(Constants.NAME, personnel.getPersonnelname());
		personnelObject.put(Constants.CODE, personnel.getPersonnelcode());
		personnelObject.put(Constants.OFFICETEL, personnel.getOfficetel());
		personnelObject.put(Constants.MOBILE, personnel.getMobile());
		personnelObject.put(Constants.EMAIL, personnel.getEmail());
		personnelObject.put("jobname", personnalMap.get("jobname_dictname"));
		personnelObject.put("stationname", personnalMap.get("stationname_dictname"));
		personnelObject.put("technical", personnalMap.get("technical_dictname"));
		personnelObject.put("sex", personnel.getSexuality());
		personnelObject.put("birthday", personnel.getBirthday());
		// ????????????
		personnelObject.put(Constants.PROFILE, personnel.getRemark());
		personnelObject.put("educationlevel", personnalMap.get("educationlevel_dictname"));
		personnelObject.put("orgfullname", personnel.getFOrganization().generateFullOrgname(dao));
		personnelObject.put(AVATAR, personnel.getFavicon());
		personnelObject.put("photo", personnel.getPhoto());
		Map<String, Object> other = new HashMap<String, Object>(0);
		CommonUtils.changePropertiesStringToMap(personnel.getAdditionstr5(), other);
		// ??????????????? additionstr5 ??????
		personnelObject.put("signature", other.get("signature"));
		personnelObject.put("country", other.get("country"));
		personnelObject.put("province", other.get("province"));
		personnelObject.put("city", other.get("city"));
		personnelObject.put("street", other.get("street"));
		// ??????????????? additionstr5 ?????????property??????
		Object tagsObject = other.get("tags");
		JSONArray tagArr = new JSONArray();
		if (tagsObject != null && tagsObject instanceof String) {
			String[] tagsArray = ((String) tagsObject).split(Constants.COMMA);
			new ArrayList<>(Arrays.asList(tagsArray)).forEach(value -> {
				if (StringUtils.isNotBlank(value)) {
					JSONObject o = new JSONObject();
					o.put(Constants.KEY, value);
					o.put(Constants.LABEL, value);
					tagArr.add(o);
				}
			});
		}
		personnelObject.put("tags", tagArr);
		result.put("personnel", personnelObject);

		return result;
	}

	@SystemLogs("??????????????????????????????")
	public List<TreeNode> getMenuTree() {
		String userid = Local.getUserid();
		String usertype = Local.getUserBean().getUsertype();
		String companyid = Local.getCompanyid();
		String sql = "select"
				+ "	 a.menuid,a.menuname as text,a.engname,a.parentid as parentId,a.icon,a.iconCls,a.iconColor,a.isdisplay as visible, "
				+ "	 c.moduletype as type,c.modulesource as url,c.objectid, c.homepageschemeid as moduleschemeid ,"
				+ "a.menutype,a.orderno,a.isexpand as expanded ,a.isdatamining as isdatamining " + " from"
				+ " 	 f_companymenu a,f_companymodule b,f_module c " + " where a.companyid = b.companyid "
				+ " 	 and a.cmoduleid = b.cmoduleid " + " 	 and b.moduleid = c.moduleid "
				+ " 	 and a.companyid = '" + companyid + "' ";
		if (!Constants.ZEROZERO.equals(usertype)) {
			// ???????????????-???????????????????????????????????????????????????
			sql += " and (" + " 	   (" + " 		  select count(1) c "
					+ " 			 from f_modulefunction mf1,f_userfunctionlimit ufl "
					+ " 			 where mf1.cmoduleid = b.cmoduleid and mf1.functionid = ufl.functionid and ufl.userid = '"
					+ userid + "' " + " 	   ) > 0" + "     or " + "     ( " + " 		  select count(1) c "
					+ " 			 from f_modulefunction mf2,f_rolefunctionlimit rfl,f_userrole ur "
					+ " 			 where mf2.cmoduleid = b.cmoduleid and mf2.functionid = rfl.functionid and rfl.roleid = ur.roleid and ur.userid ='"
					+ userid + "' " + " 	   ) > 0" + " ) ";
		}
		sql += " order by a.orderno";
		List<TreeNode> dataList = dao.executeSQLQuery(sql, TreeNode.class);
		Map<String, TreeNode> parentMap = new HashMap<String, TreeNode>(0);
		// ???????????????parentid
		Set<String> parentidSet = new HashSet<String>();
		for (TreeNode node : dataList) {
			if (!CommonUtils.isEmpty(node.getParentId())) {
				parentNode(parentMap, node, parentidSet);
			}
		}
		for (String key : parentMap.keySet()) {
			dataList.add(parentMap.get(key));
		}
		return dataList;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param allList
	 * @param dataList
	 */
	private void parentNode(Map<String, TreeNode> parentMap, TreeNode node, Set<String> parentidSet) {
		if (parentidSet.contains(node.getParentId())) {
			return;
		} else {
			parentidSet.add(node.getParentId());
		}
		String sql = "select a.menuid,a.menuname as text,a.engname,a.parentid as parentId,"
				+ " a.icon,a.iconCls,a.iconColor,a.isdisplay as visible,'00' as type,a.isexpand as expanded ,a.menutype,a.orderno"
				+ "  from f_companymenu a where a.menuid = ?0 order by a.orderno ";
		TreeNode parentNode = dao.executeSQLQueryFirst(sql, TreeNode.class, node.getParentId());
		if (!CommonUtils.isEmpty(parentNode) && !parentMap.containsKey(parentNode.getMenuid())) {
			parentMap.put(parentNode.getMenuid(), parentNode);
			if (!CommonUtils.isEmpty(parentNode.getParentId())) {
				parentNode(parentMap, parentNode, parentidSet);
			}
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @throws IOException
	 */
	public void getSystemFavicon() throws IOException {
		List<FSysteminfo> systeminfos = (List<FSysteminfo>) dao.findAll(FSysteminfo.class);
		HttpServletResponse response = Local.getResponse();
		if (systeminfos.size() > 0 && systeminfos.get(0).getIconfile() != null) {
			FileUtils.copy(new ByteArrayInputStream(systeminfos.get(0).getIconfile()), response.getOutputStream());
		} else {
			org.springframework.core.io.Resource resource = new ClassPathResource(
					Local.getProjectSpace().getImages() + "system/defaultfavicon.jpg");
			FileUtils.copy(resource.getInputStream(), response.getOutputStream());
		}
	}

	/**
	 * ????????????????????????
	 * 
	 * @param userid
	 * @throws IOException
	 */
	public void getUserFavicon(String userid) throws IOException {
		FUser user = dao.findById(FUser.class, userid == null ? Local.getUserid() : userid);
		FPersonnel personnel = user.getFPersonnel();
		HttpServletResponse response = Local.getResponse();
		if (personnel != null && personnel.getFavicon() != null && personnel.getFavicon().length > 0) {
			FileUtils.copy(new ByteArrayInputStream(personnel.getFavicon()), response.getOutputStream());
		} else {
			org.springframework.core.io.Resource resource = new ClassPathResource(
					Local.getProjectSpace().getImages() + "system/defaultuserfavicon.jpg");
			FileUtils.copy(resource.getInputStream(), response.getOutputStream());
		}
	}

	/**
	 * ?????????form?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????img???
	 * 
	 * @param uploadExcelBean
	 * @param bindingResult
	 * @param request
	 * @return
	 */
	public ActionResult uploadImageFileAndReturn(UploadFileBean uploadExcelBean, BindingResult bindingResult,
			HttpServletRequest request) {
		ActionResult result = new ActionResult();
		InputStream is;
		try {
			is = uploadExcelBean.getFile().getInputStream();
			byte[] buffer = new byte[(int) uploadExcelBean.getFile().getSize()];
			is.read(buffer, 0, (int) uploadExcelBean.getFile().getSize());
			result.setMsg(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg("???????????????????????????????????????????????????");
		}
		return result;
	}

	public ActionResult resetPassword(String userid) {
		FUser user = dao.findById(FUser.class, userid);
		user.setPassword(MD5.MD5Encode(Constants.DEFAULT_PASSWORD + user.getSalt()));
		user.setAdditionstr5("???");
		dao.update(user);
		return new ActionResult();
	}

	public ActionResult changePassword(String oldPassword, String newPassword, String strong) {
		ActionResult result = new ActionResult();
		FUser user = dao.findById(FUser.class, Local.getUserid());
		String salt = new StringBuffer(LoginService.SM4KEY).reverse().toString();
		try {
			oldPassword = Sm4Util.decrypt(oldPassword, salt);
			newPassword = Sm4Util.decrypt(newPassword, salt);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("????????????????????????????????????????????????");
			result.setSuccess(false);
			return result;
		}
		String oldmd5 = MD5.MD5Encode(oldPassword + user.getSalt());
		if (oldmd5.equals(user.getPassword())) {
			user.setPassword(MD5.MD5Encode(newPassword + user.getSalt()));
			user.setAdditionstr5(strong == null ? "???" : strong);
			dao.update(user);
			UserBean userBean = (UserBean) Local.getRequest().getSession().getAttribute(Globals.SYSTEM_USER);
			userBean.setPasswordstrong(user.getAdditionstr5());
		} else {
			result.setMsg("????????????????????????");
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @return
	 */
	public ActionResult getHintMessageCount() {
		ActionResult result = new ActionResult();
		FDataobject module = DataObjectUtils.getDataObject(VActRuTask.class.getSimpleName());
		int total = 0;
		if (module != null) {
			SqlGenerate generate = new SqlGenerate();
			generate.setDataobject(module);
			generate.pretreatment();
			Dao dao = Local.getDao();
			total = dao.selectSQLCount(generate.generateSelectCount());
		}
		result.setTag(total);
		return result;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * 
	 * id: '000000010', title: '???????????????????????????', description: '???10????????????????????????', extra:
	 * '???????????????3 ????????????', status: 'urgent', type: 'event',
	 * 
	 * action: 'approve', || 'claim', data : [{ text:'',value:''}] , moduleName :
	 * '',
	 * 
	 * @return
	 */
	public static final String CANAPPROVE = "canapprove";
	public static final String CANCLAIM = "canclaim";

	@SuppressWarnings("unchecked")

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @return
	 */
	public JSONArray getCanApproveInfo() {
		FDataobject module = DataObjectUtils.getDataObject(VActRuTask.class.getSimpleName());
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(module);
		generate.addAllFields();
		generate.pretreatment();
		Dao dao = Local.getDao();
		PageInfo<Map<String, Object>> pageinfo = dao.executeSQLQueryPage(generate.generateSelect(),
				generate.getFieldScales(), 0, Integer.MAX_VALUE, Integer.MAX_VALUE, new Object[] {});
		List<VActRuTask> records = new ArrayList<VActRuTask>();
		pageinfo.getData().forEach(rec -> {
			VActRuTask task = new VActRuTask();
			try {
				BeanUtils.populate(task, rec);
				records.add(task);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
		Map<String, JSONObject> map = new HashMap<String, JSONObject>(0);
		records.forEach(rec -> {
			String action = rec.getActAssignee() != null ? Constants.APPROVE : "claim";
			String id = rec.getObjectname() + "-" + action;
			if (!map.containsKey(id)) {
				JSONObject eventObject = new JSONObject();
				eventObject.put(Constants.ID, id);
				eventObject.put(Constants.MODULE_NAME, rec.getObjectname());
				eventObject.put(Constants.TITLE, rec.getObjecttitle());
				eventObject.put(Constants.TYPE, "event");
				eventObject.put(Constants.ACTION, action);
				eventObject.put("maxhours", 0);
				eventObject.put(Constants.DATA, new ArrayList<ValueText>());
				map.put(id, eventObject);
			}
			JSONObject eventObject = map.get(id);
			ArrayList<ValueText> vts = (ArrayList<ValueText>) eventObject.get(Constants.DATA);
			vts.add(new ValueText(rec.getActBusinessKey(),
					rec.getActBusinessName().replaceFirst("???" + rec.getObjecttitle() + "???", "")));
			eventObject.put(Constants.COUNT, vts.size());
			Integer maxdays = eventObject.getInteger("maxhours");
			Task task = taskService.createTaskQuery().taskId(rec.getActExecuteTaskId()).singleResult();
			if (task != null) {
				int hours = (int) ((System.currentTimeMillis() - task.getCreateTime().getTime()) / 1000 / 3600);
				eventObject.put("maxhours", Math.max(maxdays, hours));
			}
		});
		JSONArray array = new JSONArray();
		map.values().forEach(obj -> array.add(obj));
		return array;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @return
	 */
	public JSONArray getCanAuditInfo() {
		JSONArray result = new JSONArray();
		List<FDataobject> objects = dao.findByProperty(FDataobject.class, "hasaudit", true);
		String action = "audit";
		objects.forEach(dataobject -> {
			SqlGenerate generate = new SqlGenerate();
			generate.setDataobject(dataobject);
			UserParentFilter filter = new UserParentFilter();
			filter.setFieldName("canAuditingUserid");
			filter.setOperator("=");
			filter.setFieldvalue(Local.getUserid());
			List<UserParentFilter> filters = new ArrayList<UserParentFilter>();
			filters.add(filter);
			generate.setUserParentFilters(filters);
			generate.pretreatment();
			int total = dao.selectSQLCount(generate.generateSelectCount());
			if (total > 0) {
				JSONObject eventObject = new JSONObject();
				String id = dataobject.getObjectname() + "-" + action;
				eventObject.put(Constants.ID, id);
				eventObject.put(Constants.MODULE_NAME, dataobject.getObjectname());
				eventObject.put(Constants.TITLE, dataobject.getTitle());
				eventObject.put(Constants.TYPE, "event");
				eventObject.put(Constants.ACTION, action);
				eventObject.put(Constants.COUNT, total);
				result.add(eventObject);
			}
		});
		return result;
	}

	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getQuestionAndMessage() {
		List<PopupMessage> list = (List<PopupMessage>) getPopupMessage().getMsg();
		JSONArray array = new JSONArray();
		int[] id = new int[1];
		id[0] = 10001;
		list.forEach(rec -> {
			JSONObject object = new JSONObject();
			object.put(Constants.ID, id[0]++);
			object.put(Constants.MODULE_NAME, rec.getModuleName());
			object.put(Constants.TITLE, rec.getHeader());
			object.put("description", rec.getMessage());
			// ?????????FDataobjecthintmessage ???????????? event,?????????????????????
			if (rec.isDisableOpenModule()) {
				// ??????
				object.put(Constants.TYPE, "notification");
			} else {
				// ??????
				object.put(Constants.TYPE, "event");
			}
			object.put(Constants.LEVEL, rec.getHintlevel());
			object.put(AVATAR, rec.getHintlevel());
			object.put(Constants.COUNT, rec.getCount());
			object.put("filterFieldName", rec.getFilterFieldName());
			object.put("filterFieldOperator", rec.getFilterFieldOperator());
			object.put("filterFieldValue", rec.getFilterFieldValue());
			object.put("filterText", rec.getFilterText());
			array.add(object);
		});
		return array;
	}

	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @return
	 */
	public JSONArray getUserNotification() {
		JSONArray array = new JSONArray();
		List<FNotification> notifications = dao.findAll(FNotification.class);
		notifications.sort((n1, n2) -> {
			return n1.getStartdate().before(n2.getStartdate()) ? 1 : -1;
		});
		Set<FUsernotificationresult> userResults = dao.findById(FUser.class, Local.getUserid())
				.getFUsernotificationresults();
		notifications.forEach(notification -> {
			// ??????????????????????????????
			if (notification.getStartdate().before(new Date()) && notification.getEnabled()) {
				boolean add = false;
				// ?????????????????????????????????????????????????????????????????????
				if (notification.getFUsernotifications().size() > 0) {
					for (FUsernotification usernoti : notification.getFUsernotifications()) {
						if (usernoti.getFUser().getUserid().equals(Local.getUserid())) {
							add = true;
							break;
						}
					}
				} else {
					add = true;
				}
				if (add) {
					JSONObject object = new JSONObject();
					object.put(Constants.ID, notification.getNotificationid());
					object.put(Constants.RECORD,
							dataObjectService
									.fetchInfo(FNotification.class.getSimpleName(), notification.getNotificationid())
									.getData());
					object.put(Constants.TITLE, notification.getTitle());
					object.put("description", notification.getContext());
					object.put(Constants.TYPE, "notification");
					object.put(Constants.LEVEL, notification.getHintlevel());
					object.put(Constants.COUNT, 1);
					object.put(Constants.DATETIME, TypeChange.dateTimeToString(notification.getStartdate()));
					object.put("read", false);
					boolean deleted = false;
					for (FUsernotificationresult rec : userResults) {
						if (rec.getFNotification().getNotificationid().equals(notification.getNotificationid())) {
							// ??????????????????????????????????????????
							deleted = rec.getDeletedtime() != null;
							if (rec.getResulttime() != null) {
								object.put("read", true);
								break;
							}
						}
					}
					if (!deleted) {
						array.add(object);
					}
				}
			}
		});
		return array;
	}

	private static final String WEEKDAY = "weekday";
	private static final String MONTHDAY = "monthday";
	private static final String RANDOM = "random";

	public void getBackGround(String type, String themename) throws IOException {
		HttpServletResponse response = Local.getResponse();
		List<FovBackgroundimage> images = dao.findByProperty(FovBackgroundimage.class, "positiontype", type);
		if (images.size() == 0) {
			if (Constants.LOGIN.equalsIgnoreCase(type)) {
				org.springframework.core.io.Resource resource = new ClassPathResource(
						Local.getProjectSpace().getImages() + "loginbg.gif");
				FileUtils.copy(resource.getInputStream(), response.getOutputStream());
			}
		} else {
			// ??????????????????theme
			FovBackgroundimage currimage = null;
			// ?????????????????????theme???
			List<FovBackgroundimage> themeimages = new ArrayList<FovBackgroundimage>();
			for (FovBackgroundimage image : images) {
				if (themename.equalsIgnoreCase(image.getThemename())) {
					themeimages.add(image);
				}
			}
			if (themeimages.size() == 0) {
				// ?????????????????????theme???
				for (FovBackgroundimage image : images) {
					if (StringUtils.isBlank(image.getThemename())) {
						themeimages.add(image);
					}
				}
			}
			if (themeimages.size() == 0)
				return;
			else {
				// ?????????themeimages????????????????????????????????????????????????????????????????????????????????????????????????
				currimage = themeimages.get(0);
				if (WEEKDAY.equals(themeimages.get(0).getUsetype())) {
					// ?????? 1???2???3???4???5???6???7
					currimage = getWeekDayImage(themeimages);
				} else if (MONTHDAY.equals(themeimages.get(0).getUsetype())) {
					// ????????? 1,2,...,30,31
					currimage = getMoneyDayImage(themeimages);
				} else if (RANDOM.equals(themeimages.get(0).getUsetype())) {
					// ????????????
					currimage = getRandomImage(themeimages);
				}
				if (currimage == null) {
					currimage = themeimages.get(0);
				}
			}
			if (StringUtils.isBlank(currimage.getRgbcolor()))
				FileUtils.copy(new ByteArrayInputStream(currimage.getImagefile()), response.getOutputStream());
			else {
				// ???????????????????????????
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
				int[] rgbBytes = CommonUtils.hexString2Ints(currimage.getRgbcolor());
				int rgb = new Color(rgbBytes[0], rgbBytes[1], rgbBytes[2]).getRGB();
				bufferedImage.setRGB(0, 0, rgb);
				ImageIO.write(bufferedImage, Constants.PNG, os);
				FileUtils.copy(new ByteArrayInputStream(os.toByteArray()), response.getOutputStream());
			}
		}

	}

	private FovBackgroundimage getWeekDayImage(List<FovBackgroundimage> themeimages) {
		for (FovBackgroundimage image : themeimages) {
			if (StringUtils.isNotBlank(image.getUsevalue())) {
				String[] days = image.getUsevalue().split(Constants.COMMA);
				Calendar calendar = Calendar.getInstance();
				Integer day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				if (day == 0) {
					day = 7;
				}
				for (String d : days) {
					if (("" + day).equals(d)) {
						return image;
					}
				}
			}
		}
		return null;
	}

	private FovBackgroundimage getMoneyDayImage(List<FovBackgroundimage> themeimages) {
		for (FovBackgroundimage image : themeimages) {
			if (StringUtils.isNotBlank(image.getUsevalue())) {
				String[] days = image.getUsevalue().split(Constants.COMMA);
				Calendar calendar = Calendar.getInstance();
				Integer day = calendar.get(Calendar.DAY_OF_MONTH);
				for (String d : days) {
					if (("" + day).equals(d)) {
						return image;
					}
				}
			}
		}
		return null;
	}

	private FovBackgroundimage getRandomImage(List<FovBackgroundimage> themeimages) {
		Random random = new Random();
		return themeimages.get(random.nextInt(themeimages.size()));
	}

	public ResultBean createPersonnalUser(String personnelid)
			throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, OgnlException {
		FPersonnel person = dao.findById(FPersonnel.class, personnelid);
		if (person.getFUsers().size() > 0) {
			ResultBean result = new ResultBean();
			result.setSuccess(false);
			result.setMessage("?????????????????????????????????,??????????????????????????????????????????");
			return result;
		} else {
			JSONObject object = new JSONObject();
			object.put("FPersonnel.personnelid", personnelid);
			object.put("usertype", "10");
			object.put(Constants.USERCODE, person.getPersonnelcode());
			object.put(Constants.USERNAME, person.getPersonnelname());
			object.put(Constants.COMPANYID, Constants.ZEROZERO);
			object.put("isvalid", true);
			object.put("islocked", false);
			object.put("orgfiltertype", Constants.ZEROZERO);
			object.put(Constants.CREATER, Local.getUserid());
			object.put(Constants.CREATEDATE, new Date());
			// ??????????????????????????????
			object.put("orgfiltertype", "9920");
			return dataObjectService.saveOrUpdate(FUser.class.getSimpleName(), object.toJSONString(), null,
					Constants.NEW);
		}
	}

	/**
	 * ???????????????????????????????????????????????????Logic?????????????????????PopupMessageInterface?????????????????????????????????????????????
	 * 
	 * @return
	 */
	public ActionResult getPopupMessage() {
		ApplicationContext context = AppContextAware.getApplicationContext();
		List<PopupMessage> list = new ArrayList<PopupMessage>();
		// ?????????f_dataobjecthintmessage????????????
		List<FDataobjecthintmessage> hintMessages = dao.findAll(FDataobjecthintmessage.class);
		int tag = -1;
		// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		for (FDataobjecthintmessage message : hintMessages) {
			if (message.isEnabled() && message.inDate()) {
				// ????????????????????????????????????
				if (message.getFUserdataobjecthintmessages().size() > 0) {
					boolean found = false;
					for (FUserdataobjecthintmessage userhint : message.getFUserdataobjecthintmessages()) {
						if (userhint.getFUser().getUserid().equals(Local.getUserid())) {
							// ??????????????????????????????
							found = true;
							break;
						}
					}
					// ??????????????????????????????????????????
					if (!found) {
						continue;
					}
				}
				PopupMessage popupMessage = new PopupMessage();
				popupMessage.setHintlevel(message.getHintlevel());
				popupMessage.setHeader(message.getMessagetitle());
				popupMessage.setOrderno(message.getOrderno());
				popupMessage.setDisableOpenModule(!message.isAllowopenmodule());
				tag++;
				if (message.getFDataobject() == null) {
					popupMessage.setMessage(message.getMessagetpl());
				} else {
					popupMessage.setModuleName(message.getFDataobject().getObjectname());
					String msg = null;
					try {
						msg = getPopupMessage(message, popupMessage);
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
					popupMessage.setMessage(msg);
					popupMessage.setFilterFieldName(message.getFilterfieldname());
					popupMessage.setFilterFieldOperator(StringUtils.isBlank(message.getFieldfieldoperator()) ? "="
							: message.getFieldfieldoperator());
					popupMessage.setFilterFieldValue(message.getFilterfieldvalue());
					popupMessage.setFilterText(message.getFiltertext());
				}
				if (StringUtils.isNotBlank(popupMessage.getMessage())) {
					list.add(popupMessage);
				}
			}
		}
		String[] popupClassNames = context.getBeanNamesForType(PopupMessageInterface.class);
		for (String beanName : popupClassNames) {
			tag++;
			try {
				PopupMessage message = ((PopupMessageInterface) context.getBean(beanName)).getPopupMessage();
				if (message != null) {
					list.add(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				PopupMessage errorMessage = new PopupMessage();
				errorMessage.setOrderno(-1);
				errorMessage.setHintlevel("error");
				errorMessage.setMessage(beanName + "???????????????????????????????????????<br/>?????????" + e.getMessage());
				list.add(errorMessage);
			}
		}
		list.sort(new Comparator<PopupMessage>() {
			@Override
			public int compare(PopupMessage o1, PopupMessage o2) {
				return o1.getOrderno() - o2.getOrderno();
			}
		});

		ActionResult result = new ActionResult();
		result.setSuccess(true);
		result.setMsg(list);
		// ????????????tag???-1????????????????????????????????????popupMessage ??????????????????????????????????????????
		result.setTag(tag);
		return result;
	}

	/**
	 * ??????hintmessage??????????????????????????????????????????????????????????????????null?????????????????????
	 * 
	 * @param hintMessage
	 * @return
	 */
	private String getPopupMessage(FDataobjecthintmessage hintMessage, PopupMessage popupMessage) {
		FDataobject dataobject = hintMessage.getFDataobject();
		// ???????????????????????????????????????????????????
		if (dataobject == null || !ObjectFunctionUtils.allowQuery(dataobject)) {
			return null;
		}
		List<UserDefineFilter> filters = new ArrayList<UserDefineFilter>();
		UserDefineFilter filter = new UserDefineFilter();
		filter.setProperty(hintMessage.getFilterfieldname());
		filter.setOperator(
				StringUtils.isBlank(hintMessage.getFieldfieldoperator()) ? "=" : hintMessage.getFieldfieldoperator());
		filter.setValue(hintMessage.getFilterfieldvalue());
		filters.add(filter);
		SqlGenerate generate = new SqlGenerate();
		generate.setDataobject(dataobject);
		generate.setUserDefineFilters(filters);
		generate.disableAllBaseFields();
		generate.setDisableOrder(true);
		generate.pretreatment();
		SqlField sqlfield = new SqlField("count_", "count(*)", null);
		generate.getSelectfields().add(sqlfield);
		// ???hintMessage??????????????????????????????,??????{amount:sum(t_.tf_planamount/10000)}??????????????????
		// sqlfield = new SqlField("sumPlanAmount", "sum(t_.tf_planamount)", null);
		// generate.getSelectfields().add(sqlfield);
		String regex = "\\{.*?\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(hintMessage.getMessagetpl());
		StringBuffer resultTplBuffer = new StringBuffer();
		while (matcher.find()) {
			String s = matcher.group();
			// {count_} ?????????????????????
			if (s.indexOf(':') > -1) {
				s = s.substring(1, s.length() - 1);
				String[] parts = s.split(":");
				sqlfield = new SqlField(parts[0], parts[1], null);
				generate.getSelectfields().add(sqlfield);
				// {amount:sum(t_.tf_planamount/10000)}???????????? {amount}
				matcher.appendReplacement(resultTplBuffer, "{" + parts[0] + "}");
			}
		}
		matcher.appendTail(resultTplBuffer);
		String sql = generate.generateSelect();
		String[] fields = generate.getFieldNames();
		PageInfo<Map<String, Object>> pageInfo = dao.executeSQLQueryPage(sql, fields, 0, 1, 1, new Object[] {});
		Map<String, Object> data = pageInfo.getData().get(0);
		Integer count = Integer.parseInt(data.get("count_").toString());
		if (count == 0) {
			return null;
		}
		popupMessage.setCount(count);
		matcher = pattern.matcher(resultTplBuffer.toString());
		StringBuffer resultBuffer = new StringBuffer();
		while (matcher.find()) {
			String s = matcher.group();
			Object value = data.get(s.substring(1, s.length() - 1));
			if (value == null) {
				value = "0";
			} else {
				if (value instanceof Double) {
					value = TypeChange.doubletoString((Double) value);
				}
			}
			matcher.appendReplacement(resultBuffer, value.toString());
		}
		matcher.appendTail(resultBuffer);
		return resultBuffer.toString();
	}

	private void setProperies(Properties properties, Map<String, Object> data, String name) {
		if (data.containsKey(name)) {
			Object value = data.get(name);
			if (value == null) {
				properties.remove(name);
			} else {
				properties.setProperty(name, (String) value);
			}
		}
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	final Base64.Decoder decoder = Base64.getDecoder();
	private final String AVATAR = "avatar";
	private final String PHOTO = "photo";

	public ActionResult savePersonnelInfo(Map<String, Object> data)
			throws UnsupportedEncodingException, IOException, IllegalAccessException, InvocationTargetException {
		FUser user = dao.findById(FUser.class, Local.getUserid());
		FPersonnel personnel = user.getFPersonnel();
		if (data.containsKey(AVATAR)) {
			if (data.get(AVATAR) == null) {
				personnel.setFavicon(null);
			} else {
				personnel.setFavicon(decoder.decode(data.get(AVATAR).toString()));
			}
		}
		if (data.containsKey(PHOTO)) {
			if (data.get(PHOTO) == null) {
				personnel.setPhoto(null);
			} else {
				personnel.setPhoto(decoder.decode(data.get(PHOTO).toString()));
			}
		}
		if (data.containsKey(Constants.EMAIL)) {
			personnel.setEmail((String) data.get(Constants.EMAIL));
		}
		if (data.containsKey(Constants.MOBILE)) {
			personnel.setMobile((String) data.get(Constants.MOBILE));
		}
		if (data.containsKey(Constants.OFFICETEL)) {
			personnel.setOfficetel((String) data.get(Constants.OFFICETEL));
		}
		if (data.containsKey(Constants.PROFILE)) {
			personnel.setRemark((String) data.get(Constants.PROFILE));
		}

		Properties properties = CommonUtils.getPropertiesFromString(personnel.getAdditionstr5());
		OutputStream stream = new ByteArrayOutputStream();
		setProperies(properties, data, "country");
		setProperies(properties, data, "province");
		setProperies(properties, data, "city");
		setProperies(properties, data, "street");
		properties.store(new OutputStreamWriter(stream, "utf-8"), null);
		personnel.setAdditionstr5(stream.toString());
		dao.update(personnel);
		return new ActionResult();
	}

	public ActionResult notificationRead(String notificationId) {
		FUsernotificationresult bean = dao.findByPropertyFirst(FUsernotificationresult.class, "FUser.userid",
				Local.getUserid(), "FNotification.notificationid", notificationId);
		if (bean == null) {
			bean = new FUsernotificationresult();
			bean.setFUser(dao.findById(FUser.class, Local.getUserid()));
			bean.setFNotification(dao.findById(FNotification.class, notificationId));
			bean.setResulttime(DateUtils.getTimestamp());
			dao.save(bean);
		} else {
			bean.setResulttime(DateUtils.getTimestamp());
			dao.saveOrUpdate(bean);
		}
		return new ActionResult();
	}

	public ActionResult notificationClear(String deleteds) {
		// ????????????????????????
		String[] items = deleteds.split(",");
		for (String item : items) {
			FUsernotificationresult bean = dao.findByPropertyFirst(FUsernotificationresult.class, "FUser.userid",
					Local.getUserid(), "FNotification.notificationid", item);
			if (bean == null) {
				bean = new FUsernotificationresult();
				bean.setFUser(dao.findById(FUser.class, Local.getUserid()));
				bean.setFNotification(dao.findById(FNotification.class, item));
				dao.save(bean);
			}
		}
		Set<FUsernotificationresult> userResults = dao.findById(FUser.class, Local.getUserid())
				.getFUsernotificationresults();
		userResults.forEach(rec -> {
			boolean contain = ArrayUtils.contains(items, rec.getFNotification().getNotificationid());
			if (contain && rec.getDeletedtime() == null) {
				rec.setDeletedtime(DateUtils.getTimestamp());
				dao.saveOrUpdate(rec);
			}
		});
		return new ActionResult();
	}

	public ActionResult notificationRemove(String notificationId) {
		FUsernotificationresult bean = dao.findByPropertyFirst(FUsernotificationresult.class, "FUser.userid",
				Local.getUserid(), "FNotification.notificationid", notificationId);
		// ?????????????????????
		if (bean == null) {
			bean = new FUsernotificationresult();
			bean.setFUser(dao.findById(FUser.class, Local.getUserid()));
			bean.setFNotification(dao.findById(FNotification.class, notificationId));
			bean.setResulttime(DateUtils.getTimestamp());
			dao.save(bean);
		}
		bean.setDeletedtime(DateUtils.getTimestamp());
		dao.saveOrUpdate(bean);
		return new ActionResult();
	}

	/**
	 * ?????????????????????????????????
	 */
	public ActionResult notificationReload() {
		List<FUsernotificationresult> results = dao.findByProperty(FUsernotificationresult.class, "FUser.userid",
				Local.getUserid());
		results.forEach(result -> {
			if (result.getDeletedtime() != null) {
				result.setDeletedtime(null);
				dao.saveOrUpdate(result);
			}
		});
		return new ActionResult();
	}

}
