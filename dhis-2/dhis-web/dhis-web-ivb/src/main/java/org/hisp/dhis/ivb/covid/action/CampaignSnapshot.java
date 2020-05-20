package org.hisp.dhis.ivb.covid.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;

public class CampaignSnapshot 
{
	private String isoCode;
    private String whoRegion;
    private String unicefRegion;
    private String incomeLevel;
    private String gaviEligibleStatus;
    private String curDateStr; 
    
    //Campaign Calendar
    private String plannedColor = "#D11509";
    private String postponedColor = "#F78526";
    private String bothMatchColor = "#04A20B";
    private Map<String, Map<String, List<GenericDataVO>>> dataMap;
    private List<String> monthNames = new ArrayList<>();
    private List<String> subNatNames;
    private List<GenericTypeObj> colList; 
}
