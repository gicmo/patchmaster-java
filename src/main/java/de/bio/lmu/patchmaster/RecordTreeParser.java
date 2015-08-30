package de.bio.lmu.patchmaster;


import de.bio.lmu.patchmaster.fields.Field;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordTreeParser {

    static final String rgLevel = "([a-zA-Z]+)Level[ ]+= ([0-9]+);";
    static final String rgFieldMember =  "([a-zA-Z0-9]+)[ ]+=([ 0-9]+);[ ]+\\(\\*([ a-zA-Z0-9\",=\\.\\[\\]]+)\\*\\)";
    static final String rgRecordStart = "\\(\\* (.*) = RECORD \\*\\)";
    static private final Pattern ptFieldMember = Pattern.compile(rgFieldMember);
    static private final Pattern ptRecordStart = Pattern.compile(rgRecordStart);
    static private final Pattern ptLevel = Pattern.compile(rgLevel);

    static private final Pattern[] patterns = {ptFieldMember, ptRecordStart, ptLevel};

    ArrayList<String> levels;
    ArrayList<Field> fields = null;
    ArrayList<RecordType> types;
    RecordType recordType = null;
    String     recordName = null;


    public Object parse(String path, String tree) throws IOException {

        System.out.println("=======" + path + "=======");

        levels = new ArrayList<String>();
        types = new ArrayList<RecordType>();

        FileReader fr = new FileReader(path);
        BufferedReader in = new BufferedReader(fr);

        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            Matcher m = null;
            int i;
            boolean foundMatcher = false;
            for (i = 0; i < patterns.length; i++) {

                m = patterns[i].matcher(line);
                if (m.matches()) {
                    foundMatcher = true;
                    break;
                }
            }

            if (foundMatcher) {
                switch (i) {
                    case 0:
                        String name = m.group(1);
                        String offset = m.group(2);
                        String type = m.group(3);
                        createField(name, type, offset);
                        break;
                    case 1:
                        finishRecord();
                        beginRecord(m.group(1), tree);
                        break;

                    case 2:
                        levels.add(m.group(1));
                        break;
                }

            } else if (line.startsWith("see definition in")) {
                cancelRecord();
            }

        }

        finishRecord();

        for (int i = 0; i < levels.size(); i++) {
            String level = levels.get(i);
            RecordType rt = null;

            for (RecordType curType : types) {
                if (curType.getName().endsWith(level)) {
                    rt = curType;
                    break;
                }
            }

            if (rt == null) {
                System.err.println("ERROR: TREE NOT CORRECT " + level); //FIXME throw exec
                continue;
            }

            RecordFactory.register(rt, tree, i);
        }

        return null;
    }

    public void cancelRecord() {
        fields = null;
        recordName = null;
    }

    public void createField(String name, String type, String offset) {
        Field field = Field.fromStrings(name, type, offset);
        fields.add(field);
    }

    public void beginRecord(String name, String tree) {
        recordName = name.trim();

        if (recordName.equals("RootRecord")) {
            recordName = tree + "Root";
        }

        fields = new ArrayList<Field>();
    }

    public void finishRecord() {

        if (fields == null)
            return;

        String prefix = findCommonFieldPrefix();
        if (prefix != null) {
            stripPrefix(prefix.length());
        }

        recordType = new RecordType(recordName, fields, prefix);
        RecordFactory.register(recordType);
        types.add(recordType);
        fields = null;

    }

    public void stripPrefix(int prefixLen) {

        for (Field field : fields) {
            field.name = field.name.substring(prefixLen);
        }

    }

    public String findCommonFieldPrefix () {

        if (fields == null || fields.size() < 1) {
            return null;
        }

        String prefix = fields.get(0).name;

        for (int i = 1; i < fields.size(); i++) {
            Field field = fields.get(i);
            prefix = findCommonPrefix(prefix, field.name);
        }

        return prefix;
    }

    public static String findCommonPrefix (String a, String b) {

        if (a == null || b == null)
            return null;

        int len = Math.min(a.length(), b.length());
        int i;

        for (i = 0; i < len; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                break;
            }
        }

        if (i == 0) {
            return null;
        }

        return a.substring(0, i);
    }

}
