package com.jhopesoft.platform.logic;

import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.system.FUser;
import com.jhopesoft.platform.logic.define.AbstractBaseLogic;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Module
public class FDataobjectviewLogic extends AbstractBaseLogic<FDataobjectview> {

    @Override
    public void beforeInsert(FDataobjectview inserted) {
        // 给视图对象加上用户，这样title就不能重复也
        inserted.setFUser(Local.getDao().findById(FUser.class, Local.getUserid()));
        super.beforeInsert(inserted);
    }

}
