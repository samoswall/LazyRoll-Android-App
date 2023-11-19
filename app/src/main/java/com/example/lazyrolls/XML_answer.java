package com.example.lazyrolls;

public class XML_answer {

    String Version; String IP; String Name; String Hostname; String Time; String UpTime; String RSSI; String MQTT; String Log; //Info
    String ID; String FlashID; String RealSize; String IdeSize; String Speed; String IdeMode; //ChipInfo
    String LastCode; String Hex; //RF
    String Now; String Dest; String Max; String End1; //Position
    String Mode; String Level; //LED

    XML_answer(String xml_answer) {
        if (xml_answer.indexOf("Curtain") > 1) {
            //Info
            Version = xml_answer.substring(xml_answer.indexOf("<Version>") + 9, xml_answer.indexOf("</Version>"));
            IP = xml_answer.substring(xml_answer.indexOf("<IP>") + 4, xml_answer.indexOf("</IP>"));
            Name = xml_answer.substring(xml_answer.indexOf("<Name>") + 6, xml_answer.indexOf("</Name>"));
            Hostname = xml_answer.substring(xml_answer.indexOf("<Hostname>") + 10, xml_answer.indexOf("</Hostname>"));
            Time = xml_answer.substring(xml_answer.indexOf("<Time>") + 6, xml_answer.indexOf("</Time>"));
            UpTime = xml_answer.substring(xml_answer.indexOf("<UpTime>") + 8, xml_answer.indexOf("</UpTime>"));
            RSSI = xml_answer.substring(xml_answer.indexOf("<RSSI>") + 6, xml_answer.indexOf("</RSSI>"));
            MQTT = xml_answer.substring(xml_answer.indexOf("<MQTT>") + 6, xml_answer.indexOf("</MQTT>"));
            Log = xml_answer.substring(xml_answer.indexOf("<Log>") + 5, xml_answer.indexOf("</Log>"));
            //ChipInfo
            ID = xml_answer.substring(xml_answer.indexOf("<ID>") + 4, xml_answer.indexOf("</ID>"));
            FlashID = xml_answer.substring(xml_answer.indexOf("<FlashID>") + 9, xml_answer.indexOf("</FlashID>"));
            RealSize = xml_answer.substring(xml_answer.indexOf("<RealSize>") + 10, xml_answer.indexOf("</RealSize>"));
            IdeSize = xml_answer.substring(xml_answer.indexOf("<IdeSize>") + 9, xml_answer.indexOf("</IdeSize>"));
            Speed = xml_answer.substring(xml_answer.indexOf("<Speed>") + 7, xml_answer.indexOf("</Speed>"));
            IdeMode = xml_answer.substring(xml_answer.indexOf("<IdeMode>") + 9, xml_answer.indexOf("</IdeMode>"));
            //RF
            if (xml_answer.indexOf("<LastCode>") < 1) {
                LastCode = "Не поддерживается этой версией прошивки";
                Hex = "Не поддерживается этой версией прошивки";
            } else {
                LastCode = xml_answer.substring(xml_answer.indexOf("<LastCode>") + 10, xml_answer.indexOf("</LastCode>"));
                Hex = xml_answer.substring(xml_answer.indexOf("<Hex>") + 5, xml_answer.indexOf("</Hex>"));
            }
            //Position
            Now = xml_answer.substring(xml_answer.indexOf("<Now>") + 5, xml_answer.indexOf("</Now>"));
            Dest = xml_answer.substring(xml_answer.indexOf("<Dest>") + 6, xml_answer.indexOf("</Dest>"));
            Max = xml_answer.substring(xml_answer.indexOf("<Max>") + 5, xml_answer.indexOf("</Max>"));
            End1 = xml_answer.substring(xml_answer.indexOf("<End1>") + 6, xml_answer.indexOf("</End1>"));
            //LED
            Mode = xml_answer.substring(xml_answer.indexOf("<Mode>") + 6, xml_answer.indexOf("</Mode>"));
            Level = xml_answer.substring(xml_answer.indexOf("<Level>") + 7, xml_answer.indexOf("</Level>"));
        }
    }
}
