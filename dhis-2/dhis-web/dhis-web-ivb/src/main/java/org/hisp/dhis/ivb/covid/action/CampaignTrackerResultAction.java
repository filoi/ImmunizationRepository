package org.hisp.dhis.ivb.covid.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
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
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class CampaignTrackerResultAction
    implements Action
{

    private static final String INTRO_YEAR_DE_GROUP = "INTRO_YEAR_DE_GROUP";
    private static final String TABULAR_REPORT_DATAELEMENTGROUP_ID = "TABULAR_REPORT_DATAELEMENTGROUP_ID";
    private static final String VACCINE_ATTRIBUTE = "VACCINE_ATTRIBUTE"; 

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
    private ProgramService programService;
    
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
    private Map<String, List<CampaignVO>> dataMap;
	public Map<String, List<CampaignVO>> getDataMap() {
		return dataMap;
	}

	private List<GenericTypeObj> colList; 
	public List<GenericTypeObj> getColList() {
		return colList;
	}
	
	private NumberFormat nf;
	public NumberFormat getNf() {
		return nf;
	}

	private List<String> subNatNames;
    public List<String> getSubNatNames() {
		return subNatNames;
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
        //Selected Campaigns 
        dataSetSections = new ArrayList<Section>();
        Set<String> sectionCodes = new HashSet<>();
        for( Integer sectionId : selectedListVaccine )
        {
            Section section = sectionService.getSection( sectionId );
            dataSetSections.add( section );
            
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
        
        //Selected Columns
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
        
        
        //System.out.println(deIdsByComma);
        Map<String, GenericDataVO> dvDataMap = iscReportHelper.getLatestDataValues( deIdsByComma, ouIdsByComma );
        Map<String, Map<Integer, CampaignVO>> eventDataMap = iscReportHelper.getEventData( psIdsByComma, psDeIdsByComma, ouIdsByComma );
        //System.out.println( dvDataMap.size() +" and "+eventDataMap.size() );
        
        nf = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = new DecimalFormat( "#,###,###,##0" );
        
        //Arranging Aggregated Data
        Set<Integer> deIds = deColMap.keySet();
        dataMap = new HashMap<>();
        for( int ouId : organisationUnitIds ) {
        	String key1 = ouId+"_National";
        	dataMap.put(key1, new ArrayList<>());
        	for(Section section : dataSetSections ) {
        		int flag = 0;
        		Map<String, GenericDataVO> vacDataMap = null;
        		
        		//String key = ouId+"_"+section.getId();
        		for( DataElement de : section.getDataElements()) {
        			if( deIds.contains( de.getId() ) ){
	        			String dvKey = ouId+"_"+de.getId();
	        			String colCode = "NONE";
	        			colCode = deColMap.get(de.getId());
	        			if( dvDataMap.get(dvKey)!= null ) {
	        				if( vacDataMap == null )
	        					vacDataMap = new HashMap<>();
	        				flag = 1;
	        				vacDataMap.put(colCode, dvDataMap.get(dvKey));
	        				if( colCode.trim().equals("COL_4") )
	        					vacDataMap.put("COL_3", dvDataMap.get(dvKey));
	        				else if( colCode.trim().equals("COL_8") ) {
	        					String val = "";
	        					try{ val = dvDataMap.get(dvKey).getStrVal1();}catch(Exception e) {}
	        					try{ val = df.format(Double.parseDouble(val));}catch(Exception e) {}
	        					if( !val.trim().equals("") )
	        						dvDataMap.get(dvKey).setStrVal1( val );
	        					vacDataMap.put(colCode, dvDataMap.get(dvKey));
	        				}
							//System.out.println(key + " + " + colCode +" = " +dvDataMap.get(key2).getStrVal1());
	        			}
        			}
        		}
        		
        		if( flag == 1) {
        			CampaignVO cvo = new CampaignVO();
        			GenericDataVO dvo = new GenericDataVO();
            		dvo.setStrVal1(section.getCode() );
            		vacDataMap.put("COL_0", dvo);
            		cvo.setColDataMap( vacDataMap );
            		dataMap.get(key1).add( cvo );
        		}
        	}
        }
        
        Set<String> subNationalNames = new HashSet<>();
        subNationalNames.add("National");
        
        //Arranging Event Data
        for( int ouId : organisationUnitIds ) {
        	for(ProgramStage ps : programStages ) {
        		String eBaseKey = ps.getId()+"_"+ouId;
        		if( eventDataMap.get(eBaseKey) == null ) {
        			//System.out.println("Data Unavailable for the key "+ eBaseKey);
        			continue;
        		}
        		else {
        			//System.out.println("Data available for the key "+ eBaseKey);
        		}
        		for(Integer psInsId : eventDataMap.get(eBaseKey).keySet()) {
        			String subNationName = "National";
        			try { subNationName = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(subNationalDeId+"").getStrVal1();}catch(Exception e) {}
        			int flag = 0;
            		String key1 = ouId+"_"+subNationName;
            		//System.out.println( key1 );
            		if( dataMap.get(key1) == null)
            			dataMap.put(key1, new ArrayList<>());
            		Map<String, GenericDataVO> vacDataMap = null;
            		for( DataElement de : ps.getAllDataElements()) {
            			if( deIds.contains( de.getId() ) ){
    	        			String colCode = "NONE";
    	        			colCode = deColMap.get(de.getId());
    	        			GenericDataVO dvo = null;
    	        			try { dvo = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(de.getId()+"");}catch(Exception e) {}
    	        			if( dvo != null ) {
    	        				if( vacDataMap == null )
    	        					vacDataMap = new HashMap<>();
    	        				flag = 1;
    	        				if( colCode.trim().equals("COL_4") )
    	        					dvo.setStrVal2(dvo.getStrVal1());
    	        				else if( colCode.trim().equals("COL_8") ) {
    	        					String val = "";
    	        					try{ val = dvo.getStrVal1();}catch(Exception e) {}
    	        					try{ val = df.format(Double.parseDouble(val));}catch(Exception e) {}
    	        					if( !val.trim().equals("") )
    	        						dvo.setStrVal1( val );
    	        				}
    	        				vacDataMap.put(colCode, dvo);
    	        				subNationalNames.add(subNationName);
    	        			}
            			}
            		}
            		if( flag == 1) {
            			CampaignVO cvo = new CampaignVO();
            			GenericDataVO dvo = new GenericDataVO();
                		dvo.setStrVal1(ps.getName() );
                		vacDataMap.put("COL_0", dvo);
                		cvo.setColDataMap( vacDataMap );
                		dataMap.get(key1).add( cvo );
                		//System.out.println("Data Row added for PS instanceid : "+ psInsId );
            		}
        		}
        	}
        }		
       
        subNatNames = new ArrayList<>();
        subNatNames.addAll( subNationalNames );
        Collections.sort( subNatNames );
        
		/*
        for( String key1 : dataMap.keySet() ) {
        	for( CampaignVO cvo : dataMap.get(key1)) {
        		for(String key2 : cvo.getColDataMap().keySet() ) {
        			System.out.println( key1 + ", " + key2 + " = " + cvo.getColDataMap().get(key2).getStrVal1() );
        		}
        	}
        }
		*/
       
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
        
        
        /*
        List<Integer> orgunitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );
        String orgUnitIdsByComma = "-1";
        if ( orgunitIds.size() > 0 )
        {
            orgUnitIdsByComma = getCommaDelimitedString( orgunitIds );
        }
        headerDataValueMap = ivbUtil.getLatestDataValuesForTabularReport( headerDataElementIdsByComma, orgUnitIdsByComma );
        */
        
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
        if ( startDateParts.length <= 1 )
        {
            startDate = startDateParts[0] + "-01-01";
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
