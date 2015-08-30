package de.bio.lmu.patchmaster;



import java.util.AbstractMap;
import java.util.HashMap;

public class RecordFactory {

    static HashMap<String, RecordType> typeMap = new HashMap<String, RecordType>();
    static HashMap<TreeEntry, RecordType> treeMap = new HashMap<TreeEntry, RecordType>();


    private static class TreeEntry extends AbstractMap.SimpleEntry<String, Integer> {
        private TreeEntry(String key, Integer value) {
            super(key, value);
        }

        private TreeEntry(String key, int value) {
            super(key, value);
        }

    }

    public static synchronized void register (RecordType type) {
        typeMap.put(type.getName(), type);
        System.out.println("II new type " + type.getName());
    }

    public static synchronized RecordType lookup(String name) {
        if (name.endsWith("Record")) {
            name = name.substring(name.length()-6);
        }
        return typeMap.get(name);
    }

    public static synchronized void register (RecordType type, String tree, int level) {
        TreeEntry key = new TreeEntry(tree, level);
        treeMap.put(key, type);
    }

    public static synchronized RecordType lookup(String tree, int level) {
        TreeEntry key = new TreeEntry(tree, level);
        return treeMap.get(key);
    }
}
