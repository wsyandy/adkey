package com.joyplus.admonitor;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.joyplus.admonitor.Application.AdMonitorSDKFeature;
import com.joyplus.admonitor.Application.AdMonitorSDKManager;
import com.joyplus.admonitor.Application.CUSTOMINFO;
import com.joyplus.admonitor.Application.MD5Util;
import com.joyplus.admonitor.data.ImpressionType;
import com.joyplus.admonitor.phone.Escape;
import com.joyplus.admonitor.phone.PhoneManager;

public class Monitor {
	
      public  final static String REPLACE_MAC  = "%mac%";
      public  final static String REPLACE_DM   = "%dm%";
	  public  final static String REPLACE_IP   = "%ip%";
      public  final static String REPLACE_EX   = "%ex%";
      public  final static String REPLACE_UA   = "%UA%";
      public  final static String REPLACE_TS   = "%TS%";
      
	  private List<IMPRESSION>   mIMPRESSION  = new ArrayList<IMPRESSION>();
	  
	  private String             MAC          = "";//mac , mac , null
	  private String             DM           = "";//dm  ,devicement , null
	  private String             IP           = "";
	  private String             EX           = "";
	  private String             UA           = "";
	  private String             TS           = "";
	  
	  public void SetMAC(String mac){
		  MAC = mac;
	  }
	  public String GetMAC(){
		  if(MAC == null || "".equals(MAC))return "";
		  return MD5Util.GetMD5Code(MAC);
	  }
	  
	  public void SetDM(String pm){
		  DM = pm;
	  }
	  public String GetDM(){
		  if(DM == null || "".equals(DM)){
			  if(!AdMonitorSDKManager.IsInited())return "";
			  AdMonitorSDKManager mM = AdMonitorSDKManager.getInstance();
			  if(mM != null && mM.GetCUSTOMINFO() != null){
				  String DM = mM.GetCUSTOMINFO().GetDEVICEMOVEMENT();
				  if(!(DM==null)||("".equals(DM))){
					  return DM;
				  }
			  }
			  return "";
		  }
		  return DM;  
	  }
	  
	  public void SetIP(String ip){
		  IP = ip;
	  }
	  public String GetIP(){
		  if(IP == null || "".equals(IP))return "";
		  return IP;
	  }
	  
	  public void SetEX(String ex){
		  EX = ex;
	  }
	  public String GetEX(){
		  if(EX == null || "".equals(EX))return "";
		  return EX;
	  }
	  
	  public void SetUA(String ua){
		  UA = ua;
	  }
	  public String GetUA(){
		  if(UA == null || "".equals(UA))return "";
		  return UA;
	  }
	  
	  public void SetTS(String ts){
		  TS = ts;
	  }
	  public String GetTS(){
		  if(TS == null || "".equals(TS))return "";
		  return TS;
	  }
	  
