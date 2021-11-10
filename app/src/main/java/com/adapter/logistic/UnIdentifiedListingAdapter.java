package com.adapter.logistic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.UnidentifiedFragment;
import com.messaging.logistic.fragment.UnidentifiedRecordDetailFragment;
import com.models.UnIdentifiedRecordModel;

import java.util.ArrayList;
import java.util.List;

public class UnIdentifiedListingAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<UnIdentifiedRecordModel> unIdentifiedList;
    boolean IsAllSelectedClicked;
    boolean isChecked;
    ArrayList<String> SelectedRecordList;
    String DriverId, DriverName;
    Fragment fragment;


    public UnIdentifiedListingAdapter(Context context, String DriverId, String DriverName, boolean IsAllSelected, boolean isChecked,
                                      ArrayList<String> recordSelectedArray, List<UnIdentifiedRecordModel> list, Fragment fragment){
        this.context = context;
        this.unIdentifiedList = list;
        this.DriverId = DriverId;
        this.DriverName = DriverName;
        this.SelectedRecordList = recordSelectedArray;
        this.IsAllSelectedClicked = IsAllSelected;
        this.isChecked = isChecked;
        this.fragment = fragment;
        mInflater = LayoutInflater.from(context);




    }

    @Override
    public int getCount() {
        return unIdentifiedList.size();
    }

    @Override
    public Object getItem(int position) {
        return unIdentifiedList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_unidentified_listing, null);

            holder.unIdenDistanceTV = (TextView)convertView.findViewById(R.id.unIdenDistanceTV);
            holder.unIdenRecordDescTV = (TextView)convertView.findViewById(R.id.unIdenRecordDescTV);
            holder.requestedByTxtVw = (TextView)convertView.findViewById(R.id.requestedByTxtVw);
            holder.unIdenRecorTimeTV = (TextView)convertView.findViewById(R.id.unIdenRecorTimeTV);
            holder.unIdenRecordStatusTV = (TextView)convertView.findViewById(R.id.unIdenRecordStatusTV);

            holder.checkboxUnIdentifiedRecord = (CheckBox)convertView.findViewById(R.id.checkboxUnIdentifiedRecord);
            holder.unIdentifiedDtailView    = (RelativeLayout)convertView.findViewById(R.id.unIdentifiedDtailView) ;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String startDate = Globally.dateConversionMalfunction(unIdentifiedList.get(position).getStartDateTime());   //Globally.convertToMonNameFormat(unIdentifiedList.get(position).getStartDateTime() );
        String endDate = unIdentifiedList.get(position).getEndDateTime();
        if(endDate.equals("null")){
            endDate = "";
        }
        if(endDate.length() > 10) {
            endDate = Globally.dateConversionMalfunction(endDate); //Globally.convertToMonNameFormat(unIdentifiedList.get(position).getEndDateTime() );
        }
        holder.unIdenRecordDescTV.setText(startDate + " - "+endDate);

       holder.unIdenRecorTimeTV.setText("");
       String DutyStatus = unIdentifiedList.get(position).getDutyStatus();
       if(DutyStatus.equals("OD")){
           DutyStatus = "ON";
       }
       if(DutyStatus.equals("DR") || DutyStatus.equals("ON")){
           holder.unIdenRecordStatusTV.setText("(" + DutyStatus + ")");
       }

        if(unIdentifiedList.get(position).isCompanyAssigned()){
            holder.requestedByTxtVw.setVisibility(View.VISIBLE);
        }

        String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
        if (CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
            holder.unIdenDistanceTV.setText(unIdentifiedList.get(position).getTotalMiles() + " Miles");
        }else{
            holder.unIdenDistanceTV.setText(unIdentifiedList.get(position).getTotalKm() + " km");
        }

        if(IsAllSelectedClicked) {
            if (isChecked) {
                SelectedRecordList.set(position, "selected");
                holder.checkboxUnIdentifiedRecord.setChecked(true);
            } else {
                SelectedRecordList.set(position, "");
                holder.checkboxUnIdentifiedRecord.setChecked(false);
            }
        }




        holder.checkboxUnIdentifiedRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkboxUnIdentifiedRecord.isChecked()){
                    SelectedRecordList.set(position, "selected");
                }else{
                    SelectedRecordList.set(position, "");
                }

                IsAllSelectedClicked = false;
                UnidentifiedFragment.isItemViewClicked = true;
                int count = getSelectedCount();
                if(count < SelectedRecordList.size()){
                    UnidentifiedFragment.checkboxUnIdentifiedRecord.setChecked(false);
                }else{
                    UnidentifiedFragment.checkboxUnIdentifiedRecord.setChecked(true);
                }
                UnidentifiedFragment.checkboxUnIdentifiedRecord.setText(context.getResources().getString(R.string.select_all) + " (" + count + ")");
                UnidentifiedFragment.isItemViewClicked = false;
            }
        });


        holder.unIdentifiedDtailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveFragment(
                        unIdentifiedList.get(position).getDriverStatusId(),
                        unIdentifiedList.get(position).getUnAssignedVehicleMilesId(),
                        unIdentifiedList.get(position).getAssignedUnidentifiedRecordsId(),
                        unIdentifiedList.get(position).isCompanyAssigned(),
                        unIdentifiedList.get(position).getStartOdometer(),
                        unIdentifiedList.get(position).getEndOdometer(),
                        unIdentifiedList.get(position).getStartDateTime(),
                        unIdentifiedList.get(position).getEndDateTime(),
                        unIdentifiedList.get(position).getStartLocation(),
                        unIdentifiedList.get(position).getEndLocation(),
                        unIdentifiedList.get(position).getTotalMiles(),
                        unIdentifiedList.get(position).getTotalKm(),
                        unIdentifiedList.get(position).getStartLocationKm(),
                        unIdentifiedList.get(position).getEndLocationKm()

                );
            }
        });


        return convertView;
    }


    int getSelectedCount(){
        int count = 0;
        for(int i = 0 ; i < SelectedRecordList.size() ; i++){
            if(SelectedRecordList.get(i).equals("selected")){
                count++;
            }
        }
        return count;
    }


    private void MoveFragment(
            String DriverStatusId,
            String UnAssignedVehicleMilesId,
            String AssignedUnidentifiedRecordsId,
            boolean IsCompanyAssigned,

            String StartOdometer, String EndOdometer,
            String StartDateTime, String EndDateTime,
            String StartLocation, String EndLocation,
            String TotalMiles, String TotalKM,
            String StartLocationkm, String EndLocationKm
    ){
        UnidentifiedRecordDetailFragment detailFragment = new UnidentifiedRecordDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsKeys.DriverId, DriverId);
        bundle.putString(ConstantsKeys.UserName , DriverName);

        bundle.putString(ConstantsKeys.DriverStatusId , DriverStatusId);
        bundle.putString(ConstantsKeys.UnAssignedVehicleMilesId, UnAssignedVehicleMilesId);
        bundle.putString(ConstantsKeys.AssignedUnidentifiedRecordsId, AssignedUnidentifiedRecordsId);
        bundle.putBoolean(ConstantsKeys.CompanyAssigned, IsCompanyAssigned);

        bundle.putString(ConstantsKeys.StartOdometer, StartOdometer);
        bundle.putString(ConstantsKeys.EndOdometer, EndOdometer);
        bundle.putString(ConstantsKeys.StartDateTime, StartDateTime);
        bundle.putString(ConstantsKeys.EndDateTime, EndDateTime);
        bundle.putString(ConstantsKeys.StartLocation, StartLocation);
        bundle.putString(ConstantsKeys.EndLocation, EndLocation);
        bundle.putString(ConstantsKeys.TotalMiles, TotalMiles);
        bundle.putString(ConstantsKeys.TotalKM, TotalKM);
        bundle.putString(ConstantsKeys.StartLocationKM, StartLocationkm);
        bundle.putString(ConstantsKeys.EndLocationKM, EndLocationKm);

        detailFragment.setArguments(bundle);

        FragmentManager fragManager = fragment.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,
                android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, detailFragment);
        fragmentTran.addToBackStack("unidentified");
        fragmentTran.commit();


    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    public class ViewHolder {
        TextView unIdenDistanceTV, unIdenRecordDescTV, requestedByTxtVw, unIdenRecorTimeTV, unIdenRecordStatusTV;
        CheckBox checkboxUnIdentifiedRecord;
        RelativeLayout unIdentifiedDtailView;

    }

}