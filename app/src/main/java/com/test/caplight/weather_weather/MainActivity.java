package com.test.caplight.weather_weather;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.caplight.weather_weather.util.GetData;
import com.test.caplight.weather_weather.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    private RadioGroup rg_tab_bar;
    private RadioButton rb0;
    private RadioButton rb1;
    private RadioButton rb2;
    private ViewPager vpager;
    private ImageView bingPicImg;
    private MyFragmentPagerAdapter mAdapter;
    private List<Fragment> fragments;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isNetworkConnected()){
            showDialog();
        }
        Fragment1 f1 = new Fragment1();
        Fragment2 f2 = new Fragment2();
        Fragment3 f3 = new Fragment3();
        fragments = new ArrayList<>();
        bindViews();
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        vpager = findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(this);
        rb1.setChecked(true);
        vpager.setCurrentItem(1);
        bingPicImg=findViewById(R.id.bing_pic_img);
        try {
            getImgUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        rg_tab_bar = findViewById(R.id.rg_tab_bar);
        rb0 =  findViewById(R.id.rb0);
        rb1 = findViewById(R.id.rb1);
        rb2 =  findViewById(R.id.rb2);
        rg_tab_bar.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb0:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb1:
                vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.rb2:
                vpager.setCurrentItem(PAGE_THREE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb0.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb1.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb2.setChecked(true);
                    break;
            }
        }
    }

    private boolean isNetworkConnected() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());

    }

    private void showDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.my_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btn_setting = view.findViewById(R.id.btn_setting);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivityForResult(intent, 0);
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getImgUrl() throws Exception {
        String url="https://bing.com";
        String weatherUrl="https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(responseText);
                    JSONArray jsonArray=jsonObject.getJSONArray("images");
                    JSONObject data=jsonArray.getJSONObject(0);
                    String url="https://cn.bing.com"+data.get("url");
                    url = url.replace("1920x1080","480x800");
                    getImg(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getImg(String url) throws Exception {
        System.out.println(url);
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();//得到图片的流
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bingPicImg.setImageBitmap(bitmap);
                    }
                });
            }
        });


    }
}
