package com.fileopener;


import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FileOpener extends ReactContextBaseJavaModule {
  
  private Context context;

  public FileOpener(ReactApplicationContext reactContext) {
    super(reactContext);
    context = reactContext;
  }

  @Override
  public String getName() {
    return "FileOpener";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    return constants;
  }

  @ReactMethod
  public void open(String fileArg, String contentType, Promise promise) throws JSONException {
  		File file = new File(fileArg);

  		if (file.exists()) {
  			try {
          Uri path = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".fileprovider", file);
  				Intent intent = new Intent(Intent.ACTION_VIEW);
  				intent.setDataAndType(path, contentType);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
  				getReactApplicationContext().startActivity(intent);

                promise.resolve("Open success!!");
  			} catch (android.content.ActivityNotFoundException e) {
                promise.reject("Open error!!");
  			}
  		} else {
            promise.reject("File not found");
  		}
  	}
  
    @ReactMethod
    public void openApp(String packagename, Promise promise) throws JSONException {
       PackageInfo packageinfo = null;  
        try {
          packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);

          if (packageinfo == null) {
            promise.reject("Open error!!");
          } else {
            // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent 
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
            resolveIntent.setPackage(packageinfo.packageName);  

            // 通过getPackageManager()的queryIntentActivities方法遍历 
            List<ResolveInfo> resolveinfoList = context.getPackageManager()  
                    .queryIntentActivities(resolveIntent, 0);  

            ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
            if (resolveinfo != null) {  
                // packagename = 参数packname 
                String packageName = resolveinfo.activityInfo.packageName;  
                // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname] 
                String className = resolveinfo.activityInfo.name;  
                // LAUNCHER Intent 
                Intent intent = new Intent(Intent.ACTION_MAIN);  
                intent.addCategory(Intent.CATEGORY_LAUNCHER);  
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 设置ComponentName参数1:packagename参数2:MainActivity路径 
                ComponentName cn = new ComponentName(packageName, className);  

                intent.setComponent(cn);  
                getReactApplicationContext().startActivity(intent);  
            }
            promise.resolve("Open success!!"); 
          }          
        } catch (NameNotFoundException e) {  
          promise.reject("Open error!!");
          // e.printStackTrace();
        }  
    }

    @ReactMethod
    public void isInstalledApp(String packagename, Promise promise) throws JSONException {
       PackageInfo packageinfo = null;  
        try {
          packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
          if (packageinfo == null) {
            promise.reject("Open error!!");
          } else {            
            promise.resolve("Open success!!"); 
          }          
        } catch (NameNotFoundException e) {  
          promise.reject("Open error!!");          
        }  
    }
}
