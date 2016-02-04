package com.example.simple_storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Product_detail extends Activity implements OnClickListener{
	
	private Button back, comfirm, delete;
	private ListView listview;
	private EditText pname, pnumber, plocation, pnote; 
	private TextView delete_message;
	
	private Bundle bundle;
	private String[] company_nameNid, product_info;
	private String[] type_of_change, _ID, number_of_change, number;
	private String product_name, product_id, condition;
	private int lisview_current_position=0, listview_length=0;
	
	SQLiteDatabase db_product;
	SQLiteDatabase db_product_detail;
	public String db_product_name = "productSQL";
	public String db_product_table = "producttable";
	public String db_product_detail_name = "product_detailSQL";
	public String db_product_detail_table = "productdetailtable";
	NewListProductSQL helper = new NewListProductSQL(Product_detail.this, db_product_name);
	NewListDataSQL detaildata_helper = new NewListDataSQL(Product_detail.this, db_product_detail_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		
		db_product = helper.getReadableDatabase();
		db_product_detail = detaildata_helper.getReadableDatabase();
		
		bundle = getIntent().getExtras();
		company_nameNid = bundle.getStringArray("this_company");
		product_name = bundle.getString("product_name");
		product_id = bundle.getString("product_ID");
		condition = bundle.getString("condition");
		
		back = (Button)findViewById(R.id.Product_detail_back);
		back.setOnClickListener(this);
		comfirm = (Button)findViewById(R.id.product_detail_confirm);
		comfirm.setOnClickListener(this);
		delete = (Button)findViewById(R.id.product_detail_delete);
		delete.setOnClickListener(this);
		if(condition.equals("edit")){
			comfirm.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
		}
		pname = (EditText)findViewById(R.id.product_detail_name_type);
		pnumber = (EditText)findViewById(R.id.product_detail_number_type);
		plocation = (EditText)findViewById(R.id.product_detail_location_type);
		pnote = (EditText)findViewById(R.id.product_detail_note_mul);
		listview = (ListView)findViewById(R.id.product_detail_listview1);
		delete_message = (TextView)findViewById(R.id.product_detail_delete_textview);
		
		setBasicview();
		setlistview();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.Product_detail_back:
				Intent intent = new Intent(Product_detail.this,Product.class);
	      		setResult(Activity.RESULT_OK,intent);
				finish();
				break;
				
			case R.id.product_detail_confirm:
				if(!pname.getText().toString().isEmpty()){
					AlertDialog.Builder dialog = new AlertDialog.Builder(Product_detail.this, R.style.dialog);
			        dialog.setTitle("確認更改");
			        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                EditProductData();
			                setBasicview();		        		
			        		setlistview();
			            }
			                
			       });
			        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	//do nothing
			            }
			       });
			       dialog.show();
				}
				
			case R.id.product_detail_delete:

					delete_message.setText("選擇要刪除的項目");
					listview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, final int whichone,long arg3) {
							lisview_current_position=whichone;

								AlertDialog.Builder dialog = new AlertDialog.Builder(Product_detail.this, R.style.dialog);
						        dialog.setTitle("刪除  ??");
						        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
						            public void onClick(DialogInterface dialog, int whichButton) {
						            	AlertDialog.Builder dialog2 = new AlertDialog.Builder(Product_detail.this, R.style.dialog);
								        dialog2.setTitle("刪除確認");
								        dialog2.setMessage("確定刪除後，這一筆資料會全部清除");
								        dialog2.setPositiveButton("確定", new DialogInterface.OnClickListener() {
								            public void onClick(DialogInterface dialog, int whichButton) {
								                delete_product_detail(whichone);
								                setBasicview();
								                setlistview();
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
						       delete_message.setText("");
						       
						}
		        	
		        	});
					
				

		}
		
	}
	
	public void EditProductData(){
		ContentValues cv = new ContentValues();
		
		if(pnumber.getText().toString().isEmpty()){
			cv.put("howmany", "0");
		}
		else{
			cv.put("howmany", pnumber.getText().toString());
		}
		cv.put("name", pname.getText().toString());
		cv.put("location", plocation.getText().toString());
		cv.put("note", pnote.getText().toString());
        
        long long1 = db_product.update(db_product_table, cv, "_ID =" + product_id, null);
  	   	if (long1 == -1) {
  	   		Toast.makeText(Product_detail.this,"失敗1", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{    
      	   	final Calendar cal = Calendar.getInstance();
	      	int Year = cal.get(Calendar.YEAR);
	        int Month = cal.get(Calendar.MONTH)+1;
	        int Day = cal.get(Calendar.DAY_OF_MONTH);
	        int Hour = cal.get(Calendar.HOUR);
	        int Min = cal.get(Calendar.MINUTE);
      	   	ContentValues cv_detail = new ContentValues();
      	   	String current_number= "0";
      	   	if(pnumber.getText().toString().isEmpty()){
      	   		cv_detail.put("howmany", "0");
      	   		cv_detail.put("number_of_change", 0);
			}
			else{
				current_number=pnumber.getText().toString();
				cv_detail.put("howmany", pnumber.getText().toString());
				cv_detail.put("number_of_change", Integer.parseInt(pnumber.getText().toString()));
			}
      	  	cv_detail.put("name", pname.getText().toString());
	      	cv_detail.put("year", Year);
	      	cv_detail.put("month", Month);
	      	cv_detail.put("day", Day);
	      	cv_detail.put("hour", Hour);
	      	cv_detail.put("min", Min);
	      	cv_detail.put("product_ID", product_id);
	      	cv_detail.put("belongcompanyID", company_nameNid[1]);
	      	cv_detail.put("type_of_change", 2);
	      	long long12 = db_product_detail.insert(db_product_detail_table, "", cv_detail);
      	   	if (long12 == -1) {
      		   Toast.makeText(Product_detail.this,"失敗2", Toast.LENGTH_SHORT).show();
      	   	}
      	   	else{
      	   		
	      	   	JSONObject tmp = JSON_string_for_product(product_id, company_nameNid[1], pname.getText().toString(), 
	   	   			   Integer.toString(Year), Integer.toString(Month), Integer.toString(Day), 
	   	   			Integer.toString(Hour), Integer.toString(Min), current_number);
	   	   	   try {
						input_txt(pname.getText().toString(), tmp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
      		   Toast.makeText(Product_detail.this,"成功", Toast.LENGTH_SHORT).show();
      		   /*Intent intent = new Intent(Product_detail.this,Product.class);
      		   setResult(Activity.RESULT_OK,intent);
        	   finish();*/
      	   	}
  	   	}
	}
	//
	//***** input: Listview select position, output: Nope, Function: delete the detail date in DataBase. *****
	//
	public void delete_product_detail(int whichone){
		int real_position = listview_length - whichone;
		String change_type = type_of_change[real_position];


		//***********************
		//	入庫 = 0   出貨 = 1  更正 = 2
		//***********************

		if(change_type.equals("0")){
			delete_mode_0(real_position);
		}
		else if(change_type.equals("1")){
			delete_mode_1(real_position);
		}
		else if(change_type.equals("2")){
			delete_mode_2(real_position);
		}


	}
//***************************************************************************************
	// input: real position in list (listview is reverse list)
	// output: Nope. 
	// Function: Delete detail from Database by Mode 0
	public void delete_mode_0(int real_position){
		String delete_ID = _ID[real_position];
		int current_product_number_in_storage = Integer.parseInt(product_info[1]);
		int typo_number = Integer.parseInt(number_of_change[real_position]);
		int corrected_product_number_in_storage = current_product_number_in_storage - typo_number;

		long flag_delete_mode_0 = db_product_detail.delete(db_product_detail_table, "_ID =" + delete_ID, null);
  	   	if (flag_delete_mode_0 == -1) {
  		   Toast.makeText(Product_detail.this,"刪除失敗", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{

	  	   	ContentValues cv = new ContentValues();	
			cv.put("howmany", Integer.toString(corrected_product_number_in_storage));
	        long flag_update_mode_0 = db_product.update(db_product_table, cv, "_ID =" + product_id, null);
	  	   	if (flag_update_mode_0 == -1) {
	  	   		Toast.makeText(Product_detail.this,"刪除失敗", Toast.LENGTH_SHORT).show();
	  	   	}
	  	   	else{
	  	   		Toast.makeText(Product_detail.this,"成功", Toast.LENGTH_SHORT).show();
	  	   	}
	  	}
	}

	// input: real position in list (listview is reverse list)
	// output: Nope. 
	// Function: Delete detail from Database by Mode 1
	public void delete_mode_1(int real_position){
		String delete_ID = _ID[real_position];
		int current_product_number_in_storage = Integer.parseInt(product_info[1]);
		int typo_number = Integer.parseInt(number_of_change[real_position]);
		int corrected_product_number_in_storage = current_product_number_in_storage + typo_number;

		long flag_delete_mode_1 = db_product_detail.delete(db_product_detail_table, "_ID =" + delete_ID, null);
  	   	if (flag_delete_mode_1 == -1) {
  		   Toast.makeText(Product_detail.this,"刪除失敗", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{

	  	   	ContentValues cv = new ContentValues();	
			cv.put("howmany", Integer.toString(corrected_product_number_in_storage));
	        long flag_update_mode_1 = db_product.update(db_product_table, cv, "_ID =" + product_id, null);
	  	   	if (flag_update_mode_1 == -1) {
	  	   		Toast.makeText(Product_detail.this,"刪除失敗", Toast.LENGTH_SHORT).show();
	  	   	}
	  	   	else{
	  	   		Toast.makeText(Product_detail.this,"成功", Toast.LENGTH_SHORT).show();
	  	   	}
	  	}
	}

	// input: real position in list (listview is reverse list)
	// output: Nope. 
	// Function: Delete detail from Database by Mode 2
	public void delete_mode_2(int real_position){
		String delete_ID = _ID[real_position];

		long flag_delete_mode_2 = db_product_detail.delete(db_product_detail_table, "_ID = " + delete_ID, null);
  	   	if (flag_delete_mode_2 == -1) {
  		   Toast.makeText(Product_detail.this,"刪除失敗", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{
  	   		Toast.makeText(Product_detail.this,"成功", Toast.LENGTH_SHORT).show();
  	   	}

	}
	//*********************************************************************************************************
	//********************************************************* Basic data process ***************************
	public String[] myData(){
    	String quary_title = "select name, howmany, location, note from producttable "
    			+ "where (_ID = "+product_id+")";
    	
		Cursor cursor = db_product.rawQuery(quary_title, null);
		String[] sNote = new String[4];
		  
		int rows_num = cursor.getCount();
		//Log.e("123", Integer.toString(rows_num));
		
			
		if(rows_num != 0) {
			  cursor.moveToFirst(); 
			  for(int i=0; i<4; i++){
				  String strCr = cursor.getString(i);
				  sNote[i]=strCr;
				  //Log.e("123", strCr);
			  }

		 }
		 cursor.close(); 
		 return sNote;
	}
	public void setBasicview(){
		product_info=myData();
		pname.setText(product_info[0]);
		pnumber.setText(product_info[1]);
		plocation.setText(product_info[2]);
		pnote.setText(product_info[3]);
	}
	//*******************************************************************************************************
	//********************************************************* ListView data process ***************************
	public String[][] myData_detail(){
    	String quary_title = "select name, howmany, year, month, day, hour, min, "
    			+ "number_of_change, type_of_change, _ID from productdetailtable "
    			+ "where product_ID = '"+ product_id +"'";
    	
		Cursor cursor = db_product_detail.rawQuery(quary_title, null);
		String[][] sNote = new String[10][cursor.getCount()];
		  
		int rows_num = cursor.getCount();
			
		if(rows_num != 0) {
			  cursor.moveToFirst(); 
			  for(int j=0 ; j<rows_num ; j++){
				  for(int i=0; i<10; i++){
					  if(i <= 1){
						  String strCr = cursor.getString(i);
						  sNote[i][j]=strCr;
					  }
					  else{
						  String strCr = Integer.toString(cursor.getInt(i));
						  sNote[i][j]=strCr;
					  }
				  }
				  cursor.moveToNext();
			  }

		 }
		 cursor.close(); 
		 return sNote;
	}
	
	public ArrayList<String> builtStringForListView(){
		String[][] temp = myData_detail();
		String[] name = temp[0];
		number = temp[1];
		String[] year = temp[2];
		String[] month = temp[3];
		String[] day = temp[4];
		String[] hour = temp[5];
		String[] min = temp[6];
		number_of_change = temp[7];
		type_of_change = temp[8]; //for delete fuction. easy to track the data
		_ID = temp[9]; //for delete fuction. easy to track the data

		String blank = "  ";
		String end = " 個。";

		listview_length = name.length-1;  // for delete function, because the list is reverse order.  listview length - 1 - whichone = real position.

		ArrayList<String> ans = new ArrayList<String>();
		for(int i=name.length-1 ; i>=0 ; i=i-1){
			if(min[i].equals("0")){
				min[i]="00";
			}
			StringBuilder string_show = new StringBuilder();
			string_show.append("["+ year[i] + "/ " + month[i] + "/ " + day[i] + "--" + hour[i] + ":" + min[i] + "]"+ blank);
			//***********************
			//	入庫 = 0   出貨 = 1  更正 = 2
			//***********************
			if(type_of_change[i].equals("0")){
				string_show.append("入庫 " + number_of_change[i] + end);
			}
			else if(type_of_change[i].equals("1")){
				string_show.append("出貨 " + number_of_change[i] + end);
			}
			else if(type_of_change[i].equals("2")){
				string_show.append("更正 " + number_of_change[i] + end);
			}
			
			string_show.append(" 目前庫存: " + number[i] + end);
			/*String s = "["+ year[i] + "/ " + month[i] + "/ " + day[i] + " -- " + hour[i] + ":" + min[i] + "]"+ blank
					+ number[i] + end;*/
			ans.add(string_show.toString());
		}
		
		return ans;
	}
	
	public void setlistview(){
		ArrayList<String> product_show_on = builtStringForListView();

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Product_detail.this,
        		R.layout.mytextviewforproduct,product_show_on);
        listview.setAdapter(listAdapter);
    }
	
	//--------------------build a product JSON file ---------------------------------------------------------
		 public JSONObject JSON_string_for_product(String ID, String belingcompanyID, String name, 
				 String year, String month, String day, String hour, String min, String howmany){
		    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
				Map<String, String> params_nested = new HashMap<String, String>();
				//params_nested.put("ID", ID);
				params_nested.put("product_ID", ID);
				params_nested.put("belingcompanyID", belingcompanyID);
				params_nested.put("name", name);
				params_nested.put("year", year);
				params_nested.put("month", month);
				params_nested.put("day", day);
				params_nested.put("hour", hour);
				params_nested.put("min", min);
				params_nested.put("howmany", howmany);
				params_nested.put("number_of_change", howmany);
				params_nested.put("type_of_change", "2");
				//JSONObject json_nested = new JSONObject(params_nested);
				//params.put("schedule", json_nested);
				JSONObject json_f = new JSONObject(params_nested);
				
				return json_f;
		  }
		 //--------------------------------------------------------------------------------------------------
		 //------------------add to text file----------------------------------------------------------------
		 public void input_txt(String target, JSONObject content) throws IOException{
			 
			File sdFile = android.os.Environment.getExternalStorageDirectory();
			String path = sdFile.getPath() + File.separator + "simple_storage" + File.separator + company_nameNid[0];
			
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
	        String p = path + File.separator + target + ".txt";


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
