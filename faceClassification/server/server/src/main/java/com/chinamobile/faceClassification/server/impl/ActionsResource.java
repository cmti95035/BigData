package com.chinamobile.faceClassification.server.impl;

import com.chinamobile.faceClassification.server.FaceClassification;
import com.chinamobile.faceClassification.server.FaceImage;
import com.chinamobile.faceClassification.server.Metadata;
import com.chinamobile.faceClassification.server.Profile;
import com.chinamobile.faceClassification.server.ds.DataService;
import com.linkedin.data.template.StringMap;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.RestLiActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@RestLiActions(name = "actions", namespace = "com.chinamobile.faceClassification.server")
public class ActionsResource {
    private static DataService dataService = null;
    private static String ROOTPATH = "/tmp";
    private static String FILE_SEPARATOR = "/";
    private Logger _log = LoggerFactory.getLogger(ActionsResource.class);

    // always prepare for the connection when the service starts
    static {
        dataService = ServerUtils.initService(dataService);
    }

    /**
     * the method that receives a thumbnail photo and then call the backend to return
     * classification results
     * @param faceImage
     * @return
     */
    @Action(name = "classifyPhoto")
    public FaceClassification classifyPhoto(@ActionParam("faceImage") FaceImage faceImage) {

        if(faceImage == null || !faceImage.hasImageContent() || !faceImage.hasImageName()){
            throw new RestLiServiceException(
                    HttpStatus.S_400_BAD_REQUEST,
                    "has to include the binary content of the image and the image name");
        }

        String fileName = ROOTPATH + FILE_SEPARATOR + faceImage.getImageName();
        if(!ServerUtils.writeToFile(faceImage.getImageContent().copyBytes(), fileName)){
            throw new RestLiServiceException(
                    HttpStatus.S_500_INTERNAL_SERVER_ERROR,
                    "write content to file " + faceImage.getImageName() + " failed");
        }

        String command = "/tmp/myscript " + fileName;
        int retValue = 0;
        try {
            Process cmdProc = Runtime.getRuntime().exec(command);


            BufferedReader stdoutReader = new BufferedReader(
                    new InputStreamReader(cmdProc.getInputStream()));
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                _log.debug("command returns:");
                _log.debug(line);
            }

            BufferedReader stderrReader = new BufferedReader(
                    new InputStreamReader(cmdProc.getErrorStream()));
            while ((line = stderrReader.readLine()) != null) {
                _log.error("Error output:");
                _log.error(line);
            }

            retValue = cmdProc.exitValue();
        }catch (IOException ioException){
            _log.error("failed to execute command: " + command );
            _log.error("exit value: " + retValue );
            throw new RestLiServiceException(
                    HttpStatus.S_500_INTERNAL_SERVER_ERROR,
                    "failed to execute command: " + command );
        }

        // TODO: to call Jingxian's Python script to do the classification and generate result
        return new FaceClassification().setMatchedProfile(new Profile()).setMetadata("");
    }
}
