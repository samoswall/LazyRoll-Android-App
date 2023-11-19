package com.example.lazyrolls;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Send_command {

    String command_url;
    Send_command (String Send_IP, String Send_command) {
        command_url = "";

        switch (Send_command) {
            case "Open":
                command_url = "/open";
                break;
            case "Close":
                command_url = "/close";
                break;
            case "Stop":
                command_url = "/stop";
                break;
            case "Preset1":
                command_url = "/set?preset=1";
                break;
            case "Preset2":
                command_url = "/set?preset=2";
                break;
            case "Preset3":
                command_url = "/set?preset=3";
                break;
            case "Preset4":
                command_url = "/set?preset=4";
                break;
            case "Preset5":
                command_url = "/set?preset=5";
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://" + Send_IP + command_url)
                        .build();
                Response response = null;
                String responseData = "null";
                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
