package com.wdjpiece.android;

import java.util.ArrayList;
import android.content.Context;

public class Wdj2
{
	private Context context;
	private DataPool DP;
	private String ThisPiece="";

	Wdj2(Context context,DataPool dp,String thisPiece){
		this.context=context;
		DP=dp;ThisPiece=thisPiece;
	}
	//等式处理
	public void equateOperate(String str1,String str2)throws Exception{
		str1=thisSuperIndex(str1);
		str2=thisSuperIndex(str2);
		while(true){
			//是变量
			if(DP.ishave(str2)){
				String strx=str1;if(strx.indexOf('.')!=-1)strx=strx.substring(0, strx.indexOf('.'));
				String stry=str2;if(stry.indexOf('.')!=-1)stry=stry.substring(0, stry.indexOf('.'));
				if(strx.equals(stry)){DP.replace("DP_PIECE_TEMP", str2);DP.replace(str1, "DP_PIECE_TEMP");break;}
				else {DP.replace(str1, str2);break;}
			}
			//是函数
			else if(str2.indexOf('(')!=-1&&DP.ishave(str2.substring(0, str2.indexOf('(')))&&
					str2.indexOf('+')==-1&&str2.indexOf('-')==-1&&str2.indexOf('*')==-1&&str2.indexOf('/')==-1&&
					str2.indexOf('^')==-1&&str2.indexOf('>')==-1&&str2.indexOf('<')==-1&&str2.indexOf('=')==-1&&
					str2.indexOf('&')==-1&&str2.indexOf('|')==-1&&str2.indexOf('!')==-1){
				String strx=str2.substring(0, str2.indexOf('('));String stry=str2.substring(str2.indexOf('(')+1);
				int 括号=1,i=0;while(i<stry.length()&&括号!=0){if(stry.charAt(i)=='(')括号++;else if(stry.charAt(i)==')')括号--;i++;}
				if(括号==0){new Wdj3(context,DP,ThisPiece).functOperate(strx,stry.substring(0, i-1));str2=strx;}
				if(i<stry.length()&&stry.charAt(i)=='.')str2=strx+stry.substring(i);
			}
			else break;
		}
		//是布尔
		if(str2.equals("true")){DP.setbool(str1, true);}
		else if(str2.equals("false")){DP.setbool(str1, false);}
		//是数字
		else try{DP.setdigit(str1, Double.valueOf(str2));}
			catch(Exception ex){
				//是字符串
				if(str2.charAt(0)=='\"'&&str2.charAt(str2.length()-1)=='\"')
					DP.setstr(str1, str2.substring(1, str2.length()-1));
				//是算式
				else{
					DataBase XYZ=baseOperate(str2);
					if(XYZ.type==2)DP.setbool(str1, XYZ.getbool());
					else if(XYZ.type==1)DP.setdigit(str1, XYZ.getdigit());
					else if(XYZ.type==0)DP.setstr(str1, XYZ.getstr());
				}
			}
	}
	//--------------------------------------------------------------------------------
	//this.处理  super.处理  .index()处理
	public String thisSuperIndex(String str)throws Exception{
		char ch;int i,括号;String str1;
		if(ThisPiece.length()>0)
			if(str.equals("this"))str=ThisPiece;
			else if(str.indexOf("this.")==0)str=ThisPiece+"."+str.substring(5);
		if(ThisPiece.lastIndexOf('.')>0)
			if(str.equals("super"))str=ThisPiece.substring(0, ThisPiece.lastIndexOf('.'));
			else if(str.indexOf("super.")==0)str=ThisPiece.substring(0, ThisPiece.lastIndexOf('.'))+"."+str.substring(6);
		while(str.indexOf(".index(")!=-1){
			i=str.indexOf(".index(")+7;括号=1;str1="";
			while(i<str.length()&&括号!=0){ch=str.charAt(i++);if(ch=='(')括号++;else if(ch==')')括号--;if(括号!=0)str1=str1+ch;}
			if(括号!=0)throw new Exception("\nindex()函数错误\n"+str);
			DataBase XYZ=baseOperate(str1);
			if(XYZ.type==1)str1=String.valueOf(Math.abs(XYZ.getdigit().intValue()));
			else str1=XYZ.getstr();
			if(str1.length()==0)str1="0";
			str=str.substring(0, str.indexOf(".index("))+"."+str1+str.substring(i);
		}
		return str;
	}
	//--------------------------------------------------------------------------------
	//算式计算
	//数学运算      + - * / ^
	//比较运算      > < == >= <=
	//布尔运算      & | !
	//括号运算     ()
	//优先级10 (
	//优先级9  ^
	//优先级8  * /
	//优先级7  + -
	//优先级6  > < == >= <=
	//优先级5  !
	//优先级4  & |
	//优先级3  )
	//0 1 2  用于表示  字符串  数字  和  布尔
	public DataBase baseOperate(String str)throws Exception{
		String str1="",str2="";int 大括号数=0,中括号数=0,小括号数=0;
		ArrayList<DataBase> 算式=new ArrayList<DataBase>();
		ArrayList<DataBase> 数据栈=new ArrayList<DataBase>();
		ArrayList<DataBase> 符号栈=new ArrayList<DataBase>();
		if(str.length()>0)str1="("+thisSuperIndex(str)+")";
		//算式
		for(int i=0;i<str1.length();i++)
			if(str1.charAt(i)!='+'&&str1.charAt(i)!='-'&&str1.charAt(i)!='*'&&str1.charAt(i)!='/'&&str1.charAt(i)!='^'&&
			   str1.charAt(i)!='>'&&str1.charAt(i)!='<'&&str1.charAt(i)!='='&&str1.charAt(i)!='&'&&str1.charAt(i)!='|'&&
			   str1.charAt(i)!='!'&&str1.charAt(i)!='('&&str1.charAt(i)!=')'&&str1.charAt(i)!='['&&str1.charAt(i)!=']'&&
			   str1.charAt(i)!='{'&&str1.charAt(i)!='}')str2=str2+str1.charAt(i);
			else{
				if(str2.length()>0){DataBase X=new DataBase();X.setstr(str2);算式.add(X);str2="";}

				DataBase Y=new DataBase();
				if(str1.charAt(i)=='{'){大括号数++;Y.setstr("(");}
				else if(str1.charAt(i)=='['){中括号数++;Y.setstr("(");}
				else if(str1.charAt(i)=='('){小括号数++;Y.setstr("(");}
				else if(str1.charAt(i)=='}'){大括号数--;Y.setstr(")");}
				else if(str1.charAt(i)==']'){中括号数--;Y.setstr(")");}
				else if(str1.charAt(i)==')'){小括号数--;Y.setstr(")");}
				else Y.setstr(String.valueOf(str1.charAt(i)));
				算式.add(Y);

				if(i<str1.length()-1&&
				   (str1.charAt(i)=='('||str1.charAt(i)=='['||str1.charAt(i)=='{'||
				   str1.charAt(i)=='>'||str1.charAt(i)=='<'||str1.charAt(i)=='=')&&
				   (str1.charAt(i+1)=='+'||str1.charAt(i+1)=='-')){DataBase Z=new DataBase();Z.setstr("0");算式.add(Z);}
			}
		if(大括号数!=0)throw new Exception("\n大括号{}错误\n"+str);
		if(中括号数!=0)throw new Exception("\n中括号[]错误\n"+str);
		if(小括号数!=0)throw new Exception("\n小括号()错误\n"+str);

		//识别
		for(int i=0;i<算式.size();i++){
			str1=算式.get(i).getstr();
			str1=thisSuperIndex(str1);
			//识别
			while(DP.ishave(str1)){
				//是函数
				if(i+2<算式.size()&&算式.get(i+1).getstr().equals("(")){
					str2="";int j=i+2,括号=1;
					while(j<算式.size()&&括号!=0){
						if(算式.get(j).getstr().equals("("))括号++;else if(算式.get(j).getstr().equals(")"))括号--;
						if(括号!=0)str2=str2+算式.get(j++).getstr();
					}
					if(括号!=0)throw new Exception("\n算式错误  "+str+"\n函数错误  "+str1);
					while(j>i){算式.remove(i+1);j--;}
					new Wdj3(context,DP,ThisPiece).functOperate(str1,str2);
				}
				if(i+1<算式.size()&&算式.get(i+1).getstr().indexOf('.')==0){
					str1=str1+算式.get(i+1).getstr();算式.remove(i+1);continue;
				}
				//是变量
				算式.get(i).setbool(DP.getbool(str1));
				算式.get(i).setdigit(DP.getdigit(str1));
				算式.get(i).setstr(DP.getstr(str1));
				if(DP.gettype(str1).equals("bool"))算式.get(i).type=2;
				else if(DP.gettype(str1).equals("digit"))算式.get(i).type=1;
				else 算式.get(i).type=0;break;
			}
			//是运算符
			if(str1.equals("("))算式.get(i).type=10;
			else if(str1.equals("^"))算式.get(i).type=9;
			else if(str1.equals("*")||str1.equals("/"))算式.get(i).type=8;
			else if(str1.equals("+")||str1.equals("-"))算式.get(i).type=7;
			else if(i<算式.size()-1&&(str1.equals(">")||str1.equals("<")||str1.equals("="))&&
					算式.get(i+1).getstr().equals("=")){算式.get(i).setstr(str1+"=");算式.remove(i+1);算式.get(i).type=6;}
			else if(str1.equals(">")||str1.equals("<"))算式.get(i).type=6;
			else if(str1.equals("!"))算式.get(i).type=5;
			else if(str1.equals("&")||str1.equals("|"))算式.get(i).type=4;
			else if(str1.equals(")"))算式.get(i).type=3;
			//是布尔
			else if(str1.equals("true"))算式.get(i).setbool(true);
			else if(str1.equals("false"))算式.get(i).setbool(false);
			//是数字
			else try{算式.get(i).setdigit(Double.valueOf(str1));}
				catch(Exception ex){}
		}

		//运算
		for(int i=0;i<算式.size();i++){
			//数据进栈
			if(算式.get(i).type<3)数据栈.add(算式.get(i));
			else do{
					//运算符进栈
					if(符号栈.size()==0||符号栈.size()>0&&算式.get(i).type>符号栈.get(符号栈.size()-1).type){
						符号栈.add(算式.get(i));
						if(符号栈.get(符号栈.size()-1).getstr().equals("("))符号栈.get(符号栈.size()-1).type=3;
						break;
					}
					//出栈运算
					else{
						str1=符号栈.get(符号栈.size()-1).getstr();
						//数学运算
						if(str1.equals("+")){
							数据栈.get(数据栈.size()-2).setdigit(数据栈.get(数据栈.size()-2).getdigit()+数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("-")){
							数据栈.get(数据栈.size()-2).setdigit(数据栈.get(数据栈.size()-2).getdigit()-数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("*")){
							数据栈.get(数据栈.size()-2).setdigit(数据栈.get(数据栈.size()-2).getdigit()*数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("/")){
							数据栈.get(数据栈.size()-2).setdigit(数据栈.get(数据栈.size()-2).getdigit()/数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("^")){
							数据栈.get(数据栈.size()-2).setdigit(Math.pow(数据栈.get(数据栈.size()-2).getdigit(),数据栈.get(数据栈.size()-1).getdigit()));
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						//比较运算
						else if(str1.equals(">")){
							数据栈.get(数据栈.size()-2).setbool((double)数据栈.get(数据栈.size()-2).getdigit()>(double)数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("<")){
							数据栈.get(数据栈.size()-2).setbool((double)数据栈.get(数据栈.size()-2).getdigit()<(double)数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("==")){
							数据栈.get(数据栈.size()-2).setbool((double)数据栈.get(数据栈.size()-2).getdigit()==(double)数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals(">=")){
							数据栈.get(数据栈.size()-2).setbool((double)数据栈.get(数据栈.size()-2).getdigit()>=(double)数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("<=")){
							数据栈.get(数据栈.size()-2).setbool((double)数据栈.get(数据栈.size()-2).getdigit()<=(double)数据栈.get(数据栈.size()-1).getdigit());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						//布尔运算
						else if(str1.equals("&")){
							数据栈.get(数据栈.size()-2).setbool(数据栈.get(数据栈.size()-2).getbool()&&数据栈.get(数据栈.size()-1).getbool());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("|")){
							数据栈.get(数据栈.size()-2).setbool(数据栈.get(数据栈.size()-2).getbool()||数据栈.get(数据栈.size()-1).getbool());
							数据栈.remove(数据栈.size()-1);符号栈.remove(符号栈.size()-1);
						}
						else if(str1.equals("!")){
							数据栈.get(数据栈.size()-1).setbool(!数据栈.get(数据栈.size()-1).getbool());
							符号栈.remove(符号栈.size()-1);
						}
						//括号运算
						else if(str1.equals("(")){符号栈.remove(符号栈.size()-1);break;}
						else throw new Exception("\n算式错误  "+str+"\n未知表达  "+str1);
					}
				}while(符号栈.size()>0);
		}
		return 数据栈.get(0);
	}
}
