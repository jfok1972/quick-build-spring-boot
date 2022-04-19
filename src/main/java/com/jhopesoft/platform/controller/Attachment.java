package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.FileUploadBean;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.platform.service.AttachmentService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/attachment")
public class Attachment {

	@Autowired
	private AttachmentService attachmentService;

	@RequestMapping(value = "/upload.do")
	
	public ActionResult upload(FileUploadBean uploaditem, BindingResult bindingResult, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject object = null;
		try {
			object = attachmentService.upload(uploaditem, bindingResult);
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(500);
			return new ActionResult(false, "附件文件保存时，文件系统错误!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			response.setStatus(500);
			return new ActionResult(false, e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			response.setStatus(500);
			return new ActionResult(false, e.getMessage());
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.setStatus(500);
			return new ActionResult(false, CommonUtils.getThrowableOriginalMessage(e));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
			return new ActionResult(false, e.getMessage());
		}
		ActionResult result = new ActionResult();
		result.setMsg(object);
		return result;
	}

	/**
	 * 附件文件下载后供预览，如果是可以显示原件的，则下载原件，如果是可以转换成pdf的，则下载pdf
	 * 
	 * @param attachmentid
	 */
	@RequestMapping(value = "/preview.do")
	
	public void preview(String attachmentid) {
		try {
			attachmentService.preview(attachmentid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片文件的缩略图
	 * 
	 * @param attachmentid
	 */
	@RequestMapping(value = "/thumbnail.do")
	
	public void thumbnail(String attachmentid) {
		try {
			attachmentService.thumbnail(attachmentid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载附件原件
	 * 
	 * @param attachmentid
	 */
	@RequestMapping(value = "/download.do")
	
	public void download(String attachmentid) {
		try {
			attachmentService.download(attachmentid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("downloadall.do")
	
	public void downloadAll(String moduleName, String idkey) {
		try {
			attachmentService.downloadAll(moduleName, idkey);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
