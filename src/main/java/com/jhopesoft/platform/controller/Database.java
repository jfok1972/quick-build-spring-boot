package com.jhopesoft.platform.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.jhopesoft.framework.bean.ActionResult;
import com.jhopesoft.framework.bean.TableFieldBean;
import com.jhopesoft.framework.bean.TreeValueText;
import com.jhopesoft.framework.core.annotation.SystemLogs;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.platform.service.DatabaseService;

/**
 * 
 * @author jiangfeng
 *
 */
@RestController
@RequestMapping("/platform/database")
public class Database {

	@Resource
	private DatabaseService databaseService;

	@RequestMapping(value = "/createuserview.do")

	public ActionResult createUserView(String viewid) {
		return databaseService.createUserView(viewid);
	}

	@RequestMapping(value = "/dropuserview.do")

	public ActionResult dropUserView(String viewid) {
		return databaseService.dropUserView(viewid);
	}

	@SystemLogs("数据库schema信息查询")
	@RequestMapping(value = "/getschemas.do")

	public JSONArray getSchemas() throws SQLException {
		return databaseService.getSchemas();
	}

	@SystemLogs("数据库表和视图信息查询")
	@RequestMapping(value = "/getnotimporttableview.do")

	public TreeValueText getNotImportTableAndViews(String schema) throws SQLException {
		return databaseService.getNotImportTableAndViews(schema);
	}

	@SystemLogs("数据库表和视图的字段查询")
	@RequestMapping(value = "/getfields.do")

	public List<TableFieldBean> getFields(String schema, String tablename) {
		return databaseService.getFields(schema == null || schema.length() == 0 ? null : schema, tablename);
	}

	@SystemLogs("数据库表和视图的字段查询")
	@RequestMapping(value = "/importtableorview.do")

	public ActionResult importTableOrView(String schema, String tablename, String title, String namefield,
			String fields, boolean addtoadmin, boolean addtomenu, String objectgroup) throws Exception {
		return databaseService.importTableOrView(schema == null || schema.length() == 0 ? null : schema, tablename,
				title, namefield, objectgroup, fields, addtoadmin, addtomenu, null);
	}

	@SystemLogs("刷新一个表的字段，只会加入新建的字段")
	@RequestMapping(value = "/refreshtablefields.do")

	public ActionResult refreshTableFields(String objectid) throws IllegalAccessException, InvocationTargetException {
		return databaseService.refreshTableFields(objectid);
	}

	@SystemLogs("打包下载当前选中数据库的Java Bean文件。")
	@RequestMapping(value = "downloadbeanfiles.do")
	public void downLoadBeanFiles(String schemeName) throws IOException {
		Connection connection = Local.getDao().getConnection();
		try {
			if (StringUtils.isBlank(schemeName)) {
				schemeName = connection.getCatalog();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		databaseService.downLoadBeanFiles(schemeName);
	}
}
