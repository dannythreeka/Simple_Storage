package com.example.simple_storage;

import java.util.ArrayList;

import support.NewListBuySQL;
import support.NewListDataSQL;
import support.NewListProductSQL;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Buy_main_page extends Activity implements OnClickListener{
	private Button back, addNdelete;
	private ListView listview;
	
	SQLiteDatabase db_buy;
	public String db_buy_name = "buySQL";
	public String db_buyt_table = "buytable";
	NewListBuySQL db_buy_helper = new NewListBuySQL(Buy_main_page.this, db_buy_name);
	
	private String[][] post_date;

	
	private static final int ADD_NEW_NOTE = 95277;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_main_page);
		
		db_buy = db_buy_helper.getReadableDatabase();
		
		back = (Button)findViewById(R.id.Buy_main_back);
		back.setOnClickListener(this);
		addNdelete = (Button)findViewById(R.id.Buy_main_change);
		addNdelete.setOnClickListener(this);
		
		listview = (ListView)findViewById(R.id.Buy_main_view1);
		setlistview();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.Buy_main_back:
				finish();
				break;
				
			case R.id.Buy_main_change:
				Intent i_change = new Intent(Buy_main_page.this, Buy_Add_page.class);
				startActivityForResult(i_change, ADD_NEW_NOTE); 
				break;
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ADD_NEW_NOTE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }

       super.onActivityResult(requestCode, resultCode, data);
    }
	
	// ------------------------setlistview company---------------------------------------------------------
	public void setlistview(){
		post_date = myData();
		ArrayList<String> tmp  = build_string_for_butlist(post_date);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Buy_main_page.this,
        		R.layout.mytextviewforbuy,tmp);
        listview.setAdapter(listAdapter);
    }
	
	public String[][] myData(){
    	String quary_title = "select _ID, title, note, year, month, day from buytable";
		Cursor cursor = db_buy.rawQuery(quary_title, null);
		String[][] sNote = new String[6][cursor.getCount()];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();   
			  for(int i=0; i<rows_num; i++){
				  for(int j=0; j<6 ;j++){
					  String strCr = cursor.getString(j);
					  sNote[j][i]=strCr;
				  }
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); 
		 return sNote;
	}
	
	public ArrayList<String> build_string_for_butlist(String[][] data){
		ArrayList<String> ans = new ArrayList<String>();
		
		String[] title = data[1];
		String[] note = data[2];
		String[] year = data[3];
		String[] month = data[4];
		String[] day = data[5];
		
		for(int i=0 ; i<title.length ; i++){
			String s = year[i] + "/" + month[i] + "/" + day[i] + "  [ " + title[i] + " ] " + note[i] + " 。";
			ans.add(s);
		}
		return ans;
	}
	// -------------------------------------------------------------------------------------------------
}
