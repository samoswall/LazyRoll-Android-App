package com.example.lazyrolls;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

// import org.json.XML;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ScanActivity extends AppCompatActivity {

    private List<String> dev_arr;
    private List<String> ipList;

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

    private Handler handler;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle("Поиск в сети " + getRouterIPAddress() + ".1-254");
        dev_arr = new ArrayList<String>();
        ipList = new ArrayList<String>();

        final LinearLayout linear = (LinearLayout) findViewById(R.id.all_scan_devices);   //находим наш первый linear в activity_main.xml
        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
        final View view = getLayoutInflater().inflate(R.layout.scaned_device, null);

        TextView text_dev_ip = (TextView) view.findViewById(R.id.scan_IP);
        TextView text_dev_name = (TextView) view.findViewById(R.id.scan_Name);

        Button start_scan = (Button) findViewById(R.id.scan_start_button);

        for (int i = 100; i < 110; i++) {
            String url = "http://" + getRouterIPAddress() + "." + i + "/xml";
            Log.i("---!---", url);
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
                    Log.i("---!---", "Успешно получено " + responseData);

                    if (responseData.indexOf("Curtain") > 1) {
                        String dev_ip = responseData.substring(responseData.indexOf("<IP>")+4, responseData.indexOf("</IP>"));
                        String dev_name = responseData.substring(responseData.indexOf("<Name>")+6, responseData.indexOf("</Name>"));
                        String dev_hostname = responseData.substring(responseData.indexOf("<Hostname>")+10, responseData.indexOf("</Hostname>"));
                        String dev_now = responseData.substring(responseData.indexOf("<Now>")+5, responseData.indexOf("</Now>"));
                        String dev_max = responseData.substring(responseData.indexOf("<Max>")+5, responseData.indexOf("</Max>"));
                        dev_arr.clear();
                        dev_arr.add(dev_ip);
                        dev_arr.add(dev_name);
                        dev_arr.add(dev_hostname);
                        dev_arr.add(dev_now);
                        dev_arr.add(dev_max);
                        ipList.add(dev_arr.toString());
                        Log.i("---!---", "ipList " + ipList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_dev_ip.setText(dev_ip);
                                text_dev_name.setText(dev_name);
                            //    linear.addView(view);                                   // Тут ошибка! Вылетает apk
                            }
                            });
                        count++;
                    }
                }
            }).start();
        }

        start_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScanActivity.this, "После сканирования", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
