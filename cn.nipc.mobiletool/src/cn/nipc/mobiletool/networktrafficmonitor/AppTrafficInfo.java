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
	private String appName;
	private Drawable appIcon;
	private double downloadTraffic;
	private double uploadTraffic;

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public double getDownloadTraffic() {
		return downloadTraffic;
	}
	public void setDownloadTraffic(double downloadTraffic) {
		this.downloadTraffic = downloadTraffic;
	}
	public double getUploadTraffic() {
		return uploadTraffic;
	}
	public void setUploadTraffic(double uploadTraffic) {
		this.uploadTraffic = uploadTraffic;
	}
}
