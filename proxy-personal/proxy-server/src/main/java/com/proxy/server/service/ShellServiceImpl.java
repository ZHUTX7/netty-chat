package com.proxy.server.service;

import io.protostuff.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.annotation.Annotation;

/**
 * @author ztx
 * @date 2021-07-07 17:51
 * @description :
 */
public class ShellServiceImpl implements ShellService {

    @Override
    public boolean execute(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}