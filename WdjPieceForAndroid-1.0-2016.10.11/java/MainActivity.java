package com.wdjpiece.android;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.io.FileReader;
import android.content.DialogInterface;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.content.Intent;

public class MainActivity extends Activity 
{
	//主要变量
	private String rootpath="/mnt/sdcard/Android/data/com.wdjpiece.android";
	private String thispath=rootpath;
	private GridView maindir;
	private AlertDialog.Builder messageDialog,addFileDialog,longClickFileDialog;
	File file;String[] str;boolean isAddFile;EditText edittext;
	//----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		//变量初始化
		maindir=(GridView)findViewById(R.id.maindir);
		messageDialog=new AlertDialog.Builder(MainActivity.this);
		addFileDialog=new AlertDialog.Builder(MainActivity.this);
		longClickFileDialog=new AlertDialog.Builder(MainActivity.this);
		//文件点击动作
		maindir.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> paren,View view,int position,long id){
				LinearLayout layout=(LinearLayout)view;
				TextView text=(TextView)layout.getChildAt(1);
				file=new File(thispath+"/"+text.getText());
				if(file.isDirectory()){
					thispath=thispath+"/"+text.getText();
					showDir(thispath);
				}
				else{
					//执行piece文件
					String str="";int ch;
					try{
						FileReader fr=new FileReader(file);
						while((ch=fr.read())!=-1)str=str+(char)ch;fr.close();
					}
					catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
					new PrePiece(MainActivity.this,str);new Piece(MainActivity.this);
				}
			}
		});
		//文件长按动作
		maindir.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> paren,View view,int position,long id){
				LinearLayout layout=(LinearLayout)view;
				TextView text=(TextView)layout.getChildAt(1);
				file=new File(thispath+"/"+text.getText());
				
				if(file.isDirectory())str=new String[]{"删除"};
				else str=new String[]{"编辑","删除"};
				longClickFileDialog.setItems(str,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int which){
						if(str[which]=="编辑"){
							//编辑piece文件
							Intent intent=new Intent(MainActivity.this,EditActivity.class);
							intent.putExtra("pathname",file.getPath());
							intent.putExtra("filename",file.getName());
							try{startActivity(intent);}
							catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
						}
						else{file.delete();showDir(thispath);}
					}
				});
				longClickFileDialog.show();
				return true;
			}
		});
		
		//设置工作目录
		try{
			file=new File(rootpath);if(!file.exists())file.mkdirs();
			file=new File(rootpath+"/bin");if(!file.exists())file.mkdirs();
			file=new File(rootpath+"/lang");if(!file.exists())file.mkdirs();
			file=new File(rootpath+"/res");if(!file.exists())file.mkdirs();
		}
		catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
		showDir(thispath);
    }
	
	@Override
    public void onBackPressed(){
		if(thispath.equals(rootpath))super.onBackPressed();
		else{
			thispath=thispath.substring(0,thispath.lastIndexOf('/'));
			showDir(thispath);
		}
	}
	//----------------------------
	//加载菜单
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add("目录");
		menu.add("文件");
		return true;
	}
	//菜单动作
	public boolean onOptionsItemSelected(MenuItem item)
	{
		isAddFile=false;
		if(item.getTitle()=="目录")addFileDialog.setTitle("新建目录");
		else{addFileDialog.setTitle("新建文件");isAddFile=true;}
		edittext=new EditText(MainActivity.this);
		addFileDialog.setView(edittext);
		addFileDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which)
				{
					if(edittext.getText().length()>0)
					{
						file=new File(thispath+"/"+edittext.getText());
						if(file.exists()){messageDialog.setMessage("此目录或文件已存在!");messageDialog.show();}
						else if(!isAddFile)file.mkdirs();
						else try{file.createNewFile();}
							 catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
						showDir(thispath);
					}
				}});
		addFileDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which)
				{//取消键无操作
				}});
		addFileDialog.show();
		return true;
	}
	//显示文件
	public boolean showDir(String thispath)
	{
		File[] files=new File(thispath).listFiles();
		List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
		for(File file:files)
		{
			Map<String,Object> item=new HashMap <String,Object>();
			if(file.isDirectory())item.put("itemimage",R.drawable.folder);
			else item.put("itemimage",R.drawable.file);
			item.put("itemtext",file.getName());
			items.add(item);
		}
		SimpleAdapter adapter=new SimpleAdapter(this,items,R.layout.item,new String[]{"itemimage","itemtext"},new int[]{R.id.itemimage,R.id.itemtext});
		maindir.setAdapter(adapter);
		MainActivity.this.setTitle(thispath.substring(rootpath.length()));
		return true;
	}
}
