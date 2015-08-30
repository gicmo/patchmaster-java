package de.bio.lmu.patchmaster;


import java.util.Arrays;

public class DataReader {
    byte[] data;
    boolean lE;
    int offset = 0;

    public DataReader(byte[] data, boolean littleEndian) {
        this.data = data;
        this.lE = littleEndian;
    }

    public void skip(int howMany) {
        offset += howMany;
    }

    public boolean readBool(int pos) {
        return data[pos] != 0;
    }

    public boolean readBool() {
        boolean res = readBool(offset);
        offset += 1;
        return res;
    }

    public int readInt(int pos) {
        byte[] bytes = Arrays.copyOfRange(data, pos, pos + 4);
        return Bits.toInt(bytes, lE);
    }

    public int readInt() {
        int res = readInt(offset);
        offset += 4;
        return res;
    }

    public double readDouble(int pos) {
        byte[] bytes = Arrays.copyOfRange(data, pos, pos + 8);
        return Bits.toDouble(bytes, lE);
    }

    public double readDouble() {
        double res = readDouble(offset);
        offset += 8;
        return res;
    }

    public String readString(int start, int len) {
        byte[] bytes = Arrays.copyOfRange(data, start, start + len);
        return new String(bytes);
    }

    public String readString(int len) {
        String res = readString(offset, len);
        offset += len;
        return res;
    }



}
