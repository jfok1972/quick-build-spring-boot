package com.jhopesoft.platform.logic.define;

import java.util.List;
import java.util.Map;

import com.jhopesoft.framework.bean.PageInfo;
import com.jhopesoft.framework.core.objectquery.filter.UserNavigateFilter;
import com.jhopesoft.framework.core.objectquery.filter.UserParentFilter;
import com.jhopesoft.framework.core.objectquery.generate.SqlGenerate;
import com.jhopesoft.framework.dao.entity.attachment.FDataobjectattachment;

/**
 * 
 * @author jiangfeng
 *
 * @param <T>
 */

public interface LogicInterface<T> extends WorkFlowInterface, AuditingInterface {

	/**
	 * 在查询语句的所有子句准备好以后，准备生成sql之前执行
	 * 
	 * @param generate
	 */

	public void beforeGenerateSelect(SqlGenerate generate);

	/**
	 * 在查询语句的所有子句准备好以后，准备生成count sql之前执行
	 * 
	 * @param generate
	 */
	public void beforeGenerateSelectCount(SqlGenerate generate);

	/**
	 * 
	 * 在取得数据之后可以对当前的grid数据进行二次加工
	 * 
	 * @param pageInfo
	 * @return
	 */
	public PageInfo<Map<String, Object>> afterFatchData(PageInfo<Map<String, Object>> pageInfo);

	/**
	 * 记录插入之前的操作
	 * 
	 * @param inserted     要被插入的 hibernate bean
	 * @param errorMessage 如果不能插入，存放发生错误的字段和错误原因
	 * @param 不可以插入，抛出异常   DataUpdateException。
	 * 
	 */
	public void beforeInsert(T inserted);

	/**
	 * 
	 * 记录插入之前执行的操作，这时数据还没有写到数据库中
	 * 
	 * @param inserted
	 */
	public void afterInsert(T inserted);

	/**
	 * 记录修改之前的操作
	 * 
	 * @param type          修改的类型，有修改，或者审批，审核等
	 * @param updatedObject 记录修改后的bean
	 * @param oldObject     记录修改前的bean
	 * @param request
	 * @return 不可以修改，则抛出异常 DataUpdateException
	 * 
	 */
	public void beforeUpdate(String type, T updatedObject, T oldObject);

	/**
	 * 记录修改之后执行的操作
	 * 
	 * @param type
	 * @param updatedObject
	 * @param oldObject
	 */
	public void afterUpdate(String type, T updatedObject, T oldObject);

	/**
	 * 删除之前的操作，如果不能删除，则抛出异常 DataDeleteException 里面写上原因
	 * 
	 * @param deleted
	 * @return
	 * 
	 */
	public void beforeDelete(T deleted);

	/**
	 * 记录被删除之后执行的操作
	 * 
	 * @param deleted
	 */
	public void afterDelete(T deleted);

	/**
	 * 在新增之前取得记录的缺省值
	 * 
	 * @param userParentFilters 前台传过来的父模块筛选
	 * @param navigateFilters   前台传过来的导航值
	 * @return 缺省值字段名和字段值的集合
	 */
	public Map<String, Object> getNewDefultValue(List<UserParentFilter> userParentFilters,
			List<UserNavigateFilter> navigateFilters);

	/**
	 * 表单中可以展开一条记录，然后根据此函数的返回结果显示展开的内容
	 * 
	 * @param recordId
	 * @return
	 */
	public String getGridRecordExpandBody(String recordId);

	/**
	 * 当上传了一个附件后执行
	 * 
	 * @param recordId
	 * @param attachment
	 */
	public void afterUploadAttachment(String recordId, FDataobjectattachment attachment);

	/**
	 * 当预览了记录打印时进行的操作
	 * 
	 * @param recordId
	 * @param schemeId
	 */
	public void afterPrintRecord(String recordId, String schemeId);

}
