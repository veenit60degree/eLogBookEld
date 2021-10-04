package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.CanadaDutyStatusModel;
import com.models.PrePostModel;

import java.util.List;

public class CanDotLogInOutAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotLogInOutAdapter(Context c, List<CanadaDutyStatusModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();

    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return itemsList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_login_logout_dot, null);

            holder.logInOutDotLay           = (LinearLayout) convertView.findViewById(R.id.logInOutDotLay);

            holder.dateTimeDiffLogInTV      = (TextView)convertView.findViewById(R.id.dateTimeDiffLogInTV);

            holder.dateDotTV                = (TextView) convertView.findViewById(R.id.dateDotTV);
            holder.eventLoginDotTV          = (TextView) convertView.findViewById(R.id.eventLoginDotTV);
            holder.addInfoDotTV             = (TextView) convertView.findViewById(R.id.addInfoDotTV);

            holder.cmvLoginDotTV            = (TextView) convertView.findViewById(R.id.cmvLoginDotTV);
            holder.diatanceTotalLoginDotTV  = (TextView) convertView.findViewById(R.id.diatanceTotalLoginDotTV);
            holder.hrsTotalLoginDotTV       = (TextView) convertView.findViewById(R.id.hrsTotalLoginDotTV);

            holder.recStatusLoginDotTV      = (TextView) convertView.findViewById(R.id.recStatusLoginDotTV);
            holder.recOriginLoginDotTV      = (TextView) convertView.findViewById(R.id.recOriginLoginDotTV);
            holder.seqNoDotTV               = (TextView) convertView.findViewById(R.id.seqNoDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.logInOutDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

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

       /* if(EventDateTime.length() > 16) {
            holder.dateDotTV.setText(EventDateTime.substring(11, 16));
        }*/


        if(EventDateTime.length() >= 19) {
            holder.dateDotTV.setText(EventDateTime.substring(11, 19));
        }


        //holder.dateDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getDateTimeWithMins()) );
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


        return convertView;
    }


    private void showDateView(String lastItemDate, String EventDateTime, TextView view, boolean isDirectVisibile){
        if(isDirectVisibile){
            view.setVisibility(View.VISIBLE);
            //view.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime));
            if(EventDateTime.length() >= 19) {
                view.setText(EventDateTime.substring(11, 19));
            }
        }else{
            int dayDiff = constants.getDayDiff(lastItemDate, EventDateTime);
            if (dayDiff != 0) {
                view.setVisibility(View.VISIBLE);
               // view.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime));
                if(EventDateTime.length() >= 19) {
                    view.setText(EventDateTime.substring(11, 19));
                }
            }
        }
    }


    public class ViewHolder {
        TextView dateDotTV, eventLoginDotTV, addInfoDotTV, cmvLoginDotTV, diatanceTotalLoginDotTV, hrsTotalLoginDotTV, recStatusLoginDotTV, recOriginLoginDotTV, seqNoDotTV  ;
        TextView dateTimeDiffLogInTV;
        LinearLayout logInOutDotLay;

    }

}