package com.itshidu.common.ftp.core;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * FTP 客户端连接池
 * 
 * @author 夏增明
 *
 */
public class FTPClientPool {

	//ftp客户端连接池
	private GenericObjectPool<FTPClient> pool;

	//ftp客户端工厂
	private FTPClientFactory clientFactory;

	/**
	 * 构造函数中 注入一个bean
	 * @param clientFactory
	 */
	public FTPClientPool(FTPClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		pool = new GenericObjectPool<FTPClient>(clientFactory, clientFactory.getFtpPoolConfig());

	}

	public FTPClientFactory getClientFactory() {
		return clientFactory;
	}

	public GenericObjectPool<FTPClient> getPool() {
		return pool;
	}

	/**
	 * 从池子中借一个连接对象
	 * @return (借来的FTPClient对象)
	 * @throws Exception (异常)
	 */
	public FTPClient borrowObject() throws Exception {
		FTPClient client = pool.borrowObject();
		return client;
	}

	/**
	 * 归还一个连接对象到池子中
	 * @param ftpClient (归还的连接)
	 */
	public void returnObject(FTPClient ftpClient) {
		if (ftpClient != null) {
			pool.returnObject(ftpClient);
		}
	}
}