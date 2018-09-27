package org.hisp.dhis.ivb.report.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.EPIProfileSnapshot;
import org.hisp.dhis.ivb.util.EPIProfileHelper;
import org.hisp.dhis.ivb.util.GenericDataVO;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.opensymphony.xwork2.Action;

public class CountryEPIProfileImgUploadAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

	private I18nService i18nService;

	public void setI18nService( I18nService service ){
	    i18nService = service;
	}

	private OrganisationUnitSelectionManager selectionManager;

	public void setSelectionManager( OrganisationUnitSelectionManager selectionManager ){
		this.selectionManager = selectionManager;
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
    
	private MessageService messageService;

	public void setMessageService( MessageService messageService ){
		this.messageService = messageService;
	}

	private ConfigurationService configurationService;

	public void setConfigurationService( ConfigurationService configurationService ){
		this.configurationService = configurationService;
	}

	private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }
    
    private DocumentService documentService;
    
    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }
    
	@Autowired
	private OrganisationUnitGroupService orgUnitGroupService;

    @Autowired 
    EPIProfileHelper epiProfileHelper;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private File fileUpload1;
    private File fileUpload2;
    private File fileUpload3;
    private File fileUpload4;
    private File fileUpload5;
    private File fileUpload6;
    
    private String fileUpload1FileName;
    private String fileUpload1ContentType;
    private String fileUpload2FileName;
    private String fileUpload2ContentType;
    private String fileUpload3FileName;
    private String fileUpload3ContentType;
    private String fileUpload4FileName;
    private String fileUpload4ContentType;
    private String fileUpload5FileName;
    private String fileUpload5ContentType;
    private String fileUpload6FileName;
    private String fileUpload6ContentType;
    
    /*
    private File[] fileUpload;
    private String[] fileUploadFileName;
    private String[] fileUploadContentType;
    
    
    public File[] getFileUpload() {
    	return fileUpload;
    }
    public void setFileUpload(File[] fileUploads) {
    	this.fileUpload = fileUploads;
    }
    public String[] getFileUploadFileName() {
    	return fileUploadFileName;
    }
    public void setFileUploadFileName(String[] fileUploadFileNames) {
    	this.fileUploadFileName = fileUploadFileNames;
    }
    public String[] getFileUploadContentType() {
    	return fileUploadContentType;
    }
    public void setFileUploadContentType(String[] fileUploadContentTypes) {
    	this.fileUploadContentType = fileUploadContentTypes;
    }
    */
    
	public void setFileUpload1(File fileUpload1) {
		this.fileUpload1 = fileUpload1;
	}
	public void setFileUpload2(File fileUpload2) {
		this.fileUpload2 = fileUpload2;
	}
	public void setFileUpload3(File fileUpload3) {
		this.fileUpload3 = fileUpload3;
	}
	public void setFileUpload4(File fileUpload4) {
		this.fileUpload4 = fileUpload4;
	}
	public void setFileUpload5(File fileUpload5) {
		this.fileUpload5 = fileUpload5;
	}
	public void setFileUpload6(File fileUpload6) {
		this.fileUpload6 = fileUpload6;
	}


	public void setFileUpload1FileName(String fileUpload1FileName) {
		this.fileUpload1FileName = fileUpload1FileName;
	}
	public void setFileUpload1ContentType(String fileUpload1ContentType) {
		this.fileUpload1ContentType = fileUpload1ContentType;
	}

	public void setFileUpload2FileName(String fileUpload2FileName) {
		this.fileUpload2FileName = fileUpload2FileName;
	}
	public void setFileUpload2ContentType(String fileUpload2ContentType) {
		this.fileUpload2ContentType = fileUpload2ContentType;
	}

	public void setFileUpload3FileName(String fileUpload3FileName) {
		this.fileUpload3FileName = fileUpload3FileName;
	}
	public void setFileUpload3ContentType(String fileUpload3ContentType) {
		this.fileUpload3ContentType = fileUpload3ContentType;
	}

	public void setFileUpload4FileName(String fileUpload4FileName) {
		this.fileUpload4FileName = fileUpload4FileName;
	}
	public void setFileUpload4ContentType(String fileUpload4ContentType) {
		this.fileUpload4ContentType = fileUpload4ContentType;
	}

	public void setFileUpload5FileName(String fileUpload5FileName) {
		this.fileUpload5FileName = fileUpload5FileName;
	}
	public void setFileUpload5ContentType(String fileUpload5ContentType) {
		this.fileUpload5ContentType = fileUpload5ContentType;
	}

	public void setFileUpload6FileName(String fileUpload6FileName) {
		this.fileUpload6FileName = fileUpload6FileName;
	}
	public void setFileUpload6ContentType(String fileUpload6ContentType) {
		this.fileUpload6ContentType = fileUpload6ContentType;
	}

	
	private String orgUnitId;
    public String getOrgUnitId() {
		return orgUnitId;
	}
	public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    
    private String uploadStatus;
	public String getUploadStatus() {
		return uploadStatus;
	}

	private String language;
	public String getLanguage(){
	    return language;
	}
	private String userName;
	public String getUserName(){
	    return userName;
	}
	private int messageCount;
	public int getMessageCount(){
	    return messageCount;
	}
	private String adminStatus;
	public String getAdminStatus(){
	    return adminStatus;
	}
    
	private Map<String, Map<Integer,GenericDataVO>> countrywiseEPIChartsMap;
    public Map<String, Map<Integer,GenericDataVO>> getCountrywiseEPIChartsMap() {
		return countrywiseEPIChartsMap;
	}
	
	private String chartMapJson;
	public String getChartMapJson(){
	    return chartMapJson;
	}

	private List<OrganisationUnit> epiOrgUnits = new ArrayList<OrganisationUnit>();
	public List<OrganisationUnit> getEpiOrgUnits() {
		return epiOrgUnits;
	}

	String successImages = "";
	String failImages = "-";

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute() throws Exception
    {
		System.out.println( "orgUnitId: "+ orgUnitId );
		OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
		
	    OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroupByCode( EPIProfileSnapshot.EPI_COUNTRY_OUGROUP );
	    if( orgUnitGroup != null ){
	    	epiOrgUnits.addAll( orgUnitGroup.getMembers() );
	    	Collections.sort( epiOrgUnits, new IdentifiableObjectNameComparator() );
	    }

		//SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss");
		try{
			
			// copy the uploaded files into pre-configured location (ie. DHIS2_HOME/documents)
			//Image1
			createDocument( fileUpload1, fileUpload1FileName, fileUpload1ContentType, "01", orgUnit );
			
			//Image2
			createDocument( fileUpload2, fileUpload2FileName, fileUpload2ContentType, "02", orgUnit ); 
			
			//Image3
			createDocument( fileUpload3, fileUpload3FileName, fileUpload3ContentType, "03", orgUnit );
			
			//Image4	
			createDocument( fileUpload4, fileUpload4FileName, fileUpload4ContentType, "04", orgUnit ); 

			//Image5
			createDocument( fileUpload5, fileUpload5FileName, fileUpload5ContentType, "05", orgUnit ); 

			//Image6
			createDocument( fileUpload6, fileUpload6FileName, fileUpload6ContentType, "06", orgUnit ); 

			/*try{
	            if( fileUpload1ContentType != null && fileUpload1ContentType.trim().equalsIgnoreCase("image/png") ){
	            	String docName = "EPI_"+orgUnit.getCode()+"_IMG01";
	            	String uniqueFileName = docName+".png";
	            	Document document = new Document();
	            	List<Document> documents = documentService.getDocumentByName( docName );
	            	if( documents !=null && documents.size() > 0 )
	            		document = documents.get(0);
	            	
	            	String destPath = System.getenv("DHIS2_HOME") + File.separator + DocumentService.DIR + File.separator + uniqueFileName;
	            	File destinationFile = new File( destPath );
	            	destinationFile.delete();
	                boolean fileMoved = fileUpload1.renameTo( destinationFile );
	                document.setUrl( uniqueFileName );
	                document.setContentType( fileUpload1ContentType );
	                document.setAttachment( false );
	                document.setName( docName );
	                document.setSource( orgUnit );
	                
	                documentService.saveDocument( document );
	                successImages += "1,";
	            }
        	}
        	catch(Exception e )
        	{
        		failImages +=  "1,";
        		e.printStackTrace();
        	}
			*/
			
			/*
	        for( int i = 0; i < fileUpload.length; i++) {
	        	try{
		            File uploadedFile = fileUpload[i];
		            String fileName = fileUploadFileName[i];
		            String contentType = fileUploadContentType[i];
		            System.out.println( fileName + " - " + contentType );
		            if( contentType != null && contentType.trim().equalsIgnoreCase("image/png") ){
		            	String docName = "EPI_"+orgUnit.getCode()+"_IMG";
		            	docName+= ((i+1)<=9)?  "0"+(i+1) : (i+1);
		            	String uniqueFileName = docName+".png";
		            	Document document = new Document();
		            	List<Document> documents = documentService.getDocumentByName( docName );
		            	if( documents !=null && documents.size() > 0 )
		            		document = documents.get(0);
		            	
		            	String destPath = System.getenv("DHIS2_HOME") + File.separator + DocumentService.DIR + File.separator + uniqueFileName;
		            	File destinationFile = new File( destPath );
		            	destinationFile.delete();
		                boolean fileMoved = uploadedFile.renameTo( destinationFile );
		                document.setUrl( uniqueFileName );
		                document.setContentType( contentType );
		                document.setAttachment( false );
		                document.setName( docName );
		                document.setSource( orgUnit );
		                
		                documentService.saveDocument( document );
		                successImages += (i+1) + ",";
		            }
	        	}
	        	catch(Exception e )
	        	{
	        		failImages += (i+1) + ",";
	        		e.printStackTrace();
	        	}
	        }
	        */
			
	        uploadStatus = "EPI Report charts/graphs/maps loaded successfully: "+successImages + " and failed: "+ failImages +" for country: "+orgUnit.getName();
	        countrywiseEPIChartsMap = epiProfileHelper.getCountrywiseEPICharts();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			chartMapJson = ow.writeValueAsString(countrywiseEPIChartsMap);
		}
		catch( Exception e ){
			System.out.println("Exception in CountryEPIProfileImgUploadAction while loading EPI Report charts/graphs for country: "+orgUnit.getName() );
			uploadStatus = "Unable to load charts/graphs, please contact admin";
			e.printStackTrace();
		}
		
		System.out.println(uploadStatus);
		//jsonResp.put("result", new JSONObject(dataJSON) );
		
		
		userName = currentUserService.getCurrentUser().getUsername();
	    if( i18nService.getCurrentLocale() == null )
	        language = "en";
	    else
	        language = i18nService.getCurrentLocale().getLanguage();
	    messageCount = (int) messageService.getUnreadMessageConversationCount();
	    List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
	    if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) )
	    	adminStatus = "Yes";
	    else
	    	adminStatus = "No";

        return SUCCESS;
    }
	
	private void createDocument( File uploadedFile, String fileName, String contentType, String imgNo, OrganisationUnit orgUnit )
	{
    	try{
    		System.out.println( contentType + " -- " + fileName );
            if( contentType != null && contentType.trim().equalsIgnoreCase("image/png") ){
            	String docName = "EPI_"+orgUnit.getCode().replaceAll("_", "-")+"_IMG"+imgNo;
            	String uniqueFileName = docName+".png";
            	Document document = new Document();
            	List<Document> documents = documentService.getDocumentByName( docName );
            	if( documents !=null && documents.size() > 0 )
            		document = documents.get(0);
            	
            	String destPath = System.getenv("DHIS2_HOME") + File.separator + DocumentService.DIR + File.separator + uniqueFileName;
            	File destinationFile = new File( destPath );
            	destinationFile.delete();
                boolean fileMoved = uploadedFile.renameTo( destinationFile );
                document.setUrl( uniqueFileName );
                document.setContentType( contentType );
                document.setAttachment( false );
                document.setName( docName );
                document.setSource( orgUnit );
                
                documentService.saveDocument( document );
                successImages += imgNo + " "; 
            }
    	}
    	catch(Exception e )
    	{
    		e.printStackTrace();
    		failImages += imgNo + " ";
    	}
	}
}
