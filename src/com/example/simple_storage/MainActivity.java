package com.example.simple_storage;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	private Button storage, productin, loading;
	private TextView timeview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		storage = (Button)findViewById(R.id.main_storage_button);
		storage.setOnClickListener(this);
		productin = (Button)findViewById(R.id.main_product_in_button);
		productin.setOnClickListener(this);
		loading = (Button)findViewById(R.id.main_restore);
		loading.setOnClickListener(this);
		
		timeview = (TextView)findViewById(R.id.main_time);
		String time = time_format();
		timeview.setText(time);
		
		checkfolder_exist("simple_storage");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.main_storage_button:
				Intent i = new Intent(this, Storage.class);
				startActivity(i);
				break;
				
			case R.id.main_product_in_button:
				Intent i_buy = new Intent(this, Buy_main_page.class);
				startActivity(i_buy);
				break;
				
			case R.id.main_restore:
				String tmp = readData();
				Log.e("ans",tmp);
				break;
				
		}
		
	}
	
	
	public void checkfolder_exist(String folder_name){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ //確定SD卡可讀寫

			File sdFile = android.os.Environment.getExternalStorageDirectory();
			String path = sdFile.getPath() + File.separator + folder_name;
	
			File dirFile = new File(path);
	
			if(!dirFile.exists()){//如果資料夾不存在
				dirFile.mkdir();//建立資料夾
			}
		}
	}
	
	public static String readData(){
		byte Buffer[] = new byte[1024];
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		String path = sdFile.getPath() + File.separator + "simple_storage" 
		+ File.separator + "company_one" + File.separator + "123.txt";
		//得到文件输入流
		File file = new File(path);
		FileInputStream in = null;
		ByteArrayOutputStream outputStream = null;
		try {
			 in = new FileInputStream(file);
			//读出来的数据首先放入缓冲区，满了之后再写到字符输出流中
			int len = in.read(Buffer);
			//创建一个字节数组输出流
			outputStream = new ByteArrayOutputStream();
			outputStream.write(Buffer,0,len);
			//把字节输出流转String
			return new String(outputStream.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream!=null){
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public String time_format(){
		final Calendar cal = Calendar.getInstance();
      	int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH)+1;
        int Day = cal.get(Calendar.DAY_OF_MONTH);
        
        String month = Integer.toString(Month);
        if(Month < 10){
        	month="0"+month;
        }
        String day = Integer.toString(Day);
        if(Day < 10){
        	day="0"+day;
        }
        String ans = ""+Integer.toString(Year) + " / "+ month + " / " + day;
        return ans;
	}
	
	
	
	
	

}