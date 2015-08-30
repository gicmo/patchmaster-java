package de.bio.lmu.patchmaster;


import javax.swing.tree.TreeNode;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

public class Bundle {

    private File file;
    private Item[] items;
    private HashMap<String, Item> extMap = new HashMap<String, Item>();
    private boolean le = true;
    private static final int rootMagic = 0x54726565;

    static {

        RecordTreeParser parser = new RecordTreeParser();
        try {
            Class<?> clazz = Bundle.class;
            URL r = clazz.getResource("/AmplTreeFile_v9.txt");

            parser.parse(r.getPath(), "Amplifier");
            parser = new RecordTreeParser();

            r = clazz.getResource("/PulsedFile_v9.txt");
            parser.parse(r.getPath(), "Pulsed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Bundle(String path) {
        this.file = new File(path);
    }


    public void readHeader() throws IOException {
        RandomAccessFile fd = new RandomAccessFile(file, "r");
        byte[] header = new byte[256];
        fd.readFully(header);

        DataReader dr = new DataReader(header, header[52] != 0);
        String signature = dr.readString(8);
        String version = dr.readString(32);
        double time = dr.readDouble();
        int nitems = dr.readInt();
        le = dr.readBool();
        dr.skip(11);

        items = new Item[nitems];

        for (int i = 0; i < nitems; i++) {
            int start = dr.readInt();
            int length = dr.readInt();
            String ext = dr.readString(8).trim();

            items[i] = new Item(start, length, ext);
            extMap.put(ext, items[i]);
            System.out.println("\t" + items[i]);
        }

        System.err.println(signature);
        System.err.println(version);
        System.err.println(time);
        System.err.println(nitems);
        System.err.println(le);
        fd.close();
    }


    public class Item {
        public int start;
        public int length;
        public String ext;

        public Item(int start, int length, String ext) {
            this.start = start;
            this.length = length;
            this.ext = ext;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "start=" + start +
                    ", length=" + length +
                    ", ext='" + ext + '\'' +
                    '}';
        }
    }

    private Record loadRecord(RandomAccessFile fd, int[] levelSizes, int level, String treeType) throws IOException {
        byte[] intData = new byte[4];
        int dataSize = levelSizes[level];
        byte[] data = new byte[dataSize];

        fd.readFully(data);
        fd.readFully(intData);
        int nchildren = Bits.toInt(intData, le);

        RecordType type = RecordFactory.lookup(treeType, level);
        Record record = new Record(type, data);

        Record[] children = new Record[nchildren];
        for (int i = 0; i < nchildren; i++) {
            children[i] = loadRecord(fd, levelSizes, level+1, treeType);
        }

        record.setChildren(children);
        return record;
    }

    public Record loadTree(String ext) throws IOException {
        Item item = extMap.get(ext);

        if (item == null) {
            System.err.println("EE bundled item not found: " + ext);
            return null;
        }

        RandomAccessFile fd = new RandomAccessFile(file, "r");
        byte[] intData = new byte[4];

        fd.seek(item.start);
        fd.readFully(intData);
        int magic = Bits.toInt(intData, le);

        if (magic != rootMagic) {
            System.err.println("EE incorrect magic");
            return null;
        }

        fd.readFully(intData);
        int levels = Bits.toInt(intData, le);

        intData = new byte[4*levels];
        int[] levelSizes = new int[levels];
        fd.readFully(intData);

        for (int i = 0; i < levels; i++) {
            levelSizes[i] = Bits.toInt(intData, 4*i, le);
        }

        for (int j : levelSizes) {
            System.out.println(j);
        }

        return loadRecord(fd, levelSizes, 0, "Pulsed");
    }

    public static void dumpRecordTree(Record record, int level) {
        System.out.printf("%" + "" + ((level +1)*3-2) + "s", "");
        System.out.print(record);
        System.out.printf(" [%d]\n", level);

        for (Record child : record.getChildren()) {
            dumpRecordTree(child, level + 1);
        }
    }

    public static void dumpRecordTree(Record record) {
        dumpRecordTree(record, 0);
    }

    public static void main(String[] args) {

        Bundle b = new Bundle("/Users/gicmo/Coding/G-Node/patchmaster/2012_12_03c.dat");

        try {
            b.readHeader();

            Record r = b.loadTree(".pul");
            dumpRecordTree(r);

        } catch (IOException e) {

        }




    }

}
