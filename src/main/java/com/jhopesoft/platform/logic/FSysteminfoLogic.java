package com.jhopesoft.platform.logic;

import org.springframework.stereotype.Component;

import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.platform.logic.define.AbstractBaseLogic;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Component
public class FSysteminfoLogic extends AbstractBaseLogic<FSysteminfo> {

	@Override
	public void beforeInsert(FSysteminfo inserted) {

		if (inserted.getSessiontimeoutminute() > Constants.INT_24 * Constants.INT_60) {
			throw new DataUpdateException("sessiontimeoutminute",
					"超时时间最大值为：" + Constants.INT_24 * Constants.INT_60 + " 分钟！");
		}

		super.beforeInsert(inserted);
	}

	@Override
	public void beforeUpdate(String type, FSysteminfo updatedObject, FSysteminfo oldObject) {

		if (updatedObject.getSessiontimeoutminute() > Constants.INT_24 * Constants.INT_60) {
			throw new DataUpdateException("sessiontimeoutminute",
					"超时时间最大值为：" + Constants.INT_24 * Constants.INT_60 + " 分钟！");
		}
		super.beforeUpdate(type, updatedObject, oldObject);
	}

}
