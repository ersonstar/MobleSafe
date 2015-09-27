package com.iterson.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.iterson.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		ListView lvContact = (ListView) findViewById(R.id.lv_contact);
		lvContact.setAdapter(new SimpleAdapter(this, list,
				R.layout.list_contact_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));

		
		// raw_contacts,data,mimetypes
		// 首先读取raw_contacts，获取contact_id，
		// 使用contact_id在data表中查询相关id的信息（包含电话和名称..),
		// 然后更具mimetype_id区分具体信息是哪种
		ContentResolver resolver = getContentResolver();// 获取内容解析者

		Cursor rawCursor = resolver.query(
				Uri.parse("content://com.android.contacts/data"),
				new String[] { "contact_id" }, null, null, null);

		if (rawCursor != null) {

			while (rawCursor.moveToNext()) {
				String contactId = rawCursor.getString(0);// 获取contact_id
				// 在查询data时，实际上查询的是view_data这个视图
				Cursor dataCursor = resolver.query(
						Uri.parse("content://com.android.contacts//data"),
						new String[] { "data1", "mimetype" },
						"raw_contact_id=?", new String[] { contactId }, null);

				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);

						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							// 表示是电话
							map.put("phone", data);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							// 说明是名字
							map.put("name", data);
						}
						list.add(map);

					}
				}
				dataCursor.close();
			}

		}
		rawCursor.close();

		
		lvContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String,String> map = list.get(position);
				String phone = map.get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			}
		});
		
		
		

	}
	

	
}
