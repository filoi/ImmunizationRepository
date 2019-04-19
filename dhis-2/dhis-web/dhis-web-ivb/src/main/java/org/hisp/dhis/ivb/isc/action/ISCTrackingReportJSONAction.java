package org.hisp.dhis.ivb.isc.action;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.ivb.EPIProfileSnapshot;
import org.hisp.dhis.ivb.util.EPIProfileHelper;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.ivb.util.RegionalDashboardHelper;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ISCTrackingReportJSONAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    private IVBUtil ivbUtil;
    
    public void setIvbUtil( IVBUtil ivbUtil )
    {
        this.ivbUtil = ivbUtil;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private DocumentService documentService;
    
    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }
    
    @Autowired 
    EPIProfileHelper epiProfileHelper;
    
    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private DataElementService dataElementService;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String iscDeIds;
    public void setIscDeIds(String iscDeIds) {
		this.iscDeIds = iscDeIds;
	}

    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String dataJSON;
    
    public String getDataJSON() {
		return dataJSON;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute() throws Exception
    {
		//System.out.println( "orgUnitId: "+ orgUnitId );
		Calendar curCal = Calendar.getInstance();
		OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		EPIProfileSnapshot epiProfileSnapshot = new EPIProfileSnapshot();
		epiProfileSnapshot.setCurYear( curCal.get( Calendar.YEAR ) );
		epiProfileSnapshot.setFlagName( orgUnit.getCode() +".png" );
		epiProfileSnapshot.setCountryName( orgUnit.getName() );
		epiProfileSnapshot.setCurDate( sdf.format( curCal.getTime() ) );
		
		epiProfileSnapshot.setEvmDataMap( epiProfileHelper.getLatestDataValues( iscDeIds, orgUnit.getId(), true ) );

        JSONObject jsonResp = new JSONObject();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		dataJSON = ow.writeValueAsString(epiProfileSnapshot);
		jsonResp.put("result", new JSONObject(dataJSON) );

        return SUCCESS;
    }      
}
