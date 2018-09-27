package org.hisp.dhis.ivb.report.action;

import java.util.ArrayList;
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
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.ivb.util.RegionalDBSnapshot;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;


/**
 * @author BHARATH
 */
public class RegionalDashboardReportResultction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    private RegionalDashboardHelper regionalDBHelper;

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    private String language;

    private String userName;

    public String getLanguage()
    {
        return language;
    }

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

    private Map<String, DataValue> dataValueMap = new HashMap<String, DataValue>();

    public Map<String, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }      
    
    private List<String> orgUnits;
	public void setOrgUnits(List<String> orgUnits) {
		this.orgUnits = orgUnits;
	}

	private List<String> dataSets;
	public void setDataSets(List<String> dataSets) {
		this.dataSets = dataSets;
	}

	private RegionalDBSnapshot regionalDBSnapshot = new RegionalDBSnapshot();
	public RegionalDBSnapshot getRegionalDBSnapshot() {
		return regionalDBSnapshot;
	}

	private String includeComment;
	public String getIncludeComment() {
		return includeComment;
	}
	public void setIncludeComment(String includeComment) {
		this.includeComment = includeComment;
	}

    private Integer regionalDBCount = 0;
    public Integer getRegionalDBCount() {
		return regionalDBCount;
	}
	public void setRegionalDBCount(Integer regionalDBCount) {
		this.regionalDBCount = regionalDBCount;
	}

	// --------------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------------
    public String execute()
    {
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
       
        if( includeComment == null )
        	includeComment = "NO";
        else
        	includeComment = "YES";
        
        regionalDBSnapshot.setRegionalDBCount( regionalDBCount );
        
        Lookup lookup = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_REPORT_FLAG_ATTRIBTE_ID );
        Integer flagAttributeId = Integer.parseInt( lookup.getValue() );
        regionalDBSnapshot.setFlagAttributeId( flagAttributeId );
        
        List<OrganisationUnit> selOrgUnits = new ArrayList<OrganisationUnit>();
        selOrgUnits = orgUnitService.getOrganisationUnitsByUid( orgUnits );
        Collections.sort( selOrgUnits, new IdentifiableObjectCodeComparator() );
        regionalDBSnapshot.setSelOrgUnits( selOrgUnits );
        String ouIdsByComma = "-1";
        for( OrganisationUnit ou : regionalDBSnapshot.getSelOrgUnits() ) {
			ouIdsByComma += "," + ou.getId();
        }
        regionalDBSnapshot.setOuIdsByComma( ouIdsByComma );
        
        List<DataSet> selDataSets = dataSetService.getDataSetsByUid( dataSets );
        //Collections.sort( selDataSets, new IdentifiableObjectNameComparator() );
        Collections.sort( selDataSets, new IdentifiableObjectCodeComparator() );
        regionalDBSnapshot.setSelDataSets( selDataSets );
        String dsIdsByComma = "-1";
        for( DataSet ds : regionalDBSnapshot.getSelDataSets() ) {
        	dsIdsByComma += "," + ds.getId();
        }
        regionalDBSnapshot.setDsIdsByComma( dsIdsByComma );
        
        regionalDBSnapshot = regionalDBHelper.getRegionalDashboardSnapShot( regionalDBSnapshot );
        
        /*Lookup lookup = lookupService.getLookupByName( Lookup.BWA_ACTIVITY_REPORT_PARAMS );
        String bwa_activity_report_params = lookup.getValue();
        
        String rootOrgUnitUId = bwa_activity_report_params.split( "-" )[0];

        if ( rootOrgUnitUId != null )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( rootOrgUnitUId ) );
            
            List<OrganisationUnit> lastLevelOrgUnit = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> userOrgUnits = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
            for ( OrganisationUnit orgUnit : userOrgUnits )
            {
                if ( orgUnit.getHierarchyLevel() == 3 )
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
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );

        String orgUnitIdsByComma = "-1";
        if ( orgUnitList.size() > 0 )
        {
            orgUnitIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        
        String dataElementIdsByComma = bwa_activity_report_params.split( "-" )[1];
        
        dataValueMap = ivbUtil.getLatestDataValuesForTabularReport( dataElementIdsByComma, orgUnitIdsByComma );*/
        
        return SUCCESS;
    }
}
