package com.xiaoju.products.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import com.xiaoju.products.bean.ColumnNode;
import com.xiaoju.products.bean.RealationShip;
import com.xiaoju.products.bean.TableNode;
import com.xiaoju.products.executor.CypherExecutor;
import com.xiaoju.products.executor.JdbcCypherExecutor;
import com.xiaoju.products.util.PropertyFileUtil;
/**
 * Created by winterhanbing on 2015/8/13.
 */
public class Neo4jDaoImpl implements Neo4jDao {
	private final CypherExecutor cypher;

	public Neo4jDaoImpl() {
		cypher = createCypherExecutor(PropertyFileUtil.getProperty("neo4j.jdbc.url"));
	}
	
	public Neo4jDaoImpl(String uri) {
		cypher = createCypherExecutor(uri);
	}

	private CypherExecutor createCypherExecutor(String uri) {
		try {
			String auth = new URL(uri).getUserInfo();
			if (auth != null) {
				String[] parts = auth.split(":");
				return new JdbcCypherExecutor(uri, parts[0], parts[1]);
			}
			return new JdbcCypherExecutor(uri);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid Neo4j-ServerURL " + uri);
		}
	}

	public int createTable(TableNode node) {
		String sql = "merge (t:TABLE{tid:{1}}) on create set t.db={2}, t.table={3}";
		List<Object> args = new ArrayList<Object>(3);
		args.add(node.getId());
		args.add(node.getDb());
		args.add(node.getTable());
		return cypher.exec(sql, args);
	}

	public int createColumn(long tableId, List<ColumnNode> list) {
		
		List<Object> args = new ArrayList<Object>(3);
		args.add(tableId);
		
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (ColumnNode cn : list) {
			sb.append("(t)-[:HAS]->(:COLUMN{cid:{").append(++i).append("},column:{").append(++i).append("}}),");
			args.add(cn.getId());
			args.add(cn.getColumn());
		}
		sb.setLength(sb.length()-1);
		String sql = "MATCH (t:TABLE{tid:{1}}) CREATE UNIQUE " + sb.toString();
		return cypher.exec(sql, args);
	}

	public int createTableRealationShip(RealationShip ship) {
		String sql = "match (n:TABLE{tid:{1}}),(m:TABLE{tid:{2}}) " +
				"create UNIQUE n-[:FROM]->m";
		List<Object> args = new ArrayList<Object>(2);
		args.add(ship.getNode1Id());
		args.add(ship.getNode2Id());
		return cypher.exec(sql, args);
	}

	public int createColumnRealationShip(RealationShip ship) {
		StringBuilder sb = new StringBuilder();
		Map<String, List<String>> propertyMap = ship.getPropertyMap();
		List<Object> args = new ArrayList<Object>(2);
		args.add(ship.getNode1Id());
		args.add(ship.getNode2Id());
		int i = 2;
		for (Entry<String, List<String>> entry : propertyMap.entrySet()) {
			sb.append("r.").append(entry.getKey()).append("=");
			List<String> list = entry.getValue();
			if (list.size() == 1) {
				sb.append("{").append(++i).append("}");
				args.add(list.get(0));
			} else {
				sb.append("[");
				for (String string : list) {
					sb.append("{").append(++i).append("}").append(",");
					args.add(string);
				}
				sb.setLength(sb.length() - 1);
				sb.append("]");
			}
			sb.append(",");
		}		
		sb.setLength(sb.length() - 1);

		String sql = "match (n:COLUMN{cid:{1}}),(m:COLUMN{cid:{2}})" +
				" merge n-[r:FROM]->m " + 
				" on create set " + sb.toString() + 
				" on match set " + sb.toString();
		return cypher.exec(sql, args);
	}

}
