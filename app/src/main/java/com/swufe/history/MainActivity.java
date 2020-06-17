package com.swufe.history;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView tvDate;
    TextView tvDay;
    private String mYear;
    private String mMonth;
    private String mDay;
    private String mWay;
    private String getNewDate(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日  "+"  星期"+mWay;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        tvDate = findViewById(R.id.tvDate);
        tvDay = findViewById(R.id.tvDay);
        tvDate.setText(getNewDate());
        tvDay.setText(mDay);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData(mMonth,mDay);
            }
        }).start();
        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDlg();
            }
        });
    }

    public void showDatePickDlg () {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, final int monthOfYear, final int dayOfMonth) {
                Log.e("monthOfYear="+monthOfYear,"dayOfMonth="+dayOfMonth);
                tvDay.setText(dayOfMonth+"");
                mMonth =String.valueOf (monthOfYear+1);
                mDay =String.valueOf (dayOfMonth);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadData(mMonth,mDay);
                    }
                }).start();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private void loadData(String month,String day){
        List<History> list = new ArrayList<>();
        try {
            String url = "http://www.todayonhistory.com/"+month+"/"+day+"/";
            Log.e("url=",url);
            Connection connect = Jsoup.connect(url);//获取连接对象
            // 修改http包中的header,伪装成浏览器进行抓取
            connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = connect.get();
            Element container = doc.getElementById("container");
//            if (container!=null){
            Elements lis = container.getElementsByTag("li");
            if (lis==null){
                Toast.makeText(MainActivity.this,"没有相关数据",Toast.LENGTH_SHORT).show();
                return;
            }
            for (Element li:lis) {
                History history =new History();
                Element div = li.select("div").get(0);
                String title = div.select("a").attr("title");
                String detail = div.select("a").attr("href");
                history.setTitle(title);
                history.setDetail(detail);
                String span;
                if(div.attr("class").equals("pic")){
                    String img = div.select("a > img").first().attr("data-original");
                    String firstString = img.substring(0,1);
                    if (firstString.equals("h")){
                        history.setImage(img);
                    }else {
                        history.setImage("http://www.todayonhistory.com"+img);
                    }
                    span = div.select(".t").get(0).select("span").html();
                }else{
                    span = div.select("span").html();
                }
                history.setDate(span);
                if (!history.getTitle().isEmpty()){
                    list.add(history);
                }
            }
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        setData(list);
    }

    private void setData(final List<History> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HistoryAdapter adapter = new HistoryAdapter(MainActivity.this,list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("url",list.get(position).getDetail());
                        startActivity(intent);
                    }
                });
            }
        });

    }
}