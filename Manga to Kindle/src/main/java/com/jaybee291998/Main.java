package com.jaybee291998;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.jaybee291998.upload.DriveQuickStart;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class Main {
    private static final String UPLOAD_FILE_PATH = "credentials/credentials.json";
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        System.out.println("Hello world!");
        if(args.length < 2) {
            throw new IllegalArgumentException("Please provide file path of the file to upload, and the token directory");
        }
        String FILE_PATH = args[0];
        String tokenPath = args[1];
        DriveQuickStart q = new DriveQuickStart(tokenPath);
        File uploadedFile = q.uploadFile(FILE_PATH);
        System.out.println(uploadedFile);
        System.out.println(args[0] + ", " + args[1]);
    }
}