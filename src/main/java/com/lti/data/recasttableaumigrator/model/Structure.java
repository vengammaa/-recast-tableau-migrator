package com.lti.data.recasttableaumigrator.model;

import java.util.List;

public class Structure {

	private String type;
	
	private List<String> text;
	private List<String> lod;
	private List<String> wsize;
	private List<String> size;
	private List<String> color;
	private String xaxis;
	private String yaxis;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getText() {
		return text;
	}
	public void setText(List<String> text) {
		this.text = text;
	}
	public List<String> getLod() {
		return lod;
	}
	public void setLod(List<String> lod) {
		this.lod = lod;
	}
	public List<String> getWsize() {
		return wsize;
	}
	public void setWsize(List<String> wsize) {
		this.wsize = wsize;
	}
	public List<String> getSize() {
		return size;
	}
	public void setSize(List<String> size) {
		this.size = size;
	}
	public List<String> getColor() {
		return color;
	}
	public void setColor(List<String> color) {
		this.color = color;
	}
	
	public String getXaxis() {
		return xaxis;
	}
	public void setXaxis(String xaxis) {
		this.xaxis = xaxis;
	}
	public String getYaxis() {
		return yaxis;
	}
	public void setYaxis(String yaxis) {
		this.yaxis = yaxis;
	}
	@Override
	public String toString() {
		return "Structure [text=" + text + ", lod=" + lod + ", wsize=" + wsize + ", size=" + size + ", color=" + color
				+", type="+type+ "]";
	}
	
	
}
