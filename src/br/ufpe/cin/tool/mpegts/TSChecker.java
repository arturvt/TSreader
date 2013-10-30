/** **************************************************************************
 *
 *  Created By    : Artur Vieira Tenorio (avt2@cin.ufpe.br)
 *  Create Date   : 19th Semptember, 2013
 *
 *  Description   : br.ufpe.cin.tool.mpegts.TSChecker
 *
 *  Notes         :
 *
 *
 *****************************************************************************
 *@author Artur Tenorio(avt2)
 ****************************************************************************/

package br.ufpe.cin.tool.mpegts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import br.ufpe.cin.tool.db.DataBaseManager_working;

public class TSChecker {

    private static TSChecker instance = null;
    
    private String fileName;
    private String operator;
    private long fileLength;
    private int packetSize;
    private String pidsString = new String();
    private int PID = 0;
    
    private TSParser tsParser = null;
    
    private TSChecker() {
    	super();
    }

    public static TSChecker getInstance(){
        if (instance == null) {
            instance = new TSChecker();
        }
        return instance;
    }
    
    public ArrayList<EPGValues> getEPGList() {
    	return this.tsParser.getEITList();
    }
    
    public void insertValuuesInDB() {
    	ArrayList<EPGValues> epgList = tsParser.getEITList();
    	if (epgList != null) {
    		for (EPGValues epg:epgList) {
    			String epgValue = String.format(
    					"CountryCode: %s - Operator: %s - Name: %s - "
    					+ "OriginalID: %d - ShotDescriptor: %s "
    					+ "StartDate: %s - Starttime: %s - Duration: %s", 
    					epg.getContryCode(),
    					this.operator,
    					epg.getName(),
    					epg.getOriginalNetworkID(),
    					epg.getShortDescrition(),
    					epg.getStartDate(),
    					epg.getStartTime(),
    					epg.getDurationTime());
    			
    			System.out.println(epgValue);
				try {
					DataBaseManager_working.insertEPGvalue(
							epg.getContryCode(),epg.getStartDate(), epg.getStartTime(), epg.getDurationTime(), operator, epg.getName(), epg.getShortDescrition());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		tsParser.getEITList().clear();
    	}
    }
    
    public void loadTS (String tsName, String operator) {
        File file = new File(tsName);
        this.fileName = file.getName();
        this.operator = operator;
    	
        try {
        	System.out.println("Gathering TS INFORMATION of "+ file.getName());
    	    
        	this.packetSize = checkTSType(new BufferedInputStream(new FileInputStream(file)));
        	
        	if ( this.packetSize == -1) {
    			System.out.println( "Problem with TS type");
        	} else {
        	
        		System.out.println( "Please wait... ");
        		
    			tsParser = new TSParser(file, this.packetSize);
    			
    			tsParser.startReadTS();
    			
    			
        	}
		} catch (FileNotFoundException e) {
			System.out.println( "File not found. Msg:"+e.getMessage());
		} catch (IOException e) {
			System.out.println( "IO Exception. Msg:"+e.getMessage());
		}
    }

    private int checkTSType(BufferedInputStream bufferInput) throws IOException {
    	
    	int result = -1;
    	
    	byte[] buffer = new byte[512]; // 188 * 2 = 376
    	int ocorrences = 0;
    	 
    	try {
			bufferInput.read(buffer);
	        for (int i = 0; i < buffer.length; i++) {
	            if ((buffer[i] == TSParser.SYNC_BYTE)) {
	            	ocorrences = i;
//	            	System.out.println(i);
	            }
	        }
	        if (ocorrences % 188 == 0) {
	        	result = 188;
	        } else if (ocorrences % 192 == 0) {
	        	result = 192;
	        } else if (ocorrences % 204 == 0) {
	        	result = 204;
	        } else {
	        	result = -1;
	        }
	        if (result != -1) {
	        	return result;
	        }
    	} catch (IOException e) {
    		System.out.println( "Error reading file");
		}
        return result;
    }    

    public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public void setFileLenght(long fileLenght) {
		this.fileLength = fileLenght;
	}

	public String getPidsString() {
        return pidsString;
    }

    public void setPidsString(String pidsString) {
        this.pidsString = pidsString;
    }
    
    public long getFileLenght() {
        return fileLength;
    }

    public String getFileName() {
        return fileName;
    }

}
