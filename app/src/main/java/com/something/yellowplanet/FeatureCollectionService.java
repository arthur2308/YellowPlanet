package com.something.yellowplanet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.Console;

/**
 * Created by new user on 5/19/2016.
 */
public class FeatureCollectionService extends IntentService
{

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public FeatureCollectionService() {
        super("HelloIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: START COLLECTING DATA;
        Log.d("feature", "STARTING SERVICE!");
        return super.onStartCommand(intent,flags,startId);
    }
}
