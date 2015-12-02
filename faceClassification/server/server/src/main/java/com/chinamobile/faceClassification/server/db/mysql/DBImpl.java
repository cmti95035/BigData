package com.chinamobile.faceClassification.server.db.mysql;

import com.chinamobile.faceClassification.server.Profile;
import com.chinamobile.faceClassification.server.db.SocialDB;

import java.sql.Connection;
import java.util.Random;

public class DBImpl implements SocialDB {

    private Connection conn;
    private final String SELECT_LAST_ID = "SELECT LAST_INSERT_ID()";
    private final String SELECT_ROW_COUNT = "SELECT ROW_COUNT();";
    private static Random random = new Random();

    public DBImpl(String dbUrl, String dbName, String userName,
                  String password) {
        this.conn = DBUtilities
                .getConnection(dbUrl, dbName, userName, password);
    }

    @Override
    public Profile getProfileByName(String name) {
        return null;
    }
}
