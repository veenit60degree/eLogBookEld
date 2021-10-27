package com.constants;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.messaging.logistic.Globally;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadPdf  extends Service {

    private String TAG = DownloadAppService.class.getSimpleName();
    private String version = "", title = ""; //number;
    int position = 0;
   // boolean isDownloading ;
    DownloadingTask downloadTaskAsync = new DownloadingTask();
    Constants constants;

    @Override
    public void onCreate() {
        constants = new Constants();
        // downloadTaskAsync = new DownloadingTask();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = " ";

        if (intent != null) {
            url      = intent.getStringExtra("url");
            version  = intent.getStringExtra("Version");
            title    = intent.getStringExtra("title");
            position = intent.getIntExtra("position", 0);
        }

        String[] params = new String[]{url};

       // if(!isDownloading){
          //  SharedPref.setAsyncCancelStatus(false, getApplicationContext());
            downloadTaskAsync = new DownloadingTask();
            downloadTaskAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
      //  }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        if(downloadTaskAsync != null) {
            downloadTaskAsync.cancel(true);
            downloadTaskAsync = null;
        }
       // SharedPref.setAsyncCancelStatus(true, getApplicationContext());

        Log.d("AsyncTask", "onDestroy");
        // Toast.makeText(this, "Download Completed.", Toast.LENGTH_SHORT).show();
    }



    class DownloadingTask extends AsyncTask<String, String, String> {
        private String fileName;
        private String folder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try{
                File outputDir = getApplicationContext().getExternalCacheDir(); // context being the Activity pointer
                folder = String.valueOf(outputDir);

               // folder      = Globally.getAlsDocPath(getApplicationContext()).toString();
                fileName    = "/" + version + "_" + title + ".pdf"; //number +"_" +
                File doc = new File(folder + fileName);
                if(doc != null && doc.isFile()) {
                    constants.DeleteFile(doc.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... urls) {

            Log.d(TAG, "doInBackground: " + urls[0]);

            int count;
            try {

                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(300000);   // 5 min interval for connection timeout due to few heavy files
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);



                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Pos-" + position +" !! Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }


            return "Downloading failed.";

        }


        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);

            try {
                int Progress = Integer.parseInt(progress[0]);
                Intent intent = new Intent("download_pdf_progress");
                intent.putExtra("percentage", Progress);
                intent.putExtra("position", position);
                intent.putExtra("isCompleted", false);
                LocalBroadcastManager.getInstance(DownloadPdf.this).sendBroadcast(intent);
            }catch (Exception e){}
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute");
            stopSelf();

            checkFileExist(result);

            // Display File path after downloading
            Intent intent = new Intent("download_pdf_progress");
            intent.putExtra("percentage", 100);
            intent.putExtra("position", position);
            intent.putExtra("isCompleted", true);
            LocalBroadcastManager.getInstance(DownloadPdf.this).sendBroadcast(intent);




        }
    }


    void checkFileExist(String result){
        if(result.contains("Downloading failed")){
            String path = result.split("__")[0];
            String from = getApplicationContext().getExternalCacheDir().toString();
            File directory = new File(from);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getAbsolutePath().equals(path)) {
                    files[i].delete();
                }
            }
        }else{
            String from = getApplicationContext().getExternalCacheDir().toString();
            File directory = new File(from);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {

                if (files[i].getAbsolutePath().equals(result)) {
                    File pathOriginal = new File(Globally.getAlsDocPath(getApplicationContext()).getAbsolutePath());
                    Log.d("Files", "FileName:" + pathOriginal + "/" + files[i].getName());
                    files[i].renameTo(new File(pathOriginal + "/" + files[i].getName()));
                }

            }
        }
    }


}


