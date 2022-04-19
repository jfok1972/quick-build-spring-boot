package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.bean.TreeNode;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.dao.SqlMapperAdapter;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.exception.ProjectException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ProjectUtils;
import com.jhopesoft.framework.utils.TreeBuilder;
import com.jhopesoft.platform.service.SystemFrameService;
import com.jhopesoft.framework.bean.UploadFileBean;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/systemframe")
public class SystemFrame extends SqlMapperAdapter {

	@Resource
	private SystemFrameService service;

	@SystemLogs("获取当需要提醒的所有模块的信息")
	@RequestMapping(value = "/getpopupmessage.do")

	public ActionResult getPopupMessage() {
		return service.getPopupMessage();
	}

	@SystemLogs("获取当需要审批处理的任务模块及个数，可处理，可拾取")
	@RequestMapping(value = "/getcanapproveinfo.do")

	public JSONArray getCanApproveInfo() {
		return service.getCanApproveInfo();
	}

	@SystemLogs("获取当需要审核的模块及个数")
	@RequestMapping(value = "/getcanauditinfo.do")

	public JSONArray getCanAuditInfo() {
		return service.getCanAuditInfo();
	}

	@SystemLogs("获取当需要处理的任务模块及个数，可处理，可拾取,以及所有的问题和提示")
	@RequestMapping(value = "/getapprovequestioninfo.do")

	public JSONArray getApproveAndQuestionInfo() {
		JSONArray result = service.getCanApproveInfo();
		// 加入所有可审核的待办
		service.getCanAuditInfo().forEach(audit -> {
			result.add(audit);
		});
		// 加入所有模块里定义的待处理
		service.getQuestionAndMessage().forEach(question -> {
			result.add(question);
		});
		// 加入所有的通知消息
		service.getUserNotification().forEach(notification -> {
			result.add(notification);
		});
		return result;
	}

	@SystemLogs("获取当需要处理的任务的个数")
	@RequestMapping(value = "/gethintmessagecount.do")

	public ActionResult getHintMessageCount() {
		return service.getHintMessageCount();
	}

	@SystemLogs("获取系统菜单")
	@RequestMapping(value = "/getmenutree.do")

	public List<TreeNode> getMenuTree() {
		return TreeBuilder.buildListToTree(service.getMenuTree());
	}

	@RequestMapping(value = "/getuserfavicon.do")
	public void getUserFavicon(HttpServletRequest request, HttpServletResponse response, String userid)
			throws IOException {
		service.getUserFavicon(userid);
	}

	/**
	 * 用户在form中选择了一个上传的图片以后，需要把图像的内容再返回给客户端，使其可以生成一个临时的图像，显示在img中
	 * 
	 * @param uploadExcelBean
	 * @param bindingResult
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/uploadimagefileandreturn.do")
	public ResponseEntity<Map<String, Object>> uploadImageFileAndReturn(UploadFileBean uploadExcelBean,
			BindingResult bindingResult, HttpServletRequest request) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>(0);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		ActionResult ar = service.uploadImageFileAndReturn(uploadExcelBean, bindingResult, request);
		map.put("success", ar.getSuccess());
		map.put("msg", ar.getMsg());
		return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/resetpassword.do", method = RequestMethod.POST)

	public ActionResult resetPassword(String userid) {
		return service.resetPassword(userid);
	}

	@RequestMapping(value = "/changepassword.do", method = RequestMethod.POST)

	public ActionResult changePassword(String oldPassword, String newPassword, String strong) {
		return service.changePassword(oldPassword, newPassword, strong);
	}

	/**
	 * 
	 * 取得当前登录用户的信息，用于quick-build-antp的使用
	 * 
	 * @return
	 */
	@RequestMapping(value = "/currentuser.do")

	public JSONObject getCurrentUser() {
		return service.getCurrentUser();
	}

	/**
	 * 在个人设置中，保存员工个人信息
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/savepersonnelinfo.do")

	public ActionResult savePersonnelInfo(String data) throws IOException {
		Map<String, Object> infoData = JSON.parseObject(data);
		ActionResult result = new ActionResult();
		try {
			result = service.savePersonnelInfo(infoData);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg(e.getCause().getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/createpersonnaluser.do", method = RequestMethod.POST)
	public ResultBean createPersonnalUser(String personnelid) {
		String objectname = FUser.class.getSimpleName();
		ResultBean result = new ResultBean();
		try {
			result = service.createPersonnalUser(personnelid);
		} catch (DataUpdateException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setData(e.getErrorMessage());
			result.setMessage(e.getMessage());
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
			result = ProjectUtils.getErrorMassage(e, dataObject, dao, getSf());
			result.setMessage("当前人员自动生成时，用户名已经存在，请去用户模块中添加！");
		} catch (PersistenceException e) {
			e.printStackTrace();
			FDataobject dataObject = DataObjectUtils.getDataObject(objectname);
			result = ProjectUtils.getErrorMassage(e, dataObject, dao, getSf());
			result.setMessage("当前人员自动生成时，用户名已经存在，请去用户模块中添加！");
		} catch (ProjectException e) {
			e.printStackTrace();
			Throwable original = e.getOriginalThrowable();
			if (original.getClass().equals(DataUpdateException.class)) {
				result.setSuccess(false);
				result.setData(((DataUpdateException) original).getErrorMessage());
				result.setMessage(original.getMessage());
			} else {
				result.setSuccess(false);
				result.setMessage(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		if (result.isSuccess()) {
			@SuppressWarnings("unchecked")
			Map<String, Object> userobject = (Map<String, Object>) result.getData();
			String msg = "用户登录名：" + userobject.get(Constants.USERCODE);
			msg += "<br/>用户名称：" + userobject.get(Constants.USERNAME);
			msg += "<br/>初始密码：123456";
			result.setMessage(msg);
		}
		return result;
	}

	/**
	 * 当前用户阅读了一个通知
	 * 
	 * @param notificationId
	 * @return
	 */
	@RequestMapping(value = "notificationread.do")
	public ActionResult notificationRead(String notificationId) {
		return service.notificationRead(notificationId);
	}

	/**
	 * 当前用户删除了一个通知
	 * 
	 * @param notificationId
	 * @return
	 */
	@RequestMapping(value = "notificationremove.do")
	public ActionResult notificationRemove(String notificationId) {
		return service.notificationRemove(notificationId);
	}

	/**
	 * 当前用户想要查看所有的消息，把删除标记全部去掉
	 * 
	 * @param notificationId
	 * @return
	 */
	@RequestMapping(value = "notificationreload.do")
	public ActionResult notificationReload() {
		return service.notificationReload();
	}

	/**
	 * 当前用户清除了所有通知
	 * 
	 * @param notificationId
	 * @return
	 */
	@RequestMapping(value = "notificationclear.do")
	public ActionResult notificationClear(String deleteds) {
		if (StringUtils.isNotBlank(deleteds)) {
			return service.notificationClear(deleteds);
		}
		return new ActionResult();
	}

}
