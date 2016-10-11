package com.wdjpiece.android;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.content.Context;

public class DialogResult extends Dialog
{
	public DialogResult(Context context,String str)
	{
		super(context);
		setContentView(R.layout.result);
		
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
}
