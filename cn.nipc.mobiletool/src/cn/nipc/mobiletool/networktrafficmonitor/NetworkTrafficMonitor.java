package cn.nipc.mobiletool.networktrafficmonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;


/**
 * 类名	->		NetworkTrafficMonitor
 * 作者 	->		谢健
 * 时间 	->		2013-8-8 下午1:41:23
 * 描述	->		实现流量监控封装
 * 标签	->		监控封装
 */

public class NetworkTrafficMonitor {
	public static String TAG = "NetworkTrafficMonitor";
	public static String INTERNET = "android.permission.INTERNET";
	
	
	/**
	 * 函数名		->		getTodayNetTraffic
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	Context（上下文）
	 * 返回值		-> 	double
	 * 时间		->	 	2013-9-11 上午10:17:54 
	*/
	public static double getTodayNetTraffic(Context c) {
		double now_query_dt;		
		double now_query_ut;
		double last_query_dt;
		double last_query_ut;
		double last_day_usage_dt;
		double last_day_usage_ut;
		double now_day_usage_dt = 0;
		double now_day_usage_ut = 0;
		int    last_query_id = 32;
		int    last_query_dayNum;
		int	   dayNum;
		int    monthNum;
		Cursor cursor;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		dayNum = cal.get(Calendar.DAY_OF_MONTH);
		Log.v(TAG, "月份的输出方式是"+cal.get(Calendar.MONTH));
		monthNum = cal.get(Calendar.MONTH) + 1;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		now_query_dt = TrafficStats.getMobileRxBytes();
		now_query_ut = TrafficStats.getMobileTxBytes();
		try{
			cursor  = trafficInfoDBHelper.queryOneDayTraffic(last_query_id);
			cursor.moveToNext();
			last_query_dt = cursor.getDouble(cursor.getColumnIndex("dt"));
			last_query_ut = cursor.getDouble(cursor.getColumnIndex("ut"));
			last_query_dayNum = cursor.getInt(cursor.getColumnIndex("date"));
			cursor.close();
			int last_query_monthNum = last_query_dayNum/100;	//表示最近一次查询所在的月
			int last_query_dateNum  = last_query_dayNum%100;	//表示最近一次查询所在的号
			if(last_query_dateNum != dayNum) {
				ContentValues values2 = new ContentValues();
				values2.put("dt",0);
				values2.put("ut",0);
				values2.put("id",dayNum);
				trafficInfoDBHelper.updateOneDayTraffic(values2);
			}
			if(last_query_monthNum != monthNum) {
				ContentValues values3 = new ContentValues();
				values3.put("dt",0);
				values3.put("ut",0);
				trafficInfoDBHelper.updateOneDayTraffic(values3);
			}
			cursor = trafficInfoDBHelper.queryOneDayTraffic(dayNum);
			cursor.moveToNext();
			last_day_usage_dt = cursor.getDouble(cursor.getColumnIndex("dt"));
			last_day_usage_ut = cursor.getDouble(cursor.getColumnIndex("ut"));
			cursor.close();
			now_day_usage_dt = last_day_usage_dt + now_query_dt - last_query_dt;
			now_day_usage_ut = last_day_usage_ut + now_query_ut - last_query_ut;
			//如果开机自启动被禁用 则可能出现负值 需要额外处理
			if( (now_query_dt - last_query_dt)<0 || 
					(now_query_ut - last_query_ut)<0 )
			{
				now_day_usage_dt = now_query_dt + last_day_usage_dt;
				now_day_usage_ut = now_query_ut + last_day_usage_ut;
			}
			ContentValues values = new ContentValues();
			values.put("dt", now_day_usage_dt);
			values.put("ut", now_day_usage_ut);
			values.put("id", dayNum);
			trafficInfoDBHelper.updateOneDayTraffic(values);
			ContentValues values1 = new ContentValues();
			values1.put("dt",now_query_dt);
			values1.put("ut",now_query_ut);
			values1.put("date", dayNum + 100*monthNum);
			values1.put("id", last_query_id);
			trafficInfoDBHelper.updateOneDayTraffic(values1);
		}catch (Exception e) {
			Log.e(TAG, "esdf"+e.getMessage());
		}finally {
			trafficInfoDBHelper.close();				
		}
		
		return now_day_usage_dt + now_day_usage_ut;
	}
	
