package com.jhopesoft.framework.core.datamining.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.core.datamining.utils.ExcelExport;
import com.jhopesoft.framework.core.objectquery.export.ExcelColumn;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.PdfUtils;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Service
public class DataminingExportService {

	public void exportToExcel(String moduletitle, String schemename, String conditions, List<ExcelColumn> columns,
			List<ExcelColumn> leafcolumns, String data, boolean colorless, int monerary, String moneraryText,
			boolean disablerowgroup, boolean unittextalone, boolean topdf, String pagesize, boolean autofitwidth,
			short scale) throws IOException {

		// 计算多层表头的参数

		ExcelColumn[] cs = new ExcelColumn[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			cs[i] = columns.get(i);
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

		ExcelExport excelExport = new ExcelExport(pagesize, autofitwidth, scale, colorless, monerary, moneraryText,
				disablerowgroup, unittextalone);
		OutputStream os = excelExport.exportToExcel(schemename, conditions, allColumns, leafcolumns,
				JSONObject.parseObject(data), rowCount, colCount);

		String filename = moduletitle + "--" + schemename;
		if (topdf) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			InputStream inputstream = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
			PdfUtils.convert(inputstream, output, Constants.XLSX, Constants.PDF);
			CommonFunction.download(output, filename + Constants.DOTPDF, Local.getResponse());
		} else {
			CommonFunction.download(os, filename + Constants.DOTXLSX, Local.getResponse());
		}

	}

}
