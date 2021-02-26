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



        holder.logInOutDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.dateDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getEventDate()) );
        holder.eventLoginDotTV.setText(""+itemsList.get(position).getEventCode());
        holder.addInfoDotTV.setText(itemsList.get(position).getAdditionalInfo());

        holder.cmvLoginDotTV.setText(itemsList.get(position).getCMVVIN());
        holder.diatanceTotalLoginDotTV.setText(itemsList.get(position).getDistanceInKM());
        holder.hrsTotalLoginDotTV.setText(itemsList.get(position).getTotalEngineHours());

        holder.recStatusLoginDotTV.setText(""+itemsList.get(position).getRecordStatus());
        holder.recOriginLoginDotTV.setText(itemsList.get(position).getRecordOrigin());
        holder.seqNoDotTV.setText(""+itemsList.get(position).getSequenceNumber());


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

        return convertView;
    }


    public class ViewHolder {
        TextView dateDotTV, eventLoginDotTV, addInfoDotTV, cmvLoginDotTV, diatanceTotalLoginDotTV, hrsTotalLoginDotTV, recStatusLoginDotTV, recOriginLoginDotTV, seqNoDotTV  ;
        LinearLayout logInOutDotLay;

    }

}