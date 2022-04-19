package com.jhopesoft.framework.core.objectquery.module;

import java.util.Set;
import com.jhopesoft.framework.core.objectquery.sqlfield.AdditionParentModuleField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.DataObjectUtils;

/**
 * 
 * @author jiangfeng
 *
 */
public class ModuleOnlyHierarchyGenerate {

  public static final String CHILDSEPARATOR = ".with";

  public static BaseModule genModuleHierarchy(FDataobject module) {
    BaseModule baseModule = new BaseModule();
    baseModule.setModule(module);
    baseModule.setAsName("this_");
    int pcount = 1;
    for (FDataobjectfield field : module.getFDataobjectfields()) {
      if (field._isManyToOne() || field._isOneToOne()) {
        FDataobject pmodule = DataObjectUtils.getDataObject(field.getFieldtype());
        String parentPath = pmodule.getObjectname();
        baseModule.getParents().put(field.getFieldname(), genModuleParentHierarchy(baseModule, baseModule, field,
            pmodule, pcount++, 1, field.getFieldtitle(), parentPath, module.getObjectname()));
      }
    }
    for (String p : baseModule.getParents().keySet()) {
      baseModule.getParents().get(p).setIsDirectParent(true);
    }
    baseModule.calcParentModuleOnlyone();
    Set<FDataobjectfield> manytooneFields = DataObjectUtils.getDataObjectManyToOneField(module.getObjectname());
    if (manytooneFields != null) {
      for (FDataobjectfield field : manytooneFields) {
        String childPath = field.getFDataobject().getObjectname() + CHILDSEPARATOR + "." + field.getFieldname();
        baseModule.getChilds().put(childPath,
            genModuleChildHierarchy(baseModule, baseModule, field, field.getFDataobject(), 1,
                field.getFDataobject().getTitle() + "(" + field.getFieldtitle() + ")", childPath));
      }
    }
    return baseModule;
  }

  public static ParentModule genModuleParentHierarchy(BaseModule baseModule, Object sonModule, FDataobjectfield field,
      FDataobject pModule, int pcount, int level, String fullname, String parentPath, String proviousPath) {
    String proviousAs;
    String proviousAheadField;
    if (sonModule instanceof ParentModule) {
      proviousAs = ((ParentModule) sonModule).getAsName();
      proviousAheadField = ((ParentModule) sonModule).getFieldahead();
    } else {
      proviousAs = ((BaseModule) sonModule).getAsName();
      proviousAheadField = "";
    }
    ParentModule pm = new ParentModule();
    pm.setSonModuleHierarchy(sonModule);
    pm.setAddToFromByFields(sonModule instanceof BaseModule);
    pm.setAddToFromByFilter(false);
    pm.setModule(pModule);
    pm.setModuleField(field);
    pm.setLevel(level);
    pm.setModulePath(parentPath);
    pm.setNamePath(fullname);
    pm.setOnlyonename(field.getFieldtitle());
    pm.setFieldahead(proviousAheadField + (proviousAheadField.length() == 0 ? "" : ".") + field.getFieldname());
    pm.setAsName(proviousAs + "_" + level + "" + pcount);
    baseModule.getAllParents().put(pm.getFieldahead(), pm);
    pm.setLeftoutterjoin(String.format(" left outer join %s %s on %s = %s", pModule._getTablename(), pm.getAsName(),
        pModule._getPrimaryKeyField()._getSelectName(pm.getAsName()), proviousAs + "." + field.getFielddbname()));
    pm.setPrimarykeyField(new AdditionParentModuleField(pm.getFieldahead() + "." + pModule.getPrimarykey(),
        pModule._getPrimaryKeyField()._getSelectName(pm.getAsName())));
    pm.setNameField(new AdditionParentModuleField(pm.getFieldahead() + "." + pModule.getNamefield(),
        pModule._getNameField()._getSelectName(pm.getAsName())));
    pcount = 1;
    for (FDataobjectfield mfield : pModule.getFDataobjectfields()) {
      if (mfield._isManyToOne() || mfield._isOneToOne()) {
        FDataobject pmodule = DataObjectUtils.getDataObject(mfield.getFieldtype());
        String parentPath1 = parentPath + "--" + pmodule.getObjectname();
        pm.getParents().put(pm.getFieldahead() + "." + mfield.getFieldname(), genModuleParentHierarchy(baseModule, pm,
            mfield, pmodule, pcount++, level + 1, fullname + "--" + mfield.getFieldtitle(), parentPath1, parentPath));
      }
    }
    return pm;
  }

  public static ChildModule genModuleChildHierarchy(BaseModule baseModule, Object parentModule, FDataobjectfield field,
      FDataobject pModule, int level, String fullname, String childPath) {
    int maxLevel = 10;
    if (level == maxLevel) {
      return null;
    }
    ChildModule cm = new ChildModule();
    cm.setParentModuleHierarchy(parentModule);
    cm.setModule(pModule);
    cm.setModuleField(field);
    cm.setLevel(level);
    cm.setModulePath(childPath);
    cm.setFieldahead(childPath);
    cm.setNamePath(fullname);
    baseModule.getAllChilds().put(cm.getFieldahead(), cm);
    for (FDataobjectfield f : DataObjectUtils.getDataObjectManyToOneField(pModule.getObjectname())) {
      String fieldType = f.getFieldtype();
      if (fieldType.equals(pModule.getObjectname())) {
        String childPath1 = f.getFDataobject().getObjectname() + CHILDSEPARATOR + "."
            + childPath.replaceFirst(pModule.getObjectname() + CHILDSEPARATOR, f.getFieldname());
        String fullname1 = f.getFDataobject().getTitle() + "("
            + fullname.replaceFirst(pModule.getTitle() + "\\(", f.getFieldtitle() + "--");
        cm.getChilds().put(childPath1,
            genModuleChildHierarchy(baseModule, cm, f, f.getFDataobject(), level + 1, fullname1, childPath1));
      }
    }
    return cm;
  }

}
