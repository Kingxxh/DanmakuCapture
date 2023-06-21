package com.bilibili.danmu.thread;

import com.bilibili.danmu.client.WebSocket;
import com.bilibili.danmu.pojo.HeartPackage;

public class HeartThread extends Thread {
    private WebSocket webSocket;
    private boolean flag = true;
    public HeartThread(WebSocket webSocket){
        this.webSocket = webSocket;
    }
    public void run(){
        HeartPackage heartPackage = new HeartPackage();//生成心跳包数据
        while(flag){
            try{
                sleep(29500);//参数单位是毫秒，每发送一次休眠30秒，但是为了防止延迟，所以睡眠时间稍微比30秒短
                if(this.webSocket.isClosed()){
                    System.out.println("本次连接的心跳进程即将关闭");
                    this.flag = false;//如果连接已经关闭，则退出循环，退出循环，线程就会自己关闭
                }else{//没有关闭则发送心跳包
                    this.webSocket.send(heartPackage.getData().toByteArray());//发送心跳数据
                    System.err.println("心跳包发送成功");
                    heartPackage.setSequence();//发送完毕后让特征码sequence加一
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
