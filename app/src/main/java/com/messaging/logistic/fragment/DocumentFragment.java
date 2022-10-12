package com.messaging.logistic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.logistic.HelpDocAdapter;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DownloadPdf;
import com.constants.Logger;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.HelpDocModel;
import com.rajat.pdfviewer.PdfViewerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentFragment extends Fragment implements View.OnClickListener{

    View rootView;
    Globally global;
    ListView notiHistoryListView;
    RecyclerView recyclerView;
    ImageView eldMenuBtn;
    RelativeLayout eldMenuLay, shippingMainLay;
    RelativeLayout rightMenuBtn;
    TextView EldTitleTV, noDataEldTV;

    final  int getDocDetails = 1;
    VolleyRequest getAppDocDetailsRequest;
    HelpDocAdapter helpDocAdapter;
    List<HelpDocModel> helpDocList = new ArrayList<>();
    List<HelpDocModel> localDocList = new ArrayList<>();
    ArrayList<String> docFilesList;
    DownloadPdf downloadDocService = new DownloadPdf();

    File docFile1;
    File docFile2;
    File docFile3;
    File docFile4;
    File docFile5;
    File docFile6;
    File docFile7;
    File docFile8;
    File docFile9;
    File docFile10;

    boolean isDownloading1 = false;
    boolean isDownloading2 = false;
    boolean isDownloading3 = false;
    boolean isDownloading4 = false;
    boolean isDownloading5 = false;
    boolean isDownloading6 = false;
    boolean isDownloading7 = false;
    boolean isDownloading8 = false;
    boolean isDownloading9 = false;
    boolean isDownloading10 = false;

    long progressPercentage1 = 0;
    long progressPercentage2 = 0;
    long progressPercentage3 = 0;
    long progressPercentage4 = 0;
    long progressPercentage5 = 0;
    long progressPercentage6 = 0;
    long progressPercentage7 = 0;
    long progressPercentage8 = 0;
    long progressPercentage9 = 0;
    long progressPercentage10 = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }

        try {
            rootView = inflater.inflate(R.layout.noti_history_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View v) {
        global                  = new Globally();
        getAppDocDetailsRequest = new VolleyRequest(getActivity());

        notiHistoryListView = (ListView)v.findViewById(R.id.notiHistoryListView);
        recyclerView        = (RecyclerView)v.findViewById(R.id.notiHistoryRecyclerView);

        EldTitleTV          = (TextView)v.findViewById(R.id.EldTitleTV);
        noDataEldTV         = (TextView)v.findViewById(R.id.noDataEldTV);

        rightMenuBtn        = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        eldMenuLay          = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        shippingMainLay     = (RelativeLayout)v.findViewById(R.id.shippingMainLay);
        eldMenuBtn          = (ImageView)v.findViewById(R.id.eldMenuBtn);

        eldMenuBtn.setImageResource(R.drawable.back_btn);
        EldTitleTV.setText(getResources().getString(R.string.eld_documents));
        notiHistoryListView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        rightMenuBtn.setVisibility(View.GONE);
        notiHistoryListView.setDividerHeight(2);

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            shippingMainLay.setBackgroundColor(getContext().getResources().getColor(R.color.gray_background));
        }


        notiHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ---------- PDF offline commented code -----------

                int docPos = getFilePositionFromLocalDir(position);

                // position comparision is for local files. because files are fetching from storage and they are getting in the list in different order as compared to json file (from API).
                // So to match file name we first compare files title first and get that file position.
                if(docPos == -1){
                    checkFileBeforeView(position);
                }else {
                    checkFileBeforeView(docPos);
                }

                //displayPdfFromUrl(position);

            }
        });


        eldMenuLay.setOnClickListener(this);


    }


    // get file path from storage..
    void getLocalDocuments(boolean isLoad){

        localDocList = new ArrayList<>();
        docFilesList = global.getDownloadedDocs(getActivity());
        for(int i = 0 ; i < docFilesList.size() ; i++) {
            String filepath = global.getAlsDocPath(getActivity()).toString() + "/";
            switch (i) {
                case 0:
                    docFile1 = new File(filepath + docFilesList.get(i));
                    break;

                case 1:
                    docFile2 = new File(filepath + docFilesList.get(i));
                    break;

                case 2:
                    docFile3 = new File(filepath + docFilesList.get(i));
                    break;

                case 3:
                    docFile4 = new File(filepath + docFilesList.get(i));
                    break;

                case 4:
                    docFile5 = new File(filepath + docFilesList.get(i));
                    break;


                case 5:
                    docFile6 = new File(filepath + docFilesList.get(i));
                    break;


                case 6:
                    docFile7 = new File(filepath + docFilesList.get(i));
                    break;


                case 7:
                    docFile8 = new File(filepath + docFilesList.get(i));
                    break;


                case 8:
                    docFile9 = new File(filepath + docFilesList.get(i));
                    break;


                case 9:
                    docFile10 = new File(filepath + docFilesList.get(i));
                    break;

            }

            String[] fileNameArray = docFilesList.get(i).split("_");
            String fileTitle = "", version = "";
            if (fileNameArray.length > 1) {
                version = fileNameArray[0];
                fileTitle = fileNameArray[1].replaceAll(".pdf", "");
            }

            HelpDocModel docModel = new HelpDocModel(
                    version,
                    i,
                    fileTitle,
                    "",
                    "",
                    docFilesList.get(i)


            );
            localDocList.add(docModel);


            if(localDocList.size() > 0){
                noDataEldTV.setVisibility(View.GONE);
            }else {
                noDataEldTV.setVisibility(View.VISIBLE);
            }
            try {
                if (isLoad) {
                    helpDocAdapter = new HelpDocAdapter(getActivity(), localDocList);
                    notiHistoryListView.setAdapter(helpDocAdapter);
                }
            } catch (Exception e) {
               e.printStackTrace();
            }
        }

    }



    void documentViewerFragment(String filePath, String filename ){
        File file = new File(filePath );    //Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Logistic/AlsDoc/" + fileName);
     //   Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);

        try {

            startActivity(
                    PdfViewerActivity.Companion.launchPdfFromPath(
                            getActivity(), String.valueOf(file),
                            "Title", "dir",true,false
                    )
            );


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void displayPdfFromUrl(int position){
        String googleUrl = "https://docs.google.com/viewer?url=";
        Intent browserIntent;
        // Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", new File());

        try {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl + helpDocList.get(position).getDocumentUrl() ));
            startActivity(browserIntent);
        }catch (Exception e){
            Toast.makeText(getActivity(), getResources().getString(R.string.no_pdf_viewer), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    void downloadFile(String url, String VersionCode, String VersionName, int pos) {

        Intent serviceIntent = new Intent(getActivity(), downloadDocService.getClass());
        serviceIntent.putExtra("url", url);
        serviceIntent.putExtra("Version", VersionCode);
        serviceIntent.putExtra("title", VersionName);
        serviceIntent.putExtra("position", pos);
        getActivity().startService(serviceIntent);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.eldMenuLay:
                Logger.LogDebug("count", "stack_count: " + getParentFragmentManager().getBackStackEntryCount());
                eldMenuLay.setEnabled(false);
                if(getParentFragmentManager().getBackStackEntryCount() > 1) {
                    getParentFragmentManager().popBackStack();
                }else{
                    TabAct.host.setCurrentTab(0);
                }
                break;
        }
    }



    /*================== Get Driver Trip Details ===================*/
    void GetEldDocDetails(){  /*, final String SearchDate*/

        Map<String, String> params = new HashMap<String, String>();
        getAppDocDetailsRequest.executeRequest(com.android.volley.Request.Method.GET, APIs.GET_ELD_HELP_DOC , params, getDocDetails,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    void saveDocLocally(int pos){
        boolean hasFile = false;
        String title = helpDocList.get(pos).getDocumentTitle();
        String version = helpDocList.get(pos).getVersion();

        for(int i = 0 ; i < localDocList.size() ; i++){
            String localFileTitle = localDocList.get(i).getDocumentTitle().replaceAll(".pdf", "");
            if(localFileTitle.equals(title) && localDocList.get(i).getVersion().equals(version)){
                hasFile = true;
                break;
            }

        }

        if(!hasFile) {
            downloadFile(helpDocList.get(pos).getDocumentUrl(), helpDocList.get(pos).getVersion(), helpDocList.get(pos).getDocumentTitle(), pos);
        }



    }

    private int  getFilePositionFromLocalDir(int pos){
        int filePosition = -1;

        if(helpDocList.size() > pos) {
            String title = helpDocList.get(pos).getDocumentTitle();
            String version = helpDocList.get(pos).getVersion();

            for (int i = 0; i < localDocList.size(); i++) {
                String localFileTitle = localDocList.get(i).getDocumentTitle().replaceAll(".pdf", "");
                if (localFileTitle.equals(title) && localDocList.get(i).getVersion().equals(version)) {
                    filePosition = i;
                    break;
                }
            }
        }

        return filePosition;
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;  //, dataObj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");

            } catch (JSONException e) {  }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {
                    case getDocDetails:
                        try {
                            Logger.LogDebug("response", "response: " + response);

                            helpDocList = new ArrayList<>();

                            JSONArray docArray = new JSONArray(obj.getString("Data"));
                            for(int i = 0; i< docArray.length() ; i++){
                                JSONObject docObj = (JSONObject)docArray.get(i);
                                String Version = docObj.getString(ConstantsKeys.Version);

                                HelpDocModel docModel = new HelpDocModel(
                                        Version,
                                        docObj.getInt(ConstantsKeys.DocumentNumber),
                                        docObj.getString(ConstantsKeys.Heading),
                                        docObj.getString(ConstantsKeys.ServerDateTime),
                                        docObj.getString(ConstantsKeys.UTCDateTime),
                                        docObj.getString(ConstantsKeys.ELDFilePath)


                                );
                                helpDocList.add(docModel);

                                // ---------- PDF offline commented code -----------
                                // Save pdf file in SD card if not saved
                                   saveDocLocally(i);

                            }

                            helpDocAdapter = new HelpDocAdapter(getActivity(), helpDocList);
                            notiHistoryListView.setAdapter(helpDocAdapter);

                            if(helpDocList.size() > 0){
                                noDataEldTV.setVisibility(View.GONE);
                            }else{
                                noDataEldTV.setVisibility(View.VISIBLE);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;


                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            switch (flag){

                default:
                    Logger.LogDebug("Driver", "error" + error.toString());

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        Toast.makeText(getActivity(), Globally.DisplayErrorMessage(error.toString()), Toast.LENGTH_LONG).show();

                        if (helpDocList.size() > 0) {
                            noDataEldTV.setVisibility(View.GONE);
                        } else {
                            noDataEldTV.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        eldMenuLay.setEnabled(true);
        getLocalDocuments(true);

        if(Globally.isConnected(getActivity())){
            GetEldDocDetails();
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver( progressReceiver, new IntentFilter("download_pdf_progress"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);
    }




    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long percentage     = intent.getIntExtra("percentage", 0);
            int position        = intent.getIntExtra("position", 0);
            boolean isCompleted = intent.getBooleanExtra("isCompleted", false);

            checkDownloadStatus(percentage, position, isCompleted);

        }
    };


    // Check file download status, is file downloaded or in progress..
    private void checkDownloadStatus(long percentage, int pos,  boolean isCompleted){
        switch (pos){
            case 0:
                isDownloading1 = true;
                if(percentage >= progressPercentage1) {
                    progressPercentage1 = percentage;
                }
                if(isCompleted){
                    isDownloading1 = false;
                }

                break;

            case 1:
                isDownloading2 = true;
                if(percentage >= progressPercentage2) {
                    progressPercentage2 = percentage;
                }
                if(isCompleted){
                    isDownloading2 = false;
                }
                break;


            case 2:
                isDownloading3 = true;
                if(percentage >= progressPercentage3) {
                    progressPercentage3 = percentage;
                }
                if(isCompleted){
                    isDownloading3 = false;
                }
                break;

            case 3:
                isDownloading4 = true;
                if(percentage >= progressPercentage4) {
                    progressPercentage4 = percentage;
                }
                if(isCompleted){
                    isDownloading4 = false;
                }
                break;

            case 4:
                isDownloading5 = true;
                if(percentage >= progressPercentage5) {
                    progressPercentage5 = percentage;
                }
                if(isCompleted){
                    isDownloading5 = false;
                }
                break;

            case 5:
                isDownloading6 = true;
                if(percentage >= progressPercentage6) {
                    progressPercentage6 = percentage;
                }
                if(isCompleted){
                    isDownloading6 = false;
                }
                break;

            case 6:
                isDownloading7 = true;
                if(percentage >= progressPercentage7) {
                    progressPercentage7 = percentage;
                }
                if(isCompleted){
                    isDownloading7 = false;
                }
                break;


            case 7:
                isDownloading8 = true;
                if(percentage >= progressPercentage8) {
                    progressPercentage8 = percentage;
                }
                if(isCompleted){
                    isDownloading8 = false;
                }
                break;


            case 8:
                isDownloading9 = true;
                if(percentage >= progressPercentage9) {
                    progressPercentage9 = percentage;
                }
                if(isCompleted){
                    isDownloading9 = false;
                }
                break;


            case 9:
                isDownloading10 = true;
                if(percentage >= progressPercentage10) {
                    progressPercentage10 = percentage;
                }
                if(isCompleted){
                    isDownloading10 = false;
                }
                break;

        }

    }


    void checkFileBeforeView(int position){

        switch (position){
            case 0:
                if(docFile1 != null && docFile1.isFile() && isDownloading1 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile1.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;

            case 1:
                if(docFile2 != null && docFile2.isFile() && isDownloading2 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile2.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;

            case 2:
                if(docFile3 != null && docFile3.isFile() && isDownloading3 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile3.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;

            case 3:
                if(docFile4 != null && docFile4.isFile() && isDownloading4 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile4.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 4:
                if(docFile5 != null && docFile5.isFile() && isDownloading5 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile5.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 5:
                if(docFile6 != null && docFile6.isFile() && isDownloading6 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile6.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 6:
                if(docFile7 != null && docFile7.isFile() && isDownloading7 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile7.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 7:
                if(docFile8 != null && docFile8.isFile() && isDownloading8 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile8.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 8:
                if(docFile9 != null && docFile9.isFile() && isDownloading9 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile9.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;


            case 9:
                if(docFile10 != null && docFile10.isFile() && isDownloading10 == false){
                    if(localDocList.size() > position) {
                        documentViewerFragment(docFile10.toString(), localDocList.get(position).getDocumentTitle());
                    }else{
                        GetEldDocDetails();
                        global.EldScreenToast(eldMenuLay, getResources().getString(R.string.document_checking), getContext().getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    displayPdfFromUrl(position);
                }

                break;



        }

    }

}
