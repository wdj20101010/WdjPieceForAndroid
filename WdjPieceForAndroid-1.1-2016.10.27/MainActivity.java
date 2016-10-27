package com.wdjpiece.android;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	private GridView maindir;private EditText edittext;
	private AlertDialog.Builder messageDialog;
	File thisfile,copyfile=null;String[] dialogList;boolean isAddFile;
	//----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		//变量初始化
		maindir=(GridView)findViewById(R.id.maindir);
		messageDialog=new AlertDialog.Builder(MainActivity.this);
		//文件点击动作
		maindir.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				public void onItemClick(AdapterView<?> paren,View view,int position,long id){
					LinearLayout layout=(LinearLayout)view;
					TextView text=(TextView)layout.getChildAt(1);
					thisfile=new File(thispath+"/"+text.getText());
					if(thisfile.isDirectory()){
						thispath=thispath+"/"+text.getText();
						showDir(thispath);
					}
					else{
						//执行piece文件
						String str="";int ch;
						try{
							FileReader fr=new FileReader(thisfile);
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
					thisfile=new File(thispath+"/"+text.getText());
					if(thisfile.isDirectory())dialogList=new String[]{"复制","删除","改名"};
					else dialogList=new String[]{"编辑","复制","删除","改名"};
					
					AlertDialog.Builder longClickFileDialog=new AlertDialog.Builder(MainActivity.this);
					longClickFileDialog.setItems(dialogList,new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog,int which){
								if(dialogList[which]=="编辑"){
									//编辑piece文件
									Intent intent=new Intent(MainActivity.this,EditActivity.class);
									intent.putExtra("pathname",thisfile.getPath());
									intent.putExtra("filename",thisfile.getName());
									try{startActivity(intent);}
									catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
								}
								else if(dialogList[which]=="复制"){copyfile=thisfile;}
								else if(dialogList[which]=="删除"){delete(thisfile);showDir(thispath);}
								else if(dialogList[which]=="改名"){rename();}
							}
						});
					longClickFileDialog.show();
					return true;
				}
			});

		//设置工作目录
		try{
			thisfile=new File(rootpath);if(!thisfile.exists())thisfile.mkdirs();
			thisfile=new File(rootpath+"/bin");if(!thisfile.exists())thisfile.mkdirs();
			thisfile=new File(rootpath+"/lang");if(!thisfile.exists())thisfile.mkdirs();
			thisfile=new File(rootpath+"/res");if(!thisfile.exists())thisfile.mkdirs();
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
		menu.add("目录");menu.add("文件");menu.add("粘贴");
		return super.onCreateOptionsMenu(menu);
	}
	//菜单动作
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//新建目录或文件
		if(item.getTitle()=="目录"||item.getTitle()=="文件"){
			AlertDialog.Builder addFileDialog=new AlertDialog.Builder(MainActivity.this);
			if(item.getTitle()=="目录"){addFileDialog.setTitle("新建目录");isAddFile=false;}
			else{addFileDialog.setTitle("新建文件");isAddFile=true;}
			edittext=new EditText(MainActivity.this);
			addFileDialog.setView(edittext);
			addFileDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int which)
					{
						if(edittext.getText().length()>0)
						{
							thisfile=new File(thispath+"/"+edittext.getText());
							if(thisfile.exists()){messageDialog.setMessage("此目录或文件已存在!");messageDialog.show();}
							else if(!isAddFile)thisfile.mkdirs();
							else try{thisfile.createNewFile();}
								catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
							showDir(thispath);
						}
					}});
			addFileDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int which)
					{//取消键无操作
					}});
			addFileDialog.show();
		}
		//粘贴操作
		else if(copyfile!=null){copy(copyfile,new File(thispath));copyfile=null;showDir(thispath);}
		return true;
	}
	//显示文件
	public void showDir(String thispath)
	{
		File[] files=new File(thispath).listFiles();
		List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
		
		File filetemp=null;File[] filetemps=new File[files.length];int x=0;
		for(int i=files.length;i>1;i--)
			for(int j=0;j<i-1;j++)
				if(files[j].compareTo(files[j+1])>0)
					{filetemp=files[j];files[j]=files[j+1];files[j+1]=filetemp;}
		for(int i=0;i<files.length;i++)
			if(files[i].isDirectory())filetemps[x++]=files[i];
		for(int j=0;j<files.length;j++)
			if(files[j].isFile())filetemps[x++]=files[j];
		files=filetemps;
		
		for(File file:files){
			Map<String,Object> item=new HashMap <String,Object>();
			if(file.isDirectory())item.put("itemimage",R.drawable.folder);
			else item.put("itemimage",R.drawable.file);
			item.put("itemtext",file.getName());
			items.add(item);
		}
		SimpleAdapter adapter=new SimpleAdapter(this,items,R.layout.item,new String[]{"itemimage","itemtext"},new int[]{R.id.itemimage,R.id.itemtext});
		maindir.setAdapter(adapter);
		MainActivity.this.setTitle(thispath.substring(rootpath.length()));
	}
	//复制文件
	public void copy(File file1,File file2){
		File file=new File(file2.getParent()+"/"+file2.getName()+"/"+file1.getName());
		if(file.exists()){messageDialog.setMessage("此目录或文件已存在!");messageDialog.show();return;}
		if(file1.isDirectory()){
			String str1=file1.getParent()+"/"+file1.getName();
			String str2=file2.getParent()+"/"+file2.getName();
			if(str2.indexOf(str1)==0){messageDialog.setMessage("不能复制到此目录中!");messageDialog.show();return;}
			file.mkdirs();
			File[] files=file1.listFiles();
			for(File filetemp:files)copy(filetemp,file);
		}
		else{
			String str="";int ch;
			try{
				FileReader fr=new FileReader(file1);
				while((ch=fr.read())!=-1)str=str+(char)ch;fr.close();
				FileWriter fw=new FileWriter(file);
				fw.write(str);fw.close();
			}
			catch(Exception e){messageDialog.setMessage(e.getMessage());messageDialog.show();}
		}
	}
	//删除文件
	public void delete(File file){
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for(File filetemp:files)delete(filetemp);
		}
		file.delete();
	}
	//文件改名
	public void rename(){
		AlertDialog.Builder renameFileDialog=new AlertDialog.Builder(MainActivity.this);
		renameFileDialog.setTitle("修改名字");
		edittext=new EditText(MainActivity.this);
		renameFileDialog.setView(edittext);
		renameFileDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which)
				{
					if(edittext.getText().length()>0)
					{
						File filetemp=new File(thispath+"/"+edittext.getText());
						if(filetemp.exists()){messageDialog.setMessage("此目录或文件已存在!");messageDialog.show();}
						else{thisfile.renameTo(filetemp);showDir(thispath);}
					}
				}});
		renameFileDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which)
				{//取消键无操作
				}});
		renameFileDialog.show();
	}
}
