package com.wdjpiece.android;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPool
{
	//内置函数
	DataPool(){
		//数学函数
		setfunct("sin",new String[]{"SIN_PARAME",null});
		setfunct("cos",new String[]{"COS_PARAME",null});
		setfunct("tan",new String[]{"TAN_PARAME",null});
		setfunct("asin",new String[]{"ASIN_PARAME",null});
		setfunct("acos",new String[]{"ACOS_PARAME",null});
		setfunct("atan",new String[]{"ATAN_PARAME",null});
		setfunct("ln",new String[]{"LN_PARAME",null});
		//字符串函数
		setfunct("strlen",new String[]{"STR_PARAME",null});
		setfunct("strind",new String[]{"STR1_PARAME,STR2_PARAME",null});
		setfunct("strequ",new String[]{"STR1_PARAME,STR2_PARAME",null});
		setfunct("stradd",new String[]{"STR1_PARAME,STR2_PARAME",null});
		setfunct("strsub",new String[]{"STR_PARAME,STR1_PARAME,STR2_PARAME",null});
		//外部程序调用函数
		setfunct("run",new String[]{"RUN_PARAME",null});
		//代码执行函数
		setfunct("runcode",new String[]{"RUNCODE_PARAME",null});
		//等待函数
		setfunct("wait",new String[]{"WAIT_PARAME",null});
	}
	
	//数据区
	private HashMap<String,String> DPtype=new HashMap<String, String>();
	private HashMap<String,Boolean> DPbool=new HashMap<String, Boolean>();
	private HashMap<String,Double> DPdigit=new HashMap<String, Double>();
	private HashMap<String,String> DPstr=new HashMap<String, String>();

	void settype(String name,String str){DPtype.put(name, str);}
	void setbool(String name,Boolean bool){DPbool.put(name, bool);DPtype.put(name, "bool");named(name);}
	void setdigit(String name,Double digit){DPdigit.put(name, digit);DPtype.put(name, "digit");named(name);}
	void setstr(String name,String str){DPstr.put(name, str);DPtype.put(name, "str");named(name);}

	String gettype(String name){return DPtype.get(name);}
	Boolean getbool(String name){return DPbool.get(name);}
	Double getdigit(String name){return DPdigit.get(name);}
	String getstr(String name){return DPstr.get(name);}
	
	//过程区
	private Integer ProceCount=0;
	private HashMap<String,Integer> DPproceindex=new HashMap<String,Integer>();
	private HashMap<Integer,String> DPproce=new HashMap<Integer,String>();

	void setproceindex(String name,Integer i){DPproceindex.put(name, i);named(name);}
	void setproce(String name,String str){DPproceindex.put(name, ProceCount);DPproce.put(ProceCount++, str);named(name);}
	Integer getproceindex(String name){return DPproceindex.get(name);}
	String getproce(String name){return DPproce.get(DPproceindex.get(name));}
	
	//函数区
	private Integer FunctCount=0;
	private HashMap<String,Integer> DPfunctindex=new HashMap<String,Integer>();
	private HashMap<Integer,String[]> DPfunct=new HashMap<Integer,String[]>();

	void setfunctindex(String name,Integer i){DPfunctindex.put(name, i);named(name);}
	void setfunct(String name,String str[]){DPfunctindex.put(name, FunctCount);DPfunct.put(FunctCount++, str);named(name);}
	Integer getfunctindex(String name){return DPfunctindex.get(name);}
	String[] getfunct(String name){return DPfunct.get(DPfunctindex.get(name));}

	//名字区
	private ArrayList<String> DPname=new ArrayList<String>();
	private void named(String name){
		int i=0;while(i<DPname.size()&&!DPname.get(i).equals(name))i++;
		if(i==DPname.size())DPname.add(name);
	}
	
	//其它区
	boolean ishave(String name){
		int i=0;while(i<DPname.size()&&!(DPname.get(i).equals(name)||
					  DPname.get(i).length()>name.length()&&DPname.get(i).indexOf(name)==0&&DPname.get(i).charAt(name.length())=='.'))i++;
		if(i==DPname.size())return false;
		else return true;
	}

	void remove(String name){
		for(int i=0;i<DPname.size();i++){
			if(DPname.get(i).equals(name)||
			   DPname.get(i).length()>name.length()&&DPname.get(i).indexOf(name)==0&&DPname.get(i).charAt(name.length())=='.'){
				DPtype.remove(DPname.get(i));DPbool.remove(DPname.get(i));
				DPdigit.remove(DPname.get(i));DPstr.remove(DPname.get(i));
				DPproceindex.remove(DPname.get(i));DPfunctindex.remove(DPname.get(i));
				DPname.remove(i--);
			}
		}
	}
	
	boolean replace(String target,String source){
		if(!ishave(source))return false;
		remove(target);String str;
		for(int i=0;i<DPname.size();i++){
			if(DPname.get(i).equals(source)||
			   DPname.get(i).length()>source.length()&&DPname.get(i).indexOf(source)==0&&DPname.get(i).charAt(source.length())=='.'){
				if(DPname.get(i).equals(source))str=target;
				else str=target+DPname.get(i).substring(source.length());
				DPtype.put(str, DPtype.get(DPname.get(i)));DPbool.put(str, DPbool.get(DPname.get(i)));
				DPdigit.put(str, DPdigit.get(DPname.get(i)));DPstr.put(str, DPstr.get(DPname.get(i)));
				DPproceindex.put(str, DPproceindex.get(DPname.get(i)));DPfunctindex.put(str, DPfunctindex.get(DPname.get(i)));
				named(str);
			}
		}
		return true;
	}
}
