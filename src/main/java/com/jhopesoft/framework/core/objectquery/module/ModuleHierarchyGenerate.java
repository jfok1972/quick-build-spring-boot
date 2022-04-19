package com.jhopesoft.framework.core.objectquery.module;

import org.apache.commons.lang3.BooleanUtils;
import com.jhopesoft.framework.core.objectquery.sqlfield.AdditionParentModuleField;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.MD5;

/**
 * 
 * @author jiangfeng
 *
 */
public class ModuleHierarchyGenerate {

	public static final String CHILDSEPARATOR = ".with";
	public static final int MAXLEVEL = 50;
	public static final String AGGREGATE_ = "aggregate_";

	public static BaseModule genModuleHierarchy(FDataobject module, String asName, boolean isDatamining) {
		BaseModule baseModule = new BaseModule();
		baseModule.setModule(module);
		baseModule.setDatamining(isDatamining);
		baseModule.setAsName(asName);
		int pcount = 1;
		for (FDataobjectfield field : module.getFDataobjectfields()) {
			boolean ismanytoone = BooleanUtils.isNotTrue(field.getIsdisable())
					&& (field._isManyToOne() || field._isOneToOne());
			if (ismanytoone) {
				FDataobject pmodule = DataObjectUtils.getDataObject(field.getFieldtype());
				String parentPath = pmodule.getObjectname();
				baseModule.getParents().put(field.getFieldname(), genModuleParentHierarchy(baseModule, baseModule,
						field, pmodule, pcount++, 1, field.getFieldtitle(), parentPath, module.getObjectname(), false));
			}
		}
		for (String p : baseModule.getParents().keySet()) {
			baseModule.getParents().get(p).setIsDirectParent(true);
		}
		baseModule.calcParentModuleOnlyone();
		return baseModule;
	}

	public static ParentModule genModuleParentHierarchy(BaseModule baseModule, Object sonModule, FDataobjectfield field,
			FDataobject pModule, int pcount, int level, String fullname, String parentPath, String proviousPath,
			boolean breakdatafilterchain) {

		String proviousAs;
		String proviousAheadField;
		if (sonModule instanceof ParentModule) {
			proviousAs = ((ParentModule) sonModule).getAsName();
			proviousAheadField = ((ParentModule) sonModule).getFieldahead();
		} else {
			proviousAs = ((BaseModule) sonModule).getAsName();
			proviousAheadField = "";
		}
		if (level == MAXLEVEL) {
			return null;
		}
		ParentModule pm = new ParentModule();
		pm.setSonModuleHierarchy(sonModule);
		pm.setBreakDataFilterChain(breakdatafilterchain || BooleanUtils.isTrue(field.getBreakdatafilterchain()));
		pm.setModule(pModule);
		pm.setModuleField(field);
		pm.setLevel(level);
		pm.setModulePath(parentPath);
		pm.setOnlyonename(field.getFieldtitle());
		pm.setNamePath(fullname);
		pm.setFieldahead(proviousAheadField + (proviousAheadField.length() == 0 ? "" : ".") + field.getFieldname());
		String md5as = MD5.MD5Encode(proviousAs + level + "" + pcount);
		pm.setAsName("t_" + md5as.substring(md5as.length() - 28));
		baseModule.getAllParents().put(pm.getFieldahead(), pm);
		pm.setLeftoutterjoin(String.format("left outer join %s %s on %s = %s", pModule._getTablename(), pm.getAsName(),
				pModule._getPrimaryKeyField()._getSelectName(pm.getAsName()),
				proviousAs + "." + field.getFielddbname()));
		pcount = 1;
		for (FDataobjectfield mfield : pModule.getFDataobjectfields()) {
			boolean ismanytoone = BooleanUtils.isNotTrue(mfield.getIsdisable())
					&& (mfield._isManyToOne() || mfield._isOneToOne());
			if (ismanytoone) {
				FDataobject pmodule = DataObjectUtils.getDataObject(mfield.getFieldtype());
				String parentPath1 = parentPath + "--" + pmodule.getObjectname();
				pm.getParents().put(mfield.getFieldname(),
						genModuleParentHierarchy(baseModule, pm, mfield, pmodule, pcount++, level + 1,
								fullname + "--" + mfield.getFieldtitle(), parentPath1, parentPath,
								pm.isBreakDataFilterChain()));
			}
		}
		pm.setPrimarykeyField(new AdditionParentModuleField(pm.getFieldahead() + "." + pModule.getPrimarykey(),
				pModule._getPrimaryKeyField()._getSelectName(pm.getAsName())));
		FDataobjectfield mainLinkageField = pModule._getMainLinkageField();
		if (baseModule.isDatamining() || mainLinkageField == null || baseModule.getAsName().startsWith(AGGREGATE_)) {
			pm.setNameField(new AdditionParentModuleField(pm.getFieldahead() + "." + pModule.getNamefield(),
					pModule._getNameField()._getSelectName(pm.getAsName())));
		} else {
			ParentModule linkagepm = pm.getParents().get(mainLinkageField.getFieldname());
			linkagepm.setAddToFromByFields(true);
			linkagepm.getModule()._getNameField()._getSelectName(linkagepm.getAsName());
			pm.setNameField(new AdditionParentModuleField(pm.getFieldahead() + "." + pModule.getNamefield(),
					Local.getBusinessDao().getSf()
							.link(new String[] {
									linkagepm.getModule()._getNameField()._getSelectName(linkagepm.getAsName()),
									"' / '", pModule._getNameField()._getSelectName(pm.getAsName()) })));
		}
		return pm;
	}

	public static ChildModule genModuleChildHierarchy(BaseModule baseModule, Object parentModule,
			FDataobjectfield field, FDataobject pModule, int level, String fullname, String childPath) {
		if (level == MAXLEVEL) {
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
				cm.getChilds().put(childPath1, genModuleChildHierarchy(baseModule, cm, f, f.getFDataobject(), level + 1,
						fullname1, childPath1));
			}

		}
		return cm;
	}

}
