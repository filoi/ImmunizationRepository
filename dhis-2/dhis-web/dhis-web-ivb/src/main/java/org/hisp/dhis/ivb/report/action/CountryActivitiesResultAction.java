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

import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class CountryActivitiesResultAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private LookupService lookupService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private I18nService i18nService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    @Autowired
    private IVBUtil ivbUtil;
        
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private String includeTabularOutput;

    public String getIncludeTabularOutput()
    {
        return includeTabularOutput;
    }

    public void setIncludeTabularOutput( String includeTabularOutput )
    {
        this.includeTabularOutput = includeTabularOutput;
    }

    private Integer nextXmonths;

    public Integer getNextXmonths()
    {
        return nextXmonths;
    }

    public void setNextXmonths( Integer nextXmonths )
    {
        this.nextXmonths = nextXmonths;
    }

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    private List<Integer> selectedListDe = new ArrayList<Integer>();

    public void setSelectedListDe( List<Integer> selectedListDe )
    {
        this.selectedListDe = selectedListDe;
    }

    public OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
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

    private SimpleDateFormat simpleDateFormat1;

    public SimpleDateFormat getSimpleDateFormat1()
    {
        return simpleDateFormat1;
    }

    private SimpleDateFormat simpleDateFormat2;

    public SimpleDateFormat getSimpleDateFormat2()
    {
        return simpleDateFormat2;
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

    private List<Integer> treeSelectedId = new ArrayList<Integer>();

    public void setTreeSelectedId( List<Integer> treeSelectedId )
    {
        this.treeSelectedId = treeSelectedId;
    }

    private Map<String, DataValue> dataValueMap = new HashMap<String, DataValue>();

    public Map<String, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<DataElement> chartDes = new ArrayList<DataElement>();

    public List<DataElement> getChartDes()
    {
        return chartDes;
    }

    private List<String> next12Months = new ArrayList<String>();

    public List<String> getNext12Months()
    {
        return next12Months;
    }

    private Map<DataElement, String> deMonths = new HashMap<DataElement, String>();

    public Map<DataElement, String> getDeMonths()
    {
        return deMonths;
    }

    private Map<DataElement, DataSet> deDatasets = new HashMap<DataElement, DataSet>();
    public Map<DataElement, DataSet> getDeDatasets(){
        return deDatasets;
    }
    
    private List<String> subHeaders = new ArrayList<String>();
    public List<String> getSubHeaders() {
		return subHeaders;
	}

	private Map<String, List<DataElement>> deMap = new HashMap<String, List<DataElement>>();
    public Map<String, List<DataElement>> getDeMap() {
		return deMap;
	}

    private Map<String, Integer> countryADB_ResultMap = new HashMap<String, Integer>();
	public Map<String, Integer> getCountryADB_ResultMap() {
		return countryADB_ResultMap;
	}
	
	private Map<String, String> countryADB_monthNamesMap = new HashMap<String, String>();
	public Map<String, String> getCountryADB_monthNamesMap() {
		return countryADB_monthNamesMap;
	}
	
	private List<String> nextXMonthList = new ArrayList<String>();
	public List<String> getNextXMonthList() {
		return nextXMonthList;
	}

	// ---------------------------------------------------------------------------------------------
    // Action implementation
    // ----------------------------------------------------------------------------------------------
    public String execute() throws Exception
    {
        simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        User curUser = currentUserService.getCurrentUser();
        Set<DataElement> userDes = new HashSet<DataElement>();
        List<UserAuthorityGroup> userAuthorityGroups = new ArrayList<UserAuthorityGroup>( curUser.getUserCredentials().getUserAuthorityGroups() );
        for ( UserAuthorityGroup userAuthorityGroup : userAuthorityGroups ){
            userDes.addAll( userAuthorityGroup.getDataElements() );
           // System.out.println("userAuthorityGroups"+userAuthorityGroup.getDataElements());
        }
        
        userName = curUser.getUsername();
        if ( i18nService.getCurrentLocale() == null )
        {
            language = "en";
        }
        else
        {
            language = i18nService.getCurrentLocale().getLanguage();
        }

        if ( treeSelectedId.size() > 0 ) {
            organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }
        else if ( selectionTreeManager.getReloadedSelectedOrganisationUnits() != null ){
            organisationUnit = selectionTreeManager.getSelectedOrganisationUnit();
        }
        else{
            organisationUnit = new OrganisationUnit();
        }

        if ( includeTabularOutput == null ){
            includeTabularOutput = "no";
        }
        else{
            includeTabularOutput = "yes";
        }

        /*
        
        Date sDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime( sDate );
        for ( int i = 1; i <= nextXmonths; i++ )
        {
            next12Months.add( monthFormat.format( cal.getTime() ) );
            // cal.roll(Calendar.MONTH, 1 );
            cal.add( Calendar.MONTH, 1 );
        }
        cal.add( Calendar.MONTH, -1 );
        Date eDate = cal.getTime();
        */
        
        
        Map<String, String> tempMonthMap = new HashMap<String, String>();
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 1);
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        Date sDate = simpleDateFormat1.parse(simpleDateFormat1.format( cal.getTime()));
        for( int i = 1; i <= nextXmonths; i++ ){
        	nextXMonthList.add( monthFormat.format( cal.getTime() ) );
        	//tempMonthMap.put( simpleDateFormat1.format( cal.getTime() ), "M"+(i) );
            cal.add( Calendar.MONTH, 1 );
        }
        cal.add( Calendar.MONTH, -1 );
        Date eDate = simpleDateFormat1.parse(simpleDateFormat1.format( cal.getTime()));;


        Set<DataElement> hiddenDes = new HashSet<DataElement>( ivbUtil.getHiddenDataElementList( organisationUnit.getUid() ) );

        String dataElementIdsByComma = "-1";

        if ( selectedListDe.size() > 0 )
        {
            dataElementIdsByComma = getCommaDelimitedString( selectedListDe );
        }
        dataValueMap = ivbUtil.getLatestDataValuesForTabularReport( dataElementIdsByComma, organisationUnit.getId()+"" );

        Lookup deListLookup = lookupService.getLookupByName( "COUNTRY_ACTIVITIES_DE_LIST" );
        for( String oneSet : deListLookup.getValue().split(RegionalDashboardHelper.MAIN_SEPERATOR)) {
        	String headerName = oneSet.split(":")[0];
        	subHeaders.add( headerName );
        	List<DataElement> des = new ArrayList<DataElement>();
        	for( String deIdStr : oneSet.split(":")[1].split(",") ) {
        		Integer deId = Integer.parseInt( deIdStr );
        		if( selectedListDe.contains( deId ) ) {
        			DataValue dv = dataValueMap.get( organisationUnit.getId() + ":" + deIdStr );
        			DataElement de = dataElementService.getDataElement( deId );
        			des.add( de );
        			for( DataSet ds : de.getDataSets() ){
                        if( ds.getSources() != null && ds.getSources().size() >= 0 )
                            deDatasets.put( de, ds );
                    }
        			
        			if( dv != null ){
        				dataElements.add( de );
                    	String dateValue = "";
                        try{
                        	Integer month = 0;
                            String[] dateParts = dv.getValue().split( "-" );
                            if( dateParts.length == 1 ){
                                dateValue = dateParts[0] + "-01";
                                month = 1;
                            }
                            else{
                            	if ( dateParts[1].contains( "Q1" ) )
                                    dateParts[1] = "03";
                            	else if ( dateParts[1].contains( "Q2" ) )
                                    dateParts[1] = "06";
                            	else if ( dateParts[1].contains( "Q3" ) )
                                    dateParts[1] = "09";
                            	else if ( dateParts[1].contains( "Q4" ) )
                                    dateParts[1] = "12";
                                dateValue = dateParts[0] + "-" + dateParts[1];
                                month = Integer.parseInt( dateParts[1] );
                            }
                            dateValue += "-01";
                            Date d = simpleDateFormat1.parse( dateValue );
                        
                            //sDate=d;
                            //System.out.println( de.getName() +":  "+ sDate.getTime() + " - " + d.getTime() + " - " + eDate.getTime() );
                            //System.out.println( sDate + " - " + d + " - " + eDate);
                            if ( d != null && sDate.getTime() <= d.getTime() && d.getTime() <= eDate.getTime() )
                            	countryADB_ResultMap.put( de.getId()+":"+monthFormat.format( d ), 1);
                        }
                        catch ( Exception e )
                        {
                        }
                    }
        		}        		
        	}// de for loop end
        	Collections.sort( des, new IdentifiableObjectNameComparator() );
        	deMap.put( headerName, des );
        }//set for loop end
        //Collections.sort( subHeaders );
        
        
        /*
        for ( Integer deId : selectedListDe )
        {
            DataValue dv = dataValueMap.get( organisationUnit.getId() + ":" + deId );
            if ( dv != null )
            {
                String dateValue = "";
                try
                {
                    Integer month = 0;
                    String[] dateParts = dv.getValue().split( "-" );

                    if ( dateParts.length == 1 )
                    {
                        dateValue = dateParts[0] + "-01";
                        month = 1;
                    }
                    else
                    {
                        if ( dateParts[1].contains( "Q1" ) )
                        {
                            dateParts[1] = "03";

                        }

                        if ( dateParts[1].contains( "Q2" ) )
                        {
                            dateParts[1] = "06";

                        }
                        if ( dateParts[1].contains( "Q3" ) )
                        {
                            dateParts[1] = "09";

                        }
                        if ( dateParts[1].contains( "Q4" ) )
                        {
                            dateParts[1] = "12";

                        }
                        dateValue = dateParts[0] + "-" + dateParts[1];
                        month = Integer.parseInt( dateParts[1] );
                    }

                    dateValue += "-01";
                    Date d = simpleDateFormat1.parse( dateValue );

                    DataElement de = dataElementService.getDataElement( deId );
                    

                    dataElements.add( de );

                    for ( DataSet ds : de.getDataSets() )
                    {
                        if ( ds.getSources() != null && ds.getSources().size() >= 0 )
                        {
                            deDatasets.put( de, ds );
                        }
                    }
                
                    sDate=d;
                    if ( d != null && sDate.getTime() <= d.getTime() && d.getTime() <= eDate.getTime() )
                    {
                        chartDes.add( de );
                        deMonths.put( de, monthFormat.format( d ) );
                        
                    }

                }
                catch ( Exception e )
                {

                }
            }
        }
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
