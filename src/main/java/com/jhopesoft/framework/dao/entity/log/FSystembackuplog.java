package com.jhopesoft.framework.dao.entity.log;
// default package
// Generated 2017-2-4 15:34:48 by Hibernate Tools 5.2.0.Beta1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_systembackuplog")
public class FSystembackuplog implements java.io.Serializable {


  private String logid;
  private Date backupdate;
  private String ipaddress;
  private String backupfilename;
  private Integer uploadtimes;
  private String backupresult;
  private String otherfiles;
  private Date expiredate;
  private String remark;

  public FSystembackuplog() {}


  public FSystembackuplog(String logid, Date backupdate) {
    this.logid = logid;
    this.backupdate = backupdate;
  }

  public FSystembackuplog(String logid, Date backupdate, String ipaddress, String backupfilename, Integer uploadtimes,
      String backupresult, String otherfiles, Date expiredate, String remark) {
    this.logid = logid;
    this.backupdate = backupdate;
    this.ipaddress = ipaddress;
    this.backupfilename = backupfilename;
    this.uploadtimes = uploadtimes;
    this.backupresult = backupresult;
    this.otherfiles = otherfiles;
    this.expiredate = expiredate;
    this.remark = remark;
  }

  @Id
  @GeneratedValue(generator = "generator")
  @GenericGenerator(name = "generator", strategy = "uuid.hex")

  @Column(name = "logid", unique = true, nullable = false, length = 40)
  public String getLogid() {
    return this.logid;
  }

  public void setLogid(String logid) {
    this.logid = logid;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "backupdate", nullable = false, length = 19)
  public Date getBackupdate() {
    return this.backupdate;
  }

  public void setBackupdate(Date backupdate) {
    this.backupdate = backupdate;
  }


  @Column(name = "ipaddress", length = 50)
  public String getIpaddress() {
    return this.ipaddress;
  }

  public void setIpaddress(String ipaddress) {
    this.ipaddress = ipaddress;
  }


  @Column(name = "backupfilename", length = 50)
  public String getBackupfilename() {
    return this.backupfilename;
  }

  public void setBackupfilename(String backupfilename) {
    this.backupfilename = backupfilename;
  }


  @Column(name = "uploadtimes")
  public Integer getUploadtimes() {
    return this.uploadtimes;
  }

  public void setUploadtimes(Integer uploadtimes) {
    this.uploadtimes = uploadtimes;
  }


  @Column(name = "backupresult", length = 50)
  public String getBackupresult() {
    return this.backupresult;
  }

  public void setBackupresult(String backupresult) {
    this.backupresult = backupresult;
  }


  @Column(name = "otherfiles", length = 200)
  public String getOtherfiles() {
    return this.otherfiles;
  }

  public void setOtherfiles(String otherfiles) {
    this.otherfiles = otherfiles;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expiredate", length = 19)
  public Date getExpiredate() {
    return this.expiredate;
  }

  public void setExpiredate(Date expiredate) {
    this.expiredate = expiredate;
  }


  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }



}


