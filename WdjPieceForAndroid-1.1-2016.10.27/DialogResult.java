package com.wdjpiece.android;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;

public class DialogResult extends Dialog
{
	private Handler handler;
	private boolean clog;
	
	public DialogResult(Context context,String str)
	{
		super(context);
		setContentView(R.layout.result);
		this.clog=true;init(str);
	}
	
	public DialogResult(Context context,String str,boolean clog)
	{
		super(context);
		setContentView(R.layout.result);
		this.clog=clog;init(str);
	}
	
	private void init(String str)
	{
		DialogResult.this.setTitle("Result");
		EditText edittext=(EditText)findViewById(R.id.result);
		edittext.setText(str);
		Button button=(Button)findViewById(R.id.resultbutton);
		button.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v){
					DialogResult.this.dismiss();
				}
			});
	}
	
	@Override
	public void show()
	{
		if(clog){
			handler=new Handler(){
				@Override
				public void handleMessage(Message mesg){
					throw new RuntimeException();
				}
			};
		}
		super.show();
		if(clog){
			try{Looper.getMainLooper().loop();}
			catch(RuntimeException e){}
		}
	}

	@Override
	public void dismiss()
	{
		super.dismiss();
		if(clog){
			handler.sendMessage(handler.obtainMessage());
		}
	}
}
