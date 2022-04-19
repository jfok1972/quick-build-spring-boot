package com.jhopesoft.framework.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */
public class ExcelUtils {

	public static XSSFWorkbook createNewExcel(InputStream inputStream) throws IOException {
		XSSFWorkbook xls = new XSSFWorkbook(inputStream);
		return xls;
	}

	public static XSSFWorkbook createNewExcel(String template) throws Exception {
		OPCPackage pack = POIXMLDocument.openPackage(template);
		XSSFWorkbook doc = new XSSFWorkbook(pack);
		return doc;
	}

	public static void replace(XSSFSheet sheet, Object record, Map<String, Object> recordMap, int firstrow,
			FDataobject dataobject) throws OgnlException {
		writeOneToManyData(sheet, record, firstrow, dataobject);
		// 获得总行数
		int rowNum = sheet.getLastRowNum();
		for (int row = firstrow; row < rowNum + 1; row++) {
			// 每一行的列数
			XSSFRow arow = (XSSFRow) sheet.getRow(row);
			if (arow != null) {
				for (int col = 0; col < arow.getLastCellNum(); col++) {
					Cell cell = arow.getCell(col);
					if (cell == null) {
						continue;
					}
					writeCell(sheet, cell, record, recordMap, dataobject);
				}
			}
		}
	}

	public static void writeCell(XSSFSheet sheet, Cell cell, Object record, Map<String, Object> recordMap,
			FDataobject dataobject) {
		// 编制的单元格时所有的单元格都是String，要根据单元格里的内容把他区分开来
		if (cell.getCellType() == CellType.STRING) {
			String strValue = cell.getStringCellValue();
			// 只有一个{},那么取得该值的实际值
			if (strValue.startsWith("{") && strValue.endsWith("}")
					&& strValue.substring(1, strValue.length() - 1).indexOf('{') == -1) {
				String field = strValue.substring(1, strValue.length() - 1);
				String part[] = field.split("::");
				Object value = FieldTemplateTranslateUtils.getValue(part[0], record, recordMap, dataobject);
				if (value == null) {
					cell.setCellValue((String) null);
					return;
				}
				if (value != null && value.toString().equals(FieldTemplateTranslateUtils.NOSUCHPROPERTY)) {
					return;
				}
				String classname = value.getClass().getSimpleName().toLowerCase();
				if (part.length == Constants.INT_2) {
					if (part[1].equals("二维码")) {
						writeImage(sheet, cell, new ByteArrayInputStream(
								QrcodeUtils.createQrCode(value.toString(), 300, null).toByteArray()));
					} else if (part[1].equals("大写")) {
						cell.setCellValue(TypeChange.moneyFormatToUpper(((Number) value).doubleValue()));
					}
				} else if (classname.equals(Constants.DOUBLE)) {
					cell.setCellValue(((Double) value).doubleValue());
				} else if (classname.equals(Constants.FLOAT)) {
					cell.setCellValue(((Float) value).doubleValue());
				} else if (classname.equals(Constants.BYTE)) {
					cell.setCellValue(((Byte) value).intValue());
				} else if (classname.equals(Constants.SHORT)) {
					cell.setCellValue(((Short) value).intValue());
				} else if (classname.equals(Constants.INTEGER)) {
					cell.setCellValue(((Integer) value).intValue());
				} else if (classname.equals(Constants.LONG)) {
					cell.setCellValue(((Long) value).intValue());
				} else if (classname.equals(Constants.BIGDECIMAL)) {
					cell.setCellValue(((java.math.BigDecimal) value).doubleValue());
				} else if (classname.equals(Constants.BOOLEAN)) {
					cell.setCellValue(((Boolean) value).booleanValue());
				} else if (classname.equals(Constants.TIMESTAMP)) {
					cell.setCellValue(((java.sql.Timestamp) value));
				} else if (classname.equals(Constants.DATE)) {
					// Java.sql.date 和 java.util.date
					cell.setCellValue(((java.util.Date) value));
				} else if (classname.equals("byte[]")) {
					// byte做为图片处理，如果是oracle那么还要处理
					writeImage(sheet, cell, new ByteArrayInputStream((byte[]) value));
				} else {
					cell.setCellValue(value.toString());
				}
			} else {
				Set<String> templateWord = CommonUtils.getAllTemplateWord(strValue);
				if (templateWord.size() > 0) {
					for (String key : templateWord) {
						// 这里先不要用字段替换
						String value = FieldTemplateTranslateUtils.getStringValue(key, record, recordMap, null);
						if (value.equals(FieldTemplateTranslateUtils.NOSUCHPROPERTY)) {
							continue;
						}
						strValue = strValue.replace(key, value);
					}
				}
				cell.setCellValue(strValue);
			}
		}
	}

