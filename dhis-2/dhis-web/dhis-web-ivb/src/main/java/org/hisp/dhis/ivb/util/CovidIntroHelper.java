package org.hisp.dhis.ivb.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CovidIntroHelper 
{

	 
	public static final String FLAG_CRITERIA_NA = "NA";
	
	public static final String ALERT_COLOR_NODATA = "WHITE";
	public static final String ALERT_COLOR_FLAGGED = "RED";
	public static final String ALERT_COLOR_NOT_FLAGGED = "GREEN";
	
	//public static final String ROOT_SEPERATOR = "@!@";
	//public static final String MAIN_SEPERATOR = "#@#";
	public static final String ROOT_WORD_SEPERATOR = "@!@";
	public static final String SUB_WORD_SEPERATOR1 = "@;@";
	public static final String SUB_WORD_SEPERATOR2 = "@-@";
	
	public static final String CONDITION_BYOPTION = "BY_OPTION";
	public static final String CONDITION_FUTURE_DATE = "FUTURE_DATE";
	public static final String CONDITION_IF_BLANK = "IF_BLANK";
	public static final String CONDITION_NO_CRITERIA = "NO_CRITERIA";
	public static final String CONDITION_EQUALS = "EQUALS";
	public static final String CONDITION_LESSTHAN = "LESS_THAN";
	public static final String CONDITION_GREATERTHAN = "GREATER_THAN";	
	public static final String CONDITION_IN_NEXT = "IN_NEXT";
	public static final String CONDITION_IN_PAST = "IN_PAST";
	public static final String CONDITION_MORETHAN_X_MONTHS_AGO = "MORETHAN_X_MONTHS_AGO";
	public static final String CONDITION_AFTER = "AFTER";
	
    private String alertColorNoData = "#fcfdfd";
    private String alertColorFlagged = "";
    private String alertColorNotFlagged = "";

    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate ){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private OptionService optionService;
    
    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private DataElementService deService;
    
    
    //----------------------------------------------------------------
    // Methods
    //-----------------------------------------------------------------
    //page = 1 for Covid Intro Tracker and page =2 for Covid Country Profile
    public CovidIntroSnapshot getCovidIntroSnapshot( CovidIntroSnapshot covidIntroSnapshot, int page )
    {
    	String deIdsByComma = "3,4";
    	
    	try {
    		//DataElement list based on selected Indicator Types
    		/*
    		String query = "select t2.\"value\" as ind_type, t1.dataelementid as deid  \r\n"+
    						" from dataelementattributevalues as t1\r\n" + 
    						" inner join attributevalue as t2 on t1.attributevalueid = t2.attributevalueid\r\n" + 
    						" inner join dataelement as t4 on t1.dataelementid = t4.dataelementid\r\n" + 
    						" where t2.attributeid = "+covidIntroSnapshot.getIndTypeAttributeId()+" AND t2.value IN ("+covidIntroSnapshot.getIndTypesByComma()+")"+
    						" order by ind_type, t4.rdb_sort";
    		*/
    		String query = "select t4.dataelementid as deid, t4.\"name\" as dename, t4.\"formname\" as dealias, t4.\"code\" as decode, t4.optionsetid optionsetid, t4.rdb_sort as sort_no  \r\n"+
					" from dataelement as t4\r\n" + 
					" where t4.dataelementid IN ("+deIdsByComma+")";
    		SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
    		while ( rs2.next() ){
    			GenericTypeObj deObj = new GenericTypeObj();
    			deObj.setId( rs2.getInt("deid") );
    			deObj.setName( rs2.getString("dename") );
    			deObj.setCode( rs2.getString("decode") );
    			deObj.setAlias( rs2.getString("dealias") );
    			deObj.setIntAttrib1( rs2.getInt("optionsetid") );
    			deObj.setSortOrderNo( rs2.getInt("sort_no") );
    			
    			covidIntroSnapshot.getDeMap().put( deObj.getId(), deObj );
    		}
    		
    		query = "select t1.dataelementid as deid, t4.\"name\" as dename, t4.\"formname\" as dealias, t4.\"code\" as decode, t2.\"value\" as ind_type, t4.optionsetid optionsetid, t4.rdb_sort as sort_no  \r\n"+
					" from dataelementattributevalues as t1\r\n" + 
					" inner join attributevalue as t2 on t1.attributevalueid = t2.attributevalueid\r\n" + 
					" inner join dataelement as t4 on t1.dataelementid = t4.dataelementid\r\n" + 
					" where t2.attributeid = "+covidIntroSnapshot.getIndTypeAttributeId()+" AND t2.value IN ("+covidIntroSnapshot.getIndTypesByComma()+")"+
					" order by ind_type, t4.rdb_sort";

    		//System.out.println("Query1: "+ query );
    		SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
    		while ( rs1.next() ){
    			String indType = rs1.getString("ind_type");
    			int deid = rs1.getInt("deid");
    			GenericTypeObj deObj = new GenericTypeObj();
    			deObj.setId( rs1.getInt("deid") );
    			deObj.setName( rs1.getString("dename") );
    			deObj.setCode( rs1.getString("decode") );
    			deObj.setAlias( rs1.getString("dealias") );
    			deObj.setIntAttrib1( rs1.getInt("optionsetid") );
    			deObj.setSortOrderNo( rs1.getInt("sort_no") );
    			
    			deIdsByComma += "," + deid;
    			if( covidIntroSnapshot.getIt_deIdMap().get( indType ) == null )
    				covidIntroSnapshot.getIt_deIdMap().put(indType, new ArrayList<>());
    			covidIntroSnapshot.getIt_deIdMap().get( indType ).add( deid );
    			covidIntroSnapshot.getDeMap().put( deObj.getId(), deObj );
    		}
    		
    		Set<Integer> generalDeIds = null;
    		if( page == 2 ){ //adding dataelementids from General dataset
    			generalDeIds = new HashSet<>();
    			DataElementGroup genDeg = deService.getDataElementGroup( covidIntroSnapshot.getGeneralDeGroupId() );
    			for( DataElement de : genDeg.getMembers() ){
    				GenericTypeObj deObj = new GenericTypeObj();
        			deObj.setId( de.getId() );
        			deObj.setName( de.getName() );
        			deObj.setCode( de.getCode() );
        			deObj.setAlias( de.getFormName() );
        			try{ deObj.setIntAttrib1( de.getOptionSet().getId() ); }catch(Exception e){}
        			
    				deIdsByComma += "," + de.getId();
    				covidIntroSnapshot.getDeMap().put( deObj.getId(), deObj );
    				generalDeIds.add( de.getId() );
    			}
    		}
    		
    		
    		//------------Getting Dataelement's  flag criteria (meta-data)--------------------
    		/*
    		query = "select t1.dataelementid as deid, t4.\"name\" as dename, t4.\"formname\" as dealias, t4.\"code\" as decode, t2.\"value\" as flag_criteria, t4.optionsetid optionsetid from dataelementattributevalues as t1\r\n" + 
    						" inner join attributevalue as t2 on t1.attributevalueid = t2.attributevalueid\r\n" +     						
    						" inner join dataelement as t4 on t1.dataelementid = t4.dataelementid\r\n" + 
    						" where t2.attributeid = "+covidIntroSnapshot.getFlagAttributeId()+" AND t4.dataelementid in ("+ deIdsByComma +") AND "+
    						"       t2.value is not null and trim(t2.value) != ''"+
    						" order by t4.rdb_sort";
    		*/
    		query = "select t1.dataelementid as deid, t2.\"value\" as flag_criteria from dataelementattributevalues as t1\r\n" + 
					" inner join attributevalue as t2 on t1.attributevalueid = t2.attributevalueid\r\n" +     						
					" inner join dataelement as t4 on t1.dataelementid = t4.dataelementid\r\n" + 
					" where t2.attributeid = "+covidIntroSnapshot.getFlagAttributeId()+" AND t4.dataelementid in ("+ deIdsByComma +") AND "+
					"       t2.value is not null and trim(t2.value) != ''"+
					" order by t4.rdb_sort";

    		//System.out.println("Query2: "+ query );
    		SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    		while ( rs.next() ){
    			/*
    			GenericTypeObj deObj = new GenericTypeObj();
    			deObj.setId( rs.getInt("deid") );
    			deObj.setName( rs.getString("dename") );
    			deObj.setCode( rs.getString("decode") );
    			deObj.setAlias( rs.getString("dealias") );
    			deObj.setIntAttrib1( rs.getInt("optionsetid") );
    			String flagCriteria = rs.getString("flag_criteria");
    			
    			*/
    			String flagCriteria = rs.getString("flag_criteria");
    			int deId = rs.getInt("deid");
    			try{
    				covidIntroSnapshot.getDeMap().get(deId).setStrAttrib1(flagCriteria);
    				covidIntroSnapshot.getDeMap().get(deId).setStrAttrib2(flagCriteria.split( ROOT_WORD_SEPERATOR )[2]); //flag info to show on mouse over
    			}
    			catch(Exception e) {}
    			/*
    			deObj.setStrAttrib1( flagCriteria );
    			
    			try{
    				deObj.setStrAttrib2( flagCriteria.split( ROOT_WORD_SEPERATOR )[2] ); //flag info to show on mouse over
    			}
    			catch(Exception e) {
    				deObj.setStrAttrib2( "" );
    			}
    			
    			covidIntroSnapshot.getDeMap().put( deObj.getId(), deObj );
    			*/
            }
		
    		    		
    		//------------Getting Data for selected orgunit(s) and dataelement(s)--------------------
    		covidIntroSnapshot.setDeIdsByComma(deIdsByComma);
    		getCovidIntroTrackerData( covidIntroSnapshot, page, generalDeIds );
    		
    		
    		
    		/*
    		for( String indType : covidIntroSnapshot.getIndTypes() ){
    			for( int deId : covidIntroSnapshot.getIt_deIdMap().get( indType ) ){
    				GenericTypeObj deObj = covidIntroSnapshot.getDeMap().get( deId );
    				System.out.println( indType + " -- " + deObj.getName() + " -- "+ deObj.getSortOrderNo() );
    				
    			}
    		}
    		*/
    		
    		
    		//System.out.println( "Show Nonzero countries only : "+ covidIntroSnapshot.getNonZeroCountries() );
    		/*
    		for( OrganisationUnit ou : covidIntroSnapshot.getSelOrgUnits() ){
    			if( covidIntroSnapshot.getNonZeroOrgUnitIds().contains(ou.getId()) )
    				System.out.println( ou.getName() + ", "+ ou.getCode() +" has data");
    			else
    				System.out.println( ou.getName() + ", "+ ou.getCode() +" has no data");
    		}
    		*/
    		
    		/*
    		for( String indType : covidIntroSnapshot.getIndTypes() ){
    			for( int deId : covidIntroSnapshot.getIt_deIdMap().get( indType ) ){
    				GenericTypeObj deObj = covidIntroSnapshot.getDeMap().get( deId );
    				GenericDataVO dataVo = covidIntroSnapshot.getDataMap().get( covidIntroSnapshot.getSelOrgUnit().getId()+":"+deId );
    				if( dataVo != null )
    					System.out.println( indType + " -- " + deObj.getName() + " -- "+ dataVo.getAlertColor() );
    				
    			}
    		}
    		*/
    	}
    	catch( Exception e) {
    		e.printStackTrace();
    	}
    	
    	return covidIntroSnapshot;    	
    }//getCovidIntroTrackerSnapshot end
    
    
    
    public void getCovidIntroTrackerData( CovidIntroSnapshot covidIntroSnapshot, int page, Set<Integer> generalDeIds )
    {
        Map<Integer, Map<String, Option>> optionMap = new HashMap<Integer, Map<String, Option>>();
        for( OptionSet optionSet : optionService.getAllOptionSets() ) {
       		optionMap.put(optionSet.getId(), new HashMap<String, Option>() );
        	for( Option option : optionSet.getOptions() )
        		optionMap.get(optionSet.getId()).put( option.getCode(), option );
        }
        
        try
        {
            String query = "SELECT dv.sourceid, dv.dataelementid, dv.periodid, dv.value, dv.comment, dv.storedby, dv.lastupdated " +
                            " FROM " +
                                "( " +
                                    " SELECT periodid,dataelementid,sourceid FROM " + 
                                        "(SELECT MAX(p.startdate) AS startdate,dv.dataelementid,dv.sourceid FROM datavalue dv " +
                                            " INNER JOIN period p ON p.periodid=dv.periodid " +
                                                " WHERE dv.dataelementid IN ( "+ covidIntroSnapshot.getDeIdsByComma() +") AND dv.sourceid IN ( " + covidIntroSnapshot.getOuIdsByComma() + " ) " + 
                                                " GROUP BY dv.dataelementid,dv.sourceid " +
                                         ")asd " +
                                     " INNER JOIN period p ON p.startdate=asd.startdate " +
                                 ")asd1 " +
                             " INNER JOIN datavalue dv ON dv.sourceid=asd1.sourceid " +
                             " AND dv.dataelementid=asd1.dataelementid " +
                             " AND dv.periodid=asd1.periodid";
            //System.out.println("Query3: "+ query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            Date tempDate = null;
            while ( rs.next() )
            {
                Integer ouId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                GenericTypeObj deObj = covidIntroSnapshot.getDeMap().get( deId );
                String value = rs.getString( 4 );
                
                
                //System.out.println( deObj.getName() + " : " + ouId + " : " + value + " : " + deObj.getIntAttrib1() );
                
                if(deId==730)
				{  value = rs.getString( 4 );
					int foo = Integer.parseInt(value);
					value = NumberFormat.getNumberInstance(Locale.US).format(foo);
				}

                //For dataelements with option sets
                if( deObj.getIntAttrib1() != null && deObj.getIntAttrib1() != 0 ) {
                	//System.out.println( optionMap.get( deObj.getIntAttrib1() ) );
                	//System.out.println( optionMap.get( deObj.getIntAttrib1() ).get( value ) );
                	//System.out.println( optionMap.get( deObj.getIntAttrib1() ).get( value ).getName() );
                	if( value != null && !value.trim().equals("") && !value.trim().equals("-1") ) {
                		if( optionMap.get( deObj.getIntAttrib1() ) != null && optionMap.get( deObj.getIntAttrib1() ).get( value ) != null && optionMap.get( deObj.getIntAttrib1() ).get( value ).getName() != null  )
                			value = optionMap.get( deObj.getIntAttrib1() ).get( value ).getName();
                	}
                	else
                		value = null;
                }

                if( value != null && !value.trim().equals("") )
                	covidIntroSnapshot.getNonZeroOrgUnitIds().add( ouId );
                
                String comment = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                Date lastUpdated = rs.getDate( 7 );

                GenericDataVO dataVo = new GenericDataVO();
                
                dataVo.setValue( value );
                dataVo.setComment( comment );
                dataVo.setStoredBy( storedBy );
                dataVo.setLastUpdated( lastUpdated );
                dataVo.setAlertColor( validateFlagCriteria( dataVo,  deObj.getStrAttrib1() ) );
                
                covidIntroSnapshot.getDataMap().put( ouId+":"+deId, dataVo );
                if( page == 2 && generalDeIds.contains( deId ) && (tempDate == null || tempDate.before( lastUpdated ) ) )
                    tempDate = lastUpdated;
            }
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            if( tempDate != null )
            	covidIntroSnapshot.setLastUpdated( simpleDateFormat.format( tempDate ) );
        }
        catch ( Exception e ){
        	e.printStackTrace();
            throw new RuntimeException( "Exception in getCovidIntroTrackerData", e );
        }
    }//getCovidIntroTrackerData end

    
    public String validateFlagCriteria( GenericDataVO dataVo, String flagCriteria )
    {
    	int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    	
    	String alertColor = alertColorNoData;
    	
    	try {
    		//BY_OPTION@!@#C2C2A3@!@Colored by option@!@Yes@-@#10A30C@;@No@-@#F22C0E@;@Don't Know@-@#B2B7B4
    		//FUTURE_DATE@!@#C2C2A3@!@Flag if application date is in the future@!@#F22C0E
    		//NO_CRITERIA@!@#C2C2A3@!@No Flag@!@NONE
    		//IF_BLANK@!@#C2C2A3@!@Flag if blank@!@#F22C0E
    		
    		if( flagCriteria == null || flagCriteria.trim().equals("") )
    			return alertColor;
    		
    		String condition = flagCriteria.split( ROOT_WORD_SEPERATOR )[0];
    		String defColor = flagCriteria.split( ROOT_WORD_SEPERATOR )[1];
			String conditionValue = flagCriteria.split( ROOT_WORD_SEPERATOR )[3];

			String dvValue = (dataVo.getValue() == null )? "" : dataVo.getValue().trim();
			
			//System.out.println("1. condition="+condition+", value="+value+", dvValue="+dvValue);
			if(condition.equals( CONDITION_NO_CRITERIA ) || dvValue.equals("") ){
				alertColor = defColor;
			}
			else if( condition.equals( CONDITION_BYOPTION ) ) {
				alertColor = defColor;
				try{
					for( String optionWithColor : conditionValue.split( SUB_WORD_SEPERATOR1 ) ){
						String optionName = optionWithColor.split( SUB_WORD_SEPERATOR2 )[0];
						String optionColor = optionWithColor.split( SUB_WORD_SEPERATOR2 )[1];
						if( optionName.trim().equalsIgnoreCase(dvValue) ){
							alertColor = optionColor;
							break;
						}
					}
				}
				catch(Exception e){}
			}
			else if(condition.equals( CONDITION_IF_BLANK ) ){
				alertColor = conditionValue;
			}
			else if(condition.equals( CONDITION_FUTURE_DATE ) ){
    			Calendar criteriaCal = Calendar.getInstance();
    			criteriaCal.set( Calendar.HOUR, 0 );
    			criteriaCal.set( Calendar.MINUTE, 0);
    			criteriaCal.set(Calendar.SECOND, 0);
    			criteriaCal.set(Calendar.MILLISECOND, 0);
    			
    			Calendar dvCal = getCalendar( dvValue, 1 );
    			
    			alertColor = defColor;
    			if( dvCal != null && dvCal.after( criteriaCal ) )
					alertColor = conditionValue;				
			}
			else if( condition.equals( CONDITION_EQUALS ) ) {
    			if( conditionValue.equalsIgnoreCase( dvValue ) )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    			//System.out.println("3. alertColor="+alertColor);
    		}
    		else if( condition.equals( CONDITION_LESSTHAN ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( conditionValue );}catch(Exception e ) {}
    			int intDVValue = 0;
    			try { intDVValue = Integer.parseInt( dvValue );}catch(Exception e ) {}
    			if( intDVValue < intValue )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    		}
    		else if( condition.equals( CONDITION_GREATERTHAN ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( conditionValue );}catch(Exception e ) {}
    			int intDVValue = 0;
    			try { intDVValue = Integer.parseInt( dvValue );}catch(Exception e ) {}
    			if( intDVValue > intValue )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    		}
    		else if( condition.equals( CONDITION_MORETHAN_X_MONTHS_AGO ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( conditionValue );}catch(Exception e ) {}
    			
    			Calendar criteriaCal = Calendar.getInstance();
    			criteriaCal.set( Calendar.HOUR, 0 );
    			criteriaCal.set( Calendar.MINUTE, 0);
    			criteriaCal.set(Calendar.SECOND, 0);
    			criteriaCal.set(Calendar.MILLISECOND, 0);
    			criteriaCal.add(Calendar.MONTH, -(intValue) );
    			
    			Calendar dvCal = getCalendar( dvValue, 1 );
    			if( dvCal == null )
    				alertColor = alertColorNoData;
    			else if( dvCal != null && (dvCal.get( Calendar.YEAR) ) - criteriaCal.get( Calendar.YEAR ) < 0 )
					alertColor = alertColorFlagged;
				else
	    			alertColor = alertColorNotFlagged;	
    		}
    		else if( condition.equals( CONDITION_IN_NEXT ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( conditionValue );}catch(Exception e ) {}

    			Calendar criteriaStartCal = Calendar.getInstance();
    			criteriaStartCal.set( Calendar.HOUR, 0 );
    			criteriaStartCal.set( Calendar.MINUTE, 0);
    			criteriaStartCal.set(Calendar.SECOND, 0);
    			criteriaStartCal.set(Calendar.MILLISECOND, 0);
    			criteriaStartCal.set( Calendar.DATE, 1 );

    			Calendar criteriaEndCal = Calendar.getInstance();
    			criteriaEndCal.set( Calendar.HOUR, 0 );
    			criteriaEndCal.set( Calendar.MINUTE, 0);
    			criteriaEndCal.set(Calendar.SECOND, 0);
    			criteriaEndCal.set(Calendar.MILLISECOND, 0);
    			criteriaEndCal.add(Calendar.MONTH, intValue );
    			criteriaEndCal.getTime();
    			criteriaEndCal.set( Calendar.DATE, monthDays[ criteriaEndCal.get(Calendar.MONTH)] );

    			int monthQtr[] = { 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
    			Set<String> criteriaQtrs = new HashSet<String>();
    			Calendar fromCal = Calendar.getInstance();
    			fromCal.setTime( criteriaStartCal.getTime() );
    			while( fromCal.before( criteriaEndCal ) ) {
    				criteriaQtrs.add( fromCal.get(Calendar.YEAR ) +"-Q"+ monthQtr[ fromCal.get(Calendar.MONTH ) ] );
    				fromCal.add(Calendar.MONTH, 1);
    			}
    			
    			String[] dateParts = dvValue.split( "-" );
    	        if ( dateParts.length <= 1 ){//Year
    	        	Calendar dvCal = getCalendar( dvValue, 1 );
    	        	if( dvCal == null )
    	        		alertColor = alertColorNoData;
    	        	else if( dvCal.get( Calendar.YEAR ) >= criteriaStartCal.get( Calendar.YEAR ) && dvCal.get( Calendar.YEAR ) <= criteriaEndCal.get( Calendar.YEAR ) )
    	        		alertColor = alertColorFlagged;
    	        	else
    	        		alertColor = alertColorNotFlagged;
    	        }
    	        else if ( dateParts.length <= 2 && dateParts[1].contains("Q") ){//Quarter
    	        	if( criteriaQtrs.contains( dvValue) )
    	        		alertColor = alertColorFlagged;
    	        	else
    	        		alertColor = alertColorNotFlagged;    	        	
    	        }
    	        else {
    	        	Calendar dvCal = getCalendar( dvValue, 1 );
    	        	if( dvCal == null )
    	        		alertColor = alertColorNoData;
    	        	else if( (dvCal.equals( criteriaStartCal) || dvCal.after( criteriaStartCal )) && (dvCal.equals( criteriaEndCal) || dvCal.before( criteriaEndCal )) )
    	        		alertColor = alertColorFlagged;
    	        	else
    	        		alertColor = alertColorNotFlagged;    	        		
    	        }
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return alertColor;
    }

    public Calendar getCalendar( String dateStr, int dateFlag )
    {
    	String tempDateStr = "";
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String[] dateParts = dateStr.split( "-" );
        if ( dateParts.length <= 1 ){
        	if(dateFlag == 1)
        		tempDateStr = dateParts[0] + "-01-01";
        	else
        		tempDateStr = dateParts[0] + "-12-31";
        }
        else if ( dateParts[1].equalsIgnoreCase( "Q1" ) ){
        	if(dateFlag == 1)
        		tempDateStr = dateParts[0] + "-01-01";
        	else
        		tempDateStr = dateParts[0] + "-03-31";
        }
        else if ( dateParts[1].equalsIgnoreCase( "Q2" ) ){
        	if(dateFlag == 1)
        		tempDateStr = dateParts[0] + "-04-01";
        	else
        		tempDateStr = dateParts[0] + "-06-30";
        }
        else if ( dateParts[1].equalsIgnoreCase( "Q3" ) ){
        	if(dateFlag == 1)
        		tempDateStr = dateParts[0] + "-07-01";
        	else
        		tempDateStr = dateParts[0] + "-09-30";
        }
        else if ( dateParts[1].equalsIgnoreCase( "Q4" ) ){
        	if(dateFlag == 1)
        		tempDateStr = dateParts[0] + "-10-01";
        	else
        		tempDateStr = dateParts[0] + "-12-31";
        }
        else{
            if ( Integer.parseInt( dateParts[0] ) % 400 == 0 ){
            	if(dateFlag == 1)
            		tempDateStr = dateParts[0] + "-" + dateParts[1] + "-01";
            	else
            		tempDateStr = dateParts[0] + "-" + dateParts[1] + "-" + (monthDays[Integer.parseInt( dateParts[1] )] + 1);
            }
            else{
            	if(dateFlag == 1)
            		tempDateStr = dateParts[0] + "-" + dateParts[1] + "-01";
            	else
            		tempDateStr = dateParts[0] + "-" + dateParts[1] + "-" + (monthDays[Integer.parseInt( dateParts[1] )]);
            }
        }
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        try{
            Date eDate = simpleDateFormat.parse( tempDateStr );
            Calendar cal = Calendar.getInstance();
            cal.setTime( eDate );
            cal.set( Calendar.HOUR, 0 );
            cal.set( Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return cal;
        }
        catch( Exception e ){
            //System.out.println( "Exception in getCalendar: "+ e.getMessage() );
            return null;
        }
    }
}
