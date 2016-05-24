 package com.something.yellowplanet;

        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.ActivityManager;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.BatteryManager;
        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FilenameFilter;
        import java.io.InputStreamReader;
        import java.io.RandomAccessFile;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

/**
 * Created by new user on 5/7/2016.
 */

/*
------------------FEATURE LIST----------------------------------
"Battery" = battery statistics
"Cpu" = cpu usage statistics
"Network out" = network bandwidth out statistic
"Network in" = network bandwidth in statistic
"Ram" = ram usage statistics
"System calls" = system call usage statistics
 */

    /*
----------------VALID ARGUMENT LIST-----------------------------------
"user" = returns data only for user processes
    -cpu
    -ram
"system" = returns the data only for system processes
    -cpu
    -ram
"volts" = returns the data in volts instead of percent
    -battery
"[pid]" = returns the information for the pid passed. do NOT include square brackets, just pid
     */

public class featureExtractor
{
    /*----------------FEATURE NAMES ----------------------------------------*/
    final private String batteryName = "battery";
    final private String cpuName = "cpu";
    final private String networkOutName = "network out";
    final private String networkInName = "network in";
    final private String ramName = "ram";
    final private String threadsName = "threads";
    final private String systemCallName = "system calls";

    /*-------------------ARGUMENT NAMES ------------------------------------*/
    final private String userArg = "user";
    final private String systemArg = "system";
    final private String idleArg  = "idle";
    final private String voltsArg = "volts";

    final private String inUseArg = "in use";
    final private String freeArg = "free";
    final private String defaultArg = "default";

    final private String packetArg = "packets";

    private Context contextToUse;

    /*----------------------old feature values---------------------*/
    int previousNetworkIn = 0;
    int previousNetworkout = 0;

    int previousNetworkInPackets = 0;
    int previousNetworkOutPackets = 0;

    public featureExtractor(Context newContext)
    {
        contextToUse = newContext;
    }

    //------------------------get feature------------------------------------
    //takes a feature from the feature list and returns the statistic
    //if feature name passed does not exist, returns -1
    //you can pass arguments to get specific data, for example to only get
    //cpu time running user processes, pass "user" in args paramater
    //for default returns, pass an empty string into args
    public double getFeature(String featureName, String args) throws Exception {
        switch (featureName.toLowerCase())
        {
            case batteryName:
                return(getBatteryStatistic(args));
            case cpuName:
                return(getCpuStatistic(args));
            case networkOutName:
                return(getNetworkOutStatistics(args));
            case networkInName:
                return(getNetworkInStatistics(args));
            case ramName:
                return(getRamStatistics(args));
            case systemCallName: //NOT IMPLEMENTED
                return(getSystemCallStatistics());
            case threadsName: // NOT IMPLEMENTED
                return(getThreadStatistics(args));
            default:
                return(-1);
        }
    }


    /*---------------- START SECTION FOR GET FEATURE STATISTIC FUNCTIONS -------------------------*/

