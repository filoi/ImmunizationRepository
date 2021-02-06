package org.hisp.dhis.ivb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

public class CovidIntroSnapshot 
{
	 //Input params
	 private String isoCode;
	 private String whoRegion;
	 private String unicefRegion;
	 private String incomeLevel;
	 private String gaviEligibleStatus;
	 private String showIndType;
	 private String includeComment;
	 private String nonZeroCountries="";
	 private String showCovaxFacility;
	 private String showWBSupport;
	 private String showSource;
	 private OrganisationUnitGroupSet unicefRegionsGroupSet;
	 private List<OrganisationUnit> selOrgUnits = new ArrayList<>();
	 private List<String> indTypes = new ArrayList<>();
	 private String ouIdsByComma = "-1";
	 private String indTypesByComma = "";
	 private Integer flagAttributeId = 0;
	 private Integer indTypeAttributeId = 0;
	 private Integer generalDeGroupId = 0;
	 private OrganisationUnit selOrgUnit;
	  
	 

	//other params
	 private String curDateStr;
	 private String deIdsByComma = "-1";
	 private Map<String, List<Integer>> it_deIdMap = new HashMap<>();
	 private Map<Integer, GenericTypeObj> deMap = new HashMap<>();
	 private Map<String, GenericDataVO> dataMap = new HashMap<>();
	 private Set<Integer> nonZeroOrgUnitIds = new HashSet<>();
	 private Set<Integer> filterDeIds = new HashSet<>();
	 private List<String> anonymousOuNames = new ArrayList<>();
	 private Map<String, OrganisationUnit> anonymousOuMap = new HashMap<>();
	 
	private String lastUpdated;
	 
	private String dsIdsByComma = "-1";
	
	
	private List<DataSet> selDataSets = new ArrayList<DataSet>();
	
	private Map<Integer, List<GenericTypeObj>> ds_deMap = new HashMap<Integer, List<GenericTypeObj>>();
	
	

	//-------------------------------------------------------------------
	//Getters & Setters
	//-------------------------------------------------------------------
	
	
	public OrganisationUnitGroupSet getUnicefRegionsGroupSet() {
		return unicefRegionsGroupSet;
	}

	public List<String> getAnonymousOuNames() {
		return anonymousOuNames;
	}

	public void setAnonymousOuNames(List<String> anonymousOuNames) {
		this.anonymousOuNames = anonymousOuNames;
	}

	public Map<String, OrganisationUnit> getAnonymousOuMap() {
		return anonymousOuMap;
	}

	public void setAnonymousOuMap(Map<String, OrganisationUnit> anonymousOuMap) {
		this.anonymousOuMap = anonymousOuMap;
	}

	public OrganisationUnit getSelOrgUnit() {
		return selOrgUnit;
	}

	public void setSelOrgUnit(OrganisationUnit selOrgUnit) {
		this.selOrgUnit = selOrgUnit;
	}

	public void setUnicefRegionsGroupSet(OrganisationUnitGroupSet unicefRegionsGroupSet) {
		this.unicefRegionsGroupSet = unicefRegionsGroupSet;
	}
	
	public List<OrganisationUnit> getSelOrgUnits() {
		return selOrgUnits;
	}

	public void setSelOrgUnits(List<OrganisationUnit> selOrgUnits) {
		this.selOrgUnits = selOrgUnits;
	}

	public List<DataSet> getSelDataSets() {
		return selDataSets;
	}

	public void setSelDataSets(List<DataSet> selDataSets) {
		this.selDataSets = selDataSets;
	}

	public Map<Integer, List<GenericTypeObj>> getDs_deMap() {
		return ds_deMap;
	}

	public void setDs_deMap(Map<Integer, List<GenericTypeObj>> ds_deMap) {
		this.ds_deMap = ds_deMap;
	}

	public String getOuIdsByComma() {
		return ouIdsByComma;
	}

	public void setOuIdsByComma(String ouIdsByComma) {
		this.ouIdsByComma = ouIdsByComma;
	}

	public String getDsIdsByComma() {
		return dsIdsByComma;
	}

	public void setDsIdsByComma(String dsIdsByComma) {
		this.dsIdsByComma = dsIdsByComma;
	}

	public Integer getFlagAttributeId() {
		return flagAttributeId;
	}

	public void setFlagAttributeId(Integer flagAttributeId) {
		this.flagAttributeId = flagAttributeId;
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

	public String getShowIndType() {
		return showIndType;
	}
	public void setShowIndType(String showIndType) {
		this.showIndType = showIndType;
	}

	public String getIncludeComment() {
		return includeComment;
	}
	public void setIncludeComment(String includeComment) {
		this.includeComment = includeComment;
	}

	public String getNonZeroCountries() {
		return nonZeroCountries;
	}

	public void setNonZeroCountries(String nonZeroCountries) {
		this.nonZeroCountries = nonZeroCountries;
	}

	public List<String> getIndTypes() {
		return indTypes;
	}

	public void setIndTypes(List<String> indTypes) {
		this.indTypes = indTypes;
	}

	public String getIndTypesByComma() {
		return indTypesByComma;
	}

	public void setIndTypesByComma(String indTypesByComma) {
		this.indTypesByComma = indTypesByComma;
	}

	public Integer getIndTypeAttributeId() {
		return indTypeAttributeId;
	}

	public void setIndTypeAttributeId(Integer indTypeAttributeId) {
		this.indTypeAttributeId = indTypeAttributeId;
	}

	public Map<String, List<Integer>> getIt_deIdMap() {
		return it_deIdMap;
	}

	public void setIt_deIdMap(Map<String, List<Integer>> it_deIdMap) {
		this.it_deIdMap = it_deIdMap;
	}

	public Map<Integer, GenericTypeObj> getDeMap() {
		return deMap;
	}

	public void setDeMap(Map<Integer, GenericTypeObj> deMap) {
		this.deMap = deMap;
	}

	public Map<String, GenericDataVO> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, GenericDataVO> dataMap) {
		this.dataMap = dataMap;
	}

	public String getDeIdsByComma() {
		return deIdsByComma;
	}

	public void setDeIdsByComma(String deIdsByComma) {
		this.deIdsByComma = deIdsByComma;
	}

	public Set<Integer> getNonZeroOrgUnitIds() {
		return nonZeroOrgUnitIds;
	}

	public void setNonZeroOrgUnitIds(Set<Integer> nonZeroOrgUnitIds) {
		this.nonZeroOrgUnitIds = nonZeroOrgUnitIds;
	}

	public Integer getGeneralDeGroupId() {
		return generalDeGroupId;
	}

	public void setGeneralDeGroupId(Integer generalDeGroupId) {
		this.generalDeGroupId = generalDeGroupId;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Set<Integer> getFilterDeIds() {
		return filterDeIds;
	}

	public void setFilterDeIds(Set<Integer> filterDeIds) {
		this.filterDeIds = filterDeIds;
	}

	public String getShowCovaxFacility() {
		return showCovaxFacility;
	}

	public void setShowCovaxFacility(String showCovaxFacility) {
		this.showCovaxFacility = showCovaxFacility;
	}

	public String getShowWBSupport() {
		return showWBSupport;
	}

	public void setShowWBSupport(String showWBSupport) {
		this.showWBSupport = showWBSupport;
	}

	public String getShowSource() {
		return showSource;
	}

	public void setShowSource(String showSource) {
		this.showSource = showSource;
	}

	public String getCurDateStr() {
		return curDateStr;
	}

	public void setCurDateStr(String curDateStr) {
		this.curDateStr = curDateStr;
	}
	
	
}
