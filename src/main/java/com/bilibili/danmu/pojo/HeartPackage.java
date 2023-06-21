package com.bilibili.danmu.pojo;

import org.apache.http.util.ByteArrayBuffer;

public class HeartPackage  {
    private final int totalLength = 16;//总长度，心跳包没有正文，所以长度也是16
    private final short headLength = 16;//头部长度
    private final short protocol = 1;//协议版本，认证包和心跳包设置为1，表示不压缩
    private int operate = 2;//操作码
    private int sequence = 1;//特征码，初始为1，每次发送要加1
    private ByteArrayBuffer data;
    public HeartPackage(){
        this.data = new ByteArrayBuffer(totalLength);//总数据
        data.append(new byte[]{0,0,0,(byte)this.totalLength},0,4);//设置总长度
        data.append(new byte[]{0,(byte)headLength},0,2);//设置头部长度
        data.append(new byte[]{0,(byte)protocol},0,2);//设置协议
        data.append(new byte[]{0,0,0,(byte)operate},0,4);//设置操作码
        data.append(new byte[]{0,0,0,(byte)sequence},0,4);//设置特征码，设置完毕即可
    }
    public void setSequence(){
        this.sequence++;//每次发送心跳包，sequence都加一
        this.data = new ByteArrayBuffer(totalLength);//重新生成总数据
        data.append(new byte[]{0,0,0,(byte)this.totalLength},0,4);//设置总长度
        data.append(new byte[]{0,(byte)headLength},0,2);//设置头部长度
        data.append(new byte[]{0,(byte)protocol},0,2);//设置协议
        data.append(new byte[]{0,0,0,(byte)operate},0,4);//设置操作码
        data.append(new byte[]{0,0,0,(byte)sequence},0,4);//设置特征码，设置完毕即可
    }

    public ByteArrayBuffer getData() {
         return data;
    }
}
