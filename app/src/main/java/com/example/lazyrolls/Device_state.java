package com.example.lazyrolls;

import java.io.Serializable;

public class Device_state implements Serializable {
     String Ip;
     String Name;
     String Hostname;
     String Now;
     String Max;
     String Check_box;

     Device_state(String ip, String name, String hostname, String now, String max, String check_box) {
        Name = name;
        Hostname = hostname;
        Ip = ip;
        Now = now;
        Max = max;
        Check_box = check_box;
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
}
