package cn.nipc.mobiletool.networktrafficmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 类名	->		NetstateChangeReceiver
 * 作者	->		谢健
 * 时间	->		2013-9-22 上午10:10:52
 * 描述	->		侦听网络状态发生变化的广播
 * 标签	->		网络状态发生变化
 */
public class NetstateChangeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentNetwork = new Intent(context, TrafficQueryService.class);
		context.startService(intentNetwork);
		
	}

}
