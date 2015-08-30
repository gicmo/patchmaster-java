package de.bio.lmu.patchmaster.fields;


import de.bio.lmu.patchmaster.Record;
import de.bio.lmu.patchmaster.RecordType;

import java.util.Arrays;

public class RecordField extends Field {
    RecordType recordType;

    public RecordField(String name, RecordType type, int offset) {
        super(name, Record.class, offset);
        this.recordType = type;
    }

    public Record decodeRecord(byte[] data) {
        byte[] dCopy = Arrays.copyOfRange(data, offset, data.length - offset); //FIXME
        return new Record(recordType, dCopy);
    }

}
