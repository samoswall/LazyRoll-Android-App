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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomsActivity extends AppCompatActivity {

    ArrayList<Rooms> DeviceinRoom = new ArrayList<Rooms>();
    ArrayList<Device_state> Devices = new ArrayList<>();
    Devices_adapter adapter;

    ArrayList<Device_state> ScanDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        DeviceinRoom = new ArrayList<Rooms>();
        Devices = new ArrayList<>();
        ScanDevices = new ArrayList<>();

        // передача инфы из MainActivity
        Intent intent = getIntent();
        int line_room = intent.getIntExtra("room_position", -1);
        String name_room = intent.getStringExtra("room_name");
        setTitle(name_room);

//        Devices.add(new Device_state("192.168.1.111", "Штора виртуальная", "Roll-v", "4000", "8000", "false"));

        RecyclerView recyclerView = findViewById(R.id.linear_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Devices_adapter(this, Devices);
        recyclerView.setAdapter(adapter);
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
            openScanActivity();
        }
        return true;
    }

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
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    public void openScanActivity() {
        Intent intent = new Intent(RoomsActivity.this, ScanActivity.class);
        launchScanActivity.launch(intent);
    }
}