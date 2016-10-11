package com.wdjpiece.android;

public class DataBase
{
	byte type;
	private Boolean DBbool;  //type=2
	private Double DBdigit;  //type=1
	private String DBstr;    //type=0

	void setbool(Boolean bool){DBbool=bool;type=2;}
	void setdigit(Double digit){DBdigit=digit;type=1;}
	void setstr(String str){DBstr=str;type=0;}

	Boolean getbool(){if(DBbool!=null)return DBbool;else return false;}
	Double getdigit(){if(DBdigit!=null)return DBdigit;else return 0.0;}
	String getstr(){if(DBstr!=null)return DBstr;else return "";}
}
