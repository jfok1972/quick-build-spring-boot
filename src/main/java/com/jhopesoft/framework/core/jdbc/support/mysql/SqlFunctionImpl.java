package com.jhopesoft.framework.core.jdbc.support.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jhopesoft.framework.bean.Name;
import com.jhopesoft.framework.bean.TableBean;
import com.jhopesoft.framework.bean.TableFieldBean;
import com.jhopesoft.framework.core.jdbc.support.AbstractSqlFunction;
import com.jhopesoft.framework.dao.Dao;
import com.jhopesoft.framework.dao.entity.dataobject.FDataobjectfield;
import com.jhopesoft.framework.utils.CamelCaseUtils;
import com.jhopesoft.framework.utils.CommonUtils;
import com.jhopesoft.framework.utils.Constants;
import com.jhopesoft.framework.utils.DateFormat;
import com.jhopesoft.framework.utils.ProjectUtils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.metamodel.internal.EntityTypeImpl;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class SqlFunctionImpl extends AbstractSqlFunction {

	public SqlFunctionImpl() {

	}

	/**
	 * 返回当前数据库类型的主键或唯一索引错误的查找的regex, Duplicate entry '主键值' for key
	 * 'IX_province_name_u' 第一个是冲突值，第二个是索引名称
	 * 
	 * @return
	 */
	@Override
	public String getDuplicateMessageRegexString() {
		return "Duplicate entry '(.*)' for key '(.*)'";
	};

	@Override
	public TableBean getTable(Dao dao, String tablename, String schema) {
		TableBean bean = null;
		try {
			Connection conn = dao.getConnection();
			schema = (schema == null || schema.length() == 0) ? conn.getCatalog() : schema;
			String sql = "select a.table_name as tablename, a.table_comment as comment from information_schema.tables a where a.TABLE_SCHEMA = ?0 and a.TABLE_NAME = ?1";
			bean = dao.executeSQLQueryFirst(sql, TableBean.class, schema, tablename);
			if (bean == null) {
				return null;
			}
			EntityTypeImpl<?> entity = ProjectUtils.getEntityMap(dao)
					.get(CamelCaseUtils.underlineToCamelhump(tablename).toLowerCase());
			if (entity != null) {
				bean.setClassname(entity.getTypeName());
			}
			sql = "select " + "		a.column_name as fieldname, " + "		a.data_type as fieldtype, "
					+ "		ifnull(a.character_maximum_length, 0 ) as fieldlen,a.numeric_scale as datascale ,"
					+ "		case when a.is_nullable = 'NO' then true else false end as isrequired, "
					+ "		a.column_comment as comments, " + "		b.referenced_table_name as jointable, "
					+ "		b.referenced_column_name as joincolumnname, " + " 		a.column_key as by1 " + " from "
					+ " information_schema.columns a "
					+ " left join information_schema.key_column_usage b on a.table_schema = b.table_schema and a.table_name = b.table_name and"
					+ " a.column_name = b.column_name and b.referenced_table_name is not null"
					+ " where a.table_schema = ?0 and a.table_name = ?1 order by a.ordinal_position ";
			List<TableFieldBean> fields = dao.executeSQLQuery(sql, TableFieldBean.class, schema, tablename);
			for (TableFieldBean field : fields) {
				field.setDbfieldtype(field.getFieldtype());
				if ("PRI".equals(field.getBy1())) {
					bean.setPrimarykey(field.getFieldname());
				}
				if (!CommonUtils.isEmpty(field.getJointable())) {
					field.setFieldrelation(FDataobjectfield.MANYTOONE);
					// 应该是模块的字段，referenced_column_name 是主表的字段，主表只关联主键
					field.setJoincolumnname(field.getFieldname());
				}
			}
			bean.setFields(fields);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	@Override
	public List<String> getSchemas(Dao dao) {
		List<String> result = new ArrayList<String>();
		String sql = "select schema_name as name from information_schema.schemata a where"
				+ " a.schema_name not in('information_schema','performance_schema','sys','mysql') ";
		for (Name name : dao.executeSQLQuery(sql, Name.class)) {
			result.add(name.getName());
		}
		return result;
	}

	@Override
	public List<String> getTables(Dao dao, String schema) {
		List<String> result = new ArrayList<String>();
		String sql = "select table_name as name from information_schema.tables"
				+ " where table_type='BASE TABLE' and table_schema = ?0 ";
		for (Name name : dao.executeSQLQuery(sql, Name.class, schema)) {
			result.add(name.getName());
		}
		return result;
	}

	@Override
	public List<String> getViews(Dao dao, String schema) {
		List<String> result = new ArrayList<String>();
		String sql = "select table_name as name from information_schema.tables where table_type='VIEW' and table_schema = ?0 ";
		for (Name name : dao.executeSQLQuery(sql, Name.class, schema)) {
			result.add(name.getName());
		}
		return result;
	}

	@Override
	public String getTableComment(Dao dao, String schema, String tablename) {
		Connection conn = dao.getConnection();
		try {
			schema = (schema == null || schema.length() == 0) ? conn.getCatalog() : schema;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "select a.table_comment as name from information_schema.tables a where a.TABLE_SCHEMA = ?0 and a.TABLE_NAME = ?1";
		Name name = dao.executeSQLQueryFirst(sql, Name.class, schema, tablename);
		if (name == null) {
			return tablename;
		}
		return StringUtils.isNotBlank(name.getName()) ? name.getName() : tablename;
	}

	@Override
	public String getFkConstraintTableName(Dao dao, String tablename, String message, String schema) {
		String fkname = CommonUtils.getConstraintName(message, "FK_");
		Connection conn = dao.getConnection();
		try {
			schema = (schema == null || schema.length() == 0) ? conn.getCatalog() : schema;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (fkname != null) {
			String sql = " select  table_name as name from information_schema.key_column_usage"
					+ " where constraint_name= ?0  and table_schema = ?1 ";
			Name name = dao.executeSQLQueryFirst(sql, Name.class, fkname, schema);
			if (name != null) {
				return name.getName();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Map<String, Map<String, Object>> getAllKeyInfo(Dao dao, String schema) {
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>(0);
		Connection conn = dao.getConnection();
		try {
			schema = (schema == null || schema.length() == 0) ? conn.getCatalog() : schema;
			String sql = "select DISTINCT * from ( " + " SELECT  "
					+ "     a.TABLE_SCHEMA,a.TABLE_NAME,a.COLUMN_NAME,a.INDEX_NAME " + " FROM "
					+ "     information_schema.statistics a " + " UNION all " + " SELECT  "
					+ "     a.TABLE_SCHEMA,a.TABLE_NAME,a.COLUMN_NAME,a.CONSTRAINT_NAME as INDEX_NAME " + " FROM "
					+ "     INFORMATION_SCHEMA.KEY_COLUMN_USAGE a  " + " ) m  " + " where m.TABLE_SCHEMA ='" + schema
					+ "'";

			List<Map<String, Object>> dataList = dao.executeSQLQuery(sql);
			for (int i = 0; i < dataList.size(); i++) {
				Map<String, Object> map = dataList.get(i);
				String tableName = ((String) map.get("TABLE_NAME")).toLowerCase();
				String columnName = (String) map.get("COLUMN_NAME");
				String indexName = (String) map.get("INDEX_NAME");
				if (!resultMap.containsKey(tableName)) {
					resultMap.put(tableName, new HashMap<String, Object>(0));
				}
				String v = (String) resultMap.get(tableName).get(indexName);
				if (!CommonUtils.isEmpty(v)) {
					resultMap.get(tableName).put(indexName, v + Constants.COMMA + columnName);
				} else {
					resultMap.get(tableName).put(indexName, columnName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	public String ceil(String value) {
		return "ceiling(" + value + ")";
	}

	public String trunc(String value) {
		return "truncate(" + value + ",0)";
	}

	public String log(String value) {
		return "log(" + value + ")";
	}

	public String log10(String value) {
		return "log10(" + value + ")";
	}

	public String square(String value) {
		return "power(" + value + ",2)";
	}

	public String random() {
		return "rand()";
	}

	public String pi() {
		return "pi()";
	}

	public String degrees(String value) {
		return "degrees(" + value + ")";
	}

	public String radians(String value) {
		return "radians(" + value + ")";
	}

	public String isNull(String field, String value) {
		return "ifnull(" + field + ", " + value + ")";
	}

	public String character(String value) {
		return "char(" + value + ")";
	}

	public String link(String[] vs) {
		String str = "concat(";
		for (int i = 0; i < vs.length; i++) {
			if (i > 0) {
				str += Constants.COMMA;
			}
			str += vs[i];
		}
		str += ")";
		return str;
	}

	public String charindex(String parent, String sub, String count) {
		return "instr(" + parent + Constants.COMMA + sub + ")";
	}

	public String substring(String parent, String start, String length) {
		return "substring(" + parent + Constants.COMMA + start + Constants.COMMA + length + ")";
	}

	public String length(String value) {
		return "length(" + value + ")";
	}

	public String trim(String value) {
		return "trim(" + value + ")";
	}

	public String getDate() {
		return "now()";
	}

	public String toChar(String datestring, DateFormat format) {
		return "date_format(" + datestring + ",'" + format.getMySQLFormat() + "')";
	}

	public String toDate(String str, DateFormat format) {
		return "str_to_date(" + str + ",'" + format.getMySQLFormat() + "')";
	}

}
