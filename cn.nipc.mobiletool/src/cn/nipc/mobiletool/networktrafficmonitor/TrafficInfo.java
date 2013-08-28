package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

/**
 * 类名	->		TrafficInfo
 * 作者 	->		谢健
 * 时间 	->		2013-8-6 下午10:12:37
 * 描述	->		记录每个应用的相关信息
 * 标签	->		
 */
public class TrafficInfo {
	
	private Context context;
	public TrafficInfoDBHelper trafficInfoDBHelper;
	public static long baseUploadTraffic;
	public static long baseDownloadTraffic;
	TrafficInfo(Context c) {  
		context = c;
		trafficInfoDBHelper = new TrafficInfoDBHelper(context);
    } 
	

	/**
	 * 函数名	->		updateTodayAllTraffic
	 * 作者		->		谢健
	 * 时间		->		2013-8-6 下午10:13:23
	 * 描述		->		更新当天的各个应用消耗的流量
	 * 参数		->		无
	 * 返回值	->		void
	 */
	public void updateTodayAllTraffic(){
		long dt = TrafficStats.getMobileRxBytes() - baseDownloadTraffic;
		long ut = TrafficStats.getMobileTxBytes() - baseUploadTraffic;
		Calendar cal = Calendar.getInstance();
		String date = ""+cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DATE);
		
		ContentValues cvAllTraffic = new ContentValues();
		cvAllTraffic.put("data",date);
		cvAllTraffic.put("dt", dt/1024.0);
		cvAllTraffic.put("ut", ut/1024.0);	
		//trafficInfoDBHelper.insertAllTraffic(cvAllTraffic);
	}
	public void updateTodayAppInfo(){
		
	}
	public void initial(){
		baseUploadTraffic = TrafficStats.getMobileTxBytes();
		baseDownloadTraffic = TrafficStats.getMobileRxBytes();
	}
	
	/**
	 * 函数名	->		getInstallAppInfo
	 * 作者		->		谢健
	 * 时间		->		2013-8-6 下午10:17:52
	 * 描述		->		获得当前已经安装的应用的信息
	 * 参数		->		无
	 * 返回值	->		Map<String,Integer>
	 */
	public Map<String, Integer> getInstallAppInfo(){
		Map<String, Integer> installAppInfo=new HashMap<String, Integer>();  
		
		PackageManager pckMan = context.getPackageManager();
		List<PackageInfo> packs = pckMan.getInstalledPackages(0);
		for(PackageInfo p : packs){
			if(p.applicationInfo.packageName !=null ){
				installAppInfo.put(p.applicationInfo.packageName, p.applicationInfo.uid);
			}
		}	
		return installAppInfo;
	}

}
