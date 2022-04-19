package com.jhopesoft.framework.dao.entity.dataobject;
// default package

// Generated 2017-10-24 14:50:01 by Hibernate Tools 5.2.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.dialect.MySQLDialect;

import com.jhopesoft.framework.utils.DES;
import com.jhopesoft.framework.utils.MD5;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Cache(region = "beanCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "f_datasource", uniqueConstraints = { @UniqueConstraint(columnNames = "title"),
		@UniqueConstraint(columnNames = "name") })
public class FDatasource implements java.io.Serializable {

	public static final String MYSQL = "mysql";
	public static final String SQLSERVER = "sqlserver";
	public static final String ORACLE = "oracle";

	private String datasourceid;
	private String title;
	private Integer orderno;
	private String driverclassname;
	private String connecturl;
	private String dialect;
	private String name;
	private String databasetype;
	private String databaseversion;
	private String username;
	private String password;
	private String connectremark;
	private String othersetting;
	private String businessremark;
	private String ipaddress;
	private int portnumber;
	private String defaultschema;
	private String urladdition;
	private String userdepartment;
	private String repairdepartment;
	private Date createdatabasedate;
	private Boolean disabled;
	private String creater;
	private Date createdate;
	private String lastmodifier;
	private Date lastmodifydate;
	private Set<FDatabaseschema> FDatabaseschemas = new HashSet<FDatabaseschema>(0);

	public FDatasource() {
	}

	@Transient
	public String getDataSourceUrl() {
		String result = null;
		if (StringUtils.isNotBlank(connecturl))
			result = connecturl;
		else {
			if (isMySql()) {
				// jdbc:mysql://127.0.0.1:3306/businessTrip?useUnicode=true&characterEncoding=utf8&useFastDateParsing=false
				result = "jdbc:mysql://" + getIpaddress() + ":" + getPortnumber();
				if (defaultschema != null) {
					result = result + "/" + getDefaultschema();
				}
				if (urladdition != null) {
					result = result + "?" + urladdition;
				} else {
					result += "?useUnicode=true&characterEncoding=utf8&useFastDateParsing=false&useSSL=true";
				}
			} else if (isSqlserver()) {
				// jdbc:sqlserver://127.0.0.1:1433;databaseName=businessTrip
				result = "jdbc:sqlserver://" + getIpaddress() + ":" + getPortnumber();
				if (defaultschema != null) {
					result = result + ";databaseName=" + getDefaultschema();
				}
			} else if (isOracle()) {
				// jdbc:oracle:thin:@192.168.99.72:1521:XE
				result = "jdbc:oracle:thin:@" + getIpaddress() + ":" + getPortnumber();
				if (defaultschema != null) {
					result = result + ":" + getDefaultschema();
				}
			}
		}
		return result;
	}

	@Transient
	public String getHibernateDialect() {
		String result = null;
		if (StringUtils.isNotBlank(dialect)) {
			result = dialect;
		} else if (isMySql()) {
			result = MySQLDialect.class.getName();
		} 
		return result;
	}

	@Transient
	public String getValidationQuery() {
		String result = null;
		if (isMySql()) {
			result = "select 1";
		} else if (isSqlserver()) {
			result = "select 1";
		} else if (isOracle()) {
			result = "select 1 from dual";
		}
		return result;
	}

	@Transient
	public boolean isMySql() {
		return StringUtils.isNotBlank(databasetype) && databasetype.equalsIgnoreCase(MYSQL);
	}

	@Transient
	public boolean isSqlserver() {
		return StringUtils.isNotBlank(databasetype) && databasetype.equalsIgnoreCase(SQLSERVER);
	}

	@Transient
	public boolean isOracle() {
		return StringUtils.isNotBlank(databasetype) && databasetype.equalsIgnoreCase(ORACLE);
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "datasourceid", unique = true, nullable = false, length = 40)
	public String getDatasourceid() {
		return this.datasourceid;
	}

	public void setDatasourceid(String datasourceid) {
		this.datasourceid = datasourceid;
	}

