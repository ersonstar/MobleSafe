package com.iterson.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务状态工具类
 * @author Yang
 *
 */
public class ServiceStatusUtils {
		public static boolean isServiceRunning(Context ctx,String service){
			//通过ctx获取系统服务
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			//获得正在运行的服务
			List<RunningServiceInfo> runningServices = am.getRunningServices(100);
			
			for (RunningServiceInfo runningServiceInfo : runningServices) {
				//获取正在运行服务的名字
				String serviceName = runningServiceInfo.service.getClassName();
				if (serviceName.equals(service)) {
					return true;
				}
			}
			return false;
		}
}
