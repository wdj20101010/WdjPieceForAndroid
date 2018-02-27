package com.wdjpiece.android;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import android.content.Context;
import android.app.AlertDialog;

public class Wdj1
{
	private Context context;
	private AlertDialog.Builder messageDialog;
	private DataPool DP;
	public String ThisPiece;
	private boolean IsProce;
	private Wdj2 wdj;
	private ArrayList<String> TempPiece;

	//用于proce{}和funct{}
	Wdj1(Context context,DataPool dp,String thispiece,boolean isproce){
		this.context=context;messageDialog=new AlertDialog.Builder(context);
		DP=dp;ThisPiece=thispiece;IsProce=isproce;
		wdj=new Wdj2(context,DP,ThisPiece);
		TempPiece=new ArrayList<String>();
		//获取执行代码
		String str="";
		if(IsProce)str=DP.getproce(ThisPiece);
		else{str=DP.getfunct(ThisPiece)[1];}
		//进行处理
		try{mainOperate(str);}
		catch(Exception e){messageDialog.setMessage(e.getMessage()).show();}
		//移除TempPiece
		for(int i=0;i<TempPiece.size();i++)DP.remove(TempPiece.get(i));
	}

	//用于runcode()
	Wdj1(Context context,DataPool dp,String thispiece,String code){
		this.context=context;messageDialog=new AlertDialog.Builder(context);
		DP=dp;ThisPiece=thispiece;IsProce=false;
		wdj=new Wdj2(context,DP,ThisPiece);
		TempPiece=new ArrayList<String>();
		//进行处理
		try{mainOperate(pretreatOperate(code));}
		catch(Exception e){messageDialog.setMessage(e.getMessage()).show();}
		//移除TempPiece
		for(int i=0;i<TempPiece.size();i++)DP.remove(TempPiece.get(i));
	}