	private static void writeImage(XSSFSheet sheet, Cell cell, InputStream stream) {
		// byte做为图片处理，如果是oracle那么还要处理
		cell.setCellValue((String) null);
		BufferedImage bufferImg = null;
		// 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			bufferImg = ImageIO.read(stream);
			ImageIO.write(bufferImg, Constants.JPG, byteArrayOut);
			// 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
			XSSFDrawing patriarch = sheet.getDrawingPatriarch();
			if (patriarch == null) {
				patriarch = (XSSFDrawing) sheet.createDrawingPatriarch();
			}
			// anchor主要用于设置图片的属性
			int col = cell.getColumnIndex();
			int row = cell.getRowIndex();
			CellRangeAddress range = getCellRange(sheet, row, col);
			XSSFClientAnchor anchor;
			// 图像的边距
			int margin = 2 * Units.EMU_PER_POINT;
			if (range == null) {
				anchor = new XSSFClientAnchor(margin, margin, -margin, -margin, col, row, col + 1, row + 1);
			} else {
				anchor = new XSSFClientAnchor(margin, margin, -margin, -margin, range.getFirstColumn(),
						range.getFirstRow(), range.getLastColumn() + 1, range.getLastRow() + 1);
			}
			// 插入图片
			@SuppressWarnings("unused")
			XSSFPicture picture = patriarch.createPicture(anchor,
					sheet.getWorkbook().addPicture(byteArrayOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据excel中的 onetomany 标签来写入 onetomany 数据
	 * 
	 * @param sheet
	 * @param record
	 * @throws OgnlException
	 */
	public static void writeOneToManyData(XSSFSheet sheet, Object record, int firstrow, FDataobject dataobject)
			throws OgnlException {
		// 共有多少行
		int rows = sheet.getLastRowNum();
		boolean found = false;
		for (int row = firstrow; row < rows + 1; row++) {
			XSSFRow arow = (XSSFRow) sheet.getRow(row);
			if (arow != null) {
				Cell cell = arow.getCell(0);
				if (cell != null && cell.getStringCellValue().equalsIgnoreCase("OneToMany")) {
					found = true;
					String oneToManyName = arow.getCell(1).getStringCellValue();
					Collection<?> records = (Collection<?>) ognlExpressLevel(oneToManyName, record);
					int count = records.size();
					// @OneToMany fieldname row
					// 标题行 row+1
					// 数据行 row+2
					if (count > 0) {
						int first = row + 2;
						if (sheet.getLastRowNum() < first + 2) {
							sheet.createRow(first + 1);
						}
						// 把这条记录下面的第二条，往下移count- 1 条
						sheet.shiftRows(first + 1, sheet.getLastRowNum(), count - 1, true, false);
						shiftImge(sheet, first + 1, count - 1);
						for (int i = 1; i < count; i++) {
							sheet.copyRows(first, first, first + i, new CellCopyPolicy());
						}
						// 写入每一行数据,使用iterator
						int i = 0;
						Iterator<?> iterator = records.iterator();
						while (iterator.hasNext()) {
							Object detailRecord = iterator.next();
							// 每一行的列数
							XSSFRow recordrow = (XSSFRow) sheet.getRow(first + i);
							for (int col = 0; col < recordrow.getLastCellNum(); col++) {
								Cell acell = recordrow.getCell(col);
								if (acell == null) {
									continue;
								}
								writeCell(sheet, acell, detailRecord, null, dataobject);
							}
							i++;
						}
					}
					sheet.removeRow(arow);
					sheet.shiftRows(row + 1, sheet.getLastRowNum(), -1, true, false);
					shiftImge(sheet, row + 1, -1);
					XSSFRow lastrow = sheet.getRow(sheet.getLastRowNum());
					if (lastrow != null) {
						sheet.removeRow(lastrow);
					}
					break;
				}
			}
		}
		if (found) {
			// 如果找到了一个，那么找找有没有其他的了
			writeOneToManyData(sheet, record, firstrow, dataobject);
		}
	}

	/**
	 * 移动了行和列以后，这行下面的所有图像都要移动
	 * 
	 * @param sheet
	 * @param startRow
	 * @param n
	 */
	private static void shiftImge(XSSFSheet sheet, int startRow, int n) {
		for (POIXMLDocumentPart dr : sheet.getRelations()) {
			if (dr instanceof XSSFDrawing) {
				XSSFDrawing drawing = (XSSFDrawing) dr;
				List<XSSFShape> shapes = drawing.getShapes();
				for (XSSFShape shape : shapes) {
					XSSFPicture pic = (XSSFPicture) shape;
					// 可能变形
					// XSSFClientAnchor anchor = pic.getPreferredSize()
					XSSFClientAnchor anchor = pic.getClientAnchor();
					int row = anchor.getRow1();
					if (row >= startRow) {
						anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
						anchor.setRow1(anchor.getRow1() + n);
						anchor.setRow2(anchor.getRow2() + n);
					}
				}
			}
		}
	}

	/**
	 * 取得指定单元格的合并单元地址，如果不是合并，返回null
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	private static CellRangeAddress getCellRange(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			if (row == range.getFirstRow() && column == range.getFirstColumn()) {
				return range;
			}
		}
		return null;
	}

	/**
	 * 一级一级的解释，如果有一级为空，那么不要解释下一级了
	 * 
	 * @param express
	 * @param record
	 * @return
	 * @throws OgnlException
	 */
	public static Object ognlExpressLevel(final String express, final Object record) throws OgnlException {
		String[] exps = express.split("\\.");
		String exp = "";
		for (int i = 0; i < exps.length; i++) {
			exp += (i == 0 ? "" : ".") + exps[i];
			Object value = Ognl.getValue(exp, record);
			if (value == null) {
				return null;
			}
		}
		return Ognl.getValue(express, record);
	}
}
