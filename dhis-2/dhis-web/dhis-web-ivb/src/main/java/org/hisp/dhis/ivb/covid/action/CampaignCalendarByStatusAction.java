package org.hisp.dhis.ivb.covid.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.isc.ISCReportHelper;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class CampaignCalendarByStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SectionService sectionService;

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    @Autowired 
    private LookupService lookupService;

    @Autowired
	private ISCReportHelper iscReportHelper;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private ProgramStageService programStageService;
    
    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------
    
    private List<Integer> selectedListVaccine;

    private String introStartDate;

    private String introEndDate;

    private Integer orgUnitGroupId;

    

    private String orgUnitId;

    public void setSelectedListVaccine( List<Integer> selectedListVaccine )
    {
        this.selectedListVaccine = selectedListVaccine;
    }

    public void setIntroStartDate( String introStartDate )
    {
        this.introStartDate = introStartDate;
    }

    public void setIntroEndDate( String introEndDate )
    {
        this.introEndDate = introEndDate;
    }

    public String getIntroStartDate()
    {
        return introStartDate;
    }

    public String getIntroEndDate()
    {
        return introEndDate;
    }

    public void setOrgUnitGroupId( Integer orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }

    

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataElementType;
    
    public void setDataElementType( String dataElementType )
    {
        this.dataElementType = dataElementType;
    }
    
    private List<Integer> orgUnitIds = new ArrayList<Integer>();
    
    public void setOrgUnitIds(List<Integer> orgUnitIds) 
    {
	this.orgUnitIds = orgUnitIds;
    }

    private String isoCode;
    
    private String whoRegion;
    
    private String unicefRegion;
    
    private String incomeLevel;
    
    private String gaviEligibleStatus;
    
    public String getIsoCode() {
		return isoCode;
	}
	public void setIsoCode(String isoCode){
		this.isoCode = isoCode;
	}

	public String getWhoRegion(){
		return whoRegion;
	}
	public void setWhoRegion(String whoRegion){
		this.whoRegion = whoRegion;
	}

	public String getUnicefRegion(){
		return unicefRegion;
	}
	public void setUnicefRegion(String unicefRegion){
		this.unicefRegion = unicefRegion;
	}

	public String getIncomeLevel(){
		return incomeLevel;
	}
	public void setIncomeLevel(String incomeLevel){
		this.incomeLevel = incomeLevel;
	}

	public String getGaviEligibleStatus(){
		return gaviEligibleStatus;
	}
	public void setGaviEligibleStatus(String gaviEligibleStatus){
		this.gaviEligibleStatus = gaviEligibleStatus;
	}

    private OrganisationUnitGroupSet unicefRegionsGroupSet;
    public OrganisationUnitGroupSet getUnicefRegionsGroupSet(){
        return unicefRegionsGroupSet;
    }

    private List<String> selectedOtherData;
    public void setSelectedOtherData(List<String> selectedOtherData) {
		this.selectedOtherData = selectedOtherData;
	}

	// -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    Map<String, String> statusColorMap = new HashMap<>();    
    public Map<String, String> getStatusColorMap() {
		return statusColorMap;
	}

	private String curDateStr;    
    public String getCurDateStr() {
		return curDateStr;
	}
    
    String plannedColor = "#D11509";
    String postponedColor = "#F78526";
    String bothMatchColor = "#04A20B";
    public String getPlannedColor() {
		return plannedColor;
	}
	public String getPostponedColor() {
		return postponedColor;
	}
	public String getBothMatchColor() {
		return bothMatchColor;
	}


	private Map<String, List<CampaignVO>> dataMap;
	public Map<String, List<CampaignVO>> getDataMap() {
		return dataMap;
	}
	
	/*
	private Map<String, Map<String, List<GenericDataVO>>> dataMap;
	public Map<String, Map<String, List<GenericDataVO>>> getDataMap() {
		return dataMap;
	}
	*/

	private List<String> monthNames = new ArrayList<>();
	public List<String> getMonthNames() {
		return monthNames;
	}

	private List<String> subNatNames;
    public List<String> getSubNatNames() {
		return subNatNames;
	}
	
	private List<GenericTypeObj> colList; 
	public List<GenericTypeObj> getColList() {
		return colList;
	}
	
    public String getDataElementType()
    {
        return dataElementType;
    }

    private Map<OrganisationUnit, Map<String, Map<Integer,String>>> orgUnitResultMap;
    
    private Map<OrganisationUnit, Map<String, Map<Integer,String>>> orgUnitCommentMap;    

    public Map<OrganisationUnit, Map<String, Map<Integer, String>>> getOrgUnitCommentMap() 
    {
		return orgUnitCommentMap;
	}

	private List<Section> dataSetSections;

    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();

    private List<DataElementGroup> dataElementGroups;

    private String language;

    private String userName;

    private DataElementGroup introYearDEGroup;
    
    public DataElementGroup getIntroYearDEGroup()
    {
        return introYearDEGroup;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getUserName()
    {
        return userName;
    }

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    public Map<OrganisationUnit, Map<String, Map<Integer, String>>> getOrgUnitResultMap()
    {
        return orgUnitResultMap;
    }

    public List<Section> getDataSetSections()
    {
        return dataSetSections;
    }

    private int messageCount;

    public int getMessageCount()
    {
        return messageCount;
    }

    private String adminStatus;

    public String getAdminStatus()
    {
        return adminStatus;
    }

    private Map<String, String> countryGeneralInfoMap = new HashMap<String, String>();
    public Map<String, String> getCountryGeneralInfoMap()
    {
        return countryGeneralInfoMap;
    }

    private Map<String, DataValue> headerDataValueMap = new HashMap<String, DataValue>();
    public Map<String, DataValue> getHeaderDataValueMap()
    {
        return headerDataValueMap;
    }
    
    private String dateSelection = "Y";
    public String getDateSelection() {
		return dateSelection;
	}

	// --------------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------------
    public String execute()
    {
    	//Selected addl columns for orgunit info
        if( isoCode != null )
        	isoCode = "ON";
        if( whoRegion != null )
        	whoRegion = "ON";
        whoRegion = "ON";//making this ON as there is no user selection from first page
        if( unicefRegion != null )
        	unicefRegion = "ON";
        if( incomeLevel != null )
        	incomeLevel = "ON";
        if( gaviEligibleStatus != null )
        	gaviEligibleStatus = "ON";
        
        
        //Selected Orgunits
        Lookup lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        unicefRegionsGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) );
        if( orgUnitIds.size() > 1 )
        {
            for(Integer id : orgUnitIds )
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( id );
                orgUnitList.add( orgUnit );
            }
        }
        else if(selectionTreeManager.getReloadedSelectedOrganisationUnits() != null)
        { 
            orgUnitList =  new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) ;            
            List<OrganisationUnit> lastLevelOrgUnit = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> userOrgUnits = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
            for ( OrganisationUnit orgUnit : userOrgUnits )
            {
                if ( orgUnit.getHierarchyLevel() == 3  )
                {
                    lastLevelOrgUnit.add( orgUnit );
                }
                else
                {
                    lastLevelOrgUnit.addAll( organisationUnitService.getOrganisationUnitsAtLevel( 3, orgUnit ) );
                }
            }
            orgUnitList.retainAll( lastLevelOrgUnit );
        }
        Collections.sort(orgUnitList, new IdentifiableObjectNameComparator() );
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );
        String ouIdsByComma = "-1";
        if ( orgUnitList.size() > 0 ){
        	ouIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        //System.out.println(ouIdsByComma);
        
        //Campaigns 
        lookup = lookupService.getLookupByName( "CAMPAIGN_DATASET_UID" );
        DataSet dataSet = dataSetService.getDataSet( lookup.getValue() );
        dataSetSections = new ArrayList<Section>( dataSet.getSections() );
       
        Set<String> sectionCodes = new HashSet<>();
        for( Section section : dataSetSections ){
            sectionCodes.add( section.getCode().trim().toLowerCase() );
        }
        lookup = lookupService.getLookupByName( "CAMPAIGN_PROGRAM_STAGE_IDS" );
        String psIdsByComma = "-1";
        List<ProgramStage> programStages = new ArrayList<>();
        for(String psId : lookup.getValue().split(",")) {
        	ProgramStage ps = programStageService.getProgramStage( Integer.parseInt(psId) );
        	if( ps!= null && sectionCodes.contains( ps.getName().trim().toLowerCase()) ) {
        		programStages.add( ps );
        		psIdsByComma += ","+ps.getId();
        	}
        }
        lookup = lookupService.getLookupByName( "CAMPAIGN_SUBNATIONAL_DEID" );
        int subNationalDeId = Integer.parseInt( lookup.getValue() );
        lookup = lookupService.getLookupByName( "CAMPAIGN_STATUS_DEID" );
        int statusDeId = Integer.parseInt( lookup.getValue() );

        
        //Selected Columns
        selectedOtherData = new ArrayList<>();
        selectedOtherData.add("COL_1");
        selectedOtherData.add("COL_5");
        selectedOtherData.add("COL_3");
        Map<Integer, String> deColMap = new HashMap<>();
        String deIdsByComma = "-1";
        String psDeIdsByComma = "-1";
        psDeIdsByComma += "," +subNationalDeId;
        lookup = lookupService.getLookupByName( "CAMPAIGN_COLUMNS_INFO" );
        String campaignColInfo = lookup.getValue();
        colList = new ArrayList<GenericTypeObj>();
        for( String colInfo : campaignColInfo.split("@!@") ) {
        	if( selectedOtherData.contains( colInfo.split("@-@")[0] ) ) {
	        	GenericTypeObj colObj = new GenericTypeObj();
	        	colObj.setCode( colInfo.split("@-@")[0] );
	        	colObj.setName( colInfo.split("@-@")[1] );
	        	colObj.setStrAttrib1( colInfo.split("@-@")[2] ); //deids
	        	colObj.setStrAttrib2( colInfo.split("@-@")[3] ); //ps deids
	        	colList.add( colObj );
	        	deIdsByComma += ","+colInfo.split("@-@")[2];
	        	psDeIdsByComma += ","+colInfo.split("@-@")[3];
	        	for(String deIdStr : colObj.getStrAttrib1().split(",") ) {
	        		deColMap.put(Integer.parseInt(deIdStr), colObj.getCode());
	        	}
	        	deColMap.put(Integer.parseInt(colObj.getStrAttrib2()), colObj.getCode());
        	}
        }
        
        Set<Integer> deIds = new HashSet<Integer>( deColMap.keySet() );
        Map<Integer, String> deSectionMap = new HashMap<>();
        Map<Integer, Map<String, Integer>> sectionDeMap = new HashMap<>();
        for(Section section : dataSetSections ) {
    		for( DataElement de : section.getDataElements()) {
    			if( deIds.contains(de.getId()) ) {
    				deSectionMap.put(de.getId(), section.getCode());
    				if( sectionDeMap.get( section.getId() ) == null)
    					sectionDeMap.put(section.getId(), new HashMap<>());
    				sectionDeMap.get( section.getId() ).put(deColMap.get(de.getId()), de.getId());
    			}
    		}
        }
        
        Map<Integer, Map<String, Integer>> psDeMap = new HashMap<>();
        for(ProgramStage ps : programStages ) {
    		for( DataElement de : ps.getAllDataElements()) {
    			if( deIds.contains(de.getId()) ) {
    				if( psDeMap.get( ps.getId() ) == null)
    					psDeMap.put(ps.getId(), new HashMap<>());
    				psDeMap.get( ps.getId() ).put(deColMap.get(de.getId()), de.getId());
    			}
    		}
        }
        
        /*
        for(Integer sectionId : sectionDeMap.keySet() ) {
        	for(String colKey : sectionDeMap.get(sectionId).keySet()) {
        		System.out.println(sectionId + " and " + colKey + "  = " + sectionDeMap.get(sectionId).get(colKey) );
        	}
        }
        */
        //System.out.println(deIdsByComma);
       
        Map<String, GenericDataVO> dvDataMap = iscReportHelper.getLatestDataValues( deIdsByComma, ouIdsByComma );
		
        //Color Codes
        lookup = lookupService.getLookupByName( "CAMPAIGN_CALENDAR_COLOR_CODES" );
        try{ plannedColor = lookup.getValue().split("@!@")[0];}catch(Exception e) {}
        try{ postponedColor = lookup.getValue().split("@!@")[1];}catch(Exception e) {}
        try{ bothMatchColor = lookup.getValue().split("@!@")[2];}catch(Exception e) {}
        
       
        lookup = lookupService.getLookupByName( "CAMPAIGN_CALENDAR_STATUS_COLOR_MAP" );
        String statusColorInfo = lookup.getValue();
        for(String statusColor : statusColorInfo.split("@!@") ) {
        	statusColorMap.put(statusColor.split("@-@")[0], statusColor.split("@-@")[1]);
        }
        
        SimpleDateFormat mdf = new SimpleDateFormat("MMM-yyyy");
        dataMap = new HashMap<>();
        //Arranging aggregated data
        for( int ouId : organisationUnitIds ) {
        	String key1 = ouId+"_National";
        	//dataMap.put(key1, new HashMap<>());
        	for(Section section : dataSetSections ) {        		
        		CampaignVO cvo = new CampaignVO();
        		cvo.setColDataMap( new HashMap<>() );
        		int flag = 0;
        		
        		int aggStatusDeId = 0;
        		try{ aggStatusDeId = sectionDeMap.get( section.getId() ).get("COL_3"); }catch(Exception e) {}
        		String statusVal = "";
        		try { 
        			statusVal = dvDataMap.get(ouId+"_"+aggStatusDeId).getStrVal1();
        			cvo.getColDataMap().put("STATUS", dvDataMap.get(ouId+"_"+aggStatusDeId) );
        		}catch(Exception e) {}
        		        		        		        		
        		int plannedDeId = 0;
        		try{ plannedDeId = sectionDeMap.get( section.getId() ).get("COL_1"); }catch(Exception e) {}
        		int postponedDeId = 0;
        		try{ postponedDeId = sectionDeMap.get( section.getId() ).get("COL_5");}catch(Exception e) {}
        		
        		Date plannedDate = null;
        		String plannedVal = "";
        		try { plannedVal = dvDataMap.get(ouId+"_"+plannedDeId).getStrVal1();}catch(Exception e) {}
        		//System.out.println( "Planned: "+ouId+"_"+plannedDeId + " = " + plannedVal);
        		if( !plannedVal.trim().equals("") ) {
        			try{ plannedDate = getStartDateByString( plannedVal );}catch(Exception e) {}
        			//System.out.println( "Planned Date = "+ plannedDate );
        			if( plannedDate == null ) {
        				GenericDataVO dvo = new GenericDataVO();
        				dvo.setStrVal1( section.getCode() );
        				dvo.setStrVal2( plannedColor );
        				cvo.getColDataMap().put("NONE1", dvo);
        				flag = 1;
        			}
        		}
        		
        		Date postponedDate = null;
        		String postponedVal = "";
        		try { postponedVal = dvDataMap.get(ouId+"_"+postponedDeId).getStrVal1();}catch(Exception e) {}
        		//System.out.println( "Postponed: "+ ouId+"_"+postponedDeId + " = " + postponedVal);
        		if( !postponedVal.trim().equals("") ) {
        			try{ postponedDate = getStartDateByString( postponedVal );}catch(Exception e) {}
        			//System.out.println( "Postponed Date = "+ postponedDate );
        			if( postponedDate == null ) {
        				GenericDataVO dvo = new GenericDataVO();
        				dvo.setStrVal1( section.getCode() );
        				dvo.setStrVal2( postponedColor );
        				cvo.getColDataMap().put("NONE2", dvo);
        				flag = 1;
        			}        			
        		}
        		
        		if( plannedDate != null && postponedDate != null && plannedDate.equals(postponedDate) ) {
        			String monthName = mdf.format( postponedDate ); 
    				GenericDataVO dvo = new GenericDataVO();
    				dvo.setStrVal1( section.getCode() );
    				dvo.setStrVal2( bothMatchColor );
    				cvo.getColDataMap().put(monthName, dvo);
    				flag = 1;
        		}
        		else {
        			if( plannedDate != null ) {
        				String monthName = mdf.format( plannedDate ); 
        				GenericDataVO dvo = new GenericDataVO();
        				dvo.setStrVal1( section.getCode() );
        				dvo.setStrVal2( plannedColor );
        				cvo.getColDataMap().put(monthName, dvo);
        				flag = 1;
        			}
        			
        			if( postponedDate != null ) {
        				String monthName = mdf.format( postponedDate ); 
        				GenericDataVO dvo = new GenericDataVO();
        				dvo.setStrVal1( section.getCode() );
        				dvo.setStrVal2( postponedColor );
        				cvo.getColDataMap().put(monthName, dvo);
        				flag = 1;
        			}
        		}
        		
        		if( flag == 1 ) {
    				if( dataMap.get(key1) == null )
    					dataMap.put(key1, new ArrayList<>());
    				dataMap.get(key1).add( cvo );
        		}
        	}
        }

        
        //Arranging event data
        Set<String> subNationalNames = new HashSet<>();
        subNationalNames.add("National");
        Map<String, Map<Integer, CampaignVO>> eventDataMap = iscReportHelper.getEventData( psIdsByComma, psDeIdsByComma, ouIdsByComma );
        for( int ouId : organisationUnitIds ) {
        	for(ProgramStage ps : programStages ) {
        		String eBaseKey = ps.getId()+"_"+ouId;
        		if( eventDataMap.get(eBaseKey) == null ) {
        			continue;
        		}
        		for(Integer psInsId : eventDataMap.get(eBaseKey).keySet()) {
        			CampaignVO cvo = new CampaignVO();
        			cvo.setColDataMap( new HashMap<>() );
        			int flag = 0;
        			
        			String subNationName = "National";
        			try { subNationName = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(subNationalDeId+"").getStrVal1();}catch(Exception e) {}
        			subNationalNames.add(subNationName);
        			String key1 = ouId+"_"+subNationName;
        			            		
            		String statusVal = "";
            		try { 
            			statusVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(statusDeId+"").getStrVal1();
            			cvo.getColDataMap().put("STATUS", eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(statusDeId+"") );
            		}catch(Exception e) {}
            		
            		//if( dataMap.get(key1) == null)
            		//	dataMap.put(key1, new HashMap<>());
	        		int plannedDeId = 0;
	        		try{ plannedDeId = psDeMap.get( ps.getId() ).get("COL_1"); }catch(Exception e) {}
	        		int postponedDeId = 0;
	        		try{ postponedDeId = psDeMap.get( ps.getId() ).get("COL_5");}catch(Exception e) {}
        		
	        		Date plannedDate = null;
	        		String plannedVal = "";
	        		try { plannedVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(plannedDeId+"").getStrVal1();}catch(Exception e) {}
	        		//System.out.println( "Planned: "+eBaseKey+"_"+plannedDeId + " = " + plannedVal);
	        		if( !plannedVal.trim().equals("") ) {
	        			try{ plannedDate = getStartDateByString( plannedVal );}catch(Exception e) {}
	        			//System.out.println( "Planned Date = "+ plannedDate );
	        			if( plannedDate == null ) {
	        				GenericDataVO dvo = new GenericDataVO();
	        				dvo.setStrVal1( ps.getName() );
	        				dvo.setStrVal2( plannedColor );
	        				cvo.getColDataMap().put("NONE1", dvo);
	        				flag = 1;	        				
	        			}        			
	        		}
        		
	        		Date postponedDate = null;
	        		String postponedVal = "";
	        		try { postponedVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(postponedDeId+"").getStrVal1();}catch(Exception e) {}
	        		//System.out.println( "Postponed: "+ eBaseKey+"_"+postponedDeId + " = " + postponedVal);
	        		if( !postponedVal.trim().equals("") ) {
	        			try{ postponedDate = getStartDateByString( postponedVal );}catch(Exception e) {}
	        			//System.out.println( "Postponed Date = "+ postponedDate );
	        			if( postponedDate == null ) {
	        				GenericDataVO dvo = new GenericDataVO();
	        				dvo.setStrVal1( ps.getName() );
	        				dvo.setStrVal2( postponedColor );
	        				cvo.getColDataMap().put("NONE2", dvo);
	        				flag = 1;	        				
	        			}        			
	        		}
        		
	        		if( plannedDate != null && postponedDate != null && plannedDate.equals(postponedDate) ) {
	        			String monthName = mdf.format( postponedDate ); 
	    				GenericDataVO dvo = new GenericDataVO();
	    				dvo.setStrVal1( ps.getName() );
	    				dvo.setStrVal2( bothMatchColor );
	    				cvo.getColDataMap().put(monthName, dvo);	    				
	        		}
	        		else {
	        			if( plannedDate != null ) {
	        				String monthName = mdf.format( plannedDate ); 
	        				GenericDataVO dvo = new GenericDataVO();
	        				dvo.setStrVal1( ps.getName() );
	        				dvo.setStrVal2( plannedColor );
	        				cvo.getColDataMap().put(monthName, dvo);	        				
	        			}
        			
	        			if( postponedDate != null ) {
	        				String monthName = mdf.format( postponedDate ); 
	        				GenericDataVO dvo = new GenericDataVO();
	        				dvo.setStrVal1( ps.getName() );
	        				dvo.setStrVal2( postponedColor );
	        				cvo.getColDataMap().put(monthName, dvo);
	        			}
	        		}
	        		
	        		if( flag == 1 ) {
	    				if( dataMap.get(key1) == null )
	    					dataMap.put(key1, new ArrayList<>());
	    				dataMap.get(key1).add( cvo );
	        		}
        		}//psi for loop
        		
        	}//ps for loop
        }//ou for loop

        
        //Selected Months        
        try
        {
            if( introStartDate.length() == 4 ){
                introStartDate = introStartDate +"-01";
            }

            if( introEndDate.length() == 4 ){
                introEndDate = introEndDate +"-12";
            }
        }
        catch( Exception e ){
        }
        
        Date sDate = null;
        Date eDate = null;

        if( introStartDate != null && !introStartDate.trim().equals( "" ) ){
            sDate = getStartDateByString( introStartDate );
        }
        
        if( introEndDate != null && !introEndDate.trim().equals( "" ) ){
            eDate = getEndDateByString( introEndDate );
        }
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime( sDate );
        
        Calendar toCal = Calendar.getInstance();
        toCal.setTime( eDate );
        
        
        while( fromCal.before( toCal) || fromCal.equals(toCal) ) {
        	monthNames.add( mdf.format(fromCal.getTime()) );
        	fromCal.add(Calendar.MONTH, 1);
        }
        
        subNationalNames.remove("National");
        subNatNames = new ArrayList<>();
        subNatNames.addAll( subNationalNames );
        Collections.sort( subNatNames );
        subNatNames.add(0, "National");
        /*
        for(String baseKey : dataMap.keySet() ) {
        	for(String mName : dataMap.get(baseKey).keySet() ) {
        		for(GenericDataVO dvo : dataMap.get(baseKey).get(mName) ) {
        			System.out.println( baseKey + ", "+ mName + " = " + dvo.getStrVal1()+" and "+dvo.getStrVal2());
        		}
        	}
        }
        */
        /*
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            DataElement de1 = dataElementService.getDataElement( 4 );            
            DataValue dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
            {
                countryGeneralInfoMap.put( orgUnit.getId()+":4", "" );
            }
            else
            {
                countryGeneralInfoMap.put( orgUnit.getId()+":4", dv.getValue() );
            }

            de1 = dataElementService.getDataElement( 3 );
            dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
            {
                countryGeneralInfoMap.put( orgUnit.getId()+":3", "" );
            }
            else
            {
                countryGeneralInfoMap.put( orgUnit.getId()+":3", dv.getValue() );
            }
        }
        */
        
        /*
        List<Integer> orgunitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );
        String orgUnitIdsByComma = "-1";
        if ( orgunitIds.size() > 0 )
        {
            orgUnitIdsByComma = getCommaDelimitedString( orgunitIds );
        }
        headerDataValueMap = ivbUtil.getLatestDataValuesForTabularReport( headerDataElementIdsByComma, orgUnitIdsByComma );
        */
        
        Date curDate = new Date();
        curDateStr = format.formatDate( curDate );

        
        userName = currentUserService.getCurrentUser().getUsername();
        if ( i18nService.getCurrentLocale() == null )
        {
            language = "en";
        }
        else
        {
            language = i18nService.getCurrentLocale().getLanguage();
        }
        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) )
        {
            adminStatus = "Yes";
        }
        else
        {
            adminStatus = "No";
        }
        
        return SUCCESS;
    }

    /**
     * Get Start Date from String date foramt (format could be YYYY / YYYY-Qn /
     * YYYY-MM )
     * 
     * @param dateStr
     * @return
     */
    private Date getStartDateByString( String dateStr )
    {
        
    	String startDate = "";
        String[] startDateParts = dateStr.split( "-" );
        if( dateStr.trim().equalsIgnoreCase("TBD") || startDateParts.length <= 1 )
        {
            return null;
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q1" ) )
        {
            startDate = startDateParts[0] + "-01-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q2" ) )
        {
            startDate = startDateParts[0] + "-04-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q3" ) )
        {
            startDate = startDateParts[0] + "-07-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q4" ) )
        {
            startDate = startDateParts[0] + "-10-01";
        }
        else
        {
            startDate = startDateParts[0] + "-" + startDateParts[1] + "-01";
        }

        Date sDate = format.parseDate( startDate );

        return sDate;
    }

    /**
     * Get End Date from String date foramt (format could be YYYY / YYYY-Qn /
     * YYYY-MM )
     * 
     * @param dateStr
     * @return
     */
    private Date getEndDateByString( String dateStr )
    {
        String endDate = "";
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String[] endDateParts = dateStr.split( "-" );
        if ( endDateParts.length <= 1 )
        {
            endDate = endDateParts[0] + "-12-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q1" ) )
        {
            endDate = endDateParts[0] + "-03-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q2" ) )
        {
            endDate = endDateParts[0] + "-06-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q3" ) )
        {
            endDate = endDateParts[0] + "-09-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q4" ) )
        {
            endDate = endDateParts[0] + "-12-31";
        }
        else
        {
            if ( Integer.parseInt( endDateParts[0] ) % 400 == 0 )
            {
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-"
                    + (monthDays[Integer.parseInt( endDateParts[1] )] + 1);
            }
            else
            {
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-"
                    + (monthDays[Integer.parseInt( endDateParts[1] )]);
            }
        }

        Date eDate = format.parseDate( endDate );

        return eDate;
    }

}
