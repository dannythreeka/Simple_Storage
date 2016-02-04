package com.example.simple_storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import support.NewListBuySQL;
import support.NewListProductSQL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Buy_Add_page extends Activity implements OnClickListener{
	
	private Button back, confirm;
	private EditText title, note;
	private TextView time;
	
	private int Byear, Bmonth, Bday;
	
	SQLiteDatabase db_buy;
	public String db_buy_name = "buySQL";
	public String db_buyt_table = "buytable";
	NewListBuySQL db_buy_helper = new NewListBuySQL(Buy_Add_page.this, db_buy_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy__add_page);
		
		db_buy = db_buy_helper.getReadableDatabase();
		
		back = (Button)findViewById(R.id.Buy_add_back);
		back.setOnClickListener(this);
		confirm = (Button)findViewById(R.id.Buy_add_confirm);
		confirm.setOnClickListener(this);
		
		time = (TextView)findViewById(R.id.Buy_add_time);
		time.setOnClickListener(this);
		String tmp = time_format();
		time.setText(tmp);
		
		title = (EditText)findViewById(R.id.Buy_add_title_type);
		note = (EditText)findViewById(R.id.Buy_add_note_mul);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.Buy_add_back:
				finish();
				break;
				
			case R.id.Buy_add_time:
				showDatePickerDialog(); 
				break;
				
			case R.id.Buy_add_confirm:
				if(!title.getText().toString().isEmpty()){
					AlertDialog.Builder dialog = new AlertDialog.Builder(Buy_Add_page.this);
			        dialog.setTitle("確認新增");
			        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                addNewNote();
			                Intent intent = new Intent(Buy_Add_page.this,Buy_main_page.class);
			                setResult(Activity.RESULT_OK,intent);
							finish();
			            }
			                
			       });
			        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	//do nothing
			            }
			       });
			       dialog.show();
				}
			    break;
				
		}
		
	}
	
	public String time_format(){
		final Calendar cal = Calendar.getInstance();
      	Byear = cal.get(Calendar.YEAR);
        Bmonth = cal.get(Calendar.MONTH)+1;
        Bday = cal.get(Calendar.DAY_OF_MONTH);
        
        String month = Integer.toString(Bmonth);
        String day = Integer.toString(Bday);
        String ans = ""+Integer.toString(Byear) + " / "+ month + " / " + day;
        return ans;
	}
	//---------------------------------------------------------------------------------------------------------------
	//-----------------showDatePickerDialog for TEXTVIEW---------------------------------------------------------
	public void showDatePickerDialog() {  
    	
	  	  DatePickerDialog dpd = new DatePickerDialog(this,  
	  	    new DatePickerDialog.OnDateSetListener() { 
	  		  @Override
	  	     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
	  			  Byear=year;
	  			  Bmonth=monthOfYear+1;
	  			  Bday=dayOfMonth;
	  			time.setText(new StringBuilder().append(Byear).append(" / ").
	  		      	      append(Bmonth).append(" / ").append(Bday));
	  	     }  
	  	    }, Byear, Bmonth-1, Bday);
	  	  dpd.show();
	 } 
	//---------------------------------------------------------------------------------------------------------------
	
	public void addNewNote(){
		ContentValues cv = new ContentValues();
		
		cv.put("year", Integer.toString(Byear));
		cv.put("month", Integer.toString(Bmonth));
        cv.put("day", Integer.toString(Bday));
		cv.put("title", title.getText().toString());
		cv.put("note", note.getText().toString());
		
        
        
        long long1 = db_buy.insert(db_buyt_table, "", cv);
  	   	if (long1 == -1) {
  	   		Toast.makeText(Buy_Add_page.this,"失敗", Toast.LENGTH_SHORT).show();
  	   	}
  	   	else{
  	   		
      	   	JSONObject main_string = JSON_string_for_main(title.getText().toString(), note.getText().toString(),
      	   		Integer.toString(Byear), Integer.toString(Bmonth), Integer.toString(Bday));
            try {
				input_txt("product_list", main_string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  	   	}
	}
	
	
	//--------------------build a main JSON file ---------------------------------------------------------
		 public JSONObject JSON_string_for_main(String title, String note, String year, String month, String day){
		    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
				Map<String, String> params_nested = new HashMap<String, String>();
				params_nested.put("title", title);
				params_nested.put("note", note);
				params_nested.put("yeay", year);
				params_nested.put("month", month);
				params_nested.put("day", day);
				JSONObject json_f = new JSONObject(params_nested);
				
				return json_f;
		  }
	//------------------add to text file----------------------------------------------------------------
	 public void input_txt(String target, JSONObject content) throws IOException{
		 
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		String path = sdFile.getPath() + File.separator + "simple_storage" + File.separator + "Buy_In";
		
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}

        String p = path + File.separator + "but_in_note.txt";

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
