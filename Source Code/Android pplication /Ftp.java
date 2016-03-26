package com.example.accident;

import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

class Ftp implements Runnable{
	FTPClient ftp = new FTPClient();
	File file;
	FileInputStream in;
	
	Ftp(File f) throws Exception{
		file = f;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			upload();
		}catch(Exception e){}
	}
	
	public void upload() throws Exception{
	   	ftp.connect("ftp.byethost31.com",21);
		if (ftp.login("b31_17661762", "assist"))
		{
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			in = new FileInputStream(file);
		    ftp.storeFile("/htdocs/Drowsy/info.txt", in);
		    in.close();
		    ftp.logout();
		    ftp.disconnect();
		}
	}
}
