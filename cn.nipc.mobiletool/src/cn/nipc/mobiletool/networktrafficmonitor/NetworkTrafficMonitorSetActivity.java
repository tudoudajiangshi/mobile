package cn.nipc.mobiletool.networktrafficmonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.nipc.mobiletool.R;

public class NetworkTrafficMonitorSetActivity extends PreferenceActivity{
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.net_traffic_set);  
		setContentView(R.layout.network_traffic_set);
		this.getListView().setBackgroundColor(Color.parseColor("#ffffffff"));
		initBackbutton();	
	}
	
	/**
	 * 函数名		->		initBackbutton
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		设置返回键
	 * 返回值		-> 	void
	 * 时间		->	 	2013-11-7 下午3:48:18 
	*/
	private void initBackbutton(){
		ImageView iv = (ImageView)findViewById(R.id.net_monitor_back);
		iv.setClickable(true);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//结束这个activity
				NetworkTrafficMonitorSetActivity.this.finish();
			}
		});
	}
}
