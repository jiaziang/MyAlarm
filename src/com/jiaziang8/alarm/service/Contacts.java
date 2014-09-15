package com.jiaziang8.alarm.service;

import java.io.InputStream;
import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.example.R;
import com.jiaziang8.alarm.util.Constants;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;

public class Contacts {
	Context mContext = null;
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	/** 联系人名称 **/
	public static ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人号码 **/
	public  static ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	public  static ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
	
	public static Contacts instance;
	
	public  Contacts(Context context){
		this.mContext = context;	
		instance = this;
	}
	

	public void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				phoneNumber = phoneNumber.replaceAll(" ", "");
				if(phoneNumber.indexOf("+86")==0){
					phoneNumber = phoneNumber.substring(3);
				}
				if (TextUtils.isEmpty(phoneNumber) || isExited(phoneNumber))
					continue;

				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				Bitmap contactPhoto = null;

				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(mContext.getResources(),
							R.drawable.contact_photo);
					mContactsName.add(contactName);
					mContactsNumber.add(phoneNumber);
					mContactsPhonto.add(contactPhoto);
				}
			}
			if (Build.VERSION.SDK_INT > 14) {
				phoneCursor.close();
			}
		}
	}

	private boolean isExited(String phonenumber) {
		for (String number : mContactsNumber) {
			if (number.equals(phonenumber)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getNamaByNumber(String number){
		int i = 0;
		for (String theNumber:mContactsNumber){
			Log.v(Constants.LOG_TAG, "number is "+theNumber);
			Log.v(Constants.LOG_TAG, "name is"+mContactsName.get(i));
			if(number.trim().equals(theNumber.trim().replaceAll(" ", ""))||("+86"+number.trim()).equals(theNumber.trim().replaceAll(" ", ""))){			
				return mContactsName.get(i);
			}
			i++;
		}
		return number;
	}

}
