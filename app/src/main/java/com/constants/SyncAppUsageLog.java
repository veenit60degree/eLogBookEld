package com.constants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.local.db.ConstantsKeys;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SyncAppUsageLog extends AsyncTask<String, String, String> {

    //AsyncResponse postResponse;
    Context context;
    String strResponse = "", DriverId;
    Response response;
    int timeOut = 20;   // in seconds
    File appUsageFile;

    public SyncAppUsageLog(Context cxt, String driverId, File syncedFile) {   //}, AsyncResponse response){
        context         = cxt;
        DriverId        = driverId;
        appUsageFile     = syncedFile;
    }




    @Override
    protected String doInBackground(String... strings) {
        try {

            Request request = null;

            /* ===================  CROSS CHECK ONCE FOR LOAD_ID AND JOB_ID ==================*/

            MultipartBuilder builderNew = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart(ConstantsKeys.DriverId, DriverId );

            if (appUsageFile != null && appUsageFile.exists()) {
                builderNew.addFormDataPart("File", "file",
                        RequestBody.create(MediaType.parse("application/txt"), new File(appUsageFile.toString())));
            }


            RequestBody requestBody = builderNew.build();
            request = new Request.Builder()
                    .url(APIs.SAVE_APP_USAGE_LOG_FILE)
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

        Log.e("String Response", ">>>Sync app usage Response:  " + result);

        //  postResponse.onAsyncResponse(result);

        try {
            if(result.length() > 0) {
                JSONObject obj = new JSONObject(result);
                String status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {

                    /* ------------ Delete posted files from local after successfully posted to server --------------- */
                    if (appUsageFile != null && appUsageFile.exists())
                        appUsageFile.delete();

                }
            }else{
                /* ------------ Delete posted files from local after successfully posted to server --------------- */
                if (appUsageFile != null && appUsageFile.exists())
                    appUsageFile.delete();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}