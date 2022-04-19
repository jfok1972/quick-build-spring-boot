
package com.jhopesoft.framework.core.objectquery.export;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;

import com.jhopesoft.framework.bean.ExcelExportSetting;
import com.jhopesoft.framework.bean.GridFieldInfo;
import com.jhopesoft.framework.bean.GroupParameter;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormschemedetail;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridschemecolumn;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.TypeChange;

/**
 * 
 * 对于指定的FormScheme,按照树形格式来导出数据，并且生成Excel中的折叠符
 * 
 * @author jiangfeng
 * 
 *         每写100条记录就缓存到服务器的硬备上，防止内存溢出,但是只能往下写，不能往上面的格子里写了
 * 
 *         不写总计，没有小计, 没有颜色
 *
 */
public class ExcelExportPOI {
	/** 超出了这个记录数就不执行自动size */
	private static final int DISABLEAUTOSIZERECORD = 2000;
	private ExcelExportSetting setting;

	@SuppressWarnings("unused")
	private boolean toPDF;
	private boolean hasUnit;
	private int maxTreeLevel = 0;
	private List<Integer> subTotalRows = new ArrayList<Integer>();
	@SuppressWarnings("unused")
	private int totalrow;
	Integer x = 0;
	SXSSFWorkbook workbook = null;
	SXSSFSheet sheet = null;

