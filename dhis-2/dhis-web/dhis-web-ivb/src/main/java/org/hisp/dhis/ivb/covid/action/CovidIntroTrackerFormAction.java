package org.hisp.dhis.ivb.covid.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class CovidIntroTrackerFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
    private SelectionTreeManager selectionTreeManager;

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
    private LookupService lookupService;
    
    @Autowired
    private OptionService optionService;
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    private int covidPage = 0;
    public void setCovidPage(int covidPage) {
		this.covidPage = covidPage;
	}
	public int getCovidPage() {
		return covidPage;
	}

	private List<Option> indTypeOptions = new ArrayList<>();
	public List<Option> getIndTypeOptions() {
		return indTypeOptions;
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
    
	public String execute()
    {
        userName = currentUserService.getCurrentUser().getUsername();
        if ( i18nService.getCurrentLocale() == null ){
            language = "en";
        }
        else{
            language = i18nService.getCurrentLocale().getLanguage();
        }
        
        selectionManager.clearSelectedOrganisationUnits();
        Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );

        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) ){
            adminStatus = "Yes";
        }
        else{
            adminStatus = "No";
        }
        
        Lookup lookup = lookupService.getLookupByName( "COVID_INTRO_INDICATOR_TYPE_OPTIONSET_UID" );
        OptionSet itOptionSet = optionService.getOptionSet( lookup.getValue() );
        indTypeOptions.addAll( itOptionSet.getOptions() );
        return SUCCESS;
    }
    
}
