package org.hisp.dhis.ivb.covid.action;

import java.util.Map;

import org.hisp.dhis.ivb.util.GenericDataVO;

public class CampaignVO 
{
	private Map<String, GenericDataVO> colDataMap;

	public Map<String, GenericDataVO> getColDataMap() {
		return colDataMap;
	}

	public void setColDataMap(Map<String, GenericDataVO> colDataMap) {
		this.colDataMap = colDataMap;
	}
	
	
}
