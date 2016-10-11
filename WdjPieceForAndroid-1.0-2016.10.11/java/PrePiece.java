package com.wdjpiece.android;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;

public class PrePiece
{
	private ArrayList<String> 源码名=new ArrayList<String>();
	private ArrayList<String> 源码段=new ArrayList<String>();
	private AlertDialog.Builder messageDialog;

	public PrePiece(Context context,String str){
		messageDialog=new AlertDialog.Builder(context);
		try{mainOperate(pretreatOperate(str));saveOperate();}
		catch(Exception e){messageDialog.setMessage(e.getMessage()).show();}
	}

	//代码预先处理，代码规整化
	private String pretreatOperate(String str)throws Exception{
		if(str.length()==0)return "";
		//去除制表符
		str=" "+str.replace('\t', ' ')+" ";
		//处理enter{}语句
		int X=-1;
		int A=str.indexOf(" enter "),B=str.indexOf(" enter\n"),C=str.indexOf(" enter{");
		int D=str.indexOf("\nenter "),E=str.indexOf("\nenter\n"),F=str.indexOf("\nenter{");
		if(A>X)X=A;if(B>X)X=B;if(C>X)X=C;if(D>X)X=D;if(E>X)X=E;if(F>X)X=F;
		if(A!=-1&&A<X)X=A;if(B!=-1&&B<X)X=B;if(C!=-1&&C<X)X=C;
		if(D!=-1&&D<X)X=D;if(E!=-1&&E<X)X=E;if(F!=-1&&F<X)X=F;
		if(X!=-1){
			char ch;int Y=X;
			while(Y<str.length()&&(ch=str.charAt(Y++))!='{');
			if(Y==str.length())throw new Exception("enter{}语句错误");
			int Z=Y,大括号=1;
			while(Z<str.length()&&大括号!=0){ch=str.charAt(Z++);if(ch=='{')大括号++;else if(ch=='}')大括号--;}
			if(大括号!=0)throw new Exception("enter{}语句错误");
			String strx=str.substring(0, X);
			String stry=str.substring(Y, Z-1);
			String strz=str.substring(Z, str.length());
			return pretreatOperate(strx)+" enter{"+stry+"} "+pretreatOperate(strz);
		}
		//处理双引号" "语句
		if(str.indexOf("\"")!=-1){
			String strx="",stry="",strz="";
			strx=str.substring(0,str.indexOf("\""));
			stry=str.substring(str.indexOf("\"")+1);
			if(stry.indexOf("\"")!=-1){
				strz=stry.substring(stry.indexOf("\"")+1);
				stry=stry.substring(0,stry.indexOf("\""));
			}
			return pretreatOperate(strx)+"\""+stry+"\" "+pretreatOperate(strz);
		}
		//去除回车符和注释
		char ch;String str1="",str2="",str3="";
		if(str.length()>0)str=str+'\n';
		for(int i=0;i<str.length();i++)
			if((ch=str.charAt(i))!='\n')str1=str1+ch;
			else{if(str1.indexOf("//")!=-1)str1=str1.substring(0, str1.indexOf("//"));
				str2=str2+" "+str1;str1="";}
		//将连续空格变为单一空格
		while(str2.indexOf("  ")!=-1)str2=str2.substring(0, str2.indexOf("  "))+" "+str2.substring(str2.indexOf("  ")+2);
		if(str2.equals(" "))str2="";
		if(str2.length()>1&&str2.charAt(0)==' ')str2=str2.substring(1);
		if(str2.length()>1&&str2.charAt(str2.length()-1)==' ')str2=str2.substring(0, str2.length()-1);
		//去除+ - * / ^ > < = & | ! . , ( [ {前后的空格  去除) ] }前的空格
		for(int i=0;i<str2.length();i++)
			if(i<str2.length()-1&&str2.charAt(i)==' '&&
			   (str2.charAt(i+1)=='+'||str2.charAt(i+1)=='-'||str2.charAt(i+1)=='*'||str2.charAt(i+1)=='/'||str2.charAt(i+1)=='^'||
			   str2.charAt(i+1)=='>'||str2.charAt(i+1)=='<'||str2.charAt(i+1)=='='||str2.charAt(i+1)=='&'||str2.charAt(i+1)=='|'||
			   str2.charAt(i+1)=='!'||str2.charAt(i+1)=='('||str2.charAt(i+1)==')'||str2.charAt(i+1)=='['||str2.charAt(i+1)==']'||
			   str2.charAt(i+1)=='{'||str2.charAt(i+1)=='}'||str2.charAt(i+1)=='.'||str2.charAt(i+1)==','));
			else if(i<str2.length()-1&&str2.charAt(i+1)==' '&&
					(str2.charAt(i)=='+'||str2.charAt(i)=='-'||str2.charAt(i)=='*'||str2.charAt(i)=='/'||str2.charAt(i)=='^'||
					str2.charAt(i)=='>'||str2.charAt(i)=='<'||str2.charAt(i)=='='||str2.charAt(i)=='&'||str2.charAt(i)=='|'||
					str2.charAt(i)=='!'||str2.charAt(i)=='('||str2.charAt(i)=='['||str2.charAt(i)=='{'||
					str2.charAt(i)=='.'||str2.charAt(i)==','))str3=str3+str2.charAt(i++);
			else str3=str3+str2.charAt(i);
		return str3;
	}

	//代码分段整理
	private void mainOperate(String str)throws Exception{
		if(str.length()>0)str=str+' ';
		char ch;String str1="",str2="";
		for(int i=0;i<str.length();i++)
			if((ch=str.charAt(i))!=' ')str1=str1+ch;
		//处理include{}语句
			else if(str1.indexOf("include{")==0){
				i=i-str1.length()+8;str1="";
				//读取{}中的语句
				String strx="";int 大括号=1;
				while(i<str.length()&&大括号!=0){ch=str.charAt(i++);if(ch=='{')大括号++;else if(ch=='}')大括号--;if(大括号!=0)strx=strx+ch;}
				i--;if(大括号!=0)throw new Exception("include{}语句错误");
				//include{}语句执行
				strx=strx+' ';String stry="",strz="";int chx;
				for(int j=0;j<strx.length();j++)
					if((ch=strx.charAt(j))!=' '&&ch!=',')stry=stry+ch;
					else if(stry.length()>0){
						int k=0;while(k<源码名.size()&&!stry.equals(源码名.get(k)))k++;
						if(k==源码名.size()){
							FileReader filein=new FileReader("/mnt/sdcard/Android/data/com.wdjpiece.android/"+stry);
							while((chx=filein.read())!=-1)strz=strz+(char)chx;filein.close();
							if(strz.length()>0){mainOperate(pretreatOperate(strz));源码名.add(stry);}
						}
						stry="";strz="";
					}
			}
			else{str2=str2+str1+" ";str1="";}
		if(str2.length()>0)源码段.add(str2);
	}

	//代码写入文件
	private void saveOperate()throws Exception{
		FileWriter fileout=new FileWriter("/mnt/sdcard/Android/data/com.wdjpiece.android/bin/0");
		fileout.write(String.valueOf(源码段.size()));fileout.close();
		for(int i=1;i<=源码段.size();i++){
			fileout=new FileWriter("/mnt/sdcard/Android/data/com.wdjpiece.android/bin/"+i);
			fileout.write(源码段.get(i-1));fileout.close();
		}
	}
}
