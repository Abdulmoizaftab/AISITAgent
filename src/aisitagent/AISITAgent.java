/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aisitagent;

/**
 *
 * @author aaftab
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AISITAgent {

    /**
     * @param args the command line arguments
     */
    
      public static Logger logger = LogManager.getLogger(AISITAgent.class.getName());
    public static void main(String[] args) {
       
        
        
        
        
        
        
        String ImagePath=null;
        String DeviceID=null;
        String imlastmodtime=null;
         String server = "";
            int port = 0;
            String user = "";
            String pass = "";
            String LastImageUploaded = "";
            int ImageUploadingInterval = 60;
            int RetryConnFailureInterval = 60;
            int SendImage = 0;
            int FolderDate = 0;
            int LastDate1 = 0;
            int imagestransfered=0;
            OutputStream output = null;
           boolean thumbsFolder=false;
           boolean thumbsFiles=false;
             String Dateold="";
        String Date="";
      
            // TODO code application logic here
            System.setProperty("java.net.preferIPv4Stack", "true");
            
            try {
                        
                       Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { 
            public void uncaughtException(Thread t, Throwable e) { 
              
                logger.error("Error: UncaughtException:",e);
                
            }
        });
                        
                        
                        
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        logger.error("Error:", e);
                    }
           
                    
            
            logger.info("Application Started");
            try{
                           
                        
                            
                            
// get the registry values

ImagePath=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImagePath");
DeviceID=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ATMID");
server=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","Server");
port=Integer.parseInt(WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","Port"));
user=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","Username");
pass=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","Password");
LastImageUploaded=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploaded");
Dateold=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploadedDate");
ImageUploadingInterval=Integer.parseInt(WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploadingInterval"));
RetryConnFailureInterval=Integer.parseInt(WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","RetryConnFailureInterval"));
System.out.println("Server: "+server);
logger.info("Server: "+server);
System.out.println("port: "+port);
logger.info("port: "+port);
System.out.println("user: "+user);
logger.info("user: "+user);
System.out.println("pass: "+pass);
System.out.println("LastImageUploaded: "+LastImageUploaded);
logger.info("LastImageUploaded: "+LastImageUploaded);
logger.info("Image Path: "+ImagePath);
logger.info("ATM ID: "+DeviceID);





// connectionUrl = "jdbc:sqlserver://10.21.21.201:1433;databaseName=CEJR;user=proview1234;password=ipl1234";
                    
                        } catch (IllegalArgumentException ex) {
              logger.error("Error:", ex);
          } catch (IllegalAccessException ex) {
              logger.error("Error:", ex);
          } catch (InvocationTargetException ex) {
              logger.error("Error:", ex);
          }
            
            ImagePath=ImagePath+"\\"+DeviceID;
            boolean run=true;
            while(run!=false){
                  try {
            FTPClient ftpClient = new FTPClient();
            try{
              ftpClient.connect(server, port);
            }
            catch(ConnectException ex){
           // Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
         if(ex.toString().contains("Connection refused")){             
         logger.info("Connection to Server ("+server+") Connection Not possible");         
         }
         else if(ex.toString().contains("Connection timed out")){             
         logger.info("Connection timed out to Server("+server+")  Connection Not possible");         
         }
         else if(ex.toString().contains("No route to host")){             
         logger.info("No route to host("+server+"), Check Network Cable");         
         }
         
         else{
         logger.error( "Error:",ex);
         }
            Thread.sleep(RetryConnFailureInterval*1000);
            continue;
            
            }
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 
                
                
              File dir = new File(ImagePath);
              System.out.println("1"+dir.getAbsoluteFile());         
              if(!dir.exists()){
                        imlastmodtime="Never";
       if (imlastmodtime=="Never"){
       }
     
                       }
                       else{
                            
    File[] Folders = dir.listFiles();
     System.out.println("Folders length="+Folders.length);
    if (Folders == null || Folders.length == 0) {
       imlastmodtime="Never";
       if (imlastmodtime=="Never"){}
    }
    else{
    for (int p = 0; p < Folders.length; p++) {
        if(Folders[p].getName().equals("Thumbs.db")){
            if (thumbsFolder==false){
        logger.info("Thumbs.db found at "+Folders[p].getAbsolutePath());
        thumbsFolder=true;
            }
            
        }
        else{
       try{
        FolderDate=Integer.parseInt(Folders[p].getName());
       }
       catch (NumberFormatException e) {
   System.out.println(e);
   logger.info(Folders[p].getName());
    logger.error( "Error:",e);
    FolderDate=LastDate1;
  }
   
       Dateold=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploadedDate");
       if (Dateold.equals("Never")){Dateold="1";}
        if (FolderDate>=Integer.parseInt(Dateold)){
           Date=Folders[p].getName();
            if(FolderDate>Integer.parseInt(Dateold)){
               thumbsFolder=false;
            thumbsFiles=false;
                
                
                 WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploadedDate",Date);
                  WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploaded","Never");
                
        }
            
        String path2=(ImagePath+"\\"+Folders[p].getName()+"\\");
        System.out.println("path2="+path2);
        File imdir=new File(path2);
        System.out.println("imdir="+imdir.getAbsolutePath());
       
        File[] files=imdir.listFiles();
        
        if (files == null || files.length == 0) {
      System.out.println("Empty");
    }
    else{
             
                 Arrays.sort(files);
                 System.out.println("Folders length="+files.length);
                 AISITAgent FTPUtil= new AISITAgent();   
                 String dirPath = "/"+DeviceID+"/"+Date+"/";
                 System.out.println("RemotePath="+dirPath);
                 ftpClient.changeWorkingDirectory("/");
                 FTPUtil.makeDirectories(ftpClient, dirPath);
                 
                 for (int i=0;i<files.length;i++){
               
                    
                     if(files[i].getName().equals("Thumbs.db")){
                      if(thumbsFiles==false){
                            logger.info("Thumbs.db found at "+files[i].getAbsolutePath());
                          thumbsFiles=true;
                     }
                         
   
        
        }
        else{
                         
                File path=files[i];
            //System.out.println(path.getAbsolutePath());
            File firstLocalFile = new File(""+path);
 
            String firstRemoteFile = dirPath+path.getName();
            //System.out.println(firstRemoteFile);
             LastImageUploaded=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploaded");
              //System.out.println("LastImageUploaded: "+LastImageUploaded);
             if (LastImageUploaded.equals("Never")){
             SendImage=1;
             }
             else{
             
             String part[]=LastImageUploaded.split("_");
             int ImageSentTime=Integer.parseInt(part[0]);
             String part1[]=path.getName().split("_");
             int ImageCurrTime=Integer.parseInt(part1[0]);
             //System.out.println(ImageCurrTime+">"+ImageSentTime);
             if (ImageCurrTime>ImageSentTime){
             SendImage=1;
             }
             else{
             SendImage=0;
             }
             }
            
            
            if (SendImage!=0){
            InputStream inputStream = new FileInputStream(firstLocalFile);
 
            System.out.println("Trying to upload file: "+path.getName());
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("File Uploaded: "+path.getName());
                imagestransfered=imagestransfered+1;
            }
            else{
            System.out.println("File Uploading Failed: "+path.getName());
            }
                 
		// set the registry value
		
		  WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploaded",path.getName());

	
	
            }
            
            }
                 }
                  if(imagestransfered>0){
            logger.info("No. of Images tranfered: "+imagestransfered);
            imagestransfered=0;
            logger.info("Image Uploaded Date: "+Date+" ,LastImageUploaded: "+LastImageUploaded);
                  
                 }
        }
        }
    }
    }
    }
   
                LastImageUploaded=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\AIS","ImageUploaded");
                System.out.println("LastImageUploaded: "+LastImageUploaded);
                
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
         
              }
           
        
            
            Thread.sleep(ImageUploadingInterval*1000);
            
        } catch (IOException ex) {
            
           
            
           if(ex.toString().contains("No route to host")){          ;   
         logger.info("No route to host("+server+"), Check Network Cable");       
                try {
                    Thread.sleep(RetryConnFailureInterval*1000);
                     continue;
                } catch (InterruptedException ex1) {
                  //  Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex1);
                }
                
                
                        }
           
           else if(ex.toString().contains("SocketExcpetion")){             
         logger.info("No route to host("+server+"), Check Network Cable");       
                try {
                    Thread.sleep(RetryConnFailureInterval*1000);
                     continue;
                } catch (InterruptedException ex1) {
                  //  Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex1);
                     logger.error("Error:", ex1);
                }
                
                
                        }
           
           else{
               try {
                  // Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
                   logger.error("Error:", ex);
                  Thread.sleep(RetryConnFailureInterval*1000);
            
               } catch (InterruptedException ex1) {
                  // Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex1);
                   logger.error( "Error:",ex1);
               }
           
           }
        } catch (InterruptedException ex) {
            //Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
            logger.error( "Error:",ex);
        }   catch (IllegalArgumentException ex) {
                java.util.logging.Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                java.util.logging.Logger.getLogger(AISITAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        
      
    }
    
            
      
        
    }
    
 
    
     public static boolean makeDirectories(FTPClient ftpClient, String dirPath)
            throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        System.out.println("CREATED directory: " + singleDir);
                        logger.error("FTP CREATED directory: " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        System.out.println("COULD NOT create directory: " + singleDir);
                        logger.error("FTP COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    
}
      