package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.Collections;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.nipc.mobiletool.R;

public class NetRankFragment extends Fragment{
	
	public static String TAG = "NetRankFragment";
 	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view2= inflater.inflate(R.layout.network_traffic_rank, null);
		ListView listview2 =  (ListView) view2.findViewById(R.id.listview_rank);
		List<AppTrafficInfo> unsequenceList= NetworkTrafficMonitor.getMonthNetTrafficPerApp(getActivity().getApplicationContext());
		//去除流量为0的项
		deleteZeroApp(unsequenceList);
		//按流量大小排序
		Collections.sort(unsequenceList);
		PackageManager pm = getActivity().getPackageManager();
		try{
			for (AppTrafficInfo appTrafficInfo : unsequenceList){
				PackageInfo    pi = pm.getPackageInfo(appTrafficInfo.appName, 0);
				appTrafficInfo.appIcon = pm.getApplicationIcon(pi.applicationInfo);
				appTrafficInfo.label = (String)pi.applicationInfo.loadLabel(pm);
			}
		}catch (Exception e) {
			Log.e(TAG, e.getMessage()+"找不到这个包名的app具体信息");
		}
		final List<AppTrafficInfo> list =unsequenceList;
		ListAdapter listAdapter = new ListAdapter() {			
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEmpty() {
				return list.isEmpty();
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public int getViewTypeCount() {
				// TODO Auto-generated method stub
				return 1;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View item = getLayoutInflater(null).inflate(R.layout.network_traffic_rank_item, null);
				ImageView iv = (ImageView) item.findViewById(R.id.icon);
				TextView tv_label = (TextView) item.findViewById(R.id.label);
				TextView tv_dt = (TextView) item.findViewById(R.id.dt);
				TextView tv_ut = (TextView) item.findViewById(R.id.ut);
				
				iv.setImageDrawable(list.get(position).appIcon);
				tv_label.setText(list.get(position).label);
				tv_dt.setText("下载："+format(list.get(position).downloadTraffic));
				tv_ut.setText("上传："+format(list.get(position).uploadTraffic));
				
				return item;
			}
			
			@Override
			public int getItemViewType(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return list.get(position);
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
			@Override
			public boolean isEnabled(int position) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
		}; 
		listview2.setAdapter(listAdapter);		
		return view2;
	}
	
	/**
	 * 函数名		->		deleteZeroApp
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		删除流量为0的应用
	 * 返回值		-> 	void
	 * 时间		->	 	2013-11-6 下午5:16:23 
	*/
	public static void deleteZeroApp(List<AppTrafficInfo> appList ){
		for(int i = 0; i < appList.size(); i++){
			if(appList.get(i).downloadTraffic + appList.get(i).uploadTraffic <= 0){
				appList.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 函数名		->		format
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		规范流量字符串
	 * 返回值		-> 	String
	 * 时间		->	 	2013-10-25 下午2:47:06 
	*/
	private String format(Double a){
		String formatString;
		if(a/1024/1024 < 0.5){
			;
			formatString = String.format("%5.2fKB", a/1024);
		}
		else {
			formatString = String.format("%5.2fMB", a/1024/1024);
		}
		return formatString ;
	}
}
