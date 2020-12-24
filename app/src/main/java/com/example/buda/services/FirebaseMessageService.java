package com.example.buda.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {
    private final String TAG = "FirebaseMessageService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            Log.d(TAG, remoteMessage.getData().get("title"));
        }
    }
}