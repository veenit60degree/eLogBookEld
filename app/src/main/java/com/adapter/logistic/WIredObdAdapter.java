package com.adapter.logistic;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.constants.Constants;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.R;
import com.models.JobGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WIredObdAdapter  extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    JSONArray transferList;

    public WIredObdAdapter(Context context, JSONArray list){
        this.context = context;
        this.transferList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return transferList.length();
    }

    @Override
    public Object getItem(int arg0)  {
        JSONObject jhj = null;
        try {
            jhj = (JSONObject) transferList.get(arg0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jhj;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final JSONObject obj = (JSONObject) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.items_jobs, null);

            holder.jobTextView = (TextView)convertView.findViewById(R.id.jobTextView);
            holder.jobAddressTextView = (TextView)convertView.findViewById(R.id.jobAddressTextView);

            try {


                String time             = NullCheckJson(obj, Constants.OBD_TimeStamp);
                String vinNumber        = NullCheckJson(obj, Constants.OBD_VINNumber);
                String Odometer         = NullCheckJson(obj, Constants.OBD_Odometer);
                String IgnitionStatus   = NullCheckJson(obj, Constants.OBD_IgnitionStatus);
                String rpm              = NullCheckJson(obj, Constants.OBD_RPM);
                String TripDistance     = NullCheckJson(obj, Constants.OBD_TripDistance);
                String speed            = NullCheckJson(obj, Constants.OBD_Vss);

                double odometerKm = Double.parseDouble(Odometer);
                double odometerMiles = odometerKm * 0.621371;
                int odoKm = (int)odometerKm;
                int odoMiles = (int)odometerMiles;

                //<font color='green'><b>Ignition status: </b></font>
                String timeHtml   = "<br /> <font color='red'><b>Saved time:       </b></font> " + time  ;
                String obdGPS     = "<font color='red'><b>Vin Number:       </b></font> " + vinNumber + "<br />" +
                                    "<font color='red'><b>Odometer:         </b></font> " + odoKm + " km / " + odoMiles  + " miles <br />" +
                                    "<font color='red'><b>RPM:              </b></font> " + rpm  + "<br />" +
                                    "<font color='red'><b>Trip Distance:    </b></font> " + TripDistance  + "<br />" +
                                    "<font color='red'><b>Speed:            </b></font> " + speed + "<br />"  ;

                holder.jobTextView.setText(Html.fromHtml(timeHtml));
                holder.jobAddressTextView.setText(Html.fromHtml(obdGPS));


            }catch (Exception e){
                e.printStackTrace();
            }

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }





        return convertView;
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
        TextView jobTextView, jobAddressTextView;

    }

    private String NullCheckJson(JSONObject json, String key){
        String data = "--";
        try {
            if(!json.isNull(key)) {

                data = json.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


}
