package cn.nipc.mobiletool.networktrafficmonitor;

/**
 * 类名	->		TrafficDBAccessException
 * 作者	->		谢健
 * 时间	->		2013-11-11 下午3:51:36
 * 描述	->		数据库处理异常类
 * 标签	->		TODO
 */
public class TrafficDBAccessException extends Exception {
	private static final long serialVersionUID = 1000L;
	public TrafficDBAccessException(){}
	public TrafficDBAccessException(String msg){
		super(msg);
	}
	

}
