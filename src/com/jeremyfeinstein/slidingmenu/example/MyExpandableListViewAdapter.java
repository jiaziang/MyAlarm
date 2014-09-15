package com.jeremyfeinstein.slidingmenu.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.jiaziang8.alarm.object.Alarm.DaysOfWeek;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> groupDatas;
	private List<List<HashMap<String, String>>> childDatas;
	public List<List<HashMap<Integer, Boolean>>> childCheckBoxState = new ArrayList<List<HashMap<Integer, Boolean>>>();
	private int mDays;
	private static final String TAG="Alarm";

	public MyExpandableListViewAdapter(Context context,
			List<String> groupDatas,
			List<List<HashMap<String, String>>> childDatas) {
		super();
		this.context = context;
		this.groupDatas = groupDatas;
		this.childDatas = childDatas;
		
		InitCheckBoxState();
	}

	// 初始化checkBox状态,默认所有checkBox未选中
	public void InitCheckBoxState() {
		for (int i = 0; i < groupDatas.size(); i++) {
			List<HashMap<Integer, Boolean>> item = new ArrayList<HashMap<Integer, Boolean>>();
			for (int j = 0; j < childDatas.get(i).size(); j++) {
				HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
				map.put(j, false);
				item.add(map);
			}
			childCheckBoxState.add(item);
		}
		mDays = 0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childDatas.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.repeat_child, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.childName);
			holder.box = (CheckBox) convertView
					.findViewById(R.id.childCheckBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(childDatas.get(groupPosition).get(childPosition)
				.get("name").toString());
		holder.box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					childCheckBoxState.get(groupPosition).get(childPosition)
							.put(childPosition, true);
					mDays=caculateMydays(childCheckBoxState);
					setMyDays(mDays);
					//Log.v(TAG, "position:"+childPosition+"check  now mDay is:"+mDays);
				} else {
					childCheckBoxState.get(groupPosition).get(childPosition)
							.put(childPosition, false);
					mDays=caculateMydays(childCheckBoxState);
					setMyDays(mDays);
					//Log.v(TAG, "position:"+childPosition+"uncheck  now mDays is:"+mDays);
				}
			}
		});

		// 按状态初始化checkBox，避免父列表收缩打开checkBox混乱
		holder.box.setChecked(childCheckBoxState.get(groupPosition)
				.get(childPosition).get(childPosition) == false ? false : true);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childDatas.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupDatas.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupDatas.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.repeat_group, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.repeat_text);
			holder.repeat_value = (TextView)convertView.findViewById(R.id.repead_value);

			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DaysOfWeek mdayDaysOfWeek = new DaysOfWeek(mDays);
		String daysofweekString = mdayDaysOfWeek.toString(context, true);
		holder.repeat_value.setText(daysofweekString);
		holder.name.setText(groupDatas.get(groupPosition).toString());
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

	static class ViewHolder {
		TextView name;
		TextView repeat_value;
		CheckBox box;
	}
	
	static int caculateMydays(List<List<HashMap<Integer, Boolean>>> childCheckBoxState){
		int mDays=0;
		for(int i=0;i<7;i++){
			if(childCheckBoxState.get(0).get(i).get(i)==true){
				mDays+=java.lang.Math.pow(2, i);
			}
		}
		return mDays;
	}
	
	public static void setMyDays(int mDays){
		SetAlarmFragment.setmDays(mDays);
	}

}
