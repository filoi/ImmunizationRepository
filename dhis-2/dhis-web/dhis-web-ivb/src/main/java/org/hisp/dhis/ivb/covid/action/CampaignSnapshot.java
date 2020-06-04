package org.hisp.dhis.ivb.covid.action;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

public class CampaignSnapshot 
{
	//Common
	private Integer resultPage;
	private String isoCode;
    private String whoRegion;
    private String unicefRegion;
    private String incomeLevel;
    private String gaviEligibleStatus;
    private String curDateStr;
    private List<GenericTypeObj> colList = new ArrayList<>();
    private List<String> subNatNames = new ArrayList<>();
    private Set<Integer> ouIds = new HashSet<>();
    private Set<Integer> campaignIds = new HashSet<>();
    private Set<String> selCols = new HashSet<>();
    private OrganisationUnitGroupSet unicefRegionsGroupSet;
    private Map<String, String> countryGeneralInfoMap = new HashMap<String, String>();
    
    private List<Section> dsSections = new ArrayList<>();
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    
    //Campaign Tracker
    private NumberFormat nf;
    private String showComment;
    private List<String> selectedOtherData;
    private Map<String, List<CampaignVO>> ctDataMap;
    
    //Campaign Calendar
    private String fromDateStr;
    private String toDateStr;
    private String plannedColor = "#D11509";
    private String postponedColor = "#F78526";
    private String bothMatchColor = "#04A20B";
    private Map<String, String> statusColorMap = new HashMap<>();    
    private  Map<String, List<CampaignVO>> ccDataMap;   
	private List<String> monthNames = new ArrayList<>();
	
	//Campaign Dashboard
	private List<OrganisationUnitGroup> ouGroups = new ArrayList<>();
	private List<GenericTypeObj> rowObjList = new ArrayList<>();
	private HashMap<String, Integer> cdbDataMap = new HashMap<>();
	
    // --------------------------------------------------------------------------
    // Getters & Setters
    // --------------------------------------------------------------------------
	public Map<String, List<CampaignVO>> getCcDataMap() {
		return ccDataMap;
	}
	public List<OrganisationUnitGroup> getOuGroups() {
		return ouGroups;
	}
	public void setOuGroups(List<OrganisationUnitGroup> ouGroups) {
		this.ouGroups = ouGroups;
	}
	public List<GenericTypeObj> getRowObjList() {
		return rowObjList;
	}
	public void setRowObjList(List<GenericTypeObj> rowObjList) {
		this.rowObjList = rowObjList;
	}
	public HashMap<String, Integer> getCdbDataMap() {
		return cdbDataMap;
	}
	public void setCdbDataMap(HashMap<String, Integer> cdbDataMap) {
		this.cdbDataMap = cdbDataMap;
	}
	public void setCcDataMap(Map<String, List<CampaignVO>> ccDataMap) {
		this.ccDataMap = ccDataMap;
	}
	public void setStatusColorMap(Map<String, String> statusColorMap) {
		this.statusColorMap = statusColorMap;
	}
	public Map<String, String> getStatusColorMap() {
		return statusColorMap;
	}    
	public String getIsoCode() {
		return isoCode;
	}
	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	public String getWhoRegion() {
		return whoRegion;
	}
	public void setWhoRegion(String whoRegion) {
		this.whoRegion = whoRegion;
	}
	public String getUnicefRegion() {
		return unicefRegion;
	}
	public void setUnicefRegion(String unicefRegion) {
		this.unicefRegion = unicefRegion;
	}
	public String getIncomeLevel() {
		return incomeLevel;
	}
	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}
	public String getGaviEligibleStatus() {
		return gaviEligibleStatus;
	}
	public void setGaviEligibleStatus(String gaviEligibleStatus) {
		this.gaviEligibleStatus = gaviEligibleStatus;
	}
	public String getCurDateStr() {
		return curDateStr;
	}
	public void setCurDateStr(String curDateStr) {
		this.curDateStr = curDateStr;
	}
	public List<GenericTypeObj> getColList() {
		return colList;
	}
	public void setColList(List<GenericTypeObj> colList) {
		this.colList = colList;
	}
	public List<String> getSubNatNames() {
		return subNatNames;
	}
	public void setSubNatNames(List<String> subNatNames) {
		this.subNatNames = subNatNames;
	}
	public NumberFormat getNf() {
		return nf;
	}
	public void setNf(NumberFormat nf) {
		this.nf = nf;
	}
	public String getShowComment() {
		return showComment;
	}
	public void setShowComment(String showComment) {
		this.showComment = showComment;
	}
	public List<String> getSelectedOtherData() {
		return selectedOtherData;
	}
	public void setSelectedOtherData(List<String> selectedOtherData) {
		this.selectedOtherData = selectedOtherData;
	}
	public Map<String, List<CampaignVO>> getCtDataMap() {
		return ctDataMap;
	}
	public void setCtDataMap(Map<String, List<CampaignVO>> ctDataMap) {
		this.ctDataMap = ctDataMap;
	}
	public String getPlannedColor() {
		return plannedColor;
	}
	public void setPlannedColor(String plannedColor) {
		this.plannedColor = plannedColor;
	}
	public String getPostponedColor() {
		return postponedColor;
	}
	public void setPostponedColor(String postponedColor) {
		this.postponedColor = postponedColor;
	}
	public String getBothMatchColor() {
		return bothMatchColor;
	}
	public void setBothMatchColor(String bothMatchColor) {
		this.bothMatchColor = bothMatchColor;
	}	
	public List<String> getMonthNames() {
		return monthNames;
	}
	public void setMonthNames(List<String> monthNames) {
		this.monthNames = monthNames;
	}
	public Set<Integer> getOuIds() {
		return ouIds;
	}
	public void setOuIds(Set<Integer> ouIds) {
		this.ouIds = ouIds;
	}
	public Set<Integer> getCampaignIds() {
		return campaignIds;
	}
	public void setCampaignIds(Set<Integer> campaignIds) {
		this.campaignIds = campaignIds;
	}
	public Set<String> getSelCols() {
		return selCols;
	}
	public void setSelCols(Set<String> selCols) {
		this.selCols = selCols;
	}
	public List<Section> getDsSections() {
		return dsSections;
	}
	public void setDsSections(List<Section> dsSections) {
		this.dsSections = dsSections;
	}
	public List<OrganisationUnit> getOrgUnitList() {
		return orgUnitList;
	}
	public void setOrgUnitList(List<OrganisationUnit> orgUnitList) {
		this.orgUnitList = orgUnitList;
	}
	public OrganisationUnitGroupSet getUnicefRegionsGroupSet() {
		return unicefRegionsGroupSet;
	}
	public void setUnicefRegionsGroupSet(OrganisationUnitGroupSet unicefRegionsGroupSet) {
		this.unicefRegionsGroupSet = unicefRegionsGroupSet;
	}
	public Map<String, String> getCountryGeneralInfoMap() {
		return countryGeneralInfoMap;
	}
	public void setCountryGeneralInfoMap(Map<String, String> countryGeneralInfoMap) {
		this.countryGeneralInfoMap = countryGeneralInfoMap;
	}
	public String getFromDateStr() {
		return fromDateStr;
	}
	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}
	public String getToDateStr() {
		return toDateStr;
	}
	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}
	public Integer getResultPage() {
		return resultPage;
	}
	public void setResultPage(Integer resultPage) {
		this.resultPage = resultPage;
	}
	
}
