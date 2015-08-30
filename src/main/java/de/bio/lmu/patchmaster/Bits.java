package de.bio.lmu.patchmaster;


import java.util.Arrays;

public class Bits {

    //Caution: Sign extension!


    public static short toShort(byte[] data, boolean littleEndian) {
        short res;
        if (littleEndian) {
            res = (short) ((0xff & data[0]) | (0xff & data[1]) << 8);
        } else {
            res = (short) ((0xff & data[1]) | (0xff & data[0]) << 8);
        }

        return res;
    }

    public static int toInt(byte[] data, boolean littleEndian) {
        return toInt(data, 0, littleEndian);
    }

    public static int toInt(byte[] data, int offset, boolean littleEndian) {

        int res;
        if (littleEndian) {
            res = (0xff & data[offset])           | (0xff & data[offset + 1]) << 8 |
                  (0xff & data[offset + 2]) << 16 | (0xff & data[offset + 3]) << 24;
        } else {
            res = (0xff & data[offset + 3])       | (0xff & data[offset + 2]) << 8 |
                  (0xff & data[offset + 1]) << 16 | (0xff & data[0]) << 24;
        }

        return res;
    }

    public static long toLong(byte[] data, boolean littleEndian) {
        return toLong(data, 0, littleEndian);
    }

    public static long toLong(byte[] data, int offset, boolean littleEndian) {
        long res;
        if (littleEndian) {
            res = (long) (0xff & data[offset])           | (long) (0xff & data[offset + 1]) << 8  |
                  (long) (0xff & data[offset + 2]) << 16 | (long) (0xff & data[offset + 3]) << 24 |
                  (long) (0xff & data[offset + 4]) << 32 | (long) (0xff & data[offset + 5]) << 40 |
                  (long) (0xff & data[offset + 6]) << 48 | (long) (0xff & data[offset + 7]) << 56;
        } else {
            res = (long) (0xff & data[offset + 7])       | (long) (0xff & data[offset + 6]) << 8  |
                  (long) (0xff & data[offset + 5]) << 16 | (long) (0xff & data[offset + 4]) << 24 |
                  (long) (0xff & data[offset + 3]) << 32 | (long) (0xff & data[offset + 2]) << 40 |
                  (long) (0xff & data[offset + 1]) << 48 | (long) (0xff & data[offset    ]) << 56;
        }

        return res;
    }

    public static double toDouble(byte[] data, boolean littleEndian) {
        long bits = toLong(data, littleEndian);
        return Double.longBitsToDouble(bits);
    }

    public static double toDouble(byte[] data, int offset, boolean littleEndian) {
        long bits = toLong(data, offset, littleEndian);
        return Double.longBitsToDouble(bits);
    }

    public static String toString(byte[] data, int offset, int len) {
        byte[] bytes = Arrays.copyOfRange(data, offset, offset + len);
        return new String(bytes);
    }

}
