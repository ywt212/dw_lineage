package com.xiaoju.products.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.xiaoju.products.bean.ColLine;
import com.xiaoju.products.bean.ColumnNode;
import com.xiaoju.products.bean.RealationShip;
import com.xiaoju.products.bean.TableNode;
import com.xiaoju.products.util.Check;
import com.xiaoju.products.util.MetaCache;

/**
 * @author yangyangthomas
 */
public class Convertor {
	
	public static List<TableNode> convertToTableNode(){
		List<TableNode> list = new ArrayList<TableNode>();
		for (Entry<String, List<ColumnNode>> entry : MetaCache.getInstance().getcMap().entrySet()) {
			List<ColumnNode> value = entry.getValue();
			TableNode tn = columnNodeToTableNode(value.get(0));
    		list.add(tn);
		}
    	return list;
	}
	
	
	public static Map<Long, List<ColumnNode>> convertToColumnNode(){
		Map<Long, List<ColumnNode>> map = new HashMap<Long, List<ColumnNode>>();
		for (Entry<String, List<ColumnNode>> entry : MetaCache.getInstance().getcMap().entrySet()) {
    		List<ColumnNode> list = entry.getValue();
    		map.put(list.get(0).getTableId(), list);
		}
    	return map;
	}
	
	public static List<RealationShip> convertTableRS(Set<String> toSet, Set<String> fromSet){
		List<RealationShip> rsList = new ArrayList<RealationShip>();
    	for (String ttable : toSet) {
    		for (String ftable : fromSet) {
    			RealationShip rs = new RealationShip();
    			rs.setNode1Id(MetaCache.getInstance().getTableMap().get(ttable.toLowerCase()));
    			rs.setNode2Id(MetaCache.getInstance().getTableMap().get(ftable.toLowerCase()));
        		rsList.add(rs);
			}
		}
    	return rsList;
	}
	
	
	public static List<RealationShip> convertColumnRS(List<ColLine> clList){
		List<RealationShip> rsList = new ArrayList<RealationShip>();
    	for (ColLine cl : clList) {
    		String toName = cl.getToName();
    		Set<String> fromNameSet = cl.getFromNameSet();
    		Set<String> allConditionSet = cl.getAllConditionSet();
    		Map<String, List<String>> propertyMap = generatePropertyMap(allConditionSet);
    		for (String fromName : fromNameSet) {
        		RealationShip rs = new RealationShip();
        		rs.setNode1Id(MetaCache.getInstance().getColumnMap().get(toName.toLowerCase()));
        		rs.setNode2Id(MetaCache.getInstance().getColumnMap().get(fromName.toLowerCase()));
				rs.setPropertyMap(propertyMap);
        		rsList.add(rs);
			}
		}
    	return rsList;
	}


	private static Map<String, List<String>> generatePropertyMap(
			Set<String> allConditionSet) {
		Map<String, List<String>> propertyMap = new HashMap<String, List<String>>();
		for (String string : allConditionSet) {
			int indexOf = string.indexOf(":");
			if (indexOf > 0) {
				String key = string.substring(0, indexOf);
				List<String> list = propertyMap.get(key);
				if (Check.isEmpty(list)) {
					list = new ArrayList<String>();
					propertyMap.put(key, list);
				}
				list.add(string.substring(indexOf + 1));
			}
		}
		return propertyMap;
	}
	
	
	private static TableNode columnNodeToTableNode(ColumnNode cn){
		TableNode tn = new TableNode();
		tn.setId(cn.getTableId());
		tn.setDb(cn.getDb());
		tn.setTable(cn.getTable());
		return tn;
	}
	
	private Convertor(){		
		
	}
}
