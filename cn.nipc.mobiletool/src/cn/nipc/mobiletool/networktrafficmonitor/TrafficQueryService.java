/**
 * 功能	->		定时查询的service 实现每天的晚上23：55分左右查询下当天所使用流量
 * 				并记录到数据库中
 * 作者	->		谢健
 * 时间	->		2013-8-27 下午5:16:53
 * 描述	->		定时查询流量，保证尽量精确的记录每天的流量
 * 名称	->		TimingQueryService.java
 */
package cn.nipc.mobiletool.networktrafficmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 类名	->		TimingQueryService
 * 作者 	->		谢健
 * 时间 	->		2013-8-27 下午5:16:53
 * 描述	->		查询流量并记录的数据库中的service
 * 标签	->		定时查询
 */
public class TrafficQueryService extends Service{
	
	public static String TAG = "TimingQueryService";

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"定时查询服务已启动");
		NetworkTrafficMonitor.getTodayNetTraffic(this);
		NetworkTrafficMonitor.updateMonthNetTrafficPerApp(this);
		//终止本服务
		this.stopSelf();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG,"定时查询服务已关闭");
	}

}
