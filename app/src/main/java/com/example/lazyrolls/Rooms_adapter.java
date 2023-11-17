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

import java.lang.reflect.Method;
import java.util.List;


public class Rooms_adapter  extends RecyclerView.Adapter<Rooms_adapter.ViewHolder> {

    private List<Rooms> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
//    private LinearLayout linlayroom;

    Rooms_adapter(Context context, List<Rooms> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
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
        if (Boolean.parseBoolean(mData.get(position).ShowPreset)) {
            holder.preset_button_1.setVisibility(View.VISIBLE);                                     //Показать пресеты
            holder.preset_button_2.setVisibility(View.VISIBLE);
            holder.preset_button_3.setVisibility(View.VISIBLE);
            holder.preset_button_4.setVisibility(View.VISIBLE);
            holder.preset_button_5.setVisibility(View.VISIBLE);
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
                            Log.i("---preset---", mData.get(position).ShowPreset);
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
                        }
                        return true;
                    }
                });
                popupMenu.show();
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
        ImageButton room_menu_button;
        ImageButton preset_button_1;
        ImageButton preset_button_2;
        ImageButton preset_button_3;
        ImageButton preset_button_4;
        ImageButton preset_button_5;
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







