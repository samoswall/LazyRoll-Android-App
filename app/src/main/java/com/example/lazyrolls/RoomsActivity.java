package com.example.lazyrolls;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomsActivity extends AppCompatActivity {

    ArrayList<Rooms> DeviceinRoom = new ArrayList<Rooms>();
    ArrayList<Device_state> Devices = new ArrayList<>();
    ArrayList<Device_state> Devices_temp = new ArrayList<>();
    Devices_adapter adapter;
    ArrayList<XML_answer> Devices_state = new ArrayList<>();
    ArrayList<Device_state> ScanDevices = new ArrayList<>();
    int room_position;
    Timer request_timer = new Timer();
    int [] dev_offline = new int[20]; // счетчик устройств - 3 периода таймера нет ответа - значит offline
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        DeviceinRoom = new ArrayList<Rooms>();
        Devices = new ArrayList<>();
        Devices_temp = new ArrayList<>();
        ScanDevices = new ArrayList<>();
        Devices_state = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Devices_state.add(new XML_answer("<?xml version='1.0'?><Curtain><Info><Version>0.14</Version><IP>192.168.1.30</IP><Name>Штора тест</Name><Hostname>lazyroll.test</Hostname><Time>01:21:01 [Сб]</Time><UpTime>0d 05:13</UpTime><RSSI>-66 дБм</RSSI><MQTT>Выключен</MQTT><Log>4</Log></Info><ChipInfo><ID>547dc8</ID><FlashID>1640ef</FlashID><RealSize>4 МБ</RealSize><IdeSize>4 МБ</IdeSize><Speed>40МГц</Speed><IdeMode>DOUT</IdeMode></ChipInfo><RF><LastCode>0</LastCode><Hex>0</Hex></RF><Position><Now>0</Now><Dest>0</Dest><Max>11300</Max><End1>вкл</End1></Position><LED><Mode>Выключен</Mode><Level>Низкая</Level></LED></Curtain>"));
             // Временное решение, иначе ошибка записи во 2ю строку, если первой нет
        }


        // передача инфы из MainActivity
        Intent intent = getIntent();
        room_position = intent.getIntExtra("room_position", -1);
        String name_room = intent.getStringExtra("room_name");
        Devices_temp = (ArrayList<Device_state>) intent.getSerializableExtra("room_devices");
        if (Devices_temp != null) {
            for (int i = 0; i < Devices_temp.size(); i++) {
                Devices.add(Devices_temp.get(i));
            }
        }
        setTitle(name_room);


        RecyclerView recyclerView = findViewById(R.id.linear_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Devices_adapter(this, Devices, Devices_state, dev_offline);
        recyclerView.setAdapter(adapter);


    }


    //Таймер каждые 2 секунды
    public void Start_timer() {
        request_timer= new Timer();
        request_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Вызов каждые 2 секунды
                for (int i = 0; i < Devices.size(); i++) {
                    xmlASyncGet(Devices.get(i).Ip, i);
                    if (dev_offline[i] > 0) {dev_offline[i] -= 1;}
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();                                                 // Обновление адаптера
                    }
                });
            }
        }, 1000, 2000);
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
            openScanActivity();  // Запуск ScanActivity
        }
        return true;
    }
    // Запуск ожидания ответа от ScanActivity
    ActivityResultLauncher<Intent> launchScanActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Получаем от ScanActivity
                        ScanDevices  = (ArrayList<Device_state>) data.getSerializableExtra("keyScan");
                        for (int i=0; i<ScanDevices.size(); i++) {
                            Devices.add(ScanDevices.get(i));

                        }

                    }
                }
            });

    public void openScanActivity() {
        Intent intent = new Intent(RoomsActivity.this, ScanActivity.class);
        launchScanActivity.launch(intent);
    }

    @Override
    public void onBackPressed() {
        // Отдаем данные в MainActiviti
        Intent intent = getIntent();
        intent.putExtra("keyRoom", Devices);
        intent.putExtra("keyRoomPos", room_position);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        Start_timer();
        super.onStart();
    }

    @Override
    public void onPause() {
        if(request_timer != null) {       // стоп таймер при уходе со страницы
            request_timer.cancel();
            request_timer = null;
        }
        super.onPause();
    }



    public void xmlASyncGet(String url, int line_dev) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://" + url + "/xml")
                            .build();
                    Response response = null;
                    String responseData = "null";
                    try {
                        response = client.newCall(request).execute();
                        responseData = response.body().string();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (responseData.indexOf("Curtain") > 1) {
                        Devices_state.set(line_dev, new XML_answer(responseData));
                        Devices.get(line_dev).Now = Devices_state.get(line_dev).Now;
                        dev_offline[line_dev] = 3;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                       //         adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }).start();
    }
}