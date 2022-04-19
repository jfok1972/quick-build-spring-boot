package com.jhopesoft.framework.dao.entity.datainorout;
// default package

// Generated 2017-2-4 15:34:48 by Hibernate Tools 5.2.0.Beta1

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@Table(name = "f_recordprintschemegroup")
public class FRecordprintschemegroup implements java.io.Serializable {

	private String groupid;
	private FRecordprintscheme FRecordprintscheme;
	private int orderno;
	private String title;
	private Integer gwidth;
	private Integer gcols;
	private String gwidths;
	private Integer borderwidth;
	private String cellpadding;
	private String cssstyle;
	private Boolean isdisable;
	private String othersetting;
	private Set<FRecordprintschemegroupcell> FRecordprintschemegroupcells = new HashSet<FRecordprintschemegroupcell>(0);

	public FRecordprintschemegroup() {
	}

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@Column(name = "groupid", unique = true, nullable = false, length = 40)
	public String getGroupid() {
		return this.groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid", nullable = false)
	public FRecordprintscheme getFRecordprintscheme() {
		return this.FRecordprintscheme;
	}

	public void setFRecordprintscheme(FRecordprintscheme FRecordprintscheme) {
		this.FRecordprintscheme = FRecordprintscheme;
	}

	@Column(name = "orderno", nullable = false)
	public int getOrderno() {
		return this.orderno;
	}

	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "gwidth")
	public Integer getGwidth() {
		return this.gwidth;
	}

	public void setGwidth(Integer gwidth) {
		this.gwidth = gwidth;
	}

	@Column(name = "gcols")
	public Integer getGcols() {
		return this.gcols;
	}

	public void setGcols(Integer gcols) {
		this.gcols = gcols;
	}

	@Column(name = "gwidths", length = 200)
	public String getGwidths() {
		return this.gwidths;
	}

	public void setGwidths(String gwidths) {
		this.gwidths = gwidths;
	}

	@Column(name = "borderwidth")
	public Integer getBorderwidth() {
		return this.borderwidth;
	}

	public void setBorderwidth(Integer borderwidth) {
		this.borderwidth = borderwidth;
	}

	@Column(name = "cellpadding", length = 50)
	public String getCellpadding() {
		return this.cellpadding;
	}

	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	@Column(name = "cssstyle", length = 50)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "FRecordprintschemegroup")
	@OrderBy("orderno")
	public Set<FRecordprintschemegroupcell> getFRecordprintschemegroupcells() {
		return this.FRecordprintschemegroupcells;
	}

	public void setFRecordprintschemegroupcells(Set<FRecordprintschemegroupcell> FRecordprintschemegroupcells) {
		this.FRecordprintschemegroupcells = FRecordprintschemegroupcells;
	}

	public Object genHtml(List<FRecordprintschemegroupcell> schemeGroupCells) {
		String result = "";
		String attr = " ";
		if (gwidth != null && gwidth > 0) {
			attr = attr + " width=\"" + gwidth + "\"";
		}
		if (borderwidth != null && borderwidth > 0) {
			attr = attr + " border=\"" + borderwidth + "\"";
		}
		if (cellpadding != null && cellpadding.length() > 0) {
			attr = attr + " cellpadding=\"" + cellpadding + "\"";
		}
		if (cssstyle != null && cssstyle.length() > 0) {
			attr = attr + " class=\"" + cssstyle + "\"";
		}
		if (othersetting != null && othersetting.length() > 0) {
			attr = attr + " " + othersetting;
		}
		result = "<printtable><table " + attr + ">";
		// 加入宽度，第一行是一个0行高的宽度
		String tr = "";
		String starttr = "<tr>";
		String endtr = "</tr>";
		int nowCol = 0;
		tr = "";
		for (FRecordprintschemegroupcell cell : schemeGroupCells) {
			if (nowCol + cell.getColspan() > gcols) {
				result = result + starttr + tr + endtr;
				tr = "";
				nowCol = 0;
			}
			nowCol += cell.getColspan();
			tr += cell.genHtml();
			if (cell.getIsendrow()) {
				result = result + starttr + tr + endtr;
				tr = "";
				nowCol = 0;
			}
		}
		if (tr.length() > 0) {
			result = result + starttr + tr + endtr;
		}
		result += " </table></printtable>";
		return result;
	}

}
