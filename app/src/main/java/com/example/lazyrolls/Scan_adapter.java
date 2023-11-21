package com.example.lazyrolls;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class Scan_adapter extends BaseAdapter{

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Device_state> objects;

    Scan_adapter(Context context, ArrayList<Device_state> devices) {
        ctx = context;
        objects = devices;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.scaned_device, parent, false);
        }

        Device_state p = getProduct(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.scan_name)).setText(p.Name);
        ((TextView) view.findViewById(R.id.scan_hostname)).setText(p.Hostname);
        ((TextView) view.findViewById(R.id.scan_ip)).setText(p.Ip);

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.scan_cbBox);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        cbBuy.setTag(position);
        // заполняем данными: в корзине или нет
        cbBuy.setChecked(Boolean.parseBoolean(p.Check_box));
        return view;
    }

    // по позиции
    Device_state getProduct(int position) {
        return ((Device_state) getItem(position));
    }

    // содержимое корзины
    ArrayList<Device_state> getBox() {
        ArrayList<Device_state> box = new ArrayList<Device_state>();
        for (Device_state p : objects) {
            // если в корзине
            if (p.Check_box == "true")
                box.add(p);
        }
        return box;
    }

    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            if (isChecked) {
                getProduct((Integer) buttonView.getTag()).Check_box = "true";
            } else {getProduct((Integer) buttonView.getTag()).Check_box = "false";
            }
        }
    };

}
