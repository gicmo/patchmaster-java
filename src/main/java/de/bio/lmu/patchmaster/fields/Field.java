package de.bio.lmu.patchmaster.fields;

import de.bio.lmu.patchmaster.RecordFactory;
import de.bio.lmu.patchmaster.RecordType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Field {
    private static final String rgRecordField = "([a-zA-Z]+)Size[ ]+\\= [0-9]+";
    private static final Pattern ptRecordField = Pattern.compile(rgRecordField);

    private static final String rgStringField = "String([0-9]+)(?:Type|Size)";
    private static final Pattern ptStringField = Pattern.compile(rgStringField);

    public String name;
    public Class  type;
    public int    offset;


    @Override
    public String toString() {
        return "Field {" +
                "name='" + name + '\'' +
                ", type=" + (type != null ? type : "unknown") +
                ", @ [" + offset +
                "]}";
    }

    public Field(String name, Class type, int offset) {
        this.name = name;
        this.type = type;
        this.offset = offset;
    }

    public static Field fromStrings(String name, String typeStr, String offsetStr) {

        int offset;
        try {
            offset = Integer.parseInt(offsetStr.trim()); // FIXME
        } catch (NumberFormatException e) {
            return null;
        }

        String ts = typeStr.trim();
        Field field = null;

        if (ts.equalsIgnoreCase("BOOLEAN")) {
            field = new NumberField(name, Boolean.TYPE, offset);
        } else if (ts.equalsIgnoreCase("BYTE")) {
            field = new NumberField(name, Boolean.TYPE, offset);
        } else if (ts.equalsIgnoreCase("CHAR")) {
            field = new NumberField(name, Character.TYPE, offset);
        } else if (ts.equalsIgnoreCase("INT16")) {
            field = new NumberField(name, Short.TYPE, offset);
        } else if (ts.equalsIgnoreCase("INT32")) {
            field = new NumberField(name, Integer.TYPE, offset);
        } else if (ts.equalsIgnoreCase("LONGREAL")) {
            field = new DoubleField(name, Double.TYPE, offset);
        } else if (ts.equalsIgnoreCase("CARD32")) {
            field = new CRC32Field(name, String.class, offset);
        } else {

            Matcher mRecord = ptRecordField.matcher(ts);
            Matcher mString = ptStringField.matcher(ts);
            RecordType recordType;

            if (mRecord.matches()) {
                System.err.println(mRecord.group(1));
                if((recordType = RecordFactory.lookup(mRecord.group(1))) != null) {
                    field = new RecordField(name, recordType, offset);
                }
            } else if (mString.matches()) {
                String numstr = mString.group(1);
                int strlen = Integer.parseInt(numstr);
                field = new StringField(name, offset, strlen);
            }
        }

        if (field == null) {
            System.err.println("Type not resolved: " + ts);
            field = new Field(name, null, offset);
        }

        return field;
    }
}
