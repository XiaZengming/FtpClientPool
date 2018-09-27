package com.itshidu.common.ftp.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;


public class FtpClientUtils {
	
	private static Logger logger =Logger.getLogger(FtpClientUtils.class);
	
	/**
	 * 在FTP的工作目录下创建多层目录（要求path中的路径分隔符使用"/"），例如"/data/photo/2018"，
	 * @param client
	 * @param path
	 * @throws IOException
	 */
	public static void mkdirs(FTPClient client,String path) throws IOException {
		if(path.contains("\\")) {
			throw new RuntimeException("'\\' is not allowed in the path,please use '/'");
		}
		if(!path.startsWith("/")) {
			throw new RuntimeException("please start with '/'");
		}
		String workingDirectory = client.printWorkingDirectory();
		File f = new File(workingDirectory+path);
		List<String> names = new LinkedList<String>();
		while(f!=null&&f.toString().length()>0) {
			names.add(0, f.toString().replaceAll("\\\\", "/"));
			f=f.getParentFile();
		}
		for(String name:names) {
			client.makeDirectory(name);
		}
	}
	/**
	 * 上传文件到FTP工作目录，path示例"/data/2018"，filename示例"default.jpg"
	 * @param client
	 * @param in
	 * @param savePath
	 * @param filename
	 * @throws IOException
	 */
	public void store(FTPClient client,InputStream in,String path,String filename) throws IOException {
		if(path.contains("\\")) {
			throw new RuntimeException("'\\' is not allowed in the path,please use '/'");
		}
		if(!path.startsWith("/")) {
			throw new RuntimeException("please start with '/'");
		}
		synchronized (client) {
			System.out.println("::"+path);
	        mkdirs(client, path);
	        client.changeWorkingDirectory(path);
	        client.setFileType(FTP.BINARY_FILE_TYPE);
	        client.storeFile(filename, in);
		}
	}
	/**
	 * 删除FTP工作目录中的指定文件
	 * @param client
	 * @param pathname (文件路径，示例"/data/2018/default.jpg")
	 * @return
	 * @throws Exception
	 */
	public boolean delete(FTPClient client,String pathname) throws Exception {
	    try {
			return client.deleteFile(pathname);
		} catch(Exception e){
			logger.error("删除文件失败",e);
			throw e;
		}
	}
	/**
     * 从FTP工作目录下载remote文件
     * @param remote  (文件路径，示例"/data/2018/default.jpg")
     * @param out (输出流)
     * @throws Exception
     */
	public void retrieve(FTPClient client,String remote,OutputStream out) throws Exception {
		InputStream in =null;
	    try {
	    	  long start =System.currentTimeMillis();
	    	  in=client.retrieveFileStream(remote);
	    	  long end =System.currentTimeMillis();
	    	  logger.info("ftp下载耗时(毫秒):"+(end-start));
	    	  if(in != null){
	    		  byte[] buffer = new byte[1024];
	    		  for(int len;(len=in.read(buffer))!=-1;) {
	    			  out.write(buffer,0,len);
	    		  }
	    	  }else{
	    		  throw new RuntimeException("FTP Client retrieve Faild.");
	    	  }
		}catch(Exception e){
			logger.error("获取ftp下载流异常",e);
			throw e;
		}finally{
			if (in != null) {  
				in.close();  
			}
			if (out != null) {  
				out.close();  
			}
		}
	}
}
