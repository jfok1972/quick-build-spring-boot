package com.jhopesoft.platform.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.ExcelExportSetting;
import com.jhopesoft.framework.bean.FieldType;
import com.jhopesoft.framework.bean.GridParams;
import com.jhopesoft.framework.bean.GroupParameter;
import com.jhopesoft.framework.bean.SortParameter;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumn;
import com.jhopesoft.framework.core.objectquery.export.ExcelExportPOI;
import com.jhopesoft.framework.core.objectquery.export.ExcelExportPOI_NOCACHE;
import com.jhopesoft.framework.core.objectquery.filter.UserDefineFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;
import com.jhopesoft.framework.dao.entity.datainorout.FRecordexcelscheme;
import com.jhopesoft.framework.dao.entity.datainorout.FRecordprintscheme;
import com.jhopesoft.framework.dao.entity.datainorout.FRecordprintschemegroup;
import com.jhopesoft.framework.dao.entity.datainorout.FRecordprintschemegroupcell;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectview;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormscheme;
import com.jhopesoft.framework.dao.entity.viewsetting.FovFormschemedetail;
import com.jhopesoft.framework.dao.entity.viewsetting.FovGridsortscheme;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.ExcelUtils;
import com.jhopesoft.framework.utils.FieldTemplateTranslateUtils;
import com.jhopesoft.framework.utils.PdfUtils;
import com.jhopesoft.framework.utils.WordUtils;

/**
 * 
 * @author ?????? jfok1972@qq.com
 * 
 */

@Service
public class DataExportService {

	private static final String SUBDISPLAYDATAINDEXS = "subDisplayDataIndexs";
	private static final String SUBREMOVEDDATAINDEXS = "subRemovedDataIndexs";
	private static final String DATAINDEXS = "dataIndexs";
	private static final int MAX_LINES = 60000;

	@Autowired
	private DataObjectService dataObjectService;

	@Autowired
	private DaoImpl dao;

	@Autowired
	private AttachmentService attachmentService;

