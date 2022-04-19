package com.jhopesoft.framework.dao.entity.limit;
// Generated 2020-11-11 18:17:24 by Hibernate Tools 5.2.12.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobjecthintmessage;
import com.jhopesoft.framework.dao.entity.system.FUser;

/**
 * FUserdataobjecthintmessage generated by hbm2java
 */

@Entity
@Table(name = "f_userdataobjecthintmessage")
@DynamicUpdate
@SuppressWarnings("serial")
public class FUserdataobjecthintmessage implements java.io.Serializable {

	private String userhintmessageid;
	private FDataobjecthintmessage FDataobjecthintmessage;
	private FUser FUser;

	public FUserdataobjecthintmessage() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "userhintmessageid", unique = true, nullable = false, length = 40)
	public String getUserhintmessageid() {
		return this.userhintmessageid;
	}

	public void setUserhintmessageid(String userhintmessageid) {
		this.userhintmessageid = userhintmessageid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hintmessageid", nullable = false)
	public FDataobjecthintmessage getFDataobjecthintmessage() {
		return this.FDataobjecthintmessage;
	}

	public void setFDataobjecthintmessage(FDataobjecthintmessage FDataobjecthintmessage) {
		this.FDataobjecthintmessage = FDataobjecthintmessage;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = false)
	public FUser getFUser() {
		return this.FUser;
	}

	public void setFUser(FUser FUser) {
		this.FUser = FUser;
	}

}
