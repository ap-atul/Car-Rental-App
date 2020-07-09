package com.example.hppc.myapplication.Utils;

public class IPUtils {
    private static String ipaddress="192.168.43.248";
    private static String port="8000";
    private static String completeip;

    public static String getIpaddress() {
        return ipaddress;
    }

    public static void setIpaddress(String ipaddress) {
        IPUtils.ipaddress = ipaddress;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        IPUtils.port = port;
    }

    public static String getCompleteip() {
        completeip="http://"+ipaddress+":"+port;
        return completeip;
    }

    public static void setCompleteip(String completeip) {
        IPUtils.completeip = completeip;
        int l=completeip.length();
        int t= completeip.lastIndexOf('/');
        port=completeip.substring(l-4,l);
        ipaddress=completeip.substring(t+1,l-5);
    }
}