	/**
	 * ??????formscheme???????????????????????????excel
	 * ???formscheme??????form??????????????????treemodeexcelexport-????????????Excel????????????????????????gridmodeexcelexport-??????Excel???????????????
	 * ??????????????????excel???????????????????????????????????????treemodeexcelexport????????????????????????????????????gridmodeexcelexport????????????grid???
	 * 
	 * treemodeexcelexport?????????????????????????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????LAbel????????????????????????????????????????????????????????????
	 * ???????????????tip???????????????????????????????????????????????????node?????????????????????
	 * 
	 */
	@SuppressWarnings("unchecked")
	public OutputStream generateTreeModeExcel(String moduleName, GridParams pg, List<ExcelColumn> exportColumns,
			List<SortParameter> sort, GroupParameter group, List<UserDefineFilter> query, List<UserDefineFilter> filter,
			List<UserNavigateFilter> navigates, List<UserParentFilter> userParentFilters,
			List<UserDefineFilter> conditions, FDataobjectview viewscheme, FovGridsortscheme sortscheme,
			JSONObject sqlparam, String formschemeid, ExcelExportSetting setting) {
		FovFormscheme formScheme = dao.findById(FovFormscheme.class, formschemeid);
		// ???????????????????????????????????????????????????grid????????????
		boolean isTreeList = "treemodeexcelexport".equals(formScheme.getFormtype());
		// ???????????????????????????????????????????????????????????????????????????
		List<?> resultList = dataObjectService.fetchDataInner(moduleName, pg, null, group, sort, query, filter,
				navigates, userParentFilters, viewscheme, sortscheme, sqlparam).getData();

		List<JSONObject> treeLevelList = null;
		List<Map<String, Object>> treeResultList = null;
		if (isTreeList) {
			// ???????????????????????????resultlist,?????????,??????????????????????????????????????????????????????????????????????????????
			// ExcelColumn[] cs ??????????????????????????????????????????
			treeLevelList = new ArrayList<JSONObject>();
			// ????????????????????????
			ExcelColumn[] cs = getColumnDetails(formScheme.getDetails(), true);
			for (ExcelColumn column : cs) {
				JSONObject treelevelDefine = new JSONObject();
				// ??????????????????key???
				List<ExcelColumn> columns = column.getAllLeafItems();
				treelevelDefine.put(Constants.KEY, columns.get(0).getDataIndex());
				// ????????????????????????dataIndex,
				List<String> dataIndexs = new ArrayList<String>();
				// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				List<String> subDisplayDataIndexs = new ArrayList<String>();

				for (ExcelColumn subcolumn : columns) {
					// ?????????????????????????????????
					FovFormschemedetail detail = (FovFormschemedetail) subcolumn.getTargetObject();
					if (BooleanUtils.isTrue(detail.getCollapsible())) {
						// ???????????????????????????key
						treelevelDefine.put(Constants.KEY, subcolumn.getDataIndex());
					}
					// ???????????????????????????tip
					if (BooleanUtils.isTrue(detail.getShowdetailtip())) {
						subDisplayDataIndexs.add(subcolumn.getDataIndex());
					}
					dataIndexs.add(subcolumn.getDataIndex());
				}
				treelevelDefine.put(DATAINDEXS, dataIndexs);
				treelevelDefine.put(SUBDISPLAYDATAINDEXS, subDisplayDataIndexs);
				// ??????node?????????????????????
				List<String> subRemovedDataIndexs = new ArrayList<String>();
				for (String s : dataIndexs) {
					if (!subDisplayDataIndexs.contains(s)) {
						subRemovedDataIndexs.add(s);
					}
				}
				treelevelDefine.put(SUBREMOVEDDATAINDEXS, subRemovedDataIndexs);
				// ??????????????????????????????
				treeLevelList.add(treelevelDefine);
			}
			// ???????????????resultlist????????????????????????????????????,?????????????????????????????????????????????????????????????????????excel?????????????????????
			int level = treeLevelList.size() - 1;
			String[] key = new String[level];
			for (int i = 0; i < level; i++) {
				key[i] = "__undefined__";
			}
			// ????????????????????????????????????????????????????????????????????????????????????????????? ??????count???????????????1
			Object[] pcount = new Object[level];

			treeResultList = new ArrayList<Map<String, Object>>();
			for (Object object : resultList) {
				// ????????????
				Map<String, Object> map = (Map<String, Object>) object;
				// ?????????????????????
				for (int i = 0; i < level; i++) {
					if (key[i].equals(map.get(treeLevelList.get(i).get(Constants.KEY)).toString())) {
						;
					} else {
						// key ?????????????????????????????????tree
						key[i] = map.get(treeLevelList.get(i).get(Constants.KEY)).toString();
						Map<String, Object> treenode = new HashMap<String, Object>(0);
						treenode.put(COUNT__, 0);
						// ???????????????????????????????????????????????????
						treenode.put(LEVEL__, i);
						// ?????????????????????????????????
						for (String dataindex : (List<String>) treeLevelList.get(i).get(DATAINDEXS)) {
							treenode.put(dataindex, map.get(dataindex));
						}
						// ?????????????????????????????????subDisplayDataIndexs??????????????????
						for (int j = 0; j < i; j++) {
							for (String dataindex : (List<String>) treeLevelList.get(j).get(SUBDISPLAYDATAINDEXS)) {
								treenode.put(dataindex, map.get(dataindex));
							}
						}
						treeResultList.add(treenode);
						pcount[i] = treenode;
						// ??????????????????node,????????????????????????????????????
						for (int j = 0; j < i; j++) {
							Map<String, Object> ptreenode = (Map<String, Object>) pcount[j];
							ptreenode.put(COUNT__, (int) ptreenode.get(COUNT__) + 1);
						}
					}
					for (String dataindex : (List<String>) treeLevelList.get(i).get(SUBREMOVEDDATAINDEXS)) {
						map.remove(dataindex);
					}
				}
				// ????????????????????????????????????1
				for (int i = 0; i < level; i++) {
					Map<String, Object> treenode = (Map<String, Object>) pcount[i];
					treenode.put(COUNT__, (int) treenode.get(COUNT__) + 1);
				}
				treeResultList.add(map);
			}
		}
		// ????????????????????????
		ExcelColumn[] cs = getColumnDetails(formScheme.getDetails(), false);
		JSONObject rowCountJson = new JSONObject();
		rowCountJson.put(Constants.ROWCOUNT, 0);
		ExcelColumn.setColRowSize(cs, 0, 0, rowCountJson);
		ExcelColumn.setAllLastRow(cs, rowCountJson.getIntValue(Constants.ROWCOUNT));
		// ??????????????????dataIndexColumns??????????????????????????????????????????
		List<ExcelColumn> dataIndexColumns = new ArrayList<ExcelColumn>();
		ExcelColumn.genAllDataIndexColumns(cs, dataIndexColumns);
		if (isTreeList) {
			// ????????????????????????????????????????????????????????????0--????????????1--??????
			for (ExcelColumn column : dataIndexColumns) {
				inner: for (int i = 0; i < treeLevelList.size(); i++) {
					List<String> levelDataIndexs = (List<String>) treeLevelList.get(i).get(DATAINDEXS);
					for (String dataIndex : levelDataIndexs) {
						if (dataIndex.equals(column.getDataIndex())) {
							column.setTreeLevel(i);
							continue inner;
						}
					}
				}
			}
		}
		List<ExcelColumn> allColumns = new ArrayList<ExcelColumn>();
		ExcelColumn.genAllColumns(cs, allColumns);
		Integer rowCount = rowCountJson.getIntValue(Constants.ROWCOUNT) + 1;
		Integer colCount = dataIndexColumns.size();
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (resultList.size() > MAX_LINES) {
			return new ExcelExportPOI(setting, false).genExcel(module, formScheme.getSchemename(), conditions,
					isTreeList ? treeResultList : resultList, group, null, rowCount, colCount, allColumns,
					dataIndexColumns);
		} else {
			return new ExcelExportPOI_NOCACHE(setting, false).GenExcel(module, formScheme.getSchemename(), conditions,
					isTreeList ? treeResultList : resultList, group, null, rowCount, colCount, allColumns,
					dataIndexColumns);
		}
	}

