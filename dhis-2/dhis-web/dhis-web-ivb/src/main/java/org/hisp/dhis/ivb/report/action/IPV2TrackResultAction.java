package org.hisp.dhis.ivb.report.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18nService;
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
public class IPV2TrackResultAction implements Action
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
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

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
    
    private List<DataElement> deList = new ArrayList<DataElement>();
    public List<DataElement> getDeList() {
		return deList;
	}

	private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }      

    private List<String> orgUnitIds = new ArrayList<String>();
    public void setOrgUnitIds( List<String> orgUnitIds )
    {
        this.orgUnitIds = orgUnitIds;
    }

    private Map<DataElement, DataSet> deDatasets = new HashMap<DataElement, DataSet>();
    public Map<DataElement, DataSet> getDeDatasets(){
        return deDatasets;
    }


    private String isoCode;
    public String getIsoCode() {
		return isoCode;
	}
	public void setIsoCode(String isoCode){
		this.isoCode = isoCode;
	}

    private String whoRegion;
    public String getWhoRegion(){
		return whoRegion;
	}
	public void setWhoRegion(String whoRegion){
		this.whoRegion = whoRegion;
	}

    private String unicefRegion;
    public String getUnicefRegion(){
		return unicefRegion;
	}
	public void setUnicefRegion(String unicefRegion){
		this.unicefRegion = unicefRegion;
	}

    private String incomeLevel;
    public String getIncomeLevel(){
		return incomeLevel;
	}
	public void setIncomeLevel(String incomeLevel){
		this.incomeLevel = incomeLevel;
	}

    private String gaviEligibleStatus;
    public String getGaviEligibleStatus(){
		return gaviEligibleStatus;
	}
	public void setGaviEligibleStatus(String gaviEligibleStatus){
		this.gaviEligibleStatus = gaviEligibleStatus;
	}

    private String showComment;
    public String getShowComment() {
		return showComment;
	}
	public void setShowComment(String showComment) {
		this.showComment = showComment;
	}

    private OrganisationUnitGroupSet unicefRegionsGroupSet;
    public OrganisationUnitGroupSet getUnicefRegionsGroupSet()
    {
        return unicefRegionsGroupSet;
    }

    private Map<String, DataValue> headerDataValueMap = new HashMap<String, DataValue>();
    public Map<String, DataValue> getHeaderDataValueMap(){
        return headerDataValueMap;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute() throws Exception
    {
        //Selected addl columns for orgunit info
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
        

        userName = currentUserService.getCurrentUser().getUsername();

        if ( i18nService.getCurrentLocale() == null )
        {
            language = "en";
        }
        else
        {
            language = i18nService.getCurrentLocale().getLanguage();
        }
        
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
        
        if ( orgUnitIds.size() > 1 )
        {
            for ( String id : orgUnitIds )
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( id ) );
                if ( orgUnit.getHierarchyLevel() == 3 )
                {
                    orgUnitList.add( orgUnit );
                }
            }
        }
        else if ( selectionTreeManager.getReloadedSelectedOrganisationUnits() != null )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
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
        
        Lookup lookup = lookupService.getLookupByName( "IPV2_TRACK_PARAMS" );
        String report_params = lookup.getValue();
        if( report_params == null || report_params.trim().isEmpty() )
        	report_params = "-1";
        String dataElementIdsByComma = report_params;
        if( report_params != null && !report_params.trim().isEmpty() && !report_params.equals("-1") )
        for( String deIdStr : report_params.split(",") ) {
        	int deId = Integer.parseInt( deIdStr );
        	DataElement de = dataElementService.getDataElement( deId );
        	for( DataSet ds : de.getDataSets() ){
                if( ds.getSources() != null && ds.getSources().size() >= 0 )
                    deDatasets.put( de, ds );
            }
        	deList.add( dataElementService.getDataElement( deId ) );
        }

        String orgUnitIdsByComma = "-1";
        if ( orgUnitList.size() > 0 )
        {
            Collection<Integer> organisationUnitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }

        dataValueMap = ivbUtil.getLatestDataValuesForTabularReport( dataElementIdsByComma, orgUnitIdsByComma );
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        unicefRegionsGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) );

        Constant tabularDataElementGroupId = constantService.getConstantByName( "TABULAR_REPORT_DATAELEMENTGROUP_ID" );        
        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByGroupId( (int) tabularDataElementGroupId.getValue() ) );
        List<Integer >headerDataElementIds = new ArrayList<Integer>( getIdentifiers( dataElements ) );
        String headerDataElementIdsByComma = "-1";
        if ( headerDataElementIds.size() > 0 )
            headerDataElementIdsByComma = getCommaDelimitedString( headerDataElementIds );

        headerDataValueMap = ivbUtil.getLatestDataValuesForTabularReport( headerDataElementIdsByComma, orgUnitIdsByComma );


        return SUCCESS;
    }
}
