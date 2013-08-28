package cn.nipc.mobiletool.networktrafficmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 类名	->		NetworkTrafficMonitorService
 * 作者 	->		谢健
 * 时间 	->		2013-8-6 上午11:11:39
 * 描述	->		应用启动一个服务，一直监听各个应用的网络的流量,并实时显示当前的网速
 * 标签	->		监听网络流量
 */
public class NetworkTrafficMonitorService extends Service{

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
