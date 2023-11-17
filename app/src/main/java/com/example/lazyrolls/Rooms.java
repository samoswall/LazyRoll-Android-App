package com.example.lazyrolls;

import java.util.ArrayList;

public class Rooms {
    String RoomName;
    String PictureRoom;
    String ShowPreset;
    ArrayList<Device_state> RoomDevices;


    Rooms(String roomname, String pictureroom, String showpreset, ArrayList<Device_state> roomdevices) {
        RoomName = roomname;
        PictureRoom = pictureroom;
        ShowPreset = showpreset;
        RoomDevices = roomdevices;
    }


}
