/**
 * 功能	->		存储每个模块的图标，文字标题，打开时需要启动的service或者activiry
 * 作者	->		谢健
 * 时间	->		2013-7-31 上午11:05:41
 * 描述	->		相当于数据结构
 * 名称	->		ModuleInfo.java
 */
package cn.nipc.mobiletool;

import java.util.ArrayList;

import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitorActivity;

/**
 * 类名	->		ModuleInfo
 * 作者 	->		谢健
 * 时间 	->		2013-7-31 上午11:05:41
 * 描述	->		各个模块的信息
 * 标签	->		无
 */
public class ModuleInfo{
	public String imageMsg;
	public int imageId;
	Class<?> activityName;
	
	public static ArrayList<ModuleInfo> initModuleInfo() {
		ArrayList<ModuleInfo> moduleInfos = new ArrayList<ModuleInfo>();
		moduleInfos.add(initOneModuleInfo("手机体检", R.drawable.tool_box_ticket,null));
		moduleInfos.add(initOneModuleInfo("病毒扫描", R.drawable.tool_box_system_exam,null));
		moduleInfos.add(initOneModuleInfo("漏洞扫描", R.drawable.tool_box_quickdial,null));
		moduleInfos.add(initOneModuleInfo("流量监控", R.drawable.tool_box_network,NetworkTrafficMonitorActivity.class));
		moduleInfos.add(initOneModuleInfo("安全上网", R.drawable.tool_box_feescan,null));
		moduleInfos.add(initOneModuleInfo("数据备份", R.drawable.tool_box_baohe,null));
		moduleInfos.add(initOneModuleInfo("手机防盗", R.drawable.smsunread,null));
		moduleInfos.add(initOneModuleInfo("骚扰拦截", R.drawable.smsunread,null));
		return moduleInfos;
	}
	
	public static ModuleInfo initOneModuleInfo(String arg1, int arg2, Class<?> arg3) {
		ModuleInfo module = new ModuleInfo();
		module.imageMsg = arg1;
		module.imageId = arg2;
		module.activityName = arg3;
		return module;
	}
}
