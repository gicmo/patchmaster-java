package de.bio.lmu.patchmaster;

import de.bio.lmu.patchmaster.fields.Field;

import java.util.ArrayList;

public class RecordType {

    ArrayList<Field> fields = null;
    String  prefix = null;
    String name = null;

    public RecordType(String name, ArrayList<Field> fields, String prefix) {
        if (name.endsWith("Record")) {
            name = name.substring(0, name.length()-6);
        }

        this.name   = name;
        this.fields = fields;
        this.prefix = prefix;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public Field lookupField(String name) {

        Field field = null;
        for (Field f : fields) {
            if (f.name.equals(name)) {
                field = f;
                break;
            }
        }

        return field;
    }

}
