package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.als.logistic.R;
import com.models.HelpDocModel;

import java.util.List;

public class HelpDocAdapter  extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<HelpDocModel> helpDocList;

    public HelpDocAdapter(Context context, List<HelpDocModel> transferList){
        this.context = context;
        this.helpDocList = transferList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return helpDocList.size();
    }

    @Override
    public Object getItem(int position) {
        return helpDocList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_help_doc, null);

            holder.helpDocTitleTV = (TextView)convertView.findViewById(R.id.helpDocTitleTV);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String fileName = helpDocList.get(position).getDocumentTitle().replaceAll(".pdf", "");
        holder.helpDocTitleTV.setText(fileName);

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
        TextView helpDocTitleTV;

    }

}
