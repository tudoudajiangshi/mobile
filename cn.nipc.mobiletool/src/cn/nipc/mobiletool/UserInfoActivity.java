/**
 * 功能	->		管理用户的ID，密码，用户的登录与注销，上传备份文件或者下载备份文件。
 * 作者	->		谢健
 * 时间	->		2013-7-31 下午10:24:32
 * 描述	->		用户的个人信息管理界面
 * 名称	->		UserInfoActivity.java
 */
package cn.nipc.mobiletool;

import android.app.Activity;
import android.os.Bundle;

/**
 * 类名	->		UserInfoActivity
 * 作者 	->		谢健
 * 时间 	->		2013-7-31 下午10:24:32
 * 描述	->		用户的个人信息管理界面
 * 标签	->		
 */
public class UserInfoActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
	}
	
}
