package com.chinamobile.faceClassification.server.impl;

import com.chinamobile.faceClassification.server.FaceClassification;
import com.chinamobile.faceClassification.server.FaceImage;
import com.chinamobile.faceClassification.server.ds.DataService;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.RestLiActions;


@RestLiActions(name = "actions", namespace = "com.chinamobile.faceClassification.server")
public class ActionsResource {
    private static DataService dataService = null;

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

        if(faceImage == null || !faceImage.hasImageContent()){
            throw new RestLiServiceException(
                    HttpStatus.S_400_BAD_REQUEST,
                    "has to include the binary content of the image");
        }

        // TODO: to call Jingxian's Python script to do the classification and generate result
        return null;
    }
}
