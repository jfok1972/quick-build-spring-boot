package com.jhopesoft.framework.core.objectquery.export;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class ExcelColumn {
	private String gridFieldId;

	/** 导出树形excel时使用的是formSchemeDetail */
	private String formFieldId;

	private String text;
	private String dataIndex;
	private Boolean hidden;
	private boolean ismonetary;

	/** datamining 用到的 */
	private String fieldname;
	private String fieldtype;
	private String aggregate;
	private String aggregatefieldname;
	private String unittext;

	private int firstCol;
	private int lastCol;
	private int firstRow;
	private int lastRow;

	private ExcelColumn[] items;

	private ExcelColumnType excelColumnType;

	private Object targetObject;
	/** 当前字段在树形的结构中处在第几级，0--顶级，1--子级，2-孙级 */
	private int treeLevel;

	public ExcelColumn() {

	}

	/**
	 * 取得当前column的所有末级的节点
	 */
	public List<ExcelColumn> getAllLeafItems() {
		List<ExcelColumn> result = new ArrayList<ExcelColumn>();
		getAllLeafItems(this, result);
		return result;
	}

	private void getAllLeafItems(ExcelColumn column, List<ExcelColumn> result) {
		if (column != null) {
			if (column.items != null && column.items.length > 0) {
				for (ExcelColumn sub : column.items) {
					getAllLeafItems(sub, result);
				}
			} else {
				result.add(column);
			}
		}
	}

	/**
	 * 取得当前列的类型
	 * 
	 * @return
	 */
	public ExcelColumnType getExcelColumnType() {
		if (excelColumnType == null) {
			if (aggregate == null) {
				excelColumnType = ExcelColumnType.String;
			} else if (Constants.COUNT.equalsIgnoreCase(aggregate)) {
				// count 肯定是整型
				excelColumnType = ExcelColumnType.Integer;
			} else if (aggregate.equalsIgnoreCase("sum") || aggregate.equalsIgnoreCase("max")
					|| aggregate.equalsIgnoreCase("min") || aggregate.equalsIgnoreCase("avg")) {
				// 原来的类型
				if (fieldtype == null) {
					excelColumnType = ExcelColumnType.String;
				} else if (fieldtype.equalsIgnoreCase("Double") || fieldtype.equalsIgnoreCase("Float")
						|| fieldtype.equalsIgnoreCase("Money")) {
					excelColumnType = ismonetary ? ExcelColumnType.DoubleMonetary : ExcelColumnType.Double;
				} else if (fieldtype.equalsIgnoreCase("Integer")) {
					excelColumnType = ismonetary ? ExcelColumnType.IntegerMonetary : ExcelColumnType.Integer;
				} else if (fieldtype.equalsIgnoreCase("Percent")) {
					excelColumnType = ExcelColumnType.Percent;
				} else if (fieldtype.equalsIgnoreCase("Date")) {
					excelColumnType = ExcelColumnType.Date;
				} else if (fieldtype.equalsIgnoreCase("Datetime")) {
					excelColumnType = ExcelColumnType.Datetime;
				} else {
					excelColumnType = ExcelColumnType.String;
				}
			} else if (aggregate.equalsIgnoreCase("wavg")) {
				// 加权平均
				excelColumnType = ExcelColumnType.WeightedAverage;
			} else {
				excelColumnType = ExcelColumnType.String;
			}
		}
		return excelColumnType;
	}

	/**
	 * Integer, // 整型 IntegerMonetary, // 整型有数值单位 Double, // 实数型 DoubleMonetary, //
	 * 实数型有数值单位 Percent, // 百分比 WeightedAverage, // 加权平均 String, // 字符串 Date, // 日期
	 * Datetime // 日期时间
	 * 
	 * @param moneraryText
	 * @return
	 */
	public String _getText(String moneraryText, boolean unittextalone) {
		String result = getText().replaceAll("--", "\n");
		if (dataIndex != null) {
			if (_isCount() || _isWavg()) {
				// count 的只是计数，与unit和数值单位无关,wavg 是加权平均
				return result;
			}
			if (unittextalone) {
				return result;
			}
			String unitText = unittext == null ? "" : unittext;
			if (moneraryText != null && moneraryText.length() > 0) {
				if (getExcelColumnType() == ExcelColumnType.DoubleMonetary
						|| getExcelColumnType() == ExcelColumnType.IntegerMonetary) {
					unitText = moneraryText + unitText;
				}
			}
			if (unitText != null && unitText.length() > 0) {
				result = result + "\n(" + unitText + ")";
			}
		}

		// result = result + "\n" + fieldtype+ "-"+ fieldname +"-"+ moneraryText;

		return result;
	}

	public String _getUnitText(String moneraryText) {

		String result = "";
		if (dataIndex != null) {
			if (_isCount() || _isWavg()) {
				// count 的只是计数，与unit和数值单位无关,wavg 是加权平均
				return result;
			}
			String unitText = unittext == null ? "" : unittext;
			if (moneraryText != null && moneraryText.length() > 0) {
				if (getExcelColumnType() == ExcelColumnType.DoubleMonetary
						|| getExcelColumnType() == ExcelColumnType.IntegerMonetary) {
					unitText = moneraryText + unitText;
				}
			}
			if (unitText != null && unitText.length() > 0) {
				result = result + "(" + unitText + ")";
			}
		}
		return result;

	}

	public static int setColRowSize(ExcelColumn[] items, int col, int row, JSONObject rowCount) {
		int colCount = 0;
		for (ExcelColumn column : items) {
			if (column.items != null) {
				column.setFirstRow(row); // 多层表头，只有一行
				column.setLastRow(row);
				column.setFirstCol(col + colCount);
				int subColumn = setColRowSize(column.items, col + colCount, row + 1, rowCount);
				colCount += subColumn;
				column.setLastCol(col + colCount - 1);
			} else {
				// 末级的column，肯定只有一列
				column.setFirstCol(col + colCount);
				column.setLastCol(col + colCount);
				// 末级的开始行，结整行，都是整个层数的层级
				column.setFirstRow(row);
				colCount++;
			}
		}
		if (rowCount.getIntValue(Constants.ROWCOUNT) < row) {
			rowCount.put(Constants.ROWCOUNT, row);
		}
		return colCount;
	}

	private boolean _isCount() {
		return aggregate != null && Constants.COUNT.equalsIgnoreCase(aggregate);
	}

	private boolean _isWavg() {
		return aggregate != null && aggregate.equalsIgnoreCase("wavg");
	}

	/**
	 * 设置所有的最底层的column的 lastrow为同一个值
	 * 
	 * @param items
	 * @param lastRow
	 */
	public static void setAllLastRow(ExcelColumn[] items, int lastRow) {
		for (ExcelColumn column : items) {
			if (column.items == null) {
				column.setLastRow(lastRow);
			} else {
				setAllLastRow(column.getItems(), lastRow);
			}
		}
	}

	public static void genAllDataIndexColumns(ExcelColumn[] items, List<ExcelColumn> dataIndexColumns) {
		for (ExcelColumn column : items) {
			if (column.items == null) {
				dataIndexColumns.add(column);
			} else {
				genAllDataIndexColumns(column.getItems(), dataIndexColumns);
			}
		}
	}

	public static void genAllColumns(ExcelColumn[] items, List<ExcelColumn> dataIndexColumns) {
		for (ExcelColumn column : items) {
			dataIndexColumns.add(column);
			if (column.items != null) {
				genAllColumns(column.getItems(), dataIndexColumns);
			}
		}
	}

	/**
	 * 根据字符串返回导出grid的column，生成一个数组
	 * 
	 * @param str
	 * @return
	 */
	public static List<ExcelColumn> changeToExportColumn(String str) {
		if (str != null && str.length() > 1) {
			return JSON.parseArray(str, ExcelColumn.class);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		String result = "text=" + text + " " + firstCol + "-" + lastCol + "--" + firstRow + "-" + lastRow + ";\r\n";
		if (items != null) {
			for (ExcelColumn column : items) {
				result += column.toString();
			}
		}
		return result;
	}

	public String getGridFieldId() {
		return gridFieldId;
	}

	public void setGridFieldId(String gridFieldId) {
		this.gridFieldId = gridFieldId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public Boolean getHidden() {
		return hidden == null ? false : hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public int getFirstCol() {
		return firstCol;
	}

	public void setFirstCol(int firstCol) {
		this.firstCol = firstCol;
	}

	public int getLastCol() {
		return lastCol;
	}

	public void setLastCol(int lastCol) {
		this.lastCol = lastCol;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}

	public ExcelColumn[] getItems() {
		return items;
	}

	public void setItems(ExcelColumn[] items) {
		this.items = items;
	}

	public boolean isIsmonetary() {
		return ismonetary;
	}

	public void setIsmonetary(boolean ismonetary) {
		this.ismonetary = ismonetary;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldtype() {
		return fieldtype;
	}

	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	public String getAggregatefieldname() {
		return aggregatefieldname;
	}

	public void setAggregatefieldname(String aggregatefieldname) {
		this.aggregatefieldname = aggregatefieldname;
	}

	public String getUnittext() {
		return unittext;
	}

	public void setUnittext(String unittext) {
		this.unittext = unittext;
	}

	public String getFormFieldId() {
		return formFieldId;
	}

	public void setFormFieldId(String formFieldId) {
		this.formFieldId = formFieldId;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	public int getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(int treeLevel) {
		this.treeLevel = treeLevel;
	}

}
