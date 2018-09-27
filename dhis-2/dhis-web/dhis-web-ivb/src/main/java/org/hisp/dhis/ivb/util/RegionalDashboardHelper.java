package org.hisp.dhis.ivb.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RegionalDashboardHelper 
{

	public static final String FLAG_CRITERIA_NA = "NA";
	
	public static final String ALERT_COLOR_NODATA = "WHITE";
	public static final String ALERT_COLOR_FLAGGED = "RED";
	public static final String ALERT_COLOR_NOT_FLAGGED = "GREEN";
	
	public static final String ROOT_SEPERATOR = "@!@";
	public static final String MAIN_SEPERATOR = "#@#";
	public static final String CONDITION_EQUALS = "EQUALS";
	public static final String CONDITION_LESSTHAN = "LESS_THAN";
	public static final String CONDITION_GREATERTHAN = "GREATER_THAN";
	public static final String CONDITION_IN_NEXT = "IN_NEXT";
	public static final String CONDITION_IN_PAST = "IN_PAST";
	public static final String CONDITION_MORETHAN_X_MONTHS_AGO = "MORETHAN_X_MONTHS_AGO";
	public static final String CONDITION_AFTER = "AFTER";
	
    private String alertColorNoData = "";
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
    
    //----------------------------------------------------------------
    // Methods
    //-----------------------------------------------------------------
    public RegionalDBSnapshot getRegionalDashboardSnapShot(  RegionalDBSnapshot regionalDBSnapshot )
    {
    	Map<Integer, List<GenericTypeObj>> ds_deMap = new HashMap<Integer, List<GenericTypeObj>>();
    	Map<Integer, GenericTypeObj> deMap = new HashMap<Integer, GenericTypeObj>();
    	
    	String deIdsByComma = "-1";
    	
    	try {
    		alertColorNoData = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_ALERT_COLOR_NODATA ).getValue().split( ROOT_SEPERATOR )[ regionalDBSnapshot.getRegionalDBCount() ];
    		alertColorFlagged = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_ALERT_COLOR_FLAGGED ).getValue().split( ROOT_SEPERATOR )[ regionalDBSnapshot.getRegionalDBCount() ];
    		alertColorNotFlagged = lookupService.getLookupByName( Lookup.REGIONAL_DASHBOARD_ALERT_COLOR_NOT_FLAGGED ).getValue().split( ROOT_SEPERATOR )[ regionalDBSnapshot.getRegionalDBCount() ];
    		
    		//------------Gettings Dataelement name and flag criteria (meta-data)--------------------
    		String query = "select t3.datasetid as dsid, t1.dataelementid as deid, t4.\"name\" as dename, t4.\"code\" as decode, t2.\"value\" as flag_criteria, t4.optionsetid optionsetid from dataelementattributevalues as t1\r\n" + 
    						" inner join attributevalue as t2 on t1.attributevalueid = t2.attributevalueid\r\n" + 
    						" inner join datasetmembers as t3 on t1.dataelementid = t3.dataelementid\r\n" + 
    						" inner join dataelement as t4 on t1.dataelementid = t4.dataelementid\r\n" + 
    						" where t2.attributeid = "+regionalDBSnapshot.getFlagAttributeId()+" AND t3.datasetid in ("+ regionalDBSnapshot.getDsIdsByComma() +") AND "+
    						"       t2.value is not null and trim(t2.value) != ''"+
    						" order by dsid, t4.rdb_sort";
    		SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    		while ( rs.next() ){
    			int dsid = rs.getInt("dsid");
    			GenericTypeObj deObj = new GenericTypeObj();
    			deObj.setId( rs.getInt("deid") );
    			deObj.setName( rs.getString("dename") );
    			deObj.setCode( rs.getString("decode") );
    			String flagCriteria = null;
    			try { flagCriteria = rs.getString("flag_criteria").split( ROOT_SEPERATOR )[ regionalDBSnapshot.getRegionalDBCount() ]; }
    			catch(Exception e){}
    			
    			if( flagCriteria == null || flagCriteria.trim().isEmpty() || flagCriteria.trim().equalsIgnoreCase(FLAG_CRITERIA_NA) )
    				continue;
    			
    			deObj.setStrAttrib1( flagCriteria );
    			deObj.setIntAttrib1( rs.getInt("optionsetid") );
    			try{
    				deObj.setStrAttrib2( flagCriteria.split( MAIN_SEPERATOR )[2] );
    			}
    			catch(Exception e) {
    				deObj.setStrAttrib2( "" );
    			}
    			
    			if( ds_deMap.get( dsid ) == null )
    				ds_deMap.put( dsid, new ArrayList<GenericTypeObj>() );    			
    			ds_deMap.get( dsid ).add( deObj );
    			
    			deMap.put( deObj.getId(), deObj );
    			deIdsByComma += "," + deObj.getId();
            }
    		    		
    		//------------Getting Regional Dashboard Data for selected orgunit(s) and dataelement(s)--------------------
    		Map<String, GenericDataVO> regionalDBDataMap = new HashMap<String, GenericDataVO>();
    		regionalDBDataMap = getRegionalDashboardData( deIdsByComma, regionalDBSnapshot.getOuIdsByComma(), deMap );
    		
    		regionalDBSnapshot.setDs_deMap( ds_deMap );
    		regionalDBSnapshot.setRegionalDBDataMap( regionalDBDataMap) ;
    	}
    	catch( Exception e) {
    		e.printStackTrace();
    	}
    	
    	return regionalDBSnapshot;    	
    }//getRegionalDashboardSnapShot end
    
    
    
    public Map<String, GenericDataVO> getRegionalDashboardData( String dataElementIdsByComma, String orgUnitIdsByComma, Map<Integer, GenericTypeObj> deMap )
    {
        Map<String, GenericDataVO> regionalDBDataMap = new HashMap<String, GenericDataVO>();
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
                                                " WHERE dv.dataelementid IN ( "+ dataElementIdsByComma +") AND dv.sourceid IN ( " + orgUnitIdsByComma + " ) " + 
                                                " GROUP BY dv.dataelementid,dv.sourceid " +
                                         ")asd " +
                                     " INNER JOIN period p ON p.startdate=asd.startdate " +
                                 ")asd1 " +
                             " INNER JOIN datavalue dv ON dv.sourceid=asd1.sourceid " +
                             " AND dv.dataelementid=asd1.dataelementid " +
                             " AND dv.periodid=asd1.periodid";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
                Integer ouId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                GenericTypeObj deObj = deMap.get( deId );
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

                String comment = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                Date lastUpdated = rs.getDate( 7 );

                GenericDataVO regionalDBVo = new GenericDataVO();
                
                regionalDBVo.setValue( value );
                regionalDBVo.setComment( comment );
                regionalDBVo.setStoredBy( storedBy );
                regionalDBVo.setLastUpdated( lastUpdated );
                regionalDBVo.setAlertColor( validateFlagCriteria( regionalDBVo,  deObj.getStrAttrib1() ) );
                
                regionalDBDataMap.put( ouId+":"+deId, regionalDBVo );               
            }
        }
        catch ( Exception e ){
        	e.printStackTrace();
            throw new RuntimeException( "Exception in getRegionalDashboardData", e );
        }
        
        return regionalDBDataMap;
    }

    
    public String validateFlagCriteria( GenericDataVO regionalDBVo, String flagCriteria )
    {
    	int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    	
    	String alertColor = alertColorNoData;
    	
    	try {
    		String condition = flagCriteria.split( MAIN_SEPERATOR )[0];
    		String value = flagCriteria.split( MAIN_SEPERATOR )[1].trim();
			String dvValue = (regionalDBVo.getValue() == null )? "" : regionalDBVo.getValue().trim();

			if( dvValue.equals("") ) {
				alertColor = alertColorNoData;
			}
			//else if( flagCriteria.split( MAIN_SEPERATOR ).length == 5 && ( flagCriteria.split( MAIN_SEPERATOR )[3].equalsIgnoreCase( dvValue ) )) {
			else if( flagCriteria.split( MAIN_SEPERATOR ).length == 5 && ( flagCriteria.split( MAIN_SEPERATOR )[3].contains( dvValue ) )) {
				alertColor = flagCriteria.split( MAIN_SEPERATOR )[4];
			}
			else if( condition.equals( CONDITION_EQUALS ) ) {
    			if( value.equalsIgnoreCase( dvValue ) )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    		}
    		else if( condition.equals( CONDITION_LESSTHAN ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( value );}catch(Exception e ) {}
    			int intDVValue = 0;
    			try { intDVValue = Integer.parseInt( dvValue );}catch(Exception e ) {}
    			if( intDVValue < intValue )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    		}
    		else if( condition.equals( CONDITION_GREATERTHAN ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( value );}catch(Exception e ) {}
    			int intDVValue = 0;
    			try { intDVValue = Integer.parseInt( dvValue );}catch(Exception e ) {}
    			if( intDVValue > intValue )
    				alertColor = alertColorFlagged;
    			else
    				alertColor = alertColorNotFlagged;
    		}
    		else if( condition.equals( CONDITION_MORETHAN_X_MONTHS_AGO ) ) {
    			int intValue = 0;
    			try { intValue = Integer.parseInt( value );}catch(Exception e ) {}
    			
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
    			try { intValue = Integer.parseInt( value );}catch(Exception e ) {}

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
