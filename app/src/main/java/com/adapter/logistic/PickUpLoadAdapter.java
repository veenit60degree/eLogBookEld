package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messaging.logistic.R;
import com.models.JobGetSet;

import java.util.List;

public class PickUpLoadAdapter extends BaseAdapter {

	Context context;
	LayoutInflater mInflater;
	LayoutInflater inflater;
	List<JobGetSet> transferList;
	
	public PickUpLoadAdapter(Context context, List<JobGetSet> transferList){
		this.context = context;
		this.transferList = transferList;
		mInflater = LayoutInflater.from(context);
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
		final ViewHolder holder ;
		final JobGetSet transferItem = (JobGetSet) getItem(position);
		
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.items_jobs, null);
			
			holder.jobTextView = (TextView)convertView.findViewById(R.id.jobTextView);
			holder.jobAddressTextView = (TextView)convertView.findViewById(R.id.jobAddressTextView);
			holder.tickImageView = (ImageView)convertView.findViewById(R.id.tickImageView);


			holder.jobTextView.setText(transferItem.getName()+" (LOAD# " + transferItem.getLoadNumber()+ ")");
			holder.jobAddressTextView.setText(transferItem.getAddress()+", " + transferItem.getStateCode()+", " +
					transferItem.getCountryCode());


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
		ImageView tickImageView;
	}
	

}
