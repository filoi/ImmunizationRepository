package org.hisp.dhis.ivb.isc.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.EPIProfileSnapshot;
import org.hisp.dhis.ivb.util.EPIProfileHelper;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ISCTrackingReportFormAction implements Action
{
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private OrganisationUnitService organisationUnitService;
	
	public void setOrganisationUnitService( OrganisationUnitService organisationUnitService ){
	    this.organisationUnitService = organisationUnitService;
	} 

	private SelectionTreeManager selectionTreeManager;
	
	public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager ){
	    this.selectionTreeManager = selectionTreeManager;
	}

	private OrganisationUnitSelectionManager selectionManager;

	public void setSelectionManager( OrganisationUnitSelectionManager selectionManager ){
		this.selectionManager = selectionManager;
	}
	
	private CurrentUserService currentUserService;

	public void setCurrentUserService( CurrentUserService currentUserService ){
		this.currentUserService = currentUserService;
	}

	private I18nService i18nService;

	public void setI18nService( I18nService service ){
	    i18nService = service;
	}
	
	private MessageService messageService;

	public void setMessageService( MessageService messageService ){
		this.messageService = messageService;
	}

	private ConfigurationService configurationService;

	public void setConfigurationService( ConfigurationService configurationService ){
		this.configurationService = configurationService;
	}

	@Autowired
	private OrganisationUnitGroupService orgUnitGroupService;
	
	@Autowired
	private IVBUtil ivbUtil;
	
    @Autowired
    private LookupService lookupService;

	// -------------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------------

	private String language;
	
	public String getLanguage()
	{
	    return language;
	}
	
	private String userName;
	
	public String getUserName()
	{
	    return userName;
	}

	private List<String> countryList = new ArrayList<String>();
	
	public List<String> getCountryList()
	{
	    return countryList;
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

	
	private List<OrganisationUnit> iscOrgUnits = new ArrayList<OrganisationUnit>();
	public List<OrganisationUnit> getIscOrgUnits() {
		return iscOrgUnits;
	}

	private String orgUnitId = "";
    public String getOrgUnitId() {
		return orgUnitId;
	}

	public String execute() throws JsonGenerationException, JsonMappingException, IOException
	{
		System.out.println("Inside ISC Tracking Report Form Action....");
		
		userName = currentUserService.getCurrentUser().getUsername();

	    if( i18nService.getCurrentLocale() == null )
	        language = "en";
	    else
	        language = i18nService.getCurrentLocale().getLanguage();

	    Lookup lookup = lookupService.getLookupByName( "ISC_TRACKING_REPORT_OUGROUP_UID" );
	    OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( lookup.getValue() );
	    if( orgUnitGroup != null ){
	    	iscOrgUnits.addAll( orgUnitGroup.getMembers() );
	    	Collections.sort( iscOrgUnits, new IdentifiableObjectNameComparator() );
	    }
	    
	    selectionTreeManager.clearSelectedOrganisationUnits();
    
	    List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
	    for( OrganisationUnit org : orgUnitList )
	    {
	    	for ( OrganisationUnit o : ivbUtil.getLeafOrganisationUnits( org.getId() ) )
	    	{
	    		if ( !(countryList.contains( "\"" + o.getUid()+ "\"" )) )
	    		{
	    			countryList.add( "\"" + o.getUid() + "\"" );
	    		}
	    	}
	    }
    
	    //lookup = lookupService.getLookupByName( "COUNTRY_EPI_PROFILE_REGION_UIDS" );
	    //Set<String> ouUids = new HashSet<String>( Arrays.asList(lookup.getValue().split( RegionalDashboardHelper.ROOT_SEPERATOR )) );
        
	    //Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( organisationUnitService.getOrganisationUnitsByUid( ouUids ) );	       
	    //selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );
	    Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
	    selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );

	    
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
}
