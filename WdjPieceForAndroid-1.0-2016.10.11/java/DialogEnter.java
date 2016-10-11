package com.wdjpiece.android;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;

public class DialogEnter extends Dialog
{
	public static String DialogEnterStr=null;
	private Handler handler;
	private EditText edittext;
	
	public DialogEnter(Context context,String str)
    {
        super(context);
        setContentView(R.layout.enter);

		DialogEnter.this.setTitle("Enter");
		edittext=(EditText)findViewById(R.id.enter);
		edittext.setText(str);
		
		//取消键
		Button button1=(Button)findViewById(R.id.enterbutton1);
		button1.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				System.exit(0);
			}
		});
		
		//确认键
		Button button2=(Button)findViewById(R.id.enterbutton2);
		button2.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				DialogEnterStr=edittext.getText().toString();
				DialogEnter.this.dismiss();
				handler.sendMessage(handler.obtainMessage());
			}
		});
	}
	
	@Override
	public void show()
	{
		handler=new Handler(){
			@Override
			public void handleMessage(Message mesg){
				throw new RuntimeException();
			}
		};
		
		super.show();
		
		try{Looper.getMainLooper().loop();}
		catch(RuntimeException e){}
	}
}
