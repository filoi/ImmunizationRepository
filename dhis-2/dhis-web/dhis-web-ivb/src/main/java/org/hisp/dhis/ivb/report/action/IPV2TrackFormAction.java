package org.hisp.dhis.ivb.report.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.favorite.Favorite;
import org.hisp.dhis.favorite.FavoriteService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.util.IVBUtil;
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
public class IPV2TrackFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	@Autowired
	private SelectionTreeManager selectionTreeManager;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private I18nService i18nService;

	@Autowired
    private MessageService messageService;

	@Autowired
	private ConfigurationService configurationService;

    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------    
    private Collection<OrganisationUnit> selectedUnits = new HashSet<OrganisationUnit>();
    public Collection<OrganisationUnit> getSelectedUnits()
    {
        return selectedUnits;
    }
    

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

    
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    public String execute()
    {

        userName = currentUserService.getCurrentUser().getUsername();

        if ( i18nService.getCurrentLocale() == null )
        {
            language = "en";
        }
        else
        {
            language = i18nService.getCurrentLocale().getLanguage();
        }

        
        //Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        //selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );        
        //selectionTreeManager.clearSelectedOrganisationUnits();

        /*
        Lookup lookup = lookupService.getLookupByName( Lookup.MCV2_INTRO_PIE_OUGROUP );
        String mcv2OuGroupsStr = lookup.getValue();
        for( String strOrgunitGroupId : mcv2OuGroupsStr.split( ":" ) ){
            selectedUnits.addAll( orgUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( strOrgunitGroupId ) ).getMembers() );
        }
        */
        
        Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );
        selectionTreeManager.clearSelectedOrganisationUnits();
        //selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        /*
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        for ( OrganisationUnit org : orgUnitList )
        {
            for ( OrganisationUnit o : ivbUtil.getLeafOrganisationUnits( org.getId() ) )
            {
                if ( !(countryList.contains( "\"" + o.getUid() + "\"" )) )
                {
                    countryList.add( "\"" + o.getUid() + "\"" );
                }
            }
        }
        */

        /*
        datasetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
       
        if(favoriteService.getFavoriteByName( "PROWG REPORT" ) != null)
        {
            prowgReport = favoriteService.getFavoriteByName( "PROWG REPORT" );
        }
        
        Iterator<DataSet> dataSetIterator = datasetList.iterator();
        while ( dataSetIterator.hasNext() )
        {
            DataSet dataSet = dataSetIterator.next();
            if ( dataSet.getSources() != null && dataSet.getSources().size() > 0 )
            {
                dataSetIterator.remove();
            }
        }
        Collections.sort( datasetList, new IdentifiableObjectNameComparator() );
		*/

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
