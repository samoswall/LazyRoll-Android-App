package com.example.lazyrolls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> all_rooms;    //Создаем список rooms которые будут создаваться
    private int counter = 0;     //счетчик чисто декоративный для визуального отображения rooms
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        all_rooms = new ArrayList<View>();     //инициализировали наш массив

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String msg = "";
        int itemId = item.getItemId();
        if (itemId == R.id.add_room) {
            msg = "Add clicked";
            counter++;
            final LinearLayout linear = (LinearLayout) findViewById(R.id.linear_rooms);   //находим наш первый linear в activity_main.xml
            //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
            final View view = getLayoutInflater().inflate(R.layout.layout_rooms, null);

            TextView text_room = (TextView) view.findViewById(R.id.room_name);
            text_room.setText("Room " + counter);
            //добавляем все что создаем в массив
            all_rooms.add(view);
            //добавляем елементы в linearlayout
            linear.addView(view);

            // Referencing and Initializing the button
            ImageButton button_room_menu = (ImageButton) view.findViewById(R.id.room_menu_button);
            // Setting onClick behavior to the button
            button_room_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Initializing the popup menu and giving the reference as current context
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, button_room_menu);
                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.room_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            View view_popup = (View) view.getParent();
                            if (menuItem.getItemId() == R.id.room_menu_delete) {
                                try {
                                    //получаем родительский view и удаляем его
                                    ((LinearLayout) view_popup.getParent()).removeView(view_popup);
                                    //удаляем эту же запись из массива что бы не оставалось мертвых записей
                                    all_rooms.remove(view_popup);
                                } catch(IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            // Toast message on menu item clicked
                            Toast.makeText(MainActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            });





        } else if (itemId == R.id.rename_room) {
            msg = "Settigs clicked";
        } else if (itemId ==  R.id.delete_room) {
            msg = "Info clicked";
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        return true;
    }


}