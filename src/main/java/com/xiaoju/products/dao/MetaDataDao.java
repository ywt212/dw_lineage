package com.xiaoju.products.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xiaoju.products.bean.ColumnNode;
import com.xiaoju.products.bean.TableNode;
import com.xiaoju.products.exception.DBException;
import com.xiaoju.products.util.Check;
import com.xiaoju.products.util.DBUtil;
import com.xiaoju.products.util.DBUtil.DB_TYPE;

/**
 * 元数据dao
 * @author yangyangthomas
 */
public class MetaDataDao {
	DBUtil dbUtil = new DBUtil(DB_TYPE.META);
	
	
	public List<ColumnNode> getColumn(String db, String table){
    	String sqlWhere  = "is_effective=1 and data_name='" + table + "'" + (Check.isEmpty(db) ? " " : (" and datastorage_name='"+db+"'"));
    	List<ColumnNode> colList = new ArrayList<ColumnNode>();
    	String sql = "SELECT rc.column_id,rc.column_name,rd.data_id,rd.data_name,rd.datastorage_name FROM r_data_column rc join " +
    			"(SELECT data_id,data_name,datastorage_name from r_data where " + sqlWhere + ") rd " +
    			"on rc.data_id=rd.data_id ORDER BY rc.column_position";
    	
		try {
			List<Map<String, Object>> rs = dbUtil.doSelect(sql);
			for (Map<String, Object> map : rs) {
				ColumnNode column = new ColumnNode();
				column.setId((Long) map.get("column_id"));
				column.setColumn((String) map.get("column_name"));
				column.setTableId((Long) map.get("data_id"));
				column.setTable((String) map.get("data_name"));
				column.setDb((String) map.get("datastorage_name"));
				colList.add(column);
			}
	    	return colList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(sqlWhere, e);
		}
    }
	
	public List<TableNode> getTable(String db, String table){
    	String sqlWhere  = "is_effective=1 and data_name='" + table + "'" + (Check.isEmpty(db) ? " " : (" and datastorage_name='"+db+"'"));
    	List<TableNode> list = new ArrayList<TableNode>();
    	String sql = "SELECT data_id,data_name,datastorage_name from r_data where " + sqlWhere + "";
		try {
			List<Map<String, Object>> rs = dbUtil.doSelect(sql);
			for (Map<String, Object> map : rs) {
				TableNode tableNode = new TableNode();
				tableNode.setId((Long) map.get("data_id"));
				tableNode.setTable((String) map.get("data_name"));
				tableNode.setDb((String) map.get("datastorage_name"));
				list.add(tableNode);
			}
	    	return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(sqlWhere, e);
		}
    }
	
}
