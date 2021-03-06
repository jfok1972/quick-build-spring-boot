package com.jhopesoft.platform.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jhopesoft.framework.bean.FileUploadBean;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.DaoImpl;
import com.jhopesoft.framework.dao.entity.attachment.FAttachmentfiletype;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachmentfile;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachmentpdffile;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.dao.entity.dictionary.FDictionary;
import com.jhopesoft.framework.dao.entity.dictionary.FDictionarydetail;
import com.jhopesoft.framework.dao.entity.system.FCompany;
import com.jhopesoft.framework.dao.entity.system.FSysteminfo;
import com.jhopesoft.framework.utils.BeanUtils;
import com.jhopesoft.framework.utils.CommonFunction;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.FileUtils;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.framework.utils.PdfUtils;
import com.jhopesoft.platform.logic.define.LogicInterface;

import net.coobird.thumbnailator.Thumbnails;
import ognl.OgnlException;

/**
 * 
 * @author jiangfeng
 *
 */
@Service
public class AttachmentService {

	public static final Log logging = LogFactory.getLog(AttachmentService.class);

	@Autowired
	private DaoImpl dao;

	@Autowired
	private DataObjectService dataObjectService;

	/**
	 * ??????objectid???id??????????????????
	 * 
	 * @param objectid
	 * @param id
	 * @return
	 */
	public List<FDataobjectattachment> getAttachments(String objectid, String id) {
		List<FDataobjectattachment> attachments = dao.findByProperty(FDataobjectattachment.class, Constants.OBJECTID,
				objectid, Constants.IDVALUE, id);
		return attachments;
	}