	private static final String COUNT__ = "__count__";
	private static final String LEVEL__ = "__level__";

	private ExcelColumn[] getColumnDetails(Set<FovFormschemedetail> details, boolean all) {
		if (details != null && details.size() > 0) {
			List<ExcelColumn> result = new ArrayList<ExcelColumn>();
			for (FovFormschemedetail detail : details) {
				// ?????????????????????label,??????????????????
				if (!all && BooleanUtils.isTrue(detail.getHiddenlabel())) {
					continue;
				}
				ExcelColumn column = new ExcelColumn();
				column.setTargetObject(detail);
				FDataobjectfield field = detail.getFDataobjectfield();
				if (field == null) {
					column.setText(detail.getTitle());
					column.setItems(getColumnDetails(detail.getDetails(), all));
				} else {
					if (BooleanUtils.isTrue(field.getIshidden())) {
						continue;
					}
					column.setText(field.getFieldtitle());
					column.setFormFieldId(detail.getDetailid());
					column.setAggregate(detail.getAggregate());
					column.setUnittext(field.getUnittext());
					column.setIsmonetary(BooleanUtils.isTrue(field.getIsmonetary()));
					if (detail.getFieldahead() == null) {
						column.setDataIndex(field.getFieldname());
					} else {
						column.setDataIndex(detail.getAdditionFieldname());
					}
					if (field.getFDictionary() != null) {
						column.setDataIndex(column.getDataIndex() + "_dictname");
					}
					if (field._isManyToOne() || field._isManyToOne()) {
						FDataobject dataobject = DataObjectUtils.getDataObject(field.getFieldtype());
						column.setDataIndex(column.getDataIndex() + "." + dataobject.getNamefield());
					}
				}
				result.add(column);
			}
			ExcelColumn[] columns = new ExcelColumn[result.size()];
			result.toArray(columns);
			return columns;
		}
		return null;
	}

