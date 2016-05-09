package com.something.yellowplanet;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.util.Calendar;

import javax.crypto.Cipher;

import java.net.Socket;

/**
 * Created by new user on 4/27/2016.
 */

//TODO:figure out secret key stuff

final public class anomolyCrypto
{
    //encripts json file along with timeStamp and value
    //adrm = anomoly detection report message
    final static String outputFile = "seralized.adrm";
    final static String PUB_KEY_FILE = "pubKey.pem";
    final static String secretKey = "secret.pem";

    final static String IP = "127.0.0.1";
    final static int PORT = 3010;

    static public byte[] encriptData(File jsonFile, String cypherToSend) {
        try
        {
            boolean keyGood  = checkKeys();
            if(keyGood == false) //key not found and server can't be reached
            {
                return null;
            }
            Cipher newCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding(1024)");
            newCipher.init(Cipher.ENCRYPT_MODE, getPubKeyFromFile(PUB_KEY_FILE));
            //byte[] bytesToEncript = getPackagedMessage(jsonFile);
            byte[] bytesToEncript = cypherToSend.getBytes();
            byte[] encriptedBytes = newCipher.doFinal(bytesToEncript);
            return encriptedBytes;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return (null);
        }
    }

    private static PublicKey getPubKeyFromFile(String filename)
            throws Exception {

        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int)f.length()];
        dis.readFully(keyBytes);
        dis.close();
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    //makes sure the files containg the public RSA key exists, creates them if they dont
    //currently only checks RSA key
    static private boolean checkKeys()
    {
        try {
            Socket s = new Socket(InetAddress.getByName("192.168.1.7"),3000);
            //s.connect(new InetSocketAddress(InetAddress.getByName("google.com"), 80));
            Log.d("inet", "starting connection...");
            if(s.isConnected() == true)
            {
                Log.d("inet","CONNECTED");
            }
            else
            {
                Log.d("inet","NOT CONNECTED");
            }
            PrintWriter out = new PrintWriter(s.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    s.getInputStream()));
            out.print("pubkey");
            InputStream inStream = s.getInputStream();
            byte[] keyBytes = new byte[1024];
            int count = inStream.read(keyBytes);
            FileOutputStream fileOut = new FileOutputStream(PUB_KEY_FILE);
            fileOut.write(keyBytes);
            fileOut.close();
            return(true);
        }
        catch(Exception e)
        {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            return(false);
        }
    }

    //Packages the json file into byte array via seralization
    static private byte[] getPackagedMessage(File jsonFile)
    {
        try
        {
            readingReportPackage reportMsg = new readingReportPackage(getCurrentTime(),getBytesFromFile(jsonFile));
            FileOutputStream fileOut = new FileOutputStream(outputFile);
            ObjectOutputStream serStream = new ObjectOutputStream(fileOut);
            serStream.writeObject(reportMsg);
            serStream.close();
            fileOut.close();
            File serFile = new File(outputFile);
            byte[] retBytes = getBytesFromFile(serFile);
            serFile.delete(); //deletes file so that non encripted data isn't stored on device
            return(retBytes);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //gets a byte array of a file on the disk
    static private byte[] getBytesFromFile(File fileToRead) throws IOException {
        int size = (int) fileToRead.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(fileToRead);;
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    //gets a timestamp to send with report
    static private String getCurrentTime()
    {
        Calendar cal = Calendar.getInstance();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        return(df.format((cal.getTime())));
    }

}


//class to hold data so that seralization may happen, class is deseralized on server
class readingReportPackage implements java.io.Serializable
{
    String timeSent;
    byte[] jsonFileBytes;
    public readingReportPackage(String newTime, byte[] newFile)
    {
        timeSent = newTime;
        jsonFileBytes = newFile;
    }
}
