package com.wdjpiece.android;

import java.util.ArrayList;
import android.content.Context;

public class Wdj3
{
	private Context context;
	private DataPool DP;
	private String ThisPiece="";
	private ArrayList<String> TempPiece;
	private static int FUNCT_TIER=0;

	Wdj3(Context context,DataPool dp,String thispiece){
		this.context=context;
		DP=dp;ThisPiece=thispiece;
		TempPiece=new ArrayList<String>();
	}

	//函数处理
	public void functOperate(String name,String parame)throws Exception{
		char ch;int 括号=0;
		String str[]=DP.getfunct(name),str1="",str2="";
		String str3="FUNCT_PARAME."+String.valueOf(FUNCT_TIER++)+".";
		Wdj2 wdj=new Wdj2(context,DP,ThisPiece);
		//处理参数
		for(int i=0,j=0;i<str[0].length()&&j<parame.length();i++,j++){
			while(i<str[0].length()&&str[0].charAt(i)!=',')str1=str1+str[0].charAt(i++);
			while(j<parame.length()&&!((ch=parame.charAt(j))==','&&括号==0)){
				if(ch=='(')括号++;else if(ch==')')括号--;
				str2=str2+ch;j++;
			}
			//参数保存及标记
			if(DP.ishave(str1)){DP.replace(str3+str1, str1);TempPiece.add(str3+str1);}
			else TempPiece.add(str1);
			//参数赋值
			wdj.equateOperate(str1, str2);
			str1="";str2="";括号=0;
		}
		//数学函数
		if(name.equals("sin"))DP.setdigit("sin", Math.sin(DP.getdigit("SIN_PARAME")/180*Math.PI));
		else if(name.equals("cos"))DP.setdigit("cos", Math.cos(DP.getdigit("COS_PARAME")/180*Math.PI));
		else if(name.equals("tan"))DP.setdigit("tan", Math.tan(DP.getdigit("TAN_PARAME")/180*Math.PI));
		else if(name.equals("asin"))DP.setdigit("asin", Math.asin(DP.getdigit("ASIN_PARAME"))/Math.PI*180);
		else if(name.equals("acos"))DP.setdigit("acos", Math.acos(DP.getdigit("ACOS_PARAME"))/Math.PI*180);
		else if(name.equals("atan"))DP.setdigit("atan", Math.atan(DP.getdigit("ATAN_PARAME"))/Math.PI*180);
		else if(name.equals("ln"))DP.setdigit("ln", Math.log(DP.getdigit("LN_PARAME")));
		//字符串函数
		else if(name.equals("strlen"))DP.setdigit("strlen", (double)DP.getstr("STR_PARAME").length());
		else if(name.equals("strind"))DP.setdigit("strind", (double)DP.getstr("STR1_PARAME").indexOf(DP.getstr("STR2_PARAME")));
		else if(name.equals("strequ"))DP.setbool("strequ", DP.getstr("STR1_PARAME").equals(DP.getstr("STR2_PARAME")));
		else if(name.equals("stradd"))DP.setstr("stradd", DP.getstr("STR1_PARAME")+DP.getstr("STR2_PARAME"));
		else if(name.equals("strsub"))DP.setstr("strsub",
												DP.getstr("STR_PARAME").substring(DP.getdigit("STR1_PARAME").intValue(), DP.getdigit("STR2_PARAME").intValue()));
		//外部程序调用函数
		else if(name.equals("run")){
			DP.setbool("run", false);
			Runtime.getRuntime().exec(DP.getstr("RUN_PARAME"));
			DP.setbool("run", true);
		}
		//代码执行函数
		else if(name.equals("runcode"))new Wdj1(context,DP,"runcode",DP.getstr("RUNCODE_PARAME"));
		//等待函数
		else if(name.equals("wait")){
			DP.setbool("wait", false);
			Thread.sleep(DP.getdigit("WAIT_PARAME").longValue());
			DP.setbool("wait", true);
		}
		//其它函数
		else new Wdj1(context,DP,name,false);
		//参数恢复及移除
		for(int i=0;i<TempPiece.size();i++){
			if(TempPiece.get(i).indexOf(str3)==0)DP.replace(TempPiece.get(i).substring(str3.length()), TempPiece.get(i));
			DP.remove(TempPiece.get(i));
		}
		FUNCT_TIER--;
	}
}
