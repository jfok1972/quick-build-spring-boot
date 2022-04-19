package com.jhopesoft.platform.logic;

import java.util.Map;
import java.util.UUID;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.core.annotation.Module.Type;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.framework.exception.DataDeleteException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.MD5;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Module
public class FUserLogic {

	@Module(type = Type.newDataBefore)
	public void newDataBefore(FUser bean) {
		bean.setPassword(Constants.DEFAULT_PASSWORD);
		bean.setAdditionstr5("弱");
		bean.setSalt(UUID.randomUUID().toString());
		bean.setPassword(MD5.MD5Encode(bean.getPassword() + bean.getSalt()));
	}

	@Module(type = Type.newDefaultData)
	public void newDefaultData(Map<String, Object> map, Dao dao) {
		map.put("password", Constants.DEFAULT_PASSWORD);
	}

	// activiti的 identity 和 group 不要同步了，用不到

	@Module(type = Type.newDataAfter)
	public void newDataAfter(FUser bean) {
		UserEntity ue = new UserEntity(bean.getUserid());
		ue.setFirstName(bean.getUsercode());
		// identityService.saveUser(bean.getUserid());
	}

	@Module(type = Type.deleteDataBefore)
	public void deleteDataBefore(FUser bean) {
		if (Constants.ADMIN.equals(bean.getUsercode()) || Constants.ADMINISTRATOR.equals(bean.getUsercode())) {
			throw new DataDeleteException("不能删除用户超级管理员和系统管理员！");
		}
	}

	@Module(type = Type.deleteDataAfter)
	public void deleteDataAfter(FUser bean) {
		// identityService.deleteUser(bean.getUserid());
	}
}
