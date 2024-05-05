package com.example.bletest3;

public class Utils {
    //Conversion from BLE Byte Array to Integer
    public static int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; ++i) {
            value += (bytes[i] & 0xFF) << (8 * i);
        }
        return value;
    }

    // Conversion from Integer to BLE Byte Array
    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (value >> (i * 8));
        }
        return result;
    }

    // Formula that calculates the estimated safe sun exposure time. It is presented in
    // Hours : Minutes : Seconds
    public static double formula(int skinType, int spf, double uv, double altitude) {
        double skinFactor;
        double altitudeFactor;
        double formula_time_raw;
        // Applies a skinFactor depending on the selected skinType
        switch (skinType) {
            case 1:
                skinFactor = 0.3;
                break;
            case 2:
                skinFactor = 0.4;
                break;
            case 3:
                skinFactor = 0.5;
                break;
            case 4:
                skinFactor = 0.6;
                break;
            case 5:
                skinFactor = 0.7;
                break;
            default:
                skinFactor = 0.8;
                break;
        }
        // Applies an altitude factor. Roughly 1.10%
        altitudeFactor = 1 + ((altitude / 1000) * 0.1);

        formula_time_raw = (((double) spf * skinFactor) / (uv * altitudeFactor)) * 60;
        return formula_time_raw;
        //if the formula is less than 2 and spf spf > 15 then auto to make formula_time_raw 2 hours
        /*
        if ((formula_time_raw < 120) && (spfD >= 15) ) {
            formula_time_raw = 120;
            return formula_time_raw*60;
        } else if (formula_time_raw >= 360) {
            formula_time_raw= 360;
            return formula_time_raw*60;
        } else {
            return (spfD * skinFactor) / (uv * altitudeFactor) * 60;
        }

         */
    }
}
