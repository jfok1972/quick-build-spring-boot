package com.jhopesoft.framework.dao.entity.log;
// Generated 2021-04-06 14:21:22 by Quick build System

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhopesoft.framework.dao.entity.dataobject.FNotification;
import com.jhopesoft.framework.dao.entity.system.FUser;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
*
* FUsernotificationresult(FUsernotificationresult) generated by Quick Build System 
*
* 了解快速架构系统 https://github.com/jfok1972
*
* @author 蒋锋 jfok1972@qq.com
*
*/
@Entity
@DynamicUpdate
@SuppressWarnings("serial")
@Table(name = "f_usernotificationresult")
public class FUsernotificationresult implements Serializable {

	/** 用户通知公告处理id */
	private String usernotificationresultid;
	/** 用户ID */
	private FUser FUser;
	/** 通知公告id */
	private FNotification FNotification;
	/** 接收时间 */
	private Date receivetime;
	/** 处理意见 */
	private String resulttype;
	/** 处理时间 */
	private Timestamp resulttime;
	/** 已删除 */
	private Timestamp deletedtime;

	public FUsernotificationresult() {
	}

	@Id
	/** 请使用 strategy = "uuid.hex" 主键中最好不要有“-”号 */
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "usernotificationresultid", unique = true, nullable = false, length = 40)
	public String getUsernotificationresultid() {
		return this.usernotificationresultid;
	}

	public void setUsernotificationresultid(String usernotificationresultid) {
		this.usernotificationresultid = usernotificationresultid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = false)
	public FUser getFUser() {
		return this.FUser;
	}

	public void setFUser(FUser FUser) {
		this.FUser = FUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notificationid", nullable = false)
	public FNotification getFNotification() {
		return this.FNotification;
	}

	public void setFNotification(FNotification FNotification) {
		this.FNotification = FNotification;
	}

	@Column(name = "receivetime", length = 10)
	public Date getReceivetime() {
		return this.receivetime;
	}

	public void setReceivetime(Date receivetime) {
		this.receivetime = receivetime;
	}

	@Column(name = "resulttype", length = 50)
	public String getResulttype() {
		return this.resulttype;
	}

	public void setResulttype(String resulttype) {
		this.resulttype = resulttype;
	}

	@Column(name = "resulttime", length = 19)
	public Timestamp getResulttime() {
		return this.resulttime;
	}

	public void setResulttime(Timestamp resulttime) {
		this.resulttime = resulttime;
	}

	@Column(name = "deletedtime", length = 19)
	public Timestamp getDeletedtime() {
		return this.deletedtime;
	}

	public void setDeletedtime(Timestamp deletedtime) {
		this.deletedtime = deletedtime;
	}

}
