package com.bilibili.danmu.util;

import java.util.Arrays;

public class ByteUtil {
    public static byte[] int2BytesA(int n, int len) throws IllegalArgumentException
    {//用于将整型n转换成对应长度的byte数组
        if (len <= 0) {
            throw new IllegalArgumentException("Illegal of length");
        }
        byte[] b = new byte[len];
        for (int i = len; i > 0; i--) {
            b[(i - 1)] = ((byte)(n >> 8 * (len - i) & 0xFF));
        }
        return b;
    }
    /**
     * 方法功能：int整型转换成字节数组byte[]，注意该算法是从低位(右端)往高位(左端)取二进制。
     *
     * */
    public static void int2BytesB(){
        int i = 1000;
        byte[] arr = new byte[4] ;
//			arr[0] = (byte)i ;         //通过debug可以看到arr[0] = -24,也就是11101000‬
//			arr[1] = (byte)(i >> 8) ;  //通过debug可以看到arr[1] = 3,也就是00000011
//			arr[2] = (byte)(i >> 16) ; //通过debug可以看到arr[2] = 0, 也就是00000000
//			arr[3] = (byte)(i >> 24) ; //通过debug可以看到arr[3] = 0,  也就是00000000

        for (int j = 0; j < 4; j++) {
            arr[j] = ((byte)(i >> 8 * (j) & 0xFF));
        }
        System.out.println("从低位(右端)取字节数组为："+ Arrays.toString(arr));//输出结果：从低位(右端)取字节数组为：[-24, 3, 0, 0]
    }
}
