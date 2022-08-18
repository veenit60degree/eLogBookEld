package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.CanadaDutyStatusModel;

import java.util.List;

public class UnidentifiedRemarksAdapter extends RecyclerView.Adapter<UnidentifiedRemarksAdapter.CustomViewHolder> {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public UnidentifiedRemarksAdapter(Context c, List<CanadaDutyStatusModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();

    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_remarks_ano_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.remarksAnoDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.layout_color_dot));
        }else{
            holder.remarksAnoDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
        }

        String eventTime = itemsList.get(position).getDateTimeWithMins();
        holder.dateRemarksDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(eventTime, Globally.DateFormat_mm_dd_yy));

        if(eventTime.length() > 16) {
            holder.timeDotTV.setText(eventTime.substring(11, 16));
        }

        if(eventTime.length() >= 19) {
            holder.timeDotTV.setText(eventTime.substring(11, 19));
        }

        //holder.timeDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getDateTimeWithMins()));

        holder.usernameDotTV.setText(constants.checkNullString(itemsList.get(position).getUserName()));

        holder.sqNoRemDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());
        holder.commAnotnDotTV.setText(constants.checkNullString(itemsList.get(position).getRemarks()));

        String editTime = itemsList.get(position).getEditDateTime();
        holder.editDateCmntDotTV.setText(Globally.ConvertDateFormatMMddyy(editTime));
        if(editTime.length() > 11){
            holder.editTimeCmntDotTV.setText(editTime.substring(11, editTime.length()));
        }
        // Set text style normal
        constants.setTextStyleNormal(holder.dateRemarksDotTV);
        constants.setTextStyleNormal(holder.timeDotTV);
        constants.setTextStyleNormal(holder.usernameDotTV);

        constants.setTextStyleNormal(holder.sqNoRemDotTV);
        constants.setTextStyleNormal(holder.commAnotnDotTV);
        constants.setTextStyleNormal(holder.editDateCmntDotTV);
        constants.setTextStyleNormal(holder.editTimeCmntDotTV);


        // set Marque on view
        constants.setMarqueonView(holder.dateRemarksDotTV);
        constants.setMarqueonView(holder.timeDotTV);
        constants.setMarqueonView(holder.usernameDotTV);
        constants.setMarqueonView(holder.sqNoRemDotTV);
        constants.setMarqueonView(holder.commAnotnDotTV);
        constants.setMarqueonView(holder.editDateCmntDotTV);
        constants.setMarqueonView(holder.editTimeCmntDotTV);

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView dateRemarksDotTV, timeDotTV, usernameDotTV, sqNoRemDotTV, commAnotnDotTV  ;
        TextView editDateCmntDotTV, editTimeCmntDotTV;
        LinearLayout remarksAnoDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

            remarksAnoDotLay     = (LinearLayout) itemView.findViewById(R.id.remarksAnoDotLay);

            dateRemarksDotTV     = (TextView) itemView.findViewById(R.id.dateRemarksDotTV);
            timeDotTV            = (TextView) itemView.findViewById(R.id.timeDotTV);
            usernameDotTV        = (TextView) itemView.findViewById(R.id.usernameDotTV);
            sqNoRemDotTV         = (TextView) itemView.findViewById(R.id.sqNoRemDotTV);
            commAnotnDotTV       = (TextView) itemView.findViewById(R.id.commAnotnDotTV);
            editDateCmntDotTV       = (TextView) itemView.findViewById(R.id.editDateCmntDotTV);
            editTimeCmntDotTV       = (TextView) itemView.findViewById(R.id.editTimeCmntDotTV);



        }
    }


}
