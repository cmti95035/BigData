package com.chinamobile.faceClassification.server.db.mysql;

import com.chinamobile.faceClassification.server.Profile;
import com.chinamobile.faceClassification.server.db.FaceClassificationDB;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Random;

public class DBImpl implements FaceClassificationDB {

    private Connection conn;
    private final String SELECT_LAST_ID = "SELECT LAST_INSERT_ID()";
    private final String SELECT_ROW_COUNT = "SELECT ROW_COUNT();";
    private static Random random = new Random();
    private static HashMap<String, Profile> profileHashMap = new HashMap<String, Profile>();

    public DBImpl(String dbUrl, String dbName, String userName,
                  String password) {
        this.conn = DBUtilities
                .getConnection(dbUrl, dbName, userName, password);
    }

    static{
        profileHashMap.put("charlie", new Profile().setName("Charlie Han").setPhone("4082730088").setTitle("Principal Software Engineer").setProfileId(1));
        profileHashMap.put("jian", new Profile().setName("Jian Li").setPhone("4082730088").setTitle("Principal Software Architect").setProfileId(2));
        profileHashMap.put("rui", new Profile().setName("Rui Tang").setPhone("4082730088").setTitle("Product Manager").setProfileId(3));
        profileHashMap.put("zhimin", new Profile().setName("Zhimin He").setPhone("4082730088").setTitle("Principal Software Engineer").setProfileId(4));
        profileHashMap.put("jingxian", new Profile().setName("Jingxian Lin").setPhone("4082730088").setTitle("Data Analyst").setProfileId(5));
    }
    @Override
    public Profile getProfileByName(String name) {
        if(profileHashMap.containsKey(name))
            return profileHashMap.get(name);

        return null;
    }
}
