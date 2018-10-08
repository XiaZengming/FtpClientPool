package com.itshidu.common.ftp.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

/**
 * FTPClient工具类，提供上传、下载、删除、创建多层目录等功能
 * @author Master.Xia
 */
public class FtpClientUtils {
	
	private static Logger logger =Logger.getLogger(FtpClientUtils.class);
	
	/**
	 * 在FTP的工作目录下创建多层目录
	 * @param client (FTPClient对象)
	 * @param path (路径分隔符使用"/"，且以"/"开头，例如"/data/photo/2018")
	 * @throws IOException (IO异常)
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
	 * 上传文件到FTP工作目录
	 * @param client (FTPClient对象)
	 * @param in (要上传的输入流)
	 * @param path (在工作目录中的路径，示例"/data/2018")
	 * @param filename (文件名，示例"default.jpg")
	 * @throws IOException (IO异常)
	 */
	public void store(FTPClient client,File localFile,String path,String filename) throws IOException {
		InputStream in = new FileInputStream(localFile);
		store(client, in, path, filename);
	}
	/**
	 * 上传文件到FTP工作目录，path示例"/data/2018"，filename示例"default.jpg"
	 * @param client (FTPClient对象)
	 * @param in (要上传的输入流)
	 * @param path (在工作目录中的路径，示例"/data/2018")
	 * @param filename (文件名，示例"default.jpg")
	 * @throws IOException (IO异常)
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
	 * @return (删除成功返回true，删除失败返回false)
	 * @throws Exception (IO异常)
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
	 * @param client (FTP)
	 * @param remote (FTP文件路径，示例"/data/2018/default.jpg")
	 * @param local (保存到本地的位置)
	 * @throws Exception
	 */
	public void retrieve(FTPClient client,String remote,File local) throws Exception{
		retrieve(client, remote, new FileOutputStream(local));
	}
	/**
     * 从FTP工作目录下载remote文件
     * @param remote  (文件路径，示例"/data/2018/default.jpg")
     * @param out (输出流)
     * @throws Exception (异常)
     */
	public void retrieve(FTPClient client,String remote,OutputStream out) throws Exception  {
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
