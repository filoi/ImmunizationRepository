package org.hisp.dhis.ivb.covid.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectCodeComparator;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.util.CovidIntroHelper;
import org.hisp.dhis.ivb.util.CovidIntroSnapshot;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.ivb.util.RegionalDBSnapshot;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;


/**
 * @author BHARATH
 */
public class CovidIntroTrackerResultction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private SelectionTreeManager selectionTreeManager;
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager ){
        this.selectionTreeManager = selectionTreeManager;
    }
    
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private IVBUtil ivbUtil;
    
    @Autowired
    private I18nService i18nService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private OrganisationUnitService orgUnitService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private CovidIntroHelper covidIntroHelper;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------
    private List<String> indTypes;
    private String isoCode;
    private String whoRegion;
    private String unicefRegion;
    private String incomeLevel;
    private String gaviEligibleStatus;
    private String showIndType;
    private String includeComment;
    private String showNonZero;
    private String showCovaxFacility;
	private String showWBSupport;
    
    
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
	public void setIndTypes(List<String> indTypes) {
		this.indTypes = indTypes;
	}
	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	public void setWhoRegion(String whoRegion) {
		this.whoRegion = whoRegion;
	}
	public void setUnicefRegion(String unicefRegion) {
		this.unicefRegion = unicefRegion;
	}
	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}
	public void setGaviEligibleStatus(String gaviEligibleStatus) {
		this.gaviEligibleStatus = gaviEligibleStatus;
	}
	public void setShowIndType(String showIndType) {
		this.showIndType = showIndType;
	}
	public void setIncludeComment(String includeComment) {
		this.includeComment = includeComment;
	}
    public void setShowNonZero(String showNonZero) {
		this.showNonZero = showNonZero;
	}
    
    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------



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

    private CovidIntroSnapshot covidIntroSnapshot = new CovidIntroSnapshot();
	public CovidIntroSnapshot getCovidIntroSnapshot() {
		return covidIntroSnapshot;
	}
	
	// --------------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------------
    public String execute()
    {
    	//Standard Info which is common for all pages
        userName = currentUserService.getCurrentUser().getUsername();
        if( i18nService.getCurrentLocale() == null ){
            language = "en";
        }
        else{
            language = i18nService.getCurrentLocale().getLanguage();
        }
        
        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) ){
            adminStatus = "Yes";
        }
        else{
            adminStatus = "No";
        }
       
        //Show / Hide Columns
    	if( isoCode != null )
    		covidIntroSnapshot.setIsoCode("ON");
        if( whoRegion != null )
        	covidIntroSnapshot.setWhoRegion("ON");
        if( unicefRegion != null )
        	covidIntroSnapshot.setUnicefRegion("ON");
        if( incomeLevel != null )
        	covidIntroSnapshot.setIncomeLevel("ON");
        if( gaviEligibleStatus != null )
        	covidIntroSnapshot.setGaviEligibleStatus("ON");
        if( includeComment != null )
        	covidIntroSnapshot.setIncludeComment("ON");
        if( showIndType != null )
        	covidIntroSnapshot.setShowIndType("ON");
        if( showNonZero != null )
        	covidIntroSnapshot.setNonZeroCountries("ON");
        if( showCovaxFacility != null )
        	covidIntroSnapshot.setShowCovaxFacility("ON");
        if( showWBSupport != null )
        	covidIntroSnapshot.setShowWBSupport("ON");
       
        
        Lookup lookup = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_REPORT_FLAG_ATTRIBTE_ID );
        Integer flagAttributeId = Integer.parseInt( lookup.getValue() );
        covidIntroSnapshot.setFlagAttributeId( flagAttributeId );
        
        lookup = lookupService.getLookupByName( "INDICATOR_TYPE_ATTRIBUTE_ID" );
        Integer itAttributeId = Integer.parseInt( lookup.getValue() );
        covidIntroSnapshot.setIndTypeAttributeId( itAttributeId );
        
        lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        covidIntroSnapshot.setUnicefRegionsGroupSet(orgUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) ) );
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        if(selectionTreeManager.getReloadedSelectedOrganisationUnits() != null){ 
            orgUnitList =  new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) ;            
            List<OrganisationUnit> lastLevelOrgUnit = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> userOrgUnits = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
            for( OrganisationUnit orgUnit : userOrgUnits ){
                if ( orgUnit.getHierarchyLevel() == 3  )
                    lastLevelOrgUnit.add( orgUnit );
                else
                    lastLevelOrgUnit.addAll( orgUnitService.getOrganisationUnitsAtLevel( 3, orgUnit ) );
            }
            orgUnitList.retainAll( lastLevelOrgUnit );
        }
        Collections.sort(orgUnitList, new IdentifiableObjectCodeComparator() );
        covidIntroSnapshot.setSelOrgUnits( orgUnitList );
        String ouIdsByComma = "-1";
        for( OrganisationUnit ou : covidIntroSnapshot.getSelOrgUnits() ) {
			ouIdsByComma += "," + ou.getId();
        }
        covidIntroSnapshot.setOuIdsByComma( ouIdsByComma );

        covidIntroSnapshot.setIndTypes( indTypes );
        String indTypesByComma = "'-1'";
        for( String indType : indTypes ){
        	indTypesByComma += ",'" + indType +"'";
        }
        covidIntroSnapshot.setIndTypesByComma( indTypesByComma );
        
        covidIntroSnapshot = covidIntroHelper.getCovidIntroSnapshot( covidIntroSnapshot, 1 );
        
      
        return SUCCESS;
    }
}
