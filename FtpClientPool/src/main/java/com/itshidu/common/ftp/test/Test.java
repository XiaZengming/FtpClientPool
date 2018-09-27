package com.itshidu.common.ftp.test;

import org.apache.commons.net.ftp.FTPClient;

import com.itshidu.common.ftp.client.FtpClientUtils;
import com.itshidu.common.ftp.config.FtpPoolConfig;
import com.itshidu.common.ftp.core.FTPClientFactory;

public class Test {

	public static void main(String[] args) throws Exception {
		FtpPoolConfig cfg = new FtpPoolConfig();
		cfg.setHost("192.168.61.110");
		cfg.setPort(21);
		cfg.setUsername("ftpuser");
		cfg.setPassword("123456");
		
		FTPClientFactory factory = new FTPClientFactory(cfg);
		FTPClient c = factory.create();
		FtpClientUtils.mkdirs(c, "/data/test");
		
	}

}
