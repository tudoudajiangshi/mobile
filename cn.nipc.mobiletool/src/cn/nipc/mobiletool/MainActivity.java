package cn.nipc.mobiletool;

import java.util.ArrayList;
import cn.nipc.mobiletool.networktrafficmonitor.NetworkTrafficMonitor;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {	
	
	private ArrayList<ModuleInfo> ModuleInfos;
	private ViewPager viewPager;
	private ArrayList<View> listView;
	private TextView t1, t2, t3;
	private ImageView cursor;
	private int offset = 0;
	private int currentIndex = 0;
	private int bmpwidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences sp = this.getSharedPreferences("mobile_tool_preference", MODE_PRIVATE);
		Editor spEditor = sp.edit();
		boolean isFirstRun =  sp.getBoolean("isFirstRun", true);
		if(isFirstRun){
			spEditor.putBoolean("isFirstRun", false);  
			spEditor.commit();  
			//一些安装的时候需要的初始化
			//初始化流量查询
			NetworkTrafficMonitor.initialFirstNetTrafficQuery(this);
		}
		//初始化定时流量查询 如果手机设置了禁止自启动 则需要每次启动应用程序是启动定时查询流量。
		NetworkTrafficMonitor.initialSetTimingQuery(this);
		
		initViewPager();
		initTextView();
		initImageView();
		initBottomView();
		//测试一些函数
		
	}
	
	/**
	 * 函数名		initViewPager
	 * 作者			谢健
	 * 时间			2013-7-31 上午10:43:05
	 * 描述			初始化pageview,创建view对象，共三个，添加的view容器中，并设置适配器
	 * 参数			void
	 * 返回值		void
	 */
	private void initViewPager() {
		ModuleInfos = ModuleInfo.initModuleInfo();
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		listView = new ArrayList<View>();
		
		View view0= getLayoutInflater().inflate(R.layout.main_grid, null);
		GridView gridView0 = (GridView) view0.findViewById(R.id.gridView1);
		//去掉GridView自带的点击背景色
		gridView0.setSelector(new ColorDrawable(Color.TRANSPARENT));
		setGridViewAdapter(gridView0, 0);
		setGridViewClickListener(gridView0,0);
		
		View view1= getLayoutInflater().inflate(R.layout.main_grid, null);
		GridView gridView1 = (GridView) view1.findViewById(R.id.gridView1);
		gridView1.setSelector(new ColorDrawable(Color.TRANSPARENT));
		setGridViewAdapter(gridView1, 1);
		setGridViewClickListener(gridView1,1);
		
		View view2= getLayoutInflater().inflate(R.layout.main_grid, null);
		GridView gridView2 = (GridView) view2.findViewById(R.id.gridView1);
		gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT));
		setGridViewAdapter(gridView2, 2);
		setGridViewClickListener(gridView2,2);
		
		listView.add(view0);
		listView.add(view1);
		listView.add(view2);
		viewPager.setAdapter(new MainActivityPagerAdapter(MainActivity.this, listView));
		viewPager.setCurrentItem(0);
		viewPager.setPageMargin(50);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	/**
	 * 函数名		setGridViewAdapter
	 * 作者			谢健
	 * 时间			2013-7-31 上午10:46:41
	 * 描述			设置gridView的适配器，每个view页面中只有一个gridview，但是每个gridview中的方框（图片）的个数不同。基本是复写BaseAdapter.
	 * 参数			1.需要设置的GridView 2.页面索引号 int
	 * 返回值		void
	 */
	private void setGridViewAdapter(GridView gridView, final int index) {
		gridView.setAdapter(new BaseAdapter() {
			
			@Override
			public int getCount() {
				if(index == 0)
					return 3;
				else if (index == 1) {
					return 2;
				}
				else {
					return 3;
				}
			}
					
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View item = getLayoutInflater().inflate(R.layout.grid_item, null);
				ImageView iv = (ImageView) item.findViewById(R.id.imageView1);
				TextView tv = (TextView) item.findViewById(R.id.msg);;
				switch (index) {
				case 0:
					iv.setImageResource((ModuleInfos.get(position)).imageId);
					tv.setText((ModuleInfos.get(position)).imageMsg);
					break;
				case 1:
					iv.setImageResource((ModuleInfos.get(3 + position)).imageId);
					tv.setText((ModuleInfos.get(3 + position)).imageMsg);
					break;
				case 2:
					iv.setImageResource((ModuleInfos.get(5 + position)).imageId);
					tv.setText((ModuleInfos.get(5 + position)).imageMsg);
				default:
					break;
				}
				return item;
			}

			@Override
			public Object getItem(int position) {
				switch (index) {
				case 0:
					return ModuleInfos.get(position);
				case 1:
					return ModuleInfos.get(position+3);
				case 2:
					return ModuleInfos.get(position+5);
				}
				return ModuleInfos.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
		});
	}
	
	/**
	 * 函数名	->		setGridViewClickListener
	 * 作者		->		谢健
	 * 时间		->		2013-8-5 上午9:26:02
	 * 描述		->		设置GridView中每个图标的点击响应
	 * 参数		->		1.需要设置的GridView 2.页面索引号
	 * 返回值	->		void
	 */
	public void setGridViewClickListener(GridView gridView, final int index) {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int base = 0;
				switch (index) {
				case 0:
					base = 0;
					break;
				case 1:
					base = 3;
					break;
				case 2:
					base = 5;
					break;
				default:
					break;
				}
				Intent intent = new Intent(MainActivity.this, ModuleInfos.get((int)arg3+base).activityName);
				startActivity(intent);
				
			}
		});
	}

	/**
	 * 类名	->		MyOnPageChangeListener
	 * 作者 	->		谢健
	 * 时间 	->		2013-7-31 下午5:17:36
	 * 描述	->		设置页面切换的监听
	 * 标签	->		主要让标题下的小横条移动
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		
		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		/* (non-Javadoc)
		 * 设置页面切换的时候中间横条的动作
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
		 */
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			int one = offset * 2 + bmpwidth;    //页卡1 -> 页卡2 偏移量
			int two = one * 2;						//页卡1 -> 页卡3 偏移量
			switch (arg0) {
			case 0:
				if(currentIndex == 1) {
					animation = new TranslateAnimation(one,0,0,0);
				}else if(currentIndex == 2){
					animation = new TranslateAnimation(two, 0,0,0);
				}
				break;
			case 1:
				if(currentIndex == 0) {
					animation = new TranslateAnimation(0, one, 0, 0);
					Log.v("animation", "从0 到1"+"ddd"+one+"  "+two);
				}else if(currentIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if(currentIndex == 0) {
					animation = new TranslateAnimation(0, two, 0, 0);
				}else if(currentIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			default:
				break;
			}
			currentIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.setAnimation(animation);
			animation.startNow();
		}
	}
	/**
	 * 函数名	->		initTextView
	 * 作者		->		谢健
	 * 时间		->		2013-7-31 下午3:35:51
	 * 描述		->		初始化标题栏 即“扫描、监控、工具”
	 * 参数		->		无
	 * 返回值	->		void
	 */
	public void initTextView() {
		t1 = (TextView) findViewById(R.id.title1);
		t2 = (TextView) findViewById(R.id.title2);
		t3 = (TextView) findViewById(R.id.title3);
		
		t1.setOnClickListener(new TitleOnClickListener(0));
		t2.setOnClickListener(new TitleOnClickListener(1));
		t3.setOnClickListener(new TitleOnClickListener(2));
	}
	
	/**
	 * 类名	->		TitleOnClickListener
	 * 作者 	->		谢健
	 * 时间 	->		2013-7-31 下午3:49:58
	 * 描述	->		标题点击监听类 转换到相应的页面
	 * 标签	->		
	 */
	public class TitleOnClickListener implements View.OnClickListener{
		private int index = 0;
		
		public TitleOnClickListener(int i) {
			index = i;
		}
		
		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
		
	}
	
	/**
	 * 函数名	->		initImageView
	 * 作者		->		谢健
	 * 时间		->		2013-7-31 下午4:49:03
	 * 描述		->		初始化标题下的滑动条
	 * 参数		->		无
	 * 返回值	->		void
	 */
	public void initImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpwidth = BitmapFactory.decodeResource(getResources(), R.drawable.main_cursor).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		offset = (screenWidth / 3 - bmpwidth) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}
	
	/**
	 * 函数名	->		initBottomView
	 * 作者		->		谢健
	 * 时间		->		2013-7-31 下午9:59:02
	 * 描述		->		初始化主界面底部的几个图形按钮，包括关于、设置、用户信息。
	 * 参数		->		无
	 * 返回值	->		void
	 */
	public void initBottomView() {
			
		ImageView aboutImage= (ImageView) findViewById(R.id.about);
		aboutImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
		
		ImageView setImage= (ImageView) findViewById(R.id.set);
		setImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetActivity.class);
				startActivity(intent);
			}
		});
		
		ImageView userinfoImage= (ImageView) findViewById(R.id.userinfo);
		userinfoImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
