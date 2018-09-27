package org.hisp.dhis.ivb.isc;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author BHARATH
 */

@Transactional
public class ISCReportHelper
{
	public static final String REPORT_TEMPLATE_FOLDER = "report_templates";
	public static final String REPORT_TEMPLATE_ISC_SINGLE_COUNTRY = "ISC_SingleCountry.xlsx";
	
	private static final int processScoreCardDeIds[][] = {
				{1181662,1181663,1181664,1181665}, //Leadership
				{1181666,1181667,1181668,1181669}, //Continuous Improvement Plans
				{1181670,1181671,1181672,1181673}, //Data for Management
				{1181674,1181675,1181676,1181677}, //Cold Chain Equipment
				{1181678,1181679,1181680,1181681} //System Design
			};				
	
	private static final int pdbStatusDeId = 1181682;
	
	private static final int preformanceDashboardDeIds[][] = {
				{1181683,1181684,1181685,1181686,1181687,1181688,1181689,1181690,1181691},
				{1181692,1181693,1181694,1181695,1181696,1181697,1181698,1181699,1181700}
			};
	
	private static final String deIdsByComma = "1181662,1181663,1181664,1181665,1181666,1181667,1181668,1181669,1181670,1181671,1181672,1181673,1181674,1181675,1181676,1181677,1181678,1181679,1181680,1181681,1181682,1181683,1181684,1181685,1181686,1181687,1181688,1181689,1181690,1181691,1181692,1181693,1181694,1181695,1181696,1181697,1181698,1181699,1181700";
	
	private static Map<String, Integer> levelColMap;
	static {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		
		tempMap.put("Level 1", 2);
		tempMap.put("Level 2", 3);
		tempMap.put("Level 3", 4);
		tempMap.put("Level 4", 5);
		tempMap.put("Level 5", 6);
		
		levelColMap = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String,String> processScoreCardTextIndexMap;
	static {
		Map<String, String> tempMap2 = new HashMap<String, String>();
		
		//Leadership 
		tempMap2.put("6:2", "N-0-45;B-45-57;N-57-113"); //Level1
		tempMap2.put("6:3", "N-0-55;B-55-65;N-65-79"); //Level2
		tempMap2.put("6:4", "B-0-11;N-11-115"); //Level3
		tempMap2.put("6:5", "N-0-22;B-22-31;N-31-69;B-69-106;N-106-140"); //Level4
		tempMap2.put("6:6", "N-0-34;B-34-49;N-49-129"); //Level5
		
		//Continuous Improvement Plans
		//tempMap2.put("7:2", "N-0-32;B-32-44;N-44-100"); //Level1
		tempMap2.put("7:2", "N-0-32;B-32-37;N-37-60"); //Level1
		tempMap2.put("7:3", "N-0-20;B-20-28;N-28-48;B-48-62;N-62-99"); //Level2
		tempMap2.put("7:4", "N-0-30;B-30-43;N-43-88;B-88-114"); //Level3
		tempMap2.put("7:5", "N-0-40;B-40-53;N-53-101;B-101-111;N-111-154"); //Level4
		tempMap2.put("7:6", "N-0-20;B-20-44;N-44-49;B-49-67;N-67-152;B-152-168;N-168-236"); //Level5
		
		//Data for Management
		tempMap2.put("8:2", "N-0-25;B-25-30;N-30-149"); //Level1
		tempMap2.put("8:3", "N-0-55;B-55-68;N-68-102;B-102-118;N-118-149"); //Level2
		tempMap2.put("8:4", "N-0-70;B-70-79"); //Level3
		tempMap2.put("8:5", "N-0-55;B-55-70;N-70-91"); //Level4
		tempMap2.put("8:6", "N-0-11;B-11-30;N-30-135"); //Level5
		
		//Cold Chain Equipment
		tempMap2.put("9:2", "N-0-19;B-19-26;N-26-40;B-40-45;N-45-55;B-55-83;N-83-96;B-96-102"); //Level1
		tempMap2.put("9:3", "N-0-19;B-19-24;N-24-34;B-34-60;N-60-119"); //Level2
		tempMap2.put("9:4", "N-0-12;B-12-22;N-22-114;B-114-121;N-121-151"); //Level3
		tempMap2.put("9:5", "N-0-59;B-59-69;N-69-220"); //Level4
		tempMap2.put("9:6", "N-0-12;B-12-35;N-35-94;B-94-141;N-141-150"); //Level5
		
		//System Design
		tempMap2.put("10:2", "N-0-23;B-23-34;N-34-108"); //Level1
		tempMap2.put("10:3", "N-0-4;B-4-20;N-20-131"); //Level2
		tempMap2.put("10:4", "N-0-67;B-67-77;N-77-99;B-99-124"); //Level3
		tempMap2.put("10:5", "N-0-4;B-4-14;N-14-46;B-46-58"); //Level4
		tempMap2.put("10:6", "N-0-58;B-58-77;N-77-85"); //Level5

		processScoreCardTextIndexMap = Collections.unmodifiableMap(tempMap2);
	}
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate ){
        this.jdbcTemplate = jdbcTemplate;
    }
    
