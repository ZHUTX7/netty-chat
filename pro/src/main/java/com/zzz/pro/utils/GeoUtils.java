package com.zzz.pro.utils;


//坐标计算工具类
public class GeoUtils {
    //坐标计算方法
    public static double[] calculateMidPoint(double lon1, double lat1, double lon2, double lat2) {
        double lon1Rad = Math.toRadians(lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lon2Rad = Math.toRadians(lon2);
        double lat2Rad = Math.toRadians(lat2);

        double latMidRad = (lat1Rad + lat2Rad) / 2;
        double lonMidRad = (lon1Rad + lon2Rad) / 2;

        double latMid = Math.toDegrees(latMidRad);
        double lonMid = Math.toDegrees(lonMidRad);

        return new double[]{lonMid, latMid};
    }

    public static void main(String[] args) {
        double lon1 = 100.0;
        double lat1 = 30.0;
        double lon2 = 120.0;
        double lat2 = 40.0;

        double[] midPoint = calculateMidPoint(lon1, lat1, lon2, lat2);
        System.out.println("Midpoint Lon: " + midPoint[0] + ", Lat: " + midPoint[1]);
    }
}
