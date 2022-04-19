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
@Table(name = "f_dataobjectattachmentfile")
public class FDataobjectattachmentfile implements java.io.Serializable {

  private String attachmentid;
  private FDataobjectattachment FDataobjectattachment;
  private byte[] filedata;

  public FDataobjectattachmentfile() {
  }

  public FDataobjectattachmentfile(FDataobjectattachment FDataobjectattachment) {
    this.FDataobjectattachment = FDataobjectattachment;
  }

  public FDataobjectattachmentfile(FDataobjectattachment FDataobjectattachment, byte[] filedata) {
    this.FDataobjectattachment = FDataobjectattachment;
    this.filedata = filedata;
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

  @Column(name = "filedata")
  public byte[] getFiledata() {
    return this.filedata;
  }

  public void setFiledata(byte[] filedata) {
    this.filedata = CommonUtils.emptyBytesToNull(filedata);
  }

}
