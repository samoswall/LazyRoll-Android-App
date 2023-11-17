package com.example.lazyrolls;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.lang.reflect.Method;
import java.util.List;
public class Devices_adapter extends RecyclerView.Adapter<Devices_adapter.ViewHolder> {

    private List<Device_state> dData;
    private LayoutInflater dInflater;
    private Devices_adapter.ItemClickListener dClickListener;


    Devices_adapter(Context context, List<Device_state> data) {
        this.dInflater = LayoutInflater.from(context);
        this.dData = data;
    }

    @Override
    public Devices_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = dInflater.inflate(R.layout.layout_device, parent, false);
        return new Devices_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Devices_adapter.ViewHolder holder, int position) {
        String dev_name = dData.get(position).Name;
        String dev_now = dData.get(position).Now;
        String dev_max = dData.get(position).Max;
        if (Integer.parseInt(dev_now) > Integer.parseInt(dev_max)) dev_now = dev_max;               // не настроенный девайс
        int percent = (Integer.parseInt(dev_now) * 100) / Integer.parseInt(dev_max);
        holder.dev_percent_text.setText(String.valueOf(percent)+"%");
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.blind.getLayoutParams();
        params.height = 278 * percent / 100;               //278 или 158dp
        holder.blind.setLayoutParams(params);


        holder.dev_name_text.setText(dev_name);
//        if (dData.get(position).ShowPreset == "true") {
//            holder.preset_button_1.setVisibility(View.VISIBLE);                                                                                         // Не работает почемуто
//        }
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
                        }
                        return true;
                    }
                });
                popupMenu.show();
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
        Button dev_preset_button_1;
        ViewHolder(View itemView) {
            super(itemView);
            dev_name_text = itemView.findViewById(R.id.device_name);
            itemView.setOnClickListener(this);
            dev_menu_button = itemView.findViewById(R.id.dev_menu_button);
            dev_percent_text = itemView.findViewById(R.id.text_percent);
            blind = itemView.findViewById(R.id.blind);
          //  dev_preset_button_1 = itemView.findViewById(R.id.--------);
            //         itemView.setOnClickListener{};
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