	/**
	 * ???????????????grid?????????????????????excel???
	 */
	@SuppressWarnings("rawtypes")
	public OutputStream generateExcel(String moduleName, GridParams pg, List<ExcelColumn> exportColumns,
			List<SortParameter> sort, GroupParameter group, List<UserDefineFilter> query, List<UserDefineFilter> filter,
			List<UserNavigateFilter> navigates, List<UserParentFilter> userParentFilters,
			List<UserDefineFilter> conditions, FDataobjectview viewscheme, FovGridsortscheme sortscheme,
			JSONObject sqlparam, boolean toPdf, ExcelExportSetting setting) {

		ExcelColumn[] cs = new ExcelColumn[exportColumns.size()];

		for (int i = 0; i < exportColumns.size(); i++) {
			cs[i] = exportColumns.get(i);
		}
		JSONObject rowCountJson = new JSONObject();
		rowCountJson.put(Constants.ROWCOUNT, 0);

		ExcelColumn.setColRowSize(cs, 0, 0, rowCountJson);
		ExcelColumn.setAllLastRow(cs, rowCountJson.getIntValue(Constants.ROWCOUNT));

		List<ExcelColumn> dataIndexColumns = new ArrayList<ExcelColumn>();
		ExcelColumn.genAllDataIndexColumns(cs, dataIndexColumns);

		List<ExcelColumn> allColumns = new ArrayList<ExcelColumn>();
		ExcelColumn.genAllColumns(cs, allColumns);

		Integer rowCount = rowCountJson.getIntValue(Constants.ROWCOUNT) + 1;
		Integer colCount = dataIndexColumns.size();

		List<?> resultList = dataObjectService.fetchDataInner(moduleName, pg, null, group, sort, query, filter,
				navigates, userParentFilters, viewscheme, sortscheme, sqlparam).getData();
		FDataobject module = DataObjectUtils.getDataObject(moduleName);
		if (resultList == null) {
			resultList = new ArrayList();
		}
		if (resultList.size() > MAX_LINES) {
			return new ExcelExportPOI(setting, toPdf).genExcel(module, module.getTitle(), conditions, resultList, group,
					null, rowCount, colCount, allColumns, dataIndexColumns);
		} else {
			return new ExcelExportPOI_NOCACHE(setting, toPdf).GenExcel(module, module.getTitle(), conditions,
					resultList, group, null, rowCount, colCount, allColumns, dataIndexColumns);
		}
	}

