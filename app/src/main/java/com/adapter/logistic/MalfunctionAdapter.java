package com.adapter.logistic;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.constants.APIs;
import com.constants.AlertDialogEld;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveLogJsonObj;
import com.constants.SharedPref;
import com.custom.dialogs.MalfunctionDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.MalfncnDiagnstcViewPager;
import com.messaging.logistic.fragment.MalfunctionFragment;
import com.models.MalfunctionHeaderModel;
import com.models.MalfunctionModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class MalfunctionAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<MalfunctionHeaderModel> _listDataHeader;
    private HashMap<String, List<MalfunctionModel>> _listDataChild;
    String DriverId, CurrentCycleId;
    Constants constants;
    SaveLogJsonObj clearRecordPost;
    ProgressDialog progressDialog;
    MalfunctionDialog malfunctionDialog;
    int selectedPos = 0;
    AlertDialogEld confirmationDialog;


    public MalfunctionAdapter(Context context, String driverId, List<MalfunctionHeaderModel> listDataHeader,
                                       HashMap<String, List<MalfunctionModel>> listChildData ) {
        this._context = context;
        this.DriverId = driverId;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        constants = new Constants();

        CurrentCycleId    = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, _context);

        clearRecordPost   = new SaveLogJsonObj(_context, apiResponse );
        progressDialog    = new ProgressDialog(_context);
        progressDialog.setMessage("Loading ...");

        confirmationDialog  = new AlertDialogEld(_context);

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);


    }



    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

       final MalfunctionModel childData = _listDataChild.get(_listDataHeader.get(groupPosition).getEventCode()).get(childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_child_malfunction, null);
        }

        TextView timeMalTxtVw = (TextView) convertView.findViewById(R.id.timeMalTxtVw);
        TextView statusMalTxtVw = (TextView) convertView.findViewById(R.id.statusMalTxtVw);
        TextView vehMilesMalTxtVw = (TextView) convertView.findViewById(R.id.vehMilesMalTxtVw);
        TextView engHoursMalTxtVw = (TextView) convertView.findViewById(R.id.engHoursMalTxtVw);
        TextView seqIdMalTxtVw = (TextView) convertView.findViewById(R.id.seqIdMalTxtVw);
        TextView originMalTxtVw = (TextView) convertView.findViewById(R.id.originMalTxtVw);

        if(childData.getDriverZoneEventDate().length() > 10) {
            String date = Globally.dateConversionMalfunction(childData.getDriverZoneEventDate());   //EventDateTime()
            timeMalTxtVw.setText(date);
        }else {
            timeMalTxtVw.setText( "");
        }

       // statusMalTxtVw.setText(childData.getMalfunctionDefinition());
        engHoursMalTxtVw.setText(childData.getEngineHours());
        originMalTxtVw.setText("");
        originMalTxtVw.setVisibility(View.GONE);

        if(childData.getHexaSequenceNo().length() > 0){
            seqIdMalTxtVw.setText(childData.getHexaSequenceNo());
        }else{
            seqIdMalTxtVw.setText(childData.getSequenceNo());
        }
        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            double miles = Double.parseDouble(childData.getMiles());
            String milesInKm = constants.Convert2DecimalPlacesDouble(constants.milesToKm(miles));
            vehMilesMalTxtVw.setText(milesInKm);
        }else{
            vehMilesMalTxtVw.setText(childData.getMiles());
        }

        LinearLayout malfunctionChildLay = (LinearLayout)convertView.findViewById(R.id.malfunctionChildLay);
        if(childPosition == _listDataChild.get(_listDataHeader.get(groupPosition).getEventCode()).size() -1 ) {
            malfunctionChildLay.setBackgroundResource(R.drawable.malfunction_child_selector);
        }else{
            malfunctionChildLay.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).getEventCode()) .size();
    }



    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }



    public class GroupViewHolder {
        TextView timeMalTxtVw, statusMalTxtVw, vehMilesMalTxtVw, engHoursMalTxtVw, seqIdMalTxtVw, originMalTxtVw;
        TextView malfHeaderTxtVw, clearEventBtn;
        ImageView groupIndicatorBtn, malfncnInfoImgView;
        RelativeLayout malfunctionChildMainLay;
        LinearLayout malfunctionChildLay;

    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        MalfunctionHeaderModel headerModel = (MalfunctionHeaderModel) getGroup(groupPosition);
        final GroupViewHolder holder ;


        if (convertView == null) {
            holder = new GroupViewHolder();
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_header_malfunction, null);

            holder.timeMalTxtVw = (TextView) convertView.findViewById(R.id.timeMalTxtVw);
            holder.statusMalTxtVw = (TextView) convertView.findViewById(R.id.statusMalTxtVw);
            holder.vehMilesMalTxtVw = (TextView) convertView.findViewById(R.id.vehMilesMalTxtVw);
            holder.engHoursMalTxtVw = (TextView) convertView.findViewById(R.id.engHoursMalTxtVw);
            holder.seqIdMalTxtVw = (TextView) convertView.findViewById(R.id.seqIdMalTxtVw);
            holder.originMalTxtVw = (TextView) convertView.findViewById(R.id.originMalTxtVw);

            holder.clearEventBtn = (TextView) convertView.findViewById(R.id.clearEventBtn);
            holder.malfHeaderTxtVw = (TextView) convertView.findViewById(R.id.malfHeaderTxtVw);
            holder.malfunctionChildMainLay = (RelativeLayout)convertView.findViewById(R.id.malfunctionChildMainLay);
            holder.malfunctionChildLay = (LinearLayout)convertView.findViewById(R.id.malfunctionChildLay);
            holder.groupIndicatorBtn = (ImageView)convertView.findViewById(R.id.groupIndicatorBtn);
            holder.malfncnInfoImgView= (ImageView)convertView.findViewById(R.id.malfncnInfoImgView);

            convertView.setTag(holder);

        }else{
            holder = (GroupViewHolder) convertView.getTag();
        }



        setViewTextColorWithStyle(holder.timeMalTxtVw, holder.statusMalTxtVw, holder.vehMilesMalTxtVw,
                holder.engHoursMalTxtVw, holder.seqIdMalTxtVw, holder.originMalTxtVw);
        holder.originMalTxtVw.setVisibility(View.GONE);


        holder.malfHeaderTxtVw.setText(headerModel.getEventName());

        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            holder.vehMilesMalTxtVw.setText(_context.getResources().getString(R.string.vehKm));
        }

        holder.malfunctionChildLay.setBackgroundColor(_context.getResources().getColor(R.color.blue_button));
        holder.malfunctionChildMainLay.setBackgroundColor(_context.getResources().getColor(R.color.white));

        int sizePadding =  constants.intToPixel(_context, 5);
        int sizeMargin =  constants.intToPixel(_context, 10);

        if(Globally.isTablet(_context)){
            sizePadding =  constants.intToPixel(_context, 8);
            sizeMargin =  constants.intToPixel(_context, 15);
        }

        holder.malfunctionChildLay.setPadding(sizePadding, sizePadding, sizePadding, sizePadding);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(sizeMargin, 0, sizeMargin, 0);
        holder.malfunctionChildLay.setLayoutParams(params);

        if (isExpanded) {
            holder.groupIndicatorBtn.setImageResource(R.drawable.arrow_top);
            holder.malfunctionChildMainLay.setVisibility(View.VISIBLE);
        } else {
            holder.groupIndicatorBtn.setImageResource(R.drawable.arrow_bottom);
            holder.malfunctionChildMainLay.setVisibility(View.GONE);
        }

        if(constants.isValidInteger(headerModel.getEventCode() ) ){
                    // EventCode value is an String is Diagnostic
            if(!SharedPref.IsClearDiagnostic(_context)){
                holder.clearEventBtn.setVisibility(View.GONE);
            }else{
                holder.clearEventBtn.setVisibility(View.VISIBLE);
            }
        }else{
                    // EventCode value is an String is malfunction
            if(!SharedPref.IsClearMalfunction(_context)){
                holder.clearEventBtn.setVisibility(View.GONE);
            }else{
                holder.clearEventBtn.setVisibility(View.VISIBLE);
            }
        }


        holder.clearEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ClickEvent", "Clear Btn click Event");

                if (malfunctionDialog != null && malfunctionDialog.isShowing())
                    malfunctionDialog.dismiss();

                selectedPos = groupPosition;
                malfunctionDialog = new MalfunctionDialog(_context,
                        _listDataChild.get(_listDataHeader.get(groupPosition).getEventCode()) ,
                        new MalfunctionDiagnosticListener());
                malfunctionDialog.show();


            }
        });


        holder.malfncnInfoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.ShowAlertDialog(_listDataHeader.get(groupPosition).getEventName(), _listDataHeader.get(groupPosition).getEventDesc(),
                        _context.getResources().getString(R.string.dismiss), "",
                        101, positiveCallBack, negativeCallBack);
            }
        });


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }





    public int getChildType(final int groupPosition, final int childPosition){
        return  groupPosition;
    }

    public int getChildTypeCount(){
        return _listDataHeader.size();
    }


    private void setViewTextColorWithStyle(TextView timeMalTxtVw, TextView statusMalTxtVw, TextView vehMilesMalTxtVw,
                                  TextView engHoursMalTxtVw, TextView seqIdMalTxtVw, TextView originMalTxtVw){
        timeMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white )); //blue_button
       // statusMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white));
        vehMilesMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white));
        engHoursMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white));
        seqIdMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white));
        originMalTxtVw.setTextColor(_context.getResources().getColor(R.color.white));

        timeMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);
      //  statusMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);
        vehMilesMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);
        engHoursMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);
        seqIdMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);
        originMalTxtVw.setTypeface(timeMalTxtVw.getTypeface(), Typeface.BOLD);

      /*  int textSize =  constants.intToPixel(_context, R.dimen.text_size_15);
        timeMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
        statusMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
        vehMilesMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
        engHoursMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
        seqIdMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
        originMalTxtVw.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
*/
    }




    private class MalfunctionDiagnosticListener implements MalfunctionDialog.RecordsListener{

        @Override
        public void RecordsOkBtn(String reason, List<MalfunctionModel> listData) {

            if (malfunctionDialog != null && malfunctionDialog.isShowing())
                malfunctionDialog.dismiss();

            JSONObject clearEventObj = Constants.getMalfunctionRecordsInArray( DriverId, reason, listData);

            if (clearEventObj.length() > 0) {
                 progressDialog.show();
                 clearRecordPost.SaveLogJsonObj(clearEventObj, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT, Constants.SocketTimeout30Sec, true, false, 0, 101);
            }

        }
    }




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse apiResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

            try {

                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();


                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ConstantsKeys.Status);
                String message  = obj.getString(ConstantsKeys.Message);
                if (status.equalsIgnoreCase("true")) {
                // {"Status":true,"Message":"Required Parameter is empty","Data":null}



                    if(message.equals("Record Clear Successfully")) {
                        Globally.EldScreenToast( MalfncnDiagnstcViewPager.invisibleMalfnBtn,  _context.getResources().getString(R.string.RecordClearedSuccessfully),
                                _context.getResources().getColor(R.color.color_eld_theme));

                     //   _listDataHeader.remove(selectedPos);
                     //   _listDataChild.remove(selectedPos);

                     //   notifyDataSetChanged();

                       // MalfunctionFragment.invisibleMalfnBtn.performClick();
                        MalfncnDiagnstcViewPager.invisibleMalfnBtn.performClick();
                    }else{
                        Globally.EldScreenToast(MalfunctionFragment.invisibleMalfnBtn, message,
                                _context.getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    // {"Status":false,"Message":"Failed..","Data":null}
                    Globally.EldScreenToast(MalfunctionFragment.invisibleMalfnBtn, message,
                            _context.getResources().getColor(R.color.colorVoilation));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: ");

            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();


            Globally.EldScreenToast(MalfunctionFragment.invisibleMalfnBtn, error,
                    _context.getResources().getColor(R.color.colorVoilation));
        }

    };



    AlertDialogEld.PositiveButtonCallback positiveCallBack = new AlertDialogEld.PositiveButtonCallback() {
        @Override
        public void getPositiveClick(int flag) {
        }
    };

    AlertDialogEld.NegativeButtonCallBack negativeCallBack = new AlertDialogEld.NegativeButtonCallBack() {
        @Override
        public void getNegativeClick(int flag) {
            Log.d("negativeCallBack", "negativeCallBack: " + flag);
        }
    };



}

