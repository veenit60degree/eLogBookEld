package com.adapter.logistic;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DownloadPdf;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.WebViewActvity;
import com.models.DownloadLogsModel;
import com.rajat.pdfviewer.PdfViewerActivity;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadLogsAdapter extends BaseAdapter {

    private Context mContext;
    Constants constants;
    private final List<DownloadLogsModel> downloadLogsModels;
    LayoutInflater mInflater;
    Globally globally;
    DownloadPdf downloadDocService = new DownloadPdf();
    ArrayList<String> docFilesList;
    List downloadLogslocalDocList = new ArrayList<>();
    Globally global;
    int docPos;
    Map<String, String> params;
    VolleyRequest GetShareLinkLogRequest;
    ViewHolder holder;


    public DownloadLogsAdapter(Context c, Constants cons, List<DownloadLogsModel> list) {
        mContext = c;
        constants = cons;
        downloadLogsModels = list;
        global                  = new Globally();
        mInflater = LayoutInflater.from(mContext);
        getLocalDocuments();
        holder = null;
    }

    @Override
    public int getCount() {
        return downloadLogsModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return downloadLogsModels.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_rods_listing, null);

            globally = new Globally();
            GetShareLinkLogRequest  = new VolleyRequest(mContext);
            holder.createdDate               = (TextView) convertView.findViewById(R.id.rodsFromDateTxtView);
            holder.rodsFileViewLay           = (RelativeLayout) convertView.findViewById(R.id.rodsFileViewLay);
            holder.shareLogImgView           = (RelativeLayout) convertView.findViewById(R.id.shareLogImgView);
            holder.downloadLogImgView        = (RelativeLayout) convertView.findViewById(R.id.downloadLogImgView);
            holder.shareProgressBar          = (ProgressBar) convertView.findViewById(R.id.shareProgressBar);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.rodsFileViewLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkFilePostion = false;
                getLocalDocuments();
                 docPos = getFilePositionFromLocalDir(position);

                // position comparision is for local files. because files are fetching from storage and they are getting in the list in different order as compared to json file (from API).
                // So to match file name we first compare files title first and get that file position.
                if(docPos == -1){
                    checkFilePostion = false;
                    checkFileBeforeView(position,checkFilePostion);
                }else{
                    checkFilePostion  = true;
                    checkFileBeforeView(docPos,checkFilePostion);
                }


            }
        });





        holder.shareLogImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.shareProgressBar.setVisibility(View.VISIBLE);
                GetSharePdfLog(downloadLogsModels.get(position).getEldInspectionLogId(),downloadLogsModels.get(position).getCountry(),downloadLogsModels.get(position).getShareId());
            }
        });

        holder.downloadLogImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDocLocally(position);
            }
        });



        DateTime fromDate = Globally.getDateTimeObj(downloadLogsModels.get(position).getFromDate(), false);
        DateTime toDate = Globally.getDateTimeObj(downloadLogsModels.get(position).getToDate(), false);
        int daysDiff = Days.daysBetween(fromDate.toLocalDate(), toDate.toLocalDate()).getDays();
        int difference = daysDiff+1;

        String createdDate = globally.ConvertDateFormatddMMMyyyy(downloadLogsModels.get(position).getToDate(), Globally.DateFormat_dd_MMM_yyyy);
        String toDateStr = " <font color='#287CD1'> (" + difference +" days) </font> ";
        holder.createdDate.setText(Html.fromHtml("<b>" +createdDate + "</b>" + toDateStr), TextView.BufferType.SPANNABLE);

        return convertView;
    }

    void checkFileBeforeView(int pos,boolean checkFileExist){
        if(checkFileExist == true){
        if(downloadLogslocalDocList.size() > pos) {
            docFilesList = global.getGeneratedLogDocs(mContext);
            String filepath = global.getAlsGenerateRodsPath(mContext).toString() + "/";
            File docFile = new File(filepath + docFilesList.get(pos));
            File file = new File(String.valueOf(docFile));
            mContext.startActivity(
                    PdfViewerActivity.Companion.launchPdfFromPath(
                            mContext, String.valueOf(file),
                            "Title", "dir", true, false
                    )
            );
        }
        }else{
            if(Globally.isConnected(mContext)) {
                Intent i = new Intent(mContext, new WebViewActvity().getClass());
                i.putExtra("Path", downloadLogsModels.get(pos).getPdfFilePath());
                mContext.startActivity(i);
            }else{
                Toast.makeText(mContext, global.CHECK_INTERNET_MSG, Toast.LENGTH_LONG).show();
            }

        }
    }


    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long percentage     = intent.getIntExtra("percentage", 0);
            int position        = intent.getIntExtra("position", 0);
            boolean isCompleted = intent.getBooleanExtra("isCompleted", false);

        }
    };


    public class ViewHolder {
        TextView createdDate, pdfFilePath;
        RelativeLayout rodsFileViewLay;
        RelativeLayout shareLogImgView,downloadLogImgView;
        ProgressBar shareProgressBar;
    }

    void saveDocLocally(int pos){
        boolean hasFile = false;
        String fileName = String.valueOf(downloadLogsModels.get(pos).getFileNameUniqueNumber());

        for(int i = 0 ; i < downloadLogslocalDocList.size() ; i++){
            String[] fileNameArray = downloadLogslocalDocList.get(i).toString().split(",");

            String fileNameUnique = fileNameArray[0].toString().replace("[", "").trim();
            if(fileNameUnique.equals(fileName)){
                hasFile = true;
                break;
            }
        }

        if(!hasFile) {
            downloadFiles(downloadLogsModels.get(pos).getPdfFilePath(),downloadLogsModels.get(pos).getFileNameUniqueNumber(),downloadLogsModels.get(pos).getCountry());
        }

    }

    private int  getFilePositionFromLocalDir(int pos){
        int filePosition = -1;

        if(downloadLogsModels.size() > pos) {
            String fileName = String.valueOf(downloadLogsModels.get(pos).getFileNameUniqueNumber());

            for (int i = 0; i < downloadLogslocalDocList.size(); i++) {
                String[] fileNameArray = downloadLogslocalDocList.get(i).toString().split("_");

                String fileNameUnique = fileNameArray[0].replace("[","").trim();
                if(fileNameUnique.equals(fileName)){
                    filePosition = i;
                    break;
                }
            }
        }

        return filePosition;
    }




    public void downloadFiles(final String url, final String fileName,String country) {
        try {
            if (url != null && !url.isEmpty()) {
                Uri uri = Uri.parse(url);
                Intent serviceIntent = new Intent(mContext,DownloadLogsAdapter.class);
                serviceIntent.putExtra("url", url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setMimeType(".pdf");
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                File pathOriginal = new File(Globally.getAlsGenerateRodsDummyPath(mContext).toString());
                File[] filesList = pathOriginal.listFiles();

                if(filesList.length > 0) {

                    for (int i = 0; i < filesList.length; i++) {
                        String[] dummyArray = filesList[i].getName().split("GenerateRodsDummy/");
                        String[] afterSplitArray = dummyArray[0].split("_");
                        if (afterSplitArray[0].equals(fileName)) {
                            Toast.makeText(mContext, "Already downloaded", Toast.LENGTH_LONG).show();
                        } else {
                            request.setDestinationInExternalFilesDir(mContext, "/Logistic/GenerateRodsDummy", fileName + "_" + country + ".pdf");
                            Globally.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                            Globally.downloadManager.enqueue(request);
                            Toast.makeText(mContext, "Downloading started", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    request.setDestinationInExternalFilesDir(mContext, "/Logistic/GenerateRodsDummy", fileName + "_" + country + ".pdf");
                    Globally.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    Globally.downloadManager.enqueue(request);
                    Toast.makeText(mContext, "Downloading started", Toast.LENGTH_LONG).show();
                }



            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
//            Toast.makeText(activity, "Please insert an SD card to download file", Toast.LENGTH_SHORT).show();
        }
    }

    void getLocalDocuments(){
        docFilesList = global.getGeneratedLogDocs(mContext);
        List list = new ArrayList();


        for(int i = 0 ; i< docFilesList.size() ; i++){
            if(docFilesList.get(i).equals(".nomedia")){
                break;
            }else{
                String[] fileNameArray = docFilesList.get(i).split("_");
                if(fileNameArray.length > 0){
                    String fileName = fileNameArray[0];
                    String country = fileNameArray[1];
                    String[] countryArray = country.split(".pdf");
                    boolean isExist = false;

                    for (int j = 0 ; j < downloadLogsModels.size() ; j++){
                        if(!countryArray[0].equals(downloadLogsModels.get(j).getCountry())){
                            isExist = true;
                            break;
                        }else
                        if(fileName.equals(downloadLogsModels.get(j).getFileNameUniqueNumber())){
                            isExist = true;
                            break;
                        }
                    }

                    if(isExist == false){

                        // write delete code for this file (fileName)

                        String path = mContext.getExternalFilesDir(null).toString()+"/GenerateRods";
                        File file = new File(path, fileName+".pdf");
                        file.delete();

                    }
                }
            }
        }

        docFilesList = global.getGeneratedLogDocs(mContext);

        for(int i = 0 ; i < docFilesList.size() ; i++) {
            downloadLogslocalDocList = new ArrayList<>();
            if(docFilesList.get(i).equals(".nomedia")){
                downloadLogslocalDocList.add(0);
                break;
            }else {
                String[] fileNameArray = docFilesList.get(i).split(".pdf");
                String fileNameUniqueNumber = "";
                if (fileNameArray.length > 0) {


                    fileNameUniqueNumber = fileNameArray[0];
                    list.add(fileNameUniqueNumber);

                    downloadLogslocalDocList = list;
                } else {
                    downloadLogslocalDocList.add(0);
                }

            }

        }
    }

    void GetSharePdfLog(String eldInspectionLogId,String country,String shareId) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.EldInspectionLogId,eldInspectionLogId);
        params.put(ConstantsKeys.Country, country);
        params.put(ConstantsKeys.ShareId, shareId);

        GetShareLinkLogRequest.executeRequest(Request.Method.POST, APIs.GenerateLink, params, 1,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

//            holder.shareProgressBar.setVisibility(View.GONE);

            try {
                JSONObject dataObj = new JSONObject(response);

                String  status = dataObj.getString("Status");

                if (status.equalsIgnoreCase("true")) {

                    JSONObject getData = new JSONObject(dataObj.getString("Data"));
                    String shareLink   = getData.getString("Link");



                    try {
                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                      //  String action = shareIntent.getAction();
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Copy Link");
                        String shareMessage= "Generated RODS Link \n\n";
                        shareMessage = shareMessage + shareLink +"\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, shareLink);
                        Intent clipboardIntent = new Intent();
                        clipboardIntent.setData(Uri.parse(shareLink));
                        Constants.copyTextToClipboard(mContext, shareLink);

                        Intent chooserIntent = Intent.createChooser(shareIntent, "Share with\n\n"+ shareLink);
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { clipboardIntent });
                        mContext.startActivity(chooserIntent);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("error", ">>error: " + error);

        }
    };



}
