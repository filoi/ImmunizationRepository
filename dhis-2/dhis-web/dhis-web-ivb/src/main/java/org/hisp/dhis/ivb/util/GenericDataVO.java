package org.hisp.dhis.ivb.util;

import java.util.Date;

public class GenericDataVO 
{
	private String value;
	private String comment;
	private String storedBy;
	private Date lastUpdated;
	private String alertColor;
	
	private String strVal1;
	private String strVal2;
	private String strVal3;
	
	private Integer intVal1;
	
	
	
	public Integer getIntVal1() {
		return intVal1;
	}
	public void setIntVal1(Integer intVal1) {
		this.intVal1 = intVal1;
	}
	public String getStrVal1() {
		return strVal1;
	}
	public void setStrVal1(String strVal1) {
		this.strVal1 = strVal1;
	}
	public String getStrVal2() {
		return strVal2;
	}
	public void setStrVal2(String strVal2) {
		this.strVal2 = strVal2;
	}
	public String getStrVal3() {
		return strVal3;
	}
	public void setStrVal3(String strVal3) {
		this.strVal3 = strVal3;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getStoredBy() {
		return storedBy;
	}
	public void setStoredBy(String storedBy) {
		this.storedBy = storedBy;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getAlertColor() {
		return alertColor;
	}
	public void setAlertColor(String alertColor) {
		this.alertColor = alertColor;
	}
}
