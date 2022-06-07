package com.lti.data.recasttableaumigrator.model;

import java.util.List;

public class Filter {

	private String column;
	private String from;
	private String to;

	private List<String> membersList;

	public List<String> getMembersList() {
		return membersList;
	}

	public void setMembersList(List<String> membersList) {
		this.membersList = membersList;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "Filter [column=" + column + ", from=" + from + ", to=" + to + ", membersList=" + membersList + "]";
	}

}
