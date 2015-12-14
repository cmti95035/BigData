package com.chinamobile.faceClassification.server;

import com.linkedin.data.ByteString;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.*;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.IdResponse;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.ActionRequest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class FaceClassificationClient {
    // Create an HttpClient and wrap it in an abstraction layer
    private static final HttpClientFactory http = new HttpClientFactory();
    private static final Client r2Client = new TransportClientAdapter(
            http.getClient(Collections.<String, String>emptyMap()));
    private static final String BASE_URL = "http://54.153.110.173:6666/";
//    private static final String BASE_URL = "http://localhost:6666/";
    private static RestClient restClient = new RestClient(r2Client, BASE_URL);
    public static String ALPHABETES = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789_#?!&*";
    public static String NUMBERS = "0123456789";
    public static int PHONE_LENGTH = 10;
    private static Random random = new Random();
    private static ActionsRequestBuilders actionsRequestBuilders = new ActionsRequestBuilders();

    public static void main(String[] args) throws Exception {
        classifyImage("/Users/jianli/Downloads/409.jpg");
    }

    private static FaceClassification classifyImage(String fileName){
//        FaceImage faceImage = new FaceImage().setImageName(getBaseName(fileName));
        FaceImage faceImage = new FaceImage().setImageName(getBaseName(fileName)).setImageContent(ByteString.copy(readFromFile(fileName)));

        ActionRequest<FaceClassification> actionRequest = actionsRequestBuilders.actionClassifyPhoto().faceImageParam(faceImage).build();

        try{
            ResponseFuture<FaceClassification> responseFuture = restClient.sendRequest(actionRequest);
            Response<FaceClassification> response = responseFuture.getResponse();

            FaceClassification faceClassification = response.getEntity();
            System.out.println("\nclassifyImage returns: " + (faceClassification == null ? "null" : faceClassification));

            return faceClassification;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getBaseName(String fileName){
        if(fileName == null)
            return null;

        String[] parts = fileName.split("/");
        return parts[parts.length - 1];
    }

    private static byte[] readFromFile(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

     /** generate a random string with numeric alphabets to a specified length
     * @param length
     * @return
     */
    public static String generateRandomString(int length)
    {
        if(length<=0)
            length = 8;

        StringBuilder sb = new StringBuilder(length);

        for(int i=0; i<length; i++){
            sb.append(ALPHABETES.charAt(random.nextInt(ALPHABETES.length())));
        }

        return sb.toString();
    }

    public static String generateRandomNumbers(int length)
    {
        if(length<=0)
            length = PHONE_LENGTH;

        StringBuilder sb = new StringBuilder(length);

        for(int i=0; i<length; i++){
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }

        return sb.toString();
    }

    /**
     * utility to create hash based on user's phone and password
     * @param phone
     * @param password
     * @return
     */
    private static String generateHash(String phone, String password){
        String data = phone + password;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hash = new BigInteger(1, digest.digest()).toString(16);

        return hash;
    }

}