	@SuppressWarnings("unchecked")
	public JSONObject upload(FileUploadBean uploaditem, BindingResult bindingResult) throws IllegalAccessException,
			InvocationTargetException, IOException, ClassNotFoundException, OgnlException {
		JSONObject result = new JSONObject();
		List<FDataobjectattachment> items = getAttachments(uploaditem.getObjectid(), uploaditem.getIdvalue());
		int recno = 0;
		for (FDataobjectattachment item : items) {
			if (((int) item.getOrderno()) > recno) {
				recno = item.getOrderno();
			}
		}
		MultipartFile file = uploaditem.getFile();
		FDataobjectattachment attachment = new FDataobjectattachment();
		BeanUtils.copyProperties(attachment, uploaditem);
		attachment.setOrderno(recno + 10);
		FDataobject dataobject = DataObjectUtils.getDataObject(uploaditem.getObjectid());
		attachment.setFDataobject(dataobject);

		if (!ObjectFunctionUtils.allowAddAttachment(attachment.getFDataobject())) {
			throw new RuntimeException("?????????????????????????????????");
		}

		if (attachment.getTitle() == null || attachment.getTitle().length() == 0) {
			attachment.setTitle(file.getOriginalFilename());
		}
		attachment.setCreater(Local.getUserid());
		attachment.setCreatedate(new Date());
		attachment.setUploaddate(new Date());

		attachment.setFilename(file.getOriginalFilename());
		attachment.setSuffixname(getFileSuffix(attachment.getFilename()));
		attachment.setFilesize(file.getSize());

		dao.save(attachment);

		FSysteminfo setting = getFSysteminfo();
		// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		boolean saveInFileSystem = setting.getSaveinfilesystem()
				&& (dataobject.getIssystem() == null || !dataobject.getIssystem());
		if (saveInFileSystem) {
			// ??????????????????????????????

			// ??????2017-12????????????????????????????????????????????????
			String yyyymm = new SimpleDateFormat("yyyy-MM").format(new Date());
			fileDirExists(setting.getRootpath() + File.separator + yyyymm);
			attachment.setLocalpathname(yyyymm);
			attachment.setLocalfilename(attachment.getAttachmentid());
		}

		String suffix = attachment.getSuffixname();
		// ??????????????????????????????pdf????????????????????????
		if (suffix != null) {
			FAttachmentfiletype filetype = dao.findById(FAttachmentfiletype.class, suffix);
			if (filetype != null) {
				// ?????????????????????????????????????????????????????????pdf,mp3,mp4???
				if (filetype.getCanpreview()) {
					attachment.setOriginalpreviewmode("direct");
				}
				// ??????????????????pdf?????? ??? ????????????????????????pdf
				if (setting.getCreatepreviewpdf() && filetype.getCanpdfpreview()) {
					// pdf????????????????????????????????????
					if (saveInFileSystem) {
						createPdfPreviewFile(attachment, file, setting);
					} else {
						// pdf?????????????????????????????????
						createPdfPreviewDb(attachment, file);
					}
				}
				// ???????????????????????????????????? ??? ?????????????????????????????????
				if (filetype.getIsimage()) {
					attachment.setOriginalpreviewmode("image");
					if (setting.getCreatepreviewimage()) {
						createImagePreview(attachment, file);
					}
				}
			}
		}

		// ????????????pdf?????????????????????????????????????????????
		if (saveInFileSystem) {
			// ????????????????????????????????????
			saveAttachmentToFileSystem(attachment, file, setting);
		} else {
			// ?????????????????????????????????
			FDataobjectattachmentfile attachmentfile = new FDataobjectattachmentfile(attachment);
			attachmentfile.setFiledata(file.getBytes());
			dao.save(attachmentfile);
			attachment.setFDataobjectattachmentfile(attachmentfile);
		}
		dao.saveOrUpdate(attachment);
		int four = 4;
		// ???????????????????????????4??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		// ????????????true
		if (StringUtils.isNotBlank(attachment.getAtype()) && attachment.getAtype().length() >= four) {
			FDictionary dictionary = dao.findByPropertyFirst(FDictionary.class, "dcode", "980010");
			FDictionarydetail[] dictionaryDetails = new FDictionarydetail[1];
			dictionary.getFDictionarydetails().forEach(item -> {
				if (item.getOrderno().equals(attachment.getAtype())) {
					dictionaryDetails[0] = item;
					return;
				}
			});
			FDictionarydetail dictionaryDetail = dictionaryDetails[0];
			FDataobjectfield field = dataobject._getModuleFieldByFieldTitle(dictionaryDetail.getTitle());
			String wjAhead = "wj";
			if (field != null && field.getFieldname().toLowerCase().startsWith(wjAhead)) {
				// ??????????????????
				JSONObject updateObject = new JSONObject();
				updateObject.put(dataobject.getPrimarykey(), uploaditem.getIdvalue());
				updateObject.put(field.getFieldname(), true);
				dataObjectService.saveOrUpdate(dataobject.getObjectname(), updateObject.toJSONString(), null,
						Constants.EDIT);
			}
		}
		Object logic = Local.getLogicBean(dataobject.getObjectname() + "Logic");
		if (logic != null && logic instanceof LogicInterface) {
			((LogicInterface<Object>) logic).afterUploadAttachment(attachment.getIdvalue(), attachment);
		}
		result.put(Constants.ID, attachment.getAttachmentid());
		result.put(Constants.TITLE, attachment.getTitle());
		result.put("filename", attachment.getFilename());
		result.put("thumbnail", BooleanUtils.isTrue(attachment.getHasimagepreviewdata()));
		result.put("pdfpreview", BooleanUtils.isTrue(attachment.getHaspdfpreviewviewdata()));
		result.put("previewmode", attachment.getOriginalpreviewmode());
		return result;
	}

	private void saveAttachmentToFileSystem(FDataobjectattachment attachment, MultipartFile file, FSysteminfo setting)
			throws IOException {

		String fullpath = setting.getRootpath() + attachment._getLocalFilename();
		FileOutputStream outputStream = new FileOutputStream(fullpath);
		FileUtils.copy(file.getInputStream(), outputStream);
	}