	/**
	 * 函数名		->		getOneDayNetTraffic
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:25:20
	 * 描述		->		获得当天使用的流量  （每次关机前需要执行一次 每天晚上24:00也许要执行一次）
	 * 参数		->		dayNum当天的号数  c上下文，查询数据库需要 
	 * 返回值		->		double
	 */
	public static double getOneDayNetTraffic(int dayNum, Context c) {
		double ut = 0;
		double dt = 0;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		try{
			Cursor cursor  = trafficInfoDBHelper.queryOneDayTraffic(dayNum);
			cursor.moveToNext();
			dt = cursor.getDouble(cursor.getColumnIndex("dt"));
			ut = cursor.getDouble(cursor.getColumnIndex("ut"));
			cursor.close();
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
		return ut + dt;
	}
	/**
	 * 函数名	->		getMonthNetTraffic
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:26:44
	 * 描述		->		获得某个月使用的流量
	 * 参数		->		
	 * 返回值	->		double
	 */
	public static double getMonthNetTraffic(Context c) {
		
		int dayNum;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		double monthTraffic = 0;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());		
		dayNum = cal.get(Calendar.DAY_OF_MONTH);
		try{
			Cursor cursor = trafficInfoDBHelper.queryMonthTraffic(dayNum);
			while(cursor.moveToNext()) {
				monthTraffic += cursor.getDouble(cursor.getColumnIndex("ut")) + cursor.getDouble(cursor.getColumnIndex("dt"));
			}
			cursor.close();
		}catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
		return monthTraffic;
	}

	/**
	 * 函数名	->		getDayNetTrafficPerApp
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:29:21
	 * 描述		->		获得每个应用的当天的使用的流量
	 * 参数		->		无
	 * 返回值	->		List<AppTrafficInfo>
	 */
	public static List<AppTrafficInfo> getDayNetTrafficPerApp() {
		ArrayList appInfoList = new ArrayList<AppTrafficInfo>();
		return appInfoList;
	}

