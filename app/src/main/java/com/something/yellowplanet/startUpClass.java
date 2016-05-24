package com.something.yellowplanet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by new user on 5/18/2016.
 */
public class startUpClass extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            FeatureCollectionService collectService = new FeatureCollectionService(context);
            collectService.startCollection();
        }
    }

}
