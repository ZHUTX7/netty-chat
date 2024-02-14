package com.mindset.ameeno.utils;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;




public class Img2Base64 {
    public static void main(String[] args) {
        String imgSrcPath = "/Users/zhutianxiang/Desktop/测试头像.jpeg"; 		// 生成64编码的图片的路径
        String imgCreatePath = "/Users/zhutianxiang/Desktop/测试头像2.jpeg"; 	// 将64编码生成图片的路径
        imgCreatePath = imgCreatePath.replaceAll("\\\\", "/");
        System.out.println(imgCreatePath);
        String strImg = getImageStr(imgSrcPath);
        System.out.println(strImg);
        generateImage(strImg, imgCreatePath);
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imgSrcPath
     *            生成64编码的图片的路径
     * @return
     */
    public static String getImageStr(String imgSrcPath) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgSrcPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组Base64编码
        return Base64.encodeBase64String(data);// 返回Base64编码过的字节数组字符串
    }

    public static String getImageInput(InputStream in) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组Base64编码
        return Base64.encodeBase64String(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr
     *            转换为图片的字符串
     * @param imgCreatePath
     *            将64编码生成图片的路径
     * @return
     */
    public static boolean generateImage(String imgStr, String imgCreatePath) {
        if (imgStr == null) // 图像数据为空
            return false;
        try {
            // Base64解码
            byte[] b = Base64.decodeBase64(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgCreatePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}