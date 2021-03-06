package org.hisp.dhis.commons.action;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.commons.filter.FilterUtils;
//import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class GetFilterDataElementsAction
    extends ActionPagingSupport<DataElement>
{
    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private I18nService i18nService;
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer categoryComboId;

    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String periodTypeName;

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private boolean aggregate = false;

    public void setAggregate( boolean aggregate )
    {
        this.aggregate = aggregate;
    }

    public String domain;

    public void setDomain( String domain )
    {
        this.domain = domain;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	//System.out.println("Inside Filter DEs ACtion : ");
    	User curUser = currentUserService.getCurrentUser();
        
        List<UserAuthorityGroup> userAuthorityGroups = new ArrayList<UserAuthorityGroup>( curUser.getUserCredentials().getUserAuthorityGroups() );
        Set<DataElement> userDataElements = new HashSet<DataElement>();
        for ( UserAuthorityGroup userAuthorityGroup : userAuthorityGroups )
        {
            userDataElements.addAll( userAuthorityGroup.getDataElements() );
        }
        
        if ( id != null && id != ALL )
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );

            if ( dataElementGroup != null )
            {
                // dataElements = new ArrayList<DataElement>(
                // dataElementGroup.getMembers() );

                List<DataElement> deList = new ArrayList<DataElement>( dataElementGroup.getMembers() );

                dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );

                dataElements.retainAll( deList );
            }
        }
        else if ( categoryComboId != null && categoryComboId != ALL )
        {
            DataElementCategoryCombo categoryCombo = categoryService.getDataElementCategoryCombo( categoryComboId );

            if ( categoryCombo != null )
            {
                dataElements = new ArrayList<DataElement>(
                    dataElementService.getDataElementByCategoryCombo( categoryCombo ) );
            }
        }
        else if ( dataSetId != null )
        {
            DataSet dataset = dataSetService.getDataSet( dataSetId );

            if ( dataset != null )
            {
                dataElements = new ArrayList<DataElement>( dataset.getDataElements() );
            }
        }
        else if ( periodTypeName != null )
        {
            PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

            if ( periodType != null )
            {
                dataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByPeriodType( periodType ) );
            }
        }
        else if ( domain != null )
        {
            if ( domain.equals( DataElementDomain.AGGREGATE.getValue() ) )
            {
                dataElements = new ArrayList<DataElement>(
                    dataElementService.getDataElementsByDomainType( DataElementDomain.AGGREGATE ) );
            }
            else
            {
                dataElements = new ArrayList<DataElement>(
                    dataElementService.getDataElementsByDomainType( DataElementDomain.TRACKER ) );
            }
        }
        else
        {
            // dataElements = new ArrayList<DataElement>(
            // dataElementService.getAllDataElements() );
        	
            dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
            dataElements.retainAll( userDataElements );

            ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), dataElements );
        }

        if ( key != null )
        {
            dataElements = IdentifiableObjectUtils.filterNameByKey( dataElements, key, true );
        }

        if ( dataElements == null )
        {
            dataElements = new ArrayList<DataElement>();
        }

        Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );

        if ( aggregate )
        {
            FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        }

        if ( usePaging )
        {
            this.paging = createPaging( dataElements.size() );

            dataElements = dataElements.subList( paging.getStartPos(), paging.getEndPos() );
        }
        
        
        
            
        Constant ivbAggDEConst = constantService.getConstantByName( "IS_IVB_AGGREGATED_DE_ATTRIBUTE_ID" );
        Constant ivbRestrictedDEConst = constantService.getConstantByName( "RESTRICTED_DE_ATTRIBUTE_ID" );
        Set<DataElement> restrictedDes = new HashSet<DataElement>();
        
        Iterator<DataElement> iterator = dataElements.iterator();
        while( iterator.hasNext() )
        {
            DataElement dataElement = iterator.next();
            
            if( dataElement.getPublicAccess() != null && dataElement.getPublicAccess().equals( "--------" ) )
            {
            	iterator.remove();
            	continue;
            }
            
           	//System.out.println( "DE : " + dataElement.getName() );
            Set<AttributeValue> dataElementAttributeValues = dataElement.getAttributeValues();
            if( dataElementAttributeValues != null && dataElementAttributeValues.size() > 0 )
            {
                for( AttributeValue deAttributeValue : dataElementAttributeValues )
                {
                	//System.out.println( "DE : " + dataElement.getName() + " : " + deAttributeValue.getAttribute().getId() + " : " + deAttributeValue.getValue().equalsIgnoreCase( "true" ) + " : " + ivbRestrictedDEConst.getValue() );
                	
                    if( deAttributeValue.getAttribute().getId() == ivbAggDEConst.getValue() &&  deAttributeValue.getValue().equalsIgnoreCase( "true" ) )
                    {
                        iterator.remove();
                    }
                    else if( deAttributeValue.getAttribute().getId() == ivbRestrictedDEConst.getValue() &&  deAttributeValue.getValue().equalsIgnoreCase( "true" ) )
                    {
                    	restrictedDes.add( dataElement);
                    	//System.out.println( "Restricted DE : " + dataElement.getName() );
                    }
                }
            }
            
        }

        restrictedDes.removeAll( userDataElements );
        
        dataElements.removeAll( restrictedDes );
        
        //DataElement de = dataElementService.getDataElement( 476 );
        
        //System.out.println( "Before: " + dataElements.size() + ",  " + userDataElements.size() +",  "+ dataElements.contains( de ) + ",  " + userDataElements.contains( de ) );
        dataElements.retainAll( userDataElements );
        //System.out.println( "After: " + dataElements.size() + ",  " + userDataElements.size() +",  "+ dataElements.contains( de ) + ",  " + userDataElements.contains( de ) );
        
        i18n( i18nService, dataElements );
        
        return SUCCESS;
    }
}
