package org.hisp.dhis.ivb.report.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Bharath
 */
public class RegionalDashboardReportFormAction implements Action
{
	public static final String REGIONAL_DASHBOARD_COUNTRY_OUGROUP = "REGIONAL-DB-COUNTRIES";
	
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

/*    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    } 
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
*/    
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

/*    
    @Autowired
    private IVBUtil ivbUtil;
*/    
    @Autowired
	private OrganisationUnitGroupService orgUnitGroupService;
    
    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private DataSetService dataSetService;

    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    private String language;
    public String getLanguage(){
        return language;
    }

    private String userName;
    public String getUserName(){
        return userName;
    }
    
    private List<DataSet> datasetList = new ArrayList<DataSet>();
    public List<DataSet> getDatasetList(){
        return datasetList;
    }
    
    private List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();
	public List<OrganisationUnit> getOrgUnits() {
		return orgUnits;
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
    
    private Integer regionalDBCount = 0;
    public Integer getRegionalDBCount() {
		return regionalDBCount;
	}
	public void setRegionalDBCount(Integer regionalDBCount) {
		this.regionalDBCount = regionalDBCount;
	}

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
        
        //selectionTreeManager.clearSelectedOrganisationUnits();
        //TODO Provide proper orgunit group name
        //OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroupByCode( REGIONAL_DASHBOARD_COUNTRY_OUGROUP );
        Lookup lookup = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_OUGROUP_UIDS );
        //System.out.println( "OUGroup Lookup: " + lookup.getValue() );
        String orgUnitGroupUid = lookup.getValue().split( RegionalDashboardHelper.ROOT_SEPERATOR )[ regionalDBCount ];
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupUid );
        if( orgUnitGroup != null ){
	    	orgUnits.addAll( orgUnitGroup.getMembers() );
	    	Collections.sort( orgUnits, new IdentifiableObjectNameComparator() );
	    }
	    
	    List<DataSet> regionalDBDataSets = new ArrayList<DataSet>();	    
        lookup = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_DATASET_UIDS );
        //System.out.println( "DataSet Lookup: " + lookup.getValue() );
        String dataSetUidsStr = lookup.getValue().split( RegionalDashboardHelper.ROOT_SEPERATOR )[ regionalDBCount ];
        for( String dsUid : dataSetUidsStr.split(";") ) {
        	regionalDBDataSets.add( dataSetService.getDataSet(dsUid) );
        }

        datasetList = new ArrayList<DataSet>( currentUserService.getCurrentUser().getUserCredentials().getAllDataSets() );
        //datasetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        List<DataSet> datasets = new ArrayList<DataSet>();
        Iterator<DataSet> dataSetIterator = datasetList.iterator();
        while( dataSetIterator.hasNext() ){
            DataSet ds = dataSetIterator.next();
            if( ds.getSources() != null && ds.getSources().size() > 0 ){
                datasets.add( ds );
            }
        }
        datasetList.retainAll( datasets );
        datasetList.retainAll( regionalDBDataSets );
        Collections.sort( datasetList, new IdentifiableObjectNameComparator() );
        //Collections.sort( datasetList, new IdentifiableObjectCodeComparator() );
        
        /*
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        for ( OrganisationUnit org : orgUnitList ){
            for ( OrganisationUnit o : ivbUtil.getLeafOrganisationUnits( org.getId() ) ){
                if ( !(countryList.contains( "\"" + o.getUid()+ "\"" )) ){
                    countryList.add( "\"" + o.getUid() + "\"" );
                }
            }
        }
        */
        
        //Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        //selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );

               
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