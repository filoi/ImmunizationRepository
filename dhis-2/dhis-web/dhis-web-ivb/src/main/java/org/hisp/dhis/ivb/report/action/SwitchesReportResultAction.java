package org.hisp.dhis.ivb.report.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.constant.Constant;
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
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class SwitchesReportResultAction implements Action
{

    private static final String INTRO_YEAR_DE_GROUP = "INTRO_YEAR_DE_GROUP";
    private static final String TABULAR_REPORT_DATAELEMENTGROUP_ID = "TABULAR_REPORT_DATAELEMENTGROUP_ID";
    private static final String VACCINE_ATTRIBUTE = "VACCINE_ATTRIBUTE"; 

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private IVBUtil ivbUtil;
    
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

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    private List<Integer> selVaccineIds;
	public void setSelVaccineIds(List<Integer> selVaccineIds) {
		this.selVaccineIds = selVaccineIds;
	}

	private List<Integer> orgUnitIds = new ArrayList<Integer>();
    public void setOrgUnitIds(List<Integer> orgUnitIds) {
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

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    private List<GenericTypeObj> colList = new ArrayList<>();
    public List<GenericTypeObj> getColList() {
		return colList;
	}
    
    private Map<String, Integer> colDeMap = new HashMap<>();
	public Map<String, Integer> getColDeMap() {
		return colDeMap;
	}

	private List<Section> dsSections = new ArrayList<>();
	public List<Section> getDsSections() {
		return dsSections;
	}

	private List<OrganisationUnit> orgUnitList = new ArrayList<>();
    public List<OrganisationUnit> getOrgUnitList(){
        return orgUnitList;
    }

    private String language;
    public String getLanguage(){
        return language;
    }
    
    private String userName;
    public String getUserName(){
        return userName;
    }

    private int messageCount;
    public int getMessageCount(){
        return messageCount;
    }

    private String adminStatus;
    public String getAdminStatus(){
        return adminStatus;
    }

    private Map<String, String> countryGeneralInfoMap = new HashMap<String, String>();
    public Map<String, String> getCountryGeneralInfoMap(){
        return countryGeneralInfoMap;
    }

    private Map<String, DataValue> headerDataValueMap = new HashMap<String, DataValue>();
    public Map<String, DataValue> getHeaderDataValueMap(){
        return headerDataValueMap;
    }
    
    private Map<String, DataValue> dataMap = new HashMap<>();
	public Map<String, DataValue> getDataMap() {
		return dataMap;
	}
	
	// --------------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------------
    public String execute()
    {
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

        Lookup lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        unicefRegionsGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) );

        Constant tabularDataElementGroupId = constantService.getConstantByName( TABULAR_REPORT_DATAELEMENTGROUP_ID );
        List<DataElement> headerDataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByGroupId( (int) tabularDataElementGroupId.getValue() ) );
        List<Integer >headerDataElementIds = new ArrayList<Integer>( getIdentifiers( headerDataElements ) );
        String headerDataElementIdsByComma = "-1";
        if ( headerDataElementIds.size() > 0 ){
            headerDataElementIdsByComma = getCommaDelimitedString( headerDataElementIds );
        }
        
        userName = currentUserService.getCurrentUser().getUsername();

        if ( i18nService.getCurrentLocale() == null )
            language = "en";
        else
            language = i18nService.getCurrentLocale().getLanguage();

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        if( orgUnitIds.size() > 1 ){
            for(Integer id : orgUnitIds ){
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
        
        
        for( Integer sectionId : selVaccineIds ){
            dsSections.add( sectionService.getSection( sectionId ) );
        }
        //System.out.println( dsSections );
        
        lookup = lookupService.getLookupByName( "SWITCH_COL_HEADINGS" );
        String colHeadingInfo = lookup.getValue();
        for( String colInfo : colHeadingInfo.split("@!@") ) {
        	GenericTypeObj colObj = new GenericTypeObj();
        	colObj.setCode( colInfo.split("@-@")[0] );
        	colObj.setName( colInfo.split("@-@")[1] );
        	colList.add( colObj );
        }   
        
        
        String deIdsByComma = "-1";
        lookup = lookupService.getLookupByName( "SWITCH_VACCINE_DEIDS" );
        String reportColInfo = lookup.getValue();
        for( String colInfo : reportColInfo.split("@!@") ) {
        	int sectionId = Integer.parseInt( colInfo.split("@-@")[0] );
        	//System.out.println( "secionId = "+ sectionId);
        	if( selVaccineIds.contains( sectionId ) ){
        		int colCount = 0;
        		for(String deIdStr : colInfo.split("@-@")[1].split(",") ){
        			colDeMap.put(sectionId+":"+colList.get(colCount++).getCode(), Integer.parseInt(deIdStr));
        			deIdsByComma += ","+ deIdStr;
        		}
        	}
        }    
        
        //System.out.println( colDeMap ); 
        
        for( OrganisationUnit orgUnit : orgUnitList ){
            /**
             * Hardcoded dataelementids need to change this later
             */
            DataElement de1 = dataElementService.getDataElement( 4 );            
            DataValue dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
                countryGeneralInfoMap.put( orgUnit.getId()+":4", "" );
            else
                countryGeneralInfoMap.put( orgUnit.getId()+":4", dv.getValue() );

            de1 = dataElementService.getDataElement( 3 );
            dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
                countryGeneralInfoMap.put( orgUnit.getId()+":3", "" );
            else
                countryGeneralInfoMap.put( orgUnit.getId()+":3", dv.getValue() );
        }
        
        
        List<Integer> orgunitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );
        String orgUnitIdsByComma = "-1";
        if ( orgunitIds.size() > 0 )
        {
            orgUnitIdsByComma = getCommaDelimitedString( orgunitIds );
        }
        headerDataValueMap = ivbUtil.getLatestDataValuesForTabularReport( headerDataElementIdsByComma, orgUnitIdsByComma );
        dataMap = ivbUtil.getLatestDataValuesForTabularReport( deIdsByComma, orgUnitIdsByComma );
        
        //System.out.println( dataMap );
        
        
        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) )
            adminStatus = "Yes";
        else
            adminStatus = "No";
        
        return SUCCESS;
    }
}
