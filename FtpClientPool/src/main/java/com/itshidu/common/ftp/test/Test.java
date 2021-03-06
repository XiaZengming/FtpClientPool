package com.itshidu.common.ftp.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.itshidu.common.ftp.config.FtpPoolConfig;
import com.itshidu.common.ftp.core.FTPClientFactory;
import com.itshidu.common.ftp.core.FTPClientPool;
import com.itshidu.common.ftp.core.FtpClientUtils;

public class Test {

	public static void main(String[] args) throws Exception {
		//配置信息
		FtpPoolConfig cfg = new FtpPoolConfig();
		cfg.setHost("192.168.6.80");
		cfg.setPort(21);
		cfg.setUsername("ftpuser");
		cfg.setPassword("123456");

		FTPClientFactory factory = new FTPClientFactory(cfg);//对象工厂
		FTPClientPool pool = new FTPClientPool(factory);//连接池对象
		FtpClientUtils util = new FtpClientUtils(pool); //工具对象

		//FTPClient c = pool.borrowObject();//从池子中借一个FTPClient对象
		int t1=util.mkdirs( "/data/imgs"); //在FTP的工作目录下创建多层目录
		InputStream in = new FileInputStream("D:/001.jpg"); //读取一个本地文件
		int t2=util.store( in, "/data/imgs/2018/09/29", "main.jpg");//上传到FTP服务器
		int t3=util.retrieve( "/data/imgs/2018/09/29/main.jpg", new FileOutputStream("F:/002.jpg"));//从FTP服务器取回文件
		util.delete("/data/imgs/2018/09/29/main.jpg"); //删除FTP服务器中的文件
		//pool.returnObject(c);//把对象归还给池子
		
		System.out.println("目录耗时："+t1);
		System.out.println("上传耗时："+t2);
		System.out.println("下载耗时："+t3);
		
		/*
		FtpClientUtils ftp = new FtpClientUtils(pool);
		ftp.store(localFile, path, filename);
		ftp.store(inputstream, path, filename);
		ftp.retrieve(remote, local);
		ftp.retrieve(remote, outputstream);
		ftp.delete("/demo/1.jpg");
		*/
	}

}
