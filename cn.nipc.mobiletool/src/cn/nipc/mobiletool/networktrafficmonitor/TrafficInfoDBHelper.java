package cn.nipc.mobiletool.networktrafficmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 类名	->		TrafficInfoDBHelper
 * 作者 	->		谢健
 * 时间 	->		2013-8-6 下午10:20:30
 * 描述	->		对数据库traffic.db进行操作
 * 标签	->		数据库
 */
public class TrafficInfoDBHelper extends SQLiteOpenHelper{
	public String TAG = "TrafficInfoDBHelper";
	private static final String DB_NAME = "traffic.db";  
    private static final String TBL_NAME_APP_TRAFFIC = "app_traffic";  
    private static final String TBL_NAME_ALL_TRAFFIC = "all_traffic";  
    private static final String CREATE_TBL_APP_TRAFFIC = " create table "  
            + " app_traffic(id INTEGER primary key autoincrement,apk TEXT,dt_day DOUBLE," +
            " ut_day DOUBLE,dt_month DOUBLE,ut_month DOUBLE,access_wifi INTEGER," +
            "access_2g_3g INTEGER,dt_last_query DOUBLE,ut_last_query DOUBLE)";  
    private static final String CREATE_TBL_ALL_TRAFFIC = " create table "  
            + " all_traffic(id INTEGER primary key autoincrement,date INTEGER,dt DOUBLE,ut DOUBLE) ";  
    private SQLiteDatabase db;  
    
    public TrafficInfoDBHelper(Context c) {  
        super(c, DB_NAME, null, 2);  
    }  

    /* (non-Javadoc)
     * detail:
     * 创建数据库，共有两个表，app_traffic all_traffic 
     * 并初始化all_traffic部分记录
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        this.db = db;  
        Log.v(TAG, "创建数据库");
        db.execSQL(CREATE_TBL_APP_TRAFFIC);
        db.execSQL(CREATE_TBL_ALL_TRAFFIC);
        for(int i = 1;i<=32;i++)
        {
        	db.execSQL("insert into all_traffic(id,dt,ut) values(null,0,0)"); 	
        }
    }  
    
    /**
     * 函数名	->		insertAppTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:24:39
     * 描述		->		加入一个应用的流量信息
     * 参数		->		ContentValues 要添加的记录的信息
     * 返回值	->		void
     */
    public void insertAppTraffic(ContentValues values){  
        SQLiteDatabase db = getWritableDatabase();  
        long res;
        res = db.insert(TBL_NAME_APP_TRAFFIC, null, values); 
        if(res == -1)
        	Log.e("TrafficDBAccess", "insert error");
    }    

    
    /**
     * 函数名	->		queryAppTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:30:04
     * 描述		->		查询应用使用的流量
     * 参数		->		无
     * 返回值	->		Cursor
     */
    public Cursor queryAppTraffic() throws Exception{  
        SQLiteDatabase db = getReadableDatabase();  
        Cursor c = db.query(TBL_NAME_APP_TRAFFIC, null, null, null, null, null, null);
        if(c.getCount() == 0)
        	throw new TrafficDBAccessException("query error");
        return c;  
    }  

    
    /**
     * 函数名	->		queryMonthTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:31:00
     * 描述		->		查询本月使用的流量
     * 参数		->		dayNum 本月几号
     * 返回值	->		Cursor
     */
    public Cursor queryMonthTraffic(int dayNum) throws Exception{  
        SQLiteDatabase db = getReadableDatabase();  
        Cursor c = db.query(TBL_NAME_ALL_TRAFFIC, new String[]{"dt,ut"}, "id<?", new String[]{String.valueOf(dayNum+1)}, null, null,null);  
        if(c.getCount() == 0)
        	throw new TrafficDBAccessException("query error");
        return c;  
    }

    
    /**
     * 函数名	->		queryOneDayTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:32:47
     * 描述		->		查询某一天的流量
     * 参数		->		dayNum 本月的号数
     * 返回值	->		Cursor
     */
    public Cursor queryOneDayTraffic(int dayNum) throws Exception{  
        SQLiteDatabase db = getReadableDatabase();  
        Cursor c = db.query(TBL_NAME_ALL_TRAFFIC, new String[]{"dt,ut,date"}, "id=?", new String[]{String.valueOf(dayNum)}, null, null,null);  
        if(c.getCount() == 0)
        	throw new TrafficDBAccessException("query error");
        return c;  
    }  

    /**
     * 函数名	->		updateOneDayTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:34:17
     * 描述		->		更新本日使用的流量
     * 参数		->		values 流量信息
     * 返回值	->		void
     */
    public void updateOneDayTraffic(ContentValues values) throws Exception{
    	SQLiteDatabase db = getWritableDatabase();
    	int res;
    	if(!values.containsKey("id"))
    		res = db.update(TBL_NAME_ALL_TRAFFIC, values, null, null);
    	else {
    		int dayNum = values.getAsInteger("id");
    		res = db.update(TBL_NAME_ALL_TRAFFIC, values, "id=?", new String[]{String.valueOf(dayNum)});
    	}
    	if(res == -1)
    		throw new TrafficDBAccessException("update error");
    }

    /**
     * 函数名	->		updateAppTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:34:50
     * 描述		->		更新某个应用使用的流量
     * 参数		->		values 流量信息
     * 返回值	->		void
     */
    public void updateAppTraffic(ContentValues values) throws Exception{
    	SQLiteDatabase db = getWritableDatabase();
    	int res;
    	String apk = values.getAsString("apk");
    	if(apk == null)
    		res = db.update(TBL_NAME_APP_TRAFFIC, values, null, null);
    	else
    		res = db.update(TBL_NAME_APP_TRAFFIC, values, "apk=?", new String[]{apk});
    	if(res == -1)
    		throw new TrafficDBAccessException("update error");
    }

    /**
     * 函数名	->		delAppTraffic
     * 作者		->		谢健
     * 时间		->		2013-8-6 下午10:44:15
     * 描述		->		删除关于某个卸载应用的流量信息
     * 参数		->		apk 应用包名
     * 返回值	->		void
     */
    public void delAppTraffic(String apk) throws Exception{  
        if (db == null)  
            db = getWritableDatabase();  
        int res = db.delete(TBL_NAME_APP_TRAFFIC, "apk=?", new String[] {apk});  
        if(res == -1)
    		throw new TrafficDBAccessException("delete error");
    } 

    /* (non-Javadoc)
     * detail:
     * 关闭数据库
     * @see android.database.sqlite.SQLiteOpenHelper#close()
     */
    @Override
    public void close() {  
        if (db != null)  
            db.close();  
        super.close();
    }  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }  

}
