package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.nipc.mobiletool.MainActivityPagerAdapter;
import cn.nipc.mobiletool.R;

/**
 * 类名	->		NetworkTrafficMonitorActivity
 * 作者 	->		谢健
 * 时间 	->		2013-8-7 下午1:22:26
 * 描述	->		流量监控模块主界面
 * 标签	->		流量监控
 */
public class NetworkTrafficMonitorActivity extends Activity {
	
	public String TAG = "NetworkTrafficMonitorActivity";
	private ViewPager viewPager;
	
	private ArrayList<View> listView;
	public int last_query = 32; //一个月最多31天 ，数据库中第32天存放的上次查询所获得的流量值

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_traffic_monitor);
		SharedPreferences sp = this.getSharedPreferences("traffic_monitor_preference", MODE_PRIVATE);
		Editor spEditor = sp.edit();
		boolean isFirstRun =  sp.getBoolean("isFirstRun", true);
		if(isFirstRun){
			spEditor.putBoolean("isFirstRun", false);  
			spEditor.commit();  
		}
		initViewPager();
		//测试一些函数
		/*List<AppTrafficInfo> appInfoList = NetworkTrafficMonitor.getMonthNetTrafficPerApp(this);
		for (AppTrafficInfo appTrafficInfo : appInfoList){
			Log.e(TAG, appTrafficInfo.appName+"**"+appTrafficInfo.label);
		}*/
		//NetworkTrafficMonitor.updateMonthNetTrafficPerApp(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.network_traffic_monitor, menu);
		return true;
	}
	
	/**
	 * 函数名		->		initViewPager
	 * 作者		->		谢健
	 * 时间		->		2013-8-7 下午1:46:19
	 * 描述		->		初始化ViewPager，共分三个页面 1.流量监控 2.联网防火墙 3.统计排行
	 * 参数		->		无
	 * 返回值		->		void
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		listView  = new ArrayList<View>();
		View view0= getLayoutInflater().inflate(R.layout.network_traffic_monitor, null);
		initView0(view0);
		
		View view1= getLayoutInflater().inflate(R.layout.network_traffic_firewall, null);
		
		View view2= getLayoutInflater().inflate(R.layout.network_traffic_rank, null);
		initView2(view2);
		
		listView.add(view0);
		listView.add(view1);
		listView.add(view2);
		viewPager.setAdapter(new MainActivityPagerAdapter(NetworkTrafficMonitorActivity.this, listView));
		viewPager.setCurrentItem(0);
		viewPager.setPageMargin(50);
		//viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	/**
	 * 函数名	->		initView0
	 * 作者		->		谢健
	 * 时间		->		2013-8-7 下午1:50:23
	 * 描述		->		初始化流量监控页面。
	 * 参数		->		view0
	 * 返回值	->		void
	 */
	private void initView0(View view0) {
		//获得今天是本月几号
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int dayNum = cal.get(Calendar.DAY_OF_MONTH);
		
		TextView textViewTodayUsage = (TextView)view0.findViewById(R.id.today_usage);
		textViewTodayUsage.setText(""+NetworkTrafficMonitor.getTodayNetTraffic(this)/1024 + "Kb");
		
		TextView textViewMonthUsage = (TextView)view0.findViewById(R.id.month_usage);
		textViewMonthUsage.setText(""+NetworkTrafficMonitor.getMonthNetTraffic(this)/1024/1024 + "Mb");
		
		Button buttonSetMonth = (Button)view0.findViewById(R.id.set_month);
		
	}
	
	/**
	 * 函数名		->		initView2
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	View
	 * 返回值		-> 	void
	 * 时间		->	 	2013-10-17 上午11:05:25 
	*/
	private void initView2(View view2) {
		ListView listview2 =  (ListView) view2.findViewById(R.id.listview_rank);
		final List<AppTrafficInfo> list = NetworkTrafficMonitor.getMonthNetTrafficPerApp(this);
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
				View item = getLayoutInflater().inflate(R.layout.network_traffic_rank_item, null);
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
	}
	
	private String format(Double a){
		String formatString;
		if(a/1024/1024 < 0.5){
			formatString = String.format("%5.2fKB", a/1024);
		}
		else {
			formatString = String.format("%5.2fMB", a/1024/1024);
		}
		return formatString ;
	}

}
