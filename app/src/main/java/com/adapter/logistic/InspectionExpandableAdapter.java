package com.adapter.logistic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.models.SavedInspectionModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InspectionExpandableAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private HashMap<String, List<SavedInspectionModel>> _listDataChild;

    boolean IsTablet;
    private DisplayImageOptions options;
    ViewInspectionGridAdapter truckAdapter, trailerAdapter;


    public InspectionExpandableAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<SavedInspectionModel>> listChildData,
                                       DisplayImageOptions displayOptions, boolean isTablet) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.options = displayOptions;
        this.IsTablet   = isTablet;

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

        final ViewHolder holder ;
        SavedInspectionModel supportModel= (SavedInspectionModel) getChild(groupPosition, childPosition); //_listDataChild.get(childPosition).get(i);


        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_inspection_layout, null);

            holder.inspectionTypeTV         = (TextView)convertView.findViewById(R.id.inspectionTypeTV);
            holder.noDefectLabel            = (TextView)convertView.findViewById(R.id.noDefectLabel);
            holder.inspectionDateTv         = (TextView)convertView.findViewById(R.id.inspectionDateTv);
            holder.powerInspectionTV        = (TextView)convertView.findViewById(R.id.powerInspectionTV);
            holder.trailerInspectionTV      = (TextView)convertView.findViewById(R.id.trailerInspectionTV);

            holder.prePostRadioGroup        = (RadioGroup)convertView.findViewById(R.id.prePostRadioGroup);
            holder.correctRadioGroup        = (RadioGroup)convertView.findViewById(R.id.correctRadioGroup);
            holder.preTripButton            = (RadioButton) convertView.findViewById(R.id.preTripButton);
            holder.postTripButton           = (RadioButton)convertView.findViewById(R.id.postTripButton);
            holder.DefectsCorrectedBtn      = (RadioButton)convertView.findViewById(R.id.DefectsCorrectedBtn);
            holder.DefectsNotCorrectedBtn   = (RadioButton)convertView.findViewById(R.id.DefectsNotCorrectedBtn);


            holder.changeLocBtn             = (Button)convertView.findViewById(R.id.changeLocBtn);
            holder.saveInspectionBtn        = (Button)convertView.findViewById(R.id.saveInspectionBtn);

            holder.remarksEditText          = (EditText)convertView.findViewById(R.id.remarksEditText);
            holder.SupervisorNameTV         = (EditText)convertView.findViewById(R.id.SupervisorNameTV);
            holder.locInspectionTV          = (AutoCompleteTextView)convertView.findViewById(R.id.locInspectionTV);


            holder.signDriverIV             = (ImageView) convertView.findViewById(R.id.signDriverIV);
            holder.signSuprvsrIV            = (ImageView) convertView.findViewById(R.id.signSuprvsrIV);
            holder.imgTruck                 = (ImageView) convertView.findViewById(R.id.imgTruck);
            holder.imgTrailer               = (ImageView) convertView.findViewById(R.id.imgTrailer);

            holder.supervisorNameLay        = (LinearLayout)convertView.findViewById(R.id.supervisorNameLay);

            holder.truckTrailerTVLay        = (RelativeLayout)convertView.findViewById(R.id.truckTrailerTVLay);
            holder.truckTrailerLayout       = (RelativeLayout)convertView.findViewById(R.id.truckTrailerLayout);
            holder.superviserSignLay        = (RelativeLayout)convertView.findViewById(R.id.superviserSignLay);


            holder.truckGridView            = (GridView)convertView.findViewById(R.id.truckGridView);
            holder.trailerGridView          = (GridView)convertView.findViewById(R.id.trailerGridView);


            if(supportModel.getInspectionTypeId() == Constants.Trailer){
               // holder.inspectionTypeTV.setText("Trailer Inspection" );
                holder.truckTrailerTVLay.setVisibility(View.GONE);
                holder.truckTrailerLayout.setVisibility(View.GONE);

            }/*else if(supportModel.getInspectionTypeId() == Constants.PostInspection){
                holder.inspectionTypeTV.setText("Post Trip Inspection" );
            }else{
                holder.inspectionTypeTV.setText("Pre Trip Inspection" );
            }*/

            holder.prePostRadioGroup.setVisibility(View.GONE);
            holder.inspectionTypeTV.setVisibility(View.GONE);
            holder.saveInspectionBtn.setVisibility(View.GONE);
            holder.changeLocBtn.setVisibility(View.GONE);
            holder.imgTruck.setVisibility(View.GONE);
            holder.imgTrailer.setVisibility(View.GONE);
            holder.locInspectionTV.setEnabled(false);
            holder.remarksEditText.setEnabled(false);
            holder.preTripButton.setEnabled(false);
            holder.postTripButton.setEnabled(false);
            holder.DefectsCorrectedBtn.setChecked(false);
            holder.DefectsNotCorrectedBtn.setEnabled(false);
            holder.remarksEditText.setEnabled(false);
            holder.SupervisorNameTV.setEnabled(false);
            holder.locInspectionTV.setEnabled(false);

            holder.powerInspectionTV.setText(supportModel.getVehicleEquNumber());
            holder.trailerInspectionTV.setText(supportModel.getTrailorEquNumber());
            holder.locInspectionTV.setText(supportModel.getLocation());
            holder.remarksEditText.setText(supportModel.getRemarks());
            holder.inspectionDateTv.setText(convertDateFormat(supportModel.getCreatedDate(), supportModel.getInspectionDateTime() ));

            String DriverImage = "", SupervisorImage = "";
            DriverImage = supportModel.getDriverSignature();
            SupervisorImage = supportModel.getSupervisorMechanicsSignature();

            ImageLoader.getInstance().displayImage( DriverImage, holder.signDriverIV , options);
            ImageLoader.getInstance().displayImage( SupervisorImage, holder.signSuprvsrIV , options);

            if(SupervisorImage.length() > 0 && !SupervisorImage.equals("null") ) {
                holder.SupervisorNameTV.setText(supportModel.getSupervisorMechanicsName());
                holder.supervisorNameLay.setVisibility(View.VISIBLE);
                holder.superviserSignLay.setVisibility(View.VISIBLE);
            }else{
                holder.superviserSignLay.setVerticalGravity(View.GONE);
            }

            try{
                if(supportModel.IsPreTripInspectionSatisfactory()){
                    setPrePostBackgroundGreen(1, holder.preTripButton, holder.postTripButton);
                }else if(supportModel.IsPostTripInspectionSatisfactory()){
                    setPrePostBackgroundGreen(2, holder.preTripButton, holder.postTripButton);
                }


                if(supportModel.IsAboveDefectsCorrected()){
                    setBackgroundGreen( 1, holder.noDefectLabel, holder.DefectsCorrectedBtn,  holder.DefectsNotCorrectedBtn,
                            holder.correctRadioGroup, holder.superviserSignLay) ;

                }else if(supportModel.IsAboveDefectsNotCorrected()){
                    setBackgroundGreen(2, holder.noDefectLabel, holder.DefectsCorrectedBtn,  holder.DefectsNotCorrectedBtn,
                            holder.correctRadioGroup, holder.superviserSignLay);
                }else {
                    holder.noDefectLabel.setText("No Defects");
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            ArrayList<String> TruckList = supportModel.getTruckList();
            ArrayList<String> TrailerList = supportModel.getTrailerList();

            try{
                truckAdapter = new ViewInspectionGridAdapter(_context,  TruckList, false);
                holder.truckGridView.setAdapter(truckAdapter);
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                trailerAdapter = new ViewInspectionGridAdapter(_context, TrailerList, false);
                holder.trailerGridView.setAdapter(trailerAdapter);
            }catch (Exception e){
                e.printStackTrace();
            }

            final int truckViewCount      = TruckList.size() / 2 + TruckList.size() % 2;
            final int trailerViewCount    = TrailerList.size() / 2 + TrailerList.size() % 2;

            int viewHeight = 50;
            if(IsTablet){
                viewHeight = 50;
            }else{
                viewHeight = 41;
            }

            try {
                RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Globally.dp2px(viewHeight, _context) * truckViewCount) ;  //SavedInspectionsFragment.inspectionLayHeight
                holder.truckGridView.setLayoutParams(mParam);
            }catch (Exception e){}

            try {
                RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Globally.dp2px(viewHeight, _context) * trailerViewCount) ; //SavedInspectionsFragment.inspectionLayHeight
                holder.trailerGridView.setLayoutParams(mParam);
            }catch (Exception e){}

            convertView.setTag(holder);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
      //  return this._listDataChild.get(String.valueOf(groupPosition)).size(); //this._listDataHeader.get(
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)) .size();

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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_inspection, null);
        }

        CheckBox checkboxInspection = (CheckBox)convertView.findViewById(R.id.checkboxInspection);
        checkboxInspection.setVisibility(View.INVISIBLE);

        TextView inspectionTruckTV = (TextView) convertView.findViewById(R.id.inspectionTruckTV);
        inspectionTruckTV.setTypeface(null, Typeface.BOLD);
        inspectionTruckTV.setText(headerTitle);
        inspectionTruckTV.setTextColor(_context.getResources().getColor(R.color.color_eld_bg));

        LinearLayout inspectionItemLay = (LinearLayout)convertView.findViewById(R.id.inspectionItemLay);
        inspectionItemLay.setBackgroundColor(_context.getResources().getColor(R.color.eld_gray_bg));


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }



    public class ViewHolder {
        TextView inspectionTypeTV, noDefectLabel, inspectionDateTv, powerInspectionTV, trailerInspectionTV;
        RadioGroup prePostRadioGroup, correctRadioGroup;
        Button changeLocBtn, saveInspectionBtn;
        RelativeLayout truckTrailerTVLay, truckTrailerLayout, superviserSignLay;
        GridView truckGridView, trailerGridView ;
        RadioButton preTripButton, postTripButton, DefectsCorrectedBtn, DefectsNotCorrectedBtn;
        EditText remarksEditText, SupervisorNameTV;
        AutoCompleteTextView locInspectionTV;

        ImageView signDriverIV, signSuprvsrIV, imgTruck, imgTrailer;
        LinearLayout supervisorNameLay;
    }


    public int getChildType(final int groupPosition, final int childPosition){
        return  groupPosition;
    }

    public int getChildTypeCount(){
        return _listDataHeader.size();
    }


    void setBackgroundGreen(int SelectedBtn, TextView noDefectLabel, RadioButton DefectsCorrectedBtn, RadioButton DefectsNotCorrectedBtn,
                            RadioGroup correctRadioGroup, RelativeLayout superviserSignLay){


        noDefectLabel.setVisibility(View.GONE);
        noDefectLabel.setText("Defects");
        switch (SelectedBtn){

            case 1:
                setBackgroundSelected(DefectsCorrectedBtn);
                setBackgroundUnselected(DefectsNotCorrectedBtn);

                correctRadioGroup.setVisibility(View.VISIBLE);
                superviserSignLay.setVisibility(View.VISIBLE);
                noDefectLabel.setVisibility(View.VISIBLE);
                break;

            case 2:
                setBackgroundSelected(DefectsNotCorrectedBtn);
                setBackgroundUnselected(DefectsCorrectedBtn);

                correctRadioGroup.setVisibility(View.VISIBLE);
                superviserSignLay.setVisibility(View.VISIBLE);
                noDefectLabel.setVisibility(View.VISIBLE);

                break;

        }

    }


    void setPrePostBackgroundGreen( int SelectedBtn, RadioButton preTripButton, RadioButton postTripButton) {

        switch (SelectedBtn) {

            case 1:
                setBackgroundSelected(preTripButton);
                setBackgroundUnselected(postTripButton);

                break;

            case 2:
                setBackgroundSelected(postTripButton);
                setBackgroundUnselected(preTripButton);

                break;

        }

    }


    void setBackgroundUnselected(RadioButton view){
        view.setEnabled(false);
        view.setChecked(false);
        view.setTextColor(_context.getResources().getColor(R.color.black_semi));
    }

    void setBackgroundSelected(RadioButton view){
        view.setEnabled(true);
        view.setChecked(true);
        view.setTextColor(_context.getResources().getColor(R.color.color_eld_theme));
    }

    String convertDateFormat(String CreatedDate, String InspectionDateTime){
        try {
            if(!CreatedDate.equals("N/A")) {
                CreatedDate = Globally.ConvertDateTimeFormat(CreatedDate);
                if(CreatedDate.equals("")){
                    CreatedDate = InspectionDateTime;
                }
            }else {
                CreatedDate = InspectionDateTime;
            }
        }catch (Exception e){
            CreatedDate = InspectionDateTime;
        }

        return  CreatedDate;
    }



}

