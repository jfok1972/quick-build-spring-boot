package com.jhopesoft.platform.logic;

import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.viewsetting.FovDataobjectwidget;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFilterscheme;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.platform.logic.define.AbstractBaseLogic;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Module
public class FovDataobjectwidgetLogic extends AbstractBaseLogic<FovDataobjectwidget> {

    @Autowired
    private DaoImpl dao;

    @Override
    public void beforeInsert(FovDataobjectwidget inserted) {
        check(inserted);
        super.beforeInsert(inserted);
    }

    @Override
    public void beforeUpdate(String type, FovDataobjectwidget updatedObject, FovDataobjectwidget oldObject) {
        check(updatedObject);
        super.beforeUpdate(type, updatedObject, oldObject);
    }

    public void check(FovDataobjectwidget widget) {
        if (widget.getFovFilterscheme() != null && widget.getFDataobject() != null) {
            FovFilterscheme filterscheme = dao.findById(FovFilterscheme.class,
                    widget.getFovFilterscheme().getFilterschemeid());
            if (!(filterscheme.getFDataobject().getObjectid().equals(widget.getFDataobject().getObjectid()))) {
                throw new DataUpdateException("fovFilterscheme", "必须选择当前实体对象的筛选方案");
            }
        }
    }

}
