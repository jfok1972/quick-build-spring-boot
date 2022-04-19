package com.jhopesoft.framework.dao.entity.viewsetting;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.favorite.FovUserdefaultfilterscheme;
import com.jhopesoft.framework.dao.entity.system.FUser;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "fov_filterscheme", uniqueConstraints = @UniqueConstraint(columnNames = { "objectid", "schemename",
		"userid" }))
public class FovFilterscheme implements java.io.Serializable {

	private static final long serialVersionUID = -668544202011616070L;
	private String filterschemeid;
	private FDataobject FDataobject;
	private FUser FUser;
	private String schemename;
	private Integer orderno;
	private String othersetting;
	private Boolean isshare;
	private Boolean isshareowner;
	private String remark;
	private Set<FovUserdefaultfilterscheme> fovUserdefaultfilterschemes = new HashSet<FovUserdefaultfilterscheme>(0);
	private Set<FovFilterschemedetail> details = new HashSet<FovFilterschemedetail>(0);

	public FovFilterscheme() {
	}

	public FovFilterscheme(String filterschemeid, FDataobject FDataobject, String schemename, Integer orderno) {
		this.filterschemeid = filterschemeid;
		this.FDataobject = FDataobject;
		this.schemename = schemename;
		this.orderno = orderno;
	}

	public FovFilterscheme(String filterschemeid, FDataobject FDataobject, FUser FUser, String schemename,
			Integer orderno, String othersetting, Boolean isshare, Boolean isshareowner, String remark,
			Set<FovUserdefaultfilterscheme> fovUserdefaultfilterschemes, Set<FovFilterschemedetail> details) {
		this.filterschemeid = filterschemeid;
		this.FDataobject = FDataobject;
		this.FUser = FUser;
		this.schemename = schemename;
		this.orderno = orderno;
		this.othersetting = othersetting;
		this.isshare = isshare;
		this.isshareowner = isshareowner;
		this.remark = remark;
		this.fovUserdefaultfilterschemes = fovUserdefaultfilterschemes;
		this.details = details;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "filterschemeid", unique = true, nullable = false, length = 40)
	public String getFilterschemeid() {
		return this.filterschemeid;
	}

	public void setFilterschemeid(String filterschemeid) {
		this.filterschemeid = filterschemeid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectid", nullable = false)
	@JSONField(serialize = false)
	public FDataobject getFDataobject() {
		return this.FDataobject;
	}

	public void setFDataobject(FDataobject FDataobject) {
		this.FDataobject = FDataobject;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	@JSONField(serialize = false)
	public FUser getFUser() {
		return this.FUser;
	}

	public void setFUser(FUser FUser) {
		this.FUser = FUser;
	}

	@Column(name = "schemename", nullable = false, length = 50)
	public String getSchemename() {
		return this.schemename;
	}

	public void setSchemename(String schemename) {
		this.schemename = schemename;
	}

	@Column(name = "orderno", nullable = false)
	public Integer getOrderno() {
		return this.orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	@Column(name = "othersetting", length = 200)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

	@Column(name = "isshare")
	public Boolean getIsshare() {
		return this.isshare;
	}

	public void setIsshare(Boolean isshare) {
		this.isshare = isshare;
	}

	@Column(name = "isshareowner")
	public Boolean getIsshareowner() {
		return this.isshareowner;
	}

	public void setIsshareowner(Boolean isshareowner) {
		this.isshareowner = isshareowner;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fovFilterscheme")
	@JSONField(serialize = false)
	public Set<FovUserdefaultfilterscheme> getFovUserdefaultfilterschemes() {
		return this.fovUserdefaultfilterschemes;
	}

	public void setFovUserdefaultfilterschemes(Set<FovUserdefaultfilterscheme> fovUserdefaultfilterschemes) {
		this.fovUserdefaultfilterschemes = fovUserdefaultfilterschemes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fovFilterscheme", cascade = CascadeType.ALL)
	@OrderBy("orderno")
	public Set<FovFilterschemedetail> getDetails() {
		return this.details;
	}

	public void setDetails(Set<FovFilterschemedetail> details) {
		this.details = details;
	}

}
