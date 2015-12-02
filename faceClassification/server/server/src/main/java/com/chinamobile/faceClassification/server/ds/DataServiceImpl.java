package com.chinamobile.faceClassification.server.ds;

import com.chinamobile.faceClassification.server.Profile;
import com.chinamobile.faceClassification.server.db.SocialDB;
import com.chinamobile.faceClassification.server.db.mysql.DBImpl;

public class DataServiceImpl implements DataService{

	private String dbUrl;
	private String dbName;
	private String userName;
	private String password;
	private SocialDB db;
	
	public DataServiceImpl(String url, String name, String uName, String pwd)
	{
		this.dbName = name;
		this.dbUrl = url;
		this.password = pwd;
		this.userName = uName;
		
		this.db = new DBImpl(dbUrl, dbName, userName, password);
	}

	@Override
	public Profile getProfileByName(String name) {
		return null;
	}
}
