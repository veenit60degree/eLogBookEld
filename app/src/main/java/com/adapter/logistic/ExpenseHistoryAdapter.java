package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messaging.logistic.R;

import java.util.ArrayList;

/**
 * Created by kumar on 1/6/2017.
 */

public class ExpenseHistoryAdapter extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
   ArrayList<String> transferList;


    public ExpenseHistoryAdapter(Context context, ArrayList<String> transferList ) {
        this.context = context;
        this.transferList = transferList;
        mInflater = LayoutInflater.from(context);
        //  this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return transferList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return transferList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_expense_history, null);

            holder.tripExpenseTV = (TextView) convertView.findViewById(R.id.tripExpenseTV);
            holder.tripExpenseTV.setText(transferList.get(position));

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
        TextView tripExpenseTV;

    }

}
