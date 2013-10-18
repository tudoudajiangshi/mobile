/**
 * 功能	->		主Activity的适配器，用来显示各模块以及功能触发
 * 作者	->		谢健
 * 时间	->		2013-7-30 下午3:24:49
 * 描述	->		联系数据与显示的适配器
 * 名称	->		MainActivityPagerAdapter.java
 */
package cn.nipc.mobiletool;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 类名	 		MainActivityPagerAdapter
 * 作者 			谢健
 * 时间 			2013-7-30 下午3:24:49
 * 描述		 	主Activity的viewpager的适配器，基本都是复写函数都是由框架层自动调用，无需管理页面的缓存与删除
 */
public class MainActivityPagerAdapter extends PagerAdapter{
	ArrayList<View> listView;
	Activity activity;
	//private View currentview;
	
	/**
	 * @param activity 显示数据的Activity
	 * @param listView 已经初始化好的view容器		
	 */
	public MainActivityPagerAdapter(Activity activity, ArrayList<View> listView){
		this.listView = listView;
		this.activity = activity;
	}
	
	/* (non-Javadoc)
	 * 返回页面的个数
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount(){
		return 3;
	}
	
	/* (non-Javadoc)
	 * 判断某一对象是否为view对象
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View obj1, Object obj2){
		return obj1 == obj2;
	}	
	
	/* (non-Javadoc)
	 * 销毁某个view对象
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.View, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(View container, int index, Object arg2) {
	     ((ViewPager) container).removeView(listView.get(index));
	}
	/* (non-Javadoc)
	 * 装载某个view对象 ，即显示某个其中一个view,如果显示之前需要有大量工作要做，需要再次进行，而不能放在主activity中。
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, final int index) {
		((ViewPager) container).addView(listView.get(index));
		return listView.get(index);
	}
}
