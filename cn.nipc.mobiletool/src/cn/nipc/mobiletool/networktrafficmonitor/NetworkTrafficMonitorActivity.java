package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.nipc.mobiletool.R;

/**
 * 类名	->		NetworkTrafficMonitorActivity
 * 作者 	->		谢健
 * 时间 	->		2013-8-7 下午1:22:26
 * 描述	->		流量监控模块主界面
 * 标签	->		流量监控
 */
public class NetworkTrafficMonitorActivity extends FragmentActivity {
	
	public String TAG = "NetworkTrafficMonitorActivity";
	private ViewPager viewPager;
	/** 页面list **/
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    public Context context = this;
	
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
		initBackbutton();
		initSetbutton();
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
	 * 描述		->		初始化ViewPager，共分三个页面 1.流量监控 2.联网防火墙 (第二个由于权限不够 悲剧了-。-)3.统计排行
	 * 参数		->		无
	 * 返回值		->		void
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		//listView  = new ArrayList<View>();
		Fragment fNetTrafficMonitor = new NetTrafficFragment();
		Fragment fNetTrafficRank = new NetRankFragment();
		
		fragmentList.add(fNetTrafficMonitor);
		fragmentList.add(fNetTrafficRank);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){

			@Override
			public Fragment getItem(int arg0) {
				return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
			}

			@Override
			public int getCount() {
				return fragmentList == null ? 0 : fragmentList.size();
			}
			
		});
		viewPager.setCurrentItem(0);
		viewPager.setOffscreenPageLimit(0);
		viewPager.setPageMargin(20);
		//viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	/**
	 * 函数名		->		initBackbutton
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		设置返回键
	 * 返回值		-> 	void
	 * 时间		->	 	2013-11-7 上午11:12:10 
	*/
	private void initBackbutton(){
		ImageView iv = (ImageView)findViewById(R.id.net_monitor_back);
		iv.setClickable(true);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//结束这个activity
				NetworkTrafficMonitorActivity.this.finish();
			}
		});
	}
	
	/**
	 * 函数名		->		initSetbutton
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		流量监控的设置页面
	 * 返回值		-> 	void
	 * 时间		->	 	2013-11-7 上午11:13:41 
	*/
	private void initSetbutton(){
		ImageView iv = (ImageView)findViewById(R.id.net_monitor_set);
		iv.setClickable(true);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//跳转至设置页面
				Intent intent = new Intent(); 
				intent.setClass(NetworkTrafficMonitorActivity.this, NetworkTrafficMonitorSetActivity.class);
				startActivityForResult(intent, 0);
				
			}
		});
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 0){
			switch (resultCode) {
			case RESULT_OK:
				Log.e(TAG, "setting page back success!!!");
				break;

			default:
				break;
			}
		}
		
	}

}
