package org.hisp.dhis.ivb.isc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class ISCSnapshot 
{
	private List<GenericTypeObj> orgUnits = new ArrayList<GenericTypeObj>();
	
	public List<GenericTypeObj> getOrgUnits() {
		return orgUnits;
	}
	public void setOrgUnits(List<GenericTypeObj> orgUnits) {
		this.orgUnits = orgUnits;
	}
	private Map<String, GenericDataVO> dvMap = new HashMap<String, GenericDataVO>();
	
	public Map<String, GenericDataVO> getDvMap() {
		return dvMap;
	}
	public void setDvMap(Map<String, GenericDataVO> dvMap) {
		this.dvMap = dvMap;
	}
}
