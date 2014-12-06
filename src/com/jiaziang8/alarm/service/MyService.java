package com.jiaziang8.alarm.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.R.string;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiaziang8.alarm.object.savedAlarmObject;
import com.jiaziang8.alarm.util.Constants;
import com.jiaziang8.alarm.util.MD5;

public class MyService {
	public static final String FRIENDS_URL = Constants.url
			+ "/AlarmServer2/GetFriendsServlet";
	public static final String ADD_FRIEND_URL = Constants.url
			+ "/AlarmServer2/AddFriendServlet";
	public static final String NOTIFICATION_URL = Constants.url
			+ "/AlarmServer2/NotificationServlet";
	public static final String REALLYADDFRIEND_URL = Constants.url
			+ "/AlarmServer2/ReallyAddFriendServlet";
	public static final String REFUSEADDFRIEND_URL = Constants.url
			+ "/AlarmServer2/RefuseAddFriendServlet";	
	public static final String REFUSEALARM_URL = Constants.url
			+"/AlarmServer2/RefuseAlarmServlet";
	public static final String DELETEFRIEND_URL = Constants.url
			+ "/AlarmServer2/DeleteFriendServlet";
	public static final String CHANGESAVEDALARMSTATE = Constants.url
			+ "/ChangeSavedAlarmStateServlet";
	public static final String GETREVIEWITEMS = Constants.url
			+ "/AlarmServer2/ReviewGetItemsServlet";
	public static final String GETHEADPATH = Constants.url
			+ "/AlarmServer2/GetHeadPathServlet";
	public static final String DELETEREVIEW = Constants.url
			+"/AlarmServer2/DeleteReviewServlet";
	public static final String HEADURL = Constants.url
			+"/AlarmServer2/UploadImgServlet";
	public static final String UPLOADALARMURL = Constants.url
			+"/AlarmServer2/UploadServlet";
	public static final String GETHEADURL = Constants.url
			+"/AlarmServer2/Head";
	public static final String REGISTURL = Constants.url
			+ "/AlarmServer2/RegistServlet";
	public static final String DOWNURL = Constants.url
			+ "/AlarmServer2/DownLoadServlet";
	public static final String CHECKURL = Constants.url
			+"/AlarmServer2/CheckServlet";
	public static final int COUNT = 10;

