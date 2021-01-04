package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.messaging.logistic.R;
import com.models.MalfunctionModel;

import java.util.HashMap;
import java.util.List;

public class MalfunctionDataAdapter extends BaseAdapter {

    Context context;
    List<MalfunctionModel> malfunctionList;
    LayoutInflater mInflater;



    public MalfunctionDataAdapter(Context context, List<MalfunctionModel> listData) {
        this.context = context;
        this.malfunctionList = listData;
        mInflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return malfunctionList.size();
    }

    @Override
    public Object getItem(int i)  {

        return malfunctionList.get(i);
    }



    @Override
    public long getItemId(int i) {
        return i;   //i
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;
        MalfunctionModel malfunctionModel = malfunctionList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_malfunctions, null);

            holder.malfDefTxtView = (TextView) convertView.findViewById(R.id.malfDefTxtView);
            holder.clearMalBtn = (TextView) convertView.findViewById(R.id.clearMalBtn);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.malfDefTxtView.setText(malfunctionModel.getMalfunctionDefinition());

        holder.clearMalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;

    }


    public class ViewHolder {
        TextView malfDefTxtView, clearMalBtn;


    }



}
