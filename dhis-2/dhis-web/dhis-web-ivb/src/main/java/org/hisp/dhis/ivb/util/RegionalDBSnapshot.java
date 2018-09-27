package org.hisp.dhis.ivb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class RegionalDBSnapshot 
{
	private Integer regionalDBCount = 0;
	
	private String ouIdsByComma = "-1";
	
	private String dsIdsByComma = "-1";
	
	private Integer flagAttributeId = 0;
	
	private List<OrganisationUnit> selOrgUnits = new ArrayList<OrganisationUnit>();
	
	private List<DataSet> selDataSets = new ArrayList<DataSet>();
	
	private Map<Integer, List<GenericTypeObj>> ds_deMap = new HashMap<Integer, List<GenericTypeObj>>();
	
	private Map<String, GenericDataVO> regionalDBDataMap = new HashMap<String, GenericDataVO>();

	
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

	public Map<String, GenericDataVO> getRegionalDBDataMap() {
		return regionalDBDataMap;
	}

	public void setRegionalDBDataMap(Map<String, GenericDataVO> regionalDBDataMap) {
		this.regionalDBDataMap = regionalDBDataMap;
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

	public Integer getRegionalDBCount() {
		return regionalDBCount;
	}

	public void setRegionalDBCount(Integer regionalDBCount) {
		this.regionalDBCount = regionalDBCount;
	}
	
	
	
}