	private void fileDirExists(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void createImagePreview(FDataobjectattachment attachment, MultipartFile file) {
		try {
			Image image = ImageIO.read(file.getInputStream());
			compressImage(attachment, image, file.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ???openoffice ??????????????????pdf???????????????pdf???????????????,?????????????????????
	 * 
	 * @param attachment
	 * @param file
	 * @throws IOException
	 */
	private void createPdfPreviewDb(FDataobjectattachment attachment, MultipartFile file) throws IOException {
		ByteArrayOutputStream pdfos = createPdfPreviewStream(file, attachment.getSuffixname());
		FDataobjectattachmentpdffile pdffile = new FDataobjectattachmentpdffile(attachment);
		pdffile.setFilepdfdata(pdfos.toByteArray());
		dao.save(pdffile);
		attachment.setHaspdfpreviewviewdata(true);
		dao.saveOrUpdate(attachment);
	}

	/**
	 * ???????????????pdf??????????????????????????????
	 * 
	 * @param attachment
	 * @param file
	 * @param setting
	 * @throws IOException
	 */
	private void createPdfPreviewFile(FDataobjectattachment attachment, MultipartFile file, FSysteminfo setting)
			throws IOException {
		ByteArrayOutputStream pdfos = createPdfPreviewStream(file, attachment.getSuffixname());
		String fullpath = setting.getRootpath() + attachment._getLocalPDFFilename();
		File outputFile = new File(fullpath);
		FileUtils.copy(new ByteArrayInputStream(pdfos.toByteArray()), outputFile);
		attachment.setHaspdfpreviewviewdata(true);
		dao.saveOrUpdate(attachment);
	}

	/**
	 * ???openoffice ??????????????????pdf???????????????pdf???????????????
	 * 
	 * @param file
	 * @throws ConnectException
	 * @throws IOException
	 */
	private ByteArrayOutputStream createPdfPreviewStream(MultipartFile file, String suffix) {
		ByteArrayOutputStream pdfos = new ByteArrayOutputStream();
		try {
			PdfUtils.convert(file.getInputStream(), pdfos, suffix, Constants.PDF);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pdfos;
	}

	private static final int MAXXY = 128;

	/**
	 * ??????Thumbnails???????????????
	 * 
	 * @param attachment
	 * @param image
	 * @param is
	 * @return
	 */
	public boolean compressImage(FDataobjectattachment attachment, Image image, InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			attachment.setPwidth(width);
			attachment.setPheight(height);
			attachment.setHasimagepreviewdata(true);
			ImageIO.write(Thumbnails.of(is).size(MAXXY, MAXXY).asBufferedImage(), Constants.PNG, os);
			attachment.setPreviewdata(os.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			attachment.setPwidth(0);
			attachment.setPheight(0);
			attachment.setHasimagepreviewdata(false);
			return false;
		}
		return true;
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param attachment
	 * @param image
	 * @return
	 */
	public boolean compressImage(FDataobjectattachment attachment, Image image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			attachment.setPwidth(width);
			attachment.setPheight(height);
			attachment.setHasimagepreviewdata(true);
			int cw = MAXXY;
			int ch = MAXXY * height / width;
			if (height > width) {
				ch = MAXXY;
				cw = MAXXY * width / height;
			}
			BufferedImage bufferedImage = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_RGB);
			bufferedImage.getGraphics().drawImage(image.getScaledInstance(cw, ch, java.awt.Image.SCALE_SMOOTH), 0, 0,
					null);
			ImageIO.write(bufferedImage, Constants.PNG, os);
			attachment.setPreviewdata(os.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			attachment.setPwidth(0);
			attachment.setPheight(0);
			attachment.setHasimagepreviewdata(false);
			return false;
		}
		return true;
	}

	public String getFileSuffix(String filename) {
		int pos = filename.lastIndexOf('.');
		if (pos == -1) {
			return null;
		} else {
			return filename.substring(pos + 1);
		}
	}

	public void thumbnail(String attachmentid) throws IOException {
		FDataobjectattachment attachment = dao.findById(FDataobjectattachment.class, attachmentid);
		if (!ObjectFunctionUtils.allowQueryAttachment(attachment.getFDataobject())) {
			throw new RuntimeException("??????????????????????????????");
		}
		HttpServletResponse response = Local.getResponse();
		response.setHeader("Cache-Control", "max-age=" + 600);
		response.addHeader("Content-Disposition", "inline");
		if (BooleanUtils.isTrue(attachment.getHasimagepreviewdata())) {
			response.addHeader("Content-Length", "" + attachment.getPreviewdata().length);
			response.setContentType("image/png;charset=gb2312");
			CommonUtils.writeStreamToResponse(new ByteArrayInputStream(attachment.getPreviewdata()), response);
		} else {
			preview(attachmentid);
		}
	}

	public void preview(String attachmentid) throws IOException {
		FDataobjectattachment attachment = dao.findById(FDataobjectattachment.class, attachmentid);

		if (!ObjectFunctionUtils.allowQueryAttachment(attachment.getFDataobject())) {
			throw new RuntimeException("??????????????????????????????");
		}

		HttpServletResponse response = Local.getResponse();
		response.setHeader("Cache-Control", "max-age=" + 600);
		response.addHeader("Content-Disposition", "inline");
		if (attachment.getHaspdfpreviewviewdata() != null && attachment.getHaspdfpreviewviewdata()) {
			downloadPdfPreviewData(attachment);
		} else {
			// ?????????????????????????????????????????????????????????????????????pdf
			downloadOriginalToPreview(attachment);
		}

	}

	private void downloadOriginalToPreview(FDataobjectattachment attachment) throws IOException {
		InputStream pdfstream = getOriginalFileStream(attachment);
		HttpServletResponse response = Local.getResponse();
		response.addHeader("Content-Length", "" + pdfstream.available());
		String mimetype = getMimeType(attachment.getSuffixname());
		if (mimetype == null) {
			mimetype = "application/octet-stream";
		}
		response.setContentType(mimetype + ";charset=utf-8");
		CommonUtils.writeStreamToResponse(pdfstream, response);
	}

	private String getMimeType(String suffix) {
		if (suffix == null) {
			return null;
		}
		FAttachmentfiletype filetype = dao.findById(FAttachmentfiletype.class, suffix);
		if (filetype == null) {
			return null;
		} else {
			return filetype.getMimetype();
		}
	}

	/**
	 * ???????????????????????????inputstream
	 * 
	 * @param attachment
	 * @return
	 * @throws FileNotFoundException
	 */
	public InputStream getOriginalFileStream(FDataobjectattachment attachment) {
		FSysteminfo setting = getFSysteminfo();
		InputStream result;
		// ???????????????????????????????????????,????????????????????????
		if (attachment.getLocalfilename() != null) {
			String filename = setting.getRootpath() + attachment._getLocalFilename();
			try {
				result = new FileInputStream(new File(filename));
			} catch (FileNotFoundException e) {
				logging.error("????????????: " + filename + " ????????????");
				return new ByteArrayInputStream(new byte[0]);
			}
		} else {
			FDataobjectattachmentfile ofile = attachment.getFDataobjectattachmentfile();
			if (ofile == null) {
				logging.error(
						attachment.getFDataobject().getTitle() + "???????????????" + attachment.getIdvalue() + " ???????????????????????????????????????");
				return new ByteArrayInputStream(new byte[0]);
			}
			result = new ByteArrayInputStream(ofile.getFiledata());
		}
		return result;
	}

	/**
	 * ??????????????????pdf??????,?????????????????????
	 * 
	 * @param attachment
	 * @throws IOException
	 */
	private void downloadPdfPreviewData(FDataobjectattachment attachment) throws IOException {
		InputStream pdfstream = getPdfPreviewStream(attachment);
		HttpServletResponse response = Local.getResponse();
		response.addHeader("Content-Length", "" + pdfstream.available());
		response.setContentType("application/pdf;charset=gb2312");
		CommonUtils.writeStreamToResponse(pdfstream, response);
	}

	public InputStream getPdfPreviewStream(FDataobjectattachment attachment) throws FileNotFoundException {
		FSysteminfo setting = getFSysteminfo();
		InputStream result;
		if (attachment.getLocalfilename() != null) {
			result = new FileInputStream(new File(setting.getRootpath() + attachment._getLocalPDFFilename()));
		} else {
			FDataobjectattachmentpdffile pdffile = attachment.getFDataobjectattachmentpdffile();
			if (pdffile == null) {
				throw new RuntimeException("????????????pdf????????????????????????");
			}
			result = new ByteArrayInputStream(pdffile.getFilepdfdata());
		}
		return result;
	}

	public void download(String attachmentid) throws UnsupportedEncodingException, IOException {
		FDataobjectattachment attachment = dao.findById(FDataobjectattachment.class, attachmentid);

		if (!ObjectFunctionUtils.allowQueryAttachment(attachment.getFDataobject())) {
			throw new RuntimeException("??????????????????????????????");
		}

		HttpServletResponse response = Local.getResponse();
		response.addHeader("Content-Disposition",
				Constants.ATTACHMENT + ";filename=" + CommonFunction.getDownLoadFileName(attachment.getFilename()));
		response.setContentType("application/octet-stream");
		InputStream pdfstream = getOriginalFileStream(attachment);
		response.addHeader("Content-Length", "" + pdfstream.available());
		CommonUtils.writeStreamToResponse(pdfstream, response);
	}

	public void downloadAll(String moduleName, String idkey) throws IOException {
		FDataobject dataobject = DataObjectUtils.getDataObject(moduleName);

		if (!ObjectFunctionUtils.allowQueryAttachment(dataobject)) {
			throw new RuntimeException("??????????????????????????????");
		}

		List<FDataobjectattachment> attachments = dao.findByProperty(FDataobjectattachment.class, Constants.OBJECTID,
				dataobject.getObjectid(), Constants.IDVALUE, idkey);
		Map<String, Object> record = dataObjectService.getObjectRecordMap(moduleName, idkey);
		String recordtitle = record.get(dataobject.getNamefield()).toString();
		OutputStream os = new ByteArrayOutputStream();
		InputStream input = null;
		ZipOutputStream zipOut = new ZipOutputStream(os);
		zipOut.setLevel(Deflater.BEST_COMPRESSION);
		zipOut.setMethod(ZipOutputStream.DEFLATED);
		zipOut.setComment("??????" + dataobject.getTitle() + "\"" + recordtitle + "\"??????????????????????????????");
		Map<String, Integer> filenames = new HashMap<String, Integer>(0);
		for (FDataobjectattachment attachment : attachments) {
			if (attachment.getFilename() != null) {
				String filename = attachment.getFilename();
				if (filenames.containsKey(filename)) {
					// ??????????????????????????????
					Integer c = filenames.get(filename) + 1;
					filenames.put(filename, c);
					filename = filename + "_" + c;
				} else {
					filenames.put(filename, 0);
				}

				input = getOriginalFileStream(attachment);
				if (input != null) {
					ZipEntry zipEntry = new ZipEntry(filename);
					zipEntry.setComment(attachment.getTitle());
					zipOut.putNextEntry(zipEntry);
					int readed = 0;
					byte[] cash = new byte[2048];
					while ((readed = input.read(cash)) > 0) {
						zipOut.write(cash, 0, readed);
					}
					input.close();
				}
			}
		}
		zipOut.close();
		HttpServletResponse response = Local.getResponse();
		String filename = dataobject.getTitle() + "--" + recordtitle + "?????????" + ".zip";
		InputStream br = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
		response.addHeader("Content-Disposition",
				Constants.ATTACHMENT + ";filename=" + CommonFunction.getDownLoadFileName(filename));
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Length", "" + br.available());
		CommonUtils.writeStreamToResponse(br, response);
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param attachment
	 */
	public void deleteFile(FDataobjectattachment attachment) {
		FSysteminfo setting = getFSysteminfo();
		File file = new File(setting.getRootpath() + attachment._getLocalFilename());
		if (file.exists()) {
			file.delete();
		}
		file = new File(setting.getRootpath() + attachment._getLocalPDFFilename());
		if (file.exists()) {
			file.delete();
		}
	}

	public FSysteminfo getFSysteminfo() {
		FSysteminfo result = null;
		FCompany company = dao.findById(FCompany.class, Local.getCompanyid());

		if (company != null) {
			result = new ArrayList<FSysteminfo>(company.getFSysteminfos()).get(0);
		}

		if (result == null) {
			result = dao.findAll(FSysteminfo.class).get(0);
		}
		return result;
	}

}
