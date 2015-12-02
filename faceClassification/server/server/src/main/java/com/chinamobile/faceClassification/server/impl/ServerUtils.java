package com.chinamobile.faceClassification.server.impl;

import com.chinamobile.faceClassification.server.ds.DataService;
import com.chinamobile.faceClassification.server.ds.DataServiceImpl;
public class ServerUtils {

    private static String dbName = "faceClassification_schema";
    private static String dbUrl = "localhost";
    private static String userName = "faceClassification";
    private static String password = "faceClassification";
    
    /**
     * lazy initialization
     *
     * @param ds
     * @return
     */
    public static DataService initService(DataService ds) {
        if (ds == null) {
            ds = new DataServiceImpl(dbUrl, dbName, userName, password);
        }

        return ds;
    }
}
