package com.iterson.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.domain.ProcessInfo;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.media.Image;

public class ProcessInfoProvider {

	/**
	 * 获取真正运行的所有进程
	 * 
	 * @return
	 */
	public static ArrayList<ProcessInfo> getRunningProcess(Context cxt) {
		ActivityManager am = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		ArrayList<ProcessInfo> list = new ArrayList<ProcessInfo>();

		PackageManager pm = cxt.getPackageManager();// 得到包管理器，因为进程得到信息有包名，通过包管理器操作
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			ProcessInfo info = new ProcessInfo();
			String packageName = runningAppProcessInfo.processName;
			int pid = runningAppProcessInfo.pid;// 得到进程的id
			// 通过进程id得到进程信息
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { pid });// 通过am得到一个进程的信息，
																// 需要一个pids
																// 就是进程的id，在上面得到
			info.memory = processMemoryInfo[0].getTotalPrivateDirty()*1024;// 得到内存占有信息Return
																		// total
																		// private
																		// dirty
																		// memory
																		// usage
																		// in
																		// kB.返回为KB

			info.packageName = runningAppProcessInfo.processName;// 包名
			try {
				// 根据包名获取应用信息
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packageName, 0);// 异常
				// 有些系统进程没有图标和log
				info.icon = applicationInfo.loadIcon(pm);// 应用图标

				info.name = applicationInfo.loadLabel(pm).toString();// 应用名称
				int flags = applicationInfo.flags;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					info.isUserProcess = false;
				} else {
					info.isUserProcess = true;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				// 有些系统进程没有图标和log
				info.name = packageName;
				info.icon = cxt.getResources().getDrawable(
						R.drawable.icon_android);
				info.isUserProcess = false;

			}
			list.add(info);
		}

		return list;
	}

	/**
	 * 获取运行的进程数量
	 */
	public static int getRunningProcessNum(Context cxt) {
		ActivityManager am = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = am.getRunningAppProcesses(); // 得到运行的app数量
		return appList.size();
	}

	/**
	 * 获取剩余内存
	 */
	public static long getAvalilMemory(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();// 空对象
		am.getMemoryInfo(outInfo);// 需要一个memoryinfo我们new一个。 该方法在给我们的对象赋值
		return outInfo.availMem;// 剩余内存
	}

	/**
	 * 总共内存
	 */
	@SuppressWarnings("resource")
	public static long getTotalMemory(Context ctx) {
		/*
		 * 此方法在4.1以下系统无法识别 ActivityManager am = (ActivityManager)
		 * ctx.getSystemService(Context.ACTIVITY_SERVICE); MemoryInfo outInfo =
		 * new MemoryInfo(); am.getMemoryInfo(outInfo); return outInfo.totalMem;
		 */
		// 解决办法 读取/proc/meminfo文件第一行 获取内容大小
		try {
			FileReader in = new FileReader("/proc/meminfo");
			BufferedReader reader = new BufferedReader(in);
			String readLine = reader.readLine();
			char[] charArray = readLine.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			String strNum = sb.toString();
			long total = Long.parseLong(strNum) * 1024;// 已kb保存的 需要*1k
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 杀死所有服务
	 * 
	 * @param ctx
	 */
	public static void killAll(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<ProcessInfo> runningProcess = getRunningProcess(ctx);
		for (ProcessInfo processInfo : runningProcess) {
			if (processInfo.packageName.equals(ctx.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(processInfo.packageName);
		}
	}

}
