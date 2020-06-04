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

public class CampaignTrackerXlAction implements Action
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
    
    private Collection<Integer> ouIds;
    private Collection<Integer> campaignIds;
    private Collection<String> selCols;
    
    private Integer resultPage = 0;
	public Integer getResultPage() {
		return resultPage;
	}
	public void setResultPage(Integer resultPage) {
		this.resultPage = resultPage;
	}
	
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
	public void setCampaignIds(Collection<Integer> campaignIds) {
		this.campaignIds = campaignIds;
	}
	public void setSelCols(Collection<String> selCols) {
		this.selCols = selCols;
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
        
        campaignSnap.setResultPage( resultPage );
        
        campaignSnap.getSelCols().addAll( selCols );
        campaignSnap.getCampaignIds().addAll( campaignIds );
        campaignSnap.getOuIds().addAll( ouIds );
        
        campaignSnap = campaignHelper.getCampainTrackerSnap( campaignSnap );
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
            newdir.mkdirs();
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xlsx";


        Workbook workbook = campaignHelper.generateCampaignTrackerXl( campaignSnap );
        
        try {
            FileOutputStream out = new FileOutputStream( new File( outputReportPath ) );
            workbook.write( out );
            out.close();
        } 
        catch(FileNotFoundException e){
            System.out.println("Exception while streaming excel in CampaignTrackerXlAction: "+ e.getMessage() );
        	e.printStackTrace();
        } 
        catch(IOException e){
        	System.out.println("Exception whilee streaming excel in CampaignTrackerXlAction: "+ e.getMessage() );
            e.printStackTrace();
        }

        if( campaignSnap.getResultPage() == 1)
        	fileName = "MeaslesDashboard_"+campaignSnap.getCurDateStr()+".xlsx";
        else
        	fileName = "CampaignTracker_"+campaignSnap.getCurDateStr()+".xlsx";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();

        return SUCCESS;
    }      

}
