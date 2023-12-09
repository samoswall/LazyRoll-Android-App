package com.example.lazyrolls;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class Devices_adapter extends RecyclerView.Adapter<Devices_adapter.ViewHolder> {

    private List<Device_state> dData;
    private List<XML_answer> xData;
    private LayoutInflater dInflater;
    private int[] xDev_offline;
    Context xcontext;
    private Devices_adapter.ItemClickListener dClickListener;


    Devices_adapter(Context context, List<Device_state> data, List<XML_answer> xdata, int[] Dev_offline) {
        this.dInflater = LayoutInflater.from(context);
        this.dData = data;
        this.xData = xdata;
        this.xDev_offline = Dev_offline;
        this.xcontext = context;
    }

    @Override
    public Devices_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = dInflater.inflate(R.layout.layout_device, parent, false);
        return new Devices_adapter.ViewHolder(view);
    }

    float convertPixelsToDp(Context context, int pixels) {
        return (int) (pixels / context.getResources().getDisplayMetrics().density);
    }
    int convertDpToPixels(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBindViewHolder(Devices_adapter.ViewHolder holder, int positions) {
        int percent;
        String dev_name = dData.get(positions).Name;
        String dev_now = dData.get(positions).Now;
        String dev_max = dData.get(positions).Max;

        if (xDev_offline[positions] > 1) {
            holder.dev_offline.setText("Устройство в сети");
            holder.dev_offline.setTextColor(Color.parseColor("#078700")); //green
        } else {
            holder.dev_offline.setText("Устройство не в сети");
            holder.dev_offline.setTextColor(Color.parseColor("#870000")); //red
        }

        if (Integer.parseInt(dev_now) > Integer.parseInt(dev_max)) dev_now = dev_max;               // не настроенный девайс
        if (Integer.parseInt(dev_now) < 0) dev_now = "0";                                           // отрицательные значения

        if (Boolean.parseBoolean(dData.get(positions).Invert_percent)) {                            // инверсия
            percent = ((Integer.parseInt(dev_now) * 100) / Integer.parseInt(dev_max));
        } else {
            percent = 100 - ((Integer.parseInt(dev_now) * 100) / Integer.parseInt(dev_max));
        }

        holder.dev_percent_text.setText(String.valueOf(percent)+"%");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) xcontext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        int disp_height = displayMetrics.heightPixels;
        int disp_width = displayMetrics.widthPixels;


        ViewGroup.MarginLayoutParams params_w = (ViewGroup.MarginLayoutParams) holder.image_window.getLayoutParams();
        params_w.width = (int) (disp_width*40/100);
        params_w.leftMargin = (int) (disp_width*2/100);;
        params_w.topMargin = (int) (disp_height*0.5/100);
        params_w.height = (int) (disp_height*20/100);

        ViewGroup.LayoutParams params_left = (ViewGroup.LayoutParams) holder.blind_left.getLayoutParams();
        ViewGroup.MarginLayoutParams params_l = (ViewGroup.MarginLayoutParams) holder.blind_left.getLayoutParams();
        holder.blind_left.setLayoutParams(params_left);
        holder.blind_left.setBackgroundColor(Color.parseColor(dData.get(positions).Color_curtain));
        ViewGroup.LayoutParams params_right = (ViewGroup.LayoutParams) holder.blind_right.getLayoutParams();
        ViewGroup.MarginLayoutParams params_r = (ViewGroup.MarginLayoutParams) holder.blind_right.getLayoutParams();
        holder.blind_right.setLayoutParams(params_right);
        holder.blind_right.setBackgroundColor(Color.parseColor(dData.get(positions).Color_curtain));

        if (dData.get(positions).Direction.equals("up")) {                                                               // направление движения
            holder.blind_left.setVisibility(View.VISIBLE);
            holder.blind_right.setVisibility(View.GONE);
            params_l.width = (int) ((disp_width*34.5/100)+(disp_width/100));
            params_l.leftMargin = (int) (disp_width*4/100);
            params_l.topMargin = (int) (disp_height*1.2/100);
            params_l.height = (int) ((disp_height*17.5*percent/10000)+(disp_width/100));
        } else if (dData.get(positions).Direction.equals("left")) {
            holder.blind_left.setVisibility(View.VISIBLE);
            holder.blind_right.setVisibility(View.GONE);
            params_l.width = (int) ((disp_width*34.5*percent/10000)+(disp_width/100));
            params_l.leftMargin = (int) (disp_width*4/100);
            params_l.topMargin = (int) (disp_height*1.2/100);
            params_l.height = (int) (disp_height*18/100);
        } else if (dData.get(positions).Direction.equals("right")) {
            holder.blind_left.setVisibility(View.GONE);
            holder.blind_right.setVisibility(View.VISIBLE);
            params_r.width = (int) ((disp_width*34.5*percent/10000)+(disp_width/100));
            params_r.leftMargin = (int) ((disp_width*4/100)+(disp_width*34.5*(100-percent)/10000));
            params_r.topMargin = (int) (disp_height*1.2/100);
            params_r.height = (int) (disp_height*18/100);
        }
        holder.dev_name_text.setText(dev_name);
        if (Boolean.parseBoolean(dData.get(positions).Preset_view)) {
            holder.dev_preset_button_1.setVisibility(View.VISIBLE);                                     //Показать пресеты из конфига
            holder.dev_preset_button_2.setVisibility(View.VISIBLE);
            holder.dev_preset_button_3.setVisibility(View.VISIBLE);
            holder.dev_preset_button_4.setVisibility(View.VISIBLE);
            holder.dev_preset_button_5.setVisibility(View.VISIBLE);
        }

        holder.dev_menu_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.dev_menu_button);
                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.button_device_menu, popupMenu.getMenu());
                if(popupMenu.getClass().getSimpleName().equals("PopupMenu")){                       //добавление иконок в меню
                    try{
                        Method m = popupMenu.getClass().getDeclaredMethod ("setForceShowIcon", Boolean.TYPE);
                        m.setAccessible(true);
                        m.invoke(popupMenu, true);
                    }
                    catch(NoSuchMethodException e){
                        System.err.println("onCreateOptionsMenu");
                    }
                    catch(Exception e){
                        throw new RuntimeException(e);
                    }
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        View view_popup = (View) v.getParent();
                        if (menuItem.getItemId() == R.id.device_delete) {                        // Удаление девайса
                            Context context = v.getContext();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Вы действительно хотите удалить устройство?");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dData.remove(positions);
                                    //   notifyDataSetChanged();                                       Под вопросом для 7 андроид
                                }
                            });
                            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (menuItem.getItemId() == R.id.device_info) {                      // Инфо девайса
                            Context context = v.getContext();
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(dData.get(positions).Name);
                            builder.setIcon(R.mipmap.ic_launcher);
                            String[] listIdArr = {"Версия:", "IP:", "Имя:", "Имя в сети:", "Время:", "Время работы:", "Мощность WiFi:", "Статус MQTT:",
                                    "Записей логирования:", "ID ESP:", "ID Flash:", "Объем памяти:", "Размер прошивки:", "Частота CPU:", "Режим прошивки:",
                                    "Последний код RF:", "Код RF:", "Текущее положение:", "Целевое положение:", "Максимальное:", "Доп. концевик:",
                                    "Режим светодиода:", "Яркость светодиода:"};
                            String[] listItemArr = {xData.get(positions).Version, xData.get(positions).IP, xData.get(positions).Name, xData.get(positions).Hostname,
                                    xData.get(positions).Time, xData.get(positions).UpTime, xData.get(positions).RSSI, xData.get(positions).MQTT,
                                    xData.get(positions).Log,  xData.get(positions).ID, xData.get(positions).FlashID, xData.get(positions).RealSize,
                                    xData.get(positions).IdeSize, xData.get(positions).Speed, xData.get(positions).IdeMode, xData.get(positions).LastCode,
                                    xData.get(positions).Hex, xData.get(positions).Now, xData.get(positions).Dest, xData.get(positions).Max,
                                    xData.get(positions).End1, xData.get(positions).Mode, xData.get(positions).Level};
                            List<Map<String, Object>> dialogItemList = new ArrayList<Map<String, Object>>();
                            int listItemLen = listItemArr.length;
                            for(int i=0;i<listItemLen;i++)
                            {
                                Map<String, Object> itemMap = new HashMap<String, Object>();
                                itemMap.put("ID", listIdArr[i]);
                                itemMap.put("VALUE", listItemArr[i]);
                                dialogItemList.add(itemMap);
                            }
                            // Create SimpleAdapter object.
                            SimpleAdapter simpleAdapter = new SimpleAdapter(context, dialogItemList,
                                    R.layout.info_lay_item,
                                    new String[]{"ID", "VALUE"},
                                    new int[]{R.id.info_name,R.id.info_value});
                            // Set the data adapter.
                            builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int itemIndex) {
                                    //    Toast.makeText(MainActivity.this, "You choose item " + listItemArr[itemIndex], Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setCancelable(true);
                            builder.create();
                            builder.show();
                        } else if (menuItem.getItemId() == R.id.device_page) {                      // Перейти на страницу устройсва
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + xData.get(positions).IP));
                            dInflater.getContext().startActivity(launchBrowser);
                        } else if (menuItem.getItemId() == R.id.device_preset_show) {               // Показать или скрыть кнопки пресетов
                            if (Boolean.parseBoolean(dData.get(positions).Preset_view)) {
                                holder.dev_preset_button_1.setVisibility(View.GONE);
                                holder.dev_preset_button_2.setVisibility(View.GONE);
                                holder.dev_preset_button_3.setVisibility(View.GONE);
                                holder.dev_preset_button_4.setVisibility(View.GONE);
                                holder.dev_preset_button_5.setVisibility(View.GONE);
                                dData.get(positions).Preset_view = "false";
                            } else {
                                holder.dev_preset_button_1.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_2.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_3.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_4.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_5.setVisibility(View.VISIBLE);
                                dData.get(positions).Preset_view = "true";
                            }
                        } else if (menuItem.getItemId() == R.id.device_color) {                     // Поменять цвет шторы   https://github.com/yukuku/ambilwarna
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(dInflater.getContext(), Color.parseColor(dData.get(positions).Color_curtain), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color) {
                                    dData.get(positions).Color_curtain = "#"+Integer.toHexString(color);
                                    holder.blind_left.setBackgroundColor(Color.parseColor(dData.get(positions).Color_curtain));
                                    holder.blind_right.setBackgroundColor(Color.parseColor(dData.get(positions).Color_curtain));
                                }
                                @Override
                                public void onCancel(AmbilWarnaDialog dialog) {
                                    // cancel was selected by the user
                                }
                            });
                            dialog.show();
                        } else if (menuItem.getItemId() == R.id.device_invert_percent) {                     // Инверсия %
                            if (Boolean.parseBoolean(dData.get(positions).Invert_percent)) {
                                dData.get(positions).Invert_percent = "false";
                            } else {
                                dData.get(positions).Invert_percent = "true";
                            }
                        } else if (menuItem.getItemId() == R.id.device_add_slave) {                           // Добавить ведомый
                            Toast.makeText(v.getContext(), "Функция в разработке.", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.device_direction) {                     // Направление движения

                            int Dir_sel = -1;
                            final String[] directArray = {"Открывается вверх", "Открывается влево", "Открывается вправо"};
                            Context context = v.getContext();
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setIcon(R.mipmap.ic_launcher);
                            int checkedItem = 0; // Up
                            final Set<String> selectedItems = new HashSet<String>();
                            selectedItems.add(directArray[checkedItem]);

                            builder.setTitle("Выберите направление движения шторы");

                            builder.setSingleChoiceItems(directArray, checkedItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do Something...
                                    selectedItems.clear();
                                    selectedItems.add(directArray[which]);
                                }
                            });
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(selectedItems.isEmpty()) {
                                        return;
                                    }
                                    String direct = selectedItems.iterator().next();

                                    if (direct.equals("Открывается вверх")) {
                                        dData.get(positions).Direction = "up";
                                    } else if (direct.equals("Открывается влево")) {
                                        dData.get(positions).Direction = "left";
                                    } else if (direct.equals("Открывается вправо")) {
                                        dData.get(positions).Direction = "right";
                                    }
                                    dialog.dismiss();
                                }
                            });
                           builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                           });
                           builder.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.dev_open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Open");
            }
        });
        holder.dev_stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Stop");
            }
        });
        holder.dev_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Close");
            }
        });
        holder.dev_preset_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Preset1");
            }
        });
        holder.dev_preset_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Preset2");
            }
        });
        holder.dev_preset_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Preset3");
            }
        });
        holder.dev_preset_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Preset4");
            }
        });
        holder.dev_preset_button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(positions).Ip, "Preset5");
            }
        });
        //   notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dev_name_text;
        TextView dev_percent_text;
        TextView dev_offline;
        View blind_left;
        View blind_right;
        ImageButton dev_menu_button;
        ImageButton dev_open_button;
        ImageButton dev_stop_button;
        ImageButton dev_close_button;
        ImageButton dev_preset_button_1;
        ImageButton dev_preset_button_2;
        ImageButton dev_preset_button_3;
        ImageButton dev_preset_button_4;
        ImageButton dev_preset_button_5;
        ImageView image_window;
        ViewHolder(View itemView) {
            super(itemView);
            dev_name_text = itemView.findViewById(R.id.device_name);
            itemView.setOnClickListener(this);
            dev_menu_button = itemView.findViewById(R.id.dev_menu_button);
            dev_percent_text = itemView.findViewById(R.id.text_percent);
            dev_offline = itemView.findViewById(R.id.device_online);
            blind_left = itemView.findViewById(R.id.blind_left);
            blind_right = itemView.findViewById(R.id.blind_right);
            dev_open_button = itemView.findViewById(R.id.dev_imageButton_up);
            dev_stop_button = itemView.findViewById(R.id.dev_imageButton_stop);
            dev_close_button = itemView.findViewById(R.id.dev_imageButton_down);
            dev_preset_button_1 = itemView.findViewById(R.id.device_preset_1);
            dev_preset_button_2 = itemView.findViewById(R.id.device_preset_2);
            dev_preset_button_3 = itemView.findViewById(R.id.device_preset_3);
            dev_preset_button_4 = itemView.findViewById(R.id.device_preset_4);
            dev_preset_button_5 = itemView.findViewById(R.id.device_preset_5);
            image_window = itemView.findViewById(R.id.image_device);
        }

        @Override
        public void onClick(View view) {
            if (dClickListener != null) dClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return dData.get(id).Name;
    }

    // allows clicks events to be caught
    void setClickListener(Devices_adapter.ItemClickListener itemClickListener) {
        this.dClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private static String colorToHexString(int color) {                     //проверка цвета
        return String.format("#%06X", 0xFFFFFFFF & color);
    }

}


































