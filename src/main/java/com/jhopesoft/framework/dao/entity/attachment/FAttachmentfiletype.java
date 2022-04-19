package com.jhopesoft.framework.dao.entity.attachment;
// default package

// Generated 2017-4-27 9:24:02 by Hibernate Tools 5.2.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Entity
@DynamicUpdate
@Table(name = "f_attachmentfiletype")
public class FAttachmentfiletype implements java.io.Serializable {
  private String suffix;
  private String title;
  private String mimetype;
  /**
   * 文件能直接在网页中打开，pdf,avi,mp4,等
   */
  private boolean canpreview;
  /**
   * 文件是否能转换成pdf来进行预览
   */
  private boolean canpdfpreview;
  private String previewmode;
  /**
   * 图片文件，可以生成缩略图
   */
  private boolean isimage;
  /**
   * 是否是offce文件
   */
  private boolean isoffice;
  private boolean cancompress;
  private String remark;

  public FAttachmentfiletype() {
  }

  public FAttachmentfiletype(String suffix, String title) {
    this.suffix = suffix;
    this.title = title;
  }

  public FAttachmentfiletype(String suffix, String title, boolean canpreview, boolean canpdfpreview, boolean isoffice,
      Boolean isimage, String mimetype) {
    this.suffix = suffix;
    this.title = title;
    this.canpreview = canpreview;
    this.canpdfpreview = canpdfpreview;
    this.isimage = isimage;
    this.isoffice = isoffice;
    this.mimetype = mimetype;
  }

  public FAttachmentfiletype(String suffix, String title, boolean canpreview, boolean canpdfpreview, String previewmode,
      boolean isimage, boolean isoffice, boolean cancompress, String remark) {
    this.suffix = suffix;
    this.title = title;
    this.canpreview = canpreview;
    this.canpdfpreview = canpdfpreview;
    this.previewmode = previewmode;
    this.isimage = isimage;
    this.isoffice = isoffice;
    this.cancompress = cancompress;
    this.remark = remark;
  }

  @Id
  @Column(name = "suffix", unique = true, nullable = false, length = 20)
  public String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Column(name = "title", nullable = false, length = 50)
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "mimetype", length = 50)
  public String getMimetype() {
    return this.mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  @Column(name = "canpreview")
  public boolean getCanpreview() {
    return this.canpreview;
  }

  public void setCanpreview(boolean canpreview) {
    this.canpreview = canpreview;
  }

  @Column(name = "canpdfpreview")
  public boolean getCanpdfpreview() {
    return this.canpdfpreview;
  }

  public void setCanpdfpreview(boolean canpdfpreview) {
    this.canpdfpreview = canpdfpreview;
  }

  @Column(name = "previewmode", length = 20)
  public String getPreviewmode() {
    return this.previewmode;
  }

  public void setPreviewmode(String previewmode) {
    this.previewmode = previewmode;
  }

  @Column(name = "isimage")
  public boolean getIsimage() {
    return this.isimage;
  }

  public void setIsimage(boolean isimage) {
    this.isimage = isimage;
  }

  @Column(name = "isoffice")
  public boolean getIsoffice() {
    return this.isoffice;
  }

  public void setIsoffice(boolean isoffice) {
    this.isoffice = isoffice;
  }

  @Column(name = "cancompress")
  public boolean getCancompress() {
    return this.cancompress;
  }

  public void setCancompress(boolean cancompress) {
    this.cancompress = cancompress;
  }

  @Column(name = "remark", length = 200)
  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

}
