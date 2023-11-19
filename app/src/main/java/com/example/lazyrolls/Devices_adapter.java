package com.example.lazyrolls;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.List;

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
    private Devices_adapter.ItemClickListener dClickListener;


    Devices_adapter(Context context, List<Device_state> data, List<XML_answer> xdata) {
        this.dInflater = LayoutInflater.from(context);
        this.dData = data;
        this.xData = xdata;
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
    public void onBindViewHolder(Devices_adapter.ViewHolder holder, int position) {
        int percent;
        String dev_name = dData.get(position).Name;
        String dev_now = dData.get(position).Now;
        String dev_max = dData.get(position).Max;
        if (Integer.parseInt(dev_now) > Integer.parseInt(dev_max)) dev_now = dev_max;               // не настроенный девайс
        if (Boolean.parseBoolean(dData.get(position).Invert_percent)) {
            percent = ((Integer.parseInt(dev_now) * 100) / Integer.parseInt(dev_max));
        } else {
            percent = 100 - ((Integer.parseInt(dev_now) * 100) / Integer.parseInt(dev_max));
        }
        holder.dev_percent_text.setText(String.valueOf(percent)+"%");
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.blind.getLayoutParams();
        params.height = convertDpToPixels(this.dInflater.getContext(), 150) * percent / 100;               //262pix или 150dp
        holder.blind.setLayoutParams(params);

        holder.blind.setBackgroundColor(Color.parseColor(dData.get(position).Color_curtain));
        holder.dev_name_text.setText(dev_name);
        if (Boolean.parseBoolean(dData.get(position).Preset_view)) {
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
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dData.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (menuItem.getItemId() == R.id.device_info) {                      // Инфо девайса
                            Context context = v.getContext();
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(dData.get(position).Name);

                            builder.setMessage(
                                    "Версия: "+xData.get(position).Version+"\n" +
                                    "IP: "+xData.get(position).IP+"\n" +
                                    "Имя: "+xData.get(position).Name+"\n" +
                                    "Имя в сети: "+xData.get(position).Hostname+"\n" +
                                    "Время: "+xData.get(position).Time+"\n" +
                                    "Время работы: "+xData.get(position).UpTime+"\n" +
                                    "Мощность WiFi сигнала: "+xData.get(position).RSSI+"\n" +
                                    "Статус MQTT: "+xData.get(position).MQTT+"\n" +
                                    "Записей логирования: "+xData.get(position).Log+"\n" +
                                    "ID ESP: "+xData.get(position).ID+"\n" +
                                    "ID Flash: "+xData.get(position).FlashID+"\n" +
                                    "Объем памяти: "+xData.get(position).RealSize+"\n" +
                                    "Размер прошивки: "+xData.get(position).IdeSize+"\n" +
                                    "Частота CPU: "+xData.get(position).Speed+"\n" +
                                    "Режим прошивки: "+xData.get(position).IdeMode+"\n" +
                                    "Последний код RF: "+xData.get(position).LastCode+"\n" +
                                    "Код RF: "+xData.get(position).Hex+"\n" +
                                    "Текущее положение: "+xData.get(position).Now+"\n" +
                                    "Целевое положение: "+xData.get(position).Dest+"\n" +
                                    "Максимальное: "+xData.get(position).Max+"\n" +
                                    "Дополнительный концевик: "+xData.get(position).End1+"\n" +
                                    "Режим светодиода: "+xData.get(position).Mode+"\n" +
                                    "Яркость светодиода: "+xData.get(position).Level);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (menuItem.getItemId() == R.id.device_page) {                      // Перейти на страницу устройсва
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + xData.get(position).IP));
                            dInflater.getContext().startActivity(launchBrowser);
                        } else if (menuItem.getItemId() == R.id.device_preset_show) {               // Показать или скрыть кнопки пресетов
                            if (Boolean.parseBoolean(dData.get(position).Preset_view)) {
                                holder.dev_preset_button_1.setVisibility(View.GONE);
                                holder.dev_preset_button_2.setVisibility(View.GONE);
                                holder.dev_preset_button_3.setVisibility(View.GONE);
                                holder.dev_preset_button_4.setVisibility(View.GONE);
                                holder.dev_preset_button_5.setVisibility(View.GONE);
                                dData.get(position).Preset_view = "false";
                            } else {
                                holder.dev_preset_button_1.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_2.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_3.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_4.setVisibility(View.VISIBLE);
                                holder.dev_preset_button_5.setVisibility(View.VISIBLE);
                                dData.get(position).Preset_view = "true";
                            }
                        } else if (menuItem.getItemId() == R.id.device_color) {                     // Поменять цвет шторы   https://github.com/yukuku/ambilwarna
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(dInflater.getContext(), Color.parseColor(dData.get(position).Color_curtain), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color) {
                                    dData.get(position).Color_curtain = "#"+Integer.toHexString(color);
                                    holder.blind.setBackgroundColor(Color.parseColor(dData.get(position).Color_curtain));
                                }
                                @Override
                                public void onCancel(AmbilWarnaDialog dialog) {
                                    // cancel was selected by the user
                                }
                            });
                            dialog.show();
                        } else if (menuItem.getItemId() == R.id.device_invert_percent) {                     // Инверсия %
                            if (Boolean.parseBoolean(dData.get(position).Invert_percent)) {
                                dData.get(position).Invert_percent = "false";
                            } else {
                                dData.get(position).Invert_percent = "true";
                            }
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
                new Send_command(dData.get(position).Ip, "Open");
            }
        });
        holder.dev_stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Stop");
            }
        });
        holder.dev_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Close");
            }
        });
        holder.dev_preset_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Preset1");
            }
        });
        holder.dev_preset_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Preset2");
            }
        });
        holder.dev_preset_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Preset3");
            }
        });
        holder.dev_preset_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Preset4");
            }
        });
        holder.dev_preset_button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Send_command(dData.get(position).Ip, "Preset5");
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
        View blind;
        ImageButton dev_menu_button;
        ImageButton dev_open_button;
        ImageButton dev_stop_button;
        ImageButton dev_close_button;
        ImageButton dev_preset_button_1;
        ImageButton dev_preset_button_2;
        ImageButton dev_preset_button_3;
        ImageButton dev_preset_button_4;
        ImageButton dev_preset_button_5;
        ViewHolder(View itemView) {
            super(itemView);
            dev_name_text = itemView.findViewById(R.id.device_name);
            itemView.setOnClickListener(this);
            dev_menu_button = itemView.findViewById(R.id.dev_menu_button);
            dev_percent_text = itemView.findViewById(R.id.text_percent);
            blind = itemView.findViewById(R.id.blind);
            dev_open_button = itemView.findViewById(R.id.dev_imageButton_up);
            dev_stop_button = itemView.findViewById(R.id.dev_imageButton_stop);
            dev_close_button = itemView.findViewById(R.id.dev_imageButton_down);
            dev_preset_button_1 = itemView.findViewById(R.id.device_preset_1);
            dev_preset_button_2 = itemView.findViewById(R.id.device_preset_2);
            dev_preset_button_3 = itemView.findViewById(R.id.device_preset_3);
            dev_preset_button_4 = itemView.findViewById(R.id.device_preset_4);
            dev_preset_button_5 = itemView.findViewById(R.id.device_preset_5);
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


}


































