package com.example.lazyrolls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        this.customSimpleAdapterListView();
    }

    private void customSimpleAdapterListView()
    {
        setTitle("Информация");

        String[] titleArr = {
                "Проект LazyRolls",
                "Приложение LazyRolls",
                "Журнал изменений",
                "Посмотреть в Play Store",
                "Посмотреть на Github",
                "Пожертвовать разработчику приложения",
                "Сведения о конфидециальности",
                "Связь с разработчиками"};
        String[] descArr = {
                "Моторизированный привод для рулонных штор.",
                "Управление приводами на прошивке LazyRolls",
                "Текущая версия 1.0 Prerelease",
                "Спасибо за ваши оценки и отзывы",
                "Да, всё верно: это приложение с открытым исходным кодом",
                "Выразите свою признательность, это способствует развитию проекта",
                "Нет, я не краду ваши данные",
                "t.me/lazyrolls"};
        int[] iconArr = {
                R.drawable.lazy,
                R.drawable.app_lazyrolls_2,
                R.drawable.baseline_published_with_changes_24,
                R.drawable.playstore,
                R.drawable.github,
                R.drawable.gift_box,
                R.drawable.police,
                R.drawable.telegram};

        ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

        int titleLen = titleArr.length;
        for(int i =0; i < titleLen; i++) {
            Map<String,Object> listItemMap = new HashMap<String,Object>();
            listItemMap.put("imageId", iconArr[i]);
            listItemMap.put("title", titleArr[i]);
            listItemMap.put("description", descArr[i]);
            itemDataList.add(listItemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,R.layout.activity_info,
                new String[]{"imageId","title","description"},new int[]{R.id.infoImage, R.id.infoTitle, R.id.infoDesc});

        ListView listView = (ListView)findViewById(R.id.listViewExample);
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                HashMap clickItemMap = (HashMap)clickItemObj;
                String itemTitle = (String)clickItemMap.get("title");
                String itemDescription = (String)clickItemMap.get("description");
                if (itemTitle == "Проект LazyRolls") {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://imlazy.ru/rolls/"));
                    startActivity(launchBrowser);
                } else if (itemTitle == "Приложение LazyRolls") {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                    builder.setTitle("О приложении LazyRolls");
                    builder.setMessage("Приложение было создано в порыве изучить принципы создания приложений для мобильных устройств и попутно принести пользу сообществу LazyRolls.\n" +
                                       "Поэтому приложение предоставляется - как есть!\n" +
                                       "Поддержка приложения будет осуществляться по мере возможностей.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (itemTitle == "Посмотреть на Github") {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/samoswall/LazyRoll-Android-App"));
                    startActivity(launchBrowser);
                } else if(itemTitle == "Пожертвовать разработчику приложения") {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yoomoney.ru/fundraise/b8GYBARCVRE.230309"));
                    startActivity(launchBrowser);
                } else if(itemTitle == "Сведения о конфидециальности") {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                    builder.setTitle("Приложение LazyRolls");
                    builder.setMessage("Не собирает какие-либо личные или неперсонифицированные данные.\n" +
                                       "Разработчику не отправляется никакая информация.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if(itemTitle == "Связь с разработчиками") {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/lazyrolls"));
                    startActivity(launchBrowser);
                }
            }
        });

    }

}