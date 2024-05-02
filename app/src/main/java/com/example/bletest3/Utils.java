package com.example.bletest3;

public class Utils {
    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (value >> (i * 8));
        }
        return result;
    }

    public static int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; ++i) {
            value += (bytes[i] & 0xFF) << (8 * i);
        }
        return value;
    }
}
