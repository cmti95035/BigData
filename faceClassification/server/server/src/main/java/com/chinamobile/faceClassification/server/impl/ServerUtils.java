package com.chinamobile.faceClassification.server.impl;

import com.chinamobile.faceClassification.server.db.mysql.DBUtilities;
import com.chinamobile.faceClassification.server.ds.DataService;
import com.chinamobile.faceClassification.server.ds.DataServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ServerUtils {

    private static String dbName = "fClassification_schema";
    private static String dbUrl = "localhost";
    private static String userName = "fClassification";
    private static String password = "fClassification";

    private static Logger _log = LoggerFactory.getLogger(ServerUtils.class);
    
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

    public static boolean writeToFile(byte[] fileContent, String fileName) {
        String methodName = "writeToFile";
        final File tempFile = new File(fileName);
        FileOutputStream tmpOutputStream = null;
        try {
            tmpOutputStream = new FileOutputStream(tempFile);
            tmpOutputStream.write(fileContent);

            return true;
        } catch (FileNotFoundException e) {
            _log.error(methodName + "  writeToFile Encountered exception: " + e.getMessage());
            DBUtilities.printStackTrace(_log, e.getStackTrace());
            return false;
        } catch (IOException e) {
            _log.error(methodName + "  writeToFile Encountered exception: " + e.getMessage());
            DBUtilities.printStackTrace(_log, e.getStackTrace());
            return false;
        } catch (Exception e) {
            _log.error(methodName + "  writeToFile Encountered exception: " + e.getMessage());
            DBUtilities.printStackTrace(_log, e.getStackTrace());
            return false;
        } finally {
            if (tmpOutputStream != null)
                try {
                    tmpOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
