package com.jhopesoft.platform.logic.define;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 *
 */

public interface AuditingInterface {

	/**
	 * 记录审核后执行的操作
	 * 
	 * @param recordId
	 * @param attachment
	 */
	public void afterAuditing(String recordId);

	/**
	 * 记录取消审核后执行的操作
	 * 
	 * @param recordId
	 * @param attachment
	 */
	public void afterCancelAuditing(String recordId);

}
