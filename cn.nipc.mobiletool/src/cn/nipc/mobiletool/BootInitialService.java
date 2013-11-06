/**
 * 功能	->		开机初始化服务，由于开机需要初始化一些东西或者做一些判断，而receiver的生命周期
 * 				太短，所以需要开启一个service来完成相关操作。
 * 作者	->		谢健
 * 时间	->		2013-8-7 上午9:42:42
 * 描述	->		开启一个服务，完成1.流量的初始化查询（更新last_query,否则会出现负数）
 * 名称	->		BootInitialService.java
 */
package cn.nipc.mobiletool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitor;

/**
 * 类名	->		BootInitialService
 * 作者 	->		谢健
 * 时间 	->		2013-8-7 上午9:42:42
 * 描述	->		开启服务,首先完成流量查询初始化。
 * 标签	->		
 */
public class BootInitialService extends Service{
	
	public String TAG = "BootInitialService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG,"服务已开启");
		//流量监控相关 1.重启后上次查询数据清零 2.设置定时更新数据库
		NetworkTrafficMonitor.initialForBootDoNetworkTrafficQuery(this);
		NetworkTrafficMonitor.initialSetTimingQuery(this);
		//终止本服务
		this.stopSelf();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG,"服务已关闭");
	}
}
