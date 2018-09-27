package org.hisp.dhis.ivb.isc.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.ivb.isc.ISCReportHelper;
import org.hisp.dhis.ivb.isc.ISCSnapshot;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ISCReportMcountriesJSONAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
	private CurrentUserService currentUserService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private SelectionTreeManager selectionTreeManager;
    
    @Autowired
	private ISCReportHelper iscReportHelper;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String deIds;
	public void setDeIds(String deIds) {
		this.deIds = deIds;
	}

    private InputStream inputStream;
    public InputStream getInputStream(){
        return inputStream;
    }

    private String fileName;
    public String getFileName(){
        return fileName;
    }
    
    private String dataJSON;    
    public String getDataJSON(){
		return dataJSON;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute() throws Exception
    {
		List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
		
		if ( selectionTreeManager.getReloadedSelectedOrganisationUnits() != null )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
            List<OrganisationUnit> lastLevelOrgUnit = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> userOrgUnits = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
            for ( OrganisationUnit orgUnit : userOrgUnits ){
                if ( orgUnit.getHierarchyLevel() == 3 )
                    lastLevelOrgUnit.add( orgUnit );
                else
                    lastLevelOrgUnit.addAll( organisationUnitService.getOrganisationUnitsAtLevel( 3, orgUnit ) );
            }
            orgUnitList.retainAll( lastLevelOrgUnit );
        }
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>( getIdentifiers( orgUnitList ) );

        String ouIdsByComma = "-1";
        if ( orgUnitList.size() > 0 ){
        	ouIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        List<GenericTypeObj> ouList = new ArrayList<GenericTypeObj>();
        for( OrganisationUnit ou : orgUnitList ) {
        	GenericTypeObj ouObj = new GenericTypeObj();
        	ouObj.setId( ou.getId() );
        	ouObj.setName( ou.getName() );
        	ouObj.setShortName( ou.getShortName() );
        	ouObj.setCode( ou.getCode() );
        	
        	ouList.add( ouObj );
        }
        
		System.out.println( "ouIdsByComma = "+ ouIdsByComma );
        
		if( deIds == null || deIds.trim().equals("") )	
			deIds = "-1";
		
		Map<String, GenericDataVO> latestDataValues = iscReportHelper.getLatestDataValues( deIds, ouIdsByComma );	
		
		ISCSnapshot iscSnapshot = new ISCSnapshot();
		iscSnapshot.setOrgUnits( ouList );
		iscSnapshot.setDvMap( latestDataValues );
		
        JSONObject jsonResp = new JSONObject();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		dataJSON = ow.writeValueAsString( iscSnapshot );
		jsonResp.put("result", new JSONObject(dataJSON) );
		
        return SUCCESS;
    }      
}
