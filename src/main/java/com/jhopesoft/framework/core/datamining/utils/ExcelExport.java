package com.jhopesoft.framework.core.datamining.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumn;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumnType;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.TypeChange;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class ExcelExport {

	private String pagesize;
	boolean autofitwidth;
	private short scale;
	private boolean colorless = false;
	private int monetary = 1;
	private String moneraryText = "";
	private boolean disablerowgroup = false;
	private boolean unittextalone = false;

	private List<ExcelColumn> leafcolumns;
	private XSSFCellStyle btCellStyle;
	private XSSFWorkbook workbook;
	private XSSFCellStyle[] groupCellStyle;
	private XSSFCellStyle dataCellStyle;
	private XSSFCellStyle moneyStyle;
	private XSSFCellStyle percentStyle;
	private XSSFFont dataFont;
	private XSSFFont btFont;

	public ExcelExport(String pagesize, boolean autofitwidth, short scale, boolean colorless, int monerary,
			String moneraryText, boolean disablerowgroup, boolean unittextalone) {
		this.pagesize = pagesize;
		this.autofitwidth = autofitwidth;
		this.scale = scale;
		this.colorless = colorless;
		this.monetary = monerary;
		this.moneraryText = moneraryText;
		this.disablerowgroup = disablerowgroup;
		this.unittextalone = unittextalone;
		workbook = new XSSFWorkbook();
	}

	public OutputStream exportToExcel(String schemename, String conditions, List<ExcelColumn> allColumns,
			List<ExcelColumn> leafcolumns, JSONObject data, int rowCount, int colCount) throws IOException {

		this.leafcolumns = leafcolumns;
		workbook.createSheet();
		workbook.setSheetName(0, "数据分析查询结果");
		XSSFSheet sheet = workbook.getSheetAt(0);
		sheet.setRowSumsBelow(false);
		sheet.setRowSumsRight(false);
		Integer rownumber = 0;
		int columnCount = Math.max(leafcolumns.size(), 3);
		sheet.addMergedRegion(new CellRangeAddress(rownumber, rownumber, 0, columnCount - 1));
		XSSFRow row = sheet.createRow(rownumber++);
		row.setHeightInPoints((short) 36);
		XSSFCellStyle style2 = workbook.createCellStyle();
		style2.setLocked(true);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		if (!colorless) {
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xcd, 0xe6, 0xc7), null));
		}
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 20);
		style2.setFont(font);

		XSSFCell schemenamecell = row.createCell(0);
		schemenamecell.setCellStyle(style2);
		schemenamecell.setCellValue(schemename);
		row = sheet.createRow(rownumber);
		Integer halfy = columnCount / 2;
		sheet.addMergedRegion(new CellRangeAddress(rownumber, rownumber, 0, halfy));
		row.setHeightInPoints((short) 20);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("单位名称:" + Local.getUserBean().getCompanyname() + "--" + Local.getUsername());
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		if (!colorless) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xcd, 0xe6, 0xc7), null));
		}
		cell.setCellStyle(style);

		if (columnCount - 1 > halfy + 1) {
			sheet.addMergedRegion(new CellRangeAddress(rownumber, rownumber, halfy + 1, columnCount - 1));
		}
		cell = row.createCell(halfy + 1);
		cell.setCellValue("日期:" + TypeChange.dateToString(new Date()));
		style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		if (!colorless) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xcd, 0xe6, 0xc7), null));
		}
		cell.setCellStyle(style);
		rownumber++;
		JSONArray conditionarray = JSONArray.parseArray(conditions);
		for (int i = 0; i < conditionarray.size(); i++) {
			JSONObject object = conditionarray.getJSONObject(i);
			rownumber = writeCondition(sheet, rownumber, object, columnCount);
		}
		for (int i = 0; i < rowCount; i++) {
			XSSFRow btrow = sheet.createRow(rownumber + i);
			for (int j = 0; j < colCount; j++) {
				XSSFCell btcell = btrow.createCell(j);
				btcell.setCellStyle(getBtCellStyle());
			}
		}

		for (ExcelColumn column : allColumns) {
			if (unittextalone && column.getFirstCol() == 0) {
				sheet.addMergedRegion(new CellRangeAddress(column.getFirstRow() + rownumber,
						column.getLastRow() + rownumber + 1, column.getFirstCol(), column.getLastCol()));
			} else if (column.getFirstCol() != column.getLastCol() || column.getFirstRow() != column.getLastRow()) {
				sheet.addMergedRegion(new CellRangeAddress(column.getFirstRow() + rownumber,
						column.getLastRow() + rownumber, column.getFirstCol(), column.getLastCol()));
			}
			XSSFRow btrow = sheet.getRow(rownumber + column.getFirstRow());
			XSSFCell btcell = btrow.getCell(column.getFirstCol());
			btcell.setCellStyle(btCellStyle);
			btcell.setCellValue(column._getText(moneraryText, unittextalone).replaceAll("--", "\n"));
		}
		rownumber += rowCount;

		if (unittextalone) {
			XSSFRow unitTextRow = sheet.createRow(rownumber++);
			for (int i = 1; i < leafcolumns.size(); i++) {
				XSSFCell unitcell = unitTextRow.createCell(i);
				unitcell.setCellStyle(btCellStyle);
				unitcell.setCellValue(leafcolumns.get(i)._getUnitText(moneraryText));
			}
		}
		int repeatingrows = rownumber - 1;
		rownumber = writeTreeData(sheet, rownumber, data, 0);
		for (short column = 0; column < columnCount; column++) {
			sheet.autoSizeColumn(column);
		}
		double cm = 0;
		for (short column = 0; column < columnCount; column++) {
			cm += sheet.getColumnWidth(column);
		}
		XSSFPrintSetup ps = sheet.getPrintSetup();
		if (this.pagesize.equals("pageautofit")) {
			ps.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
			if (cm / 1216.2 > 29.7 - 4 - 0.5) {
				ps.setLandscape(true);
				ps.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
			} else if (cm / 1216.2 > 21 - 4 - 0.5) {
				ps.setLandscape(true);
				ps.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
			}
		} else {
			if (this.pagesize.equals("A4")) {
				ps.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
			} else if (this.pagesize.equals("A4landscape")) {
				ps.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
				ps.setLandscape(true);
			} else if (this.pagesize.equals("A3")) {
				ps.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
			} else if (this.pagesize.equals("A3landscape")) {
				ps.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
				ps.setLandscape(true);
			}
			if (getScale() != 100) {
				ps.setScale(getScale());
			} else if (this.autofitwidth) {
				sheet.setFitToPage(true);
				ps.setFitWidth((short) 1);
				ps.setFitHeight((short) 0);
			}
		}
		sheet.setRepeatingRows(new CellRangeAddress(0, repeatingrows, 0, 0));
		OutputStream fopts = new ByteArrayOutputStream();
		workbook.write(fopts);
		workbook.close();
		return fopts;
	}

	private Integer writeCondition(XSSFSheet sheet, Integer rownumber, JSONObject object, int columnCount) {
		XSSFRow row = sheet.createRow(rownumber);
		XSSFCell cell = row.createCell(0);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		if (!colorless) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xcd, 0xe6, 0xc7), null));
		}
		cell.setCellStyle(style);

		if ("视图方案".equals(object.getString("source"))) {
			sheet.addMergedRegion(new CellRangeAddress(rownumber, rownumber, 0, columnCount - 1));
			cell.setCellValue(object.getString("source") + ":" + object.getString("displaycond"));
		} else {
			if (object.containsKey("source")) {
				cell.setCellValue(object.getString("source")
						+ (object.containsKey("fieldtitle") ? ":" + object.getString("fieldtitle") : ""));
			}
			cell = row.createCell(1);
			cell.setCellStyle(style);

			if (object.containsKey(Constants.OPERATOR)) {
				cell.setCellValue(object.getString(Constants.OPERATOR));
			}
			cell = row.createCell(2);
			cell.setCellStyle(style);
			if (columnCount > 3) {
				sheet.addMergedRegion(new CellRangeAddress(rownumber, rownumber, 2, columnCount - 1));
			}
			if (object.containsKey("displaycond")) {
				cell.setCellValue(object.getString("displaycond"));
			}
		}
		return rownumber + 1;
	}

	private Integer writeTreeData(XSSFSheet sheet, Integer rownumber, JSONObject object, int level) {
		JSONArray rowdataarray = object.getJSONArray(Constants.DATA);
		XSSFRow row = sheet.createRow(rownumber++);
		for (int i = 0; i < rowdataarray.size(); i++) {
			XSSFCell cell = row.createCell(i);
			if (i == 0) {
				cell.setCellValue(rowdataarray.getString(i));
				cell.setCellStyle(getgroupCellStyle(level));
			} else {
				Double doublevalue;
				Integer intvalue;
				ExcelColumnType type = leafcolumns.get(i).getExcelColumnType();
				switch (type) {
					case Double:
						cell.setCellStyle(getMoneyStyle());
						doublevalue = rowdataarray.getDoubleValue(i);
						if (doublevalue != null && doublevalue != 0) {
							cell.setCellValue(doublevalue);
						}
						break;
					case DoubleMonetary:
						cell.setCellStyle(getMoneyStyle());
						doublevalue = rowdataarray.getDoubleValue(i);
						if (doublevalue != null && doublevalue != 0) {
							cell.setCellValue(doublevalue / monetary);
						}
						break;
					case Integer:
						cell.setCellStyle(getdataCellStyle());
						intvalue = rowdataarray.getInteger(i);
						if (intvalue != null && intvalue != 0) {
							cell.setCellValue(intvalue);
						}
						break;
					case IntegerMonetary:
						cell.setCellStyle(getdataCellStyle());
						intvalue = rowdataarray.getInteger(i);
						if (intvalue != null && intvalue != 0) {
							cell.setCellValue(intvalue / monetary);
						}
						break;
					case Percent:
						cell.setCellStyle(getPercentStyle());
						doublevalue = rowdataarray.getDoubleValue(i);
						if (doublevalue != null && doublevalue != 0) {
							cell.setCellValue(doublevalue);
						}
						break;
					case WeightedAverage:
						cell.setCellStyle(getPercentStyle());
						doublevalue = rowdataarray.getDoubleValue(i);
						if (doublevalue != null && doublevalue != 0) {
							cell.setCellValue(doublevalue);
						}
						break;
					case Date:
						cell.setCellValue(rowdataarray.getString(i));
						break;
					case Datetime:
						cell.setCellValue(rowdataarray.getString(i));
						break;
					case String:
						cell.setCellValue(rowdataarray.getString(i));
						break;
					default:
						break;
				}
			}
		}
		if (object.containsKey(Constants.CHILDREN)) {
			int firstrow = rownumber;
			JSONArray childs = object.getJSONArray(Constants.CHILDREN);
			for (int i = 0; i < childs.size(); i++) {
				rownumber = writeTreeData(sheet, rownumber, childs.getJSONObject(i), level + 1);
			}
			if (!disablerowgroup && level > 0) {
				sheet.groupRow(firstrow, rownumber - 1);
			}
		}
		return rownumber;
	}

	public XSSFCellStyle getBtCellStyle() {
		if (btCellStyle == null) {
			btCellStyle = workbook.createCellStyle();
			btCellStyle.setWrapText(true);
			btCellStyle.setAlignment(HorizontalAlignment.CENTER);
			btCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			addBorder(btCellStyle);
			btCellStyle.setFont(getBtFont());
			if (!colorless) {
				btCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				btCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xaf, 0xdf, 0xec), null));
			}
		}
		return btCellStyle;
	}

	public XSSFCellStyle getgroupCellStyle(int level) {
		if (groupCellStyle == null) {
			groupCellStyle = new XSSFCellStyle[20];
		}
		XSSFCellStyle result = groupCellStyle[level];
		if (result == null) {
			result = workbook.createCellStyle();
			result.setIndention((short) (1 * level));
			result.setAlignment(HorizontalAlignment.LEFT);
			result.setVerticalAlignment(VerticalAlignment.CENTER);
			addBorder(result);
			result.setFont(getBtFont());
			if (!colorless) {
				result.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				result.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xaf, 0xdf, 0xec), null));
			}
		}
		return result;
	}

	public XSSFFont getBtFont() {
		if (btFont == null) {
			btFont = workbook.createFont();
			btFont.setFontHeightInPoints((short) 9);
			btFont.setFontName("黑体");
		}
		return btFont;
	}

	public XSSFCellStyle getdataCellStyle() {
		if (dataCellStyle == null) {
			dataCellStyle = workbook.createCellStyle();
			dataCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			addBorder(dataCellStyle);
			dataCellStyle.setFont(getDataFont());

			if (!colorless) {
				dataCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				dataCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xee, 0xee, 0xee), null));
			}

		}
		return dataCellStyle;
	}

	public XSSFFont getDataFont() {
		if (dataFont == null) {
			dataFont = workbook.createFont();
			dataFont.setFontHeightInPoints((short) 9);
			dataFont.setFontName("微软雅黑");
			if (!colorless) {
				dataFont.setColor(HSSFColorPredefined.BLUE.getIndex());
			}
		}
		return dataFont;
	}

	public XSSFCellStyle getMoneyStyle() {
		if (moneyStyle == null) {
			moneyStyle = workbook.createCellStyle();
			moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
			moneyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			addBorder(moneyStyle);
			moneyStyle.setFont(getDataFont());
			if (!colorless) {
				moneyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				moneyStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xee, 0xee, 0xee), null));
			}
			// BuiltinFormats类里有好多格式
			moneyStyle.setDataFormat(0x28);
		}
		return moneyStyle;
	}

	public XSSFCellStyle getPercentStyle() {
		if (percentStyle == null) {
			percentStyle = workbook.createCellStyle();
			percentStyle.setAlignment(HorizontalAlignment.RIGHT);
			percentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			addBorder(percentStyle);
			percentStyle.setFont(getDataFont());
			XSSFDataFormat f = workbook.createDataFormat();
			percentStyle.setDataFormat(f.getFormat("0.00%"));
			if (!colorless) {
				percentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				percentStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0xee, 0xee, 0xee), null));
			}
		}
		return percentStyle;
	}

	private void addBorder(XSSFCellStyle style) {
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}

	public short getScale() {
		return scale;
	}

	public void setScale(short scale) {
		this.scale = scale;
	}

}
