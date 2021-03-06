package com.example.luhongcheng.secondclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.luhongcheng.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FifthFragment extends Fragment implements View.OnClickListener {

	private List<SecondClass> newsList;
	private SecondClassAdapter adapter;
	private Handler handler;
	private ListView lv;

	private OkHttpClient okHttpClient;
	private OkHttpClient.Builder builder;
	List<String> cookies;
	private ProgressBar progressBar;
	String str;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.layout_class_item, container, false);
		return v;
	}


	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		newsList = new ArrayList<>();
		lv = (ListView) getView().findViewById(R.id.news_lv);

		Button sendpostdata = (Button) getView().findViewById(R.id.send_request);
		sendpostdata.setOnClickListener(this);
		builder = new OkHttpClient.Builder();
		okHttpClient = builder.build();
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNormal) ;

		Button button = (Button) getActivity().findViewById(R.id.send_request);

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1){
					progressBar.setVisibility(View.GONE);
					adapter = new SecondClassAdapter(getActivity(),newsList);
					lv.setAdapter(adapter);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							SecondClass news = newsList.get(position);
							Intent intent = new Intent(getActivity(),SecondClassDisplayActvivity.class);
							intent.putExtra("news_url",news.getA2());
							startActivity(intent);
							//Intent intent = new Intent(MainActivity.this,NewsDisplayActvivity.class);
							//intent.putExtra("news_url",news.getNewsUrl());
							//startActivity(intent);

							//Intent intent2 = new Intent(MainActivity.this,NewsDisplayActvivity.class);
							//intent2.putExtra("COOKIE",str);
							//startActivity(intent2);
							//此处不能传递COOKIE，可能会混淆
						}
					});
				}
			}
		};
		getCookies();
		postdata();
	}

	private void getCookies() {
		SharedPreferences spCount = getActivity().getSharedPreferences("SecondCookie", 0);
		//在fragment中用share方法要getActivity（）
		str = spCount.getString("cookie", "");
	}

	public void onClick(View v) {
		if (v.getId() == R.id.send_request) {
			postdata();
		}
	}

	public void postdata() {
		// 开启线程来发起网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final OkHttpClient client = new OkHttpClient().newBuilder()
							.followRedirects(false)//禁止重定向
							.followSslRedirects(false)//哈哈哈哈哈哈哈好开心啊
							.build();


					Request request4 = new Request.Builder()
							// 三创
							.url("http://sc.sit.edu.cn/public/activity/activityList.action?categoryId=ff8080814e241104014eb867e1481dc3")
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
							.header("Accept-Language", "zh-CN,zh;q=0.9")
							.header("Connection", "Keep-Alive")
							.header("Cookie", str)
							.header("Host", "sc.sit.edu.cn")
							.header("Upgrade-Insecure-Requests","1")
							// .header("Referer", "http://myportal.sit.edu.cn/userPasswordValidate.portal")
							.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3423.2 Safari/537.36")
							.build();
					Response response4 = client.newCall(request4).execute();
					String responseData4 = response4.body().string();
					getNews(responseData4);




					okHttpClient.newCall(request4).enqueue(new Callback() {
						@Override
						public void onFailure(okhttp3.Call call, IOException e) {
						}
						@Override
						public void onResponse(okhttp3.Call call, Response response) throws IOException {
							//Log.d("源代码", "onResponse: " + response.body().string().toString());
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


	private void getNews(final String responseData4){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Document doc = Jsoup.parse(responseData4);
					//Element url = doc.getElementById("pf8271");   //依据ID取值
					Elements link =  doc.getElementsByTag("li");

					for(int j = 2;j < link.size();j++){
						String A1 = link.get(j).select("a").text();
						System.out.println("A1"+A1.toString());

						String A2 = link.get(j).select("a").attr("href");
						A2 = "http://sc.sit.edu.cn"+A2;
						System.out.println("A2"+A1.toString());

						String A3 = link.get(j).select("span").text();
						System.out.println("A3"+A1.toString());

						SecondClass news = new SecondClass(A1,A2,A3);
						newsList.add(news);
					}


					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}


}
