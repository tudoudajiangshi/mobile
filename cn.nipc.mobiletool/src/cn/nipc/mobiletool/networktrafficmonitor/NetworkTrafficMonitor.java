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
	 * 描述		->		获得每个应用当月使用的流量
	 * 参数		->	   	 	
	 * 返回值	->		List<AppTrafficInfo>
	 */
	public static List<AppTrafficInfo> getMonthNetTrafficPerApp(Context c) {
		ArrayList<AppTrafficInfo> appInfoList = new ArrayList<AppTrafficInfo>();
		PackageManager pm = c.getApplicationContext().getPackageManager();
		int monthNum;
		
		List<ApplicationInfo> appList;  
		appList = pm.getInstalledApplications(PackageManager.GET_PERMISSIONS);  
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		monthNum = cal.get(Calendar.MONTH) + 1;
		
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
			for(ApplicationInfo ap : appList) {
				double dt_now_query = TrafficStats.getUidRxBytes(ap.uid);
				double ut_now_query = TrafficStats.getUidTxBytes(ap.uid);
				//不消耗流量的应用直接跳过
				if ((dt_now_query + ut_now_query) <= 0)
					continue;
				
				//如果为空 说明是第一次操作 所以每个都需要插入 所以初始值为1 
				int NeedInsert = 1;
				
				for(AppTrafficInfo appTrafficInfo : appInfoList){
					NeedInsert = 0;
					if(appTrafficInfo.appName.equalsIgnoreCase(ap.packageName)) {
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
						break;
					}
					//如果循环到最后 仍没有匹配的  说明有新安装的耗费流量的应用。需要数据库插入操作。
					if(appInfoList.indexOf(appTrafficInfo) == appInfoList.size()-1){
						NeedInsert = 1;
					}
				}
				if(NeedInsert == 1){
					AppTrafficInfo appTrafficInfo = new AppTrafficInfo();
					appTrafficInfo.downloadTraffic = dt_now_query ;
					appTrafficInfo.uploadTraffic = ut_now_query;
					appTrafficInfo.appName = ap.packageName;
					appTrafficInfo.date = monthNum;
					appInfoList.add(appTrafficInfo);
					//数据库中插入记录
					ContentValues value2 = new ContentValues();
					value2.put("apk", appTrafficInfo.appName);
					value2.put("ut_month", appTrafficInfo.uploadTraffic);
					value2.put("dt_month", appTrafficInfo.downloadTraffic);
					value2.put("date", monthNum);
					value2.put("dt_last_query",dt_now_query);
					value2.put("ut_last_query", ut_now_query);
					trafficInfoDBHelper.insertAppTraffic(value2);
				}	
			}
			//从应用列表中删除已经卸载且流量为0的应用。
			for(int i = 0; i < appInfoList.size();i++){
				AppTrafficInfo appTrafficInfo = appInfoList.get(i);
				if((appTrafficInfo.downloadTraffic + appTrafficInfo.uploadTraffic) <= 0){
					appInfoList.remove(i);
					i--;
					//从数据库中删除该条
					trafficInfoDBHelper.delAppTraffic(appTrafficInfo.appName);
				}
			}
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
		//最后获得根据包名 或者每个应用的图标和名字,一般系统程序没有图标，先过滤掉
		try{
			for (AppTrafficInfo appTrafficInfo : appInfoList){
				String appName = appTrafficInfo.appName;
				appTrafficInfo.appIcon = pm.getApplicationIcon(appName);
				ApplicationInfo app= pm.getApplicationInfo(appName,PackageManager.GET_UNINSTALLED_PACKAGES);
				appTrafficInfo.label = pm.getApplicationLabel(app).toString();
			}
		}catch (Exception e) {
			Log.e(TAG, e.getMessage()+"找不到这个包名的app具体信息");
		}
		return appInfoList;
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
	 * 函数名		->		NetworkTrafficMonitorInitial
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:33:21
	 * 描述		->		监视器初始化,安装后第一次运行时需要的初始动作：1.设置闹钟，每天24:00更新一次数据库 2.数据库初始化（两个表的内容的初始化）
	 * 					    3.测试是否支持TrafficState类。
	 * 参数		->		无
	 * 返回值		->		void
	 */
	public static void NetworkTrafficMonitorInitial(Context c) {
		initialFirstNetTrafficQuery(c);
	}
	/**
	 * 函数名	->		initialFirstNetTrafficQuery
	 * 作者		->		谢健
	 * 时间		->		2013-8-26 上午10:32:34
	 * 描述		->		安装时，查询一次当前手机内记录的从上次开机到现在使用的流量
	 * 参数		->		
	 * 返回值	->		void
	 */
	public static void initialFirstNetTrafficQuery(Context c) {
		TrafficInfoDBHelper trafficInfoDBHelper = new TrafficInfoDBHelper(c);
		int dayNum;
		int monthNum;
		
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
		}catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}finally {
			trafficInfoDBHelper.close();
		}
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
		aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*5, pi);
	}

	/**
	 * 函数名		->		initialMonitor
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:34:39
	 * 描述		->		每次打开这个模块需要的初始化：1.获得已安装的所有应用，查询是否有新的应用安装
	 * 参数		->		
	 * 返回值		->		void
	 */
	public static void initialMonitor() {
		
	}
}
