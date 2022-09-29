package com.proxy.server.util;


/**
 * @author ztx
 * @date 2021-11-22 16:57
 * @description :
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamCaptureThread implements Runnable
{
    InputStream m_in;
    StringBuffer m_sb;

    public StreamCaptureThread(InputStream inputStream)
    {
        this.m_in = inputStream;
    }

    public void setOutput(StringBuffer sb)
    {
        m_sb = sb;
    }

    public void run()
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(m_in));
            String line = null;
            while (m_in!= null&&(line = br.readLine()) != null) {
                if (line.trim().length()> 0) {
                    if (m_sb != null) {
                        m_sb.append(line);
                        m_sb.append("\n");
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}

