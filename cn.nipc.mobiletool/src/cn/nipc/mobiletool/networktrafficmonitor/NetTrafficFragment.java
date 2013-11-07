package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.nipc.mobiletool.R;

public class NetTrafficFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view0= inflater.inflate(R.layout.network_traffic_monitor, null);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		Context context = getActivity().getApplicationContext();
		
		TextView textViewTodayUsage = (TextView)view0.findViewById(R.id.today_usage);
		textViewTodayUsage.setText(""+NetworkTrafficMonitor.getTodayNetTraffic(context)/1024 + "Kb");
		
		TextView textViewMonthUsage = (TextView)view0.findViewById(R.id.month_usage);
		textViewMonthUsage.setText(""+NetworkTrafficMonitor.getMonthNetTraffic(context)/1024/1024 + "Mb");
		
		Button buttonSetMonth = (Button)view0.findViewById(R.id.set_month);
		return view0;
	}
}
