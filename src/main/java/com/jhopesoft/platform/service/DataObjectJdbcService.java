package com.jhopesoft.platform.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.jhopesoft.framework.bean.DataDeleteResponseInfo;
import com.jhopesoft.framework.bean.ResultBean;
import com.jhopesoft.framework.critical.Local;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobject;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.exception.DataUpdateException;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DataObjectUtils;
import com.jhopesoft.framework.utils.DateUtils;
import com.jhopesoft.framework.utils.ObjectFunctionUtils;
import com.jhopesoft.framework.utils.ResultInfoUtils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 *
 */

@Service
public class DataObjectJdbcService {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private DataObjectService dataObjectService;

    private static final int UUIDLEN = 32;

    /**
     * 新增数据
     * 
     * @param dataObject 实体对象
     * @param inserted   数据字符串对象
     * @return
     */
    public ResultBean save(FDataobject dataObject, String inserted) {
        String objectname = dataObject.getObjectname();
        ResultBean result = new ResultBean();
        Map<String, Object> map = JSON.parseObject(inserted);
        bytesDecodeBase64(dataObject, map);
        if (!ObjectFunctionUtils.allowNew(dataObject)) {
            throw new DataUpdateException("你无权进行此模块数据的新增操作!");
        }
        String[] fields = map.keySet().toArray(new String[map.size()]);
        // 查查有没有主键，没有的话，如果是字符串的主键，生成一个uuid
        FDataobject object = DataObjectUtils.getDataObject(objectname);
        FDataobjectfield keyfield = object._getPrimaryKeyField();
        boolean haskey = false;
        for (String k : fields) {
            if (keyfield.getFieldname().equals(k)) {
                haskey = true;
                break;
            }
        }
        // 如果没找到主键，并且主键是string,长度大于32
        if (!haskey && Constants.STRING.equalsIgnoreCase(keyfield.getFieldtype())
                && keyfield.getFieldlen() >= UUIDLEN) {
            map.put(keyfield.getFieldname(), UUID.randomUUID().toString().replaceAll("-", ""));
            fields = map.keySet().toArray(new String[map.size()]);
        }
        // 创建人员改为 id
        if (map.containsKey(Constants.CREATER)) {
            map.put(Constants.CREATER, Local.getUserid());
        }
        // 找到所有的布尔字段，将true,yes,1 设置为1，其他都设置为0
        for (String fn : fields) {
            FDataobjectfield field = dataObject._getModuleFieldByFieldName(fn);
            if (field != null && field._isBooleanField()) {
                String value = map.get(fn) != null ? map.get(fn).toString().toLowerCase() : Constants.NULL;
                if (Constants.TRUE.equals(value) || Constants.YES.equals(value) || "1".equals(value)) {
                    map.put(fn, "1");
                } else if (Constants.NULL.equals(value)) {
                    map.put(fn, null);
                } else {
                    map.put(fn, "0");
                }
            }
        }

        String sql = "insert into " + dataObject.getTablename() + " ("
                + String.join(",", toTableFieldName(dataObject, fields)) + ") values ( :" + String.join(", :", fields)
                + ")";
        namedParameterJdbcTemplate.update(sql, map);
        String id = map.get(dataObject.getPrimarykey()).toString();
        result = dataObjectService.fetchInfo(objectname, id.toString());
        dataObjectService.saveOperateLog(dataObject, id.toString(),
                dataObjectService.getRecordNameValue(dataObject, map), Constants.NEW, inserted);
        result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
        return result;
    }

    /**
     * 新增数据
     * 
     * @param dataObject 实体对象
     * @param inserted   数据字符串对象
     * @return
     */
    public ResultBean update(FDataobject dataObject, String updated) {
        String objectname = dataObject.getObjectname();
        ResultBean result = new ResultBean();
        Map<String, Object> map = JSON.parseObject(updated);
        bytesDecodeBase64(dataObject, map);
        String id = map.get(dataObject.getPrimarykey()).toString();
        // 清除掉主键
        map.remove(dataObject.getPrimarykey());
        if (!ObjectFunctionUtils.allowEdit(dataObject)) {
            throw new DataUpdateException("你无权进行此模块数据的修改操作!");
        }
        // 加入最后修改日期和人员，如果有这个字段的话
        for (FDataobjectfield field : dataObject.getFDataobjectfields()) {
            if (Constants.LASTMODIFIER.equalsIgnoreCase(field.getFieldname())) {
                map.put(field.getFieldname(), Local.getUserid());
            }
            if (Constants.LASTMODIFYDATE.equalsIgnoreCase(field.getFieldname())) {
                map.put(field.getFieldname(), DateUtils.getTimestamp());
            }
        }
        String[] fields = map.keySet().toArray(new String[map.size()]);
        String[] dbfields = toTableFieldName(dataObject, fields);
        String[] uFields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            uFields[i] = dbfields[i] + " = :" + fields[i];
        }
        map.put(dataObject.getPrimarykey(), id);
        String sql = "update " + dataObject.getTablename() + " set " + String.join(",", uFields) + " where "
                + dataObject._getPrimaryKeyField().getFielddbname() + " = :" + dataObject.getPrimarykey();
        namedParameterJdbcTemplate.update(sql, map);
        result = dataObjectService.fetchInfo(objectname, id.toString());
        dataObjectService.saveOperateLog(dataObject, id.toString(),
                dataObjectService.getRecordNameValue(dataObject, map), Constants.NEW, updated);
        result.setResultInfo(ResultInfoUtils.getResultInfoMessage());
        return result;
    }

    public DataDeleteResponseInfo delete(FDataobject dataObject, String id) {
        DataDeleteResponseInfo result = new DataDeleteResponseInfo();
        Map<String, String> map = new HashMap<String, String>(0);
        map.put("id", id);
        String sql = "delete from " + dataObject.getTablename() + " where "
                + dataObject._getPrimaryKeyField().getFielddbname() + " = :id";
        namedParameterJdbcTemplate.update(sql, map);
        dataObjectService.saveOperateLog(dataObject, id, id, Constants.DELETE, null);
        result.setResultCode(0);
        return result;
    }

    private void bytesDecodeBase64(FDataobject dataobject, Map<String, Object> map) {
        for (String key : map.keySet()) {
            FDataobjectfield field = dataobject._getModuleFieldByFieldName(key);
            if (map.get(key) != null && field != null && field._isByteField()) {
                map.put(key, Base64.decodeBase64(map.get(key).toString()));
            }
        }
    }

    private String[] toTableFieldName(FDataobject dataobject, String[] names) {
        String[] result = new String[names.length];
        int i = 0;
        for (String fn : names) {
            FDataobjectfield field = dataobject._getModuleFieldByFieldName(fn);
            if (field == null) {
                // manytoone的字段是"testmastertable.masterId":"value" ,这样的
                // 只有manytoone是这样的
                String[] parts = fn.split("\\.");
                if (parts.length == 2) {
                    field = dataobject._getModuleFieldByFieldName(parts[0]);
                }
            }
            result[i] = field.getFielddbname();
            i++;
        }
        return result;
    }

}
