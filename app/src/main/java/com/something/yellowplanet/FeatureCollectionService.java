package com.something.yellowplanet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import java.io.Console;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by new user on 5/19/2016.
 */

public class FeatureCollectionService
{
    final String[][] allFeatures = {{"cpu", "ram", "network in", "network out", "battery"}, //array 0 is feature names
                                    {"default", "default", "default", "default", "default"}}; //array 1 is arguments
    final int timerSeconds = 10000;
    private featureExtractor features;
    private Timer stopWatch;
    private Context contextToUse;
    public FeatureCollectionService(Context newContext)
    {
        contextToUse = newContext;
        features = new featureExtractor(newContext);
        stopWatch = new Timer("featureTimer");
    }

    public void startCollection()
    {
        Log.d("feature", "starting feature extraction!");
        TimerTask collectDataTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Log.d("feature", "FEATURE COLLECTION FOR: " + System.currentTimeMillis());
                    Map dataResults = getAllFeatures();
                    for(int i = 0; i < allFeatures[0].length; i++)
                    {
                        Log.d("feature ", "type: " + allFeatures[0][i] +" val: " + dataResults.get(allFeatures[0][i]));
                        checkIfAnomolyDetected(allFeatures[0][i], (double)dataResults.get(allFeatures[0][i]));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("feature","reason: " + e.getMessage());
                }
            }
        };
        stopWatch.scheduleAtFixedRate(collectDataTask,timerSeconds,timerSeconds);
    }

    private Map getAllFeatures() throws Exception
    {
        Map retMap = new HashMap();
        for(int i = 0; i < allFeatures[0].length; i++)
        {
            String featureName = allFeatures[0][i];
            String featureArgs = allFeatures[1][i];
            double featureVal = features.getFeature(featureName, featureArgs);
            retMap.put(featureName,featureVal);
        }
        return(retMap);
    }

    /*------------------check if anomoloy detected---------------------
        checks if anomoly is detected
        if detected, warning activity is launched
     */
    private void checkIfAnomolyDetected(String featureName, double featureVal)
    {
        if(featureName.equals("network in") && featureVal > 1000000)
        {
            Intent i = new Intent(contextToUse, WarningActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contextToUse.startActivity(i);
        }
    }


}
