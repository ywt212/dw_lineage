package com.xiaoju.products.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiaoju.products.bean.ColLine;
import com.xiaoju.products.bean.DWTask;
import com.xiaoju.products.exception.SQLExtractException;

/**
 * 解析工具类
 * @author yangyangthomas
 *
 */
public final class ParseUtil {
	private static final Map<Integer, String> hardcodeScriptMap = new HashMap<Integer, String>();
	private static final String SPLIT_DOT = ".";
	private static final String SPLIT_COMMA = ",";
	// hive/hive2 -e "" | hive -hiveconf hive.root.logger=ERROR,console -e "${g_order_poi}"
	private static final String REGEX_HIVE = "^\\s*(\\$exe_)?hive\\d?.*?-e\\s*\"([\\s\\S]*?)\"";
	//source $current_path/tools.sh
	private static final String REGEX_SOURCE = "^\\s*source\\s*([\\S]*)\\s*";
	// sh -x run.sh
	private static final String REGEX_SHELL = "^\\s*sh\\s*(-\\S)?\\s*([\\S]*)\\s*";
	// CONDITION_WHERE=" co1 = co2 and c=1 "
	private static final String REGEX_ALONE_VAR_VALUE = "\\s*=\\s*[\"]([\\s\\S]*?)[\"]";
	
	private static final String REGEX_VAR = "[\\s\\(]+(\\$\\{?[\\w]*\\}?)[\\s\\)]+";
	
	private static final String REGEX_PYTHON_SQL_VAR = "runHiveCmd\\((\\w*?)\\s*,";
	private static final String REGEX_PYTHON_SQL_VAR_VALUE = "\\s*=\\s*\"\"\"([\\s\\S]*?)\"\"\"";
	private static final String REGEX_PYTHON_SQL_INVAR = "\\s+%\\(([\\s\\S]*?)\\)s[;\\s]"; //匹配数据库名称、表名、字段的变量名等
	private static final String REGEX_PYTHON_SQL_INVAR_VALUE = "\\s*=\\s*[\"\']([\\s\\S]*?)[\"\']";
	
	private static final Map<String, Boolean> REGEX_MULTI_VAR_VALUE = new HashMap<String, Boolean>();
	
	static {
		REGEX_MULTI_VAR_VALUE.put("\\s*=\\s*\"([\\s\\S]*?)\"", false);
		REGEX_MULTI_VAR_VALUE.put("\\s*=\\s*\'([\\s\\S]*?)\'", false);
		REGEX_MULTI_VAR_VALUE.put("\\s*=\\s*(\\w*)", false);
		REGEX_MULTI_VAR_VALUE.put("\\s*=\\s*`([\\s\\S]*?)`", true);
		
		hardcodeScriptMap.put(400, "^\\s*hive\\d?.*?-e\\s*\"([\\s\\S]*)\"");
	}
    /**
     * @param table fact.t1
     * @return [fact, t1]
     */
    public static String[] parseDBTable(String table) {
		return table.split("\\" + SPLIT_DOT);
	}
    
    public static String collectionToString(Collection<String> coll){
    	return collectionToString(coll, SPLIT_COMMA, true);
    }

    public static String collectionToString(Collection<String> coll,String split, boolean isCheck){
    	StringBuilder sb = new StringBuilder();
    	if (Check.notEmpty(coll)) {
        	for (String string : coll) {
        		if ((isCheck && Check.notEmpty(string)) || !isCheck) {
        			sb.append(string).append(split);
				}
    		}
    		if (sb.length() > 0) {
    			sb.setLength(sb.length()-split.length());
    		}
		}
		return sb.toString();
    }
    
    public static String uniqMerge(String s1, String s2){
    	Set<String> set = new HashSet<String>();
    	set.add(s1);
    	set.add(s2);
    	return collectionToString(set);
    }
    
