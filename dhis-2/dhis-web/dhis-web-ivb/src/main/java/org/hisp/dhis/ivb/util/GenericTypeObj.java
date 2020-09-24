package org.hisp.dhis.ivb.util;

public class GenericTypeObj 
{
	private Integer id;
	private String name;
	private String alias;
	private String shortName;  
	private String code;

	private Integer intAttrib1;
	
	
	private String strAttrib1;

	private String strAttrib2;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStrAttrib1() {
		return strAttrib1;
	}
	public void setStrAttrib1(String strAttrib1) {
		this.strAttrib1 = strAttrib1;
	}
	public String getStrAttrib2() {
		return strAttrib2;
	}
	public void setStrAttrib2(String strAttrib2) {
		this.strAttrib2 = strAttrib2;
	}
	public Integer getIntAttrib1() {
		return intAttrib1;
	}
	public void setIntAttrib1(Integer intAttrib1) {
		this.intAttrib1 = intAttrib1;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
}
