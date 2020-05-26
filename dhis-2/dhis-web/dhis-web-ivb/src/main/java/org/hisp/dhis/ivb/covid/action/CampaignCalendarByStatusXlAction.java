package org.hisp.dhis.ivb.covid.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Workbook;
import org.hisp.dhis.ivb.isc.ISCReportHelper;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class CampaignCalendarByStatusXlAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	@Autowired
    private OrganisationUnitService orgUnitService;
    
	@Autowired
    private CampaignHelper campaignHelper;
	
    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    private InputStream inputStream;
    public InputStream getInputStream(){
        return inputStream;
    }

    private String fileName;
    public String getFileName(){
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------
    private String isoCode;
	private String whoRegion;
    private String unicefRegion;
    private String incomeLevel;
    private String gaviEligibleStatus;
    private String showComment;
    private String fromDateStr;
    private String toDateStr;
    
    private Collection<Integer> ouIds;
    
	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	public void setWhoRegion(String whoRegion) {
		this.whoRegion = whoRegion;
	}
	public void setUnicefRegion(String unicefRegion) {
		this.unicefRegion = unicefRegion;
	}
	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}
	public void setGaviEligibleStatus(String gaviEligibleStatus) {
		this.gaviEligibleStatus = gaviEligibleStatus;
	}
	public void setShowComment(String showComment) {
		this.showComment = showComment;
	}
	public void setOuIds(Collection<Integer> ouIds) {
		this.ouIds = ouIds;
	}
    public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}
	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute() throws Exception
    {
		CampaignSnapshot campaignSnap = new CampaignSnapshot();
		if( isoCode != null && isoCode.equals("ON") )
        	campaignSnap.setIsoCode("ON");
        if( whoRegion != null && whoRegion.equals("ON") )
        	campaignSnap.setWhoRegion("ON");
        if( unicefRegion != null && unicefRegion.equals("ON") )
        	campaignSnap.setUnicefRegion("ON");
        if( incomeLevel != null && incomeLevel.equals("ON") )
        	campaignSnap.setIncomeLevel("ON");
        if( gaviEligibleStatus != null && gaviEligibleStatus.equals("ON") )
        	campaignSnap.setGaviEligibleStatus("ON");
        if( showComment != null && showComment.equals("ON") )
        	campaignSnap.setShowComment("ON");
        //campaignSnap.getSelCols().addAll( selCols );
        //campaignSnap.getCampaignIds().addAll( campaignIds );
        campaignSnap.getOuIds().addAll( ouIds );
        
        try{
        	if( fromDateStr.length() == 4 )
        		fromDateStr = fromDateStr +"-01";
            
            if( toDateStr.length() == 4 )
            	toDateStr = toDateStr +"-12";            
        }
        catch( Exception e ){
        }
        campaignSnap.setFromDateStr( fromDateStr );
        campaignSnap.setToDateStr( toDateStr );
        
        campaignSnap = campaignHelper.getCampainCalendarSnap( campaignSnap );
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
            newdir.mkdirs();
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xlsx";


        Workbook workbook = campaignHelper.generateCampaignCalendarXl( campaignSnap );
        
        try {
            FileOutputStream out = new FileOutputStream( new File( outputReportPath ) );
            workbook.write( out );
            out.close();
        } 
        catch(FileNotFoundException e){
            System.out.println("Exception while streaming excel in CampaignCalendarXlAction: "+ e.getMessage() );
        	e.printStackTrace();
        } 
        catch(IOException e){
        	System.out.println("Exception whilee streaming excel in CampaignCalendarXlAction: "+ e.getMessage() );
            e.printStackTrace();
        }

        
        fileName = "CampaignCalendar_"+campaignSnap.getCurDateStr()+".xlsx";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();

        return SUCCESS;
    }      

}
