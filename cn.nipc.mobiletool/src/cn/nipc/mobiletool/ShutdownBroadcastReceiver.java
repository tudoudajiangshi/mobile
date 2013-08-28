/**
 * 功能	->		接受系统关机时的广播，并启动对应的服务
 * 作者	->		谢健
 * 时间	->		2013-8-27 下午5:43:09
 * 描述	->		接受系统关机时的广播
 * 名称	->		ShutdownBroadcastReceiver.java
 */
package cn.nipc.mobiletool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 类名	->		ShutdownBroadcastReceiver
 * 作者 	->		谢健
 * 时间 	->		2013-8-27 下午5:43:09
 * 描述	->		接受系统关机时的广播
 * 标签	->		
 */
public class ShutdownBroadcastReceiver extends BroadcastReceiver{

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentNetwork = new Intent(context, ShutdownService.class);
		context.startService(intentNetwork);
		
	}
}