	  public synchronized void AddIMPRESSION(IMPRESSION s){
		  if(s == null)return;
		  List<IMPRESSION> ss = new ArrayList<IMPRESSION>();
		  ss.add(s);
		  AddIMPRESSION(ss);
	  }
	  public synchronized void AddIMPRESSION(List<IMPRESSION> s){
		  if(s == null )return;
		  if(mIMPRESSION == null)mIMPRESSION = new ArrayList<IMPRESSION>();
		  for(IMPRESSION mIMPRESSIONs : s){
			  if(mIMPRESSIONs.mMonitored)continue;
			  if(mIMPRESSIONs.mImpressionType == null || ImpressionType.Unknow == mIMPRESSIONs.mImpressionType)continue;
			  if(mIMPRESSIONs.mImpressionURL == null || "".equals(mIMPRESSIONs.mImpressionURL))continue;
			  if((AdMonitorSDKFeature.MIAOZHEN && ImpressionType.miaozhen == mIMPRESSIONs.mImpressionType)
					  ||(AdMonitorSDKFeature.IRESEARCH && ImpressionType.iresearch == mIMPRESSIONs.mImpressionType)
					  ||(AdMonitorSDKFeature.ADMASTER && ImpressionType.admaster == mIMPRESSIONs.mImpressionType)
					  ||(ImpressionType.Joyplus == mIMPRESSIONs.mImpressionType)){
				  if(!(mIMPRESSIONs.mImpressionURL == null || "".equals(mIMPRESSIONs.mImpressionURL))){
					  if(!mIMPRESSION.contains(mIMPRESSIONs)){
						  mIMPRESSION.add(mIMPRESSIONs);
					  }
				  }
			  }
		  }
	  }
	  public   List<IMPRESSION> GetIMPRESSION(){
		  if(mIMPRESSION == null)return new ArrayList<IMPRESSION>(); 
		  return mIMPRESSION;
	  }
	  public IMPRESSION GetFirstIMPRESSION(){
		  if(mIMPRESSION == null || mIMPRESSION.size()<=0)return null;
		  Iterator<IMPRESSION> us = mIMPRESSION.iterator();
		  while(us.hasNext()){
			  IMPRESSION s =us.next();
			  if(s.mMonitored
					  ||(s.mImpressionType == null || ImpressionType.Unknow == s.mImpressionType)
					  ||(s.mImpressionURL == null || "".equals(s.mImpressionURL))){
				  us.remove();
				  continue;
			  }
			  if((AdMonitorSDKFeature.MIAOZHEN && ImpressionType.miaozhen == s.mImpressionType)
					  ||(AdMonitorSDKFeature.ADMASTER && ImpressionType.admaster == s.mImpressionType)){
				  return s;
			  }else if((AdMonitorSDKFeature.IRESEARCH && ImpressionType.iresearch == s.mImpressionType)
                         || (ImpressionType.Joyplus == s.mImpressionType)){
				  String mac = GetMAC();//get user set first
				  if("".equals(mac)){
					  if(AdMonitorSDKManager.IsInited()){//get user base mac 
						  CUSTOMINFO info = AdMonitorSDKManager.getInstance().GetCUSTOMINFO();
						  if(info != null)mac = info.GetMAC();
					  }
					  if(mac == null || "".equals(mac))mac = PhoneManager.getInstance().GetMac();//the last to get mac by Our-self
				  }
				  if(!(mac == null || "".equals(mac))){
					  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_MAC,MD5Util.GetMD5Code(mac.toUpperCase()));
				  }else{
					  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_MAC,"");
				  }
				  String ip = GetIP();
				  if("".equals(ip))ip = PhoneManager.getInstance().GetIp();
				  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_IP,ip);
				  
				  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_DM,GetDM());
				  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_EX,GetEX());
				  
				  //if(ImpressionType.Joyplus == s.mImpressionType){
					  String ua = GetUA();
					  if(ua == null || "".equals(ua)){
						  if(AdMonitorSDKManager.IsInited()){
								 PhoneManager Phone = PhoneManager.getInstance();
								 if(Phone != null){
									 ua = Phone.GetUA1();
									 if(ua == null || "".equals(ua))ua = Phone.GetUA2();
								 }
							 }
					  }
					  if(ua == null || "".equals(ua)){
						  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_UA,"");
					  }else{
						  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_UA,Escape.escape(ua));
					  }
					  
					  String ts = GetTS();
					  if(ts == null || "".equals(ts)){
						  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_TS,(""+System.currentTimeMillis()));
					  }else{
						  s.mImpressionURL = Replace(s.mImpressionURL,REPLACE_TS,ts);
					  }
				  //}
				  return s;
				  
			  }
			  us.remove();
			  continue;
		  }
		  return null;
	  }
	  
	  
	  private String Replace(String s,String d,String di){
		  String result = s;
		  try{
			  result = s.replaceAll(d, di);
		  }catch(NullPointerException e){
			  e.printStackTrace();
			  result = s;
		  }
		  return result;
	  }
	  
	  public boolean RemoveFirstTRACKINGURL(IMPRESSION url){
		  if(mIMPRESSION == null || mIMPRESSION.size()<=0)
			  return true;
		  return mIMPRESSION.remove(url);
	  }
}
