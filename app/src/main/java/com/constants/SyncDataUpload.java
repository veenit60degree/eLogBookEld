package com.constants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SyncDataUpload extends AsyncTask<String, String, String>{

    AsyncResponse postResponse;
    Context context;
    String strResponse = "", DriverId;
    Response response;
    int timeOut = 50;   // in seconds
    File syncingFile, violatedLogFile, cycleRecordFile;
    boolean IsLogPermission;

    public SyncDataUpload(Context cxt, String driverId, File syncedFile, File logFile, File cycleChangesRecordFile, boolean isLogPermission, AsyncResponse response){
        context         = cxt;
        DriverId        = driverId;
        syncingFile     = syncedFile;
        violatedLogFile = logFile;
        cycleRecordFile = cycleChangesRecordFile;
        IsLogPermission = isLogPermission;
        postResponse    = response;
    }




    @Override
    protected String doInBackground(String... strings) {
        try {

            Request request = null;

            /* ===================  CROSS CHECK ONCE FOR LOAD_ID AND JOB_ID ==================*/

            MultipartBuilder builderNew = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart(ConstantsKeys.DriverId, DriverId ) ;

            if (syncingFile != null && syncingFile.exists()) {
                builderNew.addFormDataPart("File", "file",
                        RequestBody.create(MediaType.parse("application/txt"), new File(syncingFile.toString())));
            }

            if(IsLogPermission) {
                if (violatedLogFile != null && violatedLogFile.exists()) {
                    builderNew.addFormDataPart("File22", ConstantsKeys.ViolationTest,
                            RequestBody.create(MediaType.parse("application/txt"), new File(violatedLogFile.toString())));
                }


                if(cycleRecordFile != null && cycleRecordFile.isFile() ) {
                    builderNew.addFormDataPart("File3", ConstantsKeys.ELDCycleFile,
                            RequestBody.create(MediaType.parse("application/txt"), new File(cycleRecordFile.toString())));
                }
            }




            RequestBody requestBody = builderNew.build();
            request = new Request.Builder()
                    .url(APIs.SAVE_LOG_TEXT_FILE)
                    .post(requestBody)
                    .build();


            OkHttpClient client = new OkHttpClient();
            client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
                client.setConnectTimeout(timeOut, TimeUnit.SECONDS); // connect timeout
            client.setReadTimeout(timeOut, TimeUnit.SECONDS);
            response = client.newCall(request).execute();
            strResponse = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.e("String Response", ">>>Sync Data Response:  " + result);

         postResponse.onAsyncResponse(result, DriverId);

    }


}

/*

       public class SyncUserData extends AsyncTask<String, String, String> {

            String strResponse = "";
            Response response;



            @Override
            protected String doInBackground(String... params) {

                try {

                    Request request = null;

                    */
/* ===================  CROSS CHECK ONCE FOR LOAD_ID AND JOB_ID ================== *//*

                    MultipartBuilder builderNew = new MultipartBuilder().type(MultipartBuilder.FORM)
                            .addFormDataPart(ConstantsKeys.DriverId, Constants.DriverID ) ;

                    if (Constants.SyncingFile != null && Constants.SyncingFile.exists()) {
                        builderNew.addFormDataPart("File", "file",
                                RequestBody.create(MediaType.parse("application/txt"), new File(Constants.SyncingFile.toString())));
                    }

                    if(Constants.IsLogPermission) {
                        if (Constants.DriverLogFile != null && Constants.DriverLogFile.exists()) {
                            builderNew.addFormDataPart("File22", "ViolationTest",
                                    RequestBody.create(MediaType.parse("application/txt"), new File(Constants.DriverLogFile.toString())));
                        }
                    }



                    RequestBody requestBody = builderNew.build();
                    request = new Request.Builder()
                            .url(APIs.SAVE_LOG_TEXT_FILE)
                            .post(requestBody)
                            .build();


                    OkHttpClient client = new OkHttpClient();
                    client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
                    client.setReadTimeout(30, TimeUnit.SECONDS);
                    response = client.newCall(request).execute();
                    strResponse = response.body().string();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return strResponse;
            }



            @Override
            protected void onPostExecute(String result) {

                Log.e("String Response", ">>>Sync Response:  " + result);

               // postResponse.onAsyncResponse(result);

            }




        }

*/








