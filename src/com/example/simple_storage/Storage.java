package com.example.simple_storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import support.NewListCompanySQL;
import support.NewListDataSQL;
import support.NewListProductSQL;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;




public class Storage extends Activity implements OnClickListener{
	private Button back, addNdelete;
	private ListView company_view;
	private TextView delete_note;
	private String new_company_name;
	private String[] company_list,company_list_ID;
	
	SQLiteDatabase db;
	public String db_name = "CompanySQL";
	public String table_name = "companytable";
	support.NewListCompanySQL helper = new NewListCompanySQL(Storage.this, db_name);
	SQLiteDatabase db_product;
	SQLiteDatabase db_product_detail;
	public String db_product_name = "productSQL";
	public String db_product_table = "producttable";
	public String db_product_detail_name = "product_detailSQL";
	public String db_product_detail_table = "productdetailtable";
	NewListProductSQL product_helper = new NewListProductSQL(Storage.this, db_product_name);
	NewListDataSQL detaildata_helper = new NewListDataSQL(Storage.this, db_product_detail_name);
	
	private Bundle bundle = new Bundle();
	
	private int delete_flag=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage);
		
		db = helper.getReadableDatabase();
		back = (Button)findViewById(R.id.storage_back);
		back.setOnClickListener(this);
		addNdelete = (Button)findViewById(R.id.storage_change);
		addNdelete.setOnClickListener(this);
		delete_note = (TextView)findViewById(R.id.storage_company_note);
		company_view = (ListView)findViewById(R.id.storage_company_view1);
		setlistview();
		
		company_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int whichone,long arg3) {
				// Delete mode
				if(delete_flag==1){ 
					AlertDialog.Builder dialog = new AlertDialog.Builder(Storage.this, R.style.dialog);
			        dialog.setTitle("刪除  "+company_list[whichone] + "  ??");
			        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	AlertDialog.Builder dialog2 = new AlertDialog.Builder(Storage.this, R.style.dialog);
					        dialog2.setTitle("刪除確認");
					        dialog2.setMessage("確定刪除後，這個廠商的資料會全部清除");
					        dialog2.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					            public void onClick(DialogInterface dialog, int whichButton) {
					                delete_company(whichone);
					            }
					                
					       });
					        dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					            public void onClick(DialogInterface dialog, int whichButton) {
					            	//do nothing
					            }
					       });
					       dialog2.show();
			            }   
			       });
			        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	//do nothing
			            }
			       });
			       dialog.show();
			       delete_note.setText("公司清單");
			       delete_flag=0;
				}
				// normal check detail mode
				else{
					String[] target = new String[2];
					target[0] = company_list[whichone];
					target[1] = company_list_ID[whichone];
					bundle.putStringArray("this_company", target);
	            	Intent i = new Intent(Storage.this, Product.class);
	            	i.putExtras(bundle); 
	            	startActivity(i); 
				}
			}
        	
        });
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.storage_back:
				finish();
				break;
			case R.id.storage_change:
				AlertDialog.Builder dialog = new AlertDialog.Builder(Storage.this, R.style.dialog);
		        dialog.setTitle("新增移除");
		        dialog.setItems(R.array.addNdelete_option, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   int choosewhich=which;
		            	   // add new company
		            	   if(choosewhich == 0){
		            		   add_function();
		             	   }
		            	   // Delete company enable
		             	   else if(choosewhich == 1){
		             		   delete_note.setText("選擇移除公司");
		             		   delete_flag=1;
		             	   }
		               }
		        });
		        dialog.show();
		        break;
		}
		
	}
	// ------------------------setlistview company---------------------------------------------------------
	public void setlistview(){
		company_list = myData();
		company_list_ID = myDataID(""); // Search all of them

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Storage.this,
        		R.layout.mytextview,company_list);
		company_view.setAdapter(listAdapter);
    }
	
	public String[] myData(){
    	String quary_title = "select company_name from companytable";
		Cursor cursor = db.rawQuery(quary_title, null);
		String[] sNote = new String[cursor.getCount()];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();   
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); 
		 return sNote;
	}
	public String[] myDataID(String target){
		
		String quary_title = "select _ID from companytable";
		if(target != ""){
			quary_title = "select _ID from companytable where company_name = '" + target + "'";
		}
		Cursor cursor = db.rawQuery(quary_title, null);
		String[] sNote = new String[cursor.getCount()];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();   
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); 
		 return sNote;
	}
	

	//-----------------------------------------------------------------------------------------------
	// ------------------------add company---------------------------------------------------------
	public void add_function(){
		final EditText input = new EditText(Storage.this);
		AlertDialog.Builder add = new AlertDialog.Builder(Storage.this);
		   
		       add.setTitle("新增廠商");
		       add.setMessage("廠商名稱");
		       add.setView(input);
		       add.setPositiveButton("確認", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                new_company_name = input.getText().toString();
		                
		                ContentValues cv = new ContentValues();
		                cv.put("company_name", new_company_name);
		                
		                long long1 = db.insert(table_name, "", cv);
		 	      	   	if (long1 == -1) {
		 	      		   Toast.makeText(Storage.this,"失敗", Toast.LENGTH_SHORT).show();
		 	      	   	}
		 	      	   	else{    
		 	      		   Toast.makeText(Storage.this,"成功", Toast.LENGTH_SHORT).show();
		 	      		   JSONObject tmp = new JSONObject();
		 	      		   String[] new_comapny_ID = myDataID(new_company_name);
		 	      		   if(new_comapny_ID.length > 0 && new_comapny_ID.length <= 1){
		 	      			   tmp = JSON_string_for_backup(new_company_name,new_comapny_ID[0]);
		 	      		   }
		 	      		   try {
		 	      			   input_txt(tmp);
		 	      			   checkfolder_exist(new_company_name);
		 	      		   } catch (IOException e) {
								e.printStackTrace();
		 	      		   }
		 	      		   setlistview();
		 	      		   //checkfolder_exist(new_company_name);
		 	      	   	}
		            }
		                
		       });
		       add.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	//do nothing
		            }
		       });
		       add.show();
	}
	//-----------------------------------------------------------------------------------------------
	// ------------------------delete_company---------------------------------------------------------
	public void delete_company(int pos){
		db_product = product_helper.getReadableDatabase();
		db_product_detail = detaildata_helper.getReadableDatabase();
		
		long long1 = db.delete(table_name, "_ID=" + company_list_ID[pos], null);
		if (long1 != -1) {
			long long12 = db_product.delete(db_product_table, "belongcompanyID=" + company_list_ID[pos], null);
			long long123 = db_product_detail.delete(db_product_detail_table, "belongcompanyID=" + company_list_ID[pos], null);
			if(long12 == -1 || long123 == -1){
				Toast.makeText(Storage.this,"失敗", Toast.LENGTH_SHORT).show();
			}
		}
		db_product.close();
		db_product_detail.close();
		setlistview();

	}
	//-----------------------------------------------------------------------------------------------
	// ------------------------set new folder-----------------------------------------------
	public void checkfolder_exist(String folder_name){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ //確定SD卡可讀寫

			File sdFile = android.os.Environment.getExternalStorageDirectory();
			String path = sdFile.getPath() + File.separator + "simple_storage" + File.separator + folder_name;
	
			File dirFile = new File(path);
	
			if(!dirFile.exists()){//如果資料夾不存在
				dirFile.mkdir();//建立資料夾
			}
		}
	}
	//-----------------------------------------------------------------------------------------------
	
	//--------------------build a main JSON file ---------------------------------------------------------
	 public JSONObject JSON_string_for_backup(String name, String ID){
	    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
			Map<String, String> params_nested = new HashMap<String, String>();
			params_nested.put("ID", ID);
			params_nested.put("name", name);
			//JSONObject json_nested = new JSONObject(params_nested);
			//params.put("schedule", json_nested);
			JSONObject json_f = new JSONObject(params_nested);
			
			return json_f;
	  }
		//-----------------------------------------------------------------------------------------------
	//------------------add to text file----------------------------------------------------------------
	 public void input_txt(JSONObject content) throws IOException{
		 
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		String path = sdFile.getPath() + File.separator + "simple_storage";
		
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
        String p = path + File.separator + "company_list.txt";

		FileOutputStream outputStream = null;
		try {
             outputStream = new FileOutputStream(new File(p), true);
             String msg = new String(content.toString() + ",");
             outputStream.write(msg.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	 }
	//--------------------------------------------------------------------------------------------------
}