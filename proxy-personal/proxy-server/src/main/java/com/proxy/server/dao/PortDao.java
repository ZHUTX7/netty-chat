package com.proxy.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author : ztx
 * @version :V1.0
 * @description : port dao
 * @update : 2021/4/9 10:52
 */
public  class PortDao {


    private  static List<Integer>  PORT_ARRAY = null;

    public synchronized  static List<Integer> getPortArray(){
        if(PORT_ARRAY ==null){
            PORT_ARRAY = new ArrayList<>();
            return PORT_ARRAY;
        }
        else
            return PORT_ARRAY;
    }
}
