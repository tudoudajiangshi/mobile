/**
 * ����	->		TrafficDB��ݿ�����쳣��
 * ����	->		л��
 * ʱ��	->		2013-7-23 ����4:13:18
 * ����	->		����TrafficDB����������쳣
 * ���	->		TrafficDBAccessException.java
 */
package cn.nipc.mobiletool.networktrafficmonitor;

/**
 * ����	 		TrafficDBAccessException
 * ���� 			л��
 * ʱ�� 			2013-7-23 ����4:13:18
 * ����		 	����TrafficDB����������쳣 ���Ϊ1000L
 */
public class TrafficDBAccessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1000L;
	public TrafficDBAccessException(){}
	public TrafficDBAccessException(String msg){
		super(msg);
	}
	

}