	@SuppressWarnings("unchecked")
	public OutputStream genExcel(FDataobject module, String title, List<UserDefineFilter> conditionList,
			List<?> fu_value, GroupParameter group, Integer schemeOrder, Integer rowCount, Integer colCount,
			List<ExcelColumn> allColumns, List<ExcelColumn> dataIndexColumns) {
		String groupFieldname = null;
		if (group != null && group.getProperty() != null && group.getProperty().length() > 0) {
			groupFieldname = group.getTextProperty() == null ? group.getProperty() : group.getTextProperty();
			group.setTitle(group.getTitle() == null ? groupFieldname : group.getTitle());
		}
		if (CollectionUtils.isNotEmpty(fu_value)) {
			if (((Map<String, Object>) fu_value.get(0)).containsKey(LEVELKEY)) {
				groupFieldname = null;
			}
		}
		List<GridFieldInfo> gridFieldInfos = new ArrayList<GridFieldInfo>();
		for (ExcelColumn column : dataIndexColumns) {
			if (groupFieldname != null) {
				if (groupFieldname.equals(column.getDataIndex())) {
					group.setTitle(column.getText());
				}
			}
			if (column.getGridFieldId() != null || column.getFormFieldId() != null) {
				FDataobjectfield field;
				if (column.getGridFieldId() != null) {
					FovGridschemecolumn schemeColumn = Local.getDao().findById(FovGridschemecolumn.class,
							column.getGridFieldId());
					field = schemeColumn.getFDataobjectfield();
				} else {
					FovFormschemedetail schemeColumn = Local.getDao().findById(FovFormschemedetail.class,
							column.getFormFieldId());
					field = schemeColumn.getFDataobjectfield();
				}
				if (field != null) {
					GridFieldInfo info = new GridFieldInfo();
					info.setAllowSubTotal(field.getAllowsummary());
					info.setFieldId(field.getFieldid());
					info.setTreeLevel(column.getTreeLevel());
					maxTreeLevel = Math.max(maxTreeLevel, column.getTreeLevel());
					info.setFieldname(column.getDataIndex());
					info.setFieldtype(field.getFieldtype());
					info.setTitle(field.getFieldtitle());
					info.setUnitText(field.getUnittext());
					info.setIsmonetary(column.isIsmonetary());
					if (info.isPercentType()) {
						info.setDivisor(field.getDivisor());
						info.setDenominator(field.getDenominator());
					}
					if (column.isIsmonetary() && setting.isUsemonetary() && setting.getMonetaryUnit() != 1) {
						column.setUnittext(setting.getMonetaryText()
								+ (StringUtils.isEmpty(column.getUnittext()) ? "" : column.getUnittext()));
					} else {
						info.setIsmonetary(false);
						column.setIsmonetary(false);
					}
					if (field._isOneToMany()) {
						info.setFieldtype("Integer");
					}
					String fieldName = info.getFactFieldname();
					if (info.getValueFieldname() != null) {
						fieldName = info.getValueFieldname();
					}
					if (fieldName.endsWith("_detail")) {
						FDataobjectfield afield = module._getModuleFieldByFieldName(fieldName.replace("_detail", ""));
						if (afield != null && afield._isManyToMany()) {
							info.setManyToMany(true);
						}
					}
					gridFieldInfos.add(info);
				}
			} else {
				GridFieldInfo info = new GridFieldInfo();
				info.setAllowSubTotal(true);
				info.setFieldname(column.getDataIndex());
				info.setFieldtype("Integer");
				gridFieldInfos.add(info);
			}
		}
		hasUnit = false;
		for (ExcelColumn column : allColumns) {
			if (StringUtils.isNotBlank(column.getUnittext())) {
				hasUnit = true;
				break;
			}
		}
		OutputStream os = new ByteArrayOutputStream();
		try {
			workbook.setSheetName(0, module.getTitle());
			sheet.setMargin(HSSFSheet.TopMargin, 0.5);
			sheet.setMargin(HSSFSheet.BottomMargin, 0.5);
			sheet.setMargin(HSSFSheet.LeftMargin, 0.5);
			sheet.setMargin(HSSFSheet.RightMargin, 0.5);
			Integer groupColumnCount = groupFieldname != null ? 2 : 0;
			Integer maxY = gridFieldInfos.size() - 1 + groupColumnCount;
			x++;
			if (fu_value == null) {
				fu_value = new ArrayList<Object>();
			}
			x++;
			if (conditionList != null) {
				x += conditionList.size();
			}
			x += rowCount;
			if (hasUnit && setting.isUnitalone()) {
				x++;
			}
			String groupValue = "unused";
			if (!setting.isSumless()) {
				totalrow = x;
				x++;
			}
			drawHeader(title, fu_value, conditionList, allColumns, group, maxY, groupColumnCount, rowCount, colCount);
			int recordFirstCol = x;
			for (int i = 0; i < fu_value.size(); i++) {
				Map<String, Object> v = (Map<String, Object>) fu_value.get(i);
				boolean isTreeRoot = v.containsKey(LEVELKEY);
				if (groupColumnCount == 2) {
					String thisgroupvalue = null;
					{
						try {
							thisgroupvalue = v.get(groupFieldname).toString();
						} catch (Exception e) {
							thisgroupvalue = "";
						}
					}
					if (!groupValue.equals(thisgroupvalue)) {
						groupValue = thisgroupvalue;
						calcSubHjAndWirte(fu_value, sheet, x, groupColumnCount, groupFieldname, thisgroupvalue,
								gridFieldInfos);
						subTotalRows.add(x);
						x++;
					}
				}
				Row row = sheet.createRow(x);
				for (int k = 0; k < groupColumnCount; k++) {
					createCell(row, k, "", isTreeRoot ? _total_left : _normal_left);
				}
				for (int j = 0; j < gridFieldInfos.size(); j++) {
					GridFieldInfo fd = gridFieldInfos.get(j);
					if (fd == null) {
						continue;
					}
					Object fv = null;
					String fieldName = fd.getFactFieldname();
					if (fd.getValueFieldname() != null) {
						fieldName = fd.getValueFieldname();
					}
					if (v.containsKey(fieldName)) {
						fv = v.get(fieldName);
					}
					// fv = TypeChange.zerotoSpace(fv);
					if (fd.isDoubleType()) {
						if (fv == null) {
							createCell(row, j + groupColumnCount, (Double) null,
									isTreeRoot ? _total_double : _normal_double);
						} else {
							Double dv = fv instanceof Double ? (Double) fv : TypeChange.stringtoDouble(fv.toString());
							if (fd.isIsmonetary()) {
								dv = dv / setting.getMonetaryUnit();
							}
							createCell(row, j + groupColumnCount, dv, isTreeRoot ? _total_double : _normal_double);
						}
					} else if (fd.isPercentType()) {
						if (fv == null) {
							createCell(row, j + groupColumnCount, (Double) null,
									isTreeRoot ? _total_percent : _normal_percent);
						} else {
							Double dv = fv instanceof Double ? (Double) fv : TypeChange.stringtoDouble(fv.toString());
							createCell(row, j + groupColumnCount, dv, isTreeRoot ? _total_percent : _normal_percent);
						}
					} else if (fd.isIntType()) {
						if (fv == null) {
							createCell(row, j + groupColumnCount, (Integer) null,
									isTreeRoot ? _total_int : _normal_int);
						} else {
							Integer intv = fv instanceof Integer ? (Integer) fv
									: TypeChange.StringtoInteger(fv.toString());
							if (fd.isIsmonetary()) {
								intv = intv / setting.getMonetaryUnit();
							}
							createCell(row, j + groupColumnCount, intv, isTreeRoot ? _total_int : _normal_int);
						}
					} else if (fd.isDateType()) {
						if (fv == null) {
							createCell(row, j + groupColumnCount, (Date) null, isTreeRoot ? _total_date : _normal_date);
						} else {
							createCell(row, j + groupColumnCount,
									fv instanceof Date ? (Date) fv : TypeChange.stringToDate(fv.toString()),
									isTreeRoot ? _total_date : _normal_date);
						}
					} else if (fd.isDatetimeType()) {
						if (fv == null) {
							createCell(row, j + groupColumnCount, (Date) null,
									isTreeRoot ? _total_datetime : _normal_datetime);
						} else {
							createCell(row, j + groupColumnCount,
									fv instanceof Date ? (Date) fv : TypeChange.stringToDate(fv.toString()),
									isTreeRoot ? _total_datetime : _normal_datetime);
						}
					} else if (fd.isBlobType()) {
						createCell(row, j + groupColumnCount, (String) null, isTreeRoot ? _total_left : _normal_left);
					} else if (fv == null) {
						createCell(row, j + groupColumnCount, (String) null, isTreeRoot ? _total_left : _normal_left);
					} else {
						String str = fd.isManyToMany() ? changeManyToMany(fv.toString()) : fv.toString();
						if (str != null && fu_value.size() > DISABLEAUTOSIZERECORD) {
							widths[j + groupColumnCount] = Math.max(widths[j + groupColumnCount],
									str.getBytes("GBK").length + 1);
						}
						createCell(row, j + groupColumnCount, str, isTreeRoot ? _total_left : _normal_left);
					}
				}
				x++;
			}
			for (int i = 0; i < fu_value.size(); i++) {
				Map<String, Object> v = (Map<String, Object>) fu_value.get(i);
				if (v.containsKey(COUNTKEY)) {
					int count = Integer.parseInt(v.get(COUNTKEY).toString());
					sheet.groupRow(recordFirstCol + i + 1, recordFirstCol + i + count);
				}
			}
			for (int i = 0; i < fu_value.size(); i++) {
				Map<String, Object> v = (Map<String, Object>) fu_value.get(i);
				if (v.containsKey(LEVELKEY)) {
					int nowLevel = (Integer) v.get(LEVELKEY);
					Row arow = sheet.getRow(recordFirstCol + i);
					if (nowLevel == maxTreeLevel - 1) {
						for (int j = 0; j < gridFieldInfos.size(); j++) {
							GridFieldInfo info = gridFieldInfos.get(j);
							if (info.getTreeLevel() > nowLevel) {
								if (info.getAllowSubTotal()) {
									String colStr = excelColIndexToStr(j + groupColumnCount + 1);
									String formula = "sum(" + colStr + (recordFirstCol + i + 1 + 1) + ":" + colStr
											+ (recordFirstCol + i + 1 + (Integer) v.get(COUNTKEY)) + ")";
									Cell cell = arow.getCell(j + groupColumnCount);
									cell.setCellFormula(formula);
								}
							}
						}
					} else {
						for (int j = 0; j < gridFieldInfos.size(); j++) {
							GridFieldInfo info = gridFieldInfos.get(j);
							if (info.getTreeLevel() > nowLevel) {
								if (info.getAllowSubTotal()) {
									String colStr = excelColIndexToStr(j + groupColumnCount + 1);
									String formula = null;
									for (int k = 0; k < (Integer) v.get(COUNTKEY); k++) {
										Map<String, Object> subRecord = (Map<String, Object>) fu_value.get(i + k + 1);
										if (subRecord.containsKey(LEVELKEY)) {
											if (nowLevel + 1 == (Integer) subRecord.get(LEVELKEY)) {
												if (formula == null) {
													formula = "";
												} else {
													formula += "+";
												}
												formula += colStr + (recordFirstCol + i + k + 1 + 1);
											}
										}
									}
									Cell cell = arow.getCell(j + groupColumnCount);
									cell.setCellFormula(formula);
								}
							}
						}
					}
				}
			}
			sheet.trackAllColumnsForAutoSizing();
			for (short column = 0; column < groupColumnCount; column++) {
				sheet.autoSizeColumn(column);
			}
			for (int j = groupColumnCount; j < groupColumnCount + gridFieldInfos.size(); j++) {
				GridFieldInfo fd = gridFieldInfos.get(j - groupColumnCount);
				if (fd.isDateType()) {
					sheet.setColumnWidth(j, 10 * 256 + 96);
				} else if (fd.isDatetimeType()) {
					sheet.setColumnWidth(j, 16 * 256 + 96);
				} else if (fd.isPercentType()) {
					sheet.setColumnWidth(j, 8 * 256);
				} else if (fd.isBooleanType()) {
					sheet.setColumnWidth(j, 5 * 256 + 96);
				} else if (fd.isDoubleType()) {
					if (fu_value.size() > DISABLEAUTOSIZERECORD) {
						sheet.setColumnWidth(j, 12 * 256);
					} else {
					}
				} else {
					if (fu_value.size() > DISABLEAUTOSIZERECORD) {
						sheet.setColumnWidth(j, widths[j] * 256);
					} else {
					}
				}
			}
			for (short column = 0; column <= maxY; column++) {
				if (sheet.getColumnWidth(column) > 50 * 256) {
					sheet.setColumnWidth(column, 50 * 256);
				}
				if (sheet.getColumnWidth(column) < 5 * 256) {
					sheet.setColumnWidth(column, 5 * 256);
				}
			}
			int sumWidth = 0;
			for (short column = 0; column <= maxY; column++) {
				sumWidth += sheet.getColumnWidth(column);
			}
			if (sumWidth < 80 * 256) {
				for (short column = 0; column <= maxY; column++) {
					sheet.setColumnWidth(column, sheet.getColumnWidth(column) * 80 * 256 / sumWidth);
				}
			}
			PrintSetup printSetup = sheet.getPrintSetup();
			String pagesize = setting.getPagesize();
			if (setting.getPagesize().equals("pageautofit")) {
				printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
				if (sumWidth > 85 * 256) {
					printSetup.setLandscape(true); 
				}
				if (sumWidth > 125 * 256) {
					printSetup.setPaperSize(HSSFPrintSetup.A3_PAPERSIZE);
				}
			} else {
				if (pagesize.equals("A4")) {
					printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
				} else if (pagesize.equals("A4landscape")) {
					printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
					printSetup.setLandscape(true);
				} else if (pagesize.equals("A3")) {
					printSetup.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
				} else if (pagesize.equals("A3landscape")) {
					printSetup.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
					printSetup.setLandscape(true);
				}
				if (setting.getScale() != 100) {
					printSetup.setScale(setting.getScale());
				} else if (setting.isAutofitwidth()) {
					sheet.setFitToPage(true);
					printSetup.setFitWidth((short) 1);
					printSetup.setFitHeight((short) 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.write(os);
				workbook.dispose();
				workbook.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return os;
	}

	public static String excelColIndexToStr(int columnIndex) {
		if (columnIndex <= 0) {
			return null;
		}
		String columnStr = "";
		columnIndex--;
		do {
			if (columnStr.length() > 0) {
				columnIndex--;
			}
			columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
			columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
		} while (columnIndex > 0);
		return columnStr;
	}

	/**
	 * 如果是manytomany导出的时候是
	 * 20,竣工验收合格单,402882e5634a000701634a09e5cf0025|||50,发票,402882e5634a000701634a09e5f10027
	 * 的格式，需要取中间的文字
	 * 
	 * @param str
	 * @return
	 */
	private String changeManyToMany(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		String[] records = str.split("\\|\\|\\|");
		String[] result = new String[records.length];
		int i = 0;
		for (String record : records) {
			String[] part = record.split(Constants.COMMA);
			if (part.length >= 2) {
				result[i++] = part[1];
			}
		}
		return String.join(Constants.COMMA, result);
	}

	@SuppressWarnings("unchecked")
	public void calcSubHjAndWirte(List<?> fu_value, Sheet sheet, int x, int groupcolumns, String groupfieldname,
			String groupvalue, List<GridFieldInfo> gridFieldInfos) {
		Map<String, Object> totalValue = null;
		try {
			if (fu_value.size() > 0) {
				totalValue = new HashMap<String, Object>(0);
			} else {
				return;
			}
			int count = 0;
			for (int i = 0; i < fu_value.size(); i++) {
				Map<String, Object> v = (Map<String, Object>) fu_value.get(i);
				String nowGroupValueString = "";
				try {
					nowGroupValueString = v.get(groupfieldname).toString();
				} catch (Exception e) {

				}
				if (nowGroupValueString.equals(groupvalue)) {
					count++;
					for (int j = 0; j < gridFieldInfos.size(); j++) {
						GridFieldInfo fd = gridFieldInfos.get(j);
						try {
							if (fd.getAllowSubTotal()) {
								if (fd.isIntType()) {
									totalValue.put(fd.getFactFieldname(),
											TypeChange.itoi((Integer) totalValue.get(fd.getFactFieldname()))
													+ TypeChange.itoi(v.get(fd.getFactFieldname())));
								} else if (fd.isPercentType() && fd.getDivisor() != null) {
									totalValue.put(fd.getDivisorKey(),
											TypeChange.dtod(totalValue.get(fd.getDivisorKey()))
													+ TypeChange.dtod(v.get(fd.getDivisor())));
									totalValue.put(fd.getDenominatorKey(),
											TypeChange.dtod(totalValue.get(fd.getDenominatorKey()))
													+ TypeChange.dtod(v.get(fd.getDenominator())));
								} else {
									totalValue.put(fd.getFactFieldname(),
											TypeChange.dtod((Double) totalValue.get(fd.getFactFieldname()))
													+ TypeChange.dtod(v.get(fd.getFactFieldname())));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			Row row = sheet.createRow(x);
			row.setHeightInPoints((short) 17);
			for (int j = 0; j < gridFieldInfos.size(); j++) {
				GridFieldInfo fd = gridFieldInfos.get(j);
				if (fd.getAllowSubTotal()) {
					Object fv = null;
					try {
						fv = totalValue.get(fd.getFactFieldname());
					} catch (Exception e) {
						fv = "";
					}
					fv = TypeChange.zerotoSpace(fv);
					if (fd.isIntType()) {
						Integer iv = TypeChange.StringtoInteger(fv.toString());
						if (fd.isIsmonetary()) {
							iv = iv / setting.getMonetaryUnit();
						}
						createCell(row, j + groupcolumns, iv != 0 ? iv : null, _total_int);
					} else if (fd.isPercentType() && fd.getDivisor() != null) {
						double div = (Double) totalValue.get(fd.getDivisorKey());
						double deno = (Double) totalValue.get(fd.getDenominatorKey());
						createCell(row, j + groupcolumns, deno == 0.0 || div == 0.0 ? null : div / deno,
								_total_percent);
					} else {
						Double dv = TypeChange.stringtoDouble(fv.toString());
						if (fd.isIsmonetary()) {
							dv = dv / setting.getMonetaryUnit();
						}
						createCell(row, j + groupcolumns, dv, _total_double);
					}
				} else {
					createCell(row, j + groupcolumns, (String) null, _total_left);
				}
			}
			String tString = groupvalue + "〖小计〗";
			createCell(row, 0, tString, _total_left);
			createCell(row, 1, count, _total_int);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 最后写入总计，全部用公式
	 * 
	 * 1.标准的总计 2.有小计的总计 3.树形结构的总计 4.加权平均的
	 * 
	 * @param fuValue
	 * @param sheet
	 * @param x
	 * @param groupcolumns
	 * @param gridFieldInfos
	 */

	@SuppressWarnings("unchecked")
	public void calcHjAndWirte(List<?> fuValue, Sheet sheet, int x, int groupcolumns,
			List<GridFieldInfo> gridFieldInfos) {
		if (CollectionUtils.isEmpty(fuValue)) {
			return;
		}
		// 写入总计
		Row row = sheet.createRow(x);
		row.setHeightInPoints((short) 18);
		for (int j = 0; j < gridFieldInfos.size(); j++) {
			GridFieldInfo fd = gridFieldInfos.get(j);
			if (fd == null) {
				continue;
			}
			if (fd.getAllowSubTotal()) {
				String colStr = excelColIndexToStr(j + groupcolumns + 1);
				if (fd.isPercentType() && fd.getDivisor() != null) {

				} else {
					Cell cell = createCell(row, j + groupcolumns, 0.0, fd.isIntType() ? _total_int : _total_double);
					String formula = null;
					if (groupcolumns == 0 && !((Map<String, Object>) fuValue.get(0)).containsKey(LEVELKEY)) {
						formula = "sum(" + colStr + (x + 1 + 1) + ":" + colStr + (x + 1 + fuValue.size()) + ")";
					} else if (((Map<String, Object>) fuValue.get(0)).containsKey(LEVELKEY)) {
						formula = null;
						for (int k = 0; k < fuValue.size(); k++) {
							Map<String, Object> subRecord = (Map<String, Object>) fuValue.get(k);
							if (subRecord.containsKey(LEVELKEY) && ((int) subRecord.get(LEVELKEY)) == 0) {
								if (formula == null) {
									formula = "";
								} else {
									formula += "+";
								}
								formula += colStr + (x + k + 1 + 1);
							}
						}
						if (formula != null && formula.length() > 1023) {
							writeFieldTotal(fd, fuValue, cell);
							formula = null;
						}
					} else if (groupcolumns > 0) {
						formula = null;
						for (Integer subtotalrow : subTotalRows) {
							if (formula == null) {
								formula = "";
							} else {
								formula += "+";
							}
							formula += colStr + (subtotalrow + 1);
						}
						if (formula != null && formula.length() > 1023) {
							writeFieldTotal(fd, fuValue, cell);
							formula = null;
						}
					}
					if (formula != null) {
						cell.setCellFormula(formula);
					}
				}
			} else {
				createCell(row, j + groupcolumns, (String) null, _total_left);
			}
		}
		int zjpos = 0;
		for (int j = 0; j < gridFieldInfos.size(); j++) {
			GridFieldInfo fd = gridFieldInfos.get(j);
			if (fd.getFieldtype().equalsIgnoreCase(Constants.STRING)) {
				zjpos = j;
				break;
			}
		}
		if (groupcolumns == 0) {
			String tString = "〖总计(" + fuValue.size() + "条)〗";
			createCell(row, zjpos, tString, _total_left);
		} else {
			String tString = "〖总  计〗";
			createCell(row, 0, tString, _total_left);
			createCell(row, 1, fuValue.size(), _total_int);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeFieldTotal(GridFieldInfo fd, List<?> fuValue, Cell cell) {
		if (fd.isIntType()) {
			Integer value = CommonUtils.getIntegerFieldTotal((List<Map<String, Object>>) fuValue,
					fd.getFactFieldname());
			cell.setCellValue(value);
		} else if (fd.isDoubleType()) {
			Double value = CommonUtils.getDoubleFieldTotal((List<Map<String, Object>>) fuValue, fd.getFactFieldname());
			cell.setCellValue(value);
		}
	}

	private static final String COUNTKEY = "__count__";
	private static final String LEVELKEY = "__level__";
	CellStyle wcf_title;
	CellStyle wcf_futitle;
	CellStyle wcf_futitle_right;
	CellStyle wcf_tabletitle;

	CellStyle _normal_left;
	CellStyle _normal_double;
	CellStyle _normal_percent;
	CellStyle _normal_int;
	CellStyle _normal_date;
	CellStyle _normal_datetime;

	CellStyle _total_left;
	CellStyle _total_double;
	CellStyle _total_percent;
	CellStyle _total_int;
	CellStyle _total_date;
	CellStyle _total_datetime;

	int[] widths = new int[1024];

	/**
	 * 创建一个style , 字体，大小，前景色，背景色，加粗，边线，左右对齐，上下对齐
	 * 
	 * @param fontName
	 * @param size
	 * @return
	 */
	private CellStyle createCellStyle(String fontName, short size, String type, String dateType, boolean border,
			HorizontalAlignment ha, boolean wrapText) {
		CellStyle result = workbook.createCellStyle();
		result.setWrapText(wrapText);
		Font font = workbook.createFont();
		if (fontName != null) {
			font.setFontName(fontName);
		} else {
			font.setFontName("微软雅黑");
		}
		if (size > 0) {
			font.setFontHeightInPoints(size);
		}

		if (!setting.isColorless()) {
			if (Constants.INT.equals(dateType)) {
				font.setColor(IndexedColors.BLUE.getIndex());
			} else if (Constants.DOUBLE.equals(dateType)) {
				font.setColor(IndexedColors.BLUE.getIndex());
			} else if (Constants.PERCENT.equals(dateType)) {
				font.setColor(IndexedColors.BLUE.getIndex());
			} else if (Constants.DATE.equals(dateType) || Constants.DATETIME.equals(dateType)) {
				font.setColor(IndexedColors.DARK_GREEN.getIndex());
			}
		}
		if (Constants.TOTAL.equals(type)) {
			font.setBold(true);
		}
		result.setFont(font);
		if (ha != null) {
			result.setAlignment(ha);
		}
		// 垂直对齐
		result.setVerticalAlignment(VerticalAlignment.CENTER);
		if (border) {
			result.setBorderTop(BorderStyle.THIN);
			result.setBorderBottom(BorderStyle.THIN);
			result.setBorderLeft(BorderStyle.THIN);
			result.setBorderRight(BorderStyle.THIN);
		}
		if (!setting.isColorless()) {
			result.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			if ("columntitle".equals(type)) {
				result.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			} else if (Constants.TOTAL.equals(type)) {
				result.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			} else {
				result.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
			}
		}

		return result;
	}

	public ExcelExportPOI(ExcelExportSetting setting, boolean toPDF) {
		this.setting = setting;
		this.setting.setColorless(true);
		this.setting.setSumless(true);
		this.toPDF = toPDF;
		workbook = new SXSSFWorkbook(100);
		sheet = workbook.createSheet();

		// 主标题
		wcf_title = createCellStyle(null, (short) 18, Constants.NORMAL, Constants.STRING, false,
				HorizontalAlignment.CENTER,
				false);
		// 副标题
		wcf_futitle = createCellStyle(null, (short) 10, Constants.NORMAL, Constants.STRING, false,
				HorizontalAlignment.LEFT,
				true);
		// 副标题 右对齐
		wcf_futitle_right = createCellStyle(null, (short) 10, Constants.NORMAL, Constants.STRING, false,
				HorizontalAlignment.RIGHT, true);
		// GRID标题
		wcf_tabletitle = createCellStyle(null, (short) 10, "columntitle", Constants.STRING, true,
				HorizontalAlignment.CENTER,
				true);
		// 表格正文 左对齐
		_normal_left = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.STRING, true,
				HorizontalAlignment.LEFT,
				true);
		// 表格正文 右对齐 浮点数值，下面不符合分节符和使用分节符的
		// 表格正文 右对齐 浮点数值
		_normal_double = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.DOUBLE, true,
				HorizontalAlignment.RIGHT,
				false);

		short doubleformat = workbook.createDataFormat().getFormat("#,##0.00;[Red]-#,##0.00;_(* \"-\"??_)");
		short percentformat = workbook.createDataFormat().getFormat("0.00%;[Red]-0.00%;_(* \"-\"??_)");
		short dateformat = workbook.createDataFormat().getFormat(Constants.DATE_FORMAT);
		short datetimeformat = workbook.createDataFormat().getFormat("yyyy-MM-dd hh:mm");

		_normal_double.setDataFormat(doubleformat);

		// 表格正文 右对齐 日期
		_normal_date = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.DATE, true,
				HorizontalAlignment.CENTER,
				false);
		_normal_date.setDataFormat(dateformat);
		// 日期+时间
		_normal_datetime = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.DATETIME, true,
				HorizontalAlignment.CENTER, false);
		_normal_datetime.setDataFormat(datetimeformat);
		// 百分比
		_normal_percent = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.PERCENT, true,
				HorizontalAlignment.RIGHT,
				false);
		_normal_percent.setDataFormat(percentformat);
		// 表格正文 右对齐 整数值
		_normal_int = createCellStyle(null, (short) 9, Constants.NORMAL, Constants.INT, true, HorizontalAlignment.RIGHT,
				false);

		// 总计的样式
		_total_double = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.DOUBLE, true,
				HorizontalAlignment.RIGHT, false);
		_total_double.setDataFormat(doubleformat);

		_total_percent = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.PERCENT, true,
				HorizontalAlignment.RIGHT, false);
		_total_percent.setDataFormat(percentformat);
		///////////

		_total_int = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.INT, true, HorizontalAlignment.RIGHT,
				false);
		_total_left = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.STRING, true,
				HorizontalAlignment.LEFT, true);

		_total_date = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.DATE, true,
				HorizontalAlignment.CENTER, false);
		_total_date.setDataFormat(dateformat);

		_total_datetime = createCellStyle(null, (short) 9, Constants.TOTAL, Constants.DATETIME, true,
				HorizontalAlignment.CENTER,
				false);
		_total_datetime.setDataFormat(datetimeformat);

	}

	/**
	 * 写入表头区域，最后在计算过列宽之后写入
	 * 
	 * @param module
	 * @param fu_value
	 * @param conditionList
	 * @param allColumns
	 * @param group
	 * @param maxY
	 * @param groupcolumns
	 * @param rowCount
	 * @param colcount
	 */
	private void drawHeader(String title, List<?> fu_value, List<UserDefineFilter> conditionList,
			List<ExcelColumn> allColumns, GroupParameter group, int maxY, int groupcolumns, int rowCount,
			int colCount) {
		x = 0;
		if (maxY != 0) {
			sheet.addMergedRegion(new CellRangeAddress(x, x, 0, maxY));
		}
		Row row = sheet.createRow(x);
		row.setHeightInPoints((short) 40);
		createCell(row, 0, title, wcf_title);
		x++;
		// 写入单位名称 及计量单位 ,打印日期,记录数
		Integer halfy = maxY / 2;
		if (halfy != 0) {
			sheet.addMergedRegion(new CellRangeAddress(x, x, 0, halfy));
		}
		row = sheet.createRow(x);
		row.setHeightInPoints(18);
		createCell(row, 0, "单位名称:" + Local.getUserBean().getCompanyname() + "--" + Local.getUsername(), wcf_futitle);
		if (halfy + 1 < maxY) {
			sheet.addMergedRegion(new CellRangeAddress(x, x, halfy + 1, maxY));
		}
		if (fu_value == null) {
			fu_value = new ArrayList<Object>();
		}
		createCell(row, halfy + 1, "记录数:" + fu_value.size() + "   日期:" + TypeChange.dateToString(new Date()),
				wcf_futitle_right);
		// 写入条件
		x++;
		if (conditionList != null) {
			for (int i = 0; i < conditionList.size(); i++) {
				sheet.addMergedRegion(new CellRangeAddress(x, x, 0, maxY));
				row = sheet.createRow(x);
				row.setHeightInPoints(18);
				createCell(row, 0, conditionList.get(i).toString(), wcf_futitle);
				x++;
			}
		}
		drawColumnHeader(allColumns, groupcolumns, rowCount, colCount);
		// 有分类小计
		boolean unitalone = setting.isUnitalone();
		if (groupcolumns == 2) {
			// 要先创建每一个表格
			for (int i = 0; i < rowCount + (hasUnit && unitalone ? 1 : 0); i++) {
				Row btrow = sheet.getRow(x + i);
				for (int j = 0; j < 2; j++) {
					Cell btcell = btrow.createCell(j);
					btcell.setCellStyle(wcf_tabletitle);
				}
			}
			if (rowCount > 1 || (hasUnit && unitalone)) {
				sheet.addMergedRegion(new CellRangeAddress(x, x + rowCount - 1 + (hasUnit && unitalone ? 1 : 0), 0, 0));
				sheet.addMergedRegion(new CellRangeAddress(x, x + rowCount - 1 + (hasUnit && unitalone ? 1 : 0), 1, 1));
			}
			row = sheet.getRow(x);
			createCell(row, 0, group.getTitle(), wcf_tabletitle);
			createCell(row, 1, "记录数", wcf_tabletitle);
		}
		x += rowCount;
		if (hasUnit && unitalone) {
			x++;
		}
	}

	/**
	 * 在sheet中画出表头组
	 * 
	 * @param rowCount
	 * @param colCount
	 * @param allColumns
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void drawColumnHeader(List<ExcelColumn> allColumns, int gcol, Integer rowCount, Integer colCount) {
		boolean unitalone = setting.isUnitalone();
		// 如果有unittext 并且 要单独一行，就加1
		for (int i = 0; i < rowCount + (hasUnit && unitalone ? 1 : 0); i++) {
			Row btrow = sheet.createRow(x + i);
			for (int j = 0; j < colCount; j++) {
				Cell btcell = btrow.createCell(j + gcol);
				btcell.setCellStyle(wcf_tabletitle);
			}
		}

		for (ExcelColumn column : allColumns) {
			if (column.getFirstCol() != column.getLastCol() || column.getFirstRow() != column.getLastRow()) {
				sheet.addMergedRegion(new CellRangeAddress(x + column.getFirstRow(), x + column.getLastRow(),
						column.getFirstCol() + gcol, column.getLastCol() + gcol));
			}
			Row row = sheet.getRow(x + column.getFirstRow());
			Cell cell = row.getCell(column.getFirstCol() + gcol);
			cell.setCellStyle(wcf_tabletitle);
			cell.setCellValue(column._getText("", unitalone).replaceAll("--", "\n"));
			// 判断是否最后一行
			if (hasUnit && unitalone && StringUtils.isNotBlank(column.getUnittext())
					&& column.getLastRow() == rowCount - 1) {
				row = sheet.getRow(x + rowCount);
				cell = row.getCell(column.getFirstCol() + gcol);
				cell.setCellStyle(wcf_tabletitle);
				cell.setCellValue(column.getUnittext());
			}
		}
	}

	private Cell createCell(Row row, int col, String value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	/*
	 * 还要在此处计算，每一列写入浮点数的总和
	 */
	private Cell createCell(Row row, int col, Double value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(style);
		if (value != null) {
			cell.setCellValue(value);
		}
		return cell;
	}

	private Cell createCell(Row row, int col, Integer value, CellStyle style) {
		Cell cell = row.createCell(col);
		if (value != null && value != 0) {
			cell.setCellValue(value);
		}
		cell.setCellStyle(style);
		return cell;
	}

	private Cell createCell(Row row, int col, Date value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public ExcelExportSetting getSetting() {
		return setting;
	}

	public void setSetting(ExcelExportSetting setting) {
		this.setting = setting;
	}

	public class ExcelHeaderSpan {
		String title;
		int firstCol;
		int lastCol;
		boolean displayed;

		public ExcelHeaderSpan(String title, boolean displayed) {
			this.title = title;
			this.displayed = displayed;
			firstCol = 0;
			lastCol = 0;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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

		public boolean isDisplayed() {
			return displayed;
		}

		public void setDisplayed(boolean displayed) {
			this.displayed = displayed;
		}

	}
}
