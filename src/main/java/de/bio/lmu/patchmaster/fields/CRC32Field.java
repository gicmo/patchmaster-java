package de.bio.lmu.patchmaster.fields;

public class CRC32Field extends Field {
    public CRC32Field(String name, Class type, int offset) {
        super(name, java.util.zip.CRC32.class, offset);
    }

}
