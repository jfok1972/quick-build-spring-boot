package com.jhopesoft.platform.logic;

import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.module.FCompanymenu;
import com.jhopesoft.framework.dao.entity.module.FCompanymodule;
import com.jhopesoft.framework.dao.entity.viewsetting.FovHomepagescheme;
import com.jhopesoft.platform.logic.define.AbstractBaseLogic;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Module
public class FovHomepageschemeLogic extends AbstractBaseLogic<FovHomepagescheme> {

    /**
     * 更改过 工作台方案名称之后，在角色权限里也更新一下
     */
    @Override
    public void afterUpdate(String type, FovHomepagescheme updatedObject, FovHomepagescheme oldObject) {
        if (!updatedObject.getSchemename().equals(oldObject.getSchemename())) {
            updatedObject.getFModules().forEach(module -> {
                module.setModulename(updatedObject.getSchemename());
                Local.getDao().update(module);
                for (FCompanymodule cm : module.getFCompanymodules()) {
					for (FCompanymenu menu : cm.getFCompanymenus()) {
						menu.setMenuname(updatedObject.getSchemename());
						Local.getDao().update(menu);
					}
				}
            });
        }
        super.beforeUpdate(type, updatedObject, oldObject);
    }

}