	/**
	 * 函数名	->		getMonthNetTrafficPerApp
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:29:58
	 * 描述		->		获得每个应用当月使用的流量,直接查询数据库，不进行更新操作
	 * 参数		->	   	 	
	 * 返回值	->		List<AppTrafficInfo>
	 */
	public static List<AppTrafficInfo> getMonthNetTrafficPerApp(Context c) {
		ArrayList<AppTrafficInfo> appInfoList = new ArrayList<AppTrafficInfo>();
		PackageManager pm = c.getApplicationContext().getPackageManager();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int monthNum = cal.get(Calendar.MONTH) + 1;
		
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		try{
			Cursor cursor = trafficInfoDBHelper.queryAppTraffic();
			while(cursor.moveToNext()) {
				AppTrafficInfo appTrafficInfo = new AppTrafficInfo();
				int date = cursor.getInt(cursor.getColumnIndex("date"));
				//判断这次查询与上次查询呢是否为在不同的月份, 如果是需要先进行数据 清零操作
				if(date != monthNum){
					ContentValues value = new ContentValues();
					value.put("ut_month", 0);
					value.put("dt_mnth", 0);
					value.put("date",monthNum);
					trafficInfoDBHelper.updateAppTraffic(value);
				}
				String apk_name = cursor.getString(cursor.getColumnIndex("apk"));		
				double ut_month = cursor.getDouble(cursor.getColumnIndex("ut_month"));
				double dt_month = cursor.getDouble(cursor.getColumnIndex("dt_month"));
				double dt_last_query = cursor.getDouble(cursor.getColumnIndex("dt_last_query"));
				double ut_last_query = cursor.getDouble(cursor.getColumnIndex("ut_last_query"));
				appTrafficInfo.downloadTraffic = dt_month;
				appTrafficInfo.uploadTraffic = ut_month;
				appTrafficInfo.appName = apk_name;
				appTrafficInfo.dt_last_query = dt_last_query;
				appTrafficInfo.ut_last_query = ut_last_query;
				appTrafficInfo.date = date;
				appInfoList.add(appTrafficInfo);
			}
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
		//最后获得根据包名 或者每个应用的图标和名字,一般系统程序没有图标，先过滤掉
		/*
		try{
			for (AppTrafficInfo appTrafficInfo : appInfoList){
				PackageInfo    pi = pm.getPackageInfo(appTrafficInfo.appName, 0);
				appTrafficInfo.appIcon = pm.getApplicationIcon(pi.applicationInfo);
				appTrafficInfo.label = (String)pi.applicationInfo.loadLabel(pm);
			}
		}catch (Exception e) {
			Log.e(TAG, e.getMessage()+"找不到这个包名的app具体信息");
		}*/
		return appInfoList;
	}
	
	/**
	 * 函数名		->		updateMonthNetTrafficPerApp
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)查询每个应用使用的流量，并更新同步到数据库
	 * 参数		-> 	TODO
	 * 返回值		-> 	void
	 * 时间		->	 	2013-10-19 下午3:44:34 
	*/
	public static void updateMonthNetTrafficPerApp(Context c){
		List<AppTrafficInfo> appInfoList = getMonthNetTrafficPerApp(c);
		PackageManager pm = c.getApplicationContext().getPackageManager();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int monthNum = cal.get(Calendar.MONTH) + 1;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		
		try{
			for(AppTrafficInfo appTrafficInfo : appInfoList){
				PackageInfo  pi = pm.getPackageInfo(appTrafficInfo.appName, PackageManager.GET_PERMISSIONS);
				double dt_now_query = TrafficStats.getUidRxBytes(pi.applicationInfo.uid);
				double ut_now_query = TrafficStats.getUidTxBytes(pi.applicationInfo.uid);
				if(dt_now_query + ut_now_query <= 0)
					continue;
				
				//如果开机自启动被禁用 则可能出现负值 需要额外处理
				if( (dt_now_query - appTrafficInfo.dt_last_query)<0 || 
						(ut_now_query - appTrafficInfo.ut_last_query)<0 )
				{
					appTrafficInfo.downloadTraffic += dt_now_query ;
					appTrafficInfo.uploadTraffic += ut_now_query ;
				}
				else {
					appTrafficInfo.downloadTraffic += dt_now_query - appTrafficInfo.dt_last_query; 
					appTrafficInfo.uploadTraffic += ut_now_query - appTrafficInfo.ut_last_query;
				}
				appTrafficInfo.dt_last_query = dt_now_query;
				appTrafficInfo.ut_last_query = ut_now_query;
				appTrafficInfo.date = monthNum;
				//更新数据库
				ContentValues value1 = new ContentValues();
				value1.put("apk", appTrafficInfo.appName);
				value1.put("ut_month", appTrafficInfo.uploadTraffic);
				value1.put("dt_month", appTrafficInfo.downloadTraffic);
				value1.put("dt_last_query", dt_now_query);
				value1.put("ut_last_query", ut_now_query);
				value1.put("date", monthNum);
				trafficInfoDBHelper.updateAppTraffic(value1);
			}
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}finally{
			trafficInfoDBHelper.close();
		}
	}
	
	/**
	 * 函数名	->		setAppNetControl
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:30:27
	 * 描述		->		设置手机防火墙
	 * 参数		->		contentValues 包含 1.应用包名 2。是否可接入wifi 3.是否可接入2/3G
	 * 返回值	->		void
	 */
	public static void setAppNetControl(ContentValues values) {
		
	}

	/**
	 * 函数名		->		initialForBootDoNetworkTrafficQuery
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:33:21
	 * 描述		->		监视器初始化,重启后需要的初始动作：数据库保留的上次查询记录清零				    
	 * 参数		->		无
	 * 返回值		->		void
	 */
	public static void initialForBootDoNetworkTrafficQuery(Context c) {
		//手机重启之后的 总流量部分的初始化查询操作，跟程序安装的时候初始化查询是一样的，所以直接调用
		int dayNum;
		int monthNum;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());		
		dayNum = cal.get(Calendar.DAY_OF_MONTH);
		monthNum = cal.get(Calendar.MONTH) + 1;
	
		ContentValues values = new ContentValues();
		values.put("dt", TrafficStats.getMobileRxBytes());
		values.put("ut", TrafficStats.getMobileTxBytes());
		values.put("date", dayNum + monthNum*100);
		values.put("id", 32);
		try{
			trafficInfoDBHelper.updateOneDayTraffic(values);
		}catch (Exception e) {
			Log.e(TAG, "initialForBootDoNetworkTrafficQuery*");
		}finally{
			trafficInfoDBHelper.close();
		}
		//每个应用程序的的流量的初始化操作 跟程序安装时候的初始化化不同	
		//这里只需要将每个应用的上次查询清零即可		
		ContentValues value1 = new ContentValues();
		value1.put("dt_last_query", 0);
		value1.put("ut_last_query", 0);
		value1.put("date", monthNum);
		try{
			trafficInfoDBHelper.updateAppTraffic(value1);
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally{
			trafficInfoDBHelper.close();
		}
		
	}
	
	/**
	 * 函数名  	->		initialForInstallDoNetTrafficQuery
	 * 作者		->		谢健
	 * 时间		->		2013-8-26 上午10:32:34
	 * 描述		->		安装时，查询一次当前手机内记录的从上次开机到现在使用的流量并用这些数据初始化数据库
	 * 参数		->		
	 * 返回值	    ->		void
	 */
	public static void initialForInstallDoNetTrafficQuery(Context c) {
		int dayNum;
		int monthNum;
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());		
		dayNum = cal.get(Calendar.DAY_OF_MONTH);
		monthNum = cal.get(Calendar.MONTH) + 1;
		
		//程序安装时候  总流量的初始化查询 更新last_query
		ContentValues values = new ContentValues();
		values.put("dt", TrafficStats.getMobileRxBytes());
		values.put("ut", TrafficStats.getMobileTxBytes());
		values.put("date", dayNum + monthNum*100);
		values.put("id", 32);
		try{
			trafficInfoDBHelper.updateOneDayTraffic(values);
		}catch (Exception e) {
			Log.e(TAG, "initialForInstallDoNetTrafficQuery*");
		}finally{
			trafficInfoDBHelper.close();
		}
		//程序安装时候  每个消耗流量的应用的初始化查询 更新last_query
		initialForInstallDoGetApp(c);
	
	}
	
