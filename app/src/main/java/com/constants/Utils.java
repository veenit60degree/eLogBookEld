package com.constants;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{

    Context context;
    public String LogFilePath            = "";
    public String AppUsageLogFilePath    = "";
    public String AppExectnTimeLogPath   = "";


    public Utils(Context context){
       // super();
        this.context = context;

        LogFilePath            = getAlsLogFilePath(context, ConstantsKeys.ALS_OBD_LOG).toString();
        AppUsageLogFilePath    = getAlsLogFilePath(context, ConstantsKeys.APP_USAGE_LOG).toString();
        AppExectnTimeLogPath   = getAlsLogFilePath(context, ConstantsKeys.EXECUTION_TIME_LOG).toString();

    }





    public String createLogFile()
    {
        File file = new File("");
        try {
            file = getAlsLogFilePath(context, ConstantsKeys.ALS_OBD_LOG); //new File("sdcard/obd_log.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file.getPath();
    }


    public String createAppUsageLogFile()
    {
        File file = new File("");
        try {
            file = getAlsLogFilePath(context, ConstantsKeys.APP_USAGE_LOG);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return file.getPath();
    }


    public String createExecTimeLogFile()
    {
        File file = new File("");
        try {
            file = getAlsLogFilePath(context, ConstantsKeys.EXECUTION_TIME_LOG);
            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file.getPath();
    }


    public void writeToLogFile(String value)
    {
        if(LogFilePath.length() == 0){
            LogFilePath            = getAlsLogFilePath(context, ConstantsKeys.ALS_OBD_LOG).toString();
        }

        File LogFile = new File(LogFilePath);
        if (!LogFile.exists())
        { //Create if it isn't exist
            try
            {
                LogFile.createNewFile();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(LogFile, true);
           // String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            value = "\n\n"  + value;    //" + date + "
            fos.write(value.getBytes());
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    public void writeAppUsageLogFile(String value)
    {
        if(AppUsageLogFilePath.length() == 0) {
            AppUsageLogFilePath = getAlsLogFilePath(context, ConstantsKeys.APP_USAGE_LOG).toString();
        }

        File LogFile = new File(AppUsageLogFilePath);
        if (!LogFile.exists())
        { //Create if it isn't exist
            try
            {
                LogFile.createNewFile();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(LogFile, true);
            // String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            value = "\n\n"  + value;    //" + date + "
            fos.write(value.getBytes());
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeExectnTimeLogFile(long ExecutionTime, String usedMemory, String type)
    {
        if(AppExectnTimeLogPath.length() == 0) {
            AppExectnTimeLogPath = getAlsLogFilePath(context, ConstantsKeys.EXECUTION_TIME_LOG).toString();
        }

        File LogFile = new File(AppExectnTimeLogPath);
        if (!LogFile.exists())
        { //Create if it isn't exist
            try
            {
                LogFile.createNewFile();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        JSONObject obj = new JSONObject();
        try{

            obj.put(ConstantsKeys.Type, type);
            obj.put(ConstantsKeys.MemoryUsage, usedMemory);
            obj.put(ConstantsKeys.ExecutionTime, ExecutionTime + " ms");
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            obj.put(ConstantsKeys.Date_Time, dateTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(LogFile, true);
            String value = "\n\n"  + obj.toString();
            fos.write(value.getBytes());
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }



    public File getAlsLogFilePath(Context context, String fileName){

        File filePath = new File("");
        try {
            File apkStorageDir = new File(context.getExternalFilesDir(null), "Logistic/AlsLog");

            // Create the storage directory if it does not exist
            if (!apkStorageDir.exists()) {
                if (!apkStorageDir.mkdirs()) {
                    Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
                    return null;
                }
            }
            filePath = new File(apkStorageDir.toString() + "/" + fileName + ".txt");

        }catch (Exception e){
            e.printStackTrace();
        }
        return filePath;
    }


    public StringBuilder getObdLogData(Context context){

        File wiredObdLog = Globally.GetWiredLogFile(context, ConstantsKeys.ALS_OBD_LOG, "txt");
        StringBuilder text = new StringBuilder();

        if(wiredObdLog != null && wiredObdLog.isFile() ) {
            //Read text from file

            try {
                BufferedReader br = new BufferedReader(new FileReader(wiredObdLog));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
             Log.d("wiredObdLog", "wiredObdLog: " + text);
        }

        return text;

    }



   public String getFileContent(String targetFilePath){
            File file = new File(targetFilePath);
       FileInputStream fileInputStream = null;
       String fileContent = "";

            try {
                fileInputStream = new FileInputStream(file);
                StringBuilder sb = null;
                while(fileInputStream.available() > 0) {

                    if(null== sb)  sb = new StringBuilder();

                    sb.append((char)fileInputStream.read());
                }

                if(null!=sb){
                    fileContent= sb.toString();
                    // This is your fileContent in String.


                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

       try {
            fileInputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return fileContent;
    }





    public void syncObdLogData(Context context, String DriverId, String DriverName){

        File wiredObdLog = Globally.GetWiredLogFile(context, ConstantsKeys.ALS_OBD_LOG, "txt");
        int fileSize= 0;

        if(wiredObdLog != null && wiredObdLog.isFile() ) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(wiredObdLog));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
           // Log.d("wiredObdLog", "wiredObdLog: " + text);
            fileSize = text.toString().split("\n\n").length;

           // Log.d("log size", "log size: " + fileSize);

            if(fileSize > 20 && Globally.isConnected(context)){
                SyncWiredObdLog syncDataUpload = new SyncWiredObdLog(context, DriverId, DriverName, wiredObdLog );
                syncDataUpload.execute();

            }
        }
    }


    public void syncObdSingleLog(Context context, String DriverId, String DriverName, int count){

        File wiredObdLog = Globally.GetWiredLogFile(context, ConstantsKeys.ALS_OBD_LOG, "txt");
        int fileSize= 0;

        if(wiredObdLog != null && wiredObdLog.isFile() ) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(wiredObdLog));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Log.d("wiredObdLog", "wiredObdLog: " + text);
            fileSize = text.toString().split("\n\n").length;

            // Log.d("log size", "log size: " + fileSize);

            if(fileSize > count && Globally.isConnected(context)){
                SyncWiredObdLog syncDataUpload = new SyncWiredObdLog(context, DriverId, DriverName, wiredObdLog );
                syncDataUpload.execute();

            }
        }
    }




    public void syncAppUsageLog(Context context, String DriverId){

        File appUsageLog = Globally.GetWiredLogFile(context, ConstantsKeys.APP_USAGE_LOG, "txt");
        int fileSize= 0;

        if(appUsageLog != null && appUsageLog.isFile() ) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(appUsageLog));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
           //  Log.d(ConstantsKeys.APP_USAGE_LOG, ConstantsKeys.APP_USAGE_LOG + ": " + text);
            fileSize = text.toString().split("\n\n").length;

            // Log.d("log size", "log size: " + fileSize);

            if(fileSize >= 10 ){
                if(Globally.isConnected(context)) {
                    SyncAppUsageLog syncAppUsageLog = new SyncAppUsageLog(context, DriverId, appUsageLog);
                    syncAppUsageLog.execute();
                }
            }
        }
    }



    public int executionLogCount(){

        File appUsageLog = Globally.GetWiredLogFile(context, ConstantsKeys.EXECUTION_TIME_LOG, "txt");
        int fileSize = 0;

        if(appUsageLog != null && appUsageLog.isFile() ) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(appUsageLog));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  Log.d(ConstantsKeys.APP_USAGE_LOG, ConstantsKeys.APP_USAGE_LOG + ": " + text);
            fileSize = text.toString().split("\n\n").length;

            if(fileSize > 50000){
                deleteExecLog();
            }
        }

        return fileSize;
    }

    public void deleteServerObdLog() {
        try{
            File wiredObdLog = Globally.GetWiredLogFile(context, ConstantsKeys.SERVER_OBD_LOG, "txt");
            if (wiredObdLog != null && wiredObdLog.isFile()) {
                wiredObdLog.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void deleteWiredObdLog() {
        try{
            File wiredObdLog = Globally.GetWiredLogFile(context, ConstantsKeys.ALS_OBD_LOG, "txt");
            if (wiredObdLog != null && wiredObdLog.isFile()) {
                wiredObdLog.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteAppUsageLog(){
        try {
            File appUsageLogFile = Globally.GetWiredLogFile(context, ConstantsKeys.APP_USAGE_LOG, "txt");
            if (appUsageLogFile != null && appUsageLogFile.isFile()) {
                appUsageLogFile.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteExecLog(){
        try {
            File appUsageLogFile = Globally.GetWiredLogFile(context, ConstantsKeys.EXECUTION_TIME_LOG, "txt");
            if (appUsageLogFile != null && appUsageLogFile.isFile()) {
                appUsageLogFile.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
