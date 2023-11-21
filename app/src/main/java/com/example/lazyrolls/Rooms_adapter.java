package com.example.lazyrolls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;


public class Rooms_adapter  extends RecyclerView.Adapter<Rooms_adapter.ViewHolder> {

    private List<Rooms> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;
    MainActivity mIntent;

    Rooms_adapter(Context context, MainActivity intent, List<Rooms> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mIntent = intent;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_rooms, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String room_name = mData.get(position).RoomName;
        holder.room_name_text.setText(room_name);
        holder.count_devices.setText("Устройств " + String.valueOf(mData.get(position).RoomDevices.size()));
        if (Boolean.parseBoolean(mData.get(position).ShowPreset)) {
            holder.preset_button_1.setVisibility(View.VISIBLE);                                     //Показать пресеты
            holder.preset_button_2.setVisibility(View.VISIBLE);
            holder.preset_button_3.setVisibility(View.VISIBLE);
            holder.preset_button_4.setVisibility(View.VISIBLE);
            holder.preset_button_5.setVisibility(View.VISIBLE);
        }
        if (!mData.get(position).PictureRoom.equals("@drawable/picroom")) {
            try {
                File myDir = new File(mIntent.getFilesDir(), "RoomsImg");
                String imageName = "Image_room_" + String.valueOf(position) + ".jpg";
                File file = new File(myDir, imageName);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.room_image.setImageBitmap(myBitmap);
               }
           }
           catch(Exception e){
           }












        }
        holder.room_menu_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.room_menu_button);
                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.room_menu, popupMenu.getMenu());
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
                        if (menuItem.getItemId() == R.id.room_menu_delete) {                        // Удаление комнаты
                            Context context = v.getContext();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Вы действительно хотите удалить комнату?");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mData.remove(position);
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
                        } else if (menuItem.getItemId() == R.id.room_menu_rename) {                 // Переименование комнаты
                            Context context = v.getContext();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Введите имя комнаты");
                            builder.setIcon(R.mipmap.ic_launcher);
                            final EditText input_name_room = new EditText(context);
                            input_name_room.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input_name_room);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String m_Text = input_name_room.getText().toString();
                                    mData.get(position).RoomName = m_Text;
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
                        } else if (menuItem.getItemId() == R.id.room_menu_view_preset) {            // Показать или скрыть кнопки пресетов
                            if (Boolean.parseBoolean(mData.get(position).ShowPreset)) {
                                holder.preset_button_1.setVisibility(View.GONE);
                                holder.preset_button_2.setVisibility(View.GONE);
                                holder.preset_button_3.setVisibility(View.GONE);
                                holder.preset_button_4.setVisibility(View.GONE);
                                holder.preset_button_5.setVisibility(View.GONE);
                                mData.get(position).ShowPreset = "false";
                            } else {
                                holder.preset_button_1.setVisibility(View.VISIBLE);
                                holder.preset_button_2.setVisibility(View.VISIBLE);
                                holder.preset_button_3.setVisibility(View.VISIBLE);
                                holder.preset_button_4.setVisibility(View.VISIBLE);
                                holder.preset_button_5.setVisibility(View.VISIBLE);
                                mData.get(position).ShowPreset = "true";
                            }
                        } else if (menuItem.getItemId() == R.id.room_menu_change_picture) {            // Изменить картинку комнаты
                            //Запуск CropImage и передача номера комнаты
                            Intent intent = new Intent( mIntent, Crop_Image.class);
                            intent.putExtra("img_position", position);
                            mIntent.launchCropActivity.launch(intent);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.room_open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Open");
                }
            }
        });
        holder.room_stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Stop");
                }
            }
        });
        holder.room_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Close");
                }
            }
        });
        holder.preset_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Preset1");
                }
            }
        });
        holder.preset_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Preset2");
                }
            }
        });
        holder.preset_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Preset3");
                }
            }
        });
        holder.preset_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Preset4");
                }
            }
        });
        holder.preset_button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.get(position).RoomDevices.size(); i++) {
                    new Send_command(mData.get(position).RoomDevices.get(i).Ip, "Preset5");
                }
            }
        });
     //   notifyDataSetChanged();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView room_name_text;
        TextView count_devices;
        ImageButton room_menu_button;
        ImageButton preset_button_1;
        ImageButton preset_button_2;
        ImageButton preset_button_3;
        ImageButton preset_button_4;
        ImageButton preset_button_5;
        ImageButton room_open_button;
        ImageButton room_stop_button;
        ImageButton room_close_button;
        ImageView room_image;

        ViewHolder(View itemView) {
            super(itemView);
            room_name_text = itemView.findViewById(R.id.room_name);
            itemView.setOnClickListener(this);
            room_menu_button = itemView.findViewById(R.id.room_menu_button);
            preset_button_1 = itemView.findViewById(R.id.room_preset_1);
            preset_button_2 = itemView.findViewById(R.id.room_preset_2);
            preset_button_3 = itemView.findViewById(R.id.room_preset_3);
            preset_button_4 = itemView.findViewById(R.id.room_preset_4);
            preset_button_5 = itemView.findViewById(R.id.room_preset_5);
            room_open_button = itemView.findViewById(R.id.room_imageButton_up);
            room_stop_button = itemView.findViewById(R.id.room_imageButton_stop);
            room_close_button = itemView.findViewById(R.id.room_imageButton_down);
            count_devices = itemView.findViewById(R.id.device_sum);
            room_image = itemView.findViewById(R.id.image_room);
   //         itemView.setOnClickListener{};
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).RoomName;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}







