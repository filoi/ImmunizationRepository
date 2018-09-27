package org.hisp.dhis.ivb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;

public class EPIProfileSnapshot 
{
	public static final String EPI_COUNTRY_OUGROUP = "EPI-COUNTRIES";
	public static final String EVM_DE_IDS = "146,145,304,305,306,307,308,309,310,311,312";
	public static final String EPI_DE_IDS = "15,294,314,299,223,221,104,112,113";
	public static final int EPI_CHART_COUNT = 6;
	
	private String flagName;
	private String countryName;
	private Integer curYear;
	private String curDate;
	
	//EVM - Effective Vaccine Management
	//Key is dataelementid
	//Value is datavo where strVal1 is for first column and strVal2 is for second column
	private Map<Integer, GenericDataVO> evmDataMap;

	//Data Map for all other data
	//Key is dataelement id and value is datavalue
	private Map<Integer, GenericDataVO> epiDataMap;
	
	//document UIDs for epi charts
	private List<String> epiCharts;
	
	//For slide3: country activity dashboard table months 
	//Key is M1, M2, M3, .... 
    //Value is month name as MMM-yy format for eg: Mar-18, Apr-18, May-18 etc 
	private Map<String, String> countryADB_monthNamesMap;
	
	//For slide3: country activity dashboard table result 
	//Key is deid+"_"+M1 
    //Value is result color in html code without # for eg: 111DB1 to represent blue 
	private Map<String, Integer> countryADB_ResultMap = new HashMap<String, Integer>();
	
	//List of country activity dashboard dataelements
	private List<GenericTypeObj> countryADB_DeObjs;
	
	//List of country activity dashboard sub headers
	private List<String> countryADB_SubHeaders;

	//Key is header name; value is list of dataelements in that header
	private Map<String, List<GenericTypeObj>> countryADB_Header_DeMap;
	//-------------------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------------------
	public String getFlagName() {
		return flagName;
	}

	public void setFlagName(String flagName) {
		this.flagName = flagName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getCurYear() {
		return curYear;
	}

	public void setCurYear(Integer curYear) {
		this.curYear = curYear;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public Map<Integer, GenericDataVO> getEvmDataMap() {
		return evmDataMap;
	}

	public void setEvmDataMap(Map<Integer, GenericDataVO> evmDataMap) {
		this.evmDataMap = evmDataMap;
	}

	public Map<Integer, GenericDataVO> getEpiDataMap() {
		return epiDataMap;
	}

	public void setEpiDataMap(Map<Integer, GenericDataVO> epiDataMap) {
		this.epiDataMap = epiDataMap;
	}

	public List<String> getEpiCharts() {
		return epiCharts;
	}

	public void setEpiCharts(List<String> epiCharts) {
		this.epiCharts = epiCharts;
	}

	public Map<String, String> getCountryADB_monthNamesMap() {
		return countryADB_monthNamesMap;
	}

	public void setCountryADB_monthNamesMap(Map<String, String> countryADB_monthNamesMap) {
		this.countryADB_monthNamesMap = countryADB_monthNamesMap;
	}

	public Map<String, Integer> getCountryADB_ResultMap() {
		return countryADB_ResultMap;
	}

	public void setCountryADB_ResultMap(Map<String, Integer> countryADB_ResultMap) {
		this.countryADB_ResultMap = countryADB_ResultMap;
	}

	public List<GenericTypeObj> getCountryADB_DeObjs() {
		return countryADB_DeObjs;
	}

	public void setCountryADB_DeObjs(List<GenericTypeObj> countryADB_DeObjs) {
		this.countryADB_DeObjs = countryADB_DeObjs;
	}

	public List<String> getCountryADB_SubHeaders() {
		return countryADB_SubHeaders;
	}

	public void setCountryADB_SubHeaders(List<String> countryADB_SubHeaders) {
		this.countryADB_SubHeaders = countryADB_SubHeaders;
	}

	public Map<String, List<GenericTypeObj>> getCountryADB_Header_DeMap() {
		return countryADB_Header_DeMap;
	}

	public void setCountryADB_Header_DeMap(Map<String, List<GenericTypeObj>> countryADB_Header_DeMap) {
		this.countryADB_Header_DeMap = countryADB_Header_DeMap;
	}
}
