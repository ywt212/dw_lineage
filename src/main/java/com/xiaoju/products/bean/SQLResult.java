package com.xiaoju.products.bean;

import java.util.List;
import java.util.Set;

public class SQLResult {
	Set<String> outputTables;
	Set<String> inputTables;
	List<ColLine> colLineList;
	public Set<String> getOutputTables() {
		return outputTables;
	}
	public void setOutputTables(Set<String> outputTables) {
		this.outputTables = outputTables;
	}
	public Set<String> getInputTables() {
		return inputTables;
	}
	public void setInputTables(Set<String> inputTables) {
		this.inputTables = inputTables;
	}
	public List<ColLine> getColLineList() {
		return colLineList;
	}
	public void setColLineList(List<ColLine> colLineList) {
		this.colLineList = colLineList;
	}

}
