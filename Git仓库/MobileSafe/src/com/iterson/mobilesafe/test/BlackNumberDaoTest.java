package com.iterson.mobilesafe.test;

import java.util.Random;

import android.test.AndroidTestCase;

import com.iterson.mobilesafe.db.BlackNumberOpenHelper;
import com.iterson.mobilesafe.db.dao.BlackNumberDao;
/**
 * 单元测试 BlackNumberDaoTest
 * @author Yang
 *
 */
public class BlackNumberDaoTest extends AndroidTestCase {
	//方法必须是public
	public void testCreateDb(){
		BlackNumberOpenHelper helper = new BlackNumberOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	public void testAdd(){
		Random random =new Random();
		
		for (int i = 0; i < 100; i++) {
		int mode =random.nextInt(3)+1;
			if (i<10) {
				BlackNumberDao.getInstance(getContext()).add("9877654321"+i+i, mode);
			}else {
				BlackNumberDao.getInstance(getContext()).add("987654321"+i,mode);
			}
		}
		
		
		
	}
	public void testUpdate(){
		BlackNumberDao.getInstance(getContext()).update("100",3);
	}
	public void testFindAll(){
		boolean b = BlackNumberDao.getInstance(getContext()).find("100");
		assertEquals(true, b);//判断返回值是否和期望相符，如果符合，测试通过
		
	}
	public void testFindeMode(){
		int mode = BlackNumberDao.getInstance(getContext()).findMode("100");
		System.out.println(mode);
	}
	
}	
