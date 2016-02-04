package com.example.simple_storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import support.NewListDataSQL;
import support.NewListProductSQL;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Built_Product extends Activity implements OnClickListener{
	private Button back, confirm;
	private EditText name, number, location, note;
	
	private Bundle bundle;
	private String[] company_nameNid, Product_ID;
	
    SQLiteDatabase db_product;
    SQLiteDatabase db_product_detail;
	public String db_product_name = "productSQL";
	public String db_product_table = "producttable";
	public String db_product_detail_name = "product_detailSQL";
	public String db_product_detail_table = "productdetailtable";
	NewListProductSQL helper = new NewListProductSQL(Built_Product.this, db_product_name);
	NewListDataSQL detaildata_helper = new NewListDataSQL(Built_Product.this, db_product_detail_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_built__product);
		
		bundle = getIntent().getExtras();
		company_nameNid = bundle.getStringArray("this_company");
		
		db_product = helper.getReadableDatabase();
		db_product_detail = detaildata_helper.getReadableDatabase();
		
		back = (Button)findViewById(R.id.build_new_product_back);
		back.setOnClickListener(this);
		confirm = (Button)findViewById(R.id.build_new_product_confirm);
		confirm.setOnClickListener(this);
		
		name = (EditText)findViewById(R.id.build_new_product_name_type);
		number = (EditText)findViewById(R.id.build_new_product_number_type);
		location = (EditText)findViewById(R.id.build_new_product_location_type);
		note = (EditText)findViewById(R.id.build_new_product_note_type);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.build_new_product_back:
			finish();
			break;
		case R.id.build_new_product_confirm:
			if(!name.getText().toString().isEmpty()){
				ContentValues cv = new ContentValues();
				
				if(number.getText().toString().isEmpty()){

	      	   		cv.put("howmany", "0");
				}
				else{
					cv.put("howmany", number.getText().toString());
				}
				cv.put("name", name.getText().toString());
				cv.put("location", location.getText().toString());
				cv.put("note", note.getText().toString());
				cv.put("belongcompany", company_nameNid[0]);
	            cv.put("belongcompanyID", company_nameNid[1]);
	            
	            
	            long long1 = db_product.insert(db_product_table, "", cv);
	      	   	if (long1 == -1) {
	      	   		Toast.makeText(Built_Product.this,"失敗", Toast.LENGTH_SHORT).show();
	      	   	}
	      	   	else{
	      	   		
	      	   		Product_ID = myDataID(name.getText().toString());
	      	   		
		      	   	JSONObject main_string = JSON_string_for_main(Product_ID[0], company_nameNid[0], company_nameNid[1], name.getText().toString()
		            		,location.getText().toString(), note.getText().toString());
		            try {
						input_txt("product_list", main_string);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		      	   	final Calendar cal = Calendar.getInstance();
			      	int Year = cal.get(Calendar.YEAR);
			        int Month = cal.get(Calendar.MONTH)+1;
			        int Day = cal.get(Calendar.DAY_OF_MONTH);
			        int Hour = cal.get(Calendar.HOUR);
			        int Min = cal.get(Calendar.MINUTE);
		      	   	ContentValues cv_detail = new ContentValues();
		      	   	
		      	   	int numberofchange=0;
		      	   	if(number.getText().toString().isEmpty() == false){
		      	   		numberofchange = Integer.parseInt(number.getText().toString());
		      	   		
					}

		      	  	cv_detail.put("name", name.getText().toString());
			      	cv_detail.put("year", Year);
			      	cv_detail.put("month", Month);
			      	cv_detail.put("day", Day);
			      	cv_detail.put("hour", Hour);
			      	cv_detail.put("min", Min);
			      	cv_detail.put("belongcompanyID", company_nameNid[1]);
			      	cv_detail.put("product_ID", Product_ID[0]);
			      	cv_detail.put("howmany", Integer.toString(numberofchange));
			      	cv_detail.put("number_of_change", numberofchange);
			      	cv_detail.put("type_of_change", 0);
			      	long long12 = db_product_detail.insert(db_product_detail_table, "", cv_detail);
		      	   	if (long12 == -1) {
		      		   Toast.makeText(Built_Product.this,"失敗", Toast.LENGTH_SHORT).show();
		      	   	}
		      	   	else{
		      	   		String number_of_product = number.getText().toString();
			      	   	if(number.getText().toString().isEmpty()){
			      	   		number_of_product = "0";
						}

		      	   		JSONObject product_string_detail = JSON_string_for_product(Product_ID[0], company_nameNid[1], name.getText().toString(), 
		      	 			Integer.toString(Year), Integer.toString(Month), Integer.toString(Day), Integer.toString(Hour), 
		      	 			Integer.toString(Min), number_of_product,numberofchange ,0);

			            try {
							input_txt(name.getText().toString(), product_string_detail);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		      		   Toast.makeText(Built_Product.this,"成功", Toast.LENGTH_SHORT).show();
		      		   Intent intent = new Intent(Built_Product.this,Product.class);
		      		   setResult(Activity.RESULT_OK,intent);
		        	   finish();
		      	   	}
	      	   	}
		      	
			}
	        break;
		}
	}
	//--------------------build a main JSON file ---------------------------------------------------------
	 public JSONObject JSON_string_for_main(String ID,String belingcompany, String belingcompanyID, String name, String location, String note){
	    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
			Map<String, String> params_nested = new HashMap<String, String>();
			params_nested.put("ID", ID);
			params_nested.put("belingcompany", belingcompany);
			params_nested.put("belingcompanyID", belingcompanyID);
			params_nested.put("name", name);
			params_nested.put("location", location);
			params_nested.put("note", note);
			//JSONObject json_nested = new JSONObject(params_nested);
			//params.put("schedule", json_nested);
			JSONObject json_f = new JSONObject(params_nested);
			
			return json_f;
	  }
	 
	//--------------------build a product JSON file ---------------------------------------------------------
	 public JSONObject JSON_string_for_product(String ID, String belingcompanyID, String name, 
			 String year, String month, String day, String hour, String min, String howmany
			 , int number_of_change, int type_of_change){
	    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
			Map<String, String> params_nested = new HashMap<String, String>();
			params_nested.put("product_ID", ID);
			params_nested.put("belingcompanyID", belingcompanyID);
			params_nested.put("name", name);
			params_nested.put("year", year);
			params_nested.put("month", month);
			params_nested.put("day", day);
			params_nested.put("hour", hour);
			params_nested.put("min", min);
			params_nested.put("howmany", howmany);
			params_nested.put("number_of_change", Integer.toString(number_of_change));
			params_nested.put("type_of_change", Integer.toString(type_of_change));
			//JSONObject json_nested = new JSONObject(params_nested);
			//params.put("schedule", json_nested);
			JSONObject json_f = new JSONObject(params_nested);
			
			return json_f;
	  }
	 //--------------------------------------------------------------------------------------------------
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
	 //------------------add to text file----------------------------------------------------------------
	 /*public void input_txt(String target, JSONObject content) throws IOException{
		 
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		String path = sdFile.getPath() + File.separator + "simple_storage" + File.separator + company_nameNid[0];
		
		//String path = "simple_storage" + File.separator + company_nameNid[0];
		
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}

        String p = path + File.separator + target + ".txt";
        //String pp = target + ".txt";


		FileOutputStream outputStream = null;
		try {
             //outputStream = new FileOutputStream(new File(p), true);
			outputStream = openFileOutput(p, Context.MODE_PRIVATE);
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
	 }*/
	 //--------------------------------------------------------------------------------------------------
	 
	 //--------------Find Product ID---------------------------------
	 public String[] myDataID(String target){
			

			String quary_title = "select _ID from producttable where name = '" + target + "'";

			Cursor cursor = db_product.rawQuery(quary_title, null);
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
}