	public void exportExcelScheme(String objectid, String schemeid, String recordids, String filetype, boolean inline)
			throws Exception {
		FRecordexcelscheme scheme = dao.findById(FRecordexcelscheme.class, schemeid);
		FDataobject dataobject = scheme.getFDataobject();
		FDataobject excelSchemeobject = DataObjectUtils.getDataObject(FRecordexcelscheme.class.getSimpleName());

		FDataobjectattachment attachment = dao.findByPropertyFirst(FDataobjectattachment.class, Constants.OBJECTID,
				excelSchemeobject.getObjectid(), Constants.IDVALUE, schemeid);
		if (attachment == null) {
			CommonFunction.downloadFileError(Local.getResponse(), "???????????????????????????????????????!", null);
			return;
		}
		String[] ids;
		if (BooleanUtils.isTrue(scheme.getAllowrecords())) {
			ids = recordids.split(Constants.COMMA);
		} else {
			ids = new String[] { recordids.split(Constants.COMMA)[0] };
		}
		FDataobject recordObject = DataObjectUtils.getDataObject(objectid);
		Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(objectid, ids[0]);
		String recordtitle = recordMap.get(dataobject.getNamefield()).toString();
		Object record = null;
		if (StringUtils.isNotBlank(recordObject.getClassname())) {
			Class<?> objectClass = Class.forName(recordObject.getClassname());
			record = dao.findById(objectClass, ids[0]);
		}
		String filename = recordtitle + "--" + scheme.getTitle();
		// ?????????????????????
		InputStream inputStream = attachmentService.getOriginalFileStream(attachment);
		if (attachment.getSuffixname().equalsIgnoreCase(Constants.XLSX)) {
			// excel??????
			XSSFWorkbook excelDocument = ExcelUtils.createNewExcel(inputStream);
			if (BooleanUtils.isTrue(scheme.getMultisheet())) {
				createExcelSheets(recordObject, ids, excelDocument);
			} else {
				createOneByOne(recordObject, ids, excelDocument, scheme);
			}

			OutputStream fopts = new ByteArrayOutputStream();
			excelDocument.write(fopts);

			if (ids.length > 1) {
				filename = recordtitle + "???" + ids.length + "???--" + scheme.getTitle();
			}
			filename = filename + '.' + attachment.getSuffixname();
			if (filetype != null && Constants.PDF.equals(filetype)) {
				ByteArrayOutputStream pdfos = new ByteArrayOutputStream();
				InputStream inputstream = new ByteArrayInputStream(((ByteArrayOutputStream) fopts).toByteArray());
				PdfUtils.convert(inputstream, pdfos, Constants.XLSX, Constants.PDF);
				if (inline) {
					CommonFunction.downloadAndOpenPdf(pdfos, filename.replace(Constants.DOTXLSX, Constants.DOTPDF),
							Local.getResponse());
				} else {
					CommonFunction.download(pdfos, filename.replace(Constants.DOTXLSX, Constants.DOTPDF),
							Local.getResponse());
				}
			} else {
				CommonFunction.download(fopts, filename, Local.getResponse());
			}
		} else {
			// word??????,?????????????????????
			XWPFDocument wordDocument = WordUtils.createNewWord(inputStream);
			Set<String> templateWord = WordUtils.getAllTemplateWord(wordDocument);
			Map<String, Object> param = new HashMap<String, Object>(0);
			for (String key : templateWord) {
				String value = FieldTemplateTranslateUtils.getStringValue(key, record, recordMap, dataobject);
				if (value.equals(FieldTemplateTranslateUtils.NOSUCHPROPERTY)) {
					continue;
				}
				param.put(key, value);
			}
			WordUtils.replace(wordDocument, param);
			OutputStream fopts = new ByteArrayOutputStream();
			wordDocument.write(fopts);
			filename = filename + '.' + attachment.getSuffixname();
			if (filetype != null && Constants.PDF.equals(filetype)) {
				ByteArrayOutputStream pdfos = new ByteArrayOutputStream();
				InputStream inputstream = new ByteArrayInputStream(((ByteArrayOutputStream) fopts).toByteArray());
				PdfUtils.convert(inputstream, pdfos, "docx", Constants.PDF);
				if (inline) {
					CommonFunction.downloadAndOpenPdf(pdfos, filename.replace(".docx", Constants.DOTPDF),
							Local.getResponse());
				} else {
					CommonFunction.download(pdfos, filename.replace(".docx", Constants.DOTPDF), Local.getResponse());
				}
			} else {
				CommonFunction.download(fopts, filename, Local.getResponse());
			}
		}
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????????????????????????? ??? start????????????end???????????? ??????????????????????????????????????????????????????
	 * ?????????????????????????????????????????????????????????
	 */

	public void createOneByOne(FDataobject dataobject, String ids[], XSSFWorkbook excelDocument,
			FRecordexcelscheme scheme) throws Exception {
		XSSFSheet sheet = excelDocument.getSheetAt(0);
		int modelRow = sheet.getLastRowNum();
		for (int i = 1; i < ids.length; i++) {
			String recordid = ids[i];
			Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(dataobject.getObjectid(), recordid);
			Object record = null;
			if (StringUtils.isNotBlank(dataobject.getClassname())) {
				Class<?> objectClass = Class.forName(dataobject.getClassname());
				record = dao.findById(objectClass, recordid);
			}
			// ?????????0-allrow,????????????????????? ??????1????????? ????????????
			int firstRow = sheet.getLastRowNum() + 1;
			sheet.copyRows(scheme.getStartrow() == null ? 0 : scheme.getStartrow(), modelRow, firstRow,
					new CellCopyPolicy());
			ExcelUtils.replace(sheet, record, recordMap, firstRow, dataobject);
		}
		// ???????????????
		String recordid = ids[0];
		Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(dataobject.getObjectid(), recordid);
		Object record = null;
		if (StringUtils.isNotBlank(dataobject.getClassname())) {
			Class<?> objectClass = Class.forName(dataobject.getClassname());
			record = dao.findById(objectClass, recordid);
		}
		// ?????????onetomany????????????????????????????????????????????????
		sheet.createRow(sheet.getLastRowNum() + 1);
		ExcelUtils.replace(sheet, record, recordMap, 0, dataobject);

		// ????????????model?????????????????????
		// List<Integer> removed = new ArrayList<Integer>();
		// int sheetMergeCount = sheet.getNumMergedRegions();
		// for (int i = 0; i < sheetMergeCount; i++) {
		// CellRangeAddress range = sheet.getMergedRegion(i);
		// if (range.getFirstRow() <= modelRow) {
		// removed.add(i);
		// }
		// }
		// sheet.removeMergedRegions(removed);
		// for (int i = 0; i < modelRow; i++) {
		// Row row = sheet.getRow(i);
		// if (row != null)
		// sheet.removeRow(sheet.getRow(i));
		// }
		// sheet.shiftRows(modelRow + 1, sheet.getLastRowNum(), -modelRow - 1, true,
		// false);
	}

	/**
	 * ????????????????????????sheet?????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????????????????????????????????????????
	 * 
	 */

	public void createExcelSheets(FDataobject dataobject, String ids[], XSSFWorkbook excelDocument) throws Exception {
		XSSFSheet firstSheet = excelDocument.getSheetAt(0);
		for (int i = 1; i < ids.length; i++) {
			String recordid = ids[i];
			Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(dataobject.getObjectid(), recordid);
			String recordtitle = recordMap.get(dataobject.getNamefield()).toString();
			Object record = null;
			if (StringUtils.isNotBlank(dataobject.getClassname())) {
				Class<?> objectClass = Class.forName(dataobject.getClassname());
				record = dao.findById(objectClass, recordid);
			}
			XSSFSheet sheet = excelDocument.cloneSheet(0, getUniqueTitle(excelDocument, recordtitle, 0));
			BeanUtils.copyProperties(firstSheet.getPrintSetup(), sheet.getPrintSetup());
			ExcelUtils.replace(sheet, record, recordMap, 0, dataobject);
		}

		String recordid = ids[0];
		Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(dataobject.getObjectid(), recordid);
		String recordtitle = recordMap.get(dataobject.getNamefield()).toString();
		Object record = null;
		if (StringUtils.isNotBlank(dataobject.getClassname())) {
			Class<?> objectClass = Class.forName(dataobject.getClassname());
			record = dao.findById(objectClass, recordid);
		}
		excelDocument.setSheetName(0, getUniqueTitle(excelDocument, recordtitle, 0));
		ExcelUtils.replace(firstSheet, record, recordMap, 0, dataobject);
	}

	/**
	 * ??????sheetname??????,????????????????????????sheetName
	 * 
	 * @param excelDocument
	 * @param title
	 * @param i
	 * @return
	 */
	public String getUniqueTitle(XSSFWorkbook excelDocument, String title, int i) {
		title = title.replace('"', '\'').replace('/', ' ').replace('\\', ' ').replace(':', ' ').replace('*', ' ')
				.replace('<', ' ').replace('>', ' ').replace('|', ' ').replace('?', ' ').replace('[', ' ')
				.replace(']', ' ');
		if (excelDocument.getSheet(title + (i == 0 ? "" : i)) != null) {
			return getUniqueTitle(excelDocument, title, i + 1);
		} else {
			return title + (i == 0 ? "" : i);
		}
	}

	public String getModuleTitle(String fieldids) {
		List<String> fields = new ArrayList<String>(Arrays.asList(fieldids.split(Constants.COMMA)));
		FDataobjectfield field = dao.findById(FDataobjectfield.class, fields.get(0));
		return field.getFDataobject().getTitle();
	}

	public OutputStream exportExcelTemplate(String fieldids) {
		OutputStream os = new ByteArrayOutputStream();
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet();
		List<String> fields = new ArrayList<String>(Arrays.asList(fieldids.split(Constants.COMMA)));

		short doubleformat = workbook.createDataFormat().getFormat("#,##0.00;[Red]-#,##0.00;_(* \"-\"??_)");
		short percentformat = workbook.createDataFormat().getFormat("0.00%;[Red]-0.00%;_(* \"-\"??_)");
		short dateformat = workbook.createDataFormat().getFormat(Constants.DATE_FORMAT);
		short datetimeformat = workbook.createDataFormat().getFormat("yyyy-MM-dd hh:mm");

		CellStyle normalDouble = workbook.createCellStyle();
		normalDouble.setAlignment(HorizontalAlignment.RIGHT);
		normalDouble.setDataFormat(doubleformat);

		CellStyle normalInt = workbook.createCellStyle();
		normalInt.setAlignment(HorizontalAlignment.RIGHT);

		CellStyle normalPercent = workbook.createCellStyle();
		normalPercent.setAlignment(HorizontalAlignment.RIGHT);
		normalPercent.setDataFormat(percentformat);

		CellStyle normalDate = workbook.createCellStyle();
		normalDate.setAlignment(HorizontalAlignment.CENTER);
		normalDate.setDataFormat(dateformat);

		CellStyle normalDatetime = workbook.createCellStyle();
		normalDatetime.setAlignment(HorizontalAlignment.CENTER);
		normalDatetime.setDataFormat(datetimeformat);

		int[] rownumber = new int[1];
		rownumber[0] = 0;
		try {
			fields.forEach(fieldid -> {
				FDataobjectfield field = dao.findById(FDataobjectfield.class, fieldid);
				if (field != null) {
					Row row = sheet.createRow(rownumber[0]++);
					Cell cell = row.createCell(0);
					cell.setCellValue(field.getFieldtitle());
					cell = row.createCell(1);
					String fieldname = field.getFieldname();
					if (field._isManyToOne() || field._isManyToOne()) {
						FDataobject pobject = DataObjectUtils.getDataObject(field.getFieldtype());
						if (pobject != null) {
							fieldname = fieldname + "." + pobject.getNamefield();
						}
					}
					if (StringUtils.isNotBlank(field.getFieldahead())) {
						fieldname = field.getFieldahead() + "." + fieldname;
					}
					// if (field.getFDictionary() != null) {
					// fieldname = fieldname + "_dictname";
					// }
					cell.setCellValue("{" + fieldname + "}");
					if (field._isFloatField()) {
						cell.setCellStyle(normalDouble);
					} else if (field._isIntegerField()) {
						cell.setCellStyle(normalInt);
					} else if (field._isPercentField()) {
						cell.setCellStyle(normalPercent);
					} else if (FieldType.Date.toString().equalsIgnoreCase(field.getFieldtype())) {
						cell.setCellStyle(normalDate);
					} else if (field._isDateField()) {
						cell.setCellStyle(normalDatetime);
					}
				}
			});

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

	/**
	 * ??????????????????????????????html
	 * 
	 * @param moduleName
	 * @param id
	 * @param schemeId
	 * @return
	 */
	public static final String DAXIE = "(??????)";
	public static final String SIGNPHOTO = "(??????)";

	public String genPrintHtml(String moduleName, String id, String schemeId) throws ClassNotFoundException {

		FDataobject recordObject = DataObjectUtils.getDataObject(moduleName);

		Map<String, Object> recordMap = dataObjectService.getObjectRecordMap(moduleName, id);
		CommonUtils.attachmentStrToObject(recordMap);
		CommonUtils.completeTaskInfoToObject(recordMap);
		Object record = null;
		if (StringUtils.isNotBlank(recordObject.getClassname())) {
			Class<?> objectClass = Class.forName(recordObject.getClassname());
			record = dao.findById(objectClass, id);
		}
		FRecordprintscheme scheme = dao.findById(FRecordprintscheme.class, schemeId);
		StringBuilder result = new StringBuilder();
		for (FRecordprintschemegroup group : scheme.getFRecordprintschemegroups()) {
			if (BooleanUtils.isNotTrue(group.getIsdisable())) {
				List<FRecordprintschemegroupcell> schemeGroupCells = new ArrayList<FRecordprintschemegroupcell>();
				// ??????disabled ???
				for (FRecordprintschemegroupcell cell : group.getFRecordprintschemegroupcells()) {
					if (!BooleanUtils.isTrue(cell.getIsdisable())) {
						schemeGroupCells.add(cell);
					}
				}
				result.append(group.genHtml(schemeGroupCells));
			}
		}
		// ??????????????????
		// .*? ???????????????????????????
		// ???????????????????????????{},???????????????
		String patternStr = "\\{.*?\\}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(result);
		StringBuffer resultBuffer = new StringBuffer();
		while (matcher.find()) {
			// ???????????????????????????
			String expressText = result.substring(matcher.start() + 1, matcher.end() - 1);
			boolean isDaXie = expressText.indexOf(DAXIE) != -1;
			boolean isSignPhoto = expressText.indexOf(SIGNPHOTO) != -1;
			// ????????????????????????
			String express = expressText;
			if (isDaXie) {
				express = expressText.replace(DAXIE, "");
			}
			if (isSignPhoto) {
				express = expressText.replace(SIGNPHOTO, "");
			}
			String expressValue = FieldTemplateTranslateUtils.getPrintStringValue(express, record, recordMap,
					recordObject);
			matcher.appendReplacement(resultBuffer, changeSpaceEnter(expressValue));
		}
		matcher.appendTail(resultBuffer);
		return resultBuffer.toString();

	}

	/**
	 * ????????????????????????html ??????
	 * 
	 * @param value
	 * @return
	 */
	public String changeSpaceEnter(Object value) {
		if (value == null) {
			return "";
		}
		String result = value.toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br/>").replaceAll(" ", "&nbsp;");
		result = result.replaceAll("\\{", "???").replaceAll("\\}", "???");
		return result;

	}
}
