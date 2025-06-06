package org.hisp.dhis.ivb.report.action;

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

public class CountryEPIPrfilePPTAction  implements Action
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
    
    private String evmDeIds;

    public void setEvmDeIds(String evmDeIds) {
		this.evmDeIds = evmDeIds;
	}

	private String epiDeIds;

	public void setEpiDeIds(String epiDeIds) {
		this.epiDeIds = epiDeIds;
	}

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
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
		
		List<String> epiCharts = new ArrayList<String>();
		
		EPIProfileSnapshot epiProfileSnapshot = new EPIProfileSnapshot();
		epiProfileSnapshot.setCurYear( curCal.get( Calendar.YEAR ) );
		epiProfileSnapshot.setFlagName( orgUnit.getCode() +".png" );
		epiProfileSnapshot.setCountryName( orgUnit.getName() );
		epiProfileSnapshot.setCurDate( sdf.format( curCal.getTime() ) );
		
		if( evmDeIds == null || evmDeIds.trim().equals("") )	
			evmDeIds = EPIProfileSnapshot.EVM_DE_IDS;
		
		if( epiDeIds == null || epiDeIds.trim().equals("") )
			epiDeIds = EPIProfileSnapshot.EPI_DE_IDS;
			
		epiProfileSnapshot.setEvmDataMap( epiProfileHelper.getLatestDataValues( evmDeIds, orgUnit.getId(), true ) );
		epiProfileSnapshot.setEpiDataMap( epiProfileHelper.getLatestDataValues( epiDeIds, orgUnit.getId(), false ) );

		System.out.println("***************************************");
		for( Integer deId  : epiProfileSnapshot.getEvmDataMap().keySet() ){
			System.out.println( deId + " : " + epiProfileSnapshot.getEvmDataMap().get( deId ).getStrVal1() + " - " + epiProfileSnapshot.getEvmDataMap().get( deId ).getStrVal2() );
		}
		System.out.println("***************************************");

		System.out.println("***************************************");
		for( Integer deId  : epiProfileSnapshot.getEpiDataMap().keySet() ){
			System.out.println( deId + " : " + epiProfileSnapshot.getEpiDataMap().get( deId ).getStrVal1() + " - " + epiProfileSnapshot.getEpiDataMap().get( deId ).getStrVal2() );
		}
		System.out.println("***************************************");

		for( int i =0; i < EPIProfileSnapshot.EPI_CHART_COUNT; i++ ){
			String docName = "EPI_"+orgUnit.getCode().replaceAll("_", "-")+"_IMG";
        	docName+= ((i+1)<=9)?  "0"+(i+1) : (i+1);
        	Document document = null;
        	List<Document> documents = documentService.getDocumentByName( docName );
        	if( documents !=null && documents.size() > 0 )
        		document = documents.get(0);
        	if( document != null )
        		epiCharts.add(document.getUid());
        	else
        		epiCharts.add("");
		}
		epiProfileSnapshot.setEpiCharts( epiCharts );
		
		
		//*************************Country Activity Dashboard START***************************
        Lookup deGroupLookup = lookupService.getLookupByName( "COUNTRY_ACTIVITIES_DE_GROUP_ID" );
        DataElementGroup countryADB_DeGroup = dataElementService.getDataElementGroup( Integer.parseInt( deGroupLookup.getValue() ) );
        List<DataElement> countryADB_DataElements = new ArrayList<DataElement>( countryADB_DeGroup.getMembers() );
        Collections.sort( countryADB_DataElements, new IdentifiableObjectNameComparator() );
        String countryADB_DeIdsByComma = "-1";
        for( DataElement countryADB_DataElement : countryADB_DataElements ) {
        	countryADB_DeIdsByComma += "," + countryADB_DataElement.getId();
        }
        Map<String, DataValue> countryADB_DvMap = ivbUtil.getLatestDataValuesForTabularReport( countryADB_DeIdsByComma, orgUnit.getId() + "" );
        
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy");
        SimpleDateFormat sdf2 = new SimpleDateFormat( "yyyy-MM-dd" );
        
        //Key is M1, M2, M3, .... 
        //Value is month name as MMM-yy format for eg: Mar-18, Apr-18, May-18 etc 
        Map<String, String> countryADB_monthNamesMap = new HashMap<String, String>();
        Map<String, String> tempMonthMap = new HashMap<String, String>();
        int nextXmonths = 12;
        
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 1);
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        Date sDate = sdf2.parse(sdf2.format( cal.getTime()));
        for( int i = 1; i <= nextXmonths; i++ ){
        	countryADB_monthNamesMap.put( "M"+(i), monthFormat.format( cal.getTime() ) );
        	tempMonthMap.put( sdf2.format( cal.getTime() ), "M"+(i) );
            cal.add( Calendar.MONTH, 1 );
        }
        cal.add( Calendar.MONTH, -1 );
        Date eDate = sdf2.parse(sdf2.format( cal.getTime()));;
        
        List<GenericTypeObj> countryADB_DeObjs = new ArrayList<GenericTypeObj>();
        Map<String, Integer> countryADB_ResultMap = new HashMap<String, Integer>();
        List<String> countryADB_SubHeaders = new ArrayList<String>();
        Map<String, List<GenericTypeObj>> countryADB_Header_DeMap = new HashMap<String, List<GenericTypeObj>>();
        
        Lookup deListLookup = lookupService.getLookupByName( "COUNTRY_ACTIVITIES_DE_LIST" );
        for( String oneSet : deListLookup.getValue().split(RegionalDashboardHelper.MAIN_SEPERATOR)) {
        	String headerName = oneSet.split(":")[0];
        	countryADB_SubHeaders.add( headerName );
        	List<GenericTypeObj> des = new ArrayList<GenericTypeObj>();
        	for( String deIdStr : oneSet.split(":")[1].split(",") ) {
        		Integer deId = Integer.parseInt( deIdStr );
        		DataElement de = dataElementService.getDataElement( deId );
	        	GenericTypeObj deObj = new GenericTypeObj();
	        	deObj.setId( de.getId() );
	        	deObj.setName( de.getDisplayName() );
	        	deObj.setShortName( de.getShortName() );
	        	countryADB_DeObjs.add( deObj );
	        	des.add( deObj );
	        	
	        	DataValue dv = countryADB_DvMap.get( orgUnit.getId() + ":" + de.getId() );
	            if( dv != null ){
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
	                    Date d = sdf2.parse( dateValue );
	                
	                    //sDate=d;
	                    //System.out.println( de.getName() +":  "+ sDate.getTime() + " - " + d.getTime() + " - " + eDate.getTime() );
	                    //System.out.println( sDate + " - " + d + " - " + eDate);
	                    if ( d != null && sDate.getTime() <= d.getTime() && d.getTime() <= eDate.getTime() )
	                    	countryADB_ResultMap.put( de.getId()+"_"+tempMonthMap.get( dateValue ), 1);
	                }
	                catch ( Exception e )
	                {
	                }
	            }
        	}// de for loop end
        	//Collections.sort( des, new IdentifiableObjectNameComparator() );
        	countryADB_Header_DeMap.put( headerName, des );
        }
        
        epiProfileSnapshot.setCountryADB_monthNamesMap(countryADB_monthNamesMap);
        epiProfileSnapshot.setCountryADB_ResultMap(countryADB_ResultMap);
        epiProfileSnapshot.setCountryADB_Header_DeMap( countryADB_Header_DeMap );
        epiProfileSnapshot.setCountryADB_SubHeaders( countryADB_SubHeaders );
        //epiProfileSnapshot.setCountryADB_DeObjs( countryADB_DeObjs );
        
		//*************************Country Activity Dashboard END***************************
        
		
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        
       // System.out.println( epiProfileSnapshot.getEpiDataMap().get(1000300).getStrVal2() );
		JSONObject jsonResp = new JSONObject();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		dataJSON = ow.writeValueAsString(epiProfileSnapshot);
		//System.out.println(dataJSON);
		jsonResp.put("result", new JSONObject(dataJSON) );
		
        
		//PrintWriter out = new PrintWriter("");
		//jsonResp.write( out );
		
//		outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
//
//		HSSFWorkbook workbook = new HSSFWorkbook();
//		HSSFSheet sheet0 = workbook.createSheet("OfflineEntry");
//
//		CreationHelper factory = workbook.getCreationHelper();
//		Drawing drawing = sheet0.createDrawingPatriarch();
//		ClientAnchor anchor = factory.createClientAnchor();
//
//		try {
//			FileOutputStream out = new FileOutputStream(new File(outputReportPath));
//			workbook.write(out);
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		fileName = "OfflineEntry.xls";
//		File outputReportFile = new File(outputReportPath);
//		inputStream = new BufferedInputStream(new FileInputStream(outputReportFile));
//
//		outputReportFile.deleteOnExit();

        return SUCCESS;
    }      
}
