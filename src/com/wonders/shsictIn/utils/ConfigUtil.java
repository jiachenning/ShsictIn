package com.wonders.shsictIn.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;

import com.wonders.shsictIn.R;

public class ConfigUtil {
	protected static final String PREFS_NAME = "ShsictInSetting";
	protected static String SHSICT_URL = "SERVER_IP_PORT";
	public static String cachedServerIp = null;

	
	/**
	 * 
	 * @return eg http://1.1.1.1:8080
	 */
	public static String getCachedShsictURL(Activity activity) {
		if(cachedServerIp != null)
			return cachedServerIp;
		
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
		String url = settings.getString(SHSICT_URL, "ERROR");
		if (url.equals("ERROR")) {
			return null;
		} else {
			cachedServerIp = url;
			return cachedServerIp;
		}
	}

	/*public static boolean initShsictServiceURLString(){
		String configPath = Environment.getExternalStorageDirectory().getPath()+"/Shsict.config";
		try {
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(configPath));
			String ip = br.readLine();
			br.close();
			if(ip.trim().isEmpty()){
				return false;
			}else{
				defaultUrl =  "http://"+ip;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	*/
	public static AlertDialog showDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.input_server_url));
		LayoutInflater inflater = activity.getLayoutInflater();
		View customer_layout = inflater.inflate(R.layout.setting_view, null);
		final EditText input = (EditText) customer_layout.findViewById(R.id.url_setting);
		//取出当前的服务器地址ip
		String defalutUrl = getCachedShsictURL(activity);
		if(defalutUrl != null){
			input.setText(defalutUrl);
		}
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		builder.setView(customer_layout);

		// Set up the buttons
		builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = input.getText().toString();
				storeUrl(activity.getApplicationContext(), url);
				Toast.makeText(activity.getApplicationContext(), url + ", 设置完毕", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.show();
	}

	public static void storeUrl(Context ctx, String url) {
		cachedServerIp = url;
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SHSICT_URL, url);
		editor.commit();
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public static String getUIDFromCookie(String url){
		String uid = null;
		
		try {
			CookieManager cm = CookieManager.getInstance();
			String cookies = cm.getCookie(url);
			String[] cookieArray = cookies.split(";");
			for(String cookie : cookieArray){
				if(cookie.contains("uid")){
					int indexOfValue = cookie.indexOf("=") + 1;
					uid = cookie.substring(indexOfValue);
					break;
				}
			}
			if(uid == null || uid.trim().equals(""))
				return null;
						
			return uid;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
