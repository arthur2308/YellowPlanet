package com.something.yellowplanet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
    public FeatureCollectionService(Context newContext)
    {
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
                    Log.d("feature", "");
                    Map dataResults = getAllFeatures();
                    for(int i = 0; i < allFeatures[0].length; i++)
                    {
                        Log.d("feature ", "type: " + allFeatures[0][i] +" val: " + dataResults.get(allFeatures[0][i]));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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


}
