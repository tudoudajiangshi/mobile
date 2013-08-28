/**
 * 功能	->		开启一个服务来完成关机之前需要执行的操作
 * 作者	->		谢健
 * 时间	->		2013-8-27 下午5:48:21
 * 描述	->		关机需要执行的操作（未完）
 * 名称	->		ShutdownService.java
 */
package cn.nipc.mobiletool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitor;

/**
 * 类名	->		ShutdownService
 * 作者 	->		谢健
 * 时间 	->		2013-8-27 下午5:48:21
 * 描述	->		具体的操作有：1.关机前最后查询下本日所用的流量 
 * 标签	->		关机操作
 */
public class ShutdownService extends Service{
	
	public String TAG = "ShutdownService";

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "服务已经启动");
		NetworkTrafficMonitor.getTodayNetTraffic(this);
		//终止本服务
		this.stopSelf();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG,"服务已关闭");
	}

}
