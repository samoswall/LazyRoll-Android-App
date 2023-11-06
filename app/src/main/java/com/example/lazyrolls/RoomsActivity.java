package com.example.lazyrolls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RoomsActivity extends AppCompatActivity {

    private List<View> all_device;    //Создаем список устройств в комнате

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        all_device = new ArrayList<View>();     //инициализировали наш массив
    }


}