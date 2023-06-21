package com.bilibili.danmu;

import com.bilibili.danmu.frame.MyFrame;
import org.springframework.util.ResourceUtils;

import javax.swing.*;
import java.net.URL;


public class Main {
    private static void createGUI() {
        MyFrame myFrame=new MyFrame("Live Streaming Assistant");
        //myFrame.setSize(600,500);
        myFrame.setSize(1200,600);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = null;
        URL imgURL = null;
        try {
            imgURL = Main.class.getResource("/img/icon.png");//得到图片的路径资源
        }catch (Exception e){
            System.out.println("没有读取到标签");
        }
        if (imgURL != null) {//不为null，读取到了图片
            icon = new ImageIcon(imgURL);
            myFrame.setIconImage(icon.getImage());//读取到图片则修改图标，否则不修改
        }else{
            System.out.println("没有读取到标签");
        }
        myFrame.setVisible(true);//显示窗口
    }
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                createGUI();
            }
        });
    }
}
