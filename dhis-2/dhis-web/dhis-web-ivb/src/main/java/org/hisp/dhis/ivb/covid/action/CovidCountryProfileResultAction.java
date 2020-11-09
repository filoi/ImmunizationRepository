package org.hisp.dhis.ivb.covid.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.util.CovidIntroHelper;
import org.hisp.dhis.ivb.util.CovidIntroSnapshot;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
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
public class CovidCountryProfileResultAction implements Action
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

    @Autowired
    private OptionService optionService;
    
    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------
    private String orgUnitUID;
	public void setOrgUnitUID(String orgUnitUID) {
		this.orgUnitUID = orgUnitUID;
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
       

        Lookup lookup = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_REPORT_FLAG_ATTRIBTE_ID );
        Integer flagAttributeId = Integer.parseInt( lookup.getValue() );
        covidIntroSnapshot.setFlagAttributeId( flagAttributeId );
        
        lookup = lookupService.getLookupByName( "INDICATOR_TYPE_ATTRIBUTE_ID" );
        Integer itAttributeId = Integer.parseInt( lookup.getValue() );
        covidIntroSnapshot.setIndTypeAttributeId( itAttributeId );
        
        lookup = lookupService.getLookupByName( "GENERAL_DE_GROUP_ID" );
        Integer genDegId = Integer.parseInt( lookup.getValue() );
        covidIntroSnapshot.setGeneralDeGroupId( genDegId );
        
        lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        covidIntroSnapshot.setUnicefRegionsGroupSet(orgUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) ) );
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        OrganisationUnit selOrgUnit = orgUnitService.getOrganisationUnit( orgUnitUID );
        orgUnitList.add( selOrgUnit );
        covidIntroSnapshot.setSelOrgUnit( selOrgUnit );
        covidIntroSnapshot.setSelOrgUnits( orgUnitList );
        String ouIdsByComma = "-1";
        for( OrganisationUnit ou : covidIntroSnapshot.getSelOrgUnits() ) {
			ouIdsByComma += "," + ou.getId();
        }
        covidIntroSnapshot.setOuIdsByComma( ouIdsByComma );

        lookup = lookupService.getLookupByName( "COVID_INTRO_INDICATOR_TYPE_OPTIONSET_UID" );
        OptionSet itOptionSet = optionService.getOptionSet( lookup.getValue() );
        String indTypesByComma = "'-1'";
        for( Option indTypeOption : itOptionSet.getOptions() ){
        	covidIntroSnapshot.getIndTypes().add( indTypeOption.getCode() );
        	indTypesByComma += ",'" + indTypeOption.getCode() +"'";
        }
        covidIntroSnapshot.setIndTypesByComma( indTypesByComma );
        
        covidIntroSnapshot = covidIntroHelper.getCovidIntroSnapshot( covidIntroSnapshot, 2 );
        
      
        return SUCCESS;
    }
}
