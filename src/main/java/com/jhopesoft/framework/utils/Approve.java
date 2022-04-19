package com.jhopesoft.framework.utils;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ognl.OgnlException;

/**
 *
 * @author 蒋锋
 * 
 */

public class Approve {

	private static final String PROCESSDATE = "processdate";
	private static final String PROCESSRESULT = "processresult";
	private static final String PROCESSTITLE = "processtitle";

	/**
	 * 
	 * 返回某一个审批流程中所有的审批信息，由于审批可以退回，如果发现有二次审批，则把审批信息都加进去，并且加上日期。最后一个不加日期
	 * 
	 * @param bean       记录map值
	 * @param department 任务名称
	 * @param property   username ---- 审核人员名称; taskname ---- 任务名称; processdate ----
	 *                   审核日期; processtitle ---- 审批意见; processresult ---- 审批结果
	 * @return 指定审批的字段值
	 * 
	 *         调用方法
	 *         {@com.jhopesoft.framework.utils.Approve@getInfo(#this,'任务name','返回的属性名称')}
	 *         {@com.jhopesoft.framework.utils.Approve@getInfo(#this,'任务name','processtitle')}
	 *         {@com.jhopesoft.framework.utils.Approve@getInfo(#this,'任务name','username')}
	 *         {@com.jhopesoft.framework.utils.Approve@getInfo(#this,'任务name','processdate')}
	 *         {@com.jhopesoft.framework.utils.Approve@getInfo(#this,'任务name','processresult')}
	 * @throws Exception
	 * 
	 */
	public static Object getInfo(Object bean, String department, String property) throws Exception {
		return getInfo(bean, department, property, false);
	}

	/**
	 * 返回某一个审批流程中所有的审批信息
	 * 
	 * @param bean
	 * @param department
	 * @param property
	 * @param addResult  为true,表示在审批意见后面加上审批结果
	 * @return
	 * @throws Exception
	 */
	public static Object getInfo(Object bean, String department, String property, boolean addResult) throws Exception {
		if (!(bean instanceof Map)) {
			throw new OgnlException("不是一个map对象");
		}
		@SuppressWarnings("rawtypes")
		JSONArray array = (JSONArray) ((Map) bean).get("actCompleteTaskInfo");
		if (array == null) {
			return null;
		}
		JSONArray thisArray = new JSONArray();
		for (Object o : array) {
			JSONObject object = (JSONObject) o;
			if (department.equals(object.getString("taskname"))) {
				thisArray.add(object);
			}
		}
		if (thisArray.size() == 0) {
			return null;
		}
		// 获取人员的审批人员，审批日期，审批结果，只取最后一个的
		if (Constants.USERNAME.equals(property) || PROCESSDATE.equals(property) || PROCESSRESULT.equals(property)
				|| thisArray.size() == 1) {
			JSONObject o = (JSONObject) thisArray.get(thisArray.size() - 1);
			String str = o.get(property) != null ? o.getString(property) : "";
			if (PROCESSDATE.equals(property) && str != null) {
				str = str.substring(0, 10);
			} else if (PROCESSTITLE.equals(property) && addResult) {
				str += "（" + o.getString(PROCESSRESULT) + "）";
			}
			return str;
		}
		// 当前人员审批过二次以后，第一次意见(审批结果，日期)
		// 第二次意见(审批结果)
		String result = "";
		if (PROCESSTITLE.equals(property)) {
			for (int i = 0; i < thisArray.size() - 1; i++) {
				JSONObject o = (JSONObject) thisArray.get(i);
				result = result + ((o.get(property) != null ? o.getString(property) : "") + "（"
						+ o.getString(PROCESSRESULT) + "，" + o.getString(PROCESSDATE).substring(0, 10) + "）；\r\n");
			}
			JSONObject o = (JSONObject) thisArray.get(thisArray.size() - 1);
			result = result + (o.get(property) != null ? o.getString(property) : "") + "（" + o.getString(PROCESSRESULT)
					+ "）";
			return result;
		}
		return null;
	}

}
