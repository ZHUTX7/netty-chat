package com.proxy.common.util;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * @author : ztx
 * @version :V1.0
 * @description :
 * @update : 2021/4/16 18:18
 */
@Deprecated
public class MacUtils {
    public static String getLocalMac() throws UnknownHostException, SocketException {
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println(ia);
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        StringBuffer sb = new StringBuffer("");
        for(int i=0; i<mac.length; i++) {
            if(i!=0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i]&0xff;
            String str = Integer.toHexString(temp);
            if(str.length()==1) {
                sb.append("0"+str);
            }else {
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        String hostIP = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        hostIP=ia.getHostAddress();
                        System.out.println(hostIP);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
