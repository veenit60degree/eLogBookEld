package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.models.CanadaDutyStatusModel;

import java.util.List;


public class UnidentifiedLogInOutAdapter extends RecyclerView.Adapter<UnidentifiedLogInOutAdapter.CustomViewHolder> {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public UnidentifiedLogInOutAdapter(Context c, List<CanadaDutyStatusModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_login_logout_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.logInOutDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.trailer_dialog_background));
        }else{
            holder.logInOutDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
        }

        String EventDateTime  = itemsList.get(position).getDateTimeWithMins();
        String CertifyLogDate = itemsList.get(position).getCertifyLogDate();

        try {
            if(itemsList.get(position).IsNewDate()) {
                showDateView(CertifyLogDate, CertifyLogDate, holder.dateTimeDiffLogInTV, true);
            }

         /*   if (position == 0) {
                showDateView(EventDateTime, EventDateTime, holder.dateTimeDiffLogInTV, true);
               // holder.dateTimeDiffLogInTV.setVisibility(View.VISIBLE);
               // holder.dateTimeDiffLogInTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime));
            } else {
                if(itemsList.get(position).IsNewDate()) {
                    showDateView(itemsList.get(position - 1).getDateTimeWithMins(), EventDateTime,
                            holder.dateTimeDiffLogInTV, false);
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }


        if(EventDateTime.length() >= 19) {
            holder.dateDotTV.setText(EventDateTime.substring(11, 19));
        }


        //  holder.dateDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy) );
        holder.eventLoginDotTV.setText(constants.getLoginLogoutEventName(
                itemsList.get(position).getEventType(),
                itemsList.get(position).getEventCode() ));
        holder.addInfoDotTV.setText(constants.checkNullString(itemsList.get(position).getAdditionalInfo()));

        holder.cmvLoginDotTV.setText(constants.checkNullString(itemsList.get(position).getTruckEquipmentNo()));
        holder.diatanceTotalLoginDotTV.setText(constants.checkNullString(itemsList.get(position).getTotalVehicleKM()));
        holder.hrsTotalLoginDotTV.setText(constants.checkNullString(itemsList.get(position).getTotalEngineHours()));

        holder.recStatusLoginDotTV.setText(""+itemsList.get(position).getRecordStatus());
        holder.recOriginLoginDotTV.setText(itemsList.get(position).getRecordOrigin());
        holder.seqNoDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());


        // Set text style normal
        constants.setTextStyleNormal(holder.dateDotTV);
        constants.setTextStyleNormal(holder.eventLoginDotTV);
        constants.setTextStyleNormal(holder.addInfoDotTV);

        constants.setTextStyleNormal(holder.cmvLoginDotTV);
        constants.setTextStyleNormal(holder.diatanceTotalLoginDotTV);
        constants.setTextStyleNormal(holder.hrsTotalLoginDotTV);

        constants.setTextStyleNormal(holder.recStatusLoginDotTV);
        constants.setTextStyleNormal(holder.recOriginLoginDotTV);
        constants.setTextStyleNormal(holder.seqNoDotTV);

        // set Marque on view
        constants.setMarqueonView(holder.dateDotTV);
        constants.setMarqueonView(holder.eventLoginDotTV);
        constants.setMarqueonView(holder.addInfoDotTV);
        constants.setMarqueonView(holder.cmvLoginDotTV);
        constants.setMarqueonView(holder.diatanceTotalLoginDotTV);
        constants.setMarqueonView(holder.hrsTotalLoginDotTV);
        constants.setMarqueonView(holder.recStatusLoginDotTV);
        constants.setMarqueonView(holder.recOriginLoginDotTV);
        constants.setMarqueonView(holder.seqNoDotTV);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private void showDateView(String lastItemDate, String EventDateTime, TextView view, boolean isDirectVisibile){
        if(isDirectVisibile){
            view.setVisibility(View.VISIBLE);
            view.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));
            /*if(EventDateTime.length() >= 19) {
                view.setText(EventDateTime.substring(11, 19));
            }*/
        }else{
            int dayDiff = constants.getDayDiff(lastItemDate, EventDateTime);
            if (dayDiff != 0) {
                view.setVisibility(View.VISIBLE);
                view.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));
                /*if(EventDateTime.length() >= 19) {
                    view.setText(EventDateTime.substring(11, 19));
                }*/
            }
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView dateDotTV, eventLoginDotTV, addInfoDotTV, cmvLoginDotTV, diatanceTotalLoginDotTV, hrsTotalLoginDotTV, recStatusLoginDotTV, recOriginLoginDotTV, seqNoDotTV  ;
        TextView dateTimeDiffLogInTV;
        LinearLayout logInOutDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

            logInOutDotLay           = (LinearLayout) itemView.findViewById(R.id.logInOutDotLay);

            dateTimeDiffLogInTV      = (TextView)itemView.findViewById(R.id.dateTimeDiffLogInTV);

            dateDotTV                = (TextView) itemView.findViewById(R.id.dateDotTV);
            eventLoginDotTV          = (TextView) itemView.findViewById(R.id.eventLoginDotTV);
            addInfoDotTV             = (TextView) itemView.findViewById(R.id.addInfoDotTV);

            cmvLoginDotTV            = (TextView) itemView.findViewById(R.id.cmvLoginDotTV);
            diatanceTotalLoginDotTV  = (TextView) itemView.findViewById(R.id.diatanceTotalLoginDotTV);
            hrsTotalLoginDotTV       = (TextView) itemView.findViewById(R.id.hrsTotalLoginDotTV);

            recStatusLoginDotTV      = (TextView) itemView.findViewById(R.id.recStatusLoginDotTV);
            recOriginLoginDotTV      = (TextView) itemView.findViewById(R.id.recOriginLoginDotTV);
            seqNoDotTV               = (TextView) itemView.findViewById(R.id.seqNoDotTV);



        }
    }

}
