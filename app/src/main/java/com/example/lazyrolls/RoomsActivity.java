package com.example.lazyrolls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RoomsActivity extends AppCompatActivity {

    private List<View> all_device;    //Создаем список устройств в комнате
    TextView textView;

    private String getRouterIPAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int ip = dhcp.gateway;
        return FormatIP(ip);
    }
    private String FormatIP(int ip) {
        return String.format(
                "%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff)  //,
                //  (ip >> 24 & 0xff)
        );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        all_device = new ArrayList<View>();     //инициализировали наш массив

        // передача инфы из MainActivity
        Intent intent = getIntent();
        String rooms_name = intent.getStringExtra("name_room");
        setTitle(rooms_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String msg = "";
        int itemId = item.getItemId();
        if (itemId == R.id.add_device) {
            msg = "Add clicked";
            final LinearLayout linear = (LinearLayout) findViewById(R.id.linear_devices);   //находим наш первый linear в activity_main.xml
            //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
            final View view = getLayoutInflater().inflate(R.layout.layout_device, null);

            TextView text_dev_name = (TextView) view.findViewById(R.id.text_device_name);
            text_dev_name.setText("Device name: " + "Roll");
            TextView text_dev_ip = (TextView) view.findViewById(R.id.text_device_ip);
            text_dev_ip.setText("Router LAN: " + getRouterIPAddress() + ".X");
            //добавляем все что создаем в массив
            all_device.add(view);
            //добавляем елементы в linearlayout
            linear.addView(view);

        }
        return true;
    }


}