	@Column(name = "title", unique = true, nullable = false, length = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "orderno")
	public Integer getOrderno() {
		return this.orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	@Column(name = "driverclassname", length = 200)
	public String getDriverclassname() {
		return this.driverclassname;
	}

	public void setDriverclassname(String driverclassname) {
		this.driverclassname = driverclassname;
	}

	@Column(name = "connecturl", length = 200)
	public String getConnecturl() {
		return this.connecturl;
	}

	public void setConnecturl(String connecturl) {
		this.connecturl = connecturl;
	}

	@Column(name = "dialect", length = 200)
	public String getDialect() {
		return this.dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "databasetype", nullable = false, length = 50)
	public String getDatabasetype() {
		return this.databasetype;
	}

	public void setDatabasetype(String databasetype) {
		this.databasetype = databasetype;
	}

	@Column(name = "databaseversion", length = 50)
	public String getDatabaseversion() {
		return this.databaseversion;
	}

	public void setDatabaseversion(String databaseversion) {
		this.databaseversion = databaseversion;
	}

	@Column(name = "username", nullable = false, length = 50)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", length = 200)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		if (password == null) {
			this.password = password;
			return;
		}
		if (password.startsWith(PASSAHEAD))
			this.password = password;
		else {
			this.password = PASSAHEAD + DES.encrypt(password, _getSalt());
		}
	}

	private static final String PASSAHEAD = "已加密的密码:";
	private static final String SALTSOURCE = "12345";

	private String _getSalt() {
		String result = "";
		for (int i = 0; i < SALTSOURCE.length(); i++) {
			result = result + MD5.MD5Encode(SALTSOURCE.substring(i, i + 1));
		}
		return result;
	}

	public String _getPassword() {
		String result = this.password;
		if (result != null) {
			if (result.startsWith(PASSAHEAD)) {
				try {
					result = DES.decrypt(result.replaceFirst(PASSAHEAD, ""), _getSalt());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Column(name = "connectremark", length = 2000)
	public String getConnectremark() {
		return this.connectremark;
	}

	public void setConnectremark(String connectremark) {
		this.connectremark = connectremark;
	}

	@Column(name = "othersetting", length = 2000)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

	@Column(name = "businessremark", length = 200)
	public String getBusinessremark() {
		return this.businessremark;
	}

	public void setBusinessremark(String businessremark) {
		this.businessremark = businessremark;
	}

	@Column(name = "ipaddress", nullable = false, length = 50)
	public String getIpaddress() {
		return this.ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	@Column(name = "portnumber", nullable = false)
	public int getPortnumber() {
		return this.portnumber;
	}

	public void setPortnumber(int portnumber) {
		this.portnumber = portnumber;
	}

	@Column(name = "defaultschema", length = 50)
	public String getDefaultschema() {
		return this.defaultschema;
	}

	public void setDefaultschema(String defaultschema) {
		this.defaultschema = defaultschema;
	}

	@Column(name = "urladdition", length = 200)
	public String getUrladdition() {
		return this.urladdition;
	}

	public void setUrladdition(String urladdition) {
		this.urladdition = urladdition;
	}

	@Column(name = "userdepartment", length = 50)
	public String getUserdepartment() {
		return this.userdepartment;
	}

	public void setUserdepartment(String userdepartment) {
		this.userdepartment = userdepartment;
	}

	@Column(name = "repairdepartment", length = 50)
	public String getRepairdepartment() {
		return this.repairdepartment;
	}

	public void setRepairdepartment(String repairdepartment) {
		this.repairdepartment = repairdepartment;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdatabasedate", length = 19)
	public Date getCreatedatabasedate() {
		return this.createdatabasedate;
	}

	public void setCreatedatabasedate(Date createdatabasedate) {
		this.createdatabasedate = createdatabasedate;
	}

	@Column(name = "disabled")
	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
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

	@OrderBy("orderno")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FDatasource")
	public Set<FDatabaseschema> getFDatabaseschemas() {
		return this.FDatabaseschemas;
	}

	public void setFDatabaseschemas(Set<FDatabaseschema> FDatabaseschemas) {
		this.FDatabaseschemas = FDatabaseschemas;
	}

}
