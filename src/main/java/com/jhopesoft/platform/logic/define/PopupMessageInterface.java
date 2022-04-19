package com.jhopesoft.platform.logic.define;

import com.jhopesoft.framework.bean.PopupMessage;

/**
 * 模块具有弹出式提示，用于生成弹出式提示的文字，以及处理方式
 * 
 * 所有具有此接口的Logic的类，在用户提交getPopupMessage时都会获取相应的信息
 * 
 * @author jiangfeng
 *
 */
public interface PopupMessageInterface {

	/**
	 * getPopupMessage
	 * 
	 * @return
	 */
	public PopupMessage getPopupMessage();

}
