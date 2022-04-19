package com.jhopesoft.framework.dao.entity.viewsetting;
// Generated 2021-06-20 21:49:29 by Quick build System

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.datamining.FDataminingscheme;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectcondition;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * 实体对象组件(FovDataobjectwidget) generated by Quick Build System
 *
 * 了解快速架构系统 https://github.com/jfok1972
 *
 * @author 蒋锋 jfok1972@qq.com
 *
 */
@Entity
@DynamicUpdate
@Table(name = "fov_dataobjectwidget")
public class FovDataobjectwidget implements Serializable {

	/** 字段分组:基本信息 */
	/** 实体对象组件id */
	private String widgetid;
	/** 实体对象ID */
	private FDataobject fDataobject;
	/** 筛选方案ID */
	private FovFilterscheme fovFilterscheme;
	/** 视图方案id */
	private FDataobjectview fDataobjectview;
	/** 条件表达式id */
	private FDataobjectcondition fDataobjectcondition;
	/** 数据分析方案id */
	private FDataminingscheme fDataminingscheme;
	/** 组件名称 */
	private String title;
	/** 组件描述 */
	private String description;
	/** 类型 */
	private String widgettype;
	/** 程序中组件名称 */
	private String userdefinedname;
	/** 顺序号 */
	private int orderno;
	/** 响应式参数 */
	private String colspan;
	/** dataSet配置 */
	private String datasetproperty;
	/** 子字段指标配置 */
	private String subfieldsproperty;
	/** 指标比较的配置 */
	private String relativesproperty;
	/** 数据分析配置 */
	private String dataminingproperty;
	/** 主从展开的配置 */
	private String detailsproperty;
	/** 图表的配置 */
	private String chartproperty;
	/** 其他设置 */
	private String othersetting;

	/** 字段分组:其他信息 */
	/** 创建者 */
	private String creater;
	/** 创建日期 */
	private Timestamp createdate;
	/** 最近修改者 */
	private String lastmodifier;
	/** 最近修改日期 */
	private Timestamp lastmodifydate;
	private Set<FovHomepageschemedetail> fovHomepageschemedetails = new LinkedHashSet<FovHomepageschemedetail>(0);

	public FovDataobjectwidget() {
	}

	@Id
	/** 请使用 strategy = "uuid.hex" 主键中最好不要有“-”号 */
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "widgetid", unique = true, nullable = false, length = 40)
	public String getWidgetid() {
		return this.widgetid;
	}

	public void setWidgetid(String widgetid) {
		this.widgetid = widgetid;
	}

	@JSONField(serialize = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectid")
	public FDataobject getFDataobject() {
		return this.fDataobject;
	}

	public void setFDataobject(FDataobject fDataobject) {
		this.fDataobject = fDataobject;
	}

	@Transient
	public String getModuleName() {
		if (this.getFDataobject() != null) {
			return this.getFDataobject().getObjectid();
		}
		return null;
	}

	@JSONField(serialize = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filterschemeid")
	public FovFilterscheme getFovFilterscheme() {
		return this.fovFilterscheme;
	}

	public void setFovFilterscheme(FovFilterscheme fovFilterscheme) {
		this.fovFilterscheme = fovFilterscheme;
	}

	@Transient
	public String getFilterSchemeid() {
		if (this.getFovFilterscheme() != null) {
			return this.getFovFilterscheme().getFilterschemeid();
		}
		return null;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viewschemeid")
	public FDataobjectview getFDataobjectview() {
		return this.fDataobjectview;
	}

	public void setFDataobjectview(FDataobjectview fDataobjectview) {
		this.fDataobjectview = fDataobjectview;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conditionid")
	public FDataobjectcondition getFDataobjectcondition() {
		return this.fDataobjectcondition;
	}

	public void setFDataobjectcondition(FDataobjectcondition fDataobjectcondition) {
		this.fDataobjectcondition = fDataobjectcondition;
	}

	@JSONField(serialize = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	public FDataminingscheme getFDataminingscheme() {
		return this.fDataminingscheme;
	}

	public void setFDataminingscheme(FDataminingscheme fDataminingscheme) {
		this.fDataminingscheme = fDataminingscheme;
	}

	@Transient
	public String getDataminingSchememeid() {
		if (getFDataminingscheme() != null) {
			return getFDataminingscheme().getSchemeid();
		} else {
			return null;
		}
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "description", length = 100)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "widgettype", nullable = false, length = 30)
	public String getWidgettype() {
		return this.widgettype;
	}

	public void setWidgettype(String widgettype) {
		this.widgettype = widgettype;
	}

	@Column(name = "userdefinedname", length = 50)
	public String getUserdefinedname() {
		return this.userdefinedname;
	}

	public void setUserdefinedname(String userdefinedname) {
		this.userdefinedname = userdefinedname;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "colspan", length = 50)
	public String getColspan() {
		return this.colspan;
	}

	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	@Column(name = "datasetproperty", length = 2000)
	public String getDatasetproperty() {
		return this.datasetproperty;
	}

	public void setDatasetproperty(String datasetproperty) {
		this.datasetproperty = datasetproperty;
	}

	@Column(name = "subfieldsproperty", length = 2000)
	public String getSubfieldsproperty() {
		return this.subfieldsproperty;
	}

	public void setSubfieldsproperty(String subfieldsproperty) {
		this.subfieldsproperty = subfieldsproperty;
	}

	@Column(name = "relativesproperty", length = 2000)
	public String getRelativesproperty() {
		return this.relativesproperty;
	}

	public void setRelativesproperty(String relativesproperty) {
		this.relativesproperty = relativesproperty;
	}

	@Column(name = "dataminingproperty", length = 2000)
	public String getDataminingproperty() {
		return this.dataminingproperty;
	}

	public void setDataminingproperty(String dataminingproperty) {
		this.dataminingproperty = dataminingproperty;
	}

	@Column(name = "detailsproperty", length = 2000)
	public String getDetailsproperty() {
		return this.detailsproperty;
	}

	public void setDetailsproperty(String detailsproperty) {
		this.detailsproperty = detailsproperty;
	}

	@Column(name = "chartproperty", length = 2000)
	public String getChartproperty() {
		return this.chartproperty;
	}

	public void setChartproperty(String chartproperty) {
		this.chartproperty = chartproperty;
	}

	@Column(name = "othersetting", length = 2000)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

	@Column(name = "creater", nullable = false, length = 40)
	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	@Column(name = "createdate", nullable = false, length = 19)
	public Timestamp getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}

	@Column(name = "lastmodifier", length = 40)
	public String getLastmodifier() {
		return this.lastmodifier;
	}

	public void setLastmodifier(String lastmodifier) {
		this.lastmodifier = lastmodifier;
	}

	@Column(name = "lastmodifydate", length = 19)
	public Timestamp getLastmodifydate() {
		return this.lastmodifydate;
	}

	public void setLastmodifydate(Timestamp lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

	@JSONField(serialize = false)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fovDataobjectwidget")
	public Set<FovHomepageschemedetail> getFovHomepageschemedetails() {
		return this.fovHomepageschemedetails;
	}

	public void setFovHomepageschemedetails(Set<FovHomepageschemedetail> fovHomepageschemedetails) {
		this.fovHomepageschemedetails = fovHomepageschemedetails;
	}

}
