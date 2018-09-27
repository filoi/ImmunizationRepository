package org.hisp.dhis.ivb.util;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.commons.filter.Filter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueAudit;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author BHARATH
 */

@Transactional
public class EPIProfileHelper
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
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    private static DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private static ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private SectionService sectionService;
    
    public void setSectionService(SectionService sectionService) 
    {
	this.sectionService = sectionService;
    }

    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    //----------------------------------------------------------------
    // Input/Output
    //-----------------------------------------------------------------

    
    //----------------------------------------------------------------
    // Methods
    //-----------------------------------------------------------------
    
    //DataElementId as key and data as value
    public Map<Integer, GenericDataVO> getLatestDataValues( String dataElementIdsByComma, Integer orgUnitId, boolean includeHistory )
    {
        Map<Integer, GenericDataVO> latestDataValues = new HashMap<Integer, GenericDataVO>();
        
        try
        {
            String query = "SELECT dv.sourceid, dv.dataelementid, dv.periodid, dv.value, dv.comment, dv.storedby, dv.lastupdated " +
                    " FROM " +
                        "( " +
                            " SELECT periodid,dataelementid,sourceid FROM " + 
                                "(SELECT MAX(p.startdate) AS startdate,dv.dataelementid,dv.sourceid FROM datavalue dv " +
                                    " INNER JOIN period p ON p.periodid=dv.periodid " +
                                        " WHERE dv.dataelementid IN ( "+ dataElementIdsByComma +") AND dv.sourceid = " + orgUnitId + "  " + 
                                        " GROUP BY dv.dataelementid,dv.sourceid " +
                                 ")asd " +
                             " INNER JOIN period p ON p.startdate=asd.startdate " +
                         ")asd1 " +
                     " INNER JOIN datavalue dv ON dv.sourceid=asd1.sourceid " +
                     " AND dv.dataelementid=asd1.dataelementid " +
                     " AND dv.periodid=asd1.periodid";

/*            String query = "SELECT dv.sourceid, dv.dataelementid, dv.periodid, dv.value, dv.comment, dv.storedby, dv.lastupdated FROM datavalue dv " +
                            " INNER JOIN period p ON dv.periodid = p.periodid " + 
                            " WHERE " +
                                " CONCAT(dv.sourceid,\",\",dv.dataelementid,\",\",p.startdate) " +
                                    " IN ( "+ 
                                            " SELECT CONCAT( sourceid,\",\",dataelementid,\",\",MAX(period.startdate) ) FROM datavalue " +
                                                " INNER JOIN period ON datavalue.periodid = period.periodid " + 
                                                    " WHERE sourceid = " + orgUnitId + " AND dataelementid IN ("+ dataElementIdsByComma +") " + 
                                                    " GROUP BY sourceid,dataelementid" +
                                          " ) ORDER BY sourceid, dataelementid";
*/                                                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
                Integer ouId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                Integer pId = rs.getInt( 3 );
                String value = rs.getString( 4 );
                String comment = rs.getString( 5 );
                String storedBy = rs.getString( 6 );
                Date lastUpdated = rs.getDate( 7 );
                
                /*
                DataElement de = dataElementService.getDataElement( deId );
                OrganisationUnit ou = organisationUnitService.getOrganisationUnit( ouId );
                Period p = periodService.getPeriod( pId );
                
                DataValue dv = new DataValue();
                dv.setDataElement( de );
                dv.setSource( ou );
                dv.setPeriod( p );
                dv.setValue( value );
                dv.setComment( comment );
                dv.setStoredBy( storedBy );
                dv.setLastUpdated( lastUpdated );
                */
                
                GenericDataVO dataVo = new GenericDataVO();
                dataVo.setStrVal2( value );
                latestDataValues.put( deId, dataVo );
                
                //System.out.println( dv.getValue() );
            }
            
            if( includeHistory )
            {
	            /**
	             * TODO - need to get data for column 1 ie past year
	             */
	            query = "SELECT dataelementid, value FROM "+
	            			"("+
	            				" SELECT dataelementid, organisationunitid, categoryoptioncomboid, attributeoptioncomboid, value, timestamp, row_number() "+
	            					" OVER(PARTITION BY dataelementid, organisationunitid, categoryoptioncomboid, attributeoptioncomboid ORDER BY timestamp DESC) AS rn "+
	            				" FROM datavalueaudit WHERE dataelementid IN ("+ dataElementIdsByComma +") AND organisationunitid = "+ orgUnitId +" AND commenttype = 'H' "+
	            			") x "+
	            		" WHERE x.rn = 2";
	            System.out.println( query );
	            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
	            while ( rs1.next() )
	            {
	            	Integer deId = rs1.getInt( 1 );
	            	String value = rs1.getString(2);
	            	GenericDataVO dataVo = latestDataValues.get( deId );
	            	if( dataVo == null )
	            		dataVo = new GenericDataVO();
	            	dataVo.setStrVal1( value );
	            	latestDataValues.put( deId, dataVo );
	            }
            }
            
        }
        catch ( Exception e )
        {
        	e.printStackTrace();
            //throw new RuntimeException( "Exception in getLatestDataValues", e );
        }
        
        return latestDataValues;
    }
    
    //DataElementId as key and data as value
    public Map<String, Map<Integer,GenericDataVO>> getCountrywiseEPICharts()
    {
    	Map<String, Map<Integer,GenericDataVO>> countrywiseEPIChartsMap = new HashMap<String, Map<Integer,GenericDataVO>>();
        
        try
        {
            String query = "SELECT o.uid, string_agg(d.name,';'), string_agg(d.uid,';') FROM document d "+
								" inner join organisationunit o on o.organisationunitid = d.sourceid "+
								" WHERE d.name ilike '%EPI_%_IMG%' AND d.sourceid IS NOT NULL AND d.contenttype ilike 'image/png' "+
								" GROUP BY o.uid ";
            System.out.println( query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String ouId = rs.getString( 1 );
                String docNames = rs.getString( 2 );
                String docUids = rs.getString( 3 );
                Map<Integer, GenericDataVO> epiChartsMap = countrywiseEPIChartsMap.get( ouId );
                if( epiChartsMap == null ){
                	epiChartsMap = new HashMap<Integer,GenericDataVO>();
                	/*for( int i = 0; i<=8; i++ ){
                		epiCharts.add(i, null);
                	}*/
                }
                int docCount = 0;
                for( String docName : docNames.split(";")){
                	String docUid = docUids.split(";")[docCount++];
                	//System.out.println( docName + " : " + docUid );
                	int imgCount = Integer.parseInt( docName.split("_")[2].replaceAll("IMG", "") );
                	GenericDataVO docVo = new GenericDataVO();
                	docVo.setStrVal1( docName );
                	docVo.setStrVal2( docUid );
                	//System.out.println( docName + " : " + docUid + " : " + imgCount );
                	//System.out.println( docName + " -- " + imgCount );
                	epiChartsMap.put(imgCount-1, docVo );
                }
                countrywiseEPIChartsMap.put( ouId, epiChartsMap );
            }
        }
        catch ( Exception e )
        {
        	e.printStackTrace();
            //throw new RuntimeException( "Exception in getLatestDataValues", e );
        }
        
        return countrywiseEPIChartsMap;
    }   
}