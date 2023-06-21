package com.bilibili.danmu.pojo;

import org.apache.http.util.ByteArrayBuffer;

public class AccessPackage {
    private int totalLength;//总长度
    private final short headLength = 16;//头部长度
    private final short protocol = 1;//协议版本，认证包和心跳包设置为1，表示不压缩
    private final int operate = 7;//认证包为7
    private final int sequence = 1;//认证包特征码为1
    private byte[] bodyData;//正文数据
    private ByteArrayBuffer data = new ByteArrayBuffer(totalLength);//总数据

    public AccessPackage(int totalLength, byte[] bodyData) {
        //需要传入总长度，以及正文数据
        this.totalLength = totalLength;
        this.bodyData = bodyData;
        data.append(new byte[]{0,0,0,(byte)this.totalLength},0,4);//设置总长度
        data.append(new byte[]{0,(byte)headLength},0,2);//设置头部长度
        data.append(new byte[]{0,(byte)protocol},0,2);//设置协议
        data.append(new byte[]{0,0,0,(byte)operate},0,4);//设置操作码
        data.append(new byte[]{0,0,0,(byte)sequence},0,4);//设置特征码
        for(byte aByte:this.bodyData){
            data.append(new byte[]{aByte},0,1);//正文数据必须一个一个加进去，不能直接加
        }
    }
    public ByteArrayBuffer getData(){
        return this.data;
    }
}
