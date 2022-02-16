package com.adapter.logistic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.constants.Constants;
import com.models.SupportModel;
import com.messaging.logistic.R;

import java.net.URLEncoder;
import java.util.List;

public class SupportAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    List<SupportModel> supportList;

    /* ----Support Constants---- */
    final int SupportPhone      = 1;
    final int SupportEmail      = 2;
    final int SupportWhatsApp   = 3;


    public SupportAdapter(Context c, List<SupportModel> supportLst) {
        mContext        = c;
        supportList     = supportLst;
        mInflater       = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return supportList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return supportList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder ;
        SupportModel supportModel = supportList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_support, null);

            holder.supportTypeTV        = (TextView)convertView.findViewById(R.id.supportTypeTV);
            holder.supportTypeValueTV   = (TextView)convertView.findViewById(R.id.supportTypeValueTV);

            holder.supportTypeIV        = (ImageView) convertView.findViewById(R.id.supportTypeIV);
            holder.supportLayout        = (RelativeLayout)convertView.findViewById(R.id.supportLayout);

            final int KeyType = supportModel.getKeyType();
            holder.supportTypeValueTV.setText(supportModel.getValue());

            // Load View according to Key Type
            ShowSupportView(KeyType, holder.supportTypeIV, holder.supportTypeTV);



            holder.supportLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickEvent(KeyType, holder.supportTypeValueTV.getText().toString());
                }
            });

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }


    public class ViewHolder {
        TextView supportTypeTV, supportTypeValueTV;
        ImageView supportTypeIV;
        RelativeLayout supportLayout;
    }


    private void ShowSupportView(int type, ImageView imageView, TextView titleTV){
        switch (type){

            case SupportPhone:
                titleTV.setText("Phone: ");
                imageView.setImageResource(R.drawable.phone);
                break;

            case SupportEmail:
                titleTV.setText("Email: ");
                imageView.setImageResource(R.drawable.email);
                break;

            case SupportWhatsApp:
                titleTV.setText("WhatsApp: ");
                imageView.setImageResource(R.drawable.whatsapp);
                break;

        }
    }


    private void ClickEvent(int Type, String value){
        switch (Type){
            case SupportPhone:

                // Call dialer intent
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", value, null));
                mContext.startActivity(intent);

                break;

            case SupportEmail:

                // Call email intent

                Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
                intentEmail.setData(Uri.parse("mailto:")); // only email apps should handle this
                // intentEmail.setType("*/*");
                intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {value});
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "ALS Support");

                if (intentEmail.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intentEmail);
                }else{
                    Constants.copyTextToClipboard(mContext, value);
                    Toast.makeText(mContext, "Email copied.", Toast.LENGTH_LONG).show();
                }



                break;

            case SupportWhatsApp:

                // call Share intent
                PackageManager packageManager = mContext.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+ value +"&text=" + URLEncoder.encode("ALS Support", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        mContext.startActivity(i);
                    }else{
                        Toast.makeText(mContext, "WhatsApp is not found on your device.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                break;

        }


    }


}