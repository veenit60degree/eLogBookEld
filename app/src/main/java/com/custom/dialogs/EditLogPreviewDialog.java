package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

import com.constants.ConstantHtml;
import com.constants.Constants;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditLogPreviewDialog extends Dialog {

    public interface EditLogPreviewListener {
        public void EditPreviewReady();
    }

    Constants constants;
    HelperMethods hMethods;
    JSONArray driverLogList;
    private EditLogPreviewListener readyListener;
    Button btnSavePreviewLog, btnCancelPreviewLog;
    WebView previewGraphWebView;
    String DefaultLine      = " <g class=\"event \">\n";
    String ViolationLine    = " <g class=\"event line-red\">\n";
    String htmlAppendedText = "";

    int DRIVER_JOB_STATUS = 1, OldStatus = -1;

    int startHour       = 0;
    int startMin        = 0;
    int endHour         = 0;
    int endMin          = 0;
    int hLineX1         = 0;
    int hLineX2         = 0;

    int OFF             = 25;
    int SB              = 75;
    int DR              = 125;
    int ON              = 175;


    boolean isViolation = false;

    public EditLogPreviewDialog(Context context, JSONArray logList, EditLogPreviewListener readyListener) {
        super(context);
        this.driverLogList = logList;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_editlog_preview);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        constants           = new Constants();
        hMethods            = new HelperMethods();
        btnSavePreviewLog   = (Button) findViewById(R.id.btnSavePreviewLog);
        btnCancelPreviewLog = (Button) findViewById(R.id.btnCancelPreviewLog);
        previewGraphWebView = (WebView) findViewById(R.id.previewGraphWebView);


        ParseJsonArray(driverLogList);

        btnSavePreviewLog.setOnClickListener(new EditPreviewListener());
        btnCancelPreviewLog.setOnClickListener(new CancelBtnListener());

    }


    private class EditPreviewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                readyListener.EditPreviewReady();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            dismiss();
        }
    }


    void LoadWebView(){

        String TotalOnDutyHours        = Globally.FinalValue(hMethods.GetDutyStatusTimeInterval(driverLogList, constants, EldFragment.ON_DUTY));
        String TotalDrivingHours       = Globally.FinalValue(hMethods.GetDutyStatusTimeInterval(driverLogList, constants, EldFragment.DRIVING));
        String TotalOffDutyHours       = Globally.FinalValue(hMethods.GetDutyStatusTimeInterval(driverLogList, constants, EldFragment.OFF_DUTY));
        String TotalSleeperBerthHours  = Globally.FinalValue(hMethods.GetDutyStatusTimeInterval(driverLogList, constants, EldFragment.SLEEPER));

        String CloseTag = HtmlCloseTag(TotalOffDutyHours, TotalSleeperBerthHours, TotalDrivingHours, TotalOnDutyHours);

        String data = ConstantHtml.GraphHtml + htmlAppendedText + CloseTag;
       // previewGraphWebView.loadData(data, "text/html; charset=UTF-8", null);
        previewGraphWebView.loadDataWithBaseURL("" , data, "text/html", "UTF-8", "");
    }




    String AddHorizontalLine(int hLineX1, int hLineX2, int hLineY){
        return "<line class=\"horizontal\" x1=\""+ hLineX1 +"\" x2=\""+ hLineX2 +"\" y1=\""+ hLineY +"\" y2=\""+ hLineY +"\"></line>\n" ;
    }


    String AddVerticalLine(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }


    String AddVerticalLineViolation(int vLineX, int vLineY1, int vLineY2){
        return "<line class=\"vertical no-color\" x1=\""+ vLineX +"\" x2=\""+ vLineX +"\" y1=\""+ vLineY1 +"\" y2=\""+ vLineY2 +"\"></line>\n" ;
    }


    void DrawGraph(int hLineX1, int hLineX2, int vLineY1, int vLineY2, boolean isViolation){
        if(isViolation){
            htmlAppendedText = htmlAppendedText + ViolationLine +
                    AddHorizontalLine(hLineX1, hLineX2, vLineY2 ) +
                    AddVerticalLineViolation(hLineX1, vLineY1, vLineY2 )+
                    "                  </g>\n";
        }else{
            htmlAppendedText = htmlAppendedText + DefaultLine +
                    AddHorizontalLine(hLineX1, hLineX2, vLineY2 ) +
                    AddVerticalLine(hLineX1, vLineY1, vLineY2 )+
                    "                  </g>\n";
        }
    }



    String HtmlCloseTag(String OffDutyHour, String SleeperHour, String DrivingHour, String OnDutyHour ){
        return " </g>\n" +
                "   <g class=\"durations\" transform=\"translate(1505, 40)\">\n" +
                "                  <text class=\"label\" transform=\"translate(0, 25)\" dy=\"0.35em\">"+OffDutyHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 75)\" dy=\"0.35em\">"+SleeperHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 125)\" dy=\"0.35em\">"+DrivingHour+"</text>\n" +
                "                  <text class=\"label\" transform=\"translate(0, 175)\" dy=\"0.35em\">"+OnDutyHour+"</text>\n" +
                "               </g>    \n" +
                "            </svg>\n" +
                "         </log-graph>\n" +
                "      </div>";
    }



    void ParseJsonArray(JSONArray driverLogJsonArray){
        try{
            for(int logCount = 0 ; logCount < driverLogJsonArray.length() ; logCount ++) {
                JSONObject logObj = (JSONObject) driverLogJsonArray.get(logCount);
                DRIVER_JOB_STATUS = logObj.getInt("DriverStatusId");
                String startDateTime = logObj.getString("StartDateTime");
                String endDateTime = logObj.getString("EndDateTime");

                if(DRIVER_JOB_STATUS == 3 || DRIVER_JOB_STATUS == 4) {
                    if (!logObj.isNull("IsViolation")) {
                        isViolation = logObj.getBoolean("IsViolation");
                    } else {
                        isViolation = false;
                    }
                }else{
                    isViolation = false;
                }


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Globally.DateFormat);  //:SSSZ
                Date startDate = new Date();
                Date endDate = new Date();
                startHour = 0; startMin = 0; endHour = 0; endMin = 0;

              /*  if(logCount > 0 && logCount == driverLogJsonArray.length()-1 ) {
                    if(endDateTime.length() > 16 && endDateTime.substring(11,16).equals("00:00")){
                        endDateTime = endDateTime.substring(0,11) + "23:59" +endDateTime.substring(16,endDateTime.length());
                    }
                }*/
                try {
                    startDate   = simpleDateFormat.parse(startDateTime);
                    endDate     = simpleDateFormat.parse(endDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startHour   = startDate.getHours() * 60;
                startMin    = startDate.getMinutes();
                endHour     = endDate.getHours() * 60;
                endMin      = endDate.getMinutes();

                if( driverLogJsonArray.length() == 1) {
                    long diff = Globally.DateDifference(startDate, endDate);
                    if (diff > 0 && endDate.getHours() == 0) {
                        endHour = 24 * 60;
                    }
                }

                hLineX1 =   startHour + startMin;
                hLineX2 =   endHour + endMin;

                int VerticalLineX = VerticalLine(OldStatus);
                int VerticalLineY = VerticalLine(DRIVER_JOB_STATUS);

                if(hLineX2 > hLineX1) {
                    if (OldStatus != -1) {
                        DrawGraph(hLineX1, hLineX2, VerticalLineX, VerticalLineY, isViolation);
                    } else {
                        DrawGraph(hLineX1, hLineX2, VerticalLineY, VerticalLineY, isViolation);
                    }
                }
                OldStatus   =   DRIVER_JOB_STATUS;
            }

            // -------- Load Graph on WebView ----------
            LoadWebView();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    int VerticalLine(int status){
        int job = 0;

        switch (status){
            case 1:
                job = OFF;
                break;

            case 2:
                job = SB;
                break;

            case 3:
                job = DR;
                break;

            case 4:
                job = ON;
                break;
        }
        return job;
    }


}
