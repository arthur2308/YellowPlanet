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
        Log.d("feature","IN ON RECIEVE");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i);
        }
    }

}
