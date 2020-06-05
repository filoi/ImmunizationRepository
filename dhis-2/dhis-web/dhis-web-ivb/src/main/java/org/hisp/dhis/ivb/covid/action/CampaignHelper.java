package org.hisp.dhis.ivb.covid.action;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.commons.util.TextUtils.getCommaDelimitedString;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.GenericTypeObj;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author BHARATH
 */

@Transactional
public class CampaignHelper
{
	public static final String REPORT_TEMPLATE_FOLDER = "report_templates";
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate ){
        this.jdbcTemplate = jdbcTemplate;
    }
    
	@Autowired
    private IVBUtil ivbUtil;
	
	@Autowired
	private SectionService sectionService;
	
    @Autowired 
    private LookupService lookupService;

    @Autowired
    private OrganisationUnitGroupService ouGroupService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private DataValueService dataValueService;
    
    @Autowired
    private OrganisationUnitService ouService;
    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------

    
    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------
   
    //OrgUnitId + "_" + DataElementId as key and data as value
    public Map<String, GenericDataVO> getLatestDataValues( String deIdsByComma, String ouIdsByComma )
    {
        Map<String, GenericDataVO> latestDataValues = new HashMap<String, GenericDataVO>();
        
        try
        {
            String query = "SELECT dv.sourceid, dv.dataelementid, dv.periodid, dv.value, dv.comment, dv.storedby, dv.lastupdated " +
                    " FROM " +
                        "( " +
                            " SELECT periodid,dataelementid,sourceid FROM " + 
                                "(SELECT MAX(p.startdate) AS startdate,dv.dataelementid,dv.sourceid FROM datavalue dv " +
                                    " INNER JOIN period p ON p.periodid=dv.periodid " +
                                        " WHERE dv.dataelementid IN ( "+ deIdsByComma +") AND dv.sourceid IN (" + ouIdsByComma + ")  " + 
                                        " GROUP BY dv.dataelementid,dv.sourceid " +
                                 ")asd " +
                             " INNER JOIN period p ON p.startdate=asd.startdate " +
                         ")asd1 " +
                     " INNER JOIN datavalue dv ON dv.sourceid=asd1.sourceid " +
                     " AND dv.dataelementid=asd1.dataelementid " +
                     " AND dv.periodid=asd1.periodid";

            //System.out.println( "getLatestDataValues query = " + query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while( rs.next() ){
                Integer ouId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                Integer pId = rs.getInt( 3 );
                String value = rs.getString( 4 );
                String comment = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                Date lastUpdated = rs.getDate( 7 );

                GenericDataVO dataVo = new GenericDataVO();
                dataVo.setStrVal1( value );
                dataVo.setStrVal2( comment );
                latestDataValues.put( ouId+"_"+deId, dataVo );
            }
        }
        catch( Exception e ){
        	e.printStackTrace();
        }
        
        return latestDataValues;
    }
    
    
    public Map<String, GenericDataVO> getLatestDataValuesForCampaignReport( String deIdsByComma, String ouIdsByComma )
    {
        Map<String, GenericDataVO> latestDataValues = new HashMap<String, GenericDataVO>();
        
        try
        {
            String query = "SELECT dv.sourceid, dv.dataelementid, dv.periodid, dv.value, dv.comment, dv.storedby, dv.lastupdated " +
                    " FROM " +
                        "( " +
                            " SELECT periodid,dataelementid,sourceid FROM " + 
                                "(SELECT MAX(p.startdate) AS startdate,dv.dataelementid,dv.sourceid FROM datavalue dv " +
                                    " INNER JOIN period p ON p.periodid=dv.periodid " +
                                        " WHERE dv.dataelementid IN ( "+ deIdsByComma +") AND dv.sourceid IN (" + ouIdsByComma + ")  " + 
                                        " GROUP BY dv.dataelementid,dv.sourceid " +
                                 ")asd " +
                             " INNER JOIN period p ON p.startdate=asd.startdate " +
                         ")asd1 " +
                     " INNER JOIN datavalue dv ON dv.sourceid=asd1.sourceid " +
                     " AND dv.dataelementid=asd1.dataelementid " +
                     " AND dv.periodid=asd1.periodid " +
                     " WHERE dv.value IS NOT NULL AND dv.value <> ''";
            
            //System.out.println("getLatestDataValuesForCampaignReport query = "+ query);

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while( rs.next() ){
                Integer ouId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                Integer pId = rs.getInt( 3 );
                String value = rs.getString( 4 );
                String comment = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                Date lastUpdated = rs.getDate( 7 );

                GenericDataVO dataVo = new GenericDataVO();
                dataVo.setStrVal1( value );
                dataVo.setStrVal2( comment );
                latestDataValues.put( ouId+"_"+deId, dataVo );
            }
        }
        catch( Exception e ){
        	e.printStackTrace();
        }
        
        return latestDataValues;
    }
    
    public Map<String, List<GenericDataVO>> getEventData_UserActivity( String psIdsByComma, String deIdsByComma, String ouIdsByComma, String startDate, String endDate, User user )
    {
    	Map<String, List<GenericDataVO>> eventDataMap = new HashMap<>();
        
        try
        {
            String query = "SELECT t2.programstageid, t1.programstageinstanceid, t2.organisationunitid, t1.dataelementid, t1.value, t1.storedby, DATE(t1.timestamp) as stored_on, DATE(t2.executiondate) report_date FROM trackedentitydatavalue as t1 " + 
            					" INNER JOIN programstageinstance as t2 ON t1.programstageinstanceid = t2.programstageinstanceid " + 
            					" WHERE t2.programstageid IN ("+ psIdsByComma +") AND t2.organisationunitid IN ("+ ouIdsByComma +") AND t1.dataelementid IN ("+ deIdsByComma +")";
            				if( user != null )
            					query += " AND t1.storedby = '"+ user.getUsername() +"' ";
            				if( startDate != null && endDate != null )
                                query += " AND DATE(t1.timestamp) BETWEEN '"+ startDate +"' AND '"+ endDate +"' ";
            				query += " ORDER BY t1.timestamp DESC";
            				
            System.out.println("getEventData Query= "+query);
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while( rs.next() ){
                Integer psId = rs.getInt( 1 );
                Integer psInstId = rs.getInt( 2 );
            	Integer ouId = rs.getInt( 3 );
                Integer deId = rs.getInt( 4 );
                String value = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                String storedOn = rs.getString(7);
                String reportDate = rs.getString(8);

                String baseKey = psId+"_"+ouId+"_"+deId;
                /*
                if( eventDataMap.get( baseKey) == null )
                	eventDataMap.put(baseKey, new HashMap<>());
                
                if( eventDataMap.get( baseKey).get(psInstId) == null )
                	eventDataMap.get( baseKey).put(psInstId, new CampaignVO());
                
                if( eventDataMap.get( baseKey).get(psInstId).getColDataMap() == null )
                	eventDataMap.get( baseKey).get(psInstId).setColDataMap( new HashMap<>());
                */

                if( eventDataMap.get( baseKey) == null )
                	eventDataMap.put(baseKey, new ArrayList<>());
                
                GenericDataVO dvo = new GenericDataVO();
                dvo.setValue(value);
                dvo.setStrVal1(reportDate);
                dvo.setStrVal2(storedOn);
                dvo.setStoredBy(storedBy);

                eventDataMap.get( baseKey).add( dvo );
            }
        }
        catch( Exception e ){
        	e.printStackTrace();
        }
        
        return eventDataMap;
    }
    
    //programstageid +"_" + OrgUnitId as key and 
    //	value is hashmap where key is programstage instance id and value is CampaignVO
    public Map<String, Map<Integer, CampaignVO>> getEventData( String psIdsByComma, String deIdsByComma, String ouIdsByComma )
    {
    	Map<String, Map<Integer, CampaignVO>> eventDataMap = new HashMap<>();
        
        try
        {
            String query = "SELECT t2.programstageid, t1.programstageinstanceid, t2.organisationunitid, t1.dataelementid, t1.value FROM trackedentitydatavalue as t1 " + 
            					" INNER JOIN programstageinstance as t2 ON t1.programstageinstanceid = t2.programstageinstanceid " + 
            					" WHERE t2.programstageid IN ("+ psIdsByComma +") AND t2.organisationunitid IN ("+ ouIdsByComma +") AND t1.dataelementid IN ("+ deIdsByComma +")"; 

            //System.out.println("getEventData Query= "+query);
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while( rs.next() ){
                Integer psId = rs.getInt( 1 );
                Integer psInstId = rs.getInt( 2 );
            	Integer ouId = rs.getInt( 3 );
                Integer deId = rs.getInt( 4 );
                String value = rs.getString( 5 );

                String baseKey = psId+"_"+ouId;
                if( eventDataMap.get( baseKey) == null )
                	eventDataMap.put(baseKey, new HashMap<>());
                
                if( eventDataMap.get( baseKey).get(psInstId) == null )
                	eventDataMap.get( baseKey).put(psInstId, new CampaignVO());
                
                if( eventDataMap.get( baseKey).get(psInstId).getColDataMap() == null )
                	eventDataMap.get( baseKey).get(psInstId).setColDataMap( new HashMap<>());
                
                GenericDataVO dvo = new GenericDataVO();
                dvo.setStrVal1(value);
                eventDataMap.get( baseKey).get(psInstId).getColDataMap().put(deId+"", dvo);
            }
        }
        catch( Exception e ){
        	e.printStackTrace();
        }
        
        return eventDataMap;
    }
   
    public CampaignSnapshot getCampainTrackerSnap(CampaignSnapshot campaignSnap )
    {
    	String ouIdsByComma = "-1";
        if ( campaignSnap.getOuIds() != null && campaignSnap.getOuIds().size() > 0 ){
        	ouIdsByComma = getCommaDelimitedString( campaignSnap.getOuIds() );
        }
        Lookup lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        campaignSnap.setUnicefRegionsGroupSet( ouGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) ) );
        for(Integer ouId : campaignSnap.getOuIds() ){
            OrganisationUnit orgUnit = ouService.getOrganisationUnit( ouId );
            campaignSnap.getOrgUnitList().add( orgUnit );
        }
        Collections.sort(campaignSnap.getOrgUnitList(), new IdentifiableObjectNameComparator() );
        
        //System.out.println(ouIdsByComma);
        //Selected Campaigns 
        Set<String> sectionCodes = new HashSet<>();
        for( Integer sectionId : campaignSnap.getCampaignIds() ){
            Section section = sectionService.getSection( sectionId );
            campaignSnap.getDsSections().add( section );
            
            sectionCodes.add( section.getCode().trim().toLowerCase() );
        }

        
        lookup = lookupService.getLookupByName( "CAMPAIGN_PROGRAM_STAGE_IDS" );
        String psIdsByComma = "-1";
        List<ProgramStage> programStages = new ArrayList<>();
        for(String psId : lookup.getValue().split(",")) {
        	ProgramStage ps = programStageService.getProgramStage( Integer.parseInt(psId) );
        	if( ps!= null && sectionCodes.contains( ps.getName().trim().toLowerCase()) ) {
        		programStages.add( ps );
        		psIdsByComma += ","+ps.getId();
        	}
        }
        
        lookup = lookupService.getLookupByName( "CAMPAIGN_SUBNATIONAL_DEID" );
        int subNationalDeId = Integer.parseInt( lookup.getValue() );
        lookup = lookupService.getLookupByName( "CAMPAIGN_GEOGRAPHIC_SCALE_DEID" );
        int geoScaleDeId = Integer.parseInt( lookup.getValue() );
        
        
        //Selected Columns
        Map<Integer, String> deColMap = new HashMap<>();
        String deIdsByComma = "-1";
        String psDeIdsByComma = "-1";
        psDeIdsByComma += "," +subNationalDeId;
        lookup = lookupService.getLookupByName( "CAMPAIGN_COLUMNS_INFO" );
        String campaignColInfo = lookup.getValue();
        //colList = new ArrayList<GenericTypeObj>();
        for( String colInfo : campaignColInfo.split("@!@") ) {
        	if( campaignSnap.getSelCols().contains( colInfo.split("@-@")[0] ) ) {
	        	GenericTypeObj colObj = new GenericTypeObj();
	        	colObj.setCode( colInfo.split("@-@")[0] );
	        	colObj.setName( colInfo.split("@-@")[1] );
	        	colObj.setStrAttrib1( colInfo.split("@-@")[2] ); //deids
	        	colObj.setStrAttrib2( colInfo.split("@-@")[3] ); //ps deids
	        	campaignSnap.getColList().add( colObj );
	        	deIdsByComma += ","+colInfo.split("@-@")[2];
	        	psDeIdsByComma += ","+colInfo.split("@-@")[3];
	        	for(String deIdStr : colObj.getStrAttrib1().split(",") ) {
	        		deColMap.put(Integer.parseInt(deIdStr), colObj.getCode());
	        	}
	        	deColMap.put(Integer.parseInt(colObj.getStrAttrib2()), colObj.getCode());
        	}
        }
        
        if( campaignSnap.getResultPage() == 1 ) {
	        lookup = lookupService.getLookupByName( "MEASLES_COLUMNS_INFO" );
	        String measlesColInfo = lookup.getValue();
	        //colList = new ArrayList<GenericTypeObj>();
	        for( String colInfo : measlesColInfo.split("@!@") ) {
	        	if( campaignSnap.getSelCols().contains( colInfo.split("@-@")[0] ) ) {
		        	GenericTypeObj colObj = new GenericTypeObj();
		        	colObj.setCode( colInfo.split("@-@")[0] );
		        	colObj.setName( colInfo.split("@-@")[1] );
		        	colObj.setStrAttrib1( colInfo.split("@-@")[2] ); //deids
		        	colObj.setStrAttrib2( colInfo.split("@-@")[3] ); //ps deids
		        	campaignSnap.getColList().add( colObj );
		        	deIdsByComma += ","+colInfo.split("@-@")[2];
		        	psDeIdsByComma += ","+colInfo.split("@-@")[3];
		        	for(String deIdStr : colObj.getStrAttrib1().split(",") ) {
		        		deColMap.put(Integer.parseInt(deIdStr), colObj.getCode());
		        	}
		        	deColMap.put(Integer.parseInt(colObj.getStrAttrib2()), colObj.getCode());
	        	}
	        }
        }
        
        //System.out.println(deIdsByComma);
        Map<String, GenericDataVO> dvDataMap = getLatestDataValuesForCampaignReport( deIdsByComma, ouIdsByComma );
        Map<String, Map<Integer, CampaignVO>> eventDataMap = getEventData( psIdsByComma, psDeIdsByComma, ouIdsByComma );
        //System.out.println( dvDataMap.size() +" and "+eventDataMap.size() );
        
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = new DecimalFormat( "#,###,###,##0" );
        
        campaignSnap.setNf( nf );
        
        //Arranging Aggregated Data
        Set<Integer> deIds = deColMap.keySet();
        Map<String, List<CampaignVO>> dataMap = new HashMap<>();
        String subNationName = "NONE";
        Set<String> subNationalNames = new HashSet<>();
        for( int ouId : campaignSnap.getOuIds() ) {
        	
        	//String key1 = ouId+"_National";
			//dataMap.put(key1, new ArrayList<>());
        	for(Section section : campaignSnap.getDsSections() ) {
        		subNationName = "NONE";
        		String key1 = ouId+"_"+section.getCode();
        		if( dataMap.get(key1) == null)
        			dataMap.put(key1, new ArrayList<>());
        		int flag = 0;
        		Map<String, GenericDataVO> vacDataMap = null;
        		
        		//String key = ouId+"_"+section.getId();
        		for( DataElement de : section.getDataElements()) {
        			if( deIds.contains( de.getId() ) ){
	        			String dvKey = ouId+"_"+de.getId();
	        			String colCode = "NONE";
	        			colCode = deColMap.get(de.getId());
	        			if( dvDataMap.get(dvKey)!= null ) {
	        				if( vacDataMap == null )
	        					vacDataMap = new HashMap<>();
	        				flag = 1;
	        				
	        				vacDataMap.put(colCode, dvDataMap.get(dvKey));
	        				if( colCode.trim().equals("COL_4") )
	        					vacDataMap.put("COL_3", dvDataMap.get(dvKey));
	        				else if( colCode.trim().equals("COL_8") || colCode.trim().equals("COL_15") ) {
	        					String val = "";
	        					try{ val = dvDataMap.get(dvKey).getStrVal1();}catch(Exception e) {}
	        					try{ val = df.format(Double.parseDouble(val));}catch(Exception e) {}
	        					if( !val.trim().equals("") )
	        						dvDataMap.get(dvKey).setStrVal1( val );
	        					vacDataMap.put(colCode, dvDataMap.get(dvKey));
	        				}
	        				else if( colCode.trim().equals("COL_10") ) {
	        					String val = "";
	        					try{ val = dvDataMap.get(dvKey).getStrVal1();}catch(Exception e) {}
	        					if( !val.trim().equals("") )
	        						subNationName = val;
	        				}
	        					
							//System.out.println(key + " + " + colCode +" = " +dvDataMap.get(key2).getStrVal1());
	        			}
        			}
        		}
        		
        		if( flag == 1) {
        			CampaignVO cvo = new CampaignVO();
        			GenericDataVO dvo = new GenericDataVO();
            		dvo.setStrVal1(subNationName);
            		dvo.setIntVal1(1);
            		vacDataMap.put("COL_0", dvo);
            		cvo.setColDataMap( vacDataMap );
            		dataMap.get(key1).add( cvo );
            		subNationalNames.add( subNationName );
        		}
        	}
        }
        
        
        
        //Arranging Event Data
        for( int ouId : campaignSnap.getOuIds() ) {
        	for(ProgramStage ps : programStages ) {
        		String eBaseKey = ps.getId()+"_"+ouId;
        		if( eventDataMap.get(eBaseKey) == null ) {
        			//System.out.println("Data Unavailable for the key "+ eBaseKey);
        			continue;
        		}
        		else {
        			//System.out.println("Data available for the key "+ eBaseKey);
        		}
        		for(Integer psInsId : eventDataMap.get(eBaseKey).keySet()) {
        			subNationName = "NONE";
        			try { subNationName = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(subNationalDeId+"").getStrVal1();}catch(Exception e) {}
        			if( subNationName.trim().equals("NONE") || subNationName.trim().equals("") ) {
        				try { subNationName = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(geoScaleDeId+"").getStrVal1();}catch(Exception e) {}
        			}
        				
        			int flag = 0;
            		//String key1 = ouId+"_"+subNationName;
        			String key1 = ouId+"_"+ps.getName();
            		//System.out.println( key1 );
            		if( dataMap.get(key1) == null)
            			dataMap.put(key1, new ArrayList<>());
            		Map<String, GenericDataVO> vacDataMap = null;
            		for( DataElement de : ps.getAllDataElements()) {
            			if( deIds.contains( de.getId() ) ){
    	        			String colCode = "NONE";
    	        			colCode = deColMap.get(de.getId());
    	        			GenericDataVO dvo = null;
    	        			try { dvo = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(de.getId()+"");}catch(Exception e) {}
    	        			if( dvo != null ) {
    	        				if( vacDataMap == null )
    	        					vacDataMap = new HashMap<>();
    	        				flag = 1;
    	        				if( colCode.trim().equals("COL_4") )
    	        					dvo.setStrVal2(dvo.getStrVal1());
    	        				else if( colCode.trim().equals("COL_8") || colCode.trim().equals("COL_15") ) {
    	        					String val = "";
    	        					try{ val = dvo.getStrVal1();}catch(Exception e) {}
    	        					try{ val = df.format(Double.parseDouble(val));}catch(Exception e) {}
    	        					if( !val.trim().equals("") )
    	        						dvo.setStrVal1( val );
    	        				}
    	        				vacDataMap.put(colCode, dvo);
    	        				subNationalNames.add(subNationName);
    	        			}
            			}
            		}
            		if( flag == 1) {
            			CampaignVO cvo = new CampaignVO();
            			GenericDataVO dvo = new GenericDataVO();
                		dvo.setStrVal1(subNationName );
                		dvo.setIntVal1(2);
                		vacDataMap.put("COL_0", dvo);
                		cvo.setColDataMap( vacDataMap );
                		dataMap.get(key1).add( cvo );
                		//System.out.println("Data Row added for PS instanceid : "+ psInsId );
            		}
        		}
        	}
        }		
       
        campaignSnap.getSubNatNames().addAll( subNationalNames );
        Collections.sort( campaignSnap.getSubNatNames() );
        
        campaignSnap.setCtDataMap(dataMap);
        
		/*
        for( String key1 : dataMap.keySet() ) {
        	for( CampaignVO cvo : dataMap.get(key1)) {
        		for(String key2 : cvo.getColDataMap().keySet() ) {
        			System.out.println( key1 + ", " + key2 + " = " + cvo.getColDataMap().get(key2).getStrVal1() );
        		}
        	}
        }
		*/
       
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        for( OrganisationUnit orgUnit : campaignSnap.getOrgUnitList() )
        {
            DataElement de1 = dataElementService.getDataElement( 4 );            
            DataValue dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
            {
                campaignSnap.getCountryGeneralInfoMap().put( orgUnit.getId()+":4", "" );
            }
            else
            {
            	campaignSnap.getCountryGeneralInfoMap().put( orgUnit.getId()+":4", dv.getValue() );
            }

            de1 = dataElementService.getDataElement( 3 );
            dv = dataValueService.getLatestDataValue( de1, optionCombo, orgUnit );            
            if( dv == null || dv.getValue() == null )
            {
            	campaignSnap.getCountryGeneralInfoMap().put( orgUnit.getId()+":3", "" );
            }
            else
            {
            	campaignSnap.getCountryGeneralInfoMap().put( orgUnit.getId()+":3", dv.getValue() );
            }
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date();
        campaignSnap.setCurDateStr( sdf.format( curDate ) );
        
    	return campaignSnap;
    }
    
    private XSSFCellStyle headerStyle;
    private XSSFCellStyle subHeaderStyle;
    private XSSFCellStyle tHeaderStyle;
    private XSSFCellStyle numberStyle;
    private XSSFCellStyle dataStyle;
    private Map<String, XSSFCellStyle> statusCellStyles = new HashMap<>();
    
    private void initialiseExcelProps( XSSFWorkbook wb )
    {
    	DataFormat dataFormat = wb.createDataFormat();
    	
    	XSSFFont headerFont = wb.createFont();//Create font
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
		headerFont.setFontHeightInPoints((short)14);
		headerFont.setColor(IndexedColors.BLACK.getIndex());
	    
		XSSFFont subHeaderFont = wb.createFont();//Create font
    	subHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold    
    	subHeaderFont.setFontHeightInPoints((short)12);
    	subHeaderFont.setColor(IndexedColors.BLACK.getIndex());
	    
    	//String rgbS = "FFF000";
    	//byte[] rgbB = Hex.decodeHex(rgbS.toCharArray()); // get byte array from hex string
   	    
    	XSSFFont tHeaderFont = wb.createFont();//Create font
    	tHeaderFont.setFontHeightInPoints((short)9);
    	tHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
    	XSSFColor tHeadercolor = new XSSFColor( Color.decode("#0A2F76") );
    	tHeaderFont.setColor(tHeadercolor);
 	    
    	XSSFFont dataFont = wb.createFont();//Create font
	    dataFont.setFontHeightInPoints((short)9);
	    dataFont.setColor(IndexedColors.BLACK.getIndex());

		
	    
	    XSSFFont dataFontBoldItalic = wb.createFont();//Create font
	    dataFontBoldItalic.setFontHeightInPoints((short)9);
	    dataFontBoldItalic.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    dataFontBoldItalic.setItalic( true );
	    dataFontBoldItalic.setColor(IndexedColors.BLACK.getIndex());
	    
	    // Main Header
 		headerStyle = wb.createCellStyle();//Create style
 		headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
 		//headerStyle.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.index );
 		//headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
 		//headerStyle.setWrapText( true );
 		headerStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
 	    headerStyle.setFont(headerFont);
 	    
 	    //Sub Header
 		subHeaderStyle = wb.createCellStyle();//Create style
 		subHeaderStyle.setAlignment(CellStyle.ALIGN_LEFT);
 		subHeaderStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
 		subHeaderStyle.setFont(subHeaderFont);
 	    
 	    //Table Header
 		tHeaderStyle = wb.createCellStyle();//Create style
 		tHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
 		//tHeaderStyle.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.index );
 		XSSFColor color = new XSSFColor( Color.decode("#F0F0F0") );
 		tHeaderStyle.setFillForegroundColor( color );
 		tHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
 		tHeaderStyle.setWrapText( true );
 		tHeaderStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
 		tHeaderStyle.setFont(tHeaderFont);
 	    
 		//Data Style
 		dataStyle = wb.createCellStyle();
	    dataStyle.setAlignment(CellStyle.ALIGN_LEFT);
	    dataStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
	    dataStyle.setWrapText( true );
	    dataStyle.setFont( dataFont );
	    
 	    //Number Style
	    numberStyle = wb.createCellStyle();
	    numberStyle.setAlignment(CellStyle.ALIGN_RIGHT);
	    numberStyle.setDataFormat(dataFormat.getFormat("###,###0;[Red]-###,###0"));
	    numberStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
	    numberStyle.setFont( dataFont );
    }
    
    public Workbook generateCampaignTrackerXl( CampaignSnapshot campaignSnap )
    {
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	try {
    		//CreationHelper factory = workbook.getCreationHelper();
			//Drawing drawing = sheet.createDrawingPatriarch();
			
    		initialiseExcelProps( workbook );
    		
    		int rowCount = 0;
    		
    		String sheetName ="CampaignTracker";
    		if( campaignSnap.getResultPage()==1)
    			sheetName = "MeaslesDashboard";
    		
			Sheet sheet = workbook.createSheet( sheetName );
			
			//Main Header
			Row row = sheet.createRow( rowCount++ );			
			int colCount = 0;
			Cell cell = row.createCell( colCount++ );
			cell.setCellStyle( headerStyle );
			//sheet.addMergedRegion( new org.apache.poi.ss.util.CellRangeAddress( rowCount-1, rowCount-1, colCount-1, colCount+5 ) );
			if( campaignSnap.getResultPage()==1)
				cell.setCellValue( "Measles Dashboard - current as of "+campaignSnap.getCurDateStr() );
			else
				cell.setCellValue( "Campaign Tracker - current as of "+campaignSnap.getCurDateStr() );
			
			//Sub Header
			rowCount++;
			row = sheet.createRow( rowCount++ );			
			colCount = 0;
			cell = row.createCell( colCount++ );
			cell.setCellStyle( subHeaderStyle );
			//sheet.addMergedRegion( new org.apache.poi.ss.util.CellRangeAddress( rowCount-1, rowCount-1, colCount-1, colCount+5 ) );
			String campaigns = "";
			for( Section section : campaignSnap.getDsSections() )
				campaigns += section.getCode()+" "; 
			cell.setCellValue( "Campaigns: "+campaigns );
			
			rowCount += 2;
			
			//Table Header
			row = sheet.createRow( rowCount++ );			
			colCount = 0;			
			if( campaignSnap.getIsoCode() != null && campaignSnap.getIsoCode().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "ISO Code" );
				sheet.setColumnWidth(colCount-1, 6 * 256);

			}
			if( campaignSnap.getWhoRegion() != null && campaignSnap.getWhoRegion().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "WHO Region" );
				sheet.setColumnWidth(colCount-1, 8 * 256);
			}
			if( campaignSnap.getUnicefRegion() != null && campaignSnap.getUnicefRegion().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "UNICEF Region" );
				sheet.setColumnWidth(colCount-1, 7 * 256);
			}

			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Country" );
			sheet.setColumnWidth(colCount-1, 12 * 256);
			
			if( campaignSnap.getIncomeLevel() != null && campaignSnap.getIncomeLevel().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "Income level" );
				sheet.setColumnWidth(colCount-1, 15 * 256);
			}
			if( campaignSnap.getGaviEligibleStatus() != null && campaignSnap.getGaviEligibleStatus().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "GAVI eligibility status" );
				sheet.setColumnWidth(colCount-1, 15 * 256);
			}

			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Campaign Vaccine" );
			sheet.setColumnWidth(colCount-1, 8 * 256);
			
			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Campaign Identifier" );
			sheet.setColumnWidth(colCount-1, 13 * 256);
			
			int freezeColNo = colCount;
			Map<String, Integer> colWidthMap = new HashMap<>();
			colWidthMap.put("COL_1", 10);
			colWidthMap.put("COL_2", 10);
			colWidthMap.put("COL_3", 15);
			colWidthMap.put("COL_4", 17);
			colWidthMap.put("COL_5", 10);
			colWidthMap.put("COL_6", 10);
			colWidthMap.put("COL_7", 10);
			colWidthMap.put("COL_8", 11);
			colWidthMap.put("COL_9", 11);
			colWidthMap.put("COL_10", 12);
			colWidthMap.put("COL_11", 11);
			colWidthMap.put("COL_12", 20);
			colWidthMap.put("COL_13", 25);
			
			for( GenericTypeObj colObj : campaignSnap.getColList() ){
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( colObj.getName() );
				/*
				if( colObj.getCode().equals("COL_1") || colObj.getCode().equals("COL_2") || colObj.getCode().equals("COL_5") || colObj.getCode().equals("COL_6") ||
						colObj.getCode().equals("COL_7") || colObj.getCode().equals("COL_8") || colObj.getCode().equals("COL_10") )
					sheet.setColumnWidth(colCount-1, 12 * 256);
				else if( colObj.getCode().equals("COL_3") || colObj.getCode().equals("COL_9") || colObj.getCode().equals("COL_11") || colObj.getCode().equals("COL_12") )
					sheet.setColumnWidth(colCount-1, 15 * 256);
				else if( colObj.getCode().equals("COL_4") )
					sheet.setColumnWidth(colCount-1, 18 * 256);
				else if( colObj.getCode().equals("COL_13") )
					sheet.setColumnWidth(colCount-1, 25 * 256);
				else
					sheet.setColumnWidth(colCount-1, 10 * 256);
				*/
				int colWidth = 10;
				try { colWidth = colWidthMap.get( colObj.getCode() ); }catch(Exception e) {}
				sheet.setColumnWidth(colCount-1, colWidth * 256);
				
				if( campaignSnap.getShowComment() != null && campaignSnap.getShowComment().equals("ON") && !colObj.getCode().equals("COL_3") && !colObj.getCode().equals("COL_4") && !colObj.getCode().equals("COL_13") ) {
					cell = row.createCell( colCount++ );
					cell.setCellStyle( tHeaderStyle );
					cell.setCellValue( colObj.getName() +" Comment");

					sheet.setColumnWidth(colCount-1, 18 * 256);
				}
			}
			
			sheet.setAutoFilter(new CellRangeAddress(rowCount-1, rowCount-1, 0, colCount-1));
			sheet.createFreezePane( freezeColNo, rowCount);
			
			//Table Body - Data
			for( OrganisationUnit orgUnit : campaignSnap.getOrgUnitList() ) {
				for( Section section : campaignSnap.getDsSections() ) {
					String key1 = orgUnit.getId()+"_"+section.getCode();
					List<CampaignVO> vaccineResultList = campaignSnap.getCtDataMap().get( key1 );
					if( vaccineResultList != null ) {
						for( CampaignVO cvo : vaccineResultList ){
							Map<String, GenericDataVO> cMap = cvo.getColDataMap();
							
							row = sheet.createRow( rowCount++ );			
							colCount = 0;			
							if( campaignSnap.getIsoCode() != null && campaignSnap.getIsoCode().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								cell.setCellValue( orgUnit.getCode() );				
							}
							if( campaignSnap.getWhoRegion() != null && campaignSnap.getWhoRegion().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								cell.setCellValue( orgUnit.getParent().getShortName() );				
							}
							if( campaignSnap.getUnicefRegion() != null && campaignSnap.getUnicefRegion().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = orgUnit.getGroupInGroupSet( campaignSnap.getUnicefRegionsGroupSet() ).getShortName(); }catch(Exception e) {}
								cell.setCellValue( tempVal );										
							}

							cell = row.createCell( colCount++ );
							cell.setCellStyle( dataStyle );
							cell.setCellValue( orgUnit.getShortName() );				
							
							if( campaignSnap.getIncomeLevel() != null && campaignSnap.getIncomeLevel().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = campaignSnap.getCountryGeneralInfoMap().get(orgUnit.getId()+":3"); }catch(Exception e) {}
								cell.setCellValue( tempVal );				
							}
							if( campaignSnap.getGaviEligibleStatus() != null && campaignSnap.getGaviEligibleStatus().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = campaignSnap.getCountryGeneralInfoMap().get(orgUnit.getId()+":4"); }catch(Exception e) {}
								cell.setCellValue( tempVal );				
							}

							cell = row.createCell( colCount++ );
							cell.setCellStyle( dataStyle );
							cell.setCellValue( section.getCode() );				
							
							String subNatName = " ";
                    		try{ subNatName = cMap.get( "COL_0" ).getStrVal1(); }catch(Exception e){}
                    		if( subNatName.trim().equals("NONE") )
                    			subNatName = " ";
							cell = row.createCell( colCount++ );
							cell.setCellStyle( dataStyle );
							cell.setCellValue( subNatName );
							
							for( GenericTypeObj colObj : campaignSnap.getColList() ){
								String comment = " ";
                        		try{ comment = cMap.get( colObj.getCode() ).getStrVal2();} catch(Exception e) {}
                            	String value = " ";
                            	try{ value = cMap.get( colObj.getCode() ).getStrVal1(); } catch(Exception e) {}
                            	if( colObj.getCode().equals("COL_3") && value.equals("May postpone") )
									value = "Might postpone";
								
                            	if( colObj.getCode().equals("COL_4") )
                            		value = comment;
                            	
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								cell.setCellValue( value );
								
								if( campaignSnap.getShowComment() != null && campaignSnap.getShowComment().equals("ON") && !colObj.getCode().equals("COL_3") && !colObj.getCode().equals("COL_4") && !colObj.getCode().equals("COL_13") ) {
									cell = row.createCell( colCount++ );
									cell.setCellStyle( dataStyle );
									cell.setCellValue( comment );
								}
							}
							
						}
					}
				}
			}
    	}
    	catch(Exception e) {
    		System.out.println("Error while preparing workbook object in generateCampaignTrackerXl "+ e.getMessage() );
    		e.printStackTrace();
    	}
    	
    	return workbook;
    }
    
    
    public Workbook generateCampaignCalendarXl( CampaignSnapshot campaignSnap )
    {
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	try {
    		//CreationHelper factory = workbook.getCreationHelper();
			//Drawing drawing = sheet.createDrawingPatriarch();
			
    		initialiseExcelProps( workbook );
    		
    		campaignSnap.getStatusColorMap().put("Might postpone", campaignSnap.getStatusColorMap().get("May postpone") );
    		
    		int rowCount = 0;
    		
			Sheet sheet = workbook.createSheet( "CampaignCalendar" );
			
			//Main Header
			Row row = sheet.createRow( rowCount++ );			
			int colCount = 0;
			Cell cell = row.createCell( colCount++ );
			cell.setCellStyle( headerStyle );
			//sheet.addMergedRegion( new org.apache.poi.ss.util.CellRangeAddress( rowCount-1, rowCount-1, colCount-1, colCount+4 ) );
			cell.setCellValue( "Campaign Calendar - current as of "+campaignSnap.getCurDateStr() );
			
			//Sub Header
			//rowCount++;
			//row = sheet.createRow( rowCount++ );			
			//colCount = 0;
			//sheet.addMergedRegion( new org.apache.poi.ss.util.CellRangeAddress( rowCount-1, rowCount-1, colCount-1, colCount+5 ) );
			colCount += 6;
			XSSFFont dataFont = workbook.createFont();//Create font
		    dataFont.setFontHeightInPoints((short)9);
		    dataFont.setColor(IndexedColors.BLACK.getIndex());
		    
		    XSSFFont dataFontUnderline = workbook.createFont();//Create font
			dataFontUnderline.setFontHeightInPoints((short)9);
			dataFontUnderline.setUnderline(Font.U_SINGLE);
			dataFontUnderline.setColor(IndexedColors.BLACK.getIndex());
			
			for( String statusMapKey : campaignSnap.getStatusColorMap().keySet() ) {
				if( statusMapKey.equalsIgnoreCase("May postpone") )
					statusMapKey = "Might postpone";
				
				XSSFCellStyle tempStyle = workbook.createCellStyle();//Create style
				tempStyle.setAlignment(CellStyle.ALIGN_CENTER);
		 		XSSFColor tempColor = new XSSFColor( Color.decode(campaignSnap.getStatusColorMap().get(statusMapKey)) );
		 		tempStyle.setFillForegroundColor( tempColor );
		 		tempStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		 		tempStyle.setWrapText( true );
		 		tempStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
		 		tempStyle.setFont(dataFont);
		 		statusCellStyles.put(statusMapKey, tempStyle);
		 		
		 		XSSFCellStyle tempStyle1 = workbook.createCellStyle();//Create style
				tempStyle1.setAlignment(CellStyle.ALIGN_CENTER);
		 		tempStyle1.setFillForegroundColor( tempColor );
		 		tempStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		 		tempStyle1.setWrapText( true );
		 		tempStyle1.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
		 		tempStyle1.setFont(dataFontUnderline);
		 		statusCellStyles.put(statusMapKey+"_1", tempStyle1);
				
				cell = row.createCell( colCount++ );
				cell.setCellStyle( statusCellStyles.get(statusMapKey) );
				cell.setCellValue( statusMapKey );
			}
			colCount++;
			XSSFCellStyle tempStyle = workbook.createCellStyle();//Create style
			tempStyle.setAlignment(CellStyle.ALIGN_CENTER);
	 		//XSSFColor tempColor = new XSSFColor( Color.decode("#FFFFFF") );
	 		//tempStyle.setFillForegroundColor( tempColor );
	 		//tempStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	 		tempStyle.setWrapText( true );
	 		tempStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
	 		tempStyle.setFont(dataFontUnderline);
	 		cell = row.createCell( colCount++ );
	 		cell.setCellStyle( tempStyle );
	 		cell.setCellValue( "New Postponed Date" );

			tempStyle = workbook.createCellStyle();//Create style
			tempStyle.setAlignment(CellStyle.ALIGN_CENTER);
	 		tempStyle.setWrapText( true );
	 		tempStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
	 		tempStyle.setFont(dataFont);
	 		statusCellStyles.put("NOPE", tempStyle);
	 		
	 		XSSFCellStyle tempStyle1 = workbook.createCellStyle();//Create style
			tempStyle1.setAlignment(CellStyle.ALIGN_CENTER);
	 		tempStyle1.setWrapText( true );
	 		tempStyle1.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
	 		tempStyle1.setFont(dataFontUnderline);
	 		statusCellStyles.put("NOPE_1", tempStyle1);

			rowCount += 2;
			
			//Table Header
			row = sheet.createRow( rowCount++ );			
			colCount = 0;			
			if( campaignSnap.getIsoCode() != null && campaignSnap.getIsoCode().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "ISO Code" );
				sheet.setColumnWidth(colCount-1, 6 * 256);

			}
			if( campaignSnap.getWhoRegion() != null && campaignSnap.getWhoRegion().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "WHO Region" );
				sheet.setColumnWidth(colCount-1, 8 * 256);
			}
			if( campaignSnap.getUnicefRegion() != null && campaignSnap.getUnicefRegion().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "UNICEF Region" );
				sheet.setColumnWidth(colCount-1, 7 * 256);
			}

			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Country" );
			sheet.setColumnWidth(colCount-1, 12 * 256);
			
			if( campaignSnap.getIncomeLevel() != null && campaignSnap.getIncomeLevel().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "Income level" );
				sheet.setColumnWidth(colCount-1, 15 * 256);
			}
			if( campaignSnap.getGaviEligibleStatus() != null && campaignSnap.getGaviEligibleStatus().trim().equals("ON") ) {
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( "GAVI eligibility status" );
				sheet.setColumnWidth(colCount-1, 15 * 256);
			}
						
			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Campaign Identifier" );
			sheet.setColumnWidth(colCount-1, 13 * 256);
		
			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Status" );
			sheet.setColumnWidth(colCount-1, 15 * 256);
			
			int freezeColNo = colCount;

			cell = row.createCell( colCount++ );
			cell.setCellStyle( tHeaderStyle );
			cell.setCellValue( "Campaigns - no dates" );
			sheet.setColumnWidth(colCount-1, 10 * 256);

			/*
			Map<String, Integer> colWidthMap = new HashMap<>();
			colWidthMap.put("COL_1", 10);
			colWidthMap.put("COL_2", 10);
			colWidthMap.put("COL_3", 15);
			colWidthMap.put("COL_4", 17);
			colWidthMap.put("COL_5", 10);
			colWidthMap.put("COL_6", 10);
			colWidthMap.put("COL_7", 10);
			colWidthMap.put("COL_8", 11);
			colWidthMap.put("COL_9", 11);
			colWidthMap.put("COL_10", 12);
			colWidthMap.put("COL_11", 11);
			colWidthMap.put("COL_12", 20);
			colWidthMap.put("COL_13", 25);
			*/
			for( String monthName : campaignSnap.getMonthNames() ) {				
				cell = row.createCell( colCount++ );
				cell.setCellStyle( tHeaderStyle );
				cell.setCellValue( monthName );
				sheet.setColumnWidth(colCount-1, 10 * 256);
			}
			
			sheet.setAutoFilter(new CellRangeAddress(rowCount-1, rowCount-1, 0, colCount-1));
			sheet.createFreezePane( freezeColNo, rowCount);
			
			//Table Body - Data
			for( OrganisationUnit orgUnit : campaignSnap.getOrgUnitList() ) {
				for( String subNatName : campaignSnap.getSubNatNames() ) {
					String key1 = orgUnit.getId()+"_"+subNatName;
					List<CampaignVO> ouCvoList = campaignSnap.getCcDataMap().get( key1 );
					if(ouCvoList != null) {
						for( CampaignVO cvo : ouCvoList ){
							Map<String, GenericDataVO> cMap = cvo.getColDataMap();
							
							row = sheet.createRow( rowCount++ );			
							colCount = 0;
							if( campaignSnap.getIsoCode() != null && campaignSnap.getIsoCode().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								cell.setCellValue( orgUnit.getCode() );				
							}
							if( campaignSnap.getWhoRegion() != null && campaignSnap.getWhoRegion().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								cell.setCellValue( orgUnit.getParent().getShortName() );				
							}
							if( campaignSnap.getUnicefRegion() != null && campaignSnap.getUnicefRegion().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = orgUnit.getGroupInGroupSet( campaignSnap.getUnicefRegionsGroupSet() ).getShortName(); }catch(Exception e) {}
								cell.setCellValue( tempVal );										
							}

							cell = row.createCell( colCount++ );
							cell.setCellStyle( dataStyle );
							cell.setCellValue( orgUnit.getShortName() );				
							
							if( campaignSnap.getIncomeLevel() != null && campaignSnap.getIncomeLevel().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = campaignSnap.getCountryGeneralInfoMap().get(orgUnit.getId()+":3"); }catch(Exception e) {}
								cell.setCellValue( tempVal );				
							}
							if( campaignSnap.getGaviEligibleStatus() != null && campaignSnap.getGaviEligibleStatus().trim().equals("ON") ) {
								cell = row.createCell( colCount++ );
								cell.setCellStyle( dataStyle );
								String tempVal = " ";
								try { tempVal = campaignSnap.getCountryGeneralInfoMap().get(orgUnit.getId()+":4"); }catch(Exception e) {}
								cell.setCellValue( tempVal );				
							}
							
							cell = row.createCell( colCount++ );
							cell.setCellStyle( dataStyle );
							cell.setCellValue( subNatName );
							
							String statusVal = "";
							try{ statusVal = cMap.get( "STATUS" ).getStrVal1(); }catch(Exception e) {}
							if( statusVal.equalsIgnoreCase("May postpone") )
								statusVal = "Might postpone";
														
							cell = row.createCell( colCount++ );
							cell.setCellStyle( statusCellStyles.get(statusVal) );
							cell.setCellValue( statusVal );
							
							if( statusVal.trim().equals("") )
								statusVal = "NOPE";
							
							cell = row.createCell( colCount++ );
							XSSFRichTextString cellValue = new XSSFRichTextString();							    
							String temp1 = "";
							try{ temp1 =  cMap.get( "NONE1" ).getStrVal1(); }catch(Exception e) {}									
							String temp2 = "";
							try { temp2 =  cMap.get( "NONE2" ).getStrVal1(); }catch(Exception e) {}							
							if( !temp1.trim().equals("") || !temp2.trim().equals("") )									
								cell.setCellStyle( statusCellStyles.get(statusVal) );
							if( !temp1.trim().equals("") ) {
								if( !temp2.trim().equals("") )
									temp1 += "\n";
								String patternClass = "";
								try { patternClass = cMap.get( "NONE1" ).getStrVal3();}catch(Exception e) {}
								if( patternClass.equals("pattern2") )
									cellValue.append(temp1, dataFontUnderline);
								else 
									cellValue.append(temp1, dataFont);
							}
							if( !temp2.trim().equals("") ) {
								String patternClass = "";
								try { patternClass = cMap.get( "NONE2" ).getStrVal3();}catch(Exception e) {}
								if( patternClass.equals("pattern2") )
									cellValue.append(temp2, dataFontUnderline);
								else 
									cellValue.append(temp2, dataFont);
							}
							cell.setCellValue(cellValue);
							
														
							for( String monthName : campaignSnap.getMonthNames() ){
								String temp3 = "";
								try{ temp3 =  cMap.get( monthName ).getStrVal1(); }catch(Exception e) {}
								String patternClass = "";										
								try{ patternClass = cMap.get( monthName ).getStrVal3();}catch(Exception e) {}
								
								if( !temp3.trim().equals("") ) {
									cell = row.createCell( colCount++ );
									if( patternClass.equals("pattern2") )
										cell.setCellStyle( statusCellStyles.get(statusVal+"_1") );
									else
										cell.setCellStyle( statusCellStyles.get(statusVal) );									
									cell.setCellValue( temp3 );
								}
								else {
									cell = row.createCell( colCount++ );
									cell.setCellStyle( dataStyle );									
									cell.setCellValue( temp3 );
								}
							}
						}
					}
				}
			}
    	}
    	catch(Exception e) {
    		System.out.println("Error while preparing workbook object in generateCampaignTrackerXl "+ e.getMessage() );
    		e.printStackTrace();
    	}
    	
    	return workbook;
    }
    
    
    public CampaignSnapshot getCampainCalendarSnap(CampaignSnapshot campaignSnap )
    {
    	 Date sDate = null;
         Date eDate = null;

         if( campaignSnap.getFromDateStr() != null && !campaignSnap.getFromDateStr().trim().equals("") ){
             sDate = getStartDateByString( campaignSnap.getFromDateStr() );
         }         
         if( campaignSnap.getToDateStr() != null && !campaignSnap.getToDateStr().trim().equals("") ){
             eDate = getEndDateByString( campaignSnap.getToDateStr() );
         }
         
         String ouIdsByComma = "-1";
         if ( campaignSnap.getOuIds() != null && campaignSnap.getOuIds().size() > 0 ){
         	ouIdsByComma = getCommaDelimitedString( campaignSnap.getOuIds() );
         }
         Lookup lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
         campaignSnap.setUnicefRegionsGroupSet( ouGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) ) );
         for(Integer ouId : campaignSnap.getOuIds() ){
             OrganisationUnit orgUnit = ouService.getOrganisationUnit( ouId );
             campaignSnap.getOrgUnitList().add( orgUnit );
         }
         Collections.sort(campaignSnap.getOrgUnitList(), new IdentifiableObjectNameComparator() );

         
         //Campaigns 
         lookup = lookupService.getLookupByName( "CAMPAIGN_DATASET_UID" );
         DataSet dataSet = dataSetService.getDataSet( lookup.getValue() );
         campaignSnap.getDsSections().addAll( dataSet.getSections() );
        
         Set<String> sectionCodes = new HashSet<>();
         for( Section section : campaignSnap.getDsSections() ){
             sectionCodes.add( section.getCode().trim().toLowerCase() );
         }
         lookup = lookupService.getLookupByName( "CAMPAIGN_PROGRAM_STAGE_IDS" );
         String psIdsByComma = "-1";
         List<ProgramStage> programStages = new ArrayList<>();
         for(String psId : lookup.getValue().split(",")) {
         	ProgramStage ps = programStageService.getProgramStage( Integer.parseInt(psId) );
         	if( ps!= null && sectionCodes.contains( ps.getName().trim().toLowerCase()) ) {
         		programStages.add( ps );
         		psIdsByComma += ","+ps.getId();
         	}
         }
         lookup = lookupService.getLookupByName( "CAMPAIGN_SUBNATIONAL_DEID" );
         int subNationalDeId = Integer.parseInt( lookup.getValue() );
         lookup = lookupService.getLookupByName( "CAMPAIGN_STATUS_DEID" );
         int statusDeId = Integer.parseInt( lookup.getValue() );

         
         //Selected Columns
         campaignSnap.getSelCols().add("COL_1");
         campaignSnap.getSelCols().add("COL_5");
         campaignSnap.getSelCols().add("COL_3");
         Map<Integer, String> deColMap = new HashMap<>();
         String deIdsByComma = "-1";
         String psDeIdsByComma = "-1";
         psDeIdsByComma += "," +subNationalDeId;
         lookup = lookupService.getLookupByName( "CAMPAIGN_COLUMNS_INFO" );
         String campaignColInfo = lookup.getValue();
         //colList = new ArrayList<GenericTypeObj>();
         for( String colInfo : campaignColInfo.split("@!@") ) {
         	if( campaignSnap.getSelCols().contains( colInfo.split("@-@")[0] ) ) {
 	        	GenericTypeObj colObj = new GenericTypeObj();
 	        	colObj.setCode( colInfo.split("@-@")[0] );
 	        	colObj.setName( colInfo.split("@-@")[1] );
 	        	colObj.setStrAttrib1( colInfo.split("@-@")[2] ); //deids
 	        	colObj.setStrAttrib2( colInfo.split("@-@")[3] ); //ps deids
 	        	campaignSnap.getColList().add( colObj );
 	        	deIdsByComma += ","+colInfo.split("@-@")[2];
 	        	psDeIdsByComma += ","+colInfo.split("@-@")[3];
 	        	for(String deIdStr : colObj.getStrAttrib1().split(",") ) {
 	        		deColMap.put(Integer.parseInt(deIdStr), colObj.getCode());
 	        	}
 	        	deColMap.put(Integer.parseInt(colObj.getStrAttrib2()), colObj.getCode());
         	}
         }
         
         Set<Integer> deIds = new HashSet<Integer>( deColMap.keySet() );
         Map<Integer, String> deSectionMap = new HashMap<>();
         Map<Integer, Map<String, Integer>> sectionDeMap = new HashMap<>();
         for(Section section : campaignSnap.getDsSections() ) {
     		for( DataElement de : section.getDataElements()) {
     			if( deIds.contains(de.getId()) ) {
     				deSectionMap.put(de.getId(), section.getCode());
     				if( sectionDeMap.get( section.getId() ) == null)
     					sectionDeMap.put(section.getId(), new HashMap<>());
     				sectionDeMap.get( section.getId() ).put(deColMap.get(de.getId()), de.getId());
     			}
     		}
         }
         
         Map<Integer, Map<String, Integer>> psDeMap = new HashMap<>();
         for(ProgramStage ps : programStages ) {
     		for( DataElement de : ps.getAllDataElements()) {
     			if( deIds.contains(de.getId()) ) {
     				if( psDeMap.get( ps.getId() ) == null)
     					psDeMap.put(ps.getId(), new HashMap<>());
     				psDeMap.get( ps.getId() ).put(deColMap.get(de.getId()), de.getId());
     			}
     		}
         }                 
        
         Map<String, GenericDataVO> dvDataMap = getLatestDataValues( deIdsByComma, ouIdsByComma );
 		
         //Color Codes
         lookup = lookupService.getLookupByName( "CAMPAIGN_CALENDAR_COLOR_CODES" );
         try{ campaignSnap.setPlannedColor(lookup.getValue().split("@!@")[0]);}catch(Exception e) {}
         try{ campaignSnap.setPostponedColor(lookup.getValue().split("@!@")[1]);}catch(Exception e) {}
         try{ campaignSnap.setBothMatchColor(lookup.getValue().split("@!@")[2]);}catch(Exception e) {}
         
        
         lookup = lookupService.getLookupByName( "CAMPAIGN_CALENDAR_STATUS_COLOR_MAP" );
         String statusColorInfo = lookup.getValue();
         for(String statusColor : statusColorInfo.split("@!@") ) {
        	 campaignSnap.getStatusColorMap().put(statusColor.split("@-@")[0], statusColor.split("@-@")[1]);
         }
         
         SimpleDateFormat mdf = new SimpleDateFormat("MMM-yyyy");
         campaignSnap.setCcDataMap( new HashMap<>() );
         
         //Arranging aggregated data
         for( int ouId : campaignSnap.getOuIds() ) {
         	String key1 = ouId+"_National";
         	//dataMap.put(key1, new HashMap<>());
         	for(Section section : campaignSnap.getDsSections() ) {        		
         		CampaignVO cvo = new CampaignVO();
         		cvo.setColDataMap( new HashMap<>() );
         		int flag = 0;
         		
         		
         		        		        		        		
         		int plannedDeId = 0;
         		try{ plannedDeId = sectionDeMap.get( section.getId() ).get("COL_1"); }catch(Exception e) {}
         		int postponedDeId = 0;
         		try{ postponedDeId = sectionDeMap.get( section.getId() ).get("COL_5");}catch(Exception e) {}
         		
         		Date plannedDate = null;
         		String plannedVal = "";
         		try { plannedVal = dvDataMap.get(ouId+"_"+plannedDeId).getStrVal1();}catch(Exception e) {}
         		//System.out.println( "Planned: "+ouId+"_"+plannedDeId + " = " + plannedVal);
         		if( !plannedVal.trim().equals("") ) {
         			try{ plannedDate = getStartDateByString( plannedVal );}catch(Exception e) {}
         			if( plannedDate != null && (plannedDate.before( sDate ) || plannedDate.after( eDate )) )
         				plannedVal = "";
         			//System.out.println( "Planned Date = "+ plannedDate );
         			/*
         			if( plannedDate == null ) {
         				GenericDataVO dvo = new GenericDataVO();
         				dvo.setStrVal1( section.getCode() );
         				dvo.setStrVal2( plannedColor );
         				dvo.setStrVal3("pattern1");
         				dvo.setIntVal1(1);
         				cvo.getColDataMap().put("NONE1", dvo);
         				flag = 1;
         			}
         			*/
         		}
         		
         		Date postponedDate = null;
         		String postponedVal = "";
         		try { postponedVal = dvDataMap.get(ouId+"_"+postponedDeId).getStrVal1();}catch(Exception e) {}
         		//System.out.println( "Postponed: "+ ouId+"_"+postponedDeId + " = " + postponedVal);
         		if( !postponedVal.trim().equals("") ) {
         			try{ postponedDate = getStartDateByString( postponedVal );}catch(Exception e) {}
         			if( postponedDate != null && (postponedDate.before( sDate ) || postponedDate.after( eDate )) )
         				postponedVal = "";
         			//System.out.println( "Postponed Date = "+ postponedDate );
         			/*
         			if( postponedDate == null ) {
         				GenericDataVO dvo = new GenericDataVO();
         				dvo.setStrVal1( section.getCode() );
         				dvo.setStrVal2( postponedColor );
         				dvo.setStrVal3("pattern2");
         				dvo.setIntVal1(2);
         				cvo.getColDataMap().put("NONE2", dvo);
         				flag = 1;
         			}
         			*/        			
         		}
         		
         		if( !plannedVal.trim().equals("") && plannedDate == null && !postponedVal.trim().equals("") && postponedDate == null ) {
     				GenericDataVO dvo = new GenericDataVO();
     				dvo.setStrVal1( section.getCode() );
     				dvo.setStrVal2( campaignSnap.getBothMatchColor() );
     				dvo.setStrVal3("pattern3");
     				dvo.setIntVal1(3);
     				cvo.getColDataMap().put("NONE1", dvo);
     				flag = 1;	        				
         		}
         		else if( !plannedVal.trim().equals("") && plannedDate == null) {
         			GenericDataVO dvo = new GenericDataVO();
     				dvo.setStrVal1( section.getCode() );
     				dvo.setStrVal2( campaignSnap.getPlannedColor() );
     				dvo.setStrVal3("pattern1");
     				dvo.setIntVal1(1);
     				cvo.getColDataMap().put("NONE1", dvo);
     				flag = 1;
         		}
         		else if( !postponedVal.trim().equals("") && postponedDate == null ) {
         			GenericDataVO dvo = new GenericDataVO();
     				dvo.setStrVal1( section.getCode() );
     				dvo.setStrVal2( campaignSnap.getPostponedColor() );
     				dvo.setStrVal3("pattern2");
     				dvo.setIntVal1(2);
     				cvo.getColDataMap().put("NONE2", dvo);
     				flag = 1;
         		}
         		else if( plannedDate != null && postponedDate != null && plannedDate.equals(postponedDate) ) {
         			if( (plannedDate.before( sDate ) || plannedDate.after( eDate )) && 
         					(postponedDate.before( sDate ) || postponedDate.after( eDate ))) {
         			
         			}
         			else {
 	        			String monthName = mdf.format( postponedDate ); 
 	    				GenericDataVO dvo = new GenericDataVO();
 	    				dvo.setStrVal1( section.getCode() );
 	    				dvo.setStrVal2( campaignSnap.getBothMatchColor() );
 	    				dvo.setStrVal3("pattern3");
 	    				dvo.setIntVal1(3);
 	    				cvo.getColDataMap().put(monthName, dvo);
 	    				flag = 1;
         			}
         		}
         		else {
         			if( plannedDate != null ) {
         				if( plannedDate.before( sDate ) || plannedDate.after( eDate )) {}
         				else {
 	        				String monthName = mdf.format( plannedDate ); 
 	        				GenericDataVO dvo = new GenericDataVO();
 	        				dvo.setStrVal1( section.getCode() );
 	        				dvo.setStrVal2( campaignSnap.getPlannedColor() );
 	        				dvo.setStrVal3("pattern1");
 	        				dvo.setIntVal1(1);
 	        				cvo.getColDataMap().put(monthName, dvo);
 	        				flag = 1;
         				}
         			}
         			
         			if( postponedDate != null ) {
         				if( postponedDate.before( sDate ) || postponedDate.after( eDate )) {}
         				else {
 	        				String monthName = mdf.format( postponedDate ); 
 	        				GenericDataVO dvo = new GenericDataVO();
 	        				dvo.setStrVal1( section.getCode() );
 	        				dvo.setStrVal2( campaignSnap.getPostponedColor() );
 	        				dvo.setStrVal3("pattern2");
 	        				dvo.setIntVal1(2);
 	        				cvo.getColDataMap().put(monthName, dvo);
 	        				flag = 1;
         				}
         			}
         		}
         		
         		if( flag == 1 ) {
         			int aggStatusDeId = 0;
             		try{ aggStatusDeId = sectionDeMap.get( section.getId() ).get("COL_3"); }catch(Exception e) {}
             		String statusVal = "";
             		try { 
             			statusVal = dvDataMap.get(ouId+"_"+aggStatusDeId).getStrVal1();
             			cvo.getColDataMap().put("STATUS", dvDataMap.get(ouId+"_"+aggStatusDeId) );
             		}catch(Exception e) {}
             		
     				if( campaignSnap.getCcDataMap().get(key1) == null )
     					campaignSnap.getCcDataMap().put(key1, new ArrayList<>());
     				campaignSnap.getCcDataMap().get(key1).add( cvo );
         		}
         	}
         }

         
         //Arranging event data
         Set<String> subNationalNames = new HashSet<>();
         subNationalNames.add("National");
         Map<String, Map<Integer, CampaignVO>> eventDataMap = getEventData( psIdsByComma, psDeIdsByComma, ouIdsByComma );
         for( int ouId : campaignSnap.getOuIds() ) {
         	for(ProgramStage ps : programStages ) {
         		String eBaseKey = ps.getId()+"_"+ouId;
         		//System.out.println( "1. " + eBaseKey );
         		if( eventDataMap.get(eBaseKey) == null ) {
         			continue;
         		}
         		for(Integer psInsId : eventDataMap.get(eBaseKey).keySet()) {
         			CampaignVO cvo = new CampaignVO();
         			cvo.setColDataMap( new HashMap<>() );
         			int flag = 0;
         			
         			String subNationName = "National";
         			try { subNationName = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(subNationalDeId+"").getStrVal1();}catch(Exception e) {}
         			subNationalNames.add(subNationName);
         			String key1 = ouId+"_"+subNationName;
         			//System.out.println( "2. " + eBaseKey + "  " + subNationName );
         			            		            		            		
             		//if( dataMap.get(key1) == null)
             		//	dataMap.put(key1, new HashMap<>());
 	        		int plannedDeId = 0;
 	        		try{ plannedDeId = psDeMap.get( ps.getId() ).get("COL_1"); }catch(Exception e) {}
 	        		int postponedDeId = 0;
 	        		try{ postponedDeId = psDeMap.get( ps.getId() ).get("COL_5");}catch(Exception e) {}
         		
 	        		Date plannedDate = null;
 	        		String plannedVal = "";
 	        		try { plannedVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(plannedDeId+"").getStrVal1();}catch(Exception e) {}
 	        		//System.out.println( "Planned: "+eBaseKey+"_"+plannedDeId + " = " + plannedVal);
 	        		if( !plannedVal.trim().equals("") ) {
 	        			try{ plannedDate = getStartDateByString( plannedVal );}catch(Exception e) {}
 	        			if( plannedDate != null && (plannedDate.before( sDate ) || plannedDate.after( eDate )) )
 	        				plannedVal = "";
 	        			//System.out.println( "Planned Date = "+ plannedDate );
 	        			/*
 	        			if( plannedDate == null ) {
 	        				GenericDataVO dvo = new GenericDataVO();
 	        				dvo.setStrVal1( ps.getName() );
 	        				dvo.setStrVal2( plannedColor );
 	        				dvo.setStrVal3("pattern1");
 	        				dvo.setIntVal1(1);
 	        				cvo.getColDataMap().put("NONE1", dvo);
 	        				flag = 1;	        				
 	        			} 
 	        			*/       			
 	        		}
         		
 	        		Date postponedDate = null;
 	        		String postponedVal = "";
 	        		try { postponedVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(postponedDeId+"").getStrVal1();}catch(Exception e) {}
 	        		//System.out.println( "Postponed: "+ eBaseKey+"_"+postponedDeId + " = " + postponedVal);
 	        		if( !postponedVal.trim().equals("") ) {
 	        			try{ postponedDate = getStartDateByString( postponedVal );}catch(Exception e) {}
 	        			if( postponedDate != null && (postponedDate.before( sDate ) || postponedDate.after( eDate )) )
 	        				postponedVal = "";
 	        			//System.out.println( "Postponed Date = "+ postponedDate );
 	        			/*
 	        			if( postponedDate == null ) {
 	        				GenericDataVO dvo = new GenericDataVO();
 	        				dvo.setStrVal1( ps.getName() );
 	        				dvo.setStrVal2( postponedColor );
 	        				dvo.setStrVal3("pattern2");
 	        				dvo.setIntVal1(2);
 	        				cvo.getColDataMap().put("NONE2", dvo);
 	        				flag = 1;	        				
 	        			}
 	        			*/        			
 	        		}
 	        		
 	        		if( !plannedVal.trim().equals("") && plannedDate == null && !postponedVal.trim().equals("") && postponedDate == null ) {
         				GenericDataVO dvo = new GenericDataVO();
         				dvo.setStrVal1( ps.getName() );
         				dvo.setStrVal2( campaignSnap.getBothMatchColor() );
         				dvo.setStrVal3("pattern3");
         				dvo.setIntVal1(3);
         				cvo.getColDataMap().put("NONE1", dvo);
         				flag = 1;	        				
 	        		}
 	        		else if( !plannedVal.trim().equals("") && plannedDate == null) {
 	        			GenericDataVO dvo = new GenericDataVO();
         				dvo.setStrVal1( ps.getName() );
         				dvo.setStrVal2( campaignSnap.getPlannedColor() );
         				dvo.setStrVal3("pattern1");
         				dvo.setIntVal1(1);
         				cvo.getColDataMap().put("NONE1", dvo);
         				flag = 1;
 	        		}
 	        		else if( !postponedVal.trim().equals("") && postponedDate == null ) {
 	        			GenericDataVO dvo = new GenericDataVO();
         				dvo.setStrVal1( ps.getName() );
         				dvo.setStrVal2( campaignSnap.getPostponedColor() );
         				dvo.setStrVal3("pattern2");
         				dvo.setIntVal1(2);
         				cvo.getColDataMap().put("NONE2", dvo);
         				flag = 1;
 	        		}
 	        		else if( plannedDate != null && postponedDate != null && plannedDate.equals(postponedDate) ) {
 	        			if( (plannedDate.before( sDate ) || plannedDate.after( eDate )) && 
 	        					(postponedDate.before( sDate ) || postponedDate.after( eDate ))) {
 	        			
 	        			}
 	        			else {
 		        			String monthName = mdf.format( postponedDate ); 
 		    				GenericDataVO dvo = new GenericDataVO();
 		    				dvo.setStrVal1( ps.getName() );
 		    				dvo.setStrVal2( campaignSnap.getBothMatchColor() );
 		    				dvo.setStrVal3("pattern3");
 		    				dvo.setIntVal1(3);
 		    				cvo.getColDataMap().put(monthName, dvo);
 		    				flag = 1;
 	        			}
 	        		}
 	        		else {
 	        			if( plannedDate != null ) {
 	        				if( plannedDate.before( sDate ) || plannedDate.after( eDate )) {}
 	        				else {
 		        				String monthName = mdf.format( plannedDate ); 
 		        				GenericDataVO dvo = new GenericDataVO();
 		        				dvo.setStrVal1( ps.getName() );
 		        				dvo.setStrVal2( campaignSnap.getPlannedColor() );
 		        				dvo.setStrVal3("pattern1");
 		        				dvo.setIntVal1(1);
 		        				cvo.getColDataMap().put(monthName, dvo);
 		        				flag = 1;
 	        				}
 	        			}
         			
 	        			if( postponedDate != null ) {
 	        				if( postponedDate.before( sDate ) || postponedDate.after( eDate )) {}
 	        				else {
 		        				String monthName = mdf.format( postponedDate ); 
 		        				GenericDataVO dvo = new GenericDataVO();
 		        				dvo.setStrVal1( ps.getName() );
 		        				dvo.setStrVal2( campaignSnap.getPostponedColor() );
 		        				dvo.setStrVal3("pattern2");
 		        				dvo.setIntVal1(2);
 		        				cvo.getColDataMap().put(monthName, dvo);
 		        				flag = 1;
 	        				}
 	        			}
 	        		}
 	        		
 	        		if( flag == 1 ) {
 	        			String statusVal = "";
 	            		try { 
 	            			statusVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(statusDeId+"").getStrVal1();
 	            			cvo.getColDataMap().put("STATUS", eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(statusDeId+"") );
 	            		}catch(Exception e) {}
 	            		
 	    				if( campaignSnap.getCcDataMap().get(key1) == null )
 	    					campaignSnap.getCcDataMap().put(key1, new ArrayList<>());
 	    				campaignSnap.getCcDataMap().get(key1).add( cvo );
 	        		}
         		}//psi for loop
         		
         	}//ps for loop
         }//ou for loop

         

         Calendar fromCal = Calendar.getInstance();
         fromCal.setTime( sDate );
         
         Calendar toCal = Calendar.getInstance();
         toCal.setTime( eDate );
         
         
         while( fromCal.before( toCal) || fromCal.equals(toCal) ) {
        	 campaignSnap.getMonthNames().add( mdf.format(fromCal.getTime()) );
         	fromCal.add(Calendar.MONTH, 1);
         }
         
         subNationalNames.remove("National");
         campaignSnap.setSubNatNames( new ArrayList<>() );
         campaignSnap.getSubNatNames().addAll( subNationalNames );
         Collections.sort( campaignSnap.getSubNatNames() );
         campaignSnap.getSubNatNames().add(0, "National");
         
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         Date curDate = new Date();
         campaignSnap.setCurDateStr( sdf.format( curDate ) );
         
    	return campaignSnap;
    }

    public CampaignSnapshot getCampainDashboardSnap(CampaignSnapshot campaignSnap )
    {
    	Lookup lookup = lookupService.getLookupByName( Lookup.WHO_REGIONS_GROUPSET );
    	OrganisationUnitGroupSet whoRegionsGroupSet = ouGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) );
    	Collection<OrganisationUnit> whoOrgUnits = new ArrayList<>( whoRegionsGroupSet.getOrganisationUnits() );
    	Collection<Integer> ouIds = new ArrayList<>( getIdentifiers( whoOrgUnits ) );
    	String ouIdsByComma = "-1";
    	if ( ouIds.size() > 0 ){
        	ouIdsByComma = getCommaDelimitedString( ouIds );
        }
    	List<OrganisationUnitGroup> ouGroups = new ArrayList<>();
    	ouGroups.addAll( whoRegionsGroupSet.getOrganisationUnitGroups() );
    	Collections.sort( ouGroups, new IdentifiableObjectNameComparator() );
    	campaignSnap.setOuGroups( ouGroups );
    	
    	lookup = lookupService.getLookupByName( "CAMPAIGN_DATASET_UID" );
        DataSet dataSet = dataSetService.getDataSet( lookup.getValue() );
        campaignSnap.getDsSections().addAll( dataSet.getSections() );
       
        Set<String> sectionCodes = new HashSet<>();
        for( Section section : campaignSnap.getDsSections() ){
            sectionCodes.add( section.getCode().trim().toLowerCase() );
        }
        lookup = lookupService.getLookupByName( "CAMPAIGN_PROGRAM_STAGE_IDS" );
        String psIdsByComma = "-1";
        List<ProgramStage> programStages = new ArrayList<>();
        for(String psId : lookup.getValue().split(",")) {
        	ProgramStage ps = programStageService.getProgramStage( Integer.parseInt(psId) );
        	if( ps!= null && sectionCodes.contains( ps.getName().trim().toLowerCase()) ) {
        		programStages.add( ps );
        		psIdsByComma += ","+ps.getId();
        	}
        }
        lookup = lookupService.getLookupByName( "CAMPAIGN_SUBNATIONAL_DEID" );
        int subNationalDeId = Integer.parseInt( lookup.getValue() );
        lookup = lookupService.getLookupByName( "CAMPAIGN_STATUS_DEID" );
        int statusDeId = Integer.parseInt( lookup.getValue() );

        
        //Selected Columns
        //campaignSnap.getSelCols().add("COL_1");
        //campaignSnap.getSelCols().add("COL_5");
        campaignSnap.getSelCols().add("COL_3");
        Map<Integer, String> deColMap = new HashMap<>();
        String deIdsByComma = "-1";
        String psDeIdsByComma = "-1";
        psDeIdsByComma += "," +subNationalDeId;
        lookup = lookupService.getLookupByName( "CAMPAIGN_COLUMNS_INFO" );
        String campaignColInfo = lookup.getValue();
        //colList = new ArrayList<GenericTypeObj>();
        for( String colInfo : campaignColInfo.split("@!@") ) {
        	if( campaignSnap.getSelCols().contains( colInfo.split("@-@")[0] ) ) {
	        	GenericTypeObj colObj = new GenericTypeObj();
	        	colObj.setCode( colInfo.split("@-@")[0] );
	        	colObj.setName( colInfo.split("@-@")[1] );
	        	colObj.setStrAttrib1( colInfo.split("@-@")[2] ); //deids
	        	colObj.setStrAttrib2( colInfo.split("@-@")[3] ); //ps deids
	        	campaignSnap.getColList().add( colObj );
	        	deIdsByComma += ","+colInfo.split("@-@")[2];
	        	psDeIdsByComma += ","+colInfo.split("@-@")[3];
	        	for(String deIdStr : colObj.getStrAttrib1().split(",") ) {
	        		deColMap.put(Integer.parseInt(deIdStr), colObj.getCode());
	        	}
	        	deColMap.put(Integer.parseInt(colObj.getStrAttrib2()), colObj.getCode());
        	}
        }
        
        Set<Integer> deIds = new HashSet<Integer>( deColMap.keySet() );
        Map<Integer, String> deSectionMap = new HashMap<>();
        Map<Integer, Map<String, Integer>> sectionDeMap = new HashMap<>();
        for(Section section : campaignSnap.getDsSections() ) {
    		for( DataElement de : section.getDataElements()) {
    			if( deIds.contains(de.getId()) ) {
    				deSectionMap.put(de.getId(), section.getCode());
    				if( sectionDeMap.get( section.getId() ) == null)
    					sectionDeMap.put(section.getId(), new HashMap<>());
    				sectionDeMap.get( section.getId() ).put(deColMap.get(de.getId()), de.getId());
    			}
    		}
        }
        
        Map<Integer, Map<String, Integer>> psDeMap = new HashMap<>();
        for(ProgramStage ps : programStages ) {
    		for( DataElement de : ps.getAllDataElements()) {
    			if( deIds.contains(de.getId()) ) {
    				if( psDeMap.get( ps.getId() ) == null)
    					psDeMap.put(ps.getId(), new HashMap<>());
    				psDeMap.get( ps.getId() ).put(deColMap.get(de.getId()), de.getId());
    			}
    		}
        }                 
        
        lookup = lookupService.getLookupByName( "CAMPAIGN_DB_STATUS_VALS" );
        Set<String> statusValues = new HashSet<>();
        for(String statusVal : lookup.getValue().split("@!@")) {
        	statusValues.add( statusVal );
        }
        
        lookup = lookupService.getLookupByName( "CAMPAIGN_DB_ROW_INFO" );
        String campaignRowInfo = lookup.getValue();
        List<GenericTypeObj> rowObjList = new ArrayList<>();
        Map<String, GenericTypeObj> rowObjMap = new HashMap<>();
        Map<Integer, String> sectionRowMap = new HashMap<>();
        Map<Integer, String> psRowMap = new HashMap<>();
        for( String rowInfo : campaignRowInfo.split("@!@") ) {
        	GenericTypeObj rowObj = new GenericTypeObj();
        	rowObj.setCode( rowInfo.split("@-@")[0] );
        	rowObj.setName( rowInfo.split("@-@")[1] );
        	rowObj.setStrAttrib1( rowInfo.split("@-@")[2] ); //section ids
        	rowObj.setStrAttrib2( rowInfo.split("@-@")[3] ); //program stage ids
        	rowObjList.add( rowObj );
        	rowObjMap.put( rowObj.getCode(), rowObj );
        	
        	for(String sectionIdStr : rowObj.getStrAttrib1().split(",") ) {
        		sectionRowMap.put(Integer.parseInt(sectionIdStr), rowObj.getCode());
        	}
        	for(String psIdStr : rowObj.getStrAttrib2().split(",") ) {
        		psRowMap.put(Integer.parseInt(psIdStr), rowObj.getCode());
        	}
        }
        
        GenericTypeObj rowObj = new GenericTypeObj();
    	rowObj.setCode( "GTOTAL" );
    	rowObj.setName( "Overall Total<br/>* please note some countries may be double-counted" );
    	rowObjList.add( rowObj );
        campaignSnap.setRowObjList( rowObjList );
        
		/*
		 * for( Integer key : sectionRowMap.keySet() ) { System.out.println( key + "  "
		 * + sectionRowMap.get(key) ); }
		 */
        
        Map<String, GenericDataVO> dvDataMap = getLatestDataValuesForCampaignReport( deIdsByComma, ouIdsByComma );
        int ALL_OUGROUP_ID = -1;
        HashMap<String, Integer> dataMap = new HashMap<>();
        //Arranging aggregated data
        for( OrganisationUnit ou : whoOrgUnits ) {
        	int ouGroupId = 0;
        	try{ ouGroupId = ou.getGroupInGroupSet( whoRegionsGroupSet ).getId(); }catch(Exception e) {}
        	if( ouGroupId == 0)
        		continue;
        	for(Section section : campaignSnap.getDsSections() ) { 
        		int aggStatusDeId = 0;
         		try{ aggStatusDeId = sectionDeMap.get( section.getId() ).get("COL_3"); }catch(Exception e) {}
         		String statusVal = "";
         		try{ statusVal = dvDataMap.get(ou.getId()+"_"+aggStatusDeId).getStrVal1(); }catch(Exception e) {}
         		
         		//System.out.println( ouGroupId + "_" + section.getId() + "_" + section.getName() +"  = " +sectionRowMap.get(section.getId()) );
         		
         		if( statusValues.contains( statusVal ) && sectionRowMap.get(section.getId()) != null ) {
             		//try{ System.out.println( ouGroupId + "_" +rowObjMap.get( sectionRowMap.get(section.getId()) ).getCode() + " = " + statusVal );}catch(Exception e) {}

         			String ougKey = ouGroupId+"_"+ rowObjMap.get( sectionRowMap.get(section.getId()) ).getCode();
         			if( dataMap.get( ougKey ) == null ) {
         				dataMap.put( ougKey, 1);         				
         			}
         			else {
         				dataMap.put( ougKey, dataMap.get(ougKey)+1 );
         			}
         			
         			String allOugKey = ALL_OUGROUP_ID+"_"+ rowObjMap.get( sectionRowMap.get(section.getId()) ).getCode();
         			if( dataMap.get( allOugKey ) == null ) {
         				dataMap.put( allOugKey, 1);
         			}
         			else {
         				dataMap.put( allOugKey, dataMap.get(allOugKey)+1 );
         			}
         			
         			//Grand Total         			
     				String tempKey = ouGroupId+"_GTOTAL";
     				if( dataMap.get( tempKey ) == null ) 
     					dataMap.put( tempKey, 1);
     				else
     					dataMap.put( tempKey, dataMap.get(tempKey)+1 );
     				
     				tempKey = ALL_OUGROUP_ID+"_GTOTAL";
     				if( dataMap.get( tempKey ) == null ) 
     					dataMap.put( tempKey, 1);
     				else
     					dataMap.put( tempKey, dataMap.get(tempKey)+1 );         			
         		}
        	}
        }
        
        //TODO-Event Data
        //Arranging event data
        //Set<String> subNationalNames = new HashSet<>();
        //subNationalNames.add("National");
        Map<String, Map<Integer, CampaignVO>> eventDataMap = getEventData( psIdsByComma, psDeIdsByComma, ouIdsByComma );
        for( OrganisationUnit ou : whoOrgUnits ) {
        	int ouGroupId = 0;
        	try{ ouGroupId = ou.getGroupInGroupSet( whoRegionsGroupSet ).getId(); }catch(Exception e) {}
        	if( ouGroupId == 0)
        		continue;
        	for(ProgramStage ps : programStages ) {
        		String eBaseKey = ps.getId()+"_"+ou.getId();
        		//System.out.println( "1. " + eBaseKey );
        		if( eventDataMap.get(eBaseKey) == null ) {
        			continue;
        		}
        		for(Integer psInsId : eventDataMap.get(eBaseKey).keySet()) {
        			String statusVal = "";
         			try { statusVal = eventDataMap.get(eBaseKey).get(psInsId).getColDataMap().get(statusDeId+"").getStrVal1();}catch(Exception e) {}
         			if( statusValues.contains( statusVal ) && psRowMap.get(ps.getId()) != null ) {
                 		//try{ System.out.println( ouGroupId + "_" +rowObjMap.get( sectionRowMap.get(section.getId()) ).getCode() + " = " + statusVal );}catch(Exception e) {}

             			String ougKey = ouGroupId+"_"+ rowObjMap.get( psRowMap.get(ps.getId()) ).getCode();
             			if( dataMap.get( ougKey ) == null )
             				dataMap.put( ougKey, 1);
             			else {
             				dataMap.put( ougKey, dataMap.get(ougKey)+1 );
             			}
             			
             			String allOugKey = ALL_OUGROUP_ID+"_"+ rowObjMap.get( psRowMap.get(ps.getId()) ).getCode();
             			if( dataMap.get( allOugKey ) == null )
             				dataMap.put( allOugKey, 1);
             			else {
             				dataMap.put( allOugKey, dataMap.get(allOugKey)+1 );
             			}
             			
             			//Grand Total         			
         				String tempKey = ouGroupId+"_GTOTAL";
         				if( dataMap.get( tempKey ) == null ) 
         					dataMap.put( tempKey, 1);
         				else
         					dataMap.put( tempKey, dataMap.get(tempKey)+1 );
         				
         				tempKey = ALL_OUGROUP_ID+"_GTOTAL";
         				if( dataMap.get( tempKey ) == null ) 
         					dataMap.put( tempKey, 1);
         				else
         					dataMap.put( tempKey, dataMap.get(tempKey)+1 );
             		}
        		}
        	}
        }
        
        campaignSnap.setCdbDataMap( dataMap );
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date();
        campaignSnap.setCurDateStr( sdf.format( curDate ) );
        
        return campaignSnap;
    }
    
    private Date getStartDateByString( String dateStr )
    {        
    	String startDate = "";
        String[] startDateParts = dateStr.split( "-" );
        if( dateStr.trim().equalsIgnoreCase("TBD") || startDateParts.length <= 1 ){
            return null;
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q1" ) ){
            startDate = startDateParts[0] + "-01-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q2" ) ){
            startDate = startDateParts[0] + "-04-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q3" ) ){
            startDate = startDateParts[0] + "-07-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q4" ) ){
            startDate = startDateParts[0] + "-10-01";
        }
        else{
            startDate = startDateParts[0] + "-" + startDateParts[1] + "-01";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = null;
        try{ sDate = sdf.parse( startDate );}catch(Exception e) {}

        return sDate;
    }
    
    private Date getEndDateByString( String dateStr )
    {
        String endDate = "";
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String[] endDateParts = dateStr.split( "-" );
        if ( endDateParts.length <= 1 ){
            endDate = endDateParts[0] + "-12-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q1" ) ){
            endDate = endDateParts[0] + "-03-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q2" ) ){
            endDate = endDateParts[0] + "-06-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q3" ) ){
            endDate = endDateParts[0] + "-09-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q4" ) ){
            endDate = endDateParts[0] + "-12-31";
        }
        else{
            if ( Integer.parseInt( endDateParts[0] ) % 400 == 0 ){
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-" + (monthDays[Integer.parseInt( endDateParts[1] )] + 1);
            }
            else{
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-" + (monthDays[Integer.parseInt( endDateParts[1] )]);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date eDate = null;
        try{ eDate = sdf.parse( endDate ); }catch(Exception e) {}

        return eDate;
    }
}