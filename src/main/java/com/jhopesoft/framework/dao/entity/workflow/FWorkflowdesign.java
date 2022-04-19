package com.jhopesoft.framework.dao.entity.workflow;
// default package

// Generated 2017-8-11 19:00:58 by Hibernate Tools 5.2.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;

/**
 * FWorkflowdesign generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_workflowdesign")
public class FWorkflowdesign implements java.io.Serializable {

	private String workflowid;
	private FDataobject FDataobject;
	private FWorkflowgroup FWorkflowgroup;
	private String deploymentId;
	private String procDefId;
	private String procDefKey;
	private String name;
	private String tenantId;
	private Integer version;
	private Boolean latestversion;
	private Date deployTime;
	private String suspensionState;
	private String bpmnxml;
	private String bpmnsvg;
	private byte[] bpmnpng;
	private String remark;
	private String creater;
	private Date createdate;
	private String lastmodifier;
	private Date lastmodifydate;

	public FWorkflowdesign() {
	}

	public FWorkflowdesign(String workflowid, String creater, Date createdate) {
		this.workflowid = workflowid;
		this.creater = creater;
		this.createdate = createdate;
	}

	public FWorkflowdesign(String workflowid, FDataobject FDataobject, FWorkflowgroup FWorkflowgroup, String deploymentId,
			String procDefId, String procDefKey, String name, String tenantId, Integer version, Boolean latestversion,
			Date deployTime, String suspensionState, String remark, String creater, Date createdate, String lastmodifier,
			Date lastmodifydate) {
		this.workflowid = workflowid;
		this.FDataobject = FDataobject;
		this.FWorkflowgroup = FWorkflowgroup;
		this.deploymentId = deploymentId;
		this.procDefId = procDefId;
		this.procDefKey = procDefKey;
		this.name = name;
		this.tenantId = tenantId;
		this.version = version;
		this.latestversion = latestversion;
		this.deployTime = deployTime;
		this.suspensionState = suspensionState;
		this.remark = remark;
		this.creater = creater;
		this.createdate = createdate;
		this.lastmodifier = lastmodifier;
		this.lastmodifydate = lastmodifydate;
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "workflowid", unique = true, nullable = false, length = 40)
	public String getWorkflowid() {
		return this.workflowid;
	}

	public void setWorkflowid(String workflowid) {
		this.workflowid = workflowid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectid", nullable = false, updatable = false)
	public FDataobject getFDataobject() {
		return this.FDataobject;
	}

	public void setFDataobject(FDataobject FDataobject) {
		this.FDataobject = FDataobject;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupid", nullable = false)
	public FWorkflowgroup getFWorkflowgroup() {
		return this.FWorkflowgroup;
	}

	public void setFWorkflowgroup(FWorkflowgroup FWorkflowgroup) {
		this.FWorkflowgroup = FWorkflowgroup;
	}

	@Column(name = "deployment_id_", length = 64, insertable = false)
	public String getDeploymentId() {
		return this.deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	@Column(name = "proc_def_id_", length = 64, insertable = false)
	public String getProcDefId() {
		return this.procDefId;
	}

	public void setProcDefId(String procDefId) {
		this.procDefId = procDefId;
	}

	@Column(name = "proc_def_key_")
	public String getProcDefKey() {
		return this.procDefKey;
	}

	public void setProcDefKey(String procDefKey) {
		this.procDefKey = procDefKey;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "tenant_id_")
	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "version_", insertable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "latestversion")
	public Boolean getLatestversion() {
		return this.latestversion;
	}

	public void setLatestversion(Boolean latestversion) {
		this.latestversion = latestversion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deploy_time_", length = 19, insertable = false)
	public Date getDeployTime() {
		return this.deployTime;
	}

	public void setDeployTime(Date deployTime) {
		this.deployTime = deployTime;
	}

	@Column(name = "suspension_state_", length = 10)
	public String getSuspensionState() {
		return this.suspensionState;
	}

	public void setSuspensionState(String suspensionState) {
		this.suspensionState = suspensionState;
	}

	public String getBpmnxml() {
		return bpmnxml;
	}

	public void setBpmnxml(String bpmnxml) {
		this.bpmnxml = bpmnxml;
	}

	public String getBpmnsvg() {
		return bpmnsvg;
	}

	public void setBpmnsvg(String bpmnsvg) {
		this.bpmnsvg = bpmnsvg;
	}
	
	public byte[] getBpmnpng() {
		return bpmnpng;
	}

	public void setBpmnpng(byte[] bpmnpng) {
		this.bpmnpng = bpmnpng;
	}

	@Column(name = "remark", length = 2000)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "creater", nullable = false, length = 40)
	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdate", nullable = false, length = 19)
	public Date getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Column(name = "lastmodifier", length = 40)
	public String getLastmodifier() {
		return this.lastmodifier;
	}

	public void setLastmodifier(String lastmodifier) {
		this.lastmodifier = lastmodifier;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastmodifydate", length = 19)
	public Date getLastmodifydate() {
		return this.lastmodifydate;
	}

	public void setLastmodifydate(Date lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

}