	@Autowired
    private IVBUtil ivbUtil;
    
    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------

    
    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------
    public Workbook generateISCExcel( OrganisationUnit orgUnit )
    {
    	Map<String, DataValue> dataValueMap = new HashMap<String, DataValue>();
    	
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	
    	try {    		    		 
    		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy");
    		
    		int ouId = orgUnit.getId();
    		dataValueMap = ivbUtil.getLatestDataValuesForTabularReport( deIdsByComma, ouId+"" );
    		
	    	String templatePath = System.getenv( "DHIS2_HOME" ) + File.separator + REPORT_TEMPLATE_FOLDER + File.separator + REPORT_TEMPLATE_ISC_SINGLE_COUNTRY;
	    	
	    	FileInputStream templateExcelFile = new FileInputStream(new File(templatePath));
	    	workbook = new XSSFWorkbook(templateExcelFile);
	    	XSSFSheet sheet0 = workbook.getSheetAt(0);
	    	XSSFSheet sheet1 = workbook.getSheetAt(1);

	    	
	    	XSSFRow row = sheet0.getRow( 6 );
	    	XSSFCell cell = row.getCell( 2 );
	    	XSSFCellStyle defaultCellStyle = cell.getCellStyle();
        	

	    	XSSFFont font1 = workbook.createFont();
	    	font1.setFontName( "Calibri" );
	    	font1.setFontHeightInPoints( (short) 9 );
	    	font1.setItalic( false );
	    	font1.setColor( IndexedColors.BLACK.index );
	    	
	    	XSSFFont font2 = workbook.createFont();
	    	font2.setFontName( "Calibri" );
	    	font2.setFontHeightInPoints( (short) 9 );
	    	font2.setItalic( false );
	    	font2.setBold( true );
	    	font2.setColor( IndexedColors.BLACK.index );
	    		    	
	    	
	    	XSSFCellStyle baseLevelCellStyle = (XSSFCellStyle) defaultCellStyle.clone();
	    	baseLevelCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(216, 228, 188)));
	    	baseLevelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    	
	    	XSSFCellStyle currentLevelCellStyle = (XSSFCellStyle) defaultCellStyle.clone();
	    	currentLevelCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(196, 215, 155)));
	    	currentLevelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    	
	    	XSSFCellStyle targetLevelCellStyle = (XSSFCellStyle) defaultCellStyle.clone();
	    	targetLevelCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(155, 187, 89)));
	    	targetLevelCellStyle.setFillPattern(FillPatternType.THIN_FORWARD_DIAG);
	        
	    	
	    	XSSFCellStyle[] levelStyles = new XSSFCellStyle[] { baseLevelCellStyle, currentLevelCellStyle, targetLevelCellStyle };
	    	
	    	//Sheet0: Process Scorecard - Country Name
	    	row = sheet0.getRow( 3 );
	    	cell = row.getCell( 2 );
	        cell.setCellValue( orgUnit.getName() );

	        int rowNo = 6;
	        for( int i = 0; i < processScoreCardDeIds.length; i++ ) {
	        	row = sheet0.getRow( rowNo+i );
	        	//System.out.println( "Row = "+ (rowNo+i+1));
	        	for( int i2 = 0; i2 < 3; i2++) {
			        DataValue dv = dataValueMap.get( ouId+":"+processScoreCardDeIds[i][i2] );
			        if( dv != null && dv.getValue() != null ) {
			        	cell = row.getCell( levelColMap.get(dv.getValue()) );
			        	XSSFRichTextString richText = cell.getRichStringCellValue();		        	
			        	String tempKey = cell.getRowIndex()+":"+cell.getColumnIndex();
			        	//System.out.println( "richText = " + richText );
			        	//System.out.println( tempKey + ",   " + processScoreCardTextIndexMap.get( tempKey ) );
			        	if( processScoreCardTextIndexMap.get( tempKey ) != null ) {
			        		String richTextIndexes = processScoreCardTextIndexMap.get( tempKey );
			        		for( int j = 0; j < richTextIndexes.split(";").length; j++) {
			        			String styleCat = richTextIndexes.split(";")[j].split("-")[0];
			        			int sIndex = Integer.parseInt( richTextIndexes.split(";")[j].split("-")[1] );
			        			int eIndex = Integer.parseInt( richTextIndexes.split(";")[j].split("-")[2] );
			        			
			        			if( styleCat.equals("N") ) {
				        			richText.applyFont(sIndex, eIndex, font1);		        				
			        			}
			        			else {
				        			richText.applyFont(sIndex, eIndex, font2);
			        			}
			        		}
			        	}			        				        	
			        	cell.setCellValue( richText );
			        	cell.setCellStyle( levelStyles[i2] );
			        }
	        	}
	        		        	
	        	DataValue dv = dataValueMap.get( ouId+":"+processScoreCardDeIds[i][3] );
		        if( dv != null && dv.getValue() != null ) {
		        	cell = row.getCell( 7 );
		        	cell.setCellValue( dv.getValue() );
		        }
		        //System.out.println("Degree: "+ ((dv != null)? dv.getValue() : "no data"));
	        }
	        
	        //---------------------------------------------------------------------------------------
	        //Sheet1: Performance Dashboard
	        //---------------------------------------------------------------------------------------
	        //Country Name
	    	row = sheet1.getRow( 3 );
	    	cell = row.getCell( 2 );
	        cell.setCellValue( orgUnit.getName() );
	        /*cell = row.getCell( 4 );
	        DataValue dv = dataValueMap.get( ouId+":"+pdbStatusDeId );
	        if( dv != null && dv.getValue() != null ) {
	        	cell.setCellValue( dv.getValue() );
	        }*/
	        
	        row = sheet1.getRow( 5 );
	    	cell = row.getCell( 4 );
	    	DataValue dv = dataValueMap.get( ouId+":"+pdbStatusDeId );
	        if( dv != null && dv.getValue() != null ) {
	        	cell.setCellValue( dv.getValue() );
	        }
	        //System.out.println( "Status dv value = " + dv.getValue() );
	        
	        rowNo = 8;
	        for( int i=0; i < preformanceDashboardDeIds.length; i++ ) {
	        	row = sheet1.getRow( ++rowNo+i );
	        	
	        	//Time Frame
	        	String timeFrame = "", monYearCollected = "NA";
	        	cell = row.getCell( 2 );
		        dv = dataValueMap.get( ouId+":"+preformanceDashboardDeIds[i][0] );
		        if( dv != null && dv.getValue() != null ) {
		        	timeFrame = dv.getValue();
		        	monYearCollected = monthFormat.format( dv.getLastUpdated() );
		        }
		        else
		        	timeFrame = "NA";
		        
		        dv = dataValueMap.get( ouId+":"+preformanceDashboardDeIds[i][1] );
		        if( dv != null && dv.getValue() != null ) {
		        	timeFrame += " to " + dv.getValue();
		        	monYearCollected = monthFormat.format( dv.getLastUpdated() );
		        }
		        else
		        	timeFrame += " to NA";
		        cell.setCellValue( timeFrame );
		        
		        //Month Year Collected
		        cell = row.getCell( 3 );
		        cell.setCellValue( monYearCollected );
		        
		        //Remaining Columns
		        int colNo = 4;
	        	for(int j=2; j < preformanceDashboardDeIds[i].length; j++) {
	        		cell = row.getCell( colNo++ );	        		
	        		dv = dataValueMap.get( ouId+":"+preformanceDashboardDeIds[i][j] );
			        if( dv != null && dv.getValue() != null ) {			        	
			        	try{ cell.setCellValue( Integer.parseInt(dv.getValue() ) ); }catch(Exception e) { cell.setCellValue( dv.getValue() ); }
			        }			        
	        	}
	        }

    	}
    	catch(Exception e){
    		System.out.println("Exception in generateISCExcel: "+e.getMessage() );
    		e.printStackTrace();
    	}
    	return workbook;
    }
   
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
   
}