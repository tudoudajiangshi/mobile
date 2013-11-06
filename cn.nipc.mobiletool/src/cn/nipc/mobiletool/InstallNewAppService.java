package cn.nipc.mobiletool;

import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitor;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class InstallNewAppService extends Service{

	public static String TAG = "InstallNewAppService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "服务已经启动");
		String packageName = intent.getStringExtra("packageName");
		NetworkTrafficMonitor.checkNewInstallAppNeedNetwork(this, packageName);
		//终止本服务
		this.stopSelf();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG,"服务已关闭");
	}

}
