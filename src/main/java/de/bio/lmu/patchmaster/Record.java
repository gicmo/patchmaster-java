package de.bio.lmu.patchmaster;

import de.bio.lmu.patchmaster.fields.DoubleField;
import de.bio.lmu.patchmaster.fields.Field;
import de.bio.lmu.patchmaster.fields.RecordField;
import de.bio.lmu.patchmaster.fields.StringField;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class Record implements TreeNode {
    private RecordType type;
    private byte[]     data;
    private Record[]   children;
    private int        nRecordFields;
    ArrayList<RecordField> recordFields = new ArrayList<RecordField>();

    public Record(RecordType type, byte[] data) {
        this.type = type;
        this.data = data;

        for (Field f : type.fields) {
            if (f instanceof RecordField) {
                RecordField rf = (RecordField) f;
                recordFields.add(rf);
            }
        }
        nRecordFields = recordFields.size();
    }

    public Record[] getChildren() {
        return children;
    }

    public void setChildren(Record[] children) {
        this.children = children;
    }

    public final RecordType getType() {
        return type;
    }

    @Override
    public String toString() {
        return (type != null ? type.getName() : "unnamed") + " - [record]";
    }

    public String decodeString(String name) {
        Field field = type.lookupField(name);

        if (field == null) {
            return null; //FIXME: throw exception?
        }

        if (!(field instanceof StringField)) {
            return null; //FIXME: throw?
        }

        StringField stringField = (StringField) field;
        return stringField.decodeString(data);
    }

    public Record decodeRecord(String name) {
        Field field = type.lookupField(name);

        if (field == null) {
            return null; //FIXME: throw exception?
        }
        if (!(field instanceof RecordField)) {
            return null; //FIXME: throw?
        }

        RecordField recordField = (RecordField) field;
        return recordField.decodeRecord(data);
    }

    public double decodeDouble(String name) {
        Field field = type.lookupField(name);

        if (field == null) {
            return 0.0; //FIXME: throw exception?
        }
        if (!(field instanceof DoubleField)) {
            return 0.0; //FIXME: throw?
        }

        DoubleField recordField = (DoubleField) field;
        return recordField.decodeDouble(data, true); //FIXME

    }

    //*** TreeNode interface
    public TreeNode getChildAt(int childIndex) {
        if (childIndex < children.length) {
            return children[childIndex];
        }  else {
           RecordField rf = recordFields.get(childIndex - children.length);
           return decodeRecord(rf.name);
        }
    }

    public int getChildCount() {
        return (children != null ? children.length : 0) + nRecordFields;
    }

    public TreeNode getParent() {
        return null;
    }

    public int getIndex(TreeNode node) {
        return 0;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    public Enumeration children() {
        return null;
    }
}
