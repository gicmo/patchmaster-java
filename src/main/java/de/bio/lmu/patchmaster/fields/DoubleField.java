package de.bio.lmu.patchmaster.fields;

import de.bio.lmu.patchmaster.Bits;


public class DoubleField extends NumberField {
    public DoubleField(String name, Class type, int offset) {
        super(name, type, offset);
    }

    public double decodeDouble(byte[] data, boolean littleEndian) {
        return Bits.toDouble(data, offset, littleEndian);
    }
}
