package com.example.lazyrolls;

import static java.util.Arrays.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Rooms> DeviceinRoom = new ArrayList<Rooms>();
    ArrayList<Device_state> Devices = new ArrayList<>();
    Rooms_adapter adapter;
    int version = 1000;
    String txt_version = "1.0.0";
    String get_txt_version = "0.0.0";
    boolean binUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceinRoom = new ArrayList<Rooms>();
        Devices = new ArrayList<Device_state>();

        DeviceinRoom = LoadArrayList(DeviceinRoom, "LRkey");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Rooms_adapter(this, MainActivity.this, DeviceinRoom);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        GetUpdate();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.add_room) {
            Context context = this;
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Введите имя комнаты");
            final EditText input_name_room = new EditText(context);
            input_name_room.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input_name_room);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeviceinRoom.add(new Rooms(input_name_room.getText().toString(), "@drawable/picroom", "false",  new ArrayList<>() ));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            adapter.notifyDataSetChanged();
        } else if (itemId == R.id.settings_room) {
            Context context = this;
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Тут пока ничего нет!\nВ планах:");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Выбор цветовой темы\nМожет быть внешний MQTT");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (itemId ==  R.id.info_room) {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            MainActivity.this.startActivity(intent);
        } else if (itemId ==  R.id.update) {
            Context context = this;
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Доступно обновление приложения");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Текущая версия: " + txt_version + "\nДоступная версия: " + get_txt_version);
            builder.setPositiveButton("Скачать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/samoswall/LazyRoll-Android-App"));
                    startActivity(launchBrowser);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (itemId ==  android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void onItemClick(View view, int position) {
        //передаем на RoomsActivity
     //   Intent intent = new Intent(MainActivity.this, RoomsActivity.class);



        //переходим на RoomsActivity
        openRoomsActivity(position);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem update_item = menu.findItem(R.id.update);
        if (binUpdate) {
            update_item.setVisible(true);
        }
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){      //добавление иконок в меню
            try{
                Method m = menu.getClass().getDeclaredMethod ("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                System.err.println("onCreateOptionsMenu");
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public void SaveArrayList(ArrayList<Rooms> list, String key){
        SharedPreferences prefs = getSharedPreferences("LazyRoll Config", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        Log.i("---save---", json);
        editor.apply();
    }

    public ArrayList<Rooms> LoadArrayList(ArrayList<Rooms> list, String key){
        SharedPreferences prefs = getSharedPreferences("LazyRoll Config", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Rooms>>() {}.getType();
        if (gson.fromJson(json, type) == null){
            return list;
        } else {
            return gson.fromJson(json, type);
        }
    }

/*    @Override
    protected void onDestroy() {
        SaveArrayList(DeviceinRoom, "LRkey");
        super.onDestroy();
        //    System.exit(0);
    }      */

    @Override
    protected void onPause() {
        SaveArrayList(DeviceinRoom, "LRkey");
        super.onPause();                                                          // Как правильно сохранять перед выходом?
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {                                                                   // Выход по двойному клику
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Нажмите ещё раз для выхода", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }




    // Запуск ожидания ответа от RoomsActivity
    ActivityResultLauncher<Intent> launchRoomsActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                // Получаем от ScanActivity
                Devices  = (ArrayList<Device_state>) data.getSerializableExtra("keyRoom");
                int t_pos = data.getIntExtra("room_position", -1);
                DeviceinRoom.get(t_pos).RoomDevices = Devices;
                adapter.notifyDataSetChanged();
            }
        }
    });


    // Запуск ожидания ответа от Crop_Image
    ActivityResultLauncher<Intent> launchCropActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                // Получаем от Crop_Image
                int file_img_pos = data.getIntExtra("image_file_name", -1);
                Log.i("--otcrop--", String.valueOf(file_img_pos));

                DeviceinRoom.get(file_img_pos).PictureRoom = "true";
                adapter.notifyDataSetChanged();
            }
        }
    });
/*
    private void Room_image_load (int pos) {
        try {

            File myDir = new File(MainActivity.this.getFilesDir(), "RoomsImg");
            String imageName = "Image_room_" + String.valueOf(pos) + ".jpg";
            File file = new File(myDir, imageName);
            if (file.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ImageView myImage = (ImageView) findViewById(R.id.imageView2);
                myImage.setImageBitmap(myBitmap);
            }
        }
        catch(Exception e){
        }
    }
*/


    //переход на RoomsActivity
    public void openRoomsActivity(int position) {
        Intent intent = new Intent(MainActivity.this, RoomsActivity.class);

        intent.putExtra("room_position", position);
        intent.putExtra("room_name", DeviceinRoom.get(position).RoomName);
        intent.putExtra("room_devices", DeviceinRoom.get(position).RoomDevices);

        launchRoomsActivity.launch(intent);
    }


    public void GetUpdate() {
        String urlupdate = "https://raw.githubusercontent.com/samoswall/LazyRoll-Android-App/master/Versions";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlupdate)
                        .build();
                Response response = null;
                String responseData = "null";
                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String finalResponseData = responseData;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayUpdate(finalResponseData);
                    }
                });
            }
        }).start();
    }



    void displayUpdate(String request_text) {
        String[] lines;
        String[] num_ver;
        if (request_text.length()>4) {
            try {
                lines = request_text.split("\\r?\\n", -1);
                num_ver = lines[0].split("\\.");
                get_txt_version = lines[0];
                int Getver = (10000 * Integer.valueOf(num_ver[0])) + (100 * Integer.valueOf(num_ver[1])) + Integer.valueOf(num_ver[2]);
             //   text1.setText(String.valueOf(Getver));
                if (Getver>version) {
                    binUpdate = true;
                    MainActivity.this.invalidateOptionsMenu();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }


}