    /**
     * 返回多个应该分析的SQL集合，
     * 如： hive -e "sql1;" hive -e "sql2" ,返回 sql1,sql2集合
     * @param path
     * @return 
     */
	public static List<String> extractSQL(DWTask task) {
		String path = task.getPath();
		if (path.endsWith("sh")) { //匹配shell
			//1、直接匹配hive -e
			String content = fixSourceVar(path, read(path));
			List<String> _sqlList = regexList(REGEX_HIVE, content, 2);
			if (Check.notEmpty(_sqlList)) {
				return fixSqlVarList(content, _sqlList);
			}
			
			//2、没有有执行hive -e，执行其他脚本
			List<String> resulList = new ArrayList<String>();
			List<String> scriptList = regexList(REGEX_SHELL, content, 2);
			if (Check.notEmpty(scriptList)) {
				for (String script : scriptList) {
					if (!script.contains("#") && script.contains(".")) {
						String newPath = getFilePath(path, script); 
						String newPathContent = fixSourceVar(newPath, read(newPath));
						String rsql = getSQL(task.getId(), newPathContent);
						if (Check.notEmpty(rsql)) {
							_sqlList.add(rsql);
						}
						if (Check.notEmpty(_sqlList)) {
							resulList.addAll(fixSqlVarList(newPathContent, _sqlList));
						}
					}
				}
			}
			return resulList;
		} else if (path.endsWith("py")) { //匹配python，主要思路匹配出变量，里面含有insert into/override table的就予以分析，否则没有
			String content = read(path);
			List<String> sqlVarList = regexList(REGEX_PYTHON_SQL_VAR, content, 1);
			List<String> resulList = new ArrayList<String>();
			for (String sqlVar : sqlVarList) {
				final String sql = regex(sqlVar + REGEX_PYTHON_SQL_VAR_VALUE, content, 1);
				String _sql = sql;
				if (Check.notEmpty(sql) && sql.toLowerCase().contains("insert") && sql.toLowerCase().contains("table")) {
					List<String> sqlInVarList = regexList(REGEX_PYTHON_SQL_INVAR, sql, 1);
					for (String sqlInVar : sqlInVarList) {
						String value = regex(sqlInVar + REGEX_PYTHON_SQL_INVAR_VALUE, content, 1);
						if (Check.isEmpty(value)) {
							throw new SQLExtractException("can extract var:" + sqlInVar);
						}
						_sql = _sql.replaceAll(escape("%("+sqlInVar+")s"), Matcher.quoteReplacement(value));
					}
					resulList.add(_sql);
				}
			}
			return resulList;
		}
		throw new SQLExtractException("can extract sql");
	}

	/**
	 * 如果脚本中含有source test.sh中，则把test.sh的内容合并过来
	 * @param path
	 * @param content
	 * @return
	 */
	private static String fixSourceVar(String path, String content) {
		List<String> sourceList = regexList(REGEX_SOURCE, content, 1);
		StringBuilder sb = new StringBuilder();
		if (Check.notEmpty(sourceList)) {
			for (String source : sourceList) {
				if (source.contains(".")) {
					String newPath = getFilePath(path, source); 
					String newPathContent = read(newPath);
					sb.append(newPathContent);
				}
			}
		}
		sb.append(content);
		return sb.toString();
	}

	/**
	 * 获得文件路径，直接截取拼成当前路径+文件名
	 * //FIXME 有显生硬
	 * @param path
	 * @param source
	 * @return
	 */
	private static String getFilePath(String path, String source) {
		int lastIndexOf = source.lastIndexOf("/");
		String name = lastIndexOf > -1 ? source.substring(lastIndexOf+1) : source; 
		String newPath = path.substring(0, path.lastIndexOf("/") + 1) + name;
		return newPath;
	}

	/**
	 * 硬编码获得SQL
	 * @param name
	 * @param newPathContent
	 * @return
	 */
	private static String getSQL(long taskId, String newPathContent) {
		String rsql; 
		if (hardcodeScriptMap.containsKey(taskId)) {
			rsql = regex(hardcodeScriptMap.get(taskId), newPathContent, 1); 
		} else {
			rsql = regex(REGEX_HIVE, newPathContent, 2);
		}
		return rsql;
	}

	/**
	 * 修正SQL中的变量
	 * hive -e "${variable}" hive -e "insert into table $var " variable="real sql"
	 * @param content
	 * @param sqlList
	 * @return
	 */
	private static List<String> fixSqlVarList(String content, List<String> sqlList) {
		List<String> list = new ArrayList<String>(sqlList.size());
		for (String tsql : sqlList) {
			list.add(fixSqlVar(content, tsql));
		}
		return list;
	}

