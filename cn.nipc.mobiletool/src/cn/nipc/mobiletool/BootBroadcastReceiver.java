/**
 * 功能	->		初始化开机需要启动服务或者功能
 * 作者	->		谢健
 * 时间	->		2013-8-6 下午5:22:07
 * 描述	->		初始化开机需要启动的服务
 * 名称	->		BootBroadcastReceiver.java
 */
package cn.nipc.mobiletool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitorService;

/**
 * 类名	->		BootBroadcastReceiver
 * 作者 	->		谢健
 * 时间 	->		2013-8-6 下午5:22:07
 * 描述	->		初始化开机需要启动服务
 * 标签	->		开机启动
 */
public class BootBroadcastReceiver extends BroadcastReceiver{

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentNetwork = new Intent(context, BootInitialService.class);
		context.startService(intentNetwork);
	}
	
}