	/**
	 * 函数名	->		initialSetClockQuery
	 * 作者		->		谢健
	 * 时间		->		2013-8-27 下午3:25:01
	 * 描述		->		设置定时，每个一段时间就查询一下流量，同时更新数据库,执行查询操作时通过启动一个service来完成的。
	 * 参数		->		
	 * 返回值	->		void
	 */
	public static void initialSetTimingQuery(Context c) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(System.currentTimeMillis());
//		//cal.set(Calendar.HOUR_OF_DAY, 23);
//		cal.set(Calendar.MINUTE, 59);
		Intent intent=new Intent(c,TrafficQueryService.class);
		PendingIntent pi = PendingIntent.getService(c, 0, intent, 0);
		AlarmManager aManager = (AlarmManager)c.getSystemService(Service.ALARM_SERVICE);
		//先将AlarmManager停止
		aManager.cancel(pi);
		aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*3, pi);
	}

	/**
	 * 函数名		->		initialForInstallDoGetApp
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		安装时初始化，通过遍历程序的权限，记录可以联入互联网的程序 初始化数据库
	 * 返回值		-> 	void
	 * 时间		->	 	2013-10-24 下午5:33:12 
	*/
	public static void initialForInstallDoGetApp(Context c){
		PackageManager pm = c.getApplicationContext().getPackageManager();   
		List<ApplicationInfo> appList = pm.getInstalledApplications(PackageManager.GET_PERMISSIONS);
		for(ApplicationInfo ap : appList) 
			checkNewInstallAppNeedNetwork(c,ap.packageName);	
	}
	
	/**
	 * 函数名		->		checkNewInstallAppNeedNetwork
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		当有新程序安装时 判断该程序是否有访问网络权限 然后决定将其加入数据库
	 * 返回值		-> 	void
	 * 时间		->	 	2013-10-24 下午5:36:01 
	*/
	public static void checkNewInstallAppNeedNetwork (Context c, String packageName){
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		PackageManager pm = c.getApplicationContext().getPackageManager();
		try{
			if(isAppNeedNetwork(c, packageName) == 1){
				PackageInfo  pi = pm.getPackageInfo(packageName, 0);
/*				if((pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
					//系统更新出的应用
					Log.e("xitong gengxin", packageName);
				}
				else if((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
					//系统更新用的应用
					Log.e("xitong", packageName);
				}
				else 
					Log.e("di sanfang",packageName);*/
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				int monthNum = cal.get(Calendar.MONTH) + 1;			
				int uid = pi.applicationInfo.uid;
				ContentValues value2 = new ContentValues();
				value2.put("apk", packageName);
				value2.put("ut_month", 0);
				value2.put("dt_month", 0);
				value2.put("date", monthNum);
				value2.put("dt_last_query", TrafficStats.getUidRxBytes(uid));
				value2.put("ut_last_query", TrafficStats.getUidTxBytes(uid));
				trafficInfoDBHelper.insertAppTraffic(value2);
				Log.e(TAG, "tianjiale~~------");
			}	
		}catch (Exception e) {
			Log.e(TAG,packageName);
		}finally {
			trafficInfoDBHelper.close();
		}
		
	}
	
	/**
	 * 函数名		->		checkNewUninstallAppNeedNetwork
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	TODO
	 * 描述		->		当有新程序卸载时，判断该程序是否有访问网络的权限 然后决定是否将其从数据库中取出
	 * 返回值		-> 	void
	 * 时间		->	 	2013-10-24 下午5:44:24 
	*/
	public static void checkNewUninstallAppNeedNetwork(Context c, String packageName){
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		try{
			trafficInfoDBHelper.delAppTraffic(packageName);
		}catch (Exception e) {
			Log.e(TAG,e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
	}
	
	/**
	 * 函数名		->		isAppNeedNetwork
	 * 作者		-> 	谢健
	 * 适用条件	-> 	(这里描述这个方法适用条件 – 可选)
	 * 参数		-> 	判断某个程序是否需要连接互联网的权限
	 * 描述		->		TODO
	 * 返回值		-> 	int
	 * 时间		->	 	2013-10-24 下午8:19:21 
	*/
	public static int isAppNeedNetwork(Context c, String packageName) {
		PackageManager pm = c.getApplicationContext().getPackageManager();
		int isAccessInternet = 0;
		try{
			PackageInfo  pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			String [] usesPermissionsArray=pi.requestedPermissions;
			if(usesPermissionsArray == null){
				return isAccessInternet;
			}			
			for(int i =0; i< usesPermissionsArray.length; i++ ){
				if(usesPermissionsArray[i].equalsIgnoreCase(INTERNET)){
					isAccessInternet = 1;
					break;
				}				
			}
		}catch (Exception e) {
				Log.e(TAG, e.getMessage());
		}
		return isAccessInternet;
	}
	/**
	 * 函数名		->		initialMonitor
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:34:39
	 * 描述		->		每次打开这个模块需要的初始化：1.获得已安装的所有应用，查询是否有新的应用安装2.测试是否支持TrafficState类。
	 * 参数		->		
	 * 返回值		->		void
	 */
	public static void initialMonitor() {
		
	}
}