	//流程处理
	//处理enter{},处理result{},处理runnow{},处理runlater{}
	//处理entfrom(){},处理resulto(){},处理if(){},处理while(){}
	//处理等式语句
	private void mainOperate(String str)throws Exception{
		char ch;String str1="",str2,str3,str4;int 括号;
		if(str.length()>0)str=str+' ';
		for(int i=0;i<str.length();i++){
			if((ch=str.charAt(i))!=' ')str1=str1+ch;
			//处理enter{},处理result{},处理runnow{},处理runlater{}
			else if(str1.indexOf("enter{")==0||str1.indexOf("result{")==0||str1.indexOf("runnow{")==0||str1.indexOf("runlater{")==0){
				if(str1.indexOf("enter{")==0)i=i-str1.length()+6;
				else if(str1.indexOf("result{")==0)i=i-str1.length()+7;
				else if(str1.indexOf("runnow{")==0)i=i-str1.length()+7;
				else if(str1.indexOf("runlater{")==0)i=i-str1.length()+9;
				//读取{}中的语句
				括号=1;str2="";
				while(i<str.length()&&括号!=0){ch=str.charAt(i++);if(ch=='{')括号++;else if(ch=='}')括号--;if(括号!=0)str2=str2+ch;}
				if(括号!=0){
					if(str1.indexOf("enter{")==0)throw new Exception("\nenter{}语句错误\n"+str1);
					else if(str1.indexOf("result{")==0)throw new Exception("\nresult{}语句错误\n"+str1);
					else if(str1.indexOf("runnow{")==0)throw new Exception("\nrunnow{}语句错误\n"+str1);
					else if(str1.indexOf("runlater{")==0)throw new Exception("\nrunlater{}语句错误\n"+str1);
				}
				//enter{}语句执行
				if(str1.indexOf("enter{")==0){
					new DialogEnter(context,str2).show();
					if(DialogEnter.DialogEnterStr!=null){
						mainOperate(pretreatOperate(DialogEnter.DialogEnterStr));
						DialogEnter.DialogEnterStr=null;
					}
				}
				//result{}语句执行
				else if(str1.indexOf("result{")==0){
					String 结果="";str2=str2+' ';str3="";
					for(int j=0;j<str2.length();j++)
						if((ch=str2.charAt(j))!=' ')str3=str3+ch;
						else{
							str3=wdj.thisSuperIndex(str3);
							if(DP.gettype(str3)==null)throw new Exception("\nresult{}语句错误\n未知变量: "+str3);
							else if(DP.gettype(str3).equals("bool"))结果=结果+str3+"="+DP.getbool(str3)+"\n";
							else if(DP.gettype(str3).equals("digit"))结果=结果+str3+"="+DP.getdigit(str3)+"\n";
							else if(DP.gettype(str3).equals("str"))结果=结果+str3+"=\""+DP.getstr(str3)+"\"\n";
							str3="";
						}
					new DialogResult(context,结果).show();
				}
				//runnow{}语句执行,runlater{}语句执行
				else if(str1.indexOf("runnow{")==0||str1.indexOf("runlater{")==0){
					str2=str2+' ';str3="";
					ArrayList<String> runlist=new ArrayList<String>();
					for(int j=0;j<str2.length();j++)
						if((ch=str2.charAt(j))!=' ')str3=str3+ch;
						else{str3=wdj.thisSuperIndex(str3);
							if(DP.ishave(str3)&&DP.getproceindex(str3)!=null)runlist.add(str3);
						}
					if(runlist.size()>0&&str1.indexOf("runnow{")==0)DP.DPrunaddhead(runlist);
					else if(runlist.size()>0&&str1.indexOf("runlater{")==0)DP.DPrunaddtail(runlist);
				}
				str1="";
			}
			//处理entfrom(){},处理resulto(){},处理if(){},处理while(){}
			else if(str1.indexOf("entfrom(")==0||str1.indexOf("resulto(")==0||str1.indexOf("if(")==0||str1.indexOf("while(")==0){
				if(str1.indexOf("entfrom(")==0)i=i-str1.length()+8;
				else if(str1.indexOf("resulto(")==0)i=i-str1.length()+8;
				else if(str1.indexOf("if(")==0)i=i-str1.length()+3;
				else i=i-str1.length()+6;
				//读取()中语句和{}中语句
				括号=1;str2="";
				while(i<str.length()&&括号!=0){ch=str.charAt(i++);if(ch=='(')括号++;else if(ch==')')括号--;if(括号!=0)str2=str2+ch;}
				if(括号!=0){
					if(str1.indexOf("entfrom(")==0)throw new Exception("\nentfrom(){}语句错误\n"+str1);
					else if(str1.indexOf("resulto(")==0)throw new Exception("\nresulto(){}语句错误\n"+str1);
					else if(str1.indexOf("if(")==0)throw new Exception("\nif(){}语句错误\n"+str1);
					else throw new Exception("\nwhile(){}语句错误\n"+str1);
				}
				i++;括号=1;str3="";
				while(i<str.length()&&括号!=0){ch=str.charAt(i++);if(ch=='{')括号++;else if(ch=='}')括号--;if(括号!=0)str3=str3+ch;}
				if(括号!=0){
					if(str1.indexOf("entfrom(")==0)throw new Exception("\nentfrom(){}语句错误\n"+str1);
					else if(str1.indexOf("resulto(")==0)throw new Exception("\nresulto(){}语句错误\n"+str1);
					else if(str1.indexOf("if(")==0)throw new Exception("\nif(){}语句错误\n"+str1);
					else throw new Exception("\nwhile(){}语句错误\n"+str1);
				}
				//entfrom(){}语句执行
				if(str1.indexOf("entfrom(")==0){
					int chx;str3=str3+' ';str4="";String str5="",str6="";
					FileReader filein=new FileReader("/mnt/sdcard/Android/data/com.wdjpiece.android/"+str2);
					while((chx=filein.read())!=-1)str4=str4+(char)chx;filein.close();
					for(int j=0;j<str3.length();j++)
						if((ch=str3.charAt(j))!=' ')str5=str5+ch;
						else{
							str5=wdj.thisSuperIndex(str5);
							for(int k=0;k<str4.length();k++)
								if((ch=str4.charAt(k))!='\n')str6=str6+ch;
								else{
									if(str6.indexOf('\"')!=-1&&str6.indexOf('\"')==str6.lastIndexOf('\"')){
										str6=str6+"\n";
										while(k+1<str4.length()&&(ch=str4.charAt(++k))!='\"')str6=str6+ch;
										str6=str6+"\"";k++;
									}
									if(str6.substring(0,str6.indexOf("=")).equals(str5))
										wdj.equateOperate(str5,str6.substring(str6.indexOf("=")+1));
									str6="";
								}
							str5="";
						}
				}
				//resulto(){}语句执行
				else if(str1.indexOf("resulto(")==0){
					String 结果="";str3=str3+' ';str4="";
					for(int j=0;j<str3.length();j++)
						if((ch=str3.charAt(j))!=' ')str4=str4+ch;
						else{
							str4=wdj.thisSuperIndex(str4);
							if(DP.gettype(str4)==null)throw new Exception("\nresulto(){}语句错误\n未知变量: "+str4);
							else if(DP.gettype(str4).equals("bool"))结果=结果+str4+"="+DP.getbool(str4)+"\n";
							else if(DP.gettype(str4).equals("digit"))结果=结果+str4+"="+DP.getdigit(str4)+"\n";
							else if(DP.gettype(str4).equals("str"))结果=结果+str4+"=\""+DP.getstr(str4)+"\"\n";
							str4="";
						}
					FileWriter fileout=new FileWriter("/mnt/sdcard/Android/data/com.wdjpiece.android/"+str2);
					fileout.write(结果);fileout.close();
				}
				//if(){}语句执行
				else if(str1.indexOf("if(")==0){DataBase XYZ=wdj.baseOperate(str2);if(XYZ.getbool())mainOperate(str3);}
				//while(){}语句执行
				else{DataBase XYZ=wdj.baseOperate(str2);while(XYZ.getbool()){mainOperate(str3);XYZ=wdj.baseOperate(str2);}}
				str1="";
			}
			//处理等式语句
			else if(str1.length()>0){
				if(str1.indexOf("=")<1||str1.indexOf("=")>str1.length()-2)throw new Exception("\n语句错误\n"+str1);
				else{
					str2=str1.substring(0, str1.indexOf('='));str3=str1.substring(str1.indexOf('=')+1);str1="";
					//双引号" "处理
					if(str3.indexOf("\"")!=-1&&str3.indexOf("\"")==str3.lastIndexOf("\"")){
						while(i<str.length()&&(ch=str.charAt(i))!='\"'){str3=str3+ch;i++;}
						str3=str3+"\"";
					}
					//标记TempPiece
					str4="";
					if(str2.indexOf('.')!=-1)str4=str2.substring(0, str2.indexOf('.'));
					if(str4.length()>0&&!DP.ishave(str4))TempPiece.add(str2);
					else if(str4.length()==0&&!DP.ishave(str2))TempPiece.add(str2);
					//等式处理
					if(str2.equals("this")&&!IsProce){
						wdj.equateOperate("DP_PIECE_TEMP",str3);
						DP.setbool(ThisPiece, DP.getbool("DP_PIECE_TEMP"));
						DP.setdigit(ThisPiece, DP.getdigit("DP_PIECE_TEMP"));
						DP.setstr(ThisPiece, DP.getstr("DP_PIECE_TEMP"));
						DP.settype(ThisPiece, DP.gettype("DP_PIECE_TEMP"));
					}
					else wdj.equateOperate(str2,str3);
				}
			}
		}
	}

	//预先处理
	//代码规整化
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
			if(Y==str.length())throw new Exception("\nenter{}语句错误\n"+str);
			int Z=Y,括号=1;
			while(Z<str.length()&&括号!=0){ch=str.charAt(Z++);if(ch=='{')括号++;else if(ch=='}')括号--;}
			if(括号!=0)throw new Exception("\nenter{}语句错误\n"+str);
			String strx=str.substring(0, X);
			String stry=str.substring(Y, Z-1);
			String strz=str.substring(Z);
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
}
