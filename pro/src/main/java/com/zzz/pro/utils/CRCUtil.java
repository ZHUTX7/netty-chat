package com.zzz.pro.utils;


import java.util.zip.CRC32;

public class CRCUtil {
    public static String crc32Hex(String in) {
        CRC32 crc = new CRC32();
        crc.update(in.getBytes());
        long val = crc.getValue();
        return String.format("%08x", val);
    }
    public static String crc32HexBy2Id(String aid,String bid){
        CRC32 crc = new CRC32();
        String mergeId ;
        if(aid.hashCode()>bid.hashCode()){
            mergeId = aid+bid;
        }else {
            mergeId = bid+aid;
        }
        crc.update(mergeId.getBytes());
        long val = crc.getValue();
        return String.format("%08x", val);
    }

    public static long crc32(String in) {
        CRC32 crc = new CRC32();
        crc.update(in.getBytes());
        return crc.getValue();
    }

    public static void main(String[] args) {
        System.out.println(crc32HexBy2Id("10001","10002"));
        System.out.println(crc32HexBy2Id("10002","10001"));
        String s = "abcd";

        String s1 = new String(s);

        if (s == s1) System.out.println("the same");

        if (s.equals(s1)) System.out.println("equals");
    }
}
