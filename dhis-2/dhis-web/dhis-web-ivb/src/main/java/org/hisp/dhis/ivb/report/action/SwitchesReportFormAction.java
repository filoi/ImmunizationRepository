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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Bharath
 */
public class SwitchesReportFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private SelectionTreeManager selectionTreeManager;
    
    @Autowired
    private SectionService dsSectionService;
    
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
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    private List<Section> vaccines = new ArrayList<>();
    public List<Section> getVaccines() {
		return vaccines;
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
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        userName = currentUserService.getCurrentUser().getUsername();

        if ( i18nService.getCurrentLocale() == null )
            language = "en";
        else
            language = i18nService.getCurrentLocale().getLanguage();
        
        Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );

        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) )
            adminStatus = "Yes";
        else
            adminStatus = "No";
        
        Lookup lookup = lookupService.getLookupByName( "SWITCH_VACCINE_DEIDS" );
        String reportColInfo = lookup.getValue();
        for( String colInfo : reportColInfo.split("@!@") ) {
        	int sectionId = Integer.parseInt( colInfo.split("@-@")[0] );
        	vaccines.add( dsSectionService.getSection(sectionId) );
        }
        
        return SUCCESS;
    }
}