package com.iterson.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.iterson.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * 获取安装的应用信息
 * @author Yang
 *
 */
public class AppInfoProvider {
	/**
	 * 获取已安装的应用信息
	 */
	public static ArrayList<AppInfo> getAppInfos(Context ctx){
		PackageManager pm =ctx.getPackageManager();//先得到包管理
		List<PackageInfo> packages = pm.getInstalledPackages(0);//获取已经安装的应用
		//创建一个list
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		
			
		for (PackageInfo packageInfo : packages) {//遍历所有安装的包
			AppInfo info = new AppInfo();
			
			String packageName = packageInfo.packageName;//得到包名
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;//得到包的信息
			Drawable icon = applicationInfo.loadIcon(pm);//包信息里面取出app图标
			int uid = applicationInfo.uid;//应用的uid
			String name = applicationInfo.loadLabel(pm).toString()+uid;//取出app名字 +他的uid
			int flags = applicationInfo.flags;//得到里面的标记
			
			
			//判断是否为用户安装的APP
			if ((flags&applicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {//是系统应用，用的是与运算
				info.isUserApp =false;
			}else {
				info.isUserApp =true;
			}
			
			//判断app存放在内存还是sd卡中
			if ((flags&applicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {//是手机还是sd卡中
				info.isRom =false;
			}else {
				info.isRom =true;
			}
			
			info.icon = icon;
			info.name =name;
			info.packgeName =packageName;
			info.uid = uid;
			
			list.add(info);
		}
		return list;
	}

}
