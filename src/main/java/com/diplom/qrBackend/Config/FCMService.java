package com.diplom.qrBackend.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FCMService {

    public static void initializeFirebaseApp() {
        try {
            InputStream serviceAccount =
                    FCMService.class.getClassLoader().getResourceAsStream("service_account_key.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        public static void sendNotificationToAllDevices(String title, String message) {
        Message fcmMessage = Message.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(message).build())
                .setTopic("news")
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public static void sendNotificationToToken(String title, String message, String token) {
        Message fcmMessage = Message.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(message).build())
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
