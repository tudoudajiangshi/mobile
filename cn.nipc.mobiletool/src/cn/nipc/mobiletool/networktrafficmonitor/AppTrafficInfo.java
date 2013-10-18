package cn.nipc.mobiletool.networktrafficmonitor;

import android.graphics.drawable.Drawable;


/**
 * 类名	->		AppTrafficInfo
 * 作者 	->		谢健
 * 时间 	->		2013-8-19 下午2:29:53
 * 描述	->		每个应用程序的信息，重点是使用的流量信息
 * 标签	->		
 */
public class AppTrafficInfo {
	public String appName;
	public Drawable appIcon;
	public double downloadTraffic;
	public double uploadTraffic;
	public double dt_last_query;
	public double ut_last_query;
	public int date;
	public String label;
	
	public AppTrafficInfo(){
		this.appIcon = null;
		this.appName = null;
		this.downloadTraffic = -1;
		this.uploadTraffic = -1;
		this.ut_last_query = -1;
		this.dt_last_query = -1;
		this.date = -1;
		this.label = null;
	}
}