	//获取好友列表
	public static List<HashMap<String, Object>> getData(String account) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		HttpPost post = new HttpPost(FRIENDS_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("account", account));

		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				if (inputStream == null) {
					Log.v(Constants.LOG_TAG, "InputStream null!!~~~");
				}
				String result = ConvertToString(inputStream);
				if (result.length() == 0)
					return list;
				result = result.substring(0, result.length() - 1);
				String[] friendsStrings = result.split(" ");
				for (String friendNumber : friendsStrings) {
					HashMap<String, Object> map2 = new HashMap<String, Object>();
					String[] temStrings = friendNumber.split("@");
					if(temStrings.length==1){
						map2.put("path", "");
					}else{
						map2.put("path", temStrings[1]);
					}
					
					map2.put("number", temStrings[0]);
					String nameString = Contacts.getNamaByNumber(temStrings[0]);
					map2.put("name", nameString);
					list.add(map2);
				}
				return list;
			} else {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	//获取通知列表
	public static List<HashMap<String, Object>> getNotification(String account) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HttpPost post = new HttpPost(NOTIFICATION_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("account", account));
		Gson gson = new Gson();
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				if (inputStream == null) {
					Log.v(Constants.LOG_TAG, "InputStream null!!~~~");
				}
				// 返回的数据
				String result = ConvertToString(inputStream);
				ArrayList<savedAlarmObject> notificationList = gson.fromJson(
						result, new TypeToken<ArrayList<savedAlarmObject>>() {
						}.getType());

				for (savedAlarmObject notificationObject : notificationList) {
					HashMap<String, Object> map2 = new HashMap<String, Object>();
					map2.put("id", notificationObject.id);
					map2.put("account", notificationObject.account);
					String accountname = Contacts
							.getNamaByNumber(notificationObject.account);
					map2.put("friend", notificationObject.friend);
					map2.put("name", accountname);
					map2.put("mDays", notificationObject.mDays);
					map2.put("mHour", notificationObject.mHour);
					map2.put("mMinute", notificationObject.mMinute);
					map2.put("filename", notificationObject.filename);
					map2.put("time", notificationObject.time);
					map2.put("wordsToSay", notificationObject.wordsToSay);	
					map2.put("path", notificationObject.path);
					list.add(map2);
				}
				return list;
			} else {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	//获取日志列表
	public static List<HashMap<String, Object>> getItems(int start,
			String account) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HttpPost post = new HttpPost(GETREVIEWITEMS);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("account", account));
		param.add(new BasicNameValuePair("start", String.valueOf(start)));
		Gson gson = new Gson();
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				if (inputStream == null) {
					Log.v(Constants.LOG_TAG, "InputStream null!!~~~");
				}
				// 返回的数据
				String result = ConvertToString(inputStream);
				ArrayList<savedAlarmObject> notificationList = gson.fromJson(
						result, new TypeToken<ArrayList<savedAlarmObject>>() {
						}.getType());

				for (savedAlarmObject notificationObject : notificationList) {
					HashMap<String, Object> map2 = new HashMap<String, Object>();
					map2.put("id", notificationObject.id);
					map2.put("account", notificationObject.account);
					String accountname = Contacts
							.getNamaByNumber(notificationObject.account);
					map2.put("friend", notificationObject.friend);
					map2.put("name", accountname);
					map2.put("mDays", notificationObject.mDays);
					map2.put("mHour", notificationObject.mHour);
					map2.put("mMinute", notificationObject.mMinute);
					map2.put("filename", notificationObject.filename);
					map2.put("time", notificationObject.time);
					map2.put("wordsToSay", notificationObject.wordsToSay);
					list.add(map2);
				}
				return list;
			} else {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	
	//添加好友请求
	public static int addfriend(String user, String friend) {
		HttpPost post = new HttpPost(ADD_FRIEND_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("user", user));
		param.add(new BasicNameValuePair("friend", friend));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {

				return 1;
			} else if (response.getStatusLine().getStatusCode() == 201) {
				return 0;
			} else {
				return 2;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	//接受添加好友请求 添加好友
	public static boolean reallyAddFriend(String user, String friend) {
		HttpPost post = new HttpPost(REALLYADDFRIEND_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("user", user));
		param.add(new BasicNameValuePair("friend", friend));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//拒绝添加好友
	public static boolean refuseAddFriend(String user, String friend) {
		HttpPost post = new HttpPost(REFUSEADDFRIEND_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("user", user));
		param.add(new BasicNameValuePair("friend", friend));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean refuseAlarm(int id){
		HttpPost post = new HttpPost(REFUSEALARM_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		String idString = String.valueOf(id);
		param.add(new BasicNameValuePair("id", idString));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	//删除好友
	public static boolean deleteFriend(String user, String friend) {
		HttpPost post = new HttpPost(DELETEFRIEND_URL);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("user", user));
		param.add(new BasicNameValuePair("friend", friend));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//删除日志
	public static boolean deleteReview(int id){
		HttpPost post = new HttpPost(DELETEREVIEW);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		String idString = String.valueOf(id);
		param.add(new BasicNameValuePair("id", idString));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	//获取用户头像文件路径
	public String getHeadPathByAccount(String account) {
		HttpPost post = new HttpPost(GETHEADPATH);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("account", account));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				String result = ConvertToString(inputStream);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	//获取图片Uri
	public Uri getImageURI(String path, File cache) throws Exception {
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		File file = new File(cache, name);
		if (file.exists()) {
			return Uri.fromFile(file);
		} else {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				// 返回一个URI对象
				return Uri.fromFile(file);
			}
		}
		return null;
	}

	//Inputstream转String
	public static String ConvertToString(InputStream inputStream) {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder result = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null
					&& (!line.equals(""))) {
				result.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStreamReader.close();
				inputStream.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}
