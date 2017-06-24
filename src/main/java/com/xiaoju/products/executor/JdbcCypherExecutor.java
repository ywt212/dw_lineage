package com.xiaoju.products.executor;

import java.sql.*;
import java.util.*;

/**
 * Created by winterhanbing on 2015/8/13.
 */
public class JdbcCypherExecutor implements CypherExecutor {

    private Connection conn=null;

    public JdbcCypherExecutor(String url) {
        this(url, null, null);
    }
    
    public JdbcCypherExecutor(String url, String username, String password) {
        try {
        	String neo4j="org.neo4j.jdbc.Driver";
        	Class.forName(neo4j);
            conn = DriverManager.getConnection(url.replace("http://", "jdbc:neo4j://"), username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
        try {
            final PreparedStatement statement = conn.prepareStatement(query);
            setParameters(statement, params);
            final ResultSet result = statement.executeQuery();
            return new Iterator<Map<String, Object>>() {

                boolean hasNext = result.next();
                public List<String> columns;

                public boolean hasNext() {
                    return hasNext;
                }

                private List<String> getColumns() throws SQLException {
                    if (columns != null) return columns;
                    ResultSetMetaData metaData = result.getMetaData();
                    int count = metaData.getColumnCount();
                    List<String> cols = new ArrayList<String>(count);
                    for (int i = 1; i <= count; i++) cols.add(metaData.getColumnName(i));
                    return columns = cols;
                }

                public Map<String, Object> next() {
                    try {
                        if (hasNext) {
                            Map<String, Object> map = new LinkedHashMap<String, Object>();
                            for (String col : getColumns()) map.put(col, result.getObject(col));
                            hasNext = result.next();
                            if (!hasNext) {
                                result.close();
                                statement.close();
                            }
                            return map;
                        } else throw new NoSuchElementException();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                public void remove() {
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParameters(PreparedStatement statement, Map<String, Object> params) throws SQLException {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            statement.setObject(index, entry.getValue());
        }
    }
    
	public int exec(String sql) {
        try {
            final PreparedStatement statement = conn.prepareStatement(sql);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	
	public int exec(String sql, List<Object> objs) {
        try {
            final PreparedStatement statement = conn.prepareStatement(sql);
            int i = 0;
            for (Object object : objs) {
            	statement.setObject(++i, object);
			}
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
}
