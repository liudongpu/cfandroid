package com.musha.chifaer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChifaerActivity extends Activity {
    /** Called when the activity is first created. */
	WebView wv;
	ProgressDialog pd;
	private static final int  GUI_STOP_NOTIFIER = 1; 
	private static final long SPLASHTIME = 3000; 
	private LinearLayout splash;
	private Handler splashHandler = new Handler() { 
	    public void handleMessage(Message msg) { 
	         switch (msg.what) { 
		case ChifaerActivity.GUI_STOP_NOTIFIER:
			//loadmain();
			
			//test
			splash=(LinearLayout)findViewById(R.id.splashscreen);
			splash.setVisibility(View.GONE); 
			//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			Thread.currentThread().interrupt();
			break;
		}
		super.handleMessage(msg);
	    } 
	}; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new Thread() {
			public void run() {
				Message m = new Message();
				m.what = ChifaerActivity.GUI_STOP_NOTIFIER;
				//some initialization
				HttpTest(ChifaerActivity.this);   
		        if( isNetworkAvailable( ChifaerActivity.this) ){
		        init();//执行初始化函数
		        DisplayMetrics dm = new DisplayMetrics(); 
		        dm = getResources().getDisplayMetrics(); 
		        int screenWidth = dm.widthPixels; 
		        int screenHeight = dm.heightPixels; 
		        String piplString=GetPIPL(); 
		        String localityString="";
		        localityString=getLocality(piplString.split(",")[0],piplString.split(",")[1]);
		        String pt="null";
		        pt=NetworkMethod(ChifaerActivity.this);
		        String otherUrlString="?pw="+String.valueOf(screenWidth)+"&ph="+String.valueOf(screenHeight)+"&pl="+localityString+"&pt="+pt;
		        //String otherUrlString="?pw="+String.valueOf(screenWidth)+"&ph="+String.valueOf(screenHeight)+"&pi="+piplString+"&pt="+NetworkMethod(ChifaerActivity.this);
		        //loadurl(wv,"http://192.168.1.200/mobile/"+otherUrlString);
		        //loadurl(wv,"http://www.chifaer.com/mobile/"+otherUrlString);
		        //loadurl(wv,"http://www.chifaer.com/mobile/?pw=480&ph=800&pl=&pt=WIFI");
		        loadurl(wv,"http://www.baidu.com");
		        }
				//Chifaer2Activity.this.splashHandler.sendMessage(m);
				splashHandler.sendMessageDelayed(m, SPLASHTIME);  

			}
		}.start();
        
    }
    public void init(){//初始化
    	wv=(WebView)findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);//可用JS
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        
        wv.setWebViewClient(new WebViewClient(){   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadurl(view,url);//载入网页
                return true;   
            }//重写点击动作,用webview载入
 
        });
 
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
            wv.goBack();   
            return true;   
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
        	ConfirmExit();//按了返回键，但已经不能返回，则执行退出确认
        	return true; 
        }   
        return super.onKeyDown(keyCode, event);   
    }
    public void ConfirmExit(){//退出确认
    	AlertDialog.Builder ad=new AlertDialog.Builder(ChifaerActivity.this);
    	ad.setTitle("退出");
    	ad.setMessage("是否退出软件?");
    	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按钮
			@Override
			public void onClick(DialogInterface dialog, int i) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				ChifaerActivity.this.finish();//关闭activity
			}
		});
    	ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				//不退出不用执行任何操作
				dialog.dismiss();
			}
		});
    	ad.show();//显示对话框
    }
    public void loadurl(final WebView view,final String url){
    	new Thread(){
        	public void run(){
        		view.loadUrl(url);//载入网页
        	}
        }.start();
    }
    public static boolean isNetworkAvailable( Activity mActivity ) { 
        Context context = mActivity.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {   
          return false;
        } else {  
            NetworkInfo[] info = connectivity.getAllNetworkInfo();    
            if (info != null) {        
                for (int i = 0; i < info.length; i++) {           
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {              
                        return true; 
                    }        
                }     
            } 
        }   
        return false;
    }
    public static String NetworkMethod( Activity mActivity ) { 
        Context context = mActivity.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) 
        {    
          return "null";
        } 
        else 
        {  
        	NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();    
        	if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
        	{  
                return "WIFI";    //返回1是 WIFI网络 
        	}
        	else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
                
                
                return "MOBILE";    //返回 2是移动互联网（）  
            }
        	else {  
                
                return "UNKNOWN";    //返回3是 未知网络  
            }  
        }   
    }
    /**
     * 检测网络是否存在
     */
    public static void HttpTest(final Activity mActivity)
    {
  	  if( !isNetworkAvailable( mActivity) ){
        AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
        builders.setTitle("Opps!!,请检查网络连接！！");
        LayoutInflater _inflater = LayoutInflater.from(mActivity);
        View convertView = _inflater.inflate(R.layout.main,null);
        builders.setView(convertView);
        builders.setPositiveButton("确定",  new DialogInterface.OnClickListener(){
          public void onClick(DialogInterface dialog, int which)
          {
          	dialog.dismiss();
            mActivity.finish();
          }       
        });
        builders.show();
      }
     }
    /*
     * get “latitue,longitude”
     * 
     * */
    public String GetPIPL() 
    { 
    	LocationManager loctionManager;
        String contextService=Context.LOCATION_SERVICE;
        //通过系统服务，取得LocationManager对象
        loctionManager=(LocationManager) getSystemService(contextService);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        //从可用的位置提供器中，匹配以上标准的最佳提供器
        String provider = loctionManager.getBestProvider(criteria, true);
        Double latitude=0.0;
        Double longitude=0.0;
        //获得最后一次变化的位置
        if(provider!=null)
        {
        	try{
		        Location location = loctionManager.getLastKnownLocation(provider);
		        latitude=location.getLatitude();//纬度
		        longitude=location.getLongitude();//经度
        	}
        	catch(Exception ex)
  	      {
        		ex.getMessage();
  	      }
        }
        return String.valueOf(latitude)+","+String.valueOf(longitude);
    }
   
        private static StringBuffer getJSONData(String urlPath)
        {
		
		try {
			URL url = new URL(urlPath);
			
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setReadTimeout(5000);
			httpURLConnection.setRequestMethod("GET");
			if(httpURLConnection.getResponseCode() == 200){
				InputStream inputStream = httpURLConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);
				String temp = null;
				StringBuffer jsonsb = new StringBuffer();
				while((temp = br.readLine()) != null){
					jsonsb.append(temp);
					
				}
				return jsonsb;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
        }
/**
 * 根据经纬度获得地址
 * @param latitude
 * @param longitude
 * @return
 */
public static StringBuffer getCurrentAddressByGPS(String latitude,String longitude){
	
	String url = String.format(  
    	    "http://ditu.google.cn/maps/geo?output=json&key=abcdef&q=%s,%s",  
    	    latitude, longitude);  
		
	return getJSONData(url);
}
/*
 * 根据经纬度获取省份
 * */
public String getLocality(String latitude,String longitude)
{
	StringBuffer stringBuffer = new StringBuffer();  
    stringBuffer=getCurrentAddressByGPS(latitude,longitude);
    try {
    	if(stringBuffer != null){  
		JSONObject jsonAllData = new JSONObject(stringBuffer.toString());
		/** 
         * 获得一个长度为1的JSON数组,如:[{数据内容}] 
         */  
        String placemarkStr = jsonAllData.getString("Placemark");  
        /** 
         * 将placemarkStr数组类型字符串构造成一个JSONArray对象 
         */  
        JSONArray placemarkArray = new JSONArray(placemarkStr);  
        /** 
         * Placemark标签内容是一个长度为1的数组,获得数组的内容并转换成字符串 
         */  
        String jsonDataPlacemarkStr = placemarkArray.get(0).toString();  
        /** 
         * 对上面得到的JSON数据类型的字符串(jsonDataPlacemarkStr)进行解析 
         */  
        JSONObject jsonDataPlacemark = new JSONObject(jsonDataPlacemarkStr);  
        /** 
         * 获得标签AddressDetails的JSON数据 
         */  
        String jsonAddressDetails = jsonDataPlacemark.getString("AddressDetails");  
        /** 
         * 对上面得到的JSON数据类型的字符串(jsonAddressDetails)进行解析 
         */  
        JSONObject jsonDataAddressJDetails = new JSONObject(jsonAddressDetails);  
        /** 
         * 获得标签Country的JSON数据 
         */  
        String jsonCountry = jsonDataAddressJDetails.getString("Country");  
          
          
        /** 
         * 对上面得到的JSON数据类型的字符串(jsonCountry)进行解析 
         */  
        JSONObject jsonDataCountry = new JSONObject(jsonCountry);  
          
          
        /** 
         * 获得标签AdministrativeArea的JSON数据 
         */  
        String jsonAdministrativeArea = jsonDataCountry.getString("AdministrativeArea");  
          
        /** 
         * 对上面得到的JSON数据类型的字符串(jsonAdministrativeArea)进行解析 
         */  
        JSONObject jsonDataAdministrativeArea = new JSONObject(jsonAdministrativeArea);  

        /** 
         * 获得标签Locality的JSON数据 
         */  
        String jsonLocality = jsonDataAdministrativeArea.getString("Locality"); 
        JSONObject jsonDataLocality = new JSONObject(jsonLocality);  
        
        
        /** 
         * 设置LocalityName 
         */  
        String localString=jsonDataLocality.getString("LocalityName");

        return localString; 
    	}
    	return "";
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "";
	}  
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	 MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	        return true;
	//return super.onCreateOptionsMenu(menu);
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
    case R.id.item01:
        // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('food')");
    		}
    		}.start();
        break;
    case R.id.item02:
        // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('buy')");
    		}
    		}.start();
        break;
    case R.id.item03:
        // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('diary')");
    		}
    		}.start();
        break;
    case R.id.item04:
            // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('user')");
    		}
    		}.start();
            break;
    case R.id.item05:
        // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('about')");
    		}
    		}.start();
        break;
    case R.id.item06:
        // TODO
    	new Thread() {
    		public void run() {
    		wv.loadUrl("javascript:KeyMobileShow('noun')");
    		}
    		}.start();
        break;
        }
        return true;
	//return super.onOptionsItemSelected(item);
}

}