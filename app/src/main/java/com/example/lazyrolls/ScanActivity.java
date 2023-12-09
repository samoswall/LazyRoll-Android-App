package com.example.lazyrolls;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ScanActivity extends AppCompatActivity {

    ArrayList<Device_state> scan_devices = new ArrayList<Device_state>();
    ArrayList<Device_state> check_devices = new ArrayList<Device_state>();
    Scan_adapter boxAdapter;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // создаем адаптер
        fillData();
        boxAdapter = new Scan_adapter(this, scan_devices);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.scan_lvMain);
        lvMain.setAdapter(boxAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId ==  android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    // генерируем данные для адаптера
    public void fillData() {
        for (int i = 1; i <= 254; i++) {
            String url = "http://" + getRouterIPAddress() + "." + i + "/xml";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
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
                        String dev_ip = responseData.substring(responseData.indexOf("<IP>") + 4, responseData.indexOf("</IP>"));
                        String dev_name = responseData.substring(responseData.indexOf("<Name>") + 6, responseData.indexOf("</Name>"));
                        String dev_hostname = responseData.substring(responseData.indexOf("<Hostname>") + 10, responseData.indexOf("</Hostname>"));
                        String dev_now = responseData.substring(responseData.indexOf("<Now>") + 5, responseData.indexOf("</Now>"));
                        String dev_max = responseData.substring(responseData.indexOf("<Max>") + 5, responseData.indexOf("</Max>"));
                        if (dev_name.length() == 0) dev_name = dev_hostname;
                        scan_devices.add(new Device_state(dev_ip, dev_name, dev_hostname, dev_now, dev_max,
                                "false", "main", "true", "#6750A4", "false", "up"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boxAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    // выводим информацию о корзине
    public void showResult(View v) {
        for (Device_state p : boxAdapter.getBox()) {
            if (p.Check_box == "true")
                check_devices.add(p);
        }
        // Передаем в RoomsActivity
        Intent intent = new Intent();
        intent.putExtra("keyScan", check_devices);
        setResult(RESULT_OK, intent);
        finish();
    }

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
}