	private static String fixSqlVar(String content, String tsql) {
		//2、匹配hive -e的变量，重新定位sql
		String _tsql = tsql;
		boolean isVarAlone = _tsql.trim().startsWith("$"); //第一种情况varAlone=true
		Set<String> varSet = matchShellVar(tsql, isVarAlone); 
		if (Check.notEmpty(varSet)) {
			for (String var : varSet) {
				String _var;
				if(var.startsWith("${") && var.endsWith("}")){
					_var = var.substring(2, var.length()-1);
				} else {
					_var = var.substring(1);
				}
				if (isVarAlone) {
					_tsql = regex(_var + REGEX_ALONE_VAR_VALUE, content, 1);
					if (Check.notEmpty(matchShellVar(_tsql, false))) { //防止变量套变量的情况
						_tsql = fixSqlVar(content, _tsql);
					}
				} else { //替换很多变量
					//var='table1' , varwhere="col='a' and col2='b'" , date=`date --date="$V_DATE-14 day" +%Y%m%d`
					String value = getVarValue(content, _var);
					if (Check.isEmpty(value)) {
						throw new SQLExtractException("can extract var:" + _var);
					}
					_tsql = _tsql.replaceAll(escape(var), Matcher.quoteReplacement(value));
				}
			}
		}
		return _tsql;
	}

	public static String escape(String keyword) {  
	    if (Check.notEmpty(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	}  

	private static String getVarValue(String content, String _var) {
		String value = null;
		for (Entry<String, Boolean> entry : REGEX_MULTI_VAR_VALUE.entrySet()) {
			 String res = regex(_var + entry.getKey(), content, 1);
			 if (Check.notEmpty(res)) {
				 return entry.getValue() ? "(" + res + ")" : res;
			}
		}
		return value;
	}
	
	
	private static Set<String> matchShellVar(String sql, boolean varAlone){
		Set<String> set = new HashSet<String>();
		if (varAlone) {
			set.add(sql);
		} else {
			set.addAll(regexList(REGEX_VAR, sql, 1));
		}
		return set;
	}

	private static String read(String path){
		String property = PropertyFileUtil.getProperty("file.source");
		if ("local".equals(property)) {
			return FileUtil.read(path);
		} else if ("hdfs".equals(property)) {
			return HDPFileUtil.read4Linux(path);
		}
		return "";
	}
	

	private static String regex(String regex, String content, int group){
		List<String> regexList = regexList(regex, content, group);
		return  Check.isEmpty(regexList) ?  "" : regexList.get(0);
	}
	
	private static List<String> regexList(String regex, String content, int group){
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);
		List<String> list = new ArrayList<String>();
		while(matcher.find()){
			list.add(matcher.group(group));
		}
		return  list;
	}
    
	public static  Map<String, String> cloneAliaMap(Map<String, String> map) {
    	Map<String, String> map2 = new HashMap<String, String>(map.size());
    	for (Entry<String, String> entry : map.entrySet()) {
    		map2.put(entry.getKey(), entry.getValue());
		}
		return map2;
	}
    
	public static  Map<String, List<ColLine>> cloneSubQueryMap(Map<String, List<ColLine>> map) {
    	Map<String, List<ColLine>> map2 = new HashMap<String, List<ColLine>>(map.size());
    	for (Entry<String, List<ColLine>> entry : map.entrySet()) {
    		List<ColLine> value = entry.getValue();
    		List<ColLine> list = new ArrayList<ColLine>(value.size());
    		for (ColLine colLine : value) {
    			list.add(cloneColLine(colLine));
			}
    		map2.put(entry.getKey(), value);
		}
		return map2;
	}
	
    
	public static  ColLine cloneColLine(ColLine col) {
		return new ColLine(col.getToNameParse(), col.getColCondition(), 
				cloneSet(col.getFromNameSet()), cloneSet(col.getConditionSet()), 
				col.getToTable(), col.getToName());
	}
	
	 
	public static Set<String> cloneSet(Set<String> set){
		Set<String> set2 = new HashSet<String>(set.size());
		for (String string : set) {
			set2.add(string);
		}
		return set2;
	}
	
	public static List<ColLine> cloneList(List<ColLine> list){
		List<ColLine> list2 = new ArrayList<ColLine>(list.size());
		for (ColLine col : list) {
			list2.add(cloneColLine(col));
		}
		return list2;
	}

    
    private ParseUtil(){}
}
