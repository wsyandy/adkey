package com.joyplus.ad;

import java.io.File;
import com.joyplus.ad.data.AdFileServer;

import android.content.Context;

public class AdFileManager {
       
	private Context mContext;
    private AdFileServer mAdBootFileServer;
    
	private static AdFileManager mAdBootFileManager;
	public  static void Init(Context context) throws AdSDKManagerException{
		  if(AdSDKManager.IsInited())return;
		  if(context == null)throw new AdSDKManagerException("AdBootFileManager context is null !!!!!");
		  mAdBootFileManager = new AdFileManager(context);
	}
	public static AdFileManager getInstance(){
	      return mAdBootFileManager;
	}
    private AdFileManager(Context context){
  	      mContext = context;
  	      mAdBootFileServer = new AdFileServer(mContext);
    }
    
    /*Interface for Application*/
    public boolean UseAble(){
    	return mAdBootFileServer.UseAble();
    }
    
    public File GetBasePath(){
    	return mAdBootFileServer.GetBasePath();
    }
    
    public synchronized boolean writeSerializableData(String fileName, Object o,PublisherId id){
    	//Log.d("writeSerializableData() name="+fileName+" UseAble="+UseAble()+" GetBasePath()="+GetBasePath());
    	if(!(UseAble()||GetBasePath()==null))return false;//when no space to Save file we should return fail.     	
    	if(id == null || !id.CheckId())return false;
    	if(fileName==null  
    			|| "".equals(fileName))  
    		return false;//we should return when null to be read.
    	return mAdBootFileServer.writeSerializableData(fileName, o ,id);
    }
    
    public synchronized Object readSerializableData(String fileName,PublisherId id){
    	//Log.d("readSerializableData() name="+fileName +" UseAble="+UseAble()+" GetBasePath()="+GetBasePath());
    	if(!(UseAble()||GetBasePath()==null) ||id == null || !id.CheckId())return null;//no sapce to read file .    	
    	if(fileName==null || "".equals(fileName))return null;//nothing to be return
    	return mAdBootFileServer.readSerializableData(fileName,id);
    }
    //for report count.
    public synchronized void ReSetNum(PublisherId id){
    	mAdBootFileServer.ReSetNum(id);
    }
    public synchronized void AddReportNum(PublisherId id){
    	mAdBootFileServer.AddReportNum(id);
    }
    public synchronized int GetNum(PublisherId id){
    	return mAdBootFileServer.GetNum(id);
    }
}
