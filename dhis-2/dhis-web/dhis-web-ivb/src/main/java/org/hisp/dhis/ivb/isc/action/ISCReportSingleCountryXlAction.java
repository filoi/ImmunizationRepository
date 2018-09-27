package org.hisp.dhis.ivb.isc.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Workbook;
import org.hisp.dhis.ivb.isc.ISCReportHelper;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ISCReportSingleCountryXlAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	@Autowired
    private OrganisationUnitService orgUnitService;
    
	@Autowired
	private ISCReportHelper iscReportHelper;
	
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private InputStream inputStream;
    public InputStream getInputStream(){
        return inputStream;
    }

    private String fileName;
    public String getFileName(){
        return fileName;
    }
    
    private String orgUnitId;
    public void setOrgUnitId( String orgUnitId ){
        this.orgUnitId = orgUnitId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute() throws Exception
    {
		OrganisationUnit orgUnit = orgUnitService.getOrganisationUnit( orgUnitId );
		
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
            newdir.mkdirs();
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";


        Workbook workbook = iscReportHelper.generateISCExcel( orgUnit );
        
        try {
            FileOutputStream out = new FileOutputStream( new File( outputReportPath ) );
            workbook.write( out );
            out.close();
        } 
        catch(FileNotFoundException e){
            System.out.println("Exception whilee streaming excel in ISCReportSingleCountryXLAction: "+ e.getMessage() );
        	e.printStackTrace();
        } 
        catch(IOException e){
        	System.out.println("Exception whilee streaming excel in ISCReportSingleCountryXLAction: "+ e.getMessage() );
            e.printStackTrace();
        }

        fileName = "iSCReport_"+orgUnit.getShortName()+".xlsx";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();

        return SUCCESS;
    }      

}
