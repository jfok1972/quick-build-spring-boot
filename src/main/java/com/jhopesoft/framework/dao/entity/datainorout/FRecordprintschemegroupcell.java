package com.jhopesoft.framework.dao.entity.datainorout;
// default package

// Generated 2017-2-4 15:34:48 by Hibernate Tools 5.2.0.Beta1

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

/**
 *
 * @author 蒋锋
 * 
 */

@SuppressWarnings("serial")
@Entity
@DynamicUpdate
@Table(name = "f_recordprintschemegroupcell")
public class FRecordprintschemegroupcell implements java.io.Serializable {

	private String cellid;
	private FRecordprintschemegroup FRecordprintschemegroup;
	private int orderno;
	private String title;
	private String printtext;
	private Integer cheight;
	private Integer cwidth;
	private Integer colspan;
	private Integer rowspan;
	private Boolean isendrow;
	private String halign;
	private String valign;
	private String cssstyle;
	private Boolean isdisable;
	private String othersetting;

	public FRecordprintschemegroupcell() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "cellid", unique = true, nullable = false, length = 40)
	public String getCellid() {
		return this.cellid;
	}

	public void setCellid(String cellid) {
		this.cellid = cellid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupid", nullable = false)
	public FRecordprintschemegroup getFRecordprintschemegroup() {
		return this.FRecordprintschemegroup;
	}

	public void setFRecordprintschemegroup(FRecordprintschemegroup FRecordprintschemegroup) {
		this.FRecordprintschemegroup = FRecordprintschemegroup;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "title", nullable = false, length = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "printtext", length = 200)
	public String getPrinttext() {
		return this.printtext;
	}

	public void setPrinttext(String printtext) {
		this.printtext = printtext;
	}

	@Column(name = "cheight")
	public Integer getCheight() {
		return this.cheight;
	}

	public void setCheight(Integer cheight) {
		this.cheight = cheight;
	}

	@Column(name = "cwidth")
	public Integer getCwidth() {
		return this.cwidth;
	}

	public void setCwidth(Integer cwidth) {
		this.cwidth = cwidth;
	}

	@Column(name = "colspan")
	public Integer getColspan() {
		return (colspan == null || colspan == 0) ? 1 : colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	@Column(name = "rowspan")
	public Integer getRowspan() {
		return (rowspan == null || rowspan == 0) ? 1 : rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	@Column(name = "isendrow")
	public Boolean getIsendrow() {
		return isendrow == null ? false : isendrow;
	}

	public void setIsendrow(Boolean isendrow) {
		this.isendrow = isendrow;
	}

	@Column(name = "halign", length = 20)
	public String getHalign() {
		return this.halign;
	}

	public void setHalign(String halign) {
		this.halign = halign;
	}

	@Column(name = "valign", length = 20)
	public String getValign() {
		return this.valign;
	}

	public void setValign(String valign) {
		this.valign = valign;
	}

	@Column(name = "cssstyle", length = 100)
	public String getCssstyle() {
		return this.cssstyle;
	}

	public void setCssstyle(String cssstyle) {
		this.cssstyle = cssstyle;
	}

	@Column(name = "isdisable")
	public Boolean getIsdisable() {
		return this.isdisable;
	}

	public void setIsdisable(Boolean isdisable) {
		this.isdisable = isdisable;
	}

	@Column(name = "othersetting", length = 200)
	public String getOthersetting() {
		return this.othersetting;
	}

	public void setOthersetting(String othersetting) {
		this.othersetting = othersetting;
	}

	public String genHtml() {

		String result = "";
		String attr = "";
		if (colspan != null && colspan > 0) {
			attr = attr + " colspan=\"" + colspan + "\"";
		}
		if (rowspan != null && rowspan > 0) {
			attr = attr + " rowspan=\"" + rowspan + "\"";
		}

		// 这个取消掉了，因为第一个隐藏行里面加入了，每一列的宽度。
		if (cwidth != null && cwidth > 0) {
			attr = attr + " width=\"" + cwidth + "\"";
		}
		if (cheight != null && cheight > 0) {
			attr = attr + " height=\"" + cheight + "\"";
		}
		if (halign != null && halign.length() > 0) {
			attr = attr + " align=\"" + halign + "\"";
		}
		if (valign != null && valign.length() > 0) {
			attr = attr + " valign=\"" + valign + "\"";
		}
		if (cssstyle != null && cssstyle.length() > 0) {
			attr = attr + " class=\"" + cssstyle + "\"";
		}
		if (othersetting != null && othersetting.length() > 0) {
			attr = attr + " " + othersetting;
		}
		result = (containFormula() ? "<td " : "<th ") + attr + ">" + cellText()
				+ (containFormula() ? " </td>" : " </th>");
		return result;

	}

	public String cellText() {
		return (printtext == null ? title : printtext);
	}

	/**
	 * 此单元格是否包含公式
	 * 
	 * @return
	 */
	public boolean containFormula() {
		return cellText().indexOf("{") >= 0;
	}
}
