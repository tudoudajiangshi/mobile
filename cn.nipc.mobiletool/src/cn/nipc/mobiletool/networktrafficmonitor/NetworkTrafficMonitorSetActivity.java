package cn.nipc.mobiletool.networktrafficmonitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import cn.nipc.mobiletool.R;

public class NetworkTrafficMonitorSetActivity extends Activity{
	//public static Context context = 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.network_traffic_set);
		//this.getListView().setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		//initBackbutton();
		setContentView(R.layout.monitor_set);
		getFragmentManager().beginTransaction().replace(R.id.monitor_set_content,
	                new PF()).commit();		
	}
	
	/**
	 * 类名	->		PF
	 * 作者	->		谢健
	 * 时间	->		2013-11-27 下午2:11:26
	 * 描述	->		设置列表
	 * 标签	->		TODO
	 */
	public static class PF extends PreferenceFragment{
		
		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.net_traffic_set);
            
            OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener(){

				@Override
				public void onSharedPreferenceChanged(
						SharedPreferences sharedPreferences, String key) {
					Log.e("xxxxx", "qidongle ~~");
					if (key.equals("open_float_window")) {
			            Toast.makeText(getActivity().getApplicationContext(), "打开了悬浮窗", Toast.LENGTH_SHORT).show();
			        }		
				}
            	
            };
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }
		
	}
	
	public static class NetworkFloatWindow extends PreferenceFragment{
		
		@Override 
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			
			//Load the XML
			addPreferencesFromResource(R.xml.network_float_window);
		}
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
