package com.example.lazyrolls;

import java.io.Serializable;

public class Device_state implements Serializable {
     String Ip;
     String Name;
     String Hostname;
     String Now;
     String Max;
     String Check_box;
     String Role;
     String Invert_percent;
     String Color_curtain;
     String Preset_view;
     String Direction;

     Device_state(String ip, String name, String hostname, String now, String max, String check_box, String role, String invert_percent, String color_curtain, String preset_view, String direction) {
        Name = name;
        Hostname = hostname;
        Ip = ip;
        Now = now;
        Max = max;
        Check_box = check_box;
        Role = role;
        Invert_percent = invert_percent;
        Color_curtain = color_curtain;
        Preset_view = preset_view;
        Direction = direction;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        this.Ip = ip;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getHostname() {
        return Hostname;
    }

    public void setCompany(String hostname) {
        this.Hostname = hostname;
    }

    public String getNow() {
        return Now;
    }

    public void setNow(String now) {
        this.Now = now;
    }

    public String getMax() {
        return Max;
    }

    public void setMax(String max) {
        this.Max = max;
    }

    public String getCheck_box() {
        return Check_box;
    }

    public void setCheck_box(String check_box) {
        this.Check_box = check_box;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        this.Role = role;
    }

    public String getInvert_percent() {
        return Invert_percent;
    }

    public void setInvert_percent(String invert_percent) {
        this.Invert_percent = invert_percent;
    }

    public String getColor_curtain() {
        return Color_curtain;
    }

    public void setColor_curtain(String color_curtain) {
        this.Color_curtain = color_curtain;
    }

    public String getPreset_view() {
        return Preset_view;
    }

    public void setPreset_view(String preset_view) {
        this.Preset_view = preset_view;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        this.Direction = direction;
    }
}
