package com.jaybee291998.upload;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.jaybee291998.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


public class DriveQuickStart {

    private static ClassLoader classLoader = Main.class.getClassLoader();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "gdrive upload service";
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), "credentials/credentials.txt"
    );
    private String TOKENS_DIRECTORY_PATH;
    private static final String CLIENT_SECRET_FILE = "credentials/credentials.json";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    public DriveQuickStart(String tokenPath) {
        this.TOKENS_DIRECTORY_PATH = tokenPath;
    }

    public File uploadFile(String filePath) throws IOException, GeneralSecurityException {
        Drive service = getInstance();
        java.io.File fileContent = new java.io.File(filePath);
        File fileMetadata = new File();
        fileMetadata.setName(fileContent.getName());
        FileContent mediaContent = new FileContent("application/octet-stream", fileContent);

        return service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = classLoader.getResourceAsStream(CLIENT_SECRET_FILE);
        if(in == null) {
            throw new FileNotFoundException("Resource not found: " + CLIENT_SECRET_FILE);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    private Drive getInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }
}
