package support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
 
public class NewListDataSQL extends SQLiteOpenHelper {
 
         //資料庫版本關係到App更新時，資料庫是否要調用onUpgrade()
  private static final int VERSION = 1;//資料庫版本  
   
                //建構子
  public NewListDataSQL(Context context, String name, CursorFactory factory,int version) {
   super(context, name, factory, version);
  }
 
  public NewListDataSQL(Context context,String name) { 
   this(context, name, null, VERSION); 
   } 
   
   public NewListDataSQL(Context context, String name, int version) {  
    this(context, name, null, version);  
      }  
    
  //輔助類建立時運行該方法
  @Override
  public void onCreate(SQLiteDatabase db) {
   String DATABASE_CREATE_TABLE =
     "create table productdetailtable("
       + "_ID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
    		 + "name VARCHAR,"
             + "year INT,"
             + "month INT,"
             + "day INT,"
             + "hour INT,"
             + "min INT,"
             + "howmany VARCHAR,"
             + "product_ID VARCHAR,"
             + "belongcompanyID VARCHAR,"
             + "number_of_change INT,"
             + "type_of_change INT"
         + ")";
            db.execSQL(DATABASE_CREATE_TABLE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
   //oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
   db.execSQL("DROP TABLE IF EXISTS newtable"); //刪除舊有的資料表
   onCreate(db);
  }
   
  @Override   
   public void onOpen(SQLiteDatabase db) {     
           super.onOpen(db);       
           // TODO 每次成功打開數據庫後首先被執行     
       } 
 
   @Override
        public synchronized void close() {
            super.close();
        }
 
}