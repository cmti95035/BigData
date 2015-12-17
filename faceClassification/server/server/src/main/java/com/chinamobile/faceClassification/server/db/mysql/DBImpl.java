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
        profileHashMap.put("charlie", new Profile().setName("韩建华").setPhone("1-408-8860360").setTitle("资深软件工程师").setProfileId(1));
        profileHashMap.put("jian", new Profile().setName("李俭").setPhone("1-408-2730088").setTitle("资深软件架构师").setProfileId(2));
        profileHashMap.put("rui", new Profile().setName("唐睿").setPhone("1-617-4130163").setTitle("项目经理").setProfileId(3));
        profileHashMap.put("zhimin", new Profile().setName("何志敏").setPhone("1-408-6803612").setTitle("资深软件工程师").setProfileId(4));
        profileHashMap.put("jingxian", new Profile().setName("林景贤").setPhone("1-951-2751055").setTitle("数据分析师").setProfileId(5));
        profileHashMap.put("bin", new Profile().setName("吴滨").setPhone("86-139-10556529").setTitle("项目经理").setProfileId(6));
    }
    @Override
    public Profile getProfileByName(String name) {
        if(profileHashMap.containsKey(name))
            return profileHashMap.get(name);

        return null;
    }
}
