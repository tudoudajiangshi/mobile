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
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;
import cn.nipc.mobiletool.MainActivity;


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
	 * 函数名	->		getOneDayNetTraffic
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:25:20
	 * 描述		->		获得当天使用的流量  （每次关机前需要执行一次 每天晚上24:00也许要执行一次）
	 * 参数		->		dayNum当天的号数  c上下文，查询数据库需要 
	 * 返回值	->		double
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
	 * 参数		->	   	 无	
	 * 返回值	->		List<AppTrafficInfo>
	 */
	public static List<AppTrafficInfo> getMonthNetTrafficPerApp() {
		ArrayList appInfoList = new ArrayList<AppTrafficInfo>();
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
	 * 函数名	->		NetworkTrafficMonitorInitial
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:33:21
	 * 描述		->		监视器初始化,安装后第一次运行时需要的初始动作：1.设置闹钟，每天24:00更新一次数据库 2.数据库初始化（两个表的内容的初始化）
	 * 					3.测试是否支持TrafficState类。
	 * 参数		->		无
	 * 返回值	->		void
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
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 58);
		Intent intent=new Intent(c,MainActivity.class);
		PendingIntent pi = PendingIntent.getService(c, 0, intent, 0);
		AlarmManager aManager = (AlarmManager)c.getSystemService(Service.ALARM_SERVICE);
		aManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*60*24, pi);
	}

	/**
	 * 函数名	->		initialMonitor
	 * 作者		->		谢健
	 * 时间		->		2013-8-8 下午1:34:39
	 * 描述		->		每次打开这个模块需要的初始化：1.获得已安装的所有应用，查询是否有新的应用安装
	 * 参数		->		
	 * 返回值	->		void
	 */
	public static void initialMonitor() {
		
	}
}
