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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Product extends Activity implements OnClickListener{
	private Button back, addNdelete, search;
	private TextView delete_note, search_result;
	private ListView product_list;
	private EditText search_target;
	private int lisview_current_position=0;
	
	private Bundle bundle;
	private String[] company_nameNid, product_name, product_number, product_id;
	
	SQLiteDatabase db_product;
	SQLiteDatabase db_product_detail;
	public String db_product_name = "productSQL";
	public String db_product_table = "producttable";
	public String db_product_detail_name = "product_detailSQL";
	public String db_product_detail_table = "productdetailtable";
	NewListProductSQL helper = new NewListProductSQL(Product.this, db_product_name);
	NewListDataSQL detaildata_helper = new NewListDataSQL(Product.this, db_product_detail_name);
	
	private int delete_flag=0, listview_position_flag=0;
	/*   listview_position_flag 1 ==> go to the current(import, export and change info);
		 listview_position_flag 0 ==> go to TOP (0) position;
		 delete_flag 0 ==> normal mode
		 delete_flag 1 ==> delete mode
	 */
	 
	
	private static final int ADD_product = 456;
	private static final int Edit_product = 9527;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product);
		
		bundle = getIntent().getExtras();
		company_nameNid = bundle.getStringArray("this_company");
		
		db_product = helper.getReadableDatabase();
		db_product_detail = detaildata_helper.getReadableDatabase();
		
		back = (Button)findViewById(R.id.product_back);
		back.setOnClickListener(this);
		addNdelete = (Button)findViewById(R.id.product_change);
		addNdelete.setOnClickListener(this);
		search = (Button)findViewById(R.id.product_search_button);
		search.setOnClickListener(this);
		search_result = (TextView)findViewById(R.id.product_resulttext);
		search_target = (EditText)findViewById(R.id.product_searchedit);
		search_target.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				find_this_product();
				return false;
			}
		});;

		delete_note = (TextView)findViewById(R.id.product_note);
		product_list = (ListView)findViewById(R.id.product_listview1);
		setlistview();
		
		
		product_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int whichone,long arg3) {
				lisview_current_position=whichone;

				if(delete_flag==1){
					AlertDialog.Builder dialog = new AlertDialog.Builder(Product.this, R.style.dialog);
			        dialog.setTitle("刪除  "+product_name[whichone] + "  ??");
			        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	AlertDialog.Builder dialog2 = new AlertDialog.Builder(Product.this, R.style.dialog);
					        dialog2.setTitle("刪除確認");
					        dialog2.setMessage("確定刪除後，這個廠商的資料會全部清除");
					        dialog2.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					            public void onClick(DialogInterface dialog, int whichButton) {
					                delete_product(whichone);
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
			       delete_note.setVisibility(View.INVISIBLE);
			       delete_flag=0;
				}
				else{
					AlertDialog.Builder dialog = new AlertDialog.Builder(Product.this, R.style.dialog);
			        dialog.setTitle("選擇  "+ product_name[whichone]);
			        dialog.setItems(R.array.edit_product_option, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			            	   int choosewhich=which;
			            	   if(choosewhich == 0){
			            		   export_product_number_function(product_id[whichone], whichone);
			            		   listview_position_flag=1;
			             	   }
			            	   else if(choosewhich == 1){
			            		   import_product_number_function(product_id[whichone], whichone);
			            		   listview_position_flag=1;
			             	   }
			             	   else if(choosewhich == 2){
			             		  listview_position_flag=1;
			             		  Intent i_check = new Intent(Product.this, Product_detail.class);
			            		  Bundle b_edit_product = new Bundle();
			            		  b_edit_product.putStringArray("this_company", company_nameNid);
			            		  b_edit_product.putString("product_name", product_name[whichone]);
			            		  b_edit_product.putString("product_ID", product_id[whichone]);
			            		  b_edit_product.putString("condition", "check");
			            		  i_check.putExtras(b_edit_product);
				                  startActivity(i_check); 
			             	   }
			             	   else if(choosewhich == 3){
			             		  listview_position_flag=1;
			             		  Intent i_change = new Intent(Product.this, Product_detail.class);
			            		  Bundle b_edit_product = new Bundle();
			            		  b_edit_product.putStringArray("this_company", company_nameNid);
			            		  b_edit_product.putString("product_name", product_name[whichone]);
			            		  b_edit_product.putString("product_ID", product_id[whichone]);
			            		  b_edit_product.putString("condition", "edit");
			            		  i_change.putExtras(b_edit_product);
				                  startActivityForResult(i_change, Edit_product); 
			             	   }
			               }
			        });
			        dialog.show();
				}
			}
        	
        });
		
		/*
		 product_list.setOnScrollListener(new OnScrollListener() {

			// 滚动状态改变时调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 不滚动时保存当前滚动到的位置
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					lisview_current_position = product_list.getFirstVisiblePosition();

				}
			}

			// 滚动时调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}


		});
		
		 */
		
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.product_back:
			finish();
			break;
			
		case R.id.product_change:
			AlertDialog.Builder dialog = new AlertDialog.Builder(Product.this, R.style.dialog);
	        dialog.setTitle("新增移除");
	        dialog.setItems(R.array.addNdelete_option, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   int choosewhich=which;
	            	   if(choosewhich == 0){
	            		   Intent i = new Intent(Product.this, Built_Product.class);
	            		   Bundle b_add_new_product = new Bundle();
	            		   b_add_new_product.putStringArray("this_company", company_nameNid);
	            		   i.putExtras(b_add_new_product);
		                   startActivityForResult(i, ADD_product); 
	             	   }
	             	   else if(choosewhich == 1){
	             		   delete_note.setVisibility(View.VISIBLE);
	             		   delete_flag=1;
	             	   }
	               }
	        });
	        dialog.show();
	        break;
	        
		case R.id.product_search_button:
			find_this_product();
			break;
			
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ADD_product) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }
	    else if (requestCode == Edit_product) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }
       super.onActivityResult(requestCode, resultCode, data);
     }
	public void setlistview(){
		String[][] temp = myData();
		product_name = temp[0];
		product_number = temp[1];
		product_id = temp[2];
		ArrayList<String> product_show_on = build_product_nameNnumber(product_name, product_number);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Product.this,
        		R.layout.mytextviewforproduct,product_show_on);
        product_list.setAdapter(listAdapter);
        if(listview_position_flag == 1){
        	product_list.setSelection(lisview_current_position);
        	listview_position_flag=0;
        }
    }
	
	public String[][] myData(){
    	String quary_title = builtString();
    	
		Cursor cursor = db_product.rawQuery(quary_title, null);
		String[][] sNote = new String[3][cursor.getCount()];
		  
		int rows_num = cursor.getCount();
			
		if(rows_num != 0) {
			  cursor.moveToFirst(); 
			  for(int j=0 ; j<rows_num ; j++){
				  for(int i=0; i<3; i++){
					  String strCr = cursor.getString(i);
					  sNote[i][j]=strCr;
				  }
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); 
		 return sNote;
	}
	public String builtString(){
		String str1 = "select name, howmany, _ID from producttable where belongcompanyID = ";
		String str2 = " AND belongcompanyID = ";
		String str3 = ")";
		String final_ans = str1 + company_nameNid[1] + " order by name";
		
		return final_ans;
		
	}
	public ArrayList<String> build_product_nameNnumber(String[] name, String[] number){
		ArrayList<String> ans = new ArrayList<String>();
		String blank = "      , ";
		for(int i=0 ; i<name.length ; i++){
			String tmp  = name[i]+blank+number[i] + "  個。";
			ans.add(tmp);
		}
		
		return ans;
	}
	
	public void export_product_number_function(final String ID, final int pos){
		final EditText input = new EditText(Product.this);
		AlertDialog.Builder add = new AlertDialog.Builder(Product.this);
		   
		       add.setTitle("出貨數量");
		       add.setMessage("貨品： " + product_name[pos] + " , 目前： "+product_number[pos]+" 個。");
		       add.setView(input);
		       add.setPositiveButton("確認", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                String export_total_number = input.getText().toString();
		                int current_total = Integer.parseInt(product_number[pos]);
		                int export_total = Integer.parseInt(export_total_number);
		                String next_total = Integer.toString(current_total-export_total);
		                EditNumber(next_total, ID, pos,1,export_total);
		                
		            }
		                
		       });
		       add.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	//do nothing
		            }
		       });
		       
		       add.show();
		       
		       
	}
	
	public void import_product_number_function(final String ID, final int pos){
		final EditText input = new EditText(Product.this);
		AlertDialog.Builder add = new AlertDialog.Builder(Product.this);
		   
		       add.setTitle("入庫數量");
		       add.setMessage("貨品： " + product_name[pos] + " , 目前： "+product_number[pos]+" 個。");
		       add.setView(input);
		       add.setPositiveButton("確認", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                String export_total_number = input.getText().toString();
		                int current_total = Integer.parseInt(product_number[pos]);
		                int import_total = Integer.parseInt(export_total_number);
		                String next_total = Integer.toString(current_total+import_total);
		                EditNumber(next_total, ID, pos, 0, import_total);
		                
		            }
		                
		       });
		       add.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	//do nothing
		            }
		       });
		       add.show();
	}
	
	public void EditNumber(String current_number, String ID, int pos, int type_of_change, int number_of_change){
		ContentValues cv = new ContentValues();
		
		if(current_number.isEmpty()){
			cv.put("howmany", "0");
		}
		else{
			cv.put("howmany", current_number);
		}
        
        long long1 = db_product.update(db_product_table, cv, "_ID=" + ID, null);
  	   	if (long1 == -1) {
  	   		Toast.makeText(Product.this,"失敗1", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{    
      	   	final Calendar cal = Calendar.getInstance();
	      	int Year = cal.get(Calendar.YEAR);
	        int Month = cal.get(Calendar.MONTH)+1;
	        int Day = cal.get(Calendar.DAY_OF_MONTH);
	        int Hour = cal.get(Calendar.HOUR);
	        int Min = cal.get(Calendar.MINUTE);
      	   	ContentValues cv_detail = new ContentValues();
      	   	
      	   	
      	   	if(current_number.isEmpty()){
      	   		current_number="0";
      	   		cv_detail.put("howmany", "0");
			}
			else{
				cv_detail.put("howmany", current_number);
			}
      	  	cv_detail.put("name", product_name[pos]);
	      	cv_detail.put("year", Year);
	      	cv_detail.put("month", Month);
	      	cv_detail.put("day", Day);
	      	cv_detail.put("hour", Hour);
	      	cv_detail.put("min", Min);
	      	cv_detail.put("product_ID", ID);
	      	cv_detail.put("belongcompanyID", company_nameNid[1]);
	      	cv_detail.put("number_of_change", number_of_change);
	      	cv_detail.put("type_of_change", type_of_change);
	      	long long12 = db_product_detail.insert(db_product_detail_table, "", cv_detail);
      	   	if (long12 == -1) {
      		   Toast.makeText(Product.this,"失敗2", Toast.LENGTH_SHORT).show();
      	   	}
      	   	else{ 
      	   	   JSONObject tmp = JSON_string_for_product(ID, company_nameNid[1], product_name[pos], 
      	   			   Integer.toString(Year), Integer.toString(Month), Integer.toString(Day), 
      	   			Integer.toString(Hour), Integer.toString(Min), current_number, number_of_change, type_of_change);
      	   	   try {
					input_txt(product_name[pos], tmp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
      		   Toast.makeText(Product.this,"成功", Toast.LENGTH_SHORT).show();
      		   setlistview();
      	   	}
  	   	}
	}
	
	public void delete_product(int pos){
		
		long long12 = db_product.delete(db_product_table, "_ID=" + product_id[pos], null);
		long long123 = db_product_detail.delete(db_product_detail_table, "product_id=" + product_id[pos], null);
		if(long12 == -1 || long123 == -1){
			Toast.makeText(Product.this,"失敗", Toast.LENGTH_SHORT).show();
		}
		setlistview();
	}
	
	public void find_this_product(){
		String tmp = search_target.getText().toString();
		search_result.setText("沒有此貨品。");
		for(int i=0 ; i <product_name.length ; i++){
			if(tmp.equals(product_name[i])){
				search_result.setText(product_number[i]+" 個。");
				break;
			}
		}
	}
	 
	//--------------------build a product JSON file ---------------------------------------------------------
	 public JSONObject JSON_string_for_product(String ID, String belingcompanyID, String name, 
			 String year, String month, String day, String hour, String min, String howmany
			 , int number_of_change, int type_of_change){
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
			params_nested.put("number_of_change", Integer.toString(number_of_change));
			params_nested.put("type_of_change", Integer.toString(type_of_change));
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
