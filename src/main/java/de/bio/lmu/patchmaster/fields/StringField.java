package de.bio.lmu.patchmaster.fields;


import de.bio.lmu.patchmaster.Bits;

public class StringField extends Field {
    public int len;

    public StringField(String name, int offset, int len) {
        super(name, String.class, offset);
        this.len = len;
    }


    public String decodeString(byte[] data) {
        return Bits.toString(data, offset, len);
    }

}
