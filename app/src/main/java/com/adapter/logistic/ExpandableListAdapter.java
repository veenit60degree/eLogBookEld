package com.adapter.logistic;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.messaging.logistic.R;
import com.models.TripHistoryModel;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<TripHistoryModel>> _listDataChild;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<TripHistoryModel>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final TripHistoryModel childData = _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.item_child_trip_history, null);
		}

		TextView tripHisDetailTV = (TextView) convertView.findViewById(R.id.tripHisDetailTV);
		TextView tripHistoryNumberTV = (TextView) convertView.findViewById(R.id.tripHistoryNumberTV);

		String title = "LOAD# " + childData.getLoadNumber();
		String desc = childData.getShipperAddress()+ "," +  childData.getShipperStateCode() + "," + childData.getShipperCountryCode();


		tripHistoryNumberTV.setText(title);
		tripHisDetailTV.setText(desc);



		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(_listDataChild.get(this._listDataHeader.get(groupPosition)) != null){
			return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
		}else{
			return  0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
