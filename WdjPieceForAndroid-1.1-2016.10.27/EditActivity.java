package com.wdjpiece.android;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import java.io.FileReader;
import java.io.FileWriter;

public class EditActivity extends Activity
{
	private Intent intent;
	private String pathname,filename;
	private EditText edittext;
	private AlertDialog.Builder messageDialog;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

		intent=getIntent();
		pathname=intent.getStringExtra("pathname");
		filename=intent.getStringExtra("filename");
		edittext=(EditText)findViewById(R.id.edit);
		messageDialog=new AlertDialog.Builder(EditActivity.this);

		EditActivity.this.setTitle(filename);
		try{
			int ch;String str="";
			FileReader filein=new FileReader(pathname);
			while((ch=filein.read())!=-1)str=str+(char)ch;
			filein.close();edittext.setText(str);
		}
		catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
	}

	//加载菜单
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add("帮助");
		menu.add("保存");
		return true;
	}
	//菜单动作
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getTitle()=="帮助"){
			int ch;String str="";
			try{
				FileReader filein=new FileReader("/mnt/sdcard/Android/data/com.wdjpiece.android/lang/WdjPieceLang");
				while((ch=filein.read())!=-1)str=str+(char)ch;
				filein.close();
			}
			catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
			new DialogResult(this,str,false).show();
		}
		else try{
				FileWriter fileout=new FileWriter(pathname,false);
				fileout.write(edittext.getText().toString());fileout.close();
			}
			catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
		return true;
	}
}
