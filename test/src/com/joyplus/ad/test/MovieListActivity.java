package com.joyplus.ad.test;

import greendroid.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joyplus.ad.test.entity.MovieInfo;
import com.joyplus.ad.test.ui.HorizontalScrollView;
import com.joyplus.ad.test.ui.OnScrollChangeListener;
import com.joyplus.ad.test.ui.WaitingDialog;
import com.joyplus.ad.test.util.HttpTools;
import com.joyplus.ad.test.util.Log;

public class MovieListActivity extends Activity{

	private static final String TAG = MovieListActivity.class.getName();
	private static final int DIALOG_WAITING = 0;
	private static final int MESSAGE_GETDATA_SUCCESS = 0;
	private static final int MESSAGE_GETDATA_FAILED = MESSAGE_GETDATA_SUCCESS + 1;
	
	private LinearLayout mLinearLayout;
	private HorizontalScrollView mScrollView;
	private HorizontalScrollView mScrollViewBackground;
	private AsyncImageView mBackgroundImage;
	private int mScreenWidth;
	private float mDensity;
	private List<MovieInfo> mMovies = new ArrayList<MovieInfo>();
	private String mBackGroundUrl = null;
//	private String[] resorce_urls = {"http://www.tvptv.com/UpNewImg/42%28146%29.jpg",
//			"http://p.ganyou.com/attachment/image/2011/04/24/121323605.jpg",
//			"http://a3.att.hudong.com/06/48/01300000931713128019481868712.jpg",
//			"http://photocdn.sohu.com/20100612/Img272757506.jpg"};
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_GETDATA_SUCCESS:
				removeDialog(DIALOG_WAITING);
				initMovieList();
				break;
			case MESSAGE_GETDATA_FAILED:
				removeDialog(DIALOG_WAITING);
				Toast.makeText(MovieListActivity.this, "get data failed", Toast.LENGTH_SHORT).show();
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mDensity = displayMetrics.density;
        mLinearLayout = (LinearLayout) findViewById(R.id.layout);
        mScrollView = (HorizontalScrollView) findViewById(R.id.hscroll);
        mScrollViewBackground = (HorizontalScrollView) findViewById(R.id.hscroll_back);
        mBackgroundImage = (AsyncImageView) findViewById(R.id.bg);
//        mBackgroundImage.setLayoutParams(new LinearLayout.LayoutParams((int) (mScreenWidth*1.3), LinearLayout.LayoutParams.MATCH_PARENT));
        mScrollView.setOnScrollChangeListener(new MyOnScrollChangeListener());
        String mServerUrl = getIntent().getStringExtra("url");
        if(mServerUrl == null){
        	Log.e(TAG, "url is null");
        	finish();
        }
        showDialog(DIALOG_WAITING);
        new Thread(new GetDadaRunnable(mServerUrl)).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("Main", mScrollView.getScrollX()+"");
		return super.onKeyDown(keyCode, event);
	}
    
    private void initMovieList(){
    	mBackgroundImage.setUrl(mBackGroundUrl);
    	int width = (int) (mScreenWidth-40*mDensity)/5;
    	int height = (int) (width*4/3 + 40*mDensity);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
    	for (int i = 0; i < mMovies.size(); i++) {
    		MovieInfo info = mMovies.get(i);
    		View view = LayoutInflater.from(MovieListActivity.this).inflate(R.layout.layout_item_grid, null);
    		AsyncImageView image = (AsyncImageView) view.findViewById(R.id.item_image);
    		TextView text = (TextView) view.findViewById(R.id.movie_name);
    		text.setText(info.getName());
    		image.setUrl(info.getPictureUrl());
    		final int posistion  = i;
    		image.setOnClickListener(new View.OnClickListener() {
					
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(MovieListActivity.this, "click item " + posistion, Toast.LENGTH_SHORT).show();
				}
			});
    		view.setLayoutParams(params);
    		mLinearLayout.addView(view);
		}
    	mLinearLayout.requestFocus();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
    	switch (id) {
		case DIALOG_WAITING:
			WaitingDialog dlg = new WaitingDialog(this);
			dlg.show();
			dlg.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			dlg.setDialogWindowStyle();
			return dlg;
		default:
			return super.onCreateDialog(id);
		}
    }
    
    class MyOnScrollChangeListener extends OnScrollChangeListener{

    	private int mLastX = 0;
    	private int mLastOldx = 0;
		@Override
		public void onScrollChanged(HorizontalScrollView scrollView, int x, int y,
				int oldx, int oldy) {
			// TODO Auto-generated method stub
			int dx = x*(mBackgroundImage.getWidth() - mScreenWidth)/(mLinearLayout.getWidth() - mScreenWidth);
			if(x>mLastX){
				if(mLastOldx<=oldx){
					mScrollViewBackground.smoothScrollTo(dx, mScrollViewBackground.getScrollY());
					Log.d(TAG, "x =" + x + "\toldx = " + oldx + "\tdx = " + dx + "\tmLastX = " + mLastX + "\tmLastOldx" + mLastOldx);
					mLastX = x;
					mLastOldx = oldx;
				}
			}else if(x<mLastX){
				if(mLastOldx>=oldx){
					mScrollViewBackground.smoothScrollTo(dx, mScrollViewBackground.getScrollY());
					Log.d(TAG, "x =" + x + "\toldx = " + oldx + "\tdx = " + dx + "\tmLastX = " + mLastX + "\tmLastOldx" + mLastOldx);
					mLastX = x;
					mLastOldx = oldx;
				}
			}
		}
    }
    
    class GetDadaRunnable implements Runnable{

    	private String url;
    	
    	public GetDadaRunnable(String url) {
			// TODO Auto-generated constructor stub
    		this.url = url;
		}
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String result = HttpTools.get(MovieListActivity.this, url);
				Log.d(TAG, "result ----->" + result);
				JSONObject resultObj = new JSONObject(result);
				JSONObject _metaObj = resultObj.getJSONObject("_meta");
				if("00000".equals(_metaObj.get("code"))){
					mBackGroundUrl = resultObj.getString("creativeUrl");
					JSONArray items = resultObj.getJSONArray("items");
					for(int i=0; i<items.length(); i++){
						JSONObject item = items.getJSONObject(i);
						MovieInfo info = new MovieInfo();
						info.setId(Integer.valueOf(item.getString("id")));
						info.setName(item.getString("name"));
						info.setPictureUrl(item.getString("picUrl"));
						mMovies.add(info);
					}
					mHandler.sendEmptyMessage(MESSAGE_GETDATA_SUCCESS);
				}else{
					mHandler.sendEmptyMessage(MESSAGE_GETDATA_FAILED);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mHandler.sendEmptyMessage(MESSAGE_GETDATA_FAILED);
			}
			
		}
    }
}