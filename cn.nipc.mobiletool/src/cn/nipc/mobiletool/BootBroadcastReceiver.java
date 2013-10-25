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
import android.util.Log;

/**
 * 类名	->		BootBroadcastReceiver
 * 作者 	->		谢健
 * 时间 	->		2013-8-6 下午5:22:07
 * 描述	->		初始化“开机、关机、安装程序”需要启动服务
 * 标签	->		开机启动
 */
public class BootBroadcastReceiver extends BroadcastReceiver{
	
	public static String TAG = "BootBroadcastReceiver";

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		//接收手机启动广播 启动相关服务
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {   
            Intent intentNetwork = new Intent(context, BootInitialService.class);   
//            newIntent.setAction("android.intent.action.MAIN");             
//            newIntent.addCategory("android.intent.category.LAUNCHER");            
//            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);            
            context.startActivity(intentNetwork);
        }
		//接收关机广播
		if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
			Intent intentNetwork = new Intent(context, ShutdownService.class);
			context.startService(intentNetwork);
		}
		//接收安装程序广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString().substring(8);
            Log.e(TAG, "********"+packageName);
            Intent intentNetwork = new Intent(context, InstallNewAppService.class);
            intentNetwork.putExtra("packageName", packageName);
        }
      //接收卸载程序广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString().substring(8);
            Log.e(TAG, "********"+packageName);
            Intent intentNetwork = new Intent(context, InstallNewAppService.class);
            intentNetwork.putExtra("packageName", packageName);
        }
	}
	
}
