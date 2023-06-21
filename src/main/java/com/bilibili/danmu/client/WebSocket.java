package com.bilibili.danmu.client;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bilibili.danmu.pojo.Room;
import org.brotli.dec.BrotliInputStream;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebSocket extends WebSocketClient {
    private int danmuNumber;//弹幕数量
    private JPanel index;//首页
    private JPanel indexPanel;//首页
    private JPanel danmuContentPanel;//弹幕列表
    private JScrollPane scrollPane;//需要将弹幕页内容加入到这个带滚动条的面板
    public WebSocket(Room room,JPanel index,JPanel indexPanel,JPanel danmuContentPanel,JScrollPane danmuContenScrollPane) throws URISyntaxException {
        super(new URI(room.getWebSocketUrl()));//设置连接路径
        this.index = index;
        this.indexPanel = indexPanel;
        this.danmuContentPanel = danmuContentPanel;
        this.scrollPane = danmuContenScrollPane;
        this.danmuNumber = 0;//初始弹幕数量为0
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("websocket connect open(连接窗口打开)");
    }

    @Override
    public void onMessage(ByteBuffer message) {
        byte[] array = message.array();
        String msg = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //开始解析数据包
        int offset = 0;
        while (offset < array.length) {//通过偏移量解析数据
            byte[] packetLenArr = new byte[4];
            System.arraycopy(array, offset, packetLenArr, 0, 4);
            int packetLen = bytesToIntBigEndian(packetLenArr);
            int headLen = array[offset + 5];
            int packetVer = array[offset + 7];
            int packetType = array[offset + 11];
            int newByteArrLen = array.length - headLen;
            if (packetVer == 3) {
                //解压数据，然后转成json
                int offsetV3 = 0;
                byte[] dest = new byte[newByteArrLen];
                System.arraycopy(array, headLen, dest, 0, newByteArrLen);
                try {
                    byte[] decompress = decompress(dest, true);
                    while (offsetV3 < decompress.length) {
                        packetLenArr = new byte[4];
                        System.arraycopy(decompress, offsetV3, packetLenArr, 0, 4);
                        int packetLenV3 = bytesToIntBigEndian(packetLenArr);
                        int headLenV3 = decompress[offsetV3 + 5];
                        byte[] newDest = new byte[packetLenV3 - headLenV3];
                        System.arraycopy(decompress, offsetV3 + headLenV3, newDest, 0, packetLenV3 - headLenV3);
                        msg = new String(newDest);
                       // System.out.println(msg);
                        JSONObject jsonObject = JSONObject.parseObject(msg);
                        if (jsonObject.get("cmd").equals("DANMU_MSG")) {//json里的cmd=DANMU_MSG则是弹幕消息。
                            JSONArray jsonArray = jsonObject.getJSONArray("info");
                            String uName = jsonArray.getJSONArray(2).getString(1);
                            Date date = new Date(jsonArray.getJSONObject(9).getLong("ts") * 1000);
                            String text = jsonArray.getString(1);
                            //System.out.println(simpleDateFormat.format(date) + " " + uName + "：" + text);
                            addDamu(simpleDateFormat.format(date) + " " + uName + "：" + text);
                        }
                        offsetV3 += packetLenV3;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else { //不是弹幕的消息
                    byte[] newDest = new byte[packetLen - headLen];
                    System.arraycopy(array, offset + headLen, newDest, 0, packetLen - headLen);
                    msg = new String(newDest);
                   // System.out.println(msg);//不做打印
            }
            offset += packetLen;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("本次连接关闭，websocket connect close(连接已经断开)，信息码:" + code);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("出错了，websocket connect error,message:" + ex.getMessage());
    }

    @Override
    public void onMessage(String message) {
        // TODO 自动生成的方法存根
        System.out.println("message:" + message);
    }

    private byte[] decompress(byte[] data, boolean byByte) throws IOException {
        byte[] buffer = new byte[65536];
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BrotliInputStream brotliInput = new BrotliInputStream(input);
        if (byByte) {
            byte[] oneByte = new byte[1];
            while (true) {
                int next = brotliInput.read();
                if (next == -1) {
                    break;
                }
                oneByte[0] = (byte) next;
                output.write(oneByte, 0, 1);
            }
        } else {
            while (true) {
                int len = brotliInput.read(buffer, 0, buffer.length);
                if (len <= 0) {
                    break;
                }
                output.write(buffer, 0, len);
            }
        }
        brotliInput.close();
        return output.toByteArray();
    }

    public int bytesToIntLittleEndian(byte[] bytes) {
        // byte数组中序号小的在右边
        return bytes[0] & 0xFF | //
                (bytes[1] & 0xFF) << 8 | //
                (bytes[2] & 0xFF) << 16 | //
                (bytes[3] & 0xFF) << 24; //
    }

    public int bytesToIntBigEndian(byte[] bytes) {
        // byte数组中序号大的在右边
        return bytes[3] & 0xFF | //
                (bytes[2] & 0xFF) << 8 | //
                (bytes[1] & 0xFF) << 16 | //
                (bytes[0] & 0xFF) << 24; //
    }
    public void addDamu(String message){
        JLabel danmuLabel = new JLabel(message);
        this.danmuNumber++;//多一条消息，则要多一行
        this.danmuContentPanel.setLayout(new GridLayout(danmuNumber,1));//重新设置布局
        this.danmuContentPanel.add(danmuLabel);
        this.scrollPane.updateUI();//刷新页面，刷新后，添加的布局才会成功
        this.scrollPane.repaint();
        this.scrollPane.updateUI();//刷新页面，刷新后，添加的布局才会成功
        this.scrollPane.repaint();
        this.indexPanel.updateUI();
        this.indexPanel.repaint();//刷新页面
        this.index.updateUI();
        this.index.repaint();//刷新页面
    }
}