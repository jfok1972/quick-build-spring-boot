package com.jhopesoft.framework.dao.entity.attachment;
// default package

// Generated 2017-4-26 14:00:58 by Hibernate Tools 5.2.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.jhopesoft.framework.utils.CommonUtils;

/**
 *
 * @author 蒋锋
 * 
 */

@Entity
@SuppressWarnings("serial")
@DynamicUpdate
@Table(name = "f_dataobjectattachmentpdffile")
public class FDataobjectattachmentpdffile implements java.io.Serializable {

  private String attachmentid;
  private FDataobjectattachment FDataobjectattachment;
  private byte[] filepdfdata;

  public FDataobjectattachmentpdffile() {
  }

  public FDataobjectattachmentpdffile(FDataobjectattachment FDataobjectattachment) {
    this.FDataobjectattachment = FDataobjectattachment;
  }

  public FDataobjectattachmentpdffile(FDataobjectattachment FDataobjectattachment, byte[] filepdfdata) {
    this.FDataobjectattachment = FDataobjectattachment;
    this.filepdfdata = filepdfdata;
  }

  @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "FDataobjectattachment"))
  @Id
  @GeneratedValue(generator = "generator")
  @Column(name = "attachmentid", unique = true, nullable = false, length = 40)
  public String getAttachmentid() {
    return this.attachmentid;
  }

  public void setAttachmentid(String attachmentid) {
    this.attachmentid = attachmentid;
  }

  @OneToOne(fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  public FDataobjectattachment getFDataobjectattachment() {
    return this.FDataobjectattachment;
  }

  public void setFDataobjectattachment(FDataobjectattachment FDataobjectattachment) {
    this.FDataobjectattachment = FDataobjectattachment;
  }

  @Column(name = "filepdfdata")
  public byte[] getFilepdfdata() {
    return this.filepdfdata;
  }

  public void setFilepdfdata(byte[] filepdfdata) {
    this.filepdfdata = CommonUtils.emptyBytesToNull(filepdfdata);
  }

}