    //---------------------------------get battery statistic-----------------------------------
    //gets battery infromation from the system
    //possible args:
    //              "volts" - gets the battery level in volts
    //
    //Default: returns the percentage of battery available
    private double getBatteryStatistic(String args) throws Exception
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = contextToUse.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if(args.toLowerCase() == voltsArg)
        {
            return(level);
        }
        else
        {
            float batteryPct = level / (float)scale;
            return((double)batteryPct);
        }
        //TODO:IMPLEMENT BATTERY STATISTICS GETTER
    }

    //---------------------------------get cpu statistic-----------------------------------
    //gets cpu from the system
    //possible args:
    //              "user" - returns the time the cpu has spent on user processes
    //              "system" - returns the time the cpu has spent on system processes
    //              "idle" - returns the time the cpu has spent idle
    //
    //Default: returns the percentage of time idle
    private double getCpuStatistic(String args) throws Exception
    {
        /*
        ORDER OF COLLUMNS FROM PROC/STAT
        user: normal processes executing in user mode
        nice: niced processes executing in user mode
        system: processes executing in kernel mode
        idle: twiddling thumbs
        iowait: waiting for I/O to "complete
        irq: servicing interrupts
        softirq: servicing softirqs
         */
        //TODO:IMPLEMENT CPU STATISTICS
        RandomAccessFile reader;
        reader = new RandomAccessFile("/proc/stat", "r");
        String cpuInfo = reader.readLine();
        String[] parts = cpuInfo.split(" ");
        if(args.toLowerCase() == userArg)
        {
            return(Integer.decode(parts[2]));
        }
        else if(args.toLowerCase() == systemArg)
        {
            return(Integer.decode(parts[4]));
        }
        else if(args.toLowerCase() == idleArg)
        {
            return(Integer.decode(parts[5]));
        }
        else
        {
            int total = 0;
            int idle = Integer.decode(parts[5]);;
            for (int i = 2; i < parts.length; i++)
            {
                total += Integer.decode(parts[i]);
            }
            double retVal = (double)idle / (double) total;
            return (retVal);
        }
    }


    //---------------------------------get network out statistic-----------------------------------
    //gets the ammount of network out traffic
    //possible args:
    //              "packets" - returns the number of packets sent out
    //
    //Default: returns the number of bytes sent out
    private double getNetworkOutStatistics(String args) throws Exception
    {
        //TODO:IMPLEMENT NETWORK OUT STATISTICS GETTE
        String[] lineParts = getNetFile();
        if(args.toLowerCase() == packetArg)
        {
            int newNetPackets = Integer.decode(lineParts[10]);
            int retVal = newNetPackets - previousNetworkOutPackets;
            previousNetworkOutPackets = newNetPackets;
            return(retVal);
        }
        else
        {
            int newNetOut = Integer.decode(lineParts[9]);
            int retVal = newNetOut - previousNetworkout;
            previousNetworkout = newNetOut;
            return(retVal);
        }
    }

    //---------------------------------get network in statistic-----------------------------------
    //gets the ammount of network in traffic
    //possible args:
    //              "packets" - returns the number of packets received
    //
    //Default: returns the number of bytes received
    private double getNetworkInStatistics(String args) throws Exception
    {
        //TODO:IMLEMENT NETWORK IN STATISTICS GETTER
        String[] lineParts = getNetFile();
        if(args.toLowerCase() == packetArg)
        {
            int newNetPackets =  Integer.decode(lineParts[2]);
            int retVal = newNetPackets - previousNetworkInPackets;
            previousNetworkInPackets = newNetPackets;
            return(retVal);
        }
        else
        {
            int newNetIn = Integer.decode(lineParts[1]);
            int retVal = newNetIn - previousNetworkIn;
            previousNetworkIn = newNetIn;
            return(retVal);
        }
    }

    @TargetApi(16)
    /*
    -------------------------------------get ram statistics----------------------------
        gets ram usage from system
        possible args:
                "in use" - returns the amount of ram in use in kb
                 "free" - returns the amount of free ram in kb
        Default: returns the percentage of free ram avalible
     */
    private double getRamStatistics(String args) throws Exception
    {
        //TODO:IMPLEMENT RAM STATISTICS GETTER
        RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
        int numLine = 0;
        List<String> allLines = new ArrayList<String>();
        while(true) {
            String line = reader.readLine();
            if(line == null)
            {
                break;
            }
            else
            {
                allLines.add(line);
            }
        }
        int totalRam = getNumFromLine(allLines.get(0).split(" "));
        int ramFree = getNumFromLine(allLines.get(1).split((" ")));
        if(args.toLowerCase() == freeArg)
        {
            return(ramFree);
        }
        else if(args.toLowerCase() == inUseArg)
        {
            return(totalRam - ramFree);
        }
        else
        {
            double retVal = (double)ramFree / (double)totalRam;
            return(retVal);
        }
    }

    /*------------------Get thread statistics-----------------------
    *   Gets threads allocated from system
    *   possible args:
    *       "[pid]" - returns the threads created by the passed pid
    *   Default: returns the total number of threads created from all pids
    *      */
    private double getThreadStatistics(String args) throws Exception
    {
        //TODO: IMPLEMENT THIS IF WE HAVE TIME, CURRENTLY CANT GET ALL PIDS
        /*
        File f = new File("/proc/" + args + "/stat");
        if(f.exists() && !f.isDirectory()) { //if valid pid passed as arg
            RandomAccessFile reader = new RandomAccessFile("/proc/" + args + "/stat", "r");
            String newLine = reader.readLine();
            String[] parts = newLine.split(" ");
            return(Double.parseDouble(parts[20]));
        }
        else if(args.toLowerCase() == defaultArg)
        {
            int[] allPids = getAllPids(contextToUse);
            double retVal = 0;
            for(int i = 0; i < allPids.length; i++)
            {
                retVal += getThreadStatistics("" + allPids[i], contextToUse) + 1;
            }
            return(retVal);
        }
        else
        {
            return(-1);
        }
        */
        return(-1);
    }


    private double getSystemCallStatistics()
    {
        //TODO:IMPLEMENT SYSTEM CALL STATISTICS GETTER
        return(-1);
    }


    /*-----------------------START SECTION FOR HELPER FUNCTIONS -----------------------------------
    /*---------------------get num from line-----------------------------
        function to parse an string array and return the first numeric value found
        returns -1 if no number found
    */
    private int getNumFromLine(String[] arrayToUse)
    {
        for(int i = 0; i < arrayToUse.length; i++)
        {
            for(int k = 0; k < arrayToUse[i].length(); k++)
            {
                if(Character.isDigit(arrayToUse[i].charAt(k)))
                {
                    return(Integer.decode(arrayToUse[i]));
                }
            }
        }
        return (-1);
    }

    /*---------------------get all pids-------------------------------------
        function to return all currently running task pids
        returns by means of short array
     */
    public int[] getAllPids(Activity context) throws Exception {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> pids = activityManager.getRunningServices(Integer.MAX_VALUE);
        int processid = 0;
        int[] retVal = new int[pids.size()];
        Log.d("feature", "pid size: " + pids.size());
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningServiceInfo info = pids.get(i);
            retVal[i] = info.pid;
            Log.d("feature", "pid: " + info.service);

            return (retVal);
        }
        return(null);
    }

    /*-----------------get net file-----------------------------
        private function for getting the proc/net file
        returns the line for eth0 in array split by spaces
     */
    private String[] getNetFile() throws Exception
    {
        RandomAccessFile reader = new RandomAccessFile("/proc/net/dev", "r");
        int lineNum = 0;
        List<String> allLines = new ArrayList<String>();
        String line = reader.readLine();
        while(line != null)
        {
            allLines.add(line);
            line = reader.readLine();
        }
        String[] withExtra = allLines.get(4).split("\\s+"); //leaves empty string in spot 0 of area
        String[] retVal = new String[withExtra.length - 1];
        for(int i = 1; i < withExtra.length; i++)
        {
            retVal[i - 1] = withExtra[i];
        }
        return(retVal);
        //return(retArray);
    